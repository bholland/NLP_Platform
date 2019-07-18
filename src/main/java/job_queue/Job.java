package job_queue;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.HashMap;

public interface Job {
	/**
	 * This map to specific values within the XML. 
	 * Ensure that if these values map to meanings that change within XML, update it here.  
	 */
	public static final int JOB_QUEUE_DISABLED = 0;
	public static final int JOB_QUEUE_INSERT = 1;
	public static final int JOB_QUEUE_PROCESS = 2;
	
	public void getNextJob(Connection connection, Integer user_id) throws SQLException;
	public HashMap<String, String> getJobParameters(Connection connection, Integer user_id, Integer job_queue_id) throws SQLException;
	
	public void createJob(Connection connection, Integer user_id) throws SQLException;
	public void setJobParameters(Connection connection, Integer user_id, Integer job_queue_id, HashMap<String, String> params) throws SQLException;
}
