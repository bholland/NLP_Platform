package helper;
	
import org.junit.Test;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.factory.Nd4j;
import org.postgresql.util.PSQLException;

import de.svenjacobs.loremipsum.LoremIpsum;
import descriptors.PostProcessor_CosDistance;
import junit.framework.TestCase;
import job_queue.JobParameter;
import job_queue.JobQueue;
import job_queue.JobStatus;
import job_queue.JobType;
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

public class DatabaseHelperJobQueueTest {
    private static String type;
    private static String database_server; 
    private static String port;
    private static String database;
    private static String database_connection;
    private static String user_name;
    private static String password;
    private static Integer user_id;
    
    @Before
    public void setUp() throws SQLException {
        type = "pgsql";
        database_server = "localhost";
        port = "5432";
        database = "db_helper_job_queue_testing";
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
        user_id = 14;
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

    @Test
    public void insertJobStatusNotStartedTest() throws SQLException {
    	try (Connection connection = DriverManager.getConnection(database_connection, "ben", "password")) {
           DatabaseHelper.insertJobStatus(connection, user_id, JobStatus.JOB_STATUS_NOT_STARTED);
           
           ArrayList<JobStatus> job_list = DatabaseHelperSharedFunctions.verifyJobStatus(connection);
           assertEquals(1, job_list.size());
           
           JobStatus job = job_list.get(0);
           assertEquals(JobStatus.JOB_STATUS_NOT_STARTED, job.getJobStatusLabel());
           assertEquals(null, job.getNextStatusId());
        }
    }
    
    @Test
    public void insertJobStatusStartedTest() throws SQLException {
    	try (Connection connection = DriverManager.getConnection(database_connection, "ben", "password")) {
           DatabaseHelper.insertJobStatus(connection, user_id, JobStatus.JOB_STATUS_STARTED);
           
           ArrayList<JobStatus> job_list = DatabaseHelperSharedFunctions.verifyJobStatus(connection);
           assertEquals(1, job_list.size());
           
           JobStatus job = job_list.get(0);
           assertEquals(JobStatus.JOB_STATUS_STARTED, job.getJobStatusLabel());
           assertEquals(null, job.getNextStatusId());
        }
    }
    
    @Test
    public void insertJobStatusRunningTest() throws SQLException {
    	try (Connection connection = DriverManager.getConnection(database_connection, "ben", "password")) {
           DatabaseHelper.insertJobStatus(connection, user_id, JobStatus.JOB_STATUS_RUNNING);
           
           ArrayList<JobStatus> job_list = DatabaseHelperSharedFunctions.verifyJobStatus(connection);
           assertEquals(1, job_list.size());
           
           JobStatus job = job_list.get(0);
           assertEquals(JobStatus.JOB_STATUS_RUNNING, job.getJobStatusLabel());
           assertEquals(null, job.getNextStatusId());
        }
    }
    
    @Test
    public void insertJobStatusCleaningUpTest() throws SQLException {
    	try (Connection connection = DriverManager.getConnection(database_connection, "ben", "password")) {
           DatabaseHelper.insertJobStatus(connection, user_id, JobStatus.JOB_STATUS_CLEANING_UP);
           
           ArrayList<JobStatus> job_list = DatabaseHelperSharedFunctions.verifyJobStatus(connection);
           assertEquals(1, job_list.size());
           
           JobStatus job = job_list.get(0);
           assertEquals(JobStatus.JOB_STATUS_CLEANING_UP, job.getJobStatusLabel());
           assertEquals(null, job.getNextStatusId());
        }
    }
    
    @Test
    public void insertJobStatusFinishedTest() throws SQLException {
    	try (Connection connection = DriverManager.getConnection(database_connection, "ben", "password")) {
           DatabaseHelper.insertJobStatus(connection, user_id, JobStatus.JOB_STATUS_FINISHED);
           
           ArrayList<JobStatus> job_list = DatabaseHelperSharedFunctions.verifyJobStatus(connection);
           assertEquals(1, job_list.size());
           
           JobStatus job = job_list.get(0);
           assertEquals(JobStatus.JOB_STATUS_FINISHED, job.getJobStatusLabel());
           assertEquals(null, job.getNextStatusId());
        }
    }
    
    @Test(expected = SQLException.class)
    public void insertJobStatusDuplicateTest() throws SQLException {
    	try (Connection connection = DriverManager.getConnection(database_connection, "ben", "password")) {
           DatabaseHelper.insertJobStatus(connection, user_id, JobStatus.JOB_STATUS_FINISHED);
           DatabaseHelper.insertJobStatus(connection, user_id, JobStatus.JOB_STATUS_FINISHED);
    	}
    }
    
    @Test
    public void insertJobTypesDocumentTest() throws SQLException {
    	try (Connection connection = DriverManager.getConnection(database_connection, "ben", "password")) {
           DatabaseHelper.insertJobTypes(connection, user_id, JobType.JOB_PROCESS_DOCUMENT);
           
           Statement s = connection.createStatement();
           String select_stmt = "Select id, job_label from job_types";
           s.executeQuery(select_stmt);
           
           ResultSet rs = s.getResultSet();
           while (rs.next()) {
        	   int id = rs.getInt(1);
        	   assertTrue(id > 0);
        	   String job_label = rs.getString(2);
        	   assertEquals(JobType.JOB_PROCESS_DOCUMENT, job_label);
           }
        }
    }
    
    @Test(expected = SQLException.class)
    public void insertJobTypesDocumentDuplicateTest() throws SQLException {
    	try (Connection connection = DriverManager.getConnection(database_connection, "ben", "password")) {
           DatabaseHelper.insertJobTypes(connection, user_id, JobType.JOB_PROCESS_DOCUMENT);
           DatabaseHelper.insertJobTypes(connection, user_id, JobType.JOB_PROCESS_DOCUMENT);
    	}
    }
    
    @Test
    public void insertJobStatusNextTest() throws SQLException {
    	try (Connection connection = DriverManager.getConnection(database_connection, "ben", "password")) {
           int not_started = DatabaseHelper.insertJobStatus(connection, user_id, JobStatus.JOB_STATUS_NOT_STARTED);
           int started = DatabaseHelper.insertJobStatus(connection, user_id, JobStatus.JOB_STATUS_STARTED);
           
           DatabaseHelper.insertJobStatusNext(connection, user_id, not_started, started);
           
           ArrayList<JobStatus> job_list = DatabaseHelperSharedFunctions.verifyJobStatus(connection);
           assertEquals(2, job_list.size());
           
           boolean has_not_started = false;
           boolean has_started = false;
           
           for (JobStatus js : job_list) {
        	   if (js.getJobStatusLabel().equals(JobStatus.JOB_STATUS_NOT_STARTED)) {
        		   has_not_started = true;
        		   assertTrue(js.getNextStatusId() != null);
        		   assertEquals(started, js.getNextStatusId().intValue());
        	   }
        	   else if (js.getJobStatusLabel().equals(JobStatus.JOB_STATUS_STARTED)) {
        		   has_started = true;
        		   assertEquals(null, js.getNextStatusId());
        	   }
        	   else {
        		   assertTrue(String.format("Only '%s' and '%s' are allowed but got '%s'", JobStatus.JOB_STATUS_NOT_STARTED, JobStatus.JOB_STATUS_STARTED, js.getJobStatusLabel()), false); 
        	   }
           }
           assertTrue(has_not_started);
           assertTrue(has_started);
        }
    }
    
    @Test
    public void selectJobStausListTest() throws SQLException {
    	try (Connection connection = DriverManager.getConnection(database_connection, "ben", "password")) {
           int not_started = DatabaseHelper.insertJobStatus(connection, user_id, JobStatus.JOB_STATUS_NOT_STARTED);
           int started = DatabaseHelper.insertJobStatus(connection, user_id, JobStatus.JOB_STATUS_STARTED);
           int running = DatabaseHelper.insertJobStatus(connection, user_id, JobStatus.JOB_STATUS_RUNNING);
           
           DatabaseHelper.insertJobStatusNext(connection, user_id, not_started, started);
           DatabaseHelper.insertJobStatusNext(connection, user_id, started, running);
           
           ArrayList<JobStatus> job_list = DatabaseHelper.selectJobStausList(connection, user_id);
           assertEquals(3, job_list.size());
           
           boolean has_not_started = false;
           boolean has_started = false;
           boolean has_running = false;
           
           for (JobStatus js : job_list) {
        	   if (js.getJobStatusLabel().equals(JobStatus.JOB_STATUS_NOT_STARTED)) {
        		   has_not_started = true;
        		   assertTrue(js.getNextStatusId() != null);
        		   assertEquals(started, js.getNextStatusId().intValue());
        	   }
        	   else if (js.getJobStatusLabel().equals(JobStatus.JOB_STATUS_STARTED)) {
        		   has_started = true;
        		   assertTrue(js.getNextStatusId() != null);
        		   assertEquals(running, js.getNextStatusId().intValue());
        	   } else if (js.getJobStatusLabel().equals(JobStatus.JOB_STATUS_RUNNING)) {
        		   has_running = true;
        		   assertTrue(js.getNextStatusId() == null);
        	   } else {
        		   assertTrue(false);
        	   }
           }
           assertTrue(has_not_started);
           assertTrue(has_started);
           assertTrue(has_running);
        }
    }
    
    @Test
    public void insertNewJobTest() throws SQLException {
    	try (Connection connection = DriverManager.getConnection(database_connection, "ben", "password")) {
            int not_started = DatabaseHelper.insertJobStatus(connection, user_id, JobStatus.JOB_STATUS_NOT_STARTED);
            int started = DatabaseHelper.insertJobStatus(connection, user_id, JobStatus.JOB_STATUS_STARTED);
            int running = DatabaseHelper.insertJobStatus(connection, user_id, JobStatus.JOB_STATUS_RUNNING);
            
            int job_process_document = DatabaseHelper.insertJobTypes(connection, user_id, JobType.JOB_PROCESS_DOCUMENT);
            
            
            DatabaseHelper.insertNewJob(connection, user_id, not_started, job_process_document);
            
            Statement s = connection.createStatement();
            String select_stmt = "Select id, user_id, job_type_id, job_status_id from job_queue";
            s.executeQuery(select_stmt);
            
            ResultSet rs = s.getResultSet();
            while (rs.next()) {
         	   int id = rs.getInt(1);
         	   assertTrue(id > 0);
         	   int job_user_id = rs.getInt(2);
         	   assertEquals(user_id.intValue(), job_user_id);
         	   int job_type_id = rs.getInt(3);
         	   assertEquals(job_process_document, job_type_id);
         	   int job_status_id = rs.getInt(4);
         	   assertEquals(not_started, job_status_id);
            }
    	}
    }
    
    @Test
    public void selectJobFromIdTest() throws SQLException {
    	try (Connection connection = DriverManager.getConnection(database_connection, "ben", "password")) {
            int not_started = DatabaseHelper.insertJobStatus(connection, user_id, JobStatus.JOB_STATUS_NOT_STARTED);
            
            int job_process_document = DatabaseHelper.insertJobTypes(connection, user_id, JobType.JOB_PROCESS_DOCUMENT);
            
            int queue_job = DatabaseHelper.insertNewJob(connection, user_id, not_started, job_process_document);
            
            JobQueue job = DatabaseHelper.selectJobFromId(connection, user_id, queue_job);
            
            assertEquals(queue_job, job.getJobQueueId().intValue());
            assertEquals(JobType.JOB_PROCESS_DOCUMENT, job.getJobLabel());
    	}
    }
    
    @Test
    public void deleteJobStausTest() throws SQLException {
    	try (Connection connection = DriverManager.getConnection(database_connection, "ben", "password")) {
            int not_started = DatabaseHelper.insertJobStatus(connection, user_id, JobStatus.JOB_STATUS_NOT_STARTED);
            ArrayList<JobStatus> job_list = DatabaseHelperSharedFunctions.verifyJobStatus(connection);
            assertEquals(1, job_list.size());
            
            DatabaseHelper.deleteJobStatus(connection, user_id);
            ArrayList<JobStatus> job_list_delete = DatabaseHelperSharedFunctions.verifyJobStatus(connection);
            assertEquals(0, job_list_delete.size());
    	}
    }
    
    @Test
    public void selectJobTypesListTest() throws SQLException {
    	try (Connection connection = DriverManager.getConnection(database_connection, "ben", "password")) {
            int not_started = DatabaseHelper.insertJobStatus(connection, user_id, JobStatus.JOB_STATUS_NOT_STARTED);
            int job_process_document = DatabaseHelper.insertJobTypes(connection, user_id, JobType.JOB_PROCESS_DOCUMENT);
            ArrayList<JobStatus> job_list = DatabaseHelperSharedFunctions.verifyJobStatus(connection);
            assertEquals(1, job_list.size());
            
            ArrayList<JobType> job_type_list = DatabaseHelper.selectJobTypesList(connection, user_id);
            assertEquals(1, job_type_list.size());
            
            JobType jt = job_type_list.get(0);
            assertEquals(JobType.JOB_PROCESS_DOCUMENT, jt.getJobTypeLabel());
    	}
    }
    
    @Test
    public void deleteJobTypesTest() throws SQLException {
    	try (Connection connection = DriverManager.getConnection(database_connection, "ben", "password")) {
    		int job_process_document = DatabaseHelper.insertJobTypes(connection, user_id, JobType.JOB_PROCESS_DOCUMENT);
    		int job_create_model = DatabaseHelper.insertJobTypes(connection, user_id, JobType.JOB_CREATE_MODEL);
            
    		ArrayList<JobType> job_type_list = DatabaseHelper.selectJobTypesList(connection, user_id);
            assertEquals(2, job_type_list.size());
    		
            DatabaseHelper.deleteJobTypes(connection, user_id);
            
            job_type_list = DatabaseHelper.selectJobTypesList(connection, user_id);
            assertEquals(0, job_type_list.size());
    	}
    }
    
    @Test
    public void insertJobParameterParamTest() throws SQLException {
    	try (Connection connection = DriverManager.getConnection(database_connection, "ben", "password")) {
    		int not_started = DatabaseHelper.insertJobStatus(connection, user_id, JobStatus.JOB_STATUS_NOT_STARTED);
            int job_process_document = DatabaseHelper.insertJobTypes(connection, user_id, JobType.JOB_PROCESS_DOCUMENT);
            int job_queue_id = DatabaseHelper.insertNewJob(connection, user_id, job_process_document, not_started);
            int job_param = DatabaseHelper.insertJobParameter(connection, user_id, job_queue_id, "test_param_1", "test_value_1", null);
            assertTrue(job_param > 0);
            
            Statement s = connection.createStatement();
            String select_stmt = "Select id, job_queue_id, param_name, param_value, param_object_oid from job_additional_parameters";
            s.executeQuery(select_stmt);
            
            ResultSet rs = s.getResultSet();
            int num_rows = 0;
            while (rs.next()) {
               num_rows++;
         	   int id = rs.getInt(1);
         	   assertTrue(id > 0);
         	   int new_job_queue_id = rs.getInt(2);
         	   assertEquals(job_queue_id, new_job_queue_id);
         	   String param_name = rs.getString(3);
         	   assertEquals("test_param_1", param_name);
         	   String param_value = rs.getString(4);
        	   if (rs.wasNull()) {
        		   assertTrue("Param was null and should be test_param_1.", false);
        	   }
         	   assertEquals("test_value_1", param_value);
        	   
        	   
        	   Long param_object_oid = rs.getLong(5);
        	   if (!rs.wasNull()) {
        		   assertTrue("The OID should be null.", false);
        	   }
            }
    	}
    }
    
    @Test
    public void selectJobParametersFromJobIdTest() throws SQLException {
    	try (Connection connection = DriverManager.getConnection(database_connection, "ben", "password")) {
    		int not_started = DatabaseHelper.insertJobStatus(connection, user_id, JobStatus.JOB_STATUS_NOT_STARTED);
            int job_process_document = DatabaseHelper.insertJobTypes(connection, user_id, JobType.JOB_PROCESS_DOCUMENT);
            int job_queue_id = DatabaseHelper.insertNewJob(connection, user_id, job_process_document, not_started);
            int job_param = DatabaseHelper.insertJobParameter(connection, user_id, job_queue_id, "test_param_1", "test_value_1", null);
            assertTrue(job_param > 0);
            
            ArrayList<JobParameter> param_list = DatabaseHelper.selectJobParametersFromJobId(connection, user_id, job_queue_id);
            assertEquals(1, param_list.size());
            JobParameter param = param_list.get(0);
            assertEquals(job_queue_id, param.getmJobQueueId().intValue());
            assertEquals("test_param_1", param.getmParamName());
            assertEquals("test_value_1", param.getmParamValue());
            assertEquals(null, param.getmParamObjectOID());
            
    	}
    }
    
    @Test
    public void deleteJobParamsForJobIdTest() throws SQLException {
    	try (Connection connection = DriverManager.getConnection(database_connection, "ben", "password")) {
    		int not_started = DatabaseHelper.insertJobStatus(connection, user_id, JobStatus.JOB_STATUS_NOT_STARTED);
            int job_process_document = DatabaseHelper.insertJobTypes(connection, user_id, JobType.JOB_PROCESS_DOCUMENT);
            int job_queue_id = DatabaseHelper.insertNewJob(connection, user_id, job_process_document, not_started);
            int job_param = DatabaseHelper.insertJobParameter(connection, user_id, job_queue_id, "test_param_1", "test_value_1", null);
            assertTrue(job_param > 0);
            
            ArrayList<JobParameter> param_list = DatabaseHelper.selectJobParametersFromJobId(connection, user_id, job_queue_id);
            assertEquals(1, param_list.size());
            JobParameter param = param_list.get(0);
            assertEquals(job_queue_id, param.getmJobQueueId().intValue());
            assertEquals("test_param_1", param.getmParamName());
            assertEquals("test_value_1", param.getmParamValue());
            assertEquals(null, param.getmParamObjectOID());
            
            DatabaseHelper.deleteJobParamsForJobId(connection, user_id, job_queue_id);
            param_list = DatabaseHelper.selectJobParametersFromJobId(connection, user_id, job_queue_id);
            assertEquals(0, param_list.size());
            
    	}
    }
    
    @Test
    public void getNextJobTest() throws SQLException {
    	try (Connection connection = DriverManager.getConnection(database_connection, "ben", "password")) {
            int not_started = DatabaseHelper.insertJobStatus(connection, user_id, JobStatus.JOB_STATUS_NOT_STARTED);
            int started = DatabaseHelper.insertJobStatus(connection, user_id, JobStatus.JOB_STATUS_STARTED);
            int running = DatabaseHelper.insertJobStatus(connection, user_id, JobStatus.JOB_STATUS_RUNNING);
            DatabaseHelper.insertJobStatusNext(connection, user_id, not_started, started);
            DatabaseHelper.insertJobStatusNext(connection, user_id, started, running);
            
            int job_process_document = DatabaseHelper.insertJobTypes(connection, user_id, JobType.JOB_PROCESS_DOCUMENT);
            
            
            DatabaseHelper.insertNewJob(connection, user_id, not_started, job_process_document);
            
            Statement s = connection.createStatement();
            String select_stmt = "Select id, user_id, job_type_id, job_status_id from job_queue";
            s.executeQuery(select_stmt);
            
            ResultSet rs = s.getResultSet();
            while (rs.next()) {
         	   int id = rs.getInt(1);
         	   assertTrue(id > 0);
         	   int job_user_id = rs.getInt(2);
         	   assertEquals(user_id.intValue(), job_user_id);
         	   int job_type_id = rs.getInt(3);
         	   assertEquals(job_process_document, job_type_id);
         	   int job_status_id = rs.getInt(4);
         	   assertEquals(not_started, job_status_id);
            }
            
            DatabaseHelper.getNextJob(connection, user_id, not_started);
            
            s = connection.createStatement();
            select_stmt = "Select id, user_id, job_type_id, job_status_id from job_queue";
            s.executeQuery(select_stmt);
            
            rs = s.getResultSet();
            while (rs.next()) {
         	   int id = rs.getInt(1);
         	   assertTrue(id > 0);
         	   int job_user_id = rs.getInt(2);
         	   assertEquals(user_id.intValue(), job_user_id);
         	   int job_type_id = rs.getInt(3);
         	   assertEquals(job_process_document, job_type_id);
         	   int job_status_id = rs.getInt(4);
         	   assertEquals(started, job_status_id);
            }
            
    	}
    }
    
    @Test
    public void getNextJobWithTypeTest() throws SQLException {
    	try (Connection connection = DriverManager.getConnection(database_connection, "ben", "password")) {
            int not_started = DatabaseHelper.insertJobStatus(connection, user_id, JobStatus.JOB_STATUS_NOT_STARTED);
            int started = DatabaseHelper.insertJobStatus(connection, user_id, JobStatus.JOB_STATUS_STARTED);
            int running = DatabaseHelper.insertJobStatus(connection, user_id, JobStatus.JOB_STATUS_RUNNING);
            DatabaseHelper.insertJobStatusNext(connection, user_id, not_started, started);
            DatabaseHelper.insertJobStatusNext(connection, user_id, started, running);
            
            int job_process_document = DatabaseHelper.insertJobTypes(connection, user_id, JobType.JOB_PROCESS_DOCUMENT);
            int job_create_model = DatabaseHelper.insertJobTypes(connection, user_id, JobType.JOB_CREATE_MODEL);
            
            
            DatabaseHelper.insertNewJob(connection, user_id, not_started, job_process_document);
            
            Statement s = connection.createStatement();
            String select_stmt = "Select id, user_id, job_type_id, job_status_id from job_queue";
            s.executeQuery(select_stmt);
            
            ResultSet rs = s.getResultSet();
            while (rs.next()) {
         	   int id = rs.getInt(1);
         	   assertTrue(id > 0);
         	   int job_user_id = rs.getInt(2);
         	   assertEquals(user_id.intValue(), job_user_id);
         	   int job_type_id = rs.getInt(3);
         	   assertEquals(job_process_document, job_type_id);
         	   int job_status_id = rs.getInt(4);
         	   assertEquals(not_started, job_status_id);
            }
            
            DatabaseHelper.getNextJob(connection, user_id, not_started, job_process_document);
            
            s = connection.createStatement();
            select_stmt = "Select id, user_id, job_type_id, job_status_id from job_queue";
            s.executeQuery(select_stmt);
            
            rs = s.getResultSet();
            while (rs.next()) {
         	   int id = rs.getInt(1);
         	   assertTrue(id > 0);
         	   int job_user_id = rs.getInt(2);
         	   assertEquals(user_id.intValue(), job_user_id);
         	   int job_type_id = rs.getInt(3);
         	   if (job_type_id == job_process_document) {
         		   int job_status_id = rs.getInt(4);
         		   assertEquals(started, job_status_id);
         	   } else if (job_type_id == job_create_model) {
         		   int job_status_id = rs.getInt(4);
         		   assertEquals(not_started, job_status_id);
         	   }
            }
    	}
    }
    
    /**
     * 
     * Integer getNextJob(Connection connection, Integer user_id, Integer not_started_status_id)
	 * @throws SQLException 
     * 
     */
    
    /**
     * @TODO: test out deleting jobs from the job queue. 
     */

}
