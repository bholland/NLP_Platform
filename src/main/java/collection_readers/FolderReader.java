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
    
    private String mBaseFolder = null;
    private Boolean mIsRecursive = null;
    private Boolean mReadText = null;
    private Boolean mReadPdf = null;
    private Boolean mReadCsv = null;
    private Boolean mReadHtml = null;
    private Boolean mReadWord = null;
    private Integer mDataType = null;
    
    private String[] mCsvIdHeaders = null;
    private String[] mCsvTextHeaders = null;
    
    //private int mFolderListIndex;
    //private ArrayList<File> mFolderList = null;
    
    private int mTextListIndex;
    private ArrayList<TextObject> mTextList = null;
    
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
    
    public void ReadFiles(ArrayList<File> folderList) throws IOException, CollectionException {        
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
            if (type.equals("text/csv")) {
                CsvReader csv = new CsvReader();
                ArrayList<TextObject> texts = csv.ReadText(file, mCsvIdHeaders, mCsvTextHeaders, isModelData, isSourceData);
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
        mDataType = (Integer) getConfigParameterValue(PARAM_DATA_TYPE);
        
        mTextListIndex = 0;
        mTextList = new ArrayList<TextObject>();
        
        try (DatabaseConnector connector  = getDatabaseConnector()) {
            connector.connect();
            Connection connection = connector.getConnection();
            mLoggingUserId = DatabaseHelper.getLoggingUserNameId(connection, LOGGING_USER);
            DatabaseHelper.cleanSpellingCorrections(connection, mLoggingUserId);
            
            
            ArrayList<File> folderList = PopulateFiles(new File(mBaseFolder));
            ReadFiles(folderList);
            
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
        TextObject text_object = mTextList.get(mTextListIndex++);
        addDatabaseToCas(jcas);
        
        try (DatabaseConnector connector = getDatabaseConnector()) {
            connector.connect();
            Connection connection = connector.getConnection();
            Integer database_id = null;
            
            UnprocessedText text = new UnprocessedText(jcas);
            text.setRawTextString(text_object.getText());
            jcas.setDocumentText(CleanText.Standardize(text_object.getText()));
            
            /**
             * @TODO: make this work with the categorical text.  
             */
            if (text_object.getIsModelData() == true) {
                database_id = DatabaseHelper.insertCategoryText(connection, mLoggingUserId, text_object.getId(), text_object.getText(), null, true);
                text.setIsDocument(false);
            } else {
                database_id = DatabaseHelper.insertDocumentText(connection, mLoggingUserId, text_object.getId(), text_object.getText());
                text.setIsDocument(true);
            } 
            text.setTextId(database_id);
            text.addToIndexes();
        } catch (SQLException | ClassNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            throw new CollectionException();
        }
    }

    @Override
    public boolean hasNext() throws IOException, CollectionException {
        return mTextListIndex < mTextList.size();
    }

    @Override
    public Progress[] getProgress() {
         return new Progress[] { new ProgressImpl(mTextListIndex, mTextList.size(), Progress.ENTITIES) };
    }

    @Override
    public void close() throws IOException {
        // TODO Auto-generated method stub

    }

}
