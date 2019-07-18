package job_queue;

public class JobStatus {
	
	public static final String JOB_STATUS_NOT_STARTED = "not started";
	public static final String JOB_STATUS_STARTED = "started";
	public static final String JOB_STATUS_RUNNING = "running";
	public static final String JOB_STATUS_CLEANING_UP = "cleaning up";
	public static final String JOB_STATUS_FINISHED = "finished";
	
	private Integer mJobStatusId;
	private String mJobStatusLabel;
	private Integer mNextStatusId;
	
	public JobStatus() {
		mJobStatusId = 0;
		mJobStatusLabel = "";
		mNextStatusId = 0;
	}
	
	public JobStatus(Integer job_status_id, String job_status_label, Integer next_status_id) {
		mJobStatusId = job_status_id;
		mJobStatusLabel = job_status_label;
		mNextStatusId = next_status_id;
	}
	
	public Integer getJobStatusId() {
		return mJobStatusId;
	}
	
	public void setJobStatusId(Integer job_status_id) {
		mJobStatusId = job_status_id;
	}
	
	public String getJobStatusLabel() {
		return mJobStatusLabel;
	}
	
	public void setJobStatusLabel(String job_status_label) {
		mJobStatusLabel = job_status_label;
	}
	
	public Integer getNextStatusId() {
		return mNextStatusId;
	}
	
	public void setNextStatusId(Integer next_status_id) {
		 mNextStatusId = next_status_id;
	}

}
