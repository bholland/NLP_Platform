package helper;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Reader;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.SQLType;
import java.sql.Types;
import java.util.ArrayList;
import java.util.HashMap;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.apache.commons.lang.StringEscapeUtils;
import org.apache.log4j.Logger;

import javassist.bytecode.analysis.Type;
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
    public static Integer insertDocumentText(Connection connection, Integer user_id, String id, String text) throws SQLException {
        CallableStatement insert_statement = connection.prepareCall("{call insert_document_text(?, ?, ?)}");
        insert_statement.setInt(1, user_id);
        insert_statement.setString(2, id);
        insert_statement.setString(3, text);
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
    public static void insertDocumentSentenceMetadata(Connection connection, Integer user_id, Integer sentence_id, Integer position_in_sentence, String token, String pos_tag, String chunk, String stemmed_token) throws SQLException {
        CallableStatement insert_statement = connection.prepareCall("{call insert_document_sentence_metadata(?, ?, ?, ?, ?, ?, ?)}");
        insert_statement.setInt(1, user_id);
        insert_statement.setInt(2, sentence_id);
        insert_statement.setInt(3, position_in_sentence);
        insert_statement.setString(4, token);
        insert_statement.setString(5, pos_tag);
        insert_statement.setString(6, chunk);
        insert_statement.setString(7, stemmed_token);
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
    public static int insertCategoryText(Connection connection, Integer user_id, String id, String text, String category, boolean is_in_category) throws SQLException {
        
        if (category == null) {
            throw new SQLException("All category text must be linked to a category."); 
        }
        
        CallableStatement insert_statement = connection.prepareCall("{call insert_category_text(?, ?, ?)}");
        insert_statement.setInt(1, user_id);
        insert_statement.setString(2, id);
        insert_statement.setString(3, text);
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
    public static void insertCategorySentenceMetadata(Connection connection, Integer user_id, Integer sentence_id, Integer position_in_sentence, String token, String pos_tag, String chunk, String stemmed_token) throws SQLException {
        CallableStatement insert_statement = connection.prepareCall("{call insert_category_sentence_metadata(?, ?, ?, ?, ?, ?, ?)}");
        insert_statement.setInt(1, user_id);
        insert_statement.setInt(2, sentence_id);
        insert_statement.setInt(3, position_in_sentence);
        insert_statement.setString(4, token);
        insert_statement.setString(5, pos_tag);
        insert_statement.setString(6, chunk);
        insert_statement.setString(7, stemmed_token);
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
    
    public static void putDoccatModelFile(Connection connection, Integer user_id, Integer category_id, DoccatModel model) throws SQLException, IOException {
        CallableStatement insert_statement = connection.prepareCall("{call insert_category_model_file(?, ?, ?)}");
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ObjectOutputStream oos = new ObjectOutputStream(baos);
        oos.writeObject(model);
        oos.close();
        ByteArrayInputStream bais = new ByteArrayInputStream(baos.toByteArray());
        
        insert_statement.setInt(1,  user_id);
        insert_statement.setInt(2, category_id);
        //insert_statement.setObject(3, model);
        insert_statement.setBinaryStream(3, bais);
        insert_statement.execute();
    }
    
    public static DoccatModel getDoccatModel(Connection connection, Integer user_id, Integer category_id) throws SQLException, IOException, ClassNotFoundException {
        CallableStatement select_statement = connection.prepareCall("{call select_nlp_model(?, ?)}");
        select_statement.setInt(1, user_id);
        select_statement.setInt(2, category_id);
        select_statement.execute();
        ResultSet rs_select = select_statement.getResultSet();
        byte[] obj = null;
        DoccatModel ret = null;
        while (rs_select.next()) {
            obj = rs_select.getBytes(1);
            ByteArrayInputStream bais = new ByteArrayInputStream(obj);
            ObjectInputStream ois = new ObjectInputStream(bais);
            ret = (DoccatModel)ois.readObject();
        }
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
        
        if (ret == null) {
            throw new SQLException();
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
    
}
