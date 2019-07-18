package helper;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.HashMap;

import job_queue.JobStatus;

public class DatabaseHelperSharedFunctions {
	/**
     * @param connection: SQL connection
     * @param expected_num_rows: The expected number of rows
     * @param expected_user_id: The user_id for all entries. Note that this assumed a single user_id for all log records. 
     * @param expected_called_procedures: The array of stored procedures that should exist within the logging table.  
     * @throws SQLException
     */
    public static void TestLogging(Connection connection, Integer expected_num_rows, Integer expected_user_id, String[] expected_called_procedures) throws SQLException {
    	HashMap<String, Boolean> called_procedures = new HashMap<String, Boolean>();
    	for (String s: expected_called_procedures) {
    		called_procedures.put(s, false);
    	}
    	
    	Statement s = connection.createStatement();
        ResultSet rs = s.executeQuery(String.format("SELECT id, create_date, user_id, called_procedure, previous_state FROM logging"));
        Integer row_count = 0;
        Integer user_id = null;
        String called_procedure = null;
        while (rs.next()) {
            row_count += 1;
            Integer row_id = rs.getInt(1);
            Timestamp create_date = rs.getTimestamp(2);
            LocalDateTime timestamp = create_date.toLocalDateTime();
            
            ZonedDateTime local_timestamp = ZonedDateTime.of(timestamp, ZoneId.systemDefault());
            ZonedDateTime local_now = ZonedDateTime.now();
            
            Duration time_diff = Duration.between(local_timestamp, local_now);
            long diff_seconds = time_diff.abs().getSeconds();
            //assertTrue("Diff seconds are unreasonable.", diff_seconds < 10);
            
            user_id = rs.getInt(3);
            called_procedure = rs.getString(4);
            assertTrue(String.format("Called procedure not in expected list: %s", called_procedure), called_procedures.containsKey(called_procedure));
            called_procedures.replace(called_procedure, true);
        }
        assertEquals("Row counts are equal", row_count, expected_num_rows);
        if (row_count > 0) {
        	assertEquals("User id's are not equal.", user_id, expected_user_id);
        	for (String key: called_procedures.keySet()) {
        		assertTrue(String.format("key: %s is not in log.", key), called_procedures.get(key));
        	}
        }
    }
    
    public static void verifyDocumentText(Connection connection, String label, String text) throws SQLException {
        Statement s = connection.createStatement();
        ResultSet rs = s.executeQuery(String.format("SELECT id, original_document_id, original_document_text, processed_text, processed, num_tokens FROM document_text"));
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
    
    public static void verifyDocumentTextSentence(Connection connection, String sentence_text) throws SQLException {
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
    
    public static void verifyCategoryTextSentence(Connection connection, String sentence_text) throws SQLException {
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
    
    public static void verifyCategoryText(Connection connection, String label, String text) throws SQLException {
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
    
    public static ArrayList<JobStatus> verifyJobStatus(Connection connection) throws SQLException {
    	Statement s = connection.createStatement();
        String select_stmt = "SELECT id, job_status_label, next_status_id FROM job_status;";
        s.execute(select_stmt);
        
    	ResultSet rs = s.getResultSet();
    	ArrayList<JobStatus> ret = new ArrayList<JobStatus>();
    	while (rs.next()) {
    		
    		Integer id = rs.getInt(1);
    		String job_status_label = rs.getString(2);
    		Integer next_status_id = rs.getInt(3);
    		if (rs.wasNull()) {
    			next_status_id = null;
    		}
    		ret.add(new JobStatus(id, job_status_label, next_status_id));
    	}
    	return ret;
    }
}
