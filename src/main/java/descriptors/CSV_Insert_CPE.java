package descriptors;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;

import org.apache.uima.cas.CAS;
import org.apache.uima.cas.CASException;
import org.apache.uima.collection.CollectionException;
import org.apache.uima.jcas.JCas;
import org.apache.uima.resource.ResourceInitializationException;
import org.apache.uima.util.Progress;
import org.apache.uima.util.ProgressImpl;

import database.DatabaseConnector;
import helper.CleanText;
import helper.DatabaseHelper;
import objects.UnprocessedText;

import org.apache.commons.lang.StringEscapeUtils;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;

import java.sql.*;


public class CSV_Insert_CPE extends DatabaseCollectionReader_ImplBase {
    
    /**
     * The CSV file path.   
     */
    public static final String PARAM_CSV_FILE = "CsvFile";

    /**
     * The CSV id column. This ID will not be used as the ID of the database. 
     */
    public static final String PARAM_CSV_ID_COLUMN = "CsvIdColumn";
    
    /**
     * The CSV text column.   
     */
    public static final String PARAM_CSV_TEXT_COLUMN = "CsvTextColumn";
    
    /**
     * This is true if the associated csv file is model data.  
     */
    public static final String PARAM_IS_MODEL_DATA = "IsModelData";
    
    /**
     * This is true if the associated csv file is model data.  
     */
    public static final String PARAM_CSV_CATEGORY_COLUMN = "CsvCategoryColumn";
    
    public static final String PARAM_CSV_IS_IN_CATEGORY_COLUMN = "IsInCategoryColumn";
    
    private String[] mCsvFile;
    private String[] mCsvIdColumn;
    private String[] mCsvTextColumn;
    private Boolean[] mIsModelData;
    private String[] mCsvCategoryColumn;
    private String[] mIsInCategoryColumn;
    
    /**
     * mTextIDs: the array of text ids to process
     * mIsDocument: a boolean array where true means 
     */
    private ArrayList<Integer> mTextIDs = null;
    private ArrayList<Boolean> mIsDocument = null;
    private Integer mTextIndex = null;

    /**
     * @param connection: SQL connection
     * @param user_id: The logging user_id
     * @param csv_file: The csv file to read from
     * @param id_column: The name of the id column within the csv file (could be null)
     * @param text_column: The name of the text column within the csv file (cannot be null)
     * @param is_model_text: True if this text goes into a model, false if it just document text to categorize
     * @param category: The category to apply this to. Mandatory if is_model_text is true.
     * @param is_in_category_column: The csv column specifying if individual records belong to the category. If not null, this will override is_model_text.  
     * @throws SQLException
     * @throws FileNotFoundException
     * @throws IOException
     * @throws CollectionException
     */
    public void insertCSVFile(Connection connection, Integer user_id, String csv_file, String id_column, String text_column, boolean is_model_text, String category, String is_in_category_column) throws SQLException, FileNotFoundException, IOException, CollectionException {
        try (Reader csv_file_input = new FileReader(csv_file)) {
            
        	/*if (is_in_category_column != null && (!is_in_category_column.equals("0") &&  !is_in_category_column.equals("1") && 
        			!is_in_category_column.equals("false") && !is_in_category_column.equals("false"))) {
        		throw new CollectionException(String.format("is_in_category_column is not a valid value (%s). Valid values are 0, 1, false, and true.", is_in_category_column), new Object[0]);
        	}*/
        	
        	CSVParser parser = new CSVParser(csv_file_input, CSVFormat.EXCEL.withHeader());
            for (CSVRecord record : parser) {
                String id = "";
                if (id_column != null) {
                    id = StringEscapeUtils.escapeSql(record.get(id_column));
                }
                
                String text = StringEscapeUtils.escapeSql(record.get(text_column));
                String category_text = null;
                if (category != null) {
                    category_text = StringEscapeUtils.escapeSql(record.get(category));
                    DatabaseHelper.insertCategory(connection, user_id, category_text);
                }
                
                /*
                 * Use the category column to override is_model text, if provided.  
                 */
                if (is_in_category_column != null) {
                    String is_in_category_str = record.get(is_in_category_column).toLowerCase();
                    if (is_in_category_str.equals("0") || is_in_category_str.toLowerCase().equals("false")) {
                    	int text_id = DatabaseHelper.insertDocumentText(connection, user_id, id, text);
	                    mTextIDs.add(text_id);
	                    mIsDocument.add(true);
                    } else {
                    	if (category_text == null) {
                    		throw new SQLException(String.format("A row in %s is set to category data where %s is true but a category column is not provided.", csv_file, is_in_category_column)); 
                    	}
                    	
                    	
                    	int text_id = DatabaseHelper.insertCategoryText(connection, user_id, id, text, category_text, true);
	                    mTextIDs.add(text_id);
	                    mIsDocument.add(false);
                    }
                } else {
	                if (is_model_text == false) {
	                    int text_id = DatabaseHelper.insertDocumentText(connection, user_id, id, text);
	                    mTextIDs.add(text_id);
	                    mIsDocument.add(true);
	                } else {
	                    int text_id = DatabaseHelper.insertCategoryText(connection, user_id, id, text, category_text, true);
	                    mTextIDs.add(text_id);
	                    mIsDocument.add(false);
	                }
                }
            }
        }
    }
    
    public void CSVInsertDriver(Connection connection, Integer user_id, String[] csv_files, String[] csv_id_columns, String[] csv_text_columns, 
    		Boolean[] is_model_data, String[] csv_category_column, String[] is_in_category_column) throws ResourceInitializationException {
    	/*
    	 * primarily for testing purposes. These should probably get set on initialize(); 
    	 */
    	if (mCsvFile == null) {
    		mCsvFile = csv_files;
    		mCsvIdColumn = csv_id_columns;
            mCsvTextColumn = csv_text_columns;
            mIsModelData = is_model_data;
            mCsvCategoryColumn = csv_category_column;
            mIsInCategoryColumn = is_in_category_column;
    	} 
    	
    	try {
            /**
             * create_data_table
             * in in_drop_table tinyint
             * in in_table_name varchar(100)
             */
            
            for (int x = 0; x < mCsvFile.length; x++) {
                String id_col = mCsvIdColumn[x];
                String cat_col = mCsvCategoryColumn[x];
                String is_cat_col = mIsInCategoryColumn[x];
                if (id_col.toLowerCase().equals("none") || id_col.toLowerCase().equals("null")) {
                    id_col = null;
                }
                if (cat_col.toLowerCase().equals("none") || cat_col.toLowerCase().equals("null")) {
                    cat_col = null;
                }
                if (is_cat_col.toLowerCase().equals("none") || is_cat_col.toLowerCase().equals("null")) {
                    is_cat_col = null;
                }
				insertCSVFile(connection, user_id, mCsvFile[x], id_col, mCsvTextColumn[x], mIsModelData[x], cat_col, is_cat_col);
            }
        } catch (IOException | SQLException | CollectionException e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
            throw new ResourceInitializationException();
        }
    }
    
    @Override
    public void initialize() throws ResourceInitializationException {
        mCsvFile = (String[]) getConfigParameterValue(PARAM_CSV_FILE);
        mCsvIdColumn = (String[]) getConfigParameterValue(PARAM_CSV_ID_COLUMN);
        mCsvTextColumn = (String[]) getConfigParameterValue(PARAM_CSV_TEXT_COLUMN);
        mIsModelData = (Boolean[]) getConfigParameterValue(PARAM_IS_MODEL_DATA);
        mCsvCategoryColumn = (String[]) getConfigParameterValue(PARAM_CSV_CATEGORY_COLUMN);
        mIsInCategoryColumn = (String[]) getConfigParameterValue(PARAM_CSV_IS_IN_CATEGORY_COLUMN);
        /* I am not sure where the UIMA stack init's this object.*/
        if (mTextIDs == null) {
        	mTextIDs = new ArrayList<Integer>();
        	mIsDocument = new ArrayList<Boolean>();
        	mTextIndex = 0;
        }
        
        try (DatabaseConnector connector = getDatabaseConnector()) { 
            connector.connect();
            Connection connection = connector.getConnection();
            CSVInsertDriver(connection, connector.getLoggingUserId(), mCsvFile, mCsvIdColumn, mCsvTextColumn,
            		mIsModelData, mCsvCategoryColumn, mIsInCategoryColumn);
        } catch (IOException | SQLException | ClassNotFoundException e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
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
        int current_index = mTextIndex++;
        Integer text_id = mTextIDs.get(current_index);
        boolean is_source = mIsDocument.get(current_index);
        try (DatabaseConnector connector = getDatabaseConnector()) {
            connector.connect();
            Connection connection = connector.getConnection();
            String text = null;
            if (is_source) {
                text = DatabaseHelper.getDocumentTextFromID(connection, connector.getLoggingUserId(), text_id);
            } else {
                text = DatabaseHelper.getCategoryTextFromID(connection, connector.getLoggingUserId(), text_id);
            }
            
            jcas.setDocumentText(CleanText.Standardize(text));
            UnprocessedText unprocessed_text = new UnprocessedText(jcas);
            unprocessed_text.setTextId(text_id);
            unprocessed_text.setRawTextString(text);
            unprocessed_text.setIsDocument(is_source);
            unprocessed_text.addToIndexes();
            
            jcas = addDatabaseToCas(jcas);
        } catch (SQLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            throw new CollectionException();
        } catch (ClassNotFoundException e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        }
    }

    @Override
    public void close() throws IOException { 
        // TODO Auto-generated method stub

    }

    @Override
    public Progress[] getProgress() {
        return new Progress[] { new ProgressImpl(mTextIndex, mTextIDs.size(), Progress.ENTITIES) };
    }

    @Override
    public boolean hasNext() throws IOException, CollectionException {
        return mTextIndex < mTextIDs.size();
    }
    
    public CSV_Insert_CPE() {
    	super();
    	mTextIDs = new ArrayList<Integer>();
        mIsDocument = new ArrayList<Boolean>();
        mTextIndex = 0;
    }
}
