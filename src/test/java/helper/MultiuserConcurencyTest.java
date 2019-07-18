package helper;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.factory.Nd4j;
import org.postgresql.util.PSQLException;

import de.svenjacobs.loremipsum.LoremIpsum;
import descriptors.PostProcessor_CosDistance;
import junit.framework.TestCase;
import multiuser.MultiuserResponse;
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
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.commons.collections.EnumerationUtils;
import org.apache.log4j.Logger;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;

import com.anarsoft.vmlens.concurrent.junit.ConcurrentTestRunner;
import com.anarsoft.vmlens.concurrent.junit.ThreadCount;

@RunWith(ConcurrentTestRunner.class)
public class MultiuserConcurencyTest {
	
	private class CheckoutObject {
		private ConcurrentHashMap<Integer, Integer> mUserDocuments; //user_id : document_id;
		
		private ArrayList<Integer> mUserIds;
		
		
		
		public CheckoutObject(ArrayList<Integer> user_ids) {
			mUserDocuments = new ConcurrentHashMap<Integer, Integer>();
			mUserIds = user_ids;
		}
		
		public synchronized Integer getNextId() {
			return mUserIds.remove(0);
		}
		
		public void insertCheckedoutDocument(Integer user_id, Integer document_id) {
			mUserDocuments.put(user_id, document_id);
		}
		
		public ConcurrentHashMap<Integer, Integer> getUserDocuments() {
			return mUserDocuments;
		}
	}
	
    private static String type;
    private static String database_server; 
    private static String port;
    private static String database;
    private static String database_connection;
    private static String user_name;
    private static String password;
    private final static int THREAD_COUNT = 20;
    
    /**
     * This is a static variable to handle multiple teardown tests within this suite.
     * 1: users get unique checkouts, 600 second cooldown, should result in no duplicate ids. 
     * 2: 0 CD, all documents should be the same id.
     * 3: users request checkouts twice, 600 second cooldown, should result in 2 CheckoutObject's with identical pairings. 
     */
    private static CheckoutObject mCheckoutObject;
    private static CheckoutObject mCheckoutObjectComp;
    
    /**
     * This sets up the database. Multiuser requires a fair amount of setup.
     * It requires a users, status, a project, a category, and multiple documents to classify.   
     * @throws SQLException
     */
    @SuppressWarnings("unchecked")
	@Before
    public void setUp() throws SQLException {
        type = "pgsql";
        database_server = "localhost";
        port = "5432";
        database = "db_multiuser_concurency_testing";
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
        /*String insert_user_str = String.format("insert into users" +
        		"	(id, username, email, first_name, last_name)" +
        		"	values" +
        		"	(14, 'nlp_stack', 'admin@nlp_stack.com', null, null),"
        		+ " (1, 'user1', 'user1@nlp_stack.com', null, null),"
        		+ " (2, 'user2', 'user2@nlp_stack.com', null, null);");
        s.execute(insert_user_str);*/
        
        for (int x = 0; x < THREAD_COUNT; x++) {
        	String insert_value = String.format("('user%s', 'user%s@nlp_stack.com', null, null);", x, x);
        	String insert_document_text = String.format("insert into users" +
            		"	(username, email, first_name, last_name)" +
            		"	values" +
            		insert_value);
        	s.execute(insert_document_text);
        }
        
        s = c.createStatement();
        String select_statement = "Select id from users order by random()";
        s.execute(select_statement);
        ResultSet rs = s.getResultSet();
        ArrayList<Integer> user_ids = new ArrayList<Integer>();
        while (rs.next()) {
        	Integer user_id = rs.getInt(1);
        	user_ids.add(user_id);
        }
        
        mCheckoutObject = new CheckoutObject((ArrayList<Integer>)user_ids.clone());
        mCheckoutObjectComp = new CheckoutObject((ArrayList<Integer>)user_ids.clone());
        
        s = c.createStatement();
        String insert_status = String.format("insert into multiuser_document_classification_status_labels" +
        		"	(id, status_text, next_id, is_ready_for_review, is_initialize)" +
        		"	values" +
        		"   (1, 'initial', 2, false, true)," +
        		"   (2, 'reviewer 1', 3, false, false)," +
        		"   (3, 'reviewer 2', 4, false, false)," +
        		"   (4, 'ready for reivew', null, true, false);");
        s.execute(insert_status);
        
        //inserting a new project with nlp_stack as the admin, 4 as the ready for review
        //and a checkout timeout of 2 seconds.
        s = c.createStatement();
        String insert_project = String.format("insert into projects" +
        		"	(\"name\", owner_user_id, ready_for_review_id, checkout_timeout)" +
        		"	values" +
        		"	('testing project', 14, 4, 60);");
        s.execute(insert_project);
        
        s = c.createStatement();
        String insert_category = String.format("insert into categories" +
        		"	(category)" +
        		"	values" +
        		"	('test cat');");
        s.execute(insert_category);
        
        s = c.createStatement();
        String insert_processed_file_text = String.format("insert into completed_processed_files" +
        		"	(id, file_path, is_complete)" +
        		"	values" +
        		"	(1, 'file path', false);");
        s.execute(insert_processed_file_text);
        
        s = c.createStatement();
        
        LoremIpsum li = new LoremIpsum();
        for (int x = 0; x < THREAD_COUNT; x++) {
        	String li_pars = li.getParagraphs(2);
        	String insert_value = String.format("(%s, '%s', 1, '%s', true, %s);", x, li_pars, li_pars.toLowerCase(), li_pars.split(" ").length);
        	String insert_document_text = String.format("insert into document_text" +
            		"	(original_document_id, original_document_text, completed_processed_files_id, processed_text, processed, num_tokens)" +
            		"	values" +
            		insert_value);
        	s.execute(insert_document_text);
        }
        /*String insert_document_text = String.format("insert into document_text" +
        		"	(id, original_document_id, original_document_text, completed_processed_files_id, processed_text, processed, num_tokens)" +
        		"	values" +
        		"	(1, 1, 'Test Document One', 1, 'test document one', true, 3)," +
        		"   (2, 2, 'Test Document Two', 1, 'test document two',  true, 3)," +
        		"   (3, 3, 'Test Document Three', 1, 'test document three', true, 3)," +
        		"   (4,4, 'Test Document Four', 1, 'test document four', true, 3)," +
        		"   (5, 5, 'Test Document Five', 1, 'test document five', true, 3);");
        s.execute(insert_document_text);*/
        c.close();
    }
    
    public void testTearDown() {
    	assertTrue("user document hashtable has no entries.", mCheckoutObject.getUserDocuments().size() > 0);
    	
    	HashMap<Integer, Integer> document_count = new HashMap<Integer, Integer>();
    	
    	
    	@SuppressWarnings("unchecked")
		List<Integer> keys = EnumerationUtils.toList(mCheckoutObject.getUserDocuments().keys());
    	
    	for (Integer user_id: keys) {
    		Integer document_id = mCheckoutObject.getUserDocuments().get(user_id);
    		if (!document_count.containsKey(document_id)) {
    			document_count.put(document_id, 0);
    		}
    		document_count.put(document_id, document_count.get(document_id) + 1);
    		assertTrue(String.format("document_id: %s has more than 1 count.", document_id), document_count.get(document_id) == 1);
    	}
    }
    
    @After
    public void tearDown() throws SQLException {
		testTearDown();
    	
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
    @ThreadCount(THREAD_COUNT)
    public void testDocumentCheckout() throws Throwable {
    	Connection connection = DriverManager.getConnection(database_connection, "ben", "password");
    	Integer user_id = mCheckoutObject.getNextId();
    	MultiuserResponse response = DatabaseHelper.MultiuserCheckoutDocument(connection, user_id);
    	mCheckoutObject.insertCheckedoutDocument(user_id, response.getDocumentId());
    	connection.close();
    }
    
}


