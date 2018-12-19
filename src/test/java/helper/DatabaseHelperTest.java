package helper;

import org.junit.Test;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.factory.Nd4j;
import org.postgresql.util.PSQLException;

import descriptors.PostProcessor_CosDistance;
import junit.framework.TestCase;
import opennlp.tools.doccat.DoccatModel;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;

public class DatabaseHelperTest {
    private static String type;
    private static String database_server; 
    private static String port;
    private static String database;
    private static String database_connection;
    private static String user_name;
    private static String password;
    
    @Before
    public void setUp() throws SQLException {
        type = "pgsql";
        database_server = "localhost";
        port = "5432";
        database = "db_helper_testing";
        user_name = "ben";
        password = "password";
        
        database_connection = String.format("jdbc:postgresql://localhost:5432/%s", database);
        
        Connection c;
        
        c = DriverManager.getConnection("jdbc:postgresql://localhost:5432/", user_name, password);
        Statement s = c.createStatement();
        
        String terminate = String.format("SELECT pg_terminate_backend(pid)" + 
                "            FROM pg_stat_activity " + 
                "            WHERE datname = '%s';", database);
        s.executeQuery(terminate);
        s = c.createStatement();
        s.executeUpdate(String.format("DROP DATABASE IF EXISTS %s", database));
        
        String statement_str = String.format("CREATE DATABASE %s" + 
                "    WITH " + 
                "    OWNER = ben " + 
                "    TEMPLATE = nlp_template " +
                "    ENCODING = 'UTF8'" + 
                "    LC_COLLATE = 'en_US.UTF-8'" + 
                "    LC_CTYPE = 'en_US.UTF-8'" + 
                "    TABLESPACE = pg_default" + 
                "    CONNECTION LIMIT = -1;", database);
        s.executeUpdate(statement_str);
        c.close();
        
        c = DriverManager.getConnection(database_connection, user_name, password);
        s = c.createStatement();
        String insert_str = String.format("insert into users" +
        		"	(id, username, email, first_name, last_name)" +
        		"	values" +
        		"	(14, 'nlp_stack', 'admin@nlp_stack.com', null, null);");
        
        s.execute(insert_str);
        c.close();
    }
    
    @After
    public void tearDown() throws SQLException {
        try {
            Connection c = DriverManager.getConnection("jdbc:postgresql://localhost:5432/", user_name, password);
            Statement s = c.createStatement();
            s.executeUpdate(String.format("DROP DATABASE %s", database));
            c.close();
        } catch (SQLException e) {
            Connection c = DriverManager.getConnection("jdbc:postgresql://localhost:5432/", user_name, password);
            Statement s = c.createStatement();
            String terminate = String.format("SELECT pg_terminate_backend(pid)" + 
                    "            FROM pg_stat_activity " + 
                    "            WHERE datname = '%s';", database);
            s.executeQuery(terminate);
            
            s.executeUpdate(String.format("DROP DATABASE %s", database));
            c.close();
        }
    }
    
    @Test 
    public void connectionNlp() throws SQLException{
        Connection connection = DriverManager.getConnection(database_connection, "ben", "password");
        connection.close();
    }
    
    @Test
    public void testDatabasePassAll() throws SQLException, ClassNotFoundException, IOException {
        PostProcessor_CosDistance annotator = new PostProcessor_CosDistance();
        annotator.getDatabaseConnector(type, database_server, 
                port, database, user_name, password);
        assertEquals(annotator.getType(), type);
        assertEquals(annotator.getPort(), port);
        
    }
    
    public void testGetUsername() throws SQLException, ClassNotFoundException {
    	Connection connection = DriverManager.getConnection(database_connection, "ben", "password");
    	Integer user_id = DatabaseHelper.getLoggingUserNameId(connection, "nlp_stack");
        assertEquals((long)user_id, 14);
        connection.close();
    }
    
    private void verifyOneCorrection(Connection connection) throws SQLException {
        Statement s = connection.createStatement();
        ResultSet rs = s.executeQuery(String.format("SELECT id, orginal_word, recomended_correction, num FROM spelling_corrections"));
        
        boolean has_next = false;
        while (rs.next()) {
            has_next = true;
            Integer id = rs.getInt(1);
            String orginal_word = rs.getString(2);
            String recomended_word = rs.getString(3);
            Integer num = rs.getInt(4);
            assertTrue("id > 0", id > 0);
            assertEquals("orginal_word correct", orginal_word, "tst");
            assertEquals("recomended_word correct", recomended_word, "test");
            assertEquals("count equals 1", (long)num, (long)1);
        }
        assertTrue("has result", has_next);
    }
    
    private void verifyZeroCorrection(Connection connection) throws SQLException {
        Statement s = connection.createStatement();
        ResultSet rs = s.executeQuery(String.format("SELECT id, orginal_word, recomended_correction, num FROM spelling_corrections"));
        
        boolean has_next = false;
        while (rs.next()) {
            has_next = true;
        }
        assertTrue("has no result", !has_next);
    }
    
    @Test
    public void testInsertSpellingCorrection() throws SQLException {
        try (Connection connection = DriverManager.getConnection(database_connection, "ben", "password")) {
            DatabaseHelper.insertSpellingCorrection(connection, 14, "tst", "test");
            verifyOneCorrection(connection);
        }
    }
    
    @Test
    public void testCleanSpellingCorrections() throws SQLException {
        try (Connection connection = DriverManager.getConnection(database_connection, "ben", "password")) {
            DatabaseHelper.insertSpellingCorrection(connection, 14, "tst", "test");
            verifyOneCorrection(connection);
            DatabaseHelper.cleanSpellingCorrections(connection, 14);
            verifyZeroCorrection(connection);
        }
    }
    
    private void verifyOneCategory(Connection connection) throws SQLException {
        Statement s = connection.createStatement();
        ResultSet rs = s.executeQuery(String.format("SELECT id, creation_date, category, category_model_file FROM categories"));
        
        boolean has_next = false;
        while (rs.next()) {
            has_next = true;
            Integer id = rs.getInt(1);
            Timestamp creation_date = rs.getTimestamp(2);
            String category = rs.getString(3);
            byte[] category_model_file = rs.getBytes(4);
            
            assertTrue("id > 0", id > 0);
            assertTrue("creation_date not null", creation_date != null);
            assertEquals("category correct", category, "category_1");
            assertTrue("no model", category_model_file == null);
        }
        assertTrue("has result", has_next);
    }
    
    @Test
    public void testInsertCategoryLowercase() throws SQLException {
        try (Connection connection = DriverManager.getConnection(database_connection, "ben", "password")) {
            DatabaseHelper.insertCategory(connection, 14, "category_1");
            verifyOneCategory(connection);
        }
    }
    
    @Test
    public void testInsertCategoryUpper() throws SQLException {
        try (Connection connection = DriverManager.getConnection(database_connection, "ben", "password")) {
            DatabaseHelper.insertCategory(connection, 14, "CATEGORY_1"); //should always lowercase categories. 
            verifyOneCategory(connection);
        }
    }
    
    @Test
    public void testGetCategoryId() throws SQLException {
        try (Connection connection = DriverManager.getConnection(database_connection, "ben", "password")) {
            DatabaseHelper.insertCategory(connection, 14, "category_1");
            verifyOneCategory(connection);
            Integer id = DatabaseHelper.getCategoryId(connection, 14, "category_1");
            assertEquals("correct category id", (long)id, 1);
        }
    }
    
    
    private void verifyDocumentText(Connection connection, String label, String text) throws SQLException {
        Statement s = connection.createStatement();
        ResultSet rs = s.executeQuery(String.format("SELECT id, orginal_document_id, orginal_document_text, processed_text, processed, num_tokens FROM document_text"));
        boolean has_next = false;
        while (rs.next()) {
            has_next = true;
            Integer id = rs.getInt(1);
            String document_text_id = rs.getString(2);
            String document_text = rs.getString(3);
            String processed_text = rs.getString(4);
            Boolean processed = rs.getBoolean(5);
            Integer num_tokens = rs.getInt(6);
            
            assertTrue("id > 0", id > 0);
            assertEquals("document_text_id matches label", document_text_id, label);
            assertEquals("document_text equals insert.", document_text, text);
            assertTrue("processed_text is null", processed_text == null);
            assertTrue("not processed", !processed);
            assertTrue("num_tokens null", num_tokens == 0);
            
        }
        assertTrue("has result", has_next);
    }
    
    @Test
    public void testInsertDocumentText() throws SQLException {
        try (Connection connection = DriverManager.getConnection(database_connection, "ben", "password")) {
            Integer text_id = DatabaseHelper.insertDocumentText(connection, 14, "label1", "This is some document text.");
            verifyDocumentText(connection, "label1", "This is some document text.");
            assertEquals("text_id is 1", (long)text_id, 1);
        }
    }
    
    @Test
    public void testInsertDocumentTextNullLabel() throws SQLException {
        try (Connection connection = DriverManager.getConnection(database_connection, "ben", "password")) {
            Integer text_id = DatabaseHelper.insertDocumentText(connection, 14, null, "This is some document text.");
            verifyDocumentText(connection, null, "This is some document text.");
            assertEquals("text_id is 1", (long)text_id, 1);
        }
    }
    
    @Test(expected = SQLException.class)
    public void testInsertDocumentTextNullText() throws SQLException  {
        try (Connection connection = DriverManager.getConnection(database_connection, "ben", "password")) {
            Integer text_id = DatabaseHelper.insertDocumentText(connection, 14, null, null);
        }
    }
    
    private void verifyDocumentTextSentence(Connection connection, String sentence_text) throws SQLException {
        Statement s = connection.createStatement();
        ResultSet rs = s.executeQuery(String.format("SELECT id, document_text_id, sentence, sentence_number FROM document_sentences"));
        
        boolean has_next = false;
        while (rs.next()) {
            has_next = true;
            Integer id = rs.getInt(1);
            Integer text_id = rs.getInt(2);
            String sentence = rs.getString(3);
            Integer sentence_number = rs.getInt(4);
            
            assertTrue("id > 0", id > 0);
            assertEquals("text_id == 1", (long)text_id, 1);
            assertEquals("document_text equals insert.", sentence, sentence_text);
            assertEquals("sentence_number equals 1.", (long)sentence_number, 1);
            
        }
        assertTrue("has result", has_next);
    }
    
    @Test
    public void testInsertSentence() throws SQLException {
        try (Connection connection = DriverManager.getConnection(database_connection, "ben", "password")) {
            Integer text_id = DatabaseHelper.insertDocumentText(connection, 14, null, "This is some document text.");
            verifyDocumentText(connection, null, "This is some document text.");
            assertEquals("text_id is 1", (long)text_id, 1);
            DatabaseHelper.insertDocumentSentence(connection, 14, "This is some document text.", text_id, 1);
            verifyDocumentTextSentence(connection, "This is some document text.");
        }
    }
    
    @Test(expected=SQLException.class)
    public void testInsertNullSentence() throws SQLException {
        try (Connection connection = DriverManager.getConnection(database_connection, "ben", "password")) {
            Integer text_id = DatabaseHelper.insertDocumentText(connection, 14, null, "This is some document text.");
            verifyDocumentText(connection, null, "This is some document text.");
            assertEquals("text_id is 1", (long)text_id, 1);
            DatabaseHelper.insertDocumentSentence(connection, 14, null, text_id, 1);
        }
    }
    
    @Test(expected = SQLException.class)
    public void testInsertSentenceNullTextId() throws SQLException {
        try (Connection connection = DriverManager.getConnection(database_connection, "ben", "password")) {
            Integer text_id = DatabaseHelper.insertDocumentText(connection, 14, null, "This is some document text.");
            verifyDocumentText(connection, null, "This is some document text.");
            assertEquals("text_id is 1", (long)text_id, 1);
            DatabaseHelper.insertDocumentSentence(connection, 14, "This is some document text.", null, 1);
        }
    }
    
    @Test(expected = SQLException.class)
    public void testInsertSentenceNullSentenceNumber() throws SQLException {
        try (Connection connection = DriverManager.getConnection(database_connection, "ben", "password")) {
            Integer text_id = DatabaseHelper.insertDocumentText(connection, 14, null, "This is some document text.");
            verifyDocumentText(connection, null, "This is some document text.");
            assertEquals("text_id is 1", (long)text_id, 1);
            DatabaseHelper.insertDocumentSentence(connection, 14, "This is some document text.", text_id, null);
        }
    }
    
    @Test
    public void testInsertSentenceMetadata() throws SQLException {
        try (Connection connection = DriverManager.getConnection(database_connection, "ben", "password")) {
            Integer text_id = DatabaseHelper.insertDocumentText(connection, 14, null, "This is some document text.");
            verifyDocumentText(connection, null, "This is some document text.");
            assertEquals("text_id is 1", (long)text_id, 1);
            Integer sentence_id = DatabaseHelper.insertDocumentSentence(connection, 14, "This is some document text.", text_id, 1);
            verifyDocumentTextSentence(connection, "This is some document text.");
            DatabaseHelper.insertDocumentSentenceMetadata(connection, 14, sentence_id, 1, "This", "", "", "this");
            DatabaseHelper.insertDocumentSentenceMetadata(connection, 14, sentence_id, 2, "is", "", "", "is");
            DatabaseHelper.insertDocumentSentenceMetadata(connection, 14, sentence_id, 3, "some", "", "", "some");
            DatabaseHelper.insertDocumentSentenceMetadata(connection, 14, sentence_id, 4, "document", "", "", "document");
            DatabaseHelper.insertDocumentSentenceMetadata(connection, 14, sentence_id, 5, "text", "", "", "text");
            DatabaseHelper.insertDocumentSentenceMetadata(connection, 14, sentence_id, 6, ".", "", "", ".");
        }
    }
    
    private void verifyCategoryText(Connection connection, String label, String text) throws SQLException {
        Statement s = connection.createStatement();
        ResultSet rs = s.executeQuery(String.format("SELECT id, category_document_id, category_document_text, processed_text, processed, num_tokens FROM category_text"));
        
        boolean has_next = false;
        while (rs.next()) {
            has_next = true;
            Integer id = rs.getInt(1);
            String Category_text_id = rs.getString(2);
            String Category_text = rs.getString(3);
            String processed_text = rs.getString(4);
            Boolean processed = rs.getBoolean(5);
            Integer num_tokens = rs.getInt(6);
            
            assertTrue("id > 0", id > 0);
            assertEquals("Category_text_id > 0", Category_text_id, label);
            assertEquals("Category_text equals insert.", Category_text, text);
            assertTrue("processed_text is null", processed_text == null);
            assertTrue("not processed", !processed);
            assertTrue("num_tokens null", num_tokens == 0);
            
        }
        assertTrue("has result", has_next);
    }
    
    @Test
    public void testInsertCategoryText() throws SQLException {
        try (Connection connection = DriverManager.getConnection(database_connection, "ben", "password")) {
            DatabaseHelper.insertCategory(connection, 14, "cat_1");
            Integer text_id = DatabaseHelper.insertCategoryText(connection, 14, "label1", "This is some Category text.", "cat_1", true);
            verifyCategoryText(connection, "label1", "This is some Category text.");
            assertEquals("text_id is 1", (long)text_id, 1);
        }
    }
    
    @Test
    public void testInsertCategoryTextNullLabel() throws SQLException {
        try (Connection connection = DriverManager.getConnection(database_connection, "ben", "password")) {
            DatabaseHelper.insertCategory(connection, 14, "cat_1");
            Integer text_id = DatabaseHelper.insertCategoryText(connection, 14, null, "This is some Category text.", "cat_1", true);
            verifyCategoryText(connection, null, "This is some Category text.");
            assertEquals("text_id is 1", (long)text_id, 1);
        }
    }
    
    @Test(expected = SQLException.class)
    public void testInsertCategoryTextNullText() throws SQLException  {
        try (Connection connection = DriverManager.getConnection(database_connection, "ben", "password")) {
            DatabaseHelper.insertCategory(connection, 14, "cat_1");
            Integer text_id = DatabaseHelper.insertCategoryText(connection, 14, "label1", null, "cat_1", true);
        }
    }
    
    @Test(expected = SQLException.class)
    public void testInsertCategoryTextNullCategory() throws SQLException  {
        try (Connection connection = DriverManager.getConnection(database_connection, "ben", "password")) {
            Integer text_id = DatabaseHelper.insertCategoryText(connection, 14, "label1", "This is some Category text.", null, true);
        }
    }
    
    private void verifyCategoryTextSentence(Connection connection, String sentence_text) throws SQLException {
        Statement s = connection.createStatement();
        ResultSet rs = s.executeQuery(String.format("SELECT id, category_text_id, sentence, sentence_number FROM category_sentences"));
        
        boolean has_next = false;
        while (rs.next()) {
            has_next = true;
            Integer id = rs.getInt(1);
            Integer text_id = rs.getInt(2);
            String sentence = rs.getString(3);
            Integer sentence_number = rs.getInt(4);
            
            assertTrue("id > 0", id > 0);
            assertEquals("text_id == 1", (long)text_id, 1);
            assertEquals("Category_text equals insert.", sentence, sentence_text);
            assertEquals("sentence_number equals 1.", (long)sentence_number, 1);
            
        }
        assertTrue("has result", has_next);
    }
    
    @Test
    public void testInsertCategorySentence() throws SQLException {
        try (Connection connection = DriverManager.getConnection(database_connection, "ben", "password")) {
            DatabaseHelper.insertCategory(connection, 14, "cat_1");
            Integer text_id = DatabaseHelper.insertCategoryText(connection, 14, null, "This is some Category text.", "cat_1", true);
            verifyCategoryText(connection, null, "This is some Category text.");
            assertEquals("text_id is 1", (long)text_id, 1);
            DatabaseHelper.insertCategorySentence(connection, 14, "This is some Category text.", text_id, 1);
            verifyCategoryTextSentence(connection, "This is some Category text.");
        }
    }
    
    @Test(expected = SQLException.class)
    public void testInsertCategoryNullSentence() throws SQLException {
        try (Connection connection = DriverManager.getConnection(database_connection, "ben", "password")) {
            Integer text_id = DatabaseHelper.insertCategoryText(connection, 14, null, "This is some Category text.", "cat_1", true);
            verifyCategoryText(connection, "label1", "This is some Category text.");
            assertEquals("text_id is 1", (long)text_id, 1);
            DatabaseHelper.insertCategorySentence(connection, 14, null, text_id, 1);
        }
    }
    
    @Test(expected = SQLException.class)
    public void testInsertCategorySentenceNullTextId() throws SQLException {
        try (Connection connection = DriverManager.getConnection(database_connection, "ben", "password")) {
            Integer text_id = DatabaseHelper.insertCategoryText(connection, 14, null, "This is some Category text.", "cat_1", true);
            verifyCategoryText(connection, "label1", "This is some Category text.");
            assertEquals("text_id is 1", (long)text_id, 1);
            DatabaseHelper.insertCategorySentence(connection, 14, "This is some Category text.", null, 1);
        }
    }
    
    @Test(expected = SQLException.class)
    public void testInsertCategorySentenceNullSentenceNumber() throws SQLException {
        try (Connection connection = DriverManager.getConnection(database_connection, "ben", "password")) {
            Integer text_id = DatabaseHelper.insertCategoryText(connection, 14, null, "This is some Category text.", "cat_1", true);
            verifyCategoryText(connection, "label1", "This is some Category text.");
            assertEquals("text_id is 1", (long)text_id, 1);
            DatabaseHelper.insertCategorySentence(connection, 14, "This is some Category text.", text_id, null);
        }
    }
    
    @Test
    public void testInsertCategorySentenceMetadata() throws SQLException {
        try (Connection connection = DriverManager.getConnection(database_connection, "ben", "password")) {
            DatabaseHelper.insertCategory(connection, 14, "cat_1");
            Integer text_id = DatabaseHelper.insertCategoryText(connection, 14, null, "This is some Category text.", "cat_1", true);
            verifyCategoryText(connection, null, "This is some Category text.");
            assertEquals("text_id is 1", (long)text_id, 1);
            Integer sentence_id = DatabaseHelper.insertCategorySentence(connection, 14, "This is some Category text.", text_id, 1);
            verifyCategoryTextSentence(connection, "This is some Category text.");
            DatabaseHelper.insertCategorySentenceMetadata(connection, 14, sentence_id, 1, "This", "", "", "this");
            DatabaseHelper.insertCategorySentenceMetadata(connection, 14, sentence_id, 2, "is", "", "", "is");
            DatabaseHelper.insertCategorySentenceMetadata(connection, 14, sentence_id, 3, "some", "", "", "some");
            DatabaseHelper.insertCategorySentenceMetadata(connection, 14, sentence_id, 4, "Category", "", "", "Category");
            DatabaseHelper.insertCategorySentenceMetadata(connection, 14, sentence_id, 5, "text", "", "", "text");
            DatabaseHelper.insertCategorySentenceMetadata(connection, 14, sentence_id, 6, ".", "", "", ".");
        }
    }
    
    @Test
    public void testGetDocumentTokensMatchingCaps() throws SQLException {
        try (Connection connection = DriverManager.getConnection(database_connection, "ben", "password")) {
            Integer text_id = DatabaseHelper.insertDocumentText(connection, 14, null, "This is some document text.");
            assertEquals("text_id is 1", (long)text_id, 1);
            Integer sentence_id = DatabaseHelper.insertDocumentSentence(connection, 14, "This is some document text.", text_id, 1);
            verifyDocumentTextSentence(connection, "This is some document text.");
            DatabaseHelper.insertDocumentSentenceMetadata(connection, 14, sentence_id, 1, "This", "", "", "this");
            DatabaseHelper.insertDocumentSentenceMetadata(connection, 14, sentence_id, 2, "is", "", "", "is");
            DatabaseHelper.insertDocumentSentenceMetadata(connection, 14, sentence_id, 3, "some", "", "", "some");
            DatabaseHelper.insertDocumentSentenceMetadata(connection, 14, sentence_id, 4, "document", "", "", "document");
            DatabaseHelper.insertDocumentSentenceMetadata(connection, 14, sentence_id, 5, "text", "", "", "text");
            DatabaseHelper.insertDocumentSentenceMetadata(connection, 14, sentence_id, 6, ".", "", "", ".");
            
            HashMap<Integer, ArrayList<String>> token_hash = DatabaseHelper.getDocumentTokens(connection, 14);
            
            assertTrue("hash has text_id 1", token_hash.containsKey(1));
            ArrayList<String> tokens = token_hash.get(1);
            assertTrue("tokens is not null", tokens != null);
            assertTrue("tokens length is 6", tokens.size() >= 5); //the period might get stripped out. 
            assertEquals("token 1 correct", tokens.get(0), "This");
            assertEquals("token 2 correct", tokens.get(1), "is");
            assertEquals("token 3 correct", tokens.get(2), "some");
            assertEquals("token 4 correct", tokens.get(3), "document");
            assertEquals("token 5 correct", tokens.get(4), "text");
        }
    }
    
    @Test
    public void testGetDocumentTokensNotMatchingCaps() throws SQLException {
        try (Connection connection = DriverManager.getConnection(database_connection, "ben", "password")) {
            Integer text_id = DatabaseHelper.insertDocumentText(connection, 14, null, "This is some document text.");
            assertEquals("text_id is 1", (long)text_id, 1);
            Integer sentence_id = DatabaseHelper.insertDocumentSentence(connection, 14, "This is some document text.", text_id, 1);
            verifyDocumentTextSentence(connection, "This is some document text.");
            DatabaseHelper.insertDocumentSentenceMetadata(connection, 14, sentence_id, 1, "This", "", "", "this");
            DatabaseHelper.insertDocumentSentenceMetadata(connection, 14, sentence_id, 2, "is", "", "", "is");
            DatabaseHelper.insertDocumentSentenceMetadata(connection, 14, sentence_id, 3, "some", "", "", "some");
            DatabaseHelper.insertDocumentSentenceMetadata(connection, 14, sentence_id, 4, "document", "", "", "document");
            DatabaseHelper.insertDocumentSentenceMetadata(connection, 14, sentence_id, 5, "text", "", "", "text");
            DatabaseHelper.insertDocumentSentenceMetadata(connection, 14, sentence_id, 6, ".", "", "", ".");
            
            HashMap<Integer, ArrayList<String>> token_hash = DatabaseHelper.getDocumentTokens(connection, 14);
            
            assertTrue("hash has text_id 1", token_hash.containsKey(1));
            ArrayList<String> tokens = token_hash.get(1);
            assertTrue("tokens is not null", tokens != null);
            assertTrue("tokens length is 6", tokens.size() >= 5); //the period might get stripped out. 
            assertNotEquals("token 1 correct", tokens.get(0), "this");
            assertEquals("token 2 correct", tokens.get(1), "is");
            assertEquals("token 3 correct", tokens.get(2), "some");
            assertEquals("token 4 correct", tokens.get(3), "document");
            assertEquals("token 5 correct", tokens.get(4), "text");
        }
    }
    
    /**
     * @TODO: fill this out with a pre-existing category model. 
     * @throws SQLException
     * @throws IOException
     */
    @Test
    public void testPutDoccatModelFile() throws SQLException {
        try (Connection connection = DriverManager.getConnection(database_connection, "ben", "password")) {
            DatabaseHelper.insertCategory(connection, 14, "cat_1");
            Integer cat_id = DatabaseHelper.getCategoryId(connection, 14, "cat_1");
            //DatabaseHelper.putDoccatModelFile(connection, cat_id, new DoccatModel(new FileInputStream(new File("tmp.tmp"))));
        }
    }
    
    /**
     * @TODO: fill this out with pre-existing model
     * @throws SQLException
     * @throws IOException
     */
    @Test 
    public void testGetDoccatModel() throws SQLException {
        try (Connection connection = DriverManager.getConnection(database_connection, "ben", "password")) {
            DatabaseHelper.insertCategory(connection, 14, "cat_1");
            Integer cat_id = DatabaseHelper.getCategoryId(connection, 14, "cat_1");
            //DatabaseHelper.getDoccatModel(connection, cat_id);
        }
    }
    
    private void testBatchNumber(Connection connection, Integer expected) throws SQLException {
        Statement s = connection.createStatement();
        ResultSet rs = s.executeQuery(String.format("select last_value from text_category_batch_number_seq"));
        
        boolean has_next = false;
        Integer batch_number = null;
        while (rs.next()) {
            batch_number = rs.getInt(1);
        }
        assertEquals("Batch number matches expected value", batch_number, expected);
    }
    
    @Test
    public void testIncrementBatchNumber() throws SQLException {
        try (Connection connection = DriverManager.getConnection(database_connection, "ben", "password")) {
            //if it starts at x, calling nextval will result in x.
            //@TODO: Make this work properly within the code base. 
            testBatchNumber(connection, 1);
            DatabaseHelper.incrementBatchNumber(connection, 14);
            testBatchNumber(connection, 1); 
            DatabaseHelper.incrementBatchNumber(connection, 14);
            testBatchNumber(connection, 2);
        }
    }
    
    @Test
    public void testSelectBatchNumber() throws SQLException {
        try (Connection connection = DriverManager.getConnection(database_connection, "ben", "password")) {
            testBatchNumber(connection, 1);
            Integer batch_number = DatabaseHelper.selectBatchNumber(connection, 14);
            testBatchNumber(connection, batch_number);
        }
    }
    
    @Test
    public void testInsertCategoryProbabilities() throws SQLException {
        try (Connection connection = DriverManager.getConnection(database_connection, "ben", "password")) {
            Integer text_id = DatabaseHelper.insertDocumentText(connection, 14, null, "This is some document text.");
            DatabaseHelper.insertCategory(connection, 14, "cat_1");
            Integer cat_id = DatabaseHelper.getCategoryId(connection, 14, "cat_1");
            DatabaseHelper.insertCategoryProbabilities(connection, 14, 1, text_id, cat_id, null, 75.3, null, 100.0);
        }
    }
    
    @Test
    public void testAssignDocumentToCategory() throws SQLException {
        try (Connection connection = DriverManager.getConnection(database_connection, "ben", "password")) {
            Integer text_id = DatabaseHelper.insertDocumentText(connection, 14, null, "This is some document text.");
            DatabaseHelper.insertCategory(connection, 14, "cat_1");
            Integer cat_id = DatabaseHelper.getCategoryId(connection, 14, "cat_1");
            
            DatabaseHelper.assignDocumentToCategory(connection, 14, text_id, cat_id, true);
            verifyCategoryText(connection, null, "This is some document text.");
        }
    }
    
}


