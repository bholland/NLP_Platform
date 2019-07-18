package helper;

import org.junit.Test;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.factory.Nd4j;
import org.postgresql.util.PSQLException;

import de.svenjacobs.loremipsum.LoremIpsum;
import descriptors.PostProcessor_CosDistance;
import junit.framework.TestCase;
import opennlp.tools.doccat.DoccatModel;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import helper.DatabaseHelperSharedFunctions;
import job_queue.Job;

public class DatabaseHelperTest {
    private static String type;
    private static String database_server; 
    private static String port;
    private static String database;
    private static String database_connection;
    private static String user_name;
    private static String password;
    
    //select_document_text_from_id requires unit testing
    
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
    public void testDatabasePassAllDisableQueueTest() throws SQLException, ClassNotFoundException, IOException {
        PostProcessor_CosDistance annotator = new PostProcessor_CosDistance();
        annotator.getDatabaseConnector(type, database_server, 
                port, database, user_name, password, null, Job.JOB_QUEUE_DISABLED);
        assertEquals(annotator.getType(), type);
        assertEquals(annotator.getPort(), port);
    }
    
    @Test
    public void testDatabasePassAllInsertQueueTest() throws SQLException, ClassNotFoundException, IOException {
        PostProcessor_CosDistance annotator = new PostProcessor_CosDistance();
        annotator.getDatabaseConnector(type, database_server, 
                port, database, user_name, password, null, Job.JOB_QUEUE_INSERT);
        assertEquals(annotator.getType(), type);
        assertEquals(annotator.getPort(), port);
    }
    
    @Test
    public void testDatabasePassAllProcessQueueTest() throws SQLException, ClassNotFoundException, IOException {
        PostProcessor_CosDistance annotator = new PostProcessor_CosDistance();
        annotator.getDatabaseConnector(type, database_server, 
                port, database, user_name, password, null, Job.JOB_QUEUE_PROCESS);
        assertEquals(annotator.getType(), type);
        assertEquals(annotator.getPort(), port);
    }
    
    @Test(expected = IOException.class)
    public void testDatabasePassAllFailureTest() throws SQLException, ClassNotFoundException, IOException {
        PostProcessor_CosDistance annotator = new PostProcessor_CosDistance();
        annotator.getDatabaseConnector(type, database_server, 
                port, database, user_name, password, null, 4);
        assertEquals(annotator.getType(), type);
        assertEquals(annotator.getPort(), port);
    }
    
    @Test
    public void testDatabasePassAllDisableQueueLoggingTest() throws SQLException, ClassNotFoundException, IOException {
        PostProcessor_CosDistance annotator = new PostProcessor_CosDistance();
        annotator.getDatabaseConnector(type, database_server, 
                port, database, user_name, password, 14, Job.JOB_QUEUE_DISABLED);
        assertEquals(annotator.getType(), type);
        assertEquals(annotator.getPort(), port);
    }
    
    @Test
    public void testDatabasePassAllInsertQueueLoggingTest() throws SQLException, ClassNotFoundException, IOException {
        PostProcessor_CosDistance annotator = new PostProcessor_CosDistance();
        annotator.getDatabaseConnector(type, database_server, 
                port, database, user_name, password, 14, Job.JOB_QUEUE_INSERT);
        assertEquals(annotator.getType(), type);
        assertEquals(annotator.getPort(), port);
    }
    
    @Test
    public void testDatabasePassAllProcessQueueLoggingTest() throws SQLException, ClassNotFoundException, IOException {
        PostProcessor_CosDistance annotator = new PostProcessor_CosDistance();
        annotator.getDatabaseConnector(type, database_server, 
                port, database, user_name, password, 14, Job.JOB_QUEUE_PROCESS);
        assertEquals(annotator.getType(), type);
        assertEquals(annotator.getPort(), port);
    }
    
    @Test(expected = IOException.class)
    public void testDatabasePassAllFailureLoggingTest() throws SQLException, ClassNotFoundException, IOException {
        PostProcessor_CosDistance annotator = new PostProcessor_CosDistance();
        annotator.getDatabaseConnector(type, database_server, 
                port, database, user_name, password, 14, 4);
        assertEquals(annotator.getType(), type);
        assertEquals(annotator.getPort(), port);
    }
    
    @Test
    public void testGetUsername() throws SQLException, ClassNotFoundException {
    	Connection connection = DriverManager.getConnection(database_connection, "ben", "password");
    	Integer user_id = DatabaseHelper.getLoggingUserNameId(connection, "nlp_stack");
        assertEquals((long)user_id, 14);
        connection.close();
    }
    
    @Test
    public void testGetUsername_Logging() throws SQLException, ClassNotFoundException {
    	Connection connection = DriverManager.getConnection(database_connection, "ben", "password");
    	Integer user_id = DatabaseHelper.getLoggingUserNameId(connection, "nlp_stack");
        assertEquals((long)user_id, 14);
        DatabaseHelperSharedFunctions.TestLogging(connection, 0, null, new String[0]);
        
        
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
    public void testInsertSpellingCorrection_Logging() throws SQLException {
        try (Connection connection = DriverManager.getConnection(database_connection, "ben", "password")) {
            DatabaseHelper.insertSpellingCorrection(connection, 14, "tst", "test");
            verifyOneCorrection(connection);
            String[] tmp = {"insert_spelling_correction"};
            DatabaseHelperSharedFunctions.TestLogging(connection, 1, 14, tmp);
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
    
    @Test
    public void testCleanSpellingCorrections_Logging() throws SQLException {
        try (Connection connection = DriverManager.getConnection(database_connection, "ben", "password")) {
            DatabaseHelper.insertSpellingCorrection(connection, 14, "tst", "test");
            verifyOneCorrection(connection);
            DatabaseHelper.cleanSpellingCorrections(connection, 14);
            verifyZeroCorrection(connection);
            
            String[] tmp = {"insert_spelling_correction", "clean_spelling_corrections"};
            DatabaseHelperSharedFunctions.TestLogging(connection, 2, 14, tmp);
        }
    }
    
    private void verifyOneCategory(Connection connection) throws SQLException {
        Statement s = connection.createStatement();
        ResultSet rs = s.executeQuery(String.format("SELECT id, creation_date, category, category_model_file_oid FROM categories"));
        
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
    public void testInsertCategoryLowercase_Logging() throws SQLException {
        try (Connection connection = DriverManager.getConnection(database_connection, "ben", "password")) {
            DatabaseHelper.insertCategory(connection, 14, "category_1");
            verifyOneCategory(connection);
            String[] tmp = {"select_category", "insert_category"};
            DatabaseHelperSharedFunctions.TestLogging(connection, 2, 14, tmp);
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
            //inserts two rows in the log: 1 for the check and 1 for the insert.
        	DatabaseHelper.insertCategory(connection, 14, "category_1");
            verifyOneCategory(connection);
            Integer id = DatabaseHelper.getCategoryId(connection, 14, "category_1");
            assertEquals("correct category id", (long)id, 1);
            
            String[] tmp = {"insert_category", "select_category"};
            DatabaseHelperSharedFunctions.TestLogging(connection, 3, 14, tmp);
        }
    }
    
    
    
    
    @Test
    public void testInsertDocumentText() throws SQLException {
        try (Connection connection = DriverManager.getConnection(database_connection, "ben", "password")) {
            Integer text_id = DatabaseHelper.insertDocumentText(connection, 14, "label1", "This is some document text.", "file name");
            DatabaseHelperSharedFunctions.verifyDocumentText(connection, "label1", "This is some document text.");
            assertEquals("text_id is 1", (long)text_id, 1);
        }
    }
    
    @Test
    public void testInsertDocumentText_Logging() throws SQLException {
        try (Connection connection = DriverManager.getConnection(database_connection, "ben", "password")) {
            Integer text_id = DatabaseHelper.insertDocumentText(connection, 14, "label1", "This is some document text.", "file name");
            DatabaseHelperSharedFunctions.verifyDocumentText(connection, "label1", "This is some document text.");
            assertEquals("text_id is 1", (long)text_id, 1);
            
            String[] tmp = {"insert_document_text"};
            DatabaseHelperSharedFunctions.TestLogging(connection, 1, 14, tmp);
        }
    }
    
    @Test
    public void testInsertDocumentTextNullLabel() throws SQLException {
        try (Connection connection = DriverManager.getConnection(database_connection, "ben", "password")) {
            Integer text_id = DatabaseHelper.insertDocumentText(connection, 14, null, "This is some document text.", "file name");
            DatabaseHelperSharedFunctions.verifyDocumentText(connection, null, "This is some document text.");
            assertEquals("text_id is 1", (long)text_id, 1);
        }
    }
    
    @Test(expected = SQLException.class)
    public void testInsertDocumentTextNullText() throws SQLException  {
        try (Connection connection = DriverManager.getConnection(database_connection, "ben", "password")) {
            Integer text_id = DatabaseHelper.insertDocumentText(connection, 14, null, null, "file name");
        }
    }
    
    @Test(expected = SQLException.class)
    public void testInsertDocumentTextNullPath() throws SQLException  {
        try (Connection connection = DriverManager.getConnection(database_connection, "ben", "password")) {
            Integer text_id = DatabaseHelper.insertDocumentText(connection, 14, null, "This is some document text.", null);
        }
    }
    
    @Test
    public void testInsertSentence() throws SQLException {
        try (Connection connection = DriverManager.getConnection(database_connection, "ben", "password")) {
            Integer text_id = DatabaseHelper.insertDocumentText(connection, 14, null, "This is some document text.", "file name");
            DatabaseHelperSharedFunctions.verifyDocumentText(connection, null, "This is some document text.");
            assertEquals("text_id is 1", (long)text_id, 1);
            DatabaseHelper.insertDocumentSentence(connection, 14, "This is some document text.", text_id, 1);
            DatabaseHelperSharedFunctions.verifyDocumentTextSentence(connection, "This is some document text.");
        }
    }
    
    @Test
    public void testInsertSentence_Logging() throws SQLException {
        try (Connection connection = DriverManager.getConnection(database_connection, "ben", "password")) {
            Integer text_id = DatabaseHelper.insertDocumentText(connection, 14, null, "This is some document text.", "file name");
            DatabaseHelperSharedFunctions.verifyDocumentText(connection, null, "This is some document text.");
            assertEquals("text_id is 1", (long)text_id, 1);
            DatabaseHelper.insertDocumentSentence(connection, 14, "This is some document text.", text_id, 1);
            DatabaseHelperSharedFunctions.verifyDocumentTextSentence(connection, "This is some document text.");
            
            String[] tmp = {"insert_document_text", "insert_document_sentence"};
            DatabaseHelperSharedFunctions.TestLogging(connection, 2, 14, tmp);
        }
    }
    
    @Test(expected=SQLException.class)
    public void testInsertNullSentence() throws SQLException {
        try (Connection connection = DriverManager.getConnection(database_connection, "ben", "password")) {
            Integer text_id = DatabaseHelper.insertDocumentText(connection, 14, null, "This is some document text.", "file name");
            DatabaseHelperSharedFunctions.verifyDocumentText(connection, null, "This is some document text.");
            assertEquals("text_id is 1", (long)text_id, 1);
            DatabaseHelper.insertDocumentSentence(connection, 14, null, text_id, 1);
        }
    }
    
    @Test(expected = SQLException.class)
    public void testInsertSentenceNullTextId() throws SQLException {
        try (Connection connection = DriverManager.getConnection(database_connection, "ben", "password")) {
            Integer text_id = DatabaseHelper.insertDocumentText(connection, 14, null, "This is some document text.", "file name");
            DatabaseHelperSharedFunctions.verifyDocumentText(connection, null, "This is some document text.");
            assertEquals("text_id is 1", (long)text_id, 1);
            DatabaseHelper.insertDocumentSentence(connection, 14, "This is some document text.", null, 1);
        }
    }
    
    @Test(expected = SQLException.class)
    public void testInsertSentenceNullSentenceNumber() throws SQLException {
        try (Connection connection = DriverManager.getConnection(database_connection, "ben", "password")) {
            Integer text_id = DatabaseHelper.insertDocumentText(connection, 14, null, "This is some document text.", "file name");
            DatabaseHelperSharedFunctions.verifyDocumentText(connection, null, "This is some document text.");
            assertEquals("text_id is 1", (long)text_id, 1);
            DatabaseHelper.insertDocumentSentence(connection, 14, "This is some document text.", text_id, null);
        }
    }
    
    @Test
    public void testInsertSentenceMetadata() throws SQLException {
        try (Connection connection = DriverManager.getConnection(database_connection, "ben", "password")) {
            Integer text_id = DatabaseHelper.insertDocumentText(connection, 14, null, "This is some document text.", "file name");
            DatabaseHelperSharedFunctions.verifyDocumentText(connection, null, "This is some document text.");
            assertEquals("text_id is 1", (long)text_id, 1);
            Integer sentence_id = DatabaseHelper.insertDocumentSentence(connection, 14, "This is some document text.", text_id, 1);
            DatabaseHelperSharedFunctions.verifyDocumentTextSentence(connection, "This is some document text.");
            DatabaseHelper.insertDocumentSentenceMetadata(connection, 14, sentence_id, 1, "This", "", "", "this", true);
            DatabaseHelper.insertDocumentSentenceMetadata(connection, 14, sentence_id, 2, "is", "", "", "is", true);
            DatabaseHelper.insertDocumentSentenceMetadata(connection, 14, sentence_id, 3, "some", "", "", "some", true);
            DatabaseHelper.insertDocumentSentenceMetadata(connection, 14, sentence_id, 4, "document", "", "", "document", true);
            DatabaseHelper.insertDocumentSentenceMetadata(connection, 14, sentence_id, 5, "text", "", "", "text", true);
            DatabaseHelper.insertDocumentSentenceMetadata(connection, 14, sentence_id, 6, ".", "", "", ".", true);
        }
    }
    
    @Test
    public void testInsertSentenceMetadata_Logging() throws SQLException {
        try (Connection connection = DriverManager.getConnection(database_connection, "ben", "password")) {
            Integer text_id = DatabaseHelper.insertDocumentText(connection, 14, null, "This is some document text.", "file name");
            DatabaseHelperSharedFunctions.verifyDocumentText(connection, null, "This is some document text.");
            assertEquals("text_id is 1", (long)text_id, 1);
            
            String[] tmp = {"insert_document_text"};
            DatabaseHelperSharedFunctions.TestLogging(connection, 1, 14, tmp);
            
            Integer sentence_id = DatabaseHelper.insertDocumentSentence(connection, 14, "This is some document text.", text_id, 1);
            DatabaseHelperSharedFunctions.verifyDocumentTextSentence(connection, "This is some document text.");
            String[] tmp1 = {"insert_document_text", "insert_document_sentence"};
            DatabaseHelperSharedFunctions.TestLogging(connection, 2, 14, tmp1);
            
            DatabaseHelper.insertDocumentSentenceMetadata(connection, 14, sentence_id, 1, "This", "", "", "this", false);
            DatabaseHelper.insertDocumentSentenceMetadata(connection, 14, sentence_id, 2, "is", "", "", "is", false);
            DatabaseHelper.insertDocumentSentenceMetadata(connection, 14, sentence_id, 3, "some", "", "", "some", false);
            DatabaseHelper.insertDocumentSentenceMetadata(connection, 14, sentence_id, 4, "document", "", "", "document", false);
            DatabaseHelper.insertDocumentSentenceMetadata(connection, 14, sentence_id, 5, "text", "", "", "text", false);
            DatabaseHelper.insertDocumentSentenceMetadata(connection, 14, sentence_id, 6, ".", "", "", ".", false);
            
            String[] tmp2 = {"insert_document_text", "insert_document_sentence", "insert_document_sentence_metadata"};
            DatabaseHelperSharedFunctions.TestLogging(connection, 8, 14, tmp2);
        }
    }
    
    @Test
    public void testInsertCategoryText() throws SQLException {
        try (Connection connection = DriverManager.getConnection(database_connection, "ben", "password")) {
            DatabaseHelper.insertCategory(connection, 14, "cat_1");
            Integer text_id = DatabaseHelper.insertCategoryText(connection, 14, "label1", "This is some Category text.", "file name", "cat_1", true);
            DatabaseHelperSharedFunctions.verifyCategoryText(connection, "label1", "This is some Category text.");
            assertEquals("text_id is 1", (long)text_id, 1);
        }
    }
    
    @Test
    public void testInsertCategoryText_Logging() throws SQLException {
        try (Connection connection = DriverManager.getConnection(database_connection, "ben", "password")) {
        	//results in 2 log entries. 
            DatabaseHelper.insertCategory(connection, 14, "cat_1");
            String[] tmp = {"select_category", "insert_category"};
            DatabaseHelperSharedFunctions.TestLogging(connection, 2, 14, tmp);
            
            Integer text_id = DatabaseHelper.insertCategoryText(connection, 14, "label1", "This is some Category text.", "file name", "cat_1", true);
            String[] tmp1 = {"select_category", "insert_category", "insert_category_text", "insert_training_data"};
            DatabaseHelperSharedFunctions.TestLogging(connection, 5, 14, tmp1);
        }
    }
    
    @Test
    public void testInsertCategoryTextNullLabel() throws SQLException {
        try (Connection connection = DriverManager.getConnection(database_connection, "ben", "password")) {
            DatabaseHelper.insertCategory(connection, 14, "cat_1");
            Integer text_id = DatabaseHelper.insertCategoryText(connection, 14, null, "This is some Category text.", "file name", "cat_1", true);
            DatabaseHelperSharedFunctions.verifyCategoryText(connection, null, "This is some Category text.");
            assertEquals("text_id is 1", (long)text_id, 1);
        }
    }
    
    @Test(expected = SQLException.class)
    public void testInsertCategoryTextNullText() throws SQLException  {
        try (Connection connection = DriverManager.getConnection(database_connection, "ben", "password")) {
            DatabaseHelper.insertCategory(connection, 14, "cat_1");
            Integer text_id = DatabaseHelper.insertCategoryText(connection, 14, "label1", null, "file name", "cat_1", true);
        }
    }
    
    @Test(expected = SQLException.class)
    public void testInsertCategoryTextNullCategory() throws SQLException  {
        try (Connection connection = DriverManager.getConnection(database_connection, "ben", "password")) {
            Integer text_id = DatabaseHelper.insertCategoryText(connection, 14, "label1", "This is some Category text.", "file name", null, true);
        }
    }
    
    @Test(expected = SQLException.class)
    public void testInsertCategoryTextNullPath() throws SQLException  {
        try (Connection connection = DriverManager.getConnection(database_connection, "ben", "password")) {
            Integer text_id = DatabaseHelper.insertCategoryText(connection, 14, "label1", "This is some Category text.", null, "cat_1", true);
        }
    }
    
    
    
    @Test
    public void testInsertCategorySentence() throws SQLException {
        try (Connection connection = DriverManager.getConnection(database_connection, "ben", "password")) {
            DatabaseHelper.insertCategory(connection, 14, "cat_1");
            Integer text_id = DatabaseHelper.insertCategoryText(connection, 14, null, "This is some Category text.", "file name", "cat_1", true);
            DatabaseHelperSharedFunctions.verifyCategoryText(connection, null, "This is some Category text.");
            assertEquals("text_id is 1", (long)text_id, 1);
            DatabaseHelper.insertCategorySentence(connection, 14, "This is some Category text.", text_id, 1);
            DatabaseHelperSharedFunctions.verifyCategoryTextSentence(connection, "This is some Category text.");
        }
    }
    
    @Test
    public void testInsertCategorySentence_Logging() throws SQLException {
        try (Connection connection = DriverManager.getConnection(database_connection, "ben", "password")) {
            DatabaseHelper.insertCategory(connection, 14, "cat_1");
            
            String[] tmp = {"select_category", "insert_category"};
            DatabaseHelperSharedFunctions.TestLogging(connection, 2, 14, tmp);
            
            Integer text_id = DatabaseHelper.insertCategoryText(connection, 14, null, "This is some Category text.", "file name", "cat_1", true);
            DatabaseHelperSharedFunctions.verifyCategoryText(connection, null, "This is some Category text.");
            

            String[] tmp1 = {"select_category", "insert_category", "insert_category_text", "insert_training_data"};
            DatabaseHelperSharedFunctions.TestLogging(connection, 5, 14, tmp1);
            
            
            assertEquals("text_id is 1", (long)text_id, 1);
            DatabaseHelper.insertCategorySentence(connection, 14, "This is some Category text.", text_id, 1);
            DatabaseHelperSharedFunctions.verifyCategoryTextSentence(connection, "This is some Category text.");
            
            String[] tmp2 = {"select_category", "insert_category", "insert_category_text", "insert_training_data", "insert_category_sentence"};
            DatabaseHelperSharedFunctions.TestLogging(connection, 6, 14, tmp2);
        }
    }
    
    @Test(expected = SQLException.class)
    public void testInsertCategoryNullSentence() throws SQLException {
        try (Connection connection = DriverManager.getConnection(database_connection, "ben", "password")) {
            Integer text_id = DatabaseHelper.insertCategoryText(connection, 14, null, "This is some Category text.", "file name", "cat_1", true);
            DatabaseHelperSharedFunctions.verifyCategoryText(connection, "label1", "This is some Category text.");
            assertEquals("text_id is 1", (long)text_id, 1);
            DatabaseHelper.insertCategorySentence(connection, 14, null, text_id, 1);
        }
    }
    
    @Test(expected = SQLException.class)
    public void testInsertCategorySentenceNullTextId() throws SQLException {
        try (Connection connection = DriverManager.getConnection(database_connection, "ben", "password")) {
            Integer text_id = DatabaseHelper.insertCategoryText(connection, 14, null, "This is some Category text.", "file name", "cat_1", true);
            DatabaseHelperSharedFunctions.verifyCategoryText(connection, "label1", "This is some Category text.");
            assertEquals("text_id is 1", (long)text_id, 1);
            DatabaseHelper.insertCategorySentence(connection, 14, "This is some Category text.", null, 1);
        }
    }
    
    @Test(expected = SQLException.class)
    public void testInsertCategorySentenceNullSentenceNumber() throws SQLException {
        try (Connection connection = DriverManager.getConnection(database_connection, "ben", "password")) {
            Integer text_id = DatabaseHelper.insertCategoryText(connection, 14, null, "This is some Category text.", "file name", "cat_1", true);
            DatabaseHelperSharedFunctions.verifyCategoryText(connection, "label1", "This is some Category text.");
            assertEquals("text_id is 1", (long)text_id, 1);
            DatabaseHelper.insertCategorySentence(connection, 14, "This is some Category text.", text_id, null);
        }
    }
    
    @Test
    public void testInsertCategorySentenceMetadata() throws SQLException {
        try (Connection connection = DriverManager.getConnection(database_connection, "ben", "password")) {
            DatabaseHelper.insertCategory(connection, 14, "cat_1");
            Integer text_id = DatabaseHelper.insertCategoryText(connection, 14, null, "This is some Category text.", "file name", "cat_1", true);
            DatabaseHelperSharedFunctions.verifyCategoryText(connection, null, "This is some Category text.");
            assertEquals("text_id is 1", (long)text_id, 1);
            Integer sentence_id = DatabaseHelper.insertCategorySentence(connection, 14, "This is some Category text.", text_id, 1);
            DatabaseHelperSharedFunctions.verifyCategoryTextSentence(connection, "This is some Category text.");
            DatabaseHelper.insertCategorySentenceMetadata(connection, 14, sentence_id, 1, "This", "", "", "this", false);
            DatabaseHelper.insertCategorySentenceMetadata(connection, 14, sentence_id, 2, "is", "", "", "is", false);
            DatabaseHelper.insertCategorySentenceMetadata(connection, 14, sentence_id, 3, "some", "", "", "some", false);
            DatabaseHelper.insertCategorySentenceMetadata(connection, 14, sentence_id, 4, "Category", "", "", "Category", false);
            DatabaseHelper.insertCategorySentenceMetadata(connection, 14, sentence_id, 5, "text", "", "", "text", false);
            DatabaseHelper.insertCategorySentenceMetadata(connection, 14, sentence_id, 6, ".", "", "", ".", false);
        }
    }
    
    @Test
    public void testInsertCategorySentenceMetadata_Logging() throws SQLException {
        try (Connection connection = DriverManager.getConnection(database_connection, "ben", "password")) {
            DatabaseHelper.insertCategory(connection, 14, "cat_1");
            
            String[] tmp = {"select_category", "insert_category"};
            DatabaseHelperSharedFunctions.TestLogging(connection, 2, 14, tmp);
            
            Integer text_id = DatabaseHelper.insertCategoryText(connection, 14, null, "This is some Category text.", "file name", "cat_1", true);
            
            String[] tmp1 = {"select_category", "insert_category", "insert_category_text", "insert_training_data"};
            DatabaseHelperSharedFunctions.TestLogging(connection, 5, 14, tmp1);
            
            DatabaseHelperSharedFunctions.verifyCategoryText(connection, null, "This is some Category text.");
            assertEquals("text_id is 1", (long)text_id, 1);
            
            
            Integer sentence_id = DatabaseHelper.insertCategorySentence(connection, 14, "This is some Category text.", text_id, 1);
            String[] tmp2 = {"select_category", "insert_category", "insert_category_text", "insert_training_data", "insert_category_sentence"};
            DatabaseHelperSharedFunctions.TestLogging(connection, 6, 14, tmp2);
            
            DatabaseHelperSharedFunctions.verifyCategoryTextSentence(connection, "This is some Category text.");
            DatabaseHelper.insertCategorySentenceMetadata(connection, 14, sentence_id, 1, "This", "", "", "this", true);
            DatabaseHelper.insertCategorySentenceMetadata(connection, 14, sentence_id, 2, "is", "", "", "is", true);
            DatabaseHelper.insertCategorySentenceMetadata(connection, 14, sentence_id, 3, "some", "", "", "some", true);
            DatabaseHelper.insertCategorySentenceMetadata(connection, 14, sentence_id, 4, "Category", "", "", "Category", true);
            DatabaseHelper.insertCategorySentenceMetadata(connection, 14, sentence_id, 5, "text", "", "", "text", true);
            DatabaseHelper.insertCategorySentenceMetadata(connection, 14, sentence_id, 6, ".", "", "", ".", true);
            
            String[] tmp3 = {"select_category", "insert_category", "insert_category_text", "insert_training_data", "insert_category_sentence", "insert_category_sentence_metadata"};
            DatabaseHelperSharedFunctions.TestLogging(connection, 12, 14, tmp3);
        }
    }
    
    @Test
    public void testGetDocumentTokensMatchingCaps() throws SQLException {
        try (Connection connection = DriverManager.getConnection(database_connection, "ben", "password")) {
            Integer text_id = DatabaseHelper.insertDocumentText(connection, 14, null, "This is some document text.", "file name");
            assertEquals("text_id is 1", (long)text_id, 1);
            Integer sentence_id = DatabaseHelper.insertDocumentSentence(connection, 14, "This is some document text.", text_id, 1);
            DatabaseHelperSharedFunctions.verifyDocumentTextSentence(connection, "This is some document text.");
            DatabaseHelper.insertDocumentSentenceMetadata(connection, 14, sentence_id, 1, "This", "", "", "this", true);
            DatabaseHelper.insertDocumentSentenceMetadata(connection, 14, sentence_id, 2, "is", "", "", "is", true);
            DatabaseHelper.insertDocumentSentenceMetadata(connection, 14, sentence_id, 3, "some", "", "", "some", true);
            DatabaseHelper.insertDocumentSentenceMetadata(connection, 14, sentence_id, 4, "document", "", "", "document", true);
            DatabaseHelper.insertDocumentSentenceMetadata(connection, 14, sentence_id, 5, "text", "", "", "text", true);
            DatabaseHelper.insertDocumentSentenceMetadata(connection, 14, sentence_id, 6, ".", "", "", ".", true);
            
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
            Integer text_id = DatabaseHelper.insertDocumentText(connection, 14, null, "This is some document text.", "file name");
            assertEquals("text_id is 1", (long)text_id, 1);
            Integer sentence_id = DatabaseHelper.insertDocumentSentence(connection, 14, "This is some document text.", text_id, 1);
            DatabaseHelperSharedFunctions.verifyDocumentTextSentence(connection, "This is some document text.");
            DatabaseHelper.insertDocumentSentenceMetadata(connection, 14, sentence_id, 1, "This", "", "", "this", false);
            DatabaseHelper.insertDocumentSentenceMetadata(connection, 14, sentence_id, 2, "is", "", "", "is", false);
            DatabaseHelper.insertDocumentSentenceMetadata(connection, 14, sentence_id, 3, "some", "", "", "some", false);
            DatabaseHelper.insertDocumentSentenceMetadata(connection, 14, sentence_id, 4, "document", "", "", "document", false);
            DatabaseHelper.insertDocumentSentenceMetadata(connection, 14, sentence_id, 5, "text", "", "", "text", false);
            DatabaseHelper.insertDocumentSentenceMetadata(connection, 14, sentence_id, 6, ".", "", "", ".", false);
            
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
     * @TODO: fill this out with pre-existing model.
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
    public void testIncrementBatchNumber_Logging() throws SQLException {
        try (Connection connection = DriverManager.getConnection(database_connection, "ben", "password")) {
            //if it starts at x, calling nextval will result in x.
            //@TODO: Make this work properly within the code base. 
            testBatchNumber(connection, 1);
            DatabaseHelper.incrementBatchNumber(connection, 14);
            testBatchNumber(connection, 1);
            
            String[] tmp = {"increment_batch_number"};
            DatabaseHelperSharedFunctions.TestLogging(connection, 1, 14, tmp);
            
            DatabaseHelper.incrementBatchNumber(connection, 14);
            testBatchNumber(connection, 2);
            
            String[] tmp2 = {"increment_batch_number"};
            DatabaseHelperSharedFunctions.TestLogging(connection, 2, 14, tmp2);
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
            Integer text_id = DatabaseHelper.insertDocumentText(connection, 14, null, "This is some document text.", "file name");
            DatabaseHelper.insertCategory(connection, 14, "cat_1");
            Integer cat_id = DatabaseHelper.getCategoryId(connection, 14, "cat_1");
            DatabaseHelper.insertCategoryProbabilities(connection, 14, 1, text_id, cat_id, null, 75.3, null, 100.0);
        }
    }
    
    @Test
    public void testInsertCategoryProbabilities_Logging() throws SQLException {
        try (Connection connection = DriverManager.getConnection(database_connection, "ben", "password")) {
            Integer text_id = DatabaseHelper.insertDocumentText(connection, 14, null, "This is some document text.", "file name");
            
            String[] tmp = {"insert_document_text"};
            DatabaseHelperSharedFunctions.TestLogging(connection, 1, 14, tmp);
            
            DatabaseHelper.insertCategory(connection, 14, "cat_1");
            
            String[] tmp2 = {"insert_document_text", "insert_category", "select_category"};
            DatabaseHelperSharedFunctions.TestLogging(connection, 3, 14, tmp2);
            
            Integer cat_id = DatabaseHelper.getCategoryId(connection, 14, "cat_1");
            DatabaseHelper.insertCategoryProbabilities(connection, 14, 1, text_id, cat_id, null, 75.3, null, 100.0);
            
            String[] tmp3 = {"insert_document_text", "insert_category", "select_category", "insert_text_category_probability"};
            DatabaseHelperSharedFunctions.TestLogging(connection, 5, 14, tmp3);
        }
    }
    
    @Test
    public void testAssignDocumentToCategory() throws SQLException {
        try (Connection connection = DriverManager.getConnection(database_connection, "ben", "password")) {
            Integer text_id = DatabaseHelper.insertDocumentText(connection, 14, null, "This is some document text.", "file name");
            DatabaseHelper.insertCategory(connection, 14, "cat_1");
            Integer cat_id = DatabaseHelper.getCategoryId(connection, 14, "cat_1");
            DatabaseHelper.assignDocumentToCategory(connection, 14, text_id, cat_id, true);
            DatabaseHelperSharedFunctions.verifyCategoryText(connection, null, "This is some document text.");
        }
    }
    
    @Test
    public void testAssignDocumentToCategory_Logging() throws SQLException {
        try (Connection connection = DriverManager.getConnection(database_connection, "ben", "password")) {
            Integer text_id = DatabaseHelper.insertDocumentText(connection, 14, null, "This is some document text.", "file name");
            
            String[] tmp = {"insert_document_text"};
            DatabaseHelperSharedFunctions.TestLogging(connection, 1, 14, tmp);
            
            
            DatabaseHelper.insertCategory(connection, 14, "cat_1");
            Integer cat_id = DatabaseHelper.getCategoryId(connection, 14, "cat_1");
            
            String[] tmp2 = {"insert_document_text", "insert_category", "select_category"};
            DatabaseHelperSharedFunctions.TestLogging(connection, 4, 14, tmp2);
            
            DatabaseHelper.assignDocumentToCategory(connection, 14, text_id, cat_id, true);
            DatabaseHelperSharedFunctions.verifyCategoryText(connection, null, "This is some document text.");
            
            String[] tmp3 = {"insert_document_text", "insert_category", "select_category", "copy_document_to_category_text", "insert_training_data"};
            DatabaseHelperSharedFunctions.TestLogging(connection, 6, 14, tmp3);
        }
    }
    
    /**
     * @TODO: user unit tests
     * @TODO: role unit test
     * @TODO: add user and role
     * @TODO: delete user
     * @TODO: delete all roles for user
     * @TODO: create two users, assign roles, delete 1 user.
     * @TODO: create two users, assign roles, delete 1 user roles. 
     * @TODO: Select roles for user 
     */
    /* 
     * These tests are for the database only and are not really
     * going to be referenced from the platform. 
     */
    @Test
    public void testAddUser_Full() throws SQLException {
        try (Connection connection = DriverManager.getConnection(database_connection, "ben", "password")) {
        	Integer new_user_id = DatabaseHelper.addNewUser(connection, "test user", "test_email@email.com", "first name", "last name");
        	assertNotNull(new_user_id);
        	
        	Statement s = connection.createStatement();
            ResultSet rs = s.executeQuery(String.format("SELECT username, email, first_name, last_name FROM users where id = %s", new_user_id));
            while (rs.next()) {
            	String username = rs.getString(1);
            	String email = rs.getString(2);
            	String first_name = rs.getString(3);
            	String last_name = rs.getString(4);
            	
            	assertEquals("username test", username, "test user");
            	assertEquals("email test", email, "test_email@email.com");
            	assertEquals("first name test", first_name, "first name");
            	assertEquals("last name test", last_name, "last name");
            }
        }
    }
    
    @Test
    public void testAddUser_Full_Logging() throws SQLException {
        try (Connection connection = DriverManager.getConnection(database_connection, "ben", "password")) {
        	Integer new_user_id = DatabaseHelper.addNewUser(connection, "test user", "test_email@email.com", "first name", "last name");
        	assertNotNull(new_user_id);
        }
    }
    
    @Test
    public void testAddUser_First() throws SQLException {
        try (Connection connection = DriverManager.getConnection(database_connection, "ben", "password")) {
        	Integer new_user_id = DatabaseHelper.addNewUser(connection, "test user", "test_email@email.com", "first name", null);
        	assertNotNull(new_user_id);
        	
        	Statement s = connection.createStatement();
            ResultSet rs = s.executeQuery(String.format("SELECT username, email, first_name, last_name FROM users where id = %s", new_user_id));
            while (rs.next()) {
            	String username = rs.getString(1);
            	String email = rs.getString(2);
            	String first_name = rs.getString(3);
            	String last_name = rs.getString(4);
            	
            	assertEquals("username test", username, "test user");
            	assertEquals("email test", email, "test_email@email.com");
            	assertEquals("first name test", first_name, "first name");
            	assertNull("last name test", last_name);
            }
        }
    }
    
    @Test
    public void testAddUser_Last() throws SQLException {
        try (Connection connection = DriverManager.getConnection(database_connection, "ben", "password")) {
        	Integer new_user_id = DatabaseHelper.addNewUser(connection, "test user", "test_email@email.com", null, "last name");
        	assertNotNull(new_user_id);
        	
        	Statement s = connection.createStatement();
            ResultSet rs = s.executeQuery(String.format("SELECT username, email, first_name, last_name FROM users where id = %s", new_user_id));
            while (rs.next()) {
            	String username = rs.getString(1);
            	String email = rs.getString(2);
            	String first_name = rs.getString(3);
            	String last_name = rs.getString(4);
            	
            	assertEquals("username test", username, "test user");
            	assertEquals("email test", email, "test_email@email.com");
            	assertNull("first name test", first_name);
            	assertEquals("last name test", last_name, "last name");
            }
        }
    }
    
    @Test
    public void testDeleteUser() throws SQLException {
        try (Connection connection = DriverManager.getConnection(database_connection, "ben", "password")) {
        	Integer new_user_id = DatabaseHelper.addNewUser(connection, "test user", "test_email@email.com", null, "last name");
        	assertNotNull(new_user_id);
        	DatabaseHelper.deleteApplicationUser(connection, 14, new_user_id);
        	
        	Statement s = connection.createStatement();
        	ResultSet rs = s.executeQuery(String.format("SELECT username, email, first_name, last_name FROM users where id = %s", new_user_id));
            while (rs.next()) {
            	assertTrue("User exists in the database", false);
            }
            assertTrue("User does not exist in the database", true);
        }
    }
    
    @Test
    public void testDeleteUser_Logging() throws SQLException {
        try (Connection connection = DriverManager.getConnection(database_connection, "ben", "password")) {
        	Integer new_user_id = DatabaseHelper.addNewUser(connection, "test user", "test_email@email.com", null, "last name");
        	assertNotNull(new_user_id);
        	DatabaseHelper.deleteApplicationUser(connection, 14, new_user_id);
        	
        	String[] tmp1 = {"delete_application_user"};
            DatabaseHelperSharedFunctions.TestLogging(connection, 1, 14, tmp1);
        }
    }
    
    @Test
    public void testAddRole_Full() throws SQLException {
    	try (Connection connection = DriverManager.getConnection(database_connection, "ben", "password")) {
    		
    		LoremIpsum li = new LoremIpsum();
        	String display_name = li.getParagraphs(10);
        	String description = li.getParagraphs(10);
        	Integer new_role_id = DatabaseHelper.insertRole(connection,14, "role 1", display_name, description);
        	
        	assertNotNull(new_role_id);
        	
        	Statement s = connection.createStatement();
        	ResultSet rs = s.executeQuery(String.format("SELECT id, name, display_name, description FROM roles where id = %s", new_role_id));
            boolean has_next = false;
        	while (rs.next()) {
            	has_next = true;
            	Integer id = rs.getInt(1);
            	String name = rs.getString(2);
            	String db_display_name = rs.getString(3);
            	String db_description = rs.getString(4);
            			
            	assertTrue("id is valid", id > 0);
            	assertEquals("name is equal", name, "role 1");
            	assertEquals("display_name is equal", db_display_name, display_name);
            	assertEquals("description is equal", db_description, description);
            }
        	assertTrue("Role exists in the database", has_next);
        }
    }
    
    @Test
    public void testAddRole_Full_Logging() throws SQLException {
    	try (Connection connection = DriverManager.getConnection(database_connection, "ben", "password")) {
    		
    		LoremIpsum li = new LoremIpsum();
        	String display_name = li.getParagraphs(10);
        	String description = li.getParagraphs(10);
        	Integer new_role_id = DatabaseHelper.insertRole(connection,14, "role 1", display_name, description);
        	
        	String[] tmp = {"insert_new_role"};
            DatabaseHelperSharedFunctions.TestLogging(connection, 1, 14, tmp);
        	
            assertNotNull(new_role_id);
        }
    }

    @Test
    public void testAddRole_Partial() throws SQLException {
    	try (Connection connection = DriverManager.getConnection(database_connection, "ben", "password")) {
        	String display_name = null;
        	String description = null;
        	Integer new_role_id = DatabaseHelper.insertRole(connection,14, "role 1", display_name, description);

        	assertNotNull(new_role_id);
        	
        	Statement s = connection.createStatement();
        	ResultSet rs = s.executeQuery(String.format("SELECT id, name, display_name, description FROM roles where id = %s", new_role_id));
            boolean has_next = false;
        	while (rs.next()) {
            	has_next = true;
            	Integer id = rs.getInt(1);
            	String name = rs.getString(2);
            	String db_display_name = rs.getString(3);
            	String db_description = rs.getString(4);
            			
            	assertTrue("id is valid", id > 0);
            	assertEquals("name is equal", name, "role 1");
            	assertNull(db_display_name);
            	assertNull(db_description);
            }
        	assertTrue("Role exists in the database", has_next);
        }
    }
    
    @Test
    public void testAddUserRole_Full() throws SQLException {
    	try (Connection connection = DriverManager.getConnection(database_connection, "ben", "password")) {
    		LoremIpsum li = new LoremIpsum();
        	String display_name = li.getParagraphs(10);
        	String description = li.getParagraphs(10);
        	
        	Integer new_user_id = DatabaseHelper.addNewUser(connection, "test user", "test_email@email.com", "first name", "last name");
        	Integer new_role_id = DatabaseHelper.insertRole(connection, 14, "role 1", display_name, description);

        	Integer new_user_role_id = DatabaseHelper.insertUserRole(connection, 14, new_user_id, new_role_id);
        	
        	assertNotNull(new_user_role_id);
        	
        	Statement s = connection.createStatement();
        	ResultSet rs = s.executeQuery(String.format("SELECT id, user_id, role_id FROM user_role_assignments where id = %s", new_user_role_id));
            boolean has_next = false;
        	while (rs.next()) {
            	has_next = true;
            	Integer id = rs.getInt(1);
            	Integer user_id = rs.getInt(2);
            	Integer role_id = rs.getInt(3);            	
            			
            	assertTrue("id is valid", id > 0);
            	assertEquals("user_id is equal", user_id, new_user_id);
            	assertEquals("role_id is equal", role_id, new_role_id);
            }
        	assertTrue("Role exists in the database", has_next);
        }
    }
    
    @Test
    public void testAddUserRole_Full_Logging() throws SQLException {
    	try (Connection connection = DriverManager.getConnection(database_connection, "ben", "password")) {
    		LoremIpsum li = new LoremIpsum();
        	String display_name = li.getParagraphs(10);
        	String description = li.getParagraphs(10);
        	
        	Integer new_user_id = DatabaseHelper.addNewUser(connection, "test user", "test_email@email.com", "first name", "last name");
        	
        	//user new user will cannot have a log. What user would log it?
        	//String[] tmp = {"insert_new_user"};
            //DatabaseHelperSharedFunctions.TestLogging(connection, 0, 14, tmp);
        	
        	Integer new_role_id = DatabaseHelper.insertRole(connection, 14, "role 1", display_name, description);
        	
        	String[] tmp1 = {"insert_new_role"};
            DatabaseHelperSharedFunctions.TestLogging(connection, 1, 14, tmp1);

        	Integer new_user_role_id = DatabaseHelper.insertUserRole(connection, 14, new_user_id, new_role_id);
        	
        	String[] tmp2 = {"insert_new_role", "insert_user_role"};
            DatabaseHelperSharedFunctions.TestLogging(connection, 2, 14, tmp2);
        }
    }
    
    @Test
    public void testDeleteUserRole_Full() throws SQLException {
    	try (Connection connection = DriverManager.getConnection(database_connection, "ben", "password")) {
    		LoremIpsum li = new LoremIpsum();
        	String display_name = li.getParagraphs(10);
        	String description = li.getParagraphs(10);
        	
        	Integer new_user_id = DatabaseHelper.addNewUser(connection, "test user", "test_email@email.com", "first name", "last name");
        	Integer new_role_id = DatabaseHelper.insertRole(connection, 14, "role 1", display_name, description);
        	Integer new_role_id2 = DatabaseHelper.insertRole(connection, 14, "role 2", display_name, description);

        	Integer new_user_role_id = DatabaseHelper.insertUserRole(connection, 14, new_user_id, new_role_id);
        	Integer new_user_role_id2 = DatabaseHelper.insertUserRole(connection, 14, new_user_id, new_role_id2);
        	
        	DatabaseHelper.deleteUserRole(connection, 14, new_user_id);
        	
        	Statement s = connection.createStatement();
        	ResultSet rs = s.executeQuery(String.format("SELECT id, user_id, role_id FROM user_role_assignments where user_id = %s", new_user_id));
        	boolean has_next = false;
        	while (rs.next()) {
            	has_next = true;
            }
        	assertFalse("Role assigned to user in the database", has_next);
        }
    }
    
    @Test
    public void testDeleteUserRole_Full_Logging() throws SQLException {
    	try (Connection connection = DriverManager.getConnection(database_connection, "ben", "password")) {
    		LoremIpsum li = new LoremIpsum();
        	String display_name = li.getParagraphs(10);
        	String description = li.getParagraphs(10);
        	
        	Integer new_user_id = DatabaseHelper.addNewUser(connection, "test user", "test_email@email.com", "first name", "last name");
        	
        	Integer new_role_id = DatabaseHelper.insertRole(connection, 14, "role 1", display_name, description);
        	
        	String[] tmp1 = {"insert_new_role"};
            DatabaseHelperSharedFunctions.TestLogging(connection, 1, 14, tmp1);
            
        	Integer new_role_id2 = DatabaseHelper.insertRole(connection, 14, "role 2", display_name, description);
        	
        	DatabaseHelperSharedFunctions.TestLogging(connection, 2, 14, tmp1);

        	Integer new_user_role_id = DatabaseHelper.insertUserRole(connection, 14, new_user_id, new_role_id);
        	
        	String[] tmp2 = {"insert_new_role", "insert_user_role"};
        	DatabaseHelperSharedFunctions.TestLogging(connection, 3, 14, tmp2);
        	
        	Integer new_user_role_id2 = DatabaseHelper.insertUserRole(connection, 14, new_user_id, new_role_id2);
        	
        	DatabaseHelperSharedFunctions.TestLogging(connection, 4, 14, tmp2);
        	
        	DatabaseHelper.deleteUserRole(connection, 14, new_user_id);
        	
        	String[] tmp3 = {"insert_new_role", "insert_user_role", "delete_user_roles"};
        	DatabaseHelperSharedFunctions.TestLogging(connection, 5, 14, tmp3);
        }
    }
    
    @Test
    public void testDeleteRoleFromUsers() throws SQLException {
    	try (Connection connection = DriverManager.getConnection(database_connection, "ben", "password")) {
    		LoremIpsum li = new LoremIpsum();
        	String display_name = li.getParagraphs(10);
        	String description = li.getParagraphs(10);
        	
        	Integer new_user_id = DatabaseHelper.addNewUser(connection, "test user", "test_email@email.com", "first name", "last name");
        	Integer new_role_id = DatabaseHelper.insertRole(connection, 14, "role 1", display_name, description);
        	Integer new_role_id2 = DatabaseHelper.insertRole(connection, 14, "role 2", display_name, description);

        	Integer new_user_role_id = DatabaseHelper.insertUserRole(connection, 14, new_user_id, new_role_id);
        	Integer new_user_role_id2 = DatabaseHelper.insertUserRole(connection, 14, new_user_id, new_role_id2);
        	
        	DatabaseHelper.deleteApplicationUser(connection, 14, new_user_id);
        	
        	Statement s = connection.createStatement();
        	ResultSet rs = s.executeQuery(String.format("SELECT id, user_id, role_id FROM user_role_assignments where user_id = %s", new_user_id));
        	boolean has_next = false;
        	while (rs.next()) {
            	has_next = true;
            }
        	assertFalse("Role assigned to user in the database", has_next);
        }
    }
    
    @Test
    public void testDeleteRoleFromUsers_Logging() throws SQLException {
    	try (Connection connection = DriverManager.getConnection(database_connection, "ben", "password")) {
    		LoremIpsum li = new LoremIpsum();
        	String display_name = li.getParagraphs(10);
        	String description = li.getParagraphs(10);
        	
        	Integer new_user_id = DatabaseHelper.addNewUser(connection, "test user", "test_email@email.com", "first name", "last name");
        	
        	String[] tmp = {"insert_new_user"};
            DatabaseHelperSharedFunctions.TestLogging(connection, 0, 14, tmp);
            
        	Integer new_role_id = DatabaseHelper.insertRole(connection, 14, "role 1", display_name, description);
        	
        	String[] tmp1 = {"insert_new_role"};
            DatabaseHelperSharedFunctions.TestLogging(connection, 1, 14, tmp1);
            
        	Integer new_role_id2 = DatabaseHelper.insertRole(connection, 14, "role 2", display_name, description);
        	
        	DatabaseHelperSharedFunctions.TestLogging(connection, 2, 14, tmp1);

        	Integer new_user_role_id = DatabaseHelper.insertUserRole(connection, 14, new_user_id, new_role_id);
        	
        	String[] tmp2 = {"insert_new_role", "insert_user_role"};
        	DatabaseHelperSharedFunctions.TestLogging(connection, 3, 14, tmp2);
        	
        	Integer new_user_role_id2 = DatabaseHelper.insertUserRole(connection, 14, new_user_id, new_role_id2);
        	
        	DatabaseHelperSharedFunctions.TestLogging(connection, 4, 14, tmp2);
        	
        	DatabaseHelper.deleteApplicationUser(connection, 14, new_user_id);
        	
        	String[] tmp3 = {"insert_new_role", "insert_user_role", "delete_application_user"};
        	DatabaseHelperSharedFunctions.TestLogging(connection, 5, 14, tmp3);
        }
    }
    
    /**clean_projects
     * 	in_user_id integer
     */
    
    /**
     * insert_project 
     * 	in_user_id integer,
     *  in_owner_user_id integer,
     *  in_ready_for_review_id integer,
     *  in_checkout_timeout integer default 600)   
     */
    @Test
    public void testInsertProject() throws SQLException {
    	try (Connection connection = DriverManager.getConnection(database_connection, "ben", "password")) {
    		
    		Statement s = connection.createStatement();
            String insert_status = String.format("insert into multiuser_document_classification_status_labels" +
            		"	(id, status_text, next_id, is_ready_for_review, is_initialize)" +
            		"	values" +
            		"   (1, 'initial', 2, false, true)," +
            		"   (2, 'reviewer 1', 3, false, false)," +
            		"   (3, 'reviewer 2', 4, false, false)," +
            		"   (4, 'ready for reivew', null, true, false);");
            s.execute(insert_status);
    		
    		Integer user_id = DatabaseHelper.addNewUser(connection, "test@testing.com", "test@testing.com", "tester first name", "tester last name");
    		DatabaseHelper.insertProject(connection, "test project", 14, user_id, 4, 600);
    	}
    }
    
    
    /**
     * clean_document_classification_status
     * 	in_user_id integer
     */
    /**
     * insert_document_classification_status(
     *  in_user_id integer, 
     *  in_status text) 
     */
    @Test
    public void testInsertDocumentClassifcationStatus() throws SQLException {
    	try (Connection connection = DriverManager.getConnection(database_connection, "ben", "password")) {
    		
    		Statement s = connection.createStatement();
            String insert_status = String.format("insert into multiuser_document_classification_status_labels" +
            		"	(id, status_text, next_id, is_ready_for_review, is_initialize)" +
            		"	values" +
            		"   (1, 'initial', 2, false, true)," +
            		"   (2, 'reviewer 1', 3, false, false)," +
            		"   (3, 'reviewer 2', 4, false, false)," +
            		"   (4, 'ready for reivew', null, true, false);");
            s.execute(insert_status);
    		Integer user_id = DatabaseHelper.addNewUser(connection, "test@testing.com", "test@testing.com", "tester first name", "tester last name");
    		DatabaseHelper.insertProject(connection, "test project", 14, user_id, 4, 600);
    	}
    }
    
    
    
    /**
     * insert_document_classification_status_next
     *  in_user_id integer, 
     *	in_document_classification_status_id integer, 
     *  in_next_id integer 
     */
    
    /**
     * select_document_classification_status(
     *  in_user_id integer 
     */
}


