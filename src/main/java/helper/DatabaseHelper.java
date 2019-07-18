package helper;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.HashMap;

import org.apache.log4j.Logger;
import org.postgresql.largeobject.LargeObject;
import org.postgresql.largeobject.LargeObjectManager;

import job_queue.JobParameter;
import job_queue.JobQueue;
import job_queue.JobStatus;
import job_queue.JobType;
import multiuser.MultiuserResponse;
import multiuser.MultiuserReviewDocument;
import opennlp.tools.doccat.DoccatModel;

public class DatabaseHelper {
    
    private DatabaseHelper() {
        
    }
    
    /*public static void cleanAllText(Connection connection) throws SQLException {
        CallableStatement clear_text = connection.prepareCall("{call clean_all_text()}");
        clear_text.execute();
    }*/
    /**
     * Remove all spelling corrections. 
     * @param connection: sql connection
     * @throws SQLException
     */
    public static void cleanSpellingCorrections(Connection connection, Integer user_id) throws SQLException {
        CallableStatement clear_corrections = connection.prepareCall("{call clean_spelling_corrections(?)}");
        clear_corrections.setInt(1,  user_id);
        clear_corrections.execute();
    }
    
    /**
     * Insert a spelling correction. If there is already an entry for word then it will update a counter.  
     * @param connection: sql connection
     * @param word: the misspelled word
     * @param correction: the corrected word
     * @throws SQLException
     */
    public static void insertSpellingCorrection(Connection connection, Integer user_id, String word, String correction) throws SQLException {
        CallableStatement insert_corrections = connection.prepareCall("{call insert_spelling_correction(?, ?, ?)}");
        insert_corrections.setInt(1,  user_id);
        insert_corrections.setString(2, word);
        insert_corrections.setString(3, correction);
        insert_corrections.execute();
    }
    
    /**
     * Insert a new NLP category
     * @param connection: sql connection
     * @param category: category name (this will be converted to lowercase)
     * @throws SQLException
     */
    public static void insertCategory(Connection connection, Integer user_id, String category) throws SQLException {
        Integer category_id = getCategoryId(connection, user_id, category);
        if (category_id == null) {
            category = category.trim().toLowerCase();
            CallableStatement insert_statement = connection.prepareCall("{call insert_category(?, ?)}");
            insert_statement.setInt(1, user_id);
            insert_statement.setString(2, category);
            insert_statement.execute();
        }
    }
    
    /**
     * Get the category id by category
     * @param connection: sql connection
     * @param category: the category to retrieve
     * @return: the category id
     * @throws SQLException
     */
    public static Integer getCategoryId(Connection connection, Integer user_id, String category) throws SQLException {
        category = category.trim().toLowerCase();
        CallableStatement select_statement = connection.prepareCall("{call select_category(?, ?)}");
        select_statement.setInt(1, user_id);
        select_statement.setString(2, category);
        boolean has_ret = select_statement.execute();
        ResultSet rs_select = select_statement.getResultSet();
        Integer category_id = null;
        while (rs_select.next()) {
            category_id = rs_select.getInt(1);
            if (rs_select.wasNull()) {
                category_id = null;
            }
        }
        return category_id;
    }
    
    /* Document Text */
    
    /**
     * Insert the raw text as a Document (a text to categorize)
     * @param connection: sql connection
     * @param id: An optional id of the text, often coming from the original data document 
     * @param text: The text to insert
     * @return: the document text_id
     * @throws SQLException
     */
    public static Integer insertDocumentText(Connection connection, Integer user_id, String id, String text, String document_path) throws SQLException {
        CallableStatement insert_statement = connection.prepareCall("{call insert_document_text(?, ?, ?, ?)}");
        insert_statement.setInt(1, user_id);
        insert_statement.setString(2, id);
        insert_statement.setString(3, text);
        insert_statement.setString(4, document_path);
        insert_statement.execute();
        ResultSet rs = insert_statement.getResultSet();
        Integer ret = null;
        while (rs.next()) {
            ret = rs.getInt(1);
        }
        
        if (ret == null) {
            throw new SQLException();
        }
        return ret;
    }
    
    /**
     * Insert a document sentence (a sentence to categorize).  
     * @param connection: sql connection
     * @param sentence_string: the string to insert
     * @param text_id: the document text_id
     * @param sentence_number: the number of the sentence within the text.
     * @return: the sentence id from the inserted sentence
     * @throws SQLException
     */
    public static Integer insertDocumentSentence(Connection connection, Integer user_id, String sentence_string, Integer text_id, Integer sentence_number) throws SQLException {
        CallableStatement insert_statement = connection.prepareCall("{call insert_document_sentence(?, ?, ?, ?, ?)}");
        insert_statement.setInt(1,  user_id);
        insert_statement.setString(2, sentence_string);
        
        if (text_id == null) {
            insert_statement.setNull(3, java.sql.Types.INTEGER);
        } else {
            insert_statement.setInt(3, text_id);
        }
        
        if (sentence_number == null) {
            insert_statement.setNull(4, java.sql.Types.INTEGER);
        } else {
            insert_statement.setInt(4, sentence_number);
        }
        
        insert_statement.registerOutParameter(5, Types.INTEGER);
        boolean has_results = insert_statement.execute();
        Integer sentence_id = insert_statement.getInt(5);
        return sentence_id;
    }
    
    /**
     * Insert document sentence metadata (the metadata for a sentence to categorize)
     * @param connection: sql connection
     * @param sentence_id: the document sentence id
     * @param position_in_sentence: the position of the token in the sentence
     * @param token: the token
     * @param pos_tag: the part of speech tag for the token
     * @param chunk: NLP chunk 
     * @param stemmed_token: the stemmed token
     * @throws SQLException
     */
    public static void insertDocumentSentenceMetadata(Connection connection, Integer user_id, Integer sentence_id, Integer position_in_sentence, String token, String pos_tag, String chunk, String stemmed_token, Boolean is_raw_text) throws SQLException {
        CallableStatement insert_statement = connection.prepareCall("{call insert_document_sentence_metadata(?, ?, ?, ?, ?, ?, ?, ?)}");
        insert_statement.setInt(1, user_id);
        insert_statement.setInt(2, sentence_id);
        insert_statement.setInt(3, position_in_sentence);
        insert_statement.setString(4, token);
        insert_statement.setString(5, pos_tag);
        insert_statement.setString(6, chunk);
        insert_statement.setString(7, stemmed_token);
        insert_statement.setBoolean(8, is_raw_text);
        if (token.length() > 15) {
            Logger logger = Logger.getRootLogger();
            logger.warn(String.format("Warning: Very long token in text. Text id: %s word: %s", sentence_id, token));
        }
        boolean has_results = insert_statement.execute();
    }
    
    /* Category Text*/
    
    /**
     * Insert a category sentence (a categorical sentence to categorize). 
     * @param connection: sql connection
     * @param id: An optional id of the text, often coming from the original data document  
     * @param text: category text
     * @param category: the mandatory category that this text belongs to
     * @param is_in_category: Is text_id in or not in the category_id? If true, this pairing gets added to the is_* training dataset. If false, this pairing gets added to the is_not_* training dataset.
     * @return
     * @throws SQLException
     */
    public static int insertCategoryText(Connection connection, Integer user_id, String id, String text, String document_path, String category, boolean is_in_category) throws SQLException {
        
        if (category == null) {
            throw new SQLException("All category text must be linked to a category."); 
        }
        
        CallableStatement insert_statement = connection.prepareCall("{call insert_category_text(?, ?, ?, ?)}");
        insert_statement.setInt(1, user_id);
        insert_statement.setString(2, id);
        insert_statement.setString(3, text);
        insert_statement.setString(4,  document_path);
        insert_statement.execute();
        ResultSet rs = insert_statement.getResultSet();
        Integer text_id = null;
        while (rs.next()) {
            text_id = rs.getInt(1);
        }
        
        if (text_id == null) {
            throw new SQLException();
        }
        
        if (category != null) {
            Integer category_id = getCategoryId(connection, user_id, category);
            //check but this should never happen, 
            if (category_id == null) {
                throw new SQLException("Category does not exist in the database.");
            }
            
            insert_statement = connection.prepareCall("{call insert_training_data(?, ?, ?, ?)}");
            insert_statement.setInt(1, user_id);
            insert_statement.setInt(2, text_id);
            insert_statement.setInt(3, category_id);
            insert_statement.setBoolean(4, is_in_category);
            insert_statement.execute();
        }
        
        return text_id;
    }
    
    /**
     * Insert a category sentence (a categorization sentence).  
     * @param connection: sql connection
     * @param sentence_string: the string to insert
     * @param text_id: the document text_id
     * @param sentence_number: the number of the sentence within the text.
     * @return: the sentence id from the inserted sentence
     * @throws SQLException
     */
    public static Integer insertCategorySentence(Connection connection, Integer user_id, String sentence_string, Integer text_id, Integer sentence_number) throws SQLException {
        CallableStatement insert_statement = connection.prepareCall("{call insert_category_sentence(?, ?, ?, ?, ?)}");
        insert_statement.setInt(1, user_id);
        insert_statement.setString(2, sentence_string);
        insert_statement.setInt(3, text_id);
        insert_statement.setInt(4, sentence_number);
        insert_statement.registerOutParameter(5, Types.INTEGER);
        boolean has_results = insert_statement.execute();
        Integer sentence_id = insert_statement.getInt(5);
        return sentence_id;
    }
    
    /**
     * Insert category sentence metadata (the metadata for a categorization sentence)
     * @param connection: sql connection
     * @param sentence_id: the document sentence id
     * @param position_in_sentence: the position of the token in the sentence
     * @param token: the token
     * @param pos_tag: the part of speech tag for the token
     * @param chunk: NLP chunk 
     * @param stemmed_token: the stemmed token
     * @throws SQLException
     */
    public static void insertCategorySentenceMetadata(Connection connection, Integer user_id, Integer sentence_id, Integer position_in_sentence, String token, String pos_tag, String chunk, String stemmed_token, Boolean is_raw_text) throws SQLException {
        CallableStatement insert_statement = connection.prepareCall("{call insert_category_sentence_metadata(?, ?, ?, ?, ?, ?, ?, ?)}");
        insert_statement.setInt(1, user_id);
        insert_statement.setInt(2, sentence_id);
        insert_statement.setInt(3, position_in_sentence);
        insert_statement.setString(4, token);
        insert_statement.setString(5, pos_tag);
        insert_statement.setString(6, chunk);
        insert_statement.setString(7, stemmed_token);
        insert_statement.setBoolean(8, is_raw_text);
        if (token.length() > 15) {
            Logger logger = Logger.getRootLogger();
            logger.warn(String.format("Warning: Very long token in text. Text id: %s word: %s", sentence_id, token));
        }
        boolean has_results = insert_statement.execute();
    }
    
    public static HashMap<Integer, ArrayList<String>> getDocumentTokens(Connection connection, Integer user_id)  throws SQLException {
        HashMap<Integer, ArrayList<String>> ret = new HashMap<Integer, ArrayList<String>>();
        CallableStatement select_statement = connection.prepareCall("{call select_document_tokens(?)}");
        select_statement.setInt(1, user_id);
        boolean has_ret = select_statement.execute();
        ResultSet rs_select = select_statement.getResultSet();
        while (rs_select.next()) {
            int document_id = rs_select.getInt(1);
            int sentence_index = rs_select.getInt(2);
            String token = rs_select.getString(3);
            String tag = rs_select.getString(4);
            if (!ret.containsKey(document_id)) {
                ret.put(document_id, new ArrayList<String>());
            }
            ArrayList<String> tokens = ret.get(document_id);
            tokens.add(token);
        }
        return ret;
    }
    
    /**
     * This will put the model file into the database using the Large Object system.
     * @param connection: sql connection
     * @param user_id: the logging user id
     * @param category_id: the category to update
     * @param model: the doccat model
     * @throws SQLException
     * @throws IOException
     */
    public static void putDoccatModelFile(Connection connection, Integer user_id, Integer category_id, DoccatModel model) throws SQLException, IOException {
    	//Large object API calls have to be within a transaction. 
    	connection.setAutoCommit(false);
    	
    	//get the large object manager and note that this likely will break all MsSQL command and will probably
    	//force this application to only support PostgreSQL
    	LargeObjectManager lo_manager = connection.unwrap(org.postgresql.PGConnection.class).getLargeObjectAPI();
    	
    	//create a new large object
    	long oid = lo_manager.createLO(LargeObjectManager.READWRITE);
    	
    	// Open the large object for writing
    	LargeObject obj = lo_manager.open(oid, LargeObjectManager.WRITE);
    	
        ObjectOutputStream obj_output_stream = new ObjectOutputStream(obj.getOutputStream());
        obj_output_stream.writeObject(model);
        obj_output_stream.flush();
        obj.close();
        
        /*
        //convert the model object to a byte array input stream
    	ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ObjectOutputStream oos = new ObjectOutputStream(baos);
        oos.writeObject(model);
        oos.close();
        ByteArrayInputStream bais = new ByteArrayInputStream(baos.toByteArray());
    	
    	// Copy the data from the file to the large object using 4k pages
        int buff_size = 4096;
    	byte buf[] = new byte[buff_size];
    	
    	int read_bytes = 0;
    	int total_bytes = 0;
    	while ((read_bytes = bais.read(buf, total_bytes, buff_size)) > 0)
    	{
    	    obj.write(buf, 0, read_bytes);
    	    total_bytes += read_bytes;
    	}

    	// Close the large object
    	obj.close();
    	*/
    	CallableStatement insert_statement = connection.prepareCall("{call insert_category_model_file(?, ?, ?)}");
        insert_statement.setInt(1,  user_id);
        insert_statement.setInt(2, category_id);
        insert_statement.setLong(3, oid);
        insert_statement.execute();
        connection.commit();
        connection.setAutoCommit(true);
    }
    
    public static DoccatModel getDoccatModel(Connection connection, Integer user_id, Integer category_id) throws SQLException, IOException, ClassNotFoundException {
        CallableStatement select_statement = connection.prepareCall("{call select_nlp_model(?, ?)}");
        select_statement.setInt(1, user_id);
        select_statement.setInt(2, category_id);
        select_statement.execute();
        ResultSet rs_select = select_statement.getResultSet();
        Long oid = null;
        while (rs_select.next()) {
        	oid = rs_select.getLong(1);
        }
        if (oid == null) {
        	throw new SQLException(String.format("No object for category: %s" , category_id));
        }
        
        // All LargeObject API calls must be within a transaction block
        connection.setAutoCommit(false);
        
        //Get the large object manager
        LargeObjectManager lo_manager = connection.unwrap(org.postgresql.PGConnection.class).getLargeObjectAPI();
        
        //open up the object with the oid in read mode
        LargeObject obj = lo_manager.open(oid, LargeObjectManager.READ);
        
        DoccatModel ret = null;
        ObjectInputStream ois = new ObjectInputStream(obj.getInputStream());
        ret = (DoccatModel)ois.readObject();
        connection.commit();
        connection.setAutoCommit(true);
        return ret;
    }
    
    public static void incrementBatchNumber(Connection connection, Integer user_id) throws SQLException {
        CallableStatement select_statement = connection.prepareCall("{call increment_batch_number(?)}");
        select_statement.setInt(1, user_id);
        select_statement.execute();
        ResultSet rs_select = select_statement.getResultSet();
        int ret = 0;
        while (rs_select.next()) {
            ret = rs_select.getInt(1);
        }
    }
    
    public static Integer selectBatchNumber(Connection connection, Integer user_id) throws SQLException {
        CallableStatement select_statement = connection.prepareCall("{call select_batch_number(?)}");
        select_statement.setInt(1, user_id);
        select_statement.execute();
        ResultSet rs_select = select_statement.getResultSet();
        Integer ret = null;
        while (rs_select.next()) {
            ret = rs_select.getInt(1);
        }
        return ret;
    }
    
    public static void insertCategoryProbabilities(Connection connection, Integer user_id, Integer batch_number, Integer text_id, Integer category_id, String cat1_label, double cat1, String cat2_label, double cat2) throws SQLException {
        CallableStatement select_statement = connection.prepareCall("{call insert_text_category_probability(?, ?, ?, ?, ?, ?, ?, ?)}");
        
        select_statement.setInt(1, user_id);
        select_statement.setInt(2, text_id);
        select_statement.setInt(3, category_id);
        select_statement.setInt(4, batch_number);
        select_statement.setDouble(5, cat1);
        select_statement.setDouble(6, cat2);
        select_statement.setString(7, cat1_label);
        select_statement.setString(8, cat2_label);
        select_statement.execute();
    }
    
    public static String getCategoryTextFromID(Connection connection, Integer user_id, int id) throws SQLException {
        CallableStatement select_statement = connection.prepareCall("{call select_category_text_from_id(?, ?)}");
        select_statement.setInt(1, user_id);
        select_statement.setInt(2, id);
        select_statement.execute();
        
        ResultSet rs = select_statement.getResultSet();
        String ret = null;
        while (rs.next()) {
            ret = rs.getString(1);
        }
        
        if (ret == null) {
            throw new SQLException();
        }
        return ret;
    }
    
    public static String getDocumentTextFromID(Connection connection, Integer user_id, int id) throws SQLException {
        CallableStatement select_statement = connection.prepareCall("{call select_document_text_from_id(?, ?)}");
        select_statement.setInt(1, user_id);
        select_statement.setInt(2, id);
        select_statement.execute();
        
        ResultSet rs = select_statement.getResultSet();
        String ret = null;
        while (rs.next()) {
            ret = rs.getString(1);
        }
        return ret;
    }
    
    /**
     * Update the training data dataset.  
     * @param connection: sql connection
     * @param document_text_id: the document text id that you want to assign to a particular category
     * @param category_id: the category id you want to assign the document id to  
     * @param is_in_category: Is text_id in or not in the category_id? If true, this pairing gets added to the is_* training dataset. If false, this pairing gets added to the is_not_* training dataset. 
     * @throws SQLException
     */
    public static void assignDocumentToCategory(Connection connection, Integer user_id, int document_text_id, int category_id, boolean is_in_category) throws SQLException {
        CallableStatement select_statement = connection.prepareCall("{call copy_document_to_category_text(?, ?)}");
        select_statement.setInt(1, user_id);
        select_statement.setInt(2, document_text_id);
        select_statement.execute();
        ResultSet rs = select_statement.getResultSet();
        Integer category_text_id = null;
        while (rs.next()) {
        	category_text_id = rs.getInt(1);
        }
        
        CallableStatement insert_statement = connection.prepareCall("{call insert_training_data(?, ?, ?, ?)}");
        insert_statement.setInt(1, user_id);
        insert_statement.setInt(2, category_text_id);
        insert_statement.setInt(3, category_id);
        insert_statement.setBoolean(4, is_in_category);
        insert_statement.execute();
    }
    
    public static Integer getLoggingUserNameId(Connection connection, String user_name) throws SQLException {
    	CallableStatement select_statement = connection.prepareCall("{call select_username_id(?)}");
        select_statement.setString(1, user_name);
        select_statement.execute();
        
        ResultSet rs = select_statement.getResultSet();
        Integer ret = null;
        while (rs.next()) {
            ret = rs.getInt(1);
        }
        return ret;
    }
    
    /**
     * @param connection: SQL connection
     * @param user_id: the logging user_id
     * @param user_name: new user name
     * @param email: user email
     * @param first_name: optional first name
     * @param last_name: optional last name
     * @return
     * @throws SQLException
     */
    public static Integer addNewUser(Connection connection, String user_name, String email, String first_name, String last_name) throws SQLException {
    	CallableStatement insert_statement = connection.prepareCall("{call insert_new_user(?, ?, ?, ?)}");
    	insert_statement.setString(1, user_name);
        insert_statement.setString(2, email);
        insert_statement.setString(3, first_name);
        insert_statement.setString(4, last_name);
        insert_statement.execute();
        
        ResultSet rs = insert_statement.getResultSet();
        Integer new_user_id = null;
        while (rs.next()) {
        	new_user_id = rs.getInt(1);
        }
        return new_user_id;
    }
    
    public static void deleteApplicationUser(Connection connection, Integer user_id, Integer application_user_id) throws SQLException {
    	CallableStatement delete_statement = connection.prepareCall("{call delete_application_user(?, ?)}");
        delete_statement.setInt(1, user_id);
        delete_statement.setInt(2, application_user_id);
        delete_statement.execute();
    }
    
    public static Integer insertRole(Connection connection, Integer user_id, String role_name, String display_name, String description) throws SQLException {
    	CallableStatement insert_statement = connection.prepareCall("{call insert_new_role(?, ?, ?, ?)}");
        insert_statement.setInt(1, user_id);
        insert_statement.setString(2, role_name);
        insert_statement.setString(3, display_name);
        insert_statement.setString(4, description);
        insert_statement.execute();
        
        ResultSet rs = insert_statement.getResultSet();
        Integer new_role_id = null;
        while (rs.next()) {
        	new_role_id = rs.getInt(1);
        }
        return new_role_id;
    }
    
    public static Integer insertUserRole(Connection connection, Integer user_id, Integer role_user_id, Integer role_id) throws SQLException {
    	CallableStatement insert_statement = connection.prepareCall("{call insert_user_role(?, ?, ?)}");
        insert_statement.setInt(1, user_id);
        insert_statement.setInt(2,  role_user_id);
        insert_statement.setInt(3,  role_id);
        insert_statement.execute();
        
        ResultSet rs = insert_statement.getResultSet();
        Integer new_user_role_id = null;
        while (rs.next()) {
        	new_user_role_id = rs.getInt(1);
        }
        return new_user_role_id;
    }
    
    public static void deleteUserRole(Connection connection, Integer user_id, Integer role_user_id) throws SQLException {
    	CallableStatement delete_statement = connection.prepareCall("{call delete_user_roles(?, ?)}");
        delete_statement.setInt(1, user_id);
        delete_statement.setInt(2, role_user_id);
        delete_statement.execute();
    }
    /**
     * @TODO: Unit test these functions
     * @param connection
     * @param user_id
     * @param category_text_id
     * @param processed_text
     * @param num_tokens
     * @throws SQLException
     */
    public static void insertCategoryTokenCount(Connection connection, Integer user_id, Integer category_text_id, String processed_text, Integer num_tokens) throws SQLException {
    	CallableStatement sp_call = connection.prepareCall("{call insert_category_token_count(?, ?, ?, ?)}");
    	sp_call.setInt(1, user_id);
    	sp_call.setInt(2, category_text_id);
        sp_call.setString(3,  processed_text);
        sp_call.setInt(4, num_tokens);
        sp_call.execute();
    }
    
    public static void insertDocumentTokenCount(Connection connection, Integer user_id, Integer document_text_id, String processed_text, Integer num_tokens) throws SQLException {
    	CallableStatement sp_call = connection.prepareCall("{call insert_document_token_count(?, ?, ?, ?)}");
    	sp_call.setInt(1, user_id);
    	sp_call.setInt(2, document_text_id);
        sp_call.setString(3,  processed_text);
        sp_call.setInt(4, num_tokens);
        sp_call.execute();
    }
    
    public static void cleanCategorySentences(Connection connection, Integer user_id, Integer category_text_id) throws SQLException {
    	CallableStatement sp_call = connection.prepareCall("{call clean_category_sentences(?, ?)}");
    	sp_call.setInt(1, user_id);
    	sp_call.setInt(2, category_text_id);
        sp_call.execute();
    }
    
    public static void cleanDocumentSentences(Connection connection, Integer user_id, Integer category_text_id) throws SQLException {
    	CallableStatement sp_call = connection.prepareCall("{call clean_document_sentences(?, ?)}");
    	sp_call.setInt(1, user_id);
    	sp_call.setInt(2, category_text_id);
        sp_call.execute();
    }
    
    public static ArrayList<String> selectUnprocessedFiles(Connection connection, Integer user_id) throws SQLException {
    	CallableStatement sp_call = connection.prepareCall("{call select_unprocessed_files(?)}");
    	sp_call.setInt(1, user_id);
    	sp_call.execute();
        
        ResultSet rs = sp_call.getResultSet();
        ArrayList<String> files = new ArrayList<String>();
        String file_path;
        while (rs.next()) {
        	file_path  = rs.getString(1);
        	files.add(file_path);
        }
        return files;
    }
    
    public static ArrayList<String> selectProcessedFiles(Connection connection, Integer user_id) throws SQLException {
    	CallableStatement sp_call = connection.prepareCall("{call select_processed_files(?)}");
    	sp_call.setInt(1, user_id);
    	sp_call.execute();
        
        ResultSet rs = sp_call.getResultSet();
        ArrayList<String> files = new ArrayList<String>();
        String file_path;
        while (rs.next()) {
        	file_path  = rs.getString(1);
        	files.add(file_path);
        }
        return files;
    }
    
    public static void updateProcessedFilesSetFinished(Connection connection, Integer user_id, String file_path) throws SQLException {
    	CallableStatement sp_call = connection.prepareCall("{call update_processed_files_set_finished(?, ?)}");
    	sp_call.setInt(1, user_id);
    	sp_call.setString(2, file_path);
    	sp_call.execute();
    }
    
    public static void deleteDocumentTextsAtPath(Connection connection, Integer user_id, String file_path) throws SQLException {
    	CallableStatement sp_call = connection.prepareCall("{call delete_document_texts_at_path(?, ?)}");
    	sp_call.setInt(1, user_id);
    	sp_call.setString(2, file_path);
    	sp_call.execute();
    }
    
    public static void deleteCategoryTextsAtPath(Connection connection, Integer user_id, String file_path) throws SQLException {
    	CallableStatement sp_call = connection.prepareCall("{call delete_category_texts_at_path(?, ?)}");
    	sp_call.setInt(1, user_id);
    	sp_call.setString(2, file_path);
    	sp_call.execute();
    }
    
    //job stored procedures
    
    public static Integer insertJobStatus(Connection connection, Integer user_id, String job_status_label) throws SQLException {
    	CallableStatement sp_call = connection.prepareCall("{call insert_job_status(?, ?)}");
    	sp_call.setInt(1, user_id);
    	sp_call.setString(2, job_status_label);
    	sp_call.execute();
    	
    	ResultSet rs = sp_call.getResultSet();
    	Integer ret = null;
    	while (rs.next()) {
    		ret = rs.getInt(1);
        }
        return ret;
    }
    
    public static void insertJobStatusNext(Connection connection, Integer user_id, Integer job_status_id, Integer job_status_next_id) throws SQLException {
    	//insert_job_status_next
    	CallableStatement sp_call = connection.prepareCall("{call insert_job_status_next(?, ?, ?)}");
    	sp_call.setInt(1, user_id);
    	sp_call.setInt(2, job_status_id);
    	sp_call.setInt(3, job_status_next_id);
    	sp_call.execute();
    }
    
    public static ArrayList<JobStatus> selectJobStausList(Connection connection, Integer user_id) throws SQLException {
    	//
    	CallableStatement sp_call = connection.prepareCall("{call select_job_status_list(?)}");
    	sp_call.setInt(1, user_id);
    	sp_call.execute();
    	
    	ResultSet rs = sp_call.getResultSet();
    	ArrayList<JobStatus> ret = new ArrayList<JobStatus>();
    	while (rs.next()) {
    		Integer job_status_id = rs.getInt(1);
    		String job_status_label = rs.getString(2);
    		Integer next_status_id = rs.getInt(3);
    		if (rs.wasNull()) {
    			next_status_id = null;
    		}
        	ret.add(new JobStatus(job_status_id, job_status_label, next_status_id));
        }
        return ret; 
    }
    
    public static Integer insertNewJob(Connection connection, Integer user_id, Integer in_job_type_id, Integer in_job_status_not_started_id) throws SQLException {
    	//
    	CallableStatement sp_call = connection.prepareCall("{call insert_new_job(?, ?, ?)}");
    	sp_call.setInt(1, user_id);
    	sp_call.setInt(2,  in_job_type_id);
    	sp_call.setInt(3,  in_job_status_not_started_id);
    	sp_call.execute();
    	
    	ResultSet rs = sp_call.getResultSet();
    	Integer job_queue_id = null;
    	while (rs.next()) {
    		job_queue_id = rs.getInt(1);
    	}
    	return job_queue_id;
    }
    
    public static void selectJobFromId(Connection connection, Integer user_id, Integer job_queue_id, JobQueue job_queue) throws SQLException {
    	//
    	CallableStatement sp_call = connection.prepareCall("{call select_job_from_id(?, ?)}");
    	sp_call.setInt(1, user_id);
    	sp_call.setInt(2,  job_queue_id);
    	sp_call.execute();
    	
    	ResultSet rs = sp_call.getResultSet();
    	while (rs.next()) {
    		Integer new_job_queue_id = rs.getInt(1);
    		Integer new_user_id = rs.getInt(2);
    		Integer job_type_id = rs.getInt(3);
    		String job_label = rs.getString(4);
    		Integer job_status_id = rs.getInt(5);
    		String job_staus_label = rs.getString(6);
    		Integer next_status_id = rs.getInt(7);
    		
    		job_queue.setJobQueueId(new_job_queue_id);
    		job_queue.setUserId(new_user_id);
    		job_queue.setJobTypeId(job_type_id);
    		job_queue.setJobLabel(job_label);
    		job_queue.setJobStatusId(job_status_id);
    		job_queue.setJobStatusLabel(job_staus_label);
    		job_queue.setNextStatusId(next_status_id);
        }
    }
    
    public static JobQueue selectJobFromId(Connection connection, Integer user_id, Integer job_queue_id) throws SQLException {
    	JobQueue job_queue = new JobQueue();
    	selectJobFromId(connection, user_id, job_queue_id, job_queue);
    	return job_queue;
    }
    
    public static void deleteJobStatus(Connection connection, Integer user_id) throws SQLException {
    	//
    	CallableStatement sp_call = connection.prepareCall("{call delete_job_status(?)}");
    	sp_call.setInt(1, user_id);
    	sp_call.execute(); 
    }
    

    public static Integer insertJobTypes(Connection connection, Integer user_id, String job_type_label) throws SQLException {
    	CallableStatement sp_call = connection.prepareCall("{call insert_job_type(?, ?)}");
    	sp_call.setInt(1, user_id);
    	sp_call.setString(2, job_type_label);
    	sp_call.execute();
    	
    	ResultSet rs = sp_call.getResultSet();
    	Integer ret = null;
    	while (rs.next()) {
    		ret = rs.getInt(1);
        }
        return ret;
    }
    
    public static ArrayList<JobType> selectJobTypesList(Connection connection, Integer user_id) throws SQLException {
    	//
    	CallableStatement sp_call = connection.prepareCall("{call select_job_types_list(?)}");
    	sp_call.setInt(1, user_id);
    	sp_call.execute();
    	
    	ResultSet rs = sp_call.getResultSet();
    	ArrayList<JobType> ret = new ArrayList<JobType>();
    	while (rs.next()) {
    		Integer job_status_id = rs.getInt(1);
    		String job_status_label = rs.getString(2);
        	ret.add(new JobType(job_status_id, job_status_label));
        }
        return ret; 
    }
    
    public static void deleteJobTypes(Connection connection, Integer user_id) throws SQLException {
    	//
    	CallableStatement sp_call = connection.prepareCall("{call delete_job_types(?)}");
    	sp_call.setInt(1, user_id);
    	sp_call.execute(); 
    }
    
    /**
     * @param connection: the SQL connection
     * @param user_id: the logging user_id
     * @param in_job_queue_id: the job id to add things to
     * @param in_param_name: the name of the parameter, job specific
     * @param in_param_value: the value of the parameter, job specific, is nullable but should only be null with a valid large object oid (in_param_object_oid)
     * @param in_param_object_oid: the value of the parameter object (Large Object OID), job specific, is nullable but should only be null with a valid parameter (in_param_value)
     * @return
     * @throws SQLException
     */
    public static Integer insertJobParameter(Connection connection, Integer user_id, Integer job_queue_id, 
    		String param_name, String param_value, Long  param_object_oid) throws SQLException {
    	CallableStatement sp_call = connection.prepareCall("{call insert_job_param(?, ?, ?, ?, ?)}");
    	sp_call.setInt(1, user_id);
    	sp_call.setInt(2, job_queue_id);
    	sp_call.setString(3, param_name);
    	if (param_value == null) {
    		sp_call.setNull(4, java.sql.Types.VARCHAR);
    	} else {
    		sp_call.setString(4, param_value);
    	}
    	
    	if (param_object_oid == null) { 
    		sp_call.setNull(5,  java.sql.Types.BIGINT);
    	} else {
    		sp_call.setLong(5,  param_object_oid);
    	}
    	sp_call.execute();
    	
    	ResultSet rs = sp_call.getResultSet();
    	Integer ret = null;
    	while (rs.next()) {
    		ret = rs.getInt(1);
        }
        return ret;  
    }

    
    public static ArrayList<JobParameter> selectJobParametersFromJobId(Connection connection, Integer user_id, Integer in_job_queue_id) throws SQLException {
    	CallableStatement sp_call = connection.prepareCall("{call select_job_params_from_job_id(?, ?)}");
    	sp_call.setInt(1, user_id);
    	sp_call.setInt(2, in_job_queue_id);
    	sp_call.execute();
    	
    	ResultSet rs = sp_call.getResultSet();
    	ArrayList<JobParameter> ret = new ArrayList<JobParameter>();
    	while (rs.next()) {
    		Integer job_queue_id = rs.getInt(1);
    		String param_name = rs.getString(2);
    		String param_value = rs.getString(3);
    		if (rs.wasNull()) {
    			param_value = null;
    		}
    		Long param_object_oid = rs.getLong(4);
    		if (rs.wasNull()) {
    			param_object_oid = null;
    		}
    		JobParameter param = new JobParameter(job_queue_id, param_name, param_value, param_object_oid);
    		ret.add(param);
        }
        return ret;  
    }
    
    public static void deleteJobParamsForJobId(Connection connection, Integer user_id, Integer in_job_queue_id) throws SQLException {
    	CallableStatement sp_call = connection.prepareCall("{call delete_job_params_for_job_id(?, ?)}");
    	sp_call.setInt(1, user_id);
    	sp_call.setInt(2, in_job_queue_id);
    	sp_call.execute();
    }
    
    public static Integer getNextJob(Connection connection, int user_id, int not_started_status_id) throws SQLException {
    	CallableStatement sp_call = connection.prepareCall("{call select_next_job(?, ?)}");
    	sp_call.setInt(1, user_id);
    	sp_call.setInt(2, not_started_status_id);
    	sp_call.execute();
    	
    	
    	ResultSet rs = sp_call.getResultSet();
    	Integer job_queue_id = null;
    	while (rs.next()) {
    		job_queue_id = rs.getInt(1);
        }
        return job_queue_id;  
    }  		
    
    public static Integer getNextJob(Connection connection, int user_id, int not_started_status_id, int job_type_id) throws SQLException {
    	CallableStatement sp_call = connection.prepareCall("{call select_next_job(?, ?, ?)}");
    	sp_call.setInt(1, user_id);
    	sp_call.setInt(2, not_started_status_id);
    	sp_call.setInt(3, job_type_id);
    	sp_call.execute();
    	
    	
    	ResultSet rs = sp_call.getResultSet();
    	Integer job_queue_id = null;
    	while (rs.next()) {
    		job_queue_id = rs.getInt(1);
        }
        return job_queue_id;  
    }  	
    
    public static Integer incrementNextJobStatus(Connection connection, int user_id, int job_queue_id) throws SQLException {
    	CallableStatement sp_call = connection.prepareCall("{call increment_next_job_status(?, ?)}");
    	sp_call.setInt(1, user_id);
    	sp_call.setInt(2, job_queue_id);
    	sp_call.execute();
    	
    	
    	ResultSet rs = sp_call.getResultSet();
    	Integer job_status_id = null;
    	while (rs.next()) {
    		job_status_id = rs.getInt(1);
        }
        return job_status_id;  
    }
    
    public static Integer deleteJobFromQueue(Connection connection, int user_id, int job_queue_id, int job_status_finished_id) throws SQLException {
    	CallableStatement sp_call = connection.prepareCall("{call delete_job_from_queue(?, ?, ?)}");
    	sp_call.setInt(1, user_id);
    	sp_call.setInt(2, job_queue_id);
    	sp_call.setInt(3, job_status_finished_id);
    	sp_call.execute();
    	
    	ResultSet rs = sp_call.getResultSet();
    	Integer job_status_id = null;
    	while (rs.next()) {
    		job_status_id = rs.getInt(1);
        }
        return job_status_id;  
    }
    
    //project functions
    
    public static void cleanProjects(Connection connection, int user_id) throws SQLException {
    	CallableStatement sp_call = connection.prepareCall("{call clean_projects(?)}");
    	sp_call.setInt(1, user_id);
    	sp_call.execute();    	
    }
    
    public static Integer insertProject(Connection connection, String name, int user_id, int owner_user_id, int ready_for_review_id, int checkout_timeout_seconds) throws SQLException {
    	CallableStatement sp_call = connection.prepareCall("{call insert_project(?, ?, ?, ?, ?)}");
    	sp_call.setString(1,  name);
    	sp_call.setInt(2, user_id);
    	sp_call.setInt(3,  owner_user_id);
    	sp_call.setInt(4, ready_for_review_id);
    	sp_call.setInt(5, checkout_timeout_seconds);
    	sp_call.execute();
    	
    	ResultSet rs = sp_call.getResultSet();
    	Integer project_id = null;
    	while (rs.next()) {
    		project_id = rs.getInt(1);
        }
        return project_id;  
    }
    
    //document status functions
    public static void cleanDocumetClassificationStatus(Connection connection, int user_id) throws SQLException {
    	CallableStatement sp_call = connection.prepareCall("{call clean_document_classification_status(?)}");
    	sp_call.setInt(1, user_id);
    	sp_call.execute();
    }
    
    public static Integer insertMultiuserDocumentClassificationStatusLabel(Connection connection, int user_id, String status_name, Boolean is_ready_for_review, Boolean is_initialize) throws SQLException {
    	CallableStatement sp_call = connection.prepareCall("{call insert_multiuser_document_classification_status_labels(?, ?, ?, ?)}");
    	sp_call.setInt(1, user_id);
    	sp_call.setString(2, status_name);
    	sp_call.setBoolean(3,  is_ready_for_review);
    	sp_call.setBoolean(4, is_initialize);
    	sp_call.execute();
    	
    	ResultSet rs = sp_call.getResultSet();
    	Integer status_id = null;
    	while (rs.next()) {
    		status_id = rs.getInt(1);
        }
        return status_id;  
    }
    
    public static void insertMultiuserDocumentClassificationStatusNext(Connection connection, int user_id, int multiuser_document_classification_status_id, int next_id) throws SQLException {
    	CallableStatement sp_call = connection.prepareCall("{call insert_multiuser_document_classification_status_next(?, ?, ?)}");
    	sp_call.setInt(1, user_id);
    	sp_call.setInt(2, multiuser_document_classification_status_id);
    	sp_call.setInt(3, next_id);
    	sp_call.execute();
    }
    
    public static Integer selectDocumentClassificationStatus(Connection connection, int user_id, int document_classification_status_id, int next_id) throws SQLException {
    	CallableStatement sp_call = connection.prepareCall("{call select_document_classification_status(?)}");
    	sp_call.setInt(1, user_id);
    	sp_call.execute();
    	
    	ResultSet rs = sp_call.getResultSet();
    	Integer status_id = null;
    	while (rs.next()) {
    		status_id = rs.getInt(1);
        }
        return status_id;  
    }
    
    public static MultiuserResponse MultiuserCheckoutDocument(Connection connection, int user_id) throws SQLException {
    	CallableStatement sp_call = connection.prepareCall("{call multiuser_checkout_document(?)}");
    	sp_call.setInt(1, user_id);
    	sp_call.execute();
    	
    	ResultSet rs = sp_call.getResultSet();
    	Integer checkout_document_id = null;
    	Integer document_id = null;
    	MultiuserResponse ret = null;
    	while (rs.next()) {
    		checkout_document_id = rs.getInt(1);
    		document_id = rs.getInt(2);
    		ret = new MultiuserResponse(checkout_document_id, document_id);
        }
        return ret;  
    }
    
    public static MultiuserReviewDocument MultiuserSelectDocumentForReview(Connection connection, int in_user_id) throws SQLException {
    	CallableStatement sp_call = connection.prepareCall("{call multiuser_select_document_for_review(?)}");
    	sp_call.setInt(1, in_user_id);
    	sp_call.execute();
    	
    	ResultSet rs = sp_call.getResultSet();
    	Integer document_id = null;
    	Integer user_id = null;
    	Integer category_id = null;
    	
    	MultiuserReviewDocument ret = null;
    	while (rs.next()) {
    		document_id = rs.getInt(1);
    		user_id = rs.getInt(2);
    		category_id = rs.getInt(3);
    		ret = new MultiuserReviewDocument(document_id, user_id, category_id);
        }
        return ret;  
    }
    
    public static void MultiuserSetCategoryAdmin(Connection connection, int in_user_id, int in_document_id, int in_category_id) throws SQLException {
    	CallableStatement sp_call = connection.prepareCall("{call multiuser_set_category_admin(?, ?, ?)}");
    	sp_call.setInt(1, in_user_id);
    	sp_call.setInt(2, in_document_id);
    	sp_call.setInt(3, in_category_id);
    	sp_call.execute();
    }
    
    public static void MultiuserSetCategoryUser(Connection connection, int in_user_id, int in_document_id, int in_category_id) throws SQLException {
    	CallableStatement sp_call = connection.prepareCall("{call multiuser_set_category_user(?, ?, ?)}");
    	sp_call.setInt(1, in_user_id);
    	sp_call.setInt(2, in_document_id);
    	sp_call.setInt(3, in_category_id);
    	sp_call.execute();
    }
    
    
    
    
    
    

}
