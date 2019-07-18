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

public class DatabaseHelperDeleteTextTest {
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
        database = "db_delete_text_testing";
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
    public void testDocumentTextDelete() throws SQLException {
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
            
            DatabaseHelper.deleteDocumentTextsAtPath(connection, 14, "file name");
            
            Statement s = connection.createStatement();
            ResultSet rs = s.executeQuery(String.format("SELECT count(*) FROM document_text"));
            int row_count = 0;
            while (rs.next()) {
            	int document_text_row_count = rs.getInt(1);
            	assertEquals(0, document_text_row_count);
            	row_count++;
            }
            assertEquals(row_count, 1);
            
            s = connection.createStatement();
            rs = s.executeQuery(String.format("SELECT count(*) FROM document_sentences"));
            row_count = 0;
            while (rs.next()) {
            	int document_text_row_count = rs.getInt(1);
            	assertEquals(0, document_text_row_count);
            	row_count++;
            }
            assertEquals(row_count, 1);
            
            s = connection.createStatement();
            rs = s.executeQuery(String.format("SELECT count(*) FROM document_sentences_metadata"));
            row_count = 0;
            while (rs.next()) {
            	int document_text_row_count = rs.getInt(1);
            	assertEquals(0, document_text_row_count);
            	row_count++;
            }
            assertEquals(row_count, 1);
        }
    }
    
    @Test
    public void testCategoryTextDelete() throws SQLException {
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
            

            DatabaseHelper.deleteCategoryTextsAtPath(connection, 14, "file name");
            
            Statement s = connection.createStatement();
            ResultSet rs = s.executeQuery(String.format("SELECT count(*) FROM category_text"));
            int row_count = 0;
            while (rs.next()) {
            	int category_text_row_count = rs.getInt(1);
            	assertEquals(0, category_text_row_count);
            	row_count++;
            }
            assertEquals(row_count, 1);
            
            s = connection.createStatement();
            rs = s.executeQuery(String.format("SELECT count(*) FROM category_sentences"));
            row_count = 0;
            while (rs.next()) {
            	int category_sentences_row_count = rs.getInt(1);
            	assertEquals(0, category_sentences_row_count);
            	row_count++;
            }
            assertEquals(row_count, 1);
            
            s = connection.createStatement();
            rs = s.executeQuery(String.format("SELECT count(*) FROM category_sentences_metadata"));
            row_count = 0;
            while (rs.next()) {
            	int category_metadata_row_count = rs.getInt(1);
            	assertEquals(0, category_metadata_row_count);
            	row_count++;
            }
            assertEquals(row_count, 1);
        }
    }
}
