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

public class DatabaseHelperFilePathTest {
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
        database = "db_helper_file_path_testing";
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
        
        c = DriverManager.getConnection(database_connection, user_name, password);
        s = c.createStatement();
        String insert_file_path_str = String.format("insert into completed_processed_files" +
        		"	(file_path, is_complete)" +
        		"	values" +
        		"	('file_path', FALSE);");
        s.execute(insert_file_path_str);
        
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
    public void connectionNlp() throws SQLException {
        Connection connection = DriverManager.getConnection(database_connection, "ben", "password");
        connection.close();
    }
	/**
     * @TODO: add unit tests for the file path completed_processed_files
     */
    
    @Test 
    public void selectProcessedFilesTest() throws SQLException {
        Connection connection = DriverManager.getConnection(database_connection, "ben", "password");
        DatabaseHelper.selectUnprocessedFiles(connection, 14);
        
        String[] tmp = {"select_unprocessed_files"};
    	DatabaseHelperSharedFunctions.TestLogging(connection, 1, 14, tmp);
        connection.close();
    }
    
    @Test 
    public void selectProcessedFilesLengthTest() throws SQLException {
        Connection connection = DriverManager.getConnection(database_connection, "ben", "password");
        ArrayList<String> files = DatabaseHelper.selectUnprocessedFiles(connection, 14);
        assertEquals(1, files.size());
        assertEquals("file_path", files.get(0));
        
        String[] tmp = {"select_unprocessed_files"};
    	DatabaseHelperSharedFunctions.TestLogging(connection, 1, 14, tmp);
        connection.close();
    }
    
    @Test 
    public void updateProcessedFilesSetFinishedTest() throws SQLException {
        Connection connection = DriverManager.getConnection(database_connection, "ben", "password");
        DatabaseHelper.updateProcessedFilesSetFinished(connection, 14, "file_path");
        
        String[] tmp = {"update_processed_files_set_finished"};
    	DatabaseHelperSharedFunctions.TestLogging(connection, 1, 14, tmp);
        connection.close();
    }
    
    @Test 
    public void updateProcessedFilesSetFinishedLengthTest() throws SQLException {
        Connection connection = DriverManager.getConnection(database_connection, "ben", "password");
        DatabaseHelper.updateProcessedFilesSetFinished(connection, 14, "file_path");
        
        ArrayList<String> files = DatabaseHelper.selectUnprocessedFiles(connection, 14);
        assertEquals(0, files.size());
        
        String[] tmp = {"update_processed_files_set_finished", "select_unprocessed_files"};
    	DatabaseHelperSharedFunctions.TestLogging(connection, 2, 14, tmp);
        connection.close();
    }
    
    @Test 
    public void updateProcessedFilesSetFinishedProcessedTest() throws SQLException {
        Connection connection = DriverManager.getConnection(database_connection, "ben", "password");
        DatabaseHelper.updateProcessedFilesSetFinished(connection, 14, "file_path");
        
        ArrayList<String> processed = DatabaseHelper.selectProcessedFiles(connection, 14);
        assertEquals(1, processed.size());
        assertEquals("file_path", processed.get(0));
        
        ArrayList<String> files = DatabaseHelper.selectUnprocessedFiles(connection, 14);
        assertEquals(0, files.size());
        
        String[] tmp = {"select_processed_files", "select_unprocessed_files", "update_processed_files_set_finished"};
    	DatabaseHelperSharedFunctions.TestLogging(connection, 3, 14, tmp);
        connection.close();
    }
    
    
    

}
