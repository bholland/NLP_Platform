package collection_readers;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Stream;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.uima.cas.CAS;
import org.apache.uima.cas.CASException;
import org.apache.uima.collection.CollectionException;
import org.apache.uima.collection.CollectionReader_ImplBase;
import org.apache.uima.jcas.JCas;
import org.apache.uima.resource.ResourceInitializationException;
import org.apache.uima.util.Progress;
import org.apache.uima.util.ProgressImpl;

import database.DatabaseConnector;
import descriptors.DatabaseCollectionReader_ImplBase;
import helper.CleanText;
import helper.DatabaseHelper;
import job_queue.Job;
import job_queue.JobParameter;
import job_queue.JobStatus;
import job_queue.JobType;
import objects.CsvObject;
import objects.PdfObject;
import objects.UnprocessedText;
import reader.CsvReader;
import reader.HtmlReader;
import reader.PdfReader;
import reader.Reader_ImplBase;
import reader.TextObject;
import reader.TextReader;

public class FolderReader extends DatabaseCollectionReader_ImplBase {
    
    /**
     * Base folder location
     */
    private static final String PARAM_BASE_FOLDER = "BaseFolder";
    
    /**
     * Boolean to walk all subpaths 
     */
    private static final String PARAM_RECURSIVE_FOLDER = "IsRecursive";
    
    /**
     * Boollean to Read PDFs 
     */
    private static final String PARAM_READ_TEXT = "ReadText";
    
    /**
     * Boollean to Read PDFs 
     */
    private static final String PARAM_READ_PDF = "ReadPdf";
    
    /**
     * Boolean to read CSV
     */
    private static final String PARAM_READ_CSV = "ReadCsv";
    
    /**
     * Boolean to read HTML
     */
    private static final String PARAM_READ_HTML = "ReadHtml";
    
    /**
     * Boolean to read word documents
     */
    private static final String PARAM_READ_WORD = "ReadWord";
    
    /**
     * List of possible values for the csv ID column header
     */
    private static final String PARAM_CSV_ID_HEADERS = "CsvIdHeaders";
    
    /**
     *  List of possible values for the csv text column header
     */
    private static final String PARAM_CSV_TEXT_HEADERS = "CsvTextHeaders";
    
    /**
     * List of csv category values
     */
    private static final String PARAM_CSV_CATEGORY_HEADERS = "CsvCategoryHeaders";
    
    /**
     * Database server location (probably localhost. 
     */
    public static final String PARAM_DATABASE_SERVER = "DatabaseServer";
    
    /**
     * The database to connect to
     */
    public static final String PARAM_DATABASE = "Database";
    
    /**
     * Database user name
     */
    public static final String PARAM_DATABASE_USER_NAME = "DatabaseUserName";
    
    /**
     * Database password
     */
    public static final String PARAM_DATABASE_PASSWORD = "DatabasePassword";

    /**
     * The database server port
     */
    public static final String PARAM_DATABASE_SERVER_PORT = "DatabasePort";
    
    /**
     * The database server type
     */
    public static final String PARAM_DATABASE_TYPE = "DatabaseType";
    
    /**
     * DataType
     * DataType == 0: source only
     * DataType == 1: category only
     * DataType == 2: both source and category.
     */
    private static final String PARAM_DATA_TYPE = "DataType";
    
    private static final String PARAM_CLEAN_DATA = "CleanData";
    
    private String mBaseFolder = null;
    private Boolean mIsRecursive = null;
    private Boolean mReadText = null;
    private Boolean mReadPdf = null;
    private Boolean mReadCsv = null;
    private Boolean mReadHtml = null;
    private Boolean mReadWord = null;
    private Integer mDataType = null;
    private Boolean mCleanData = null;
    
    private String[] mCsvIdHeaders = null;
    private String[] mCsvTextHeaders = null;
    private String[] mCsvCategoryHeaders = null;
    
    //private int mFolderListIndex;
    //private ArrayList<File> mFolderList = null;
    
    private int mTextListIndex;
    private ArrayList<TextObject> mTextList = null;
    
    private ArrayList<String> mFilesToProcess = null;
    private ArrayList<String> mProcessedFiles = null;
    
    private Integer mJobNotStartedId = null;
    private Integer mJobFinishedId = null;
    private Integer mJobTypeId = null;
    private ArrayList<Integer> mJobQueueIds = null;
    /*
     * text/html
     * 
     */
    
    /**
     * Populate the file list with all files that the application should iterate over. 
     * @param baseFolder
     * @throws IOException
     */
    private ArrayList<File> PopulateFiles(File baseFolder) throws IOException {
        ArrayList<File> ret = new ArrayList<File>();
        ArrayList<Path> pathlist = new ArrayList<Path>();
        
        try (Stream<Path> paths = Files.walk(baseFolder.toPath())) {
            paths.distinct().forEach(pathlist::add);
        }
        
        for (Path path : pathlist) {
            
            String type = Files.probeContentType(path);
            if (type == null) {
            	continue;
            }
            if (type.equals("text/csv") && mReadCsv == true) {
                ret.add(path.toFile());
            } else if (type.equals("application/pdf") && mReadPdf == true) {
                ret.add(path.toFile());
            } else if (type.equals("text/html") && mReadHtml == true) {
                ret.add(path.toFile());
            } else if (type.equals("text/plain") && mReadText == true) {
                ret.add(path.toFile());
            }
            
            //System.out.println(String.format("File: %s type: %s included: %s", path.toFile().getAbsolutePath(), type, included));
        }
        return ret;
    }
    
    public void ReadFiles(Connection connection, ArrayList<File> folderList) throws IOException, CollectionException, SQLException {        
        boolean isModelData = false;
        boolean isSourceData = false;
        if (mDataType == 0) {
            isSourceData = true;
        } else if (mDataType == 1) {
            isModelData = true;
        } else if (mDataType == 2) {
            isSourceData = true;
            isModelData = true;
        }
        
        for (File file : folderList) {
            String type = Files.probeContentType(file.toPath());
            if (type == null) {
            	continue;
            }
            //If this file path is already processed, ignore it.
            if (mProcessedFiles.contains(file.getAbsolutePath())) {
            	continue;
            }
            //Otherwise, delete it all because it has data but it wasn't finished yet. 
            DatabaseHelper.deleteDocumentTextsAtPath(connection, mLoggingUserId, file.getAbsolutePath());
            if (type.equals("text/csv")) {
                CsvReader csv = new CsvReader();
                ArrayList<TextObject> texts = csv.ReadText(file, mCsvIdHeaders, mCsvTextHeaders, mCsvCategoryHeaders, isModelData, isSourceData);
                mTextList.addAll(texts);
            } else if (type.equals("application/pdf")) {
                PdfReader pdf = new PdfReader();
                ArrayList<TextObject> text = pdf.ReadText(file, isModelData, isSourceData);
                mTextList.addAll(text);
            } else if (type.equals("text/html")) {
                HtmlReader html = new HtmlReader();
                ArrayList<TextObject> text = html.ReadText(file, isModelData, isSourceData);
                mTextList.addAll(text);
            } else if (type.equals("text/plain")) {
                TextReader text_reader = new TextReader();
                ArrayList<TextObject> text = text_reader.ReadText(file, isModelData, isSourceData);
                mTextList.addAll(text);
            }
            mFilesToProcess.add(file.getAbsolutePath());
        }
    }
    
    @Override
    public void initialize() throws ResourceInitializationException {
        super.initialize();
        mBaseFolder = (String) getConfigParameterValue(PARAM_BASE_FOLDER);
        mIsRecursive = (Boolean) getConfigParameterValue(PARAM_RECURSIVE_FOLDER);
        mReadText = (Boolean) getConfigParameterValue(PARAM_READ_TEXT);
        mReadPdf = (Boolean) getConfigParameterValue(PARAM_READ_PDF);
        mReadCsv = (Boolean) getConfigParameterValue(PARAM_READ_CSV);
        mReadHtml = (Boolean) getConfigParameterValue(PARAM_READ_HTML);
        mReadWord = (Boolean) getConfigParameterValue(PARAM_READ_WORD);
        mCsvIdHeaders = (String[]) getConfigParameterValue(PARAM_CSV_ID_HEADERS);
        mCsvTextHeaders = (String[]) getConfigParameterValue(PARAM_CSV_TEXT_HEADERS);
        mCsvCategoryHeaders = (String[]) getConfigParameterValue(PARAM_CSV_CATEGORY_HEADERS);
        mDataType = (Integer) getConfigParameterValue(PARAM_DATA_TYPE);
        mCleanData = (Boolean) getConfigParameterValue(PARAM_CLEAN_DATA);
        
        mTextListIndex = 0;
        mTextList = new ArrayList<TextObject>();
        mFilesToProcess = new ArrayList<String>();
        
        try (DatabaseConnector connector  = getDatabaseConnector()) {
            connector.connect();
            Connection connection = connector.getConnection();
            mLoggingUserId = DatabaseHelper.getLoggingUserNameId(connection, LOGGING_USER);
            DatabaseHelper.cleanSpellingCorrections(connection, mLoggingUserId);
            //assume no processed files to clean the data. 
            if (mCleanData) {
            	mProcessedFiles = new ArrayList<String>();
            } else {
            	mProcessedFiles = DatabaseHelper.selectUnprocessedFiles(connection, mLoggingUserId);
            }
            
          //only set the queue ids if the job will use the run queue. 
            if (getUseRunQueue() == Job.JOB_QUEUE_INSERT || getUseRunQueue() == Job.JOB_QUEUE_PROCESS) {
            	ArrayList<JobStatus> job_status_list = DatabaseHelper.selectJobStausList(connection, mLoggingUserId);
            	ArrayList<JobType> job_type_list = DatabaseHelper.selectJobTypesList(connection, mLoggingUserId);
            	//sets the default job status and type
            	for (JobStatus s : job_status_list) {
            		if (s.getJobStatusLabel().equals(JobStatus.JOB_STATUS_NOT_STARTED)) {
            			mJobNotStartedId = s.getJobStatusId();
            			
            		} else if (s.getJobStatusLabel().equals(JobStatus.JOB_STATUS_FINISHED)) {
            			mJobFinishedId = s.getJobStatusId();
            		}
            	}
            	if (mJobNotStartedId == null) {
            		throw new ResourceInitializationException("Count not find 'not started' job status.", new Object[0]);
            	}
            	
            	if (mJobFinishedId == null) {
            		throw new ResourceInitializationException("Count not find 'finished' job status.", new Object[0]);
            	}
            	
            	for (JobType t : job_type_list) {
            		if (t.getJobTypeLabel().equals(JobType.JOB_PROCESS_DOCUMENT)) {
            			mJobTypeId = t.getJobTypeId();
            			break;
            		}
            	}
            	if (mJobTypeId == null) {
            		throw new ResourceInitializationException("Count not find the job process document job type.", new Object[0]);
            	}
            }
            
            if (getUseRunQueue() == Job.JOB_QUEUE_DISABLED || getUseRunQueue() == Job.JOB_QUEUE_INSERT) {
            	//Here is where mTextList list gets populated
                ArrayList<File> folderList = PopulateFiles(new File(mBaseFolder));
                ReadFiles(connection, folderList);
                
                //insert all objects into the databases for processing.  
                for (TextObject text_object : mTextList) {
                	if (text_object.getIsModelData() == true) {
                		DatabaseHelper.insertCategory(connection, mLoggingUserId, text_object.getCategory());
                        Integer database_id = DatabaseHelper.insertCategoryText(connection, mLoggingUserId, text_object.getId(), text_object.getText(), text_object.getPath(), text_object.getCategory(), true);
                        text_object.setDatabaseId(database_id);
                    } else {
                    	Integer database_id = DatabaseHelper.insertDocumentText(connection, mLoggingUserId, text_object.getId(), text_object.getText(), text_object.getPath());
                        text_object.setDatabaseId(database_id);
                    } 
                }
            }
            
            //create all processing jobs and clear the text list. 
            if (getUseRunQueue() == Job.JOB_QUEUE_INSERT) {
            	createJob(connection, mLoggingUserId);
            	mTextList.clear();
            } else if (getUseRunQueue() == Job.JOB_QUEUE_PROCESS) {
            	mJobQueueIds = new ArrayList<Integer>();
            	getNextJob(connection, mLoggingUserId);
            }
        } catch (IOException | SQLException | ClassNotFoundException | CollectionException e) {
            e.printStackTrace();
            throw new ResourceInitializationException();
        }
    }
    
    
    @Override
    public void getNext(CAS aCAS) throws IOException, CollectionException {
        JCas jcas;
        try {
            jcas = aCAS.getJCas();
        } catch (CASException e) {
            throw new CollectionException(e);
        }
        int current_index = mTextListIndex++;
        TextObject text_object = mTextList.get(current_index);
        addDatabaseToCas(jcas);
        
        int job_queue_id = 0;
        if (getUseRunQueue() == Job.JOB_QUEUE_PROCESS) {
        	job_queue_id = mJobQueueIds.get(current_index);
        }

        UnprocessedText text = new UnprocessedText(jcas);
        text.setRawTextString(text_object.getText());
        jcas.setDocumentText(CleanText.Standardize(text_object.getText()));
        
        
        if (text_object.getIsModelData() == true) {
            text.setIsDocument(false);
        } else {
            text.setIsDocument(true);
        } 
        text.setTextId(text_object.getDatabaseId());
        text.setJobQueueId(job_queue_id);
        text.addToIndexes();
    }
    
    public void updatePaths(Connection connection) throws SQLException {
    	for (String file_path : mFilesToProcess) {
        	DatabaseHelper.updateProcessedFilesSetFinished(connection, mLoggingUserId, file_path);
        }
    }

    @Override
    public boolean hasNext() throws IOException, CollectionException {
    	/**
    	 * This might not be the best place to do this but I would hope that this gets closed only after all processing happens. 
    	 */
        // TODO Auto-generated method stub
    	
    	try (DatabaseConnector connector  = getDatabaseConnector()) {
            connector.connect();
            Connection connection = connector.getConnection();
            updatePaths(connection);
            
            if (getUseRunQueue() == Job.JOB_QUEUE_PROCESS) {
            	for (Integer job_queue_id : mJobQueueIds) {
            		//go from running to cleaning up
            		DatabaseHelper.incrementNextJobStatus(connection, mLoggingUserId, job_queue_id);
            		//go from cleaning up to finished
            		DatabaseHelper.incrementNextJobStatus(connection, mLoggingUserId, job_queue_id);
            		//delete it. 
            		DatabaseHelper.deleteJobFromQueue(connection, mLoggingUserId, job_queue_id, mJobNotStartedId);
            	}
            }
            
        } catch (IOException | SQLException | ClassNotFoundException e) {
            e.printStackTrace();
            throw new IOException();
        }
    	
        return mTextListIndex < mTextList.size();
    }

    @Override
    public Progress[] getProgress() {
         return new Progress[] { new ProgressImpl(mTextListIndex, mTextList.size(), Progress.ENTITIES) };
    }

    @Override
    public void close() throws IOException {

    }

	@Override
	public void getNextJob(Connection connection, Integer user_id) throws SQLException {
		Integer job_queue_id = DatabaseHelper.getNextJob(connection, user_id, mJobNotStartedId, mJobTypeId);
		mJobQueueIds.add(job_queue_id);
		HashMap<String, String> params = getJobParameters(connection, user_id, job_queue_id);
    	
		String text_id = null;
		if (params.containsKey("text_id")) {
			text_id = params.get("text_id");
		}
    	String raw_text = params.get("raw_text");
    	String path = params.get("path");
    	Boolean is_model_data = Boolean.parseBoolean(params.get("is_model_data"));
    	Integer database_id = Integer.parseInt(params.get("database_id"));
    	
    	String category = null;
    	if (params.containsKey("category")) {
			text_id = params.get("category");
		}
    	TextObject text_object = new TextObject(text_id, raw_text, path, is_model_data, database_id, text_id);
    	mTextList.add(text_object);
	}

	@Override
	public void createJob(Connection connection, Integer user_id) throws SQLException {
		for (TextObject text_object : mTextList) {
        	Integer job_queue_id = DatabaseHelper.insertNewJob(connection, user_id,  mJobTypeId, mJobNotStartedId);
        	HashMap<String, String> params = new HashMap<String, String>();
        	String text_id = text_object.getId();
        	String raw_text = text_object.getText();
        	String path = text_object.getPath();
        	Boolean is_model_data = text_object.getIsModelData();
        	Integer database_id = text_object.getDatabaseId();
        	String category = text_object.getCategory();
        	
        	if (text_id != null) {
        		params.put("text_id", text_id);
        	}
        	params.put("raw_text", raw_text);
        	params.put("path", path);
        	params.put("is_model_data", is_model_data.toString());
        	params.put("database_id", database_id.toString());
        	
        	if (category != null) {
        		params.put("category", category);
        	}
        	
        	setJobParameters(connection, user_id, job_queue_id, params);
			
        }
		
	}

	@Override
	public HashMap<String, String> getJobParameters(Connection connection, Integer user_id, Integer job_queue_id)
			throws SQLException {
		ArrayList<JobParameter> param_list = DatabaseHelper.selectJobParametersFromJobId(connection, user_id, job_queue_id);
		HashMap<String, String> ret = new HashMap<String, String>();
		for (JobParameter p: param_list) {
			ret.put(p.getmParamName(), p.getmParamValue());
		}
		return ret;
	}

	@Override
	public void setJobParameters(Connection connection, Integer user_id, Integer job_queue_id,
			HashMap<String, String> params) throws SQLException {
		for (String s : params.keySet()) {
			DatabaseHelper.insertJobParameter(connection, user_id, job_queue_id, s, params.get(s), null);
		}
		
	}
}
