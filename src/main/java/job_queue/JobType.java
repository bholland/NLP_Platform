package job_queue;

public class JobType {
	public static final String JOB_PROCESS_DOCUMENT = "process_document";
	public static final String JOB_CREATE_MODEL = "create_model";
	
	private Integer mJobTypeId;
	private String mJobTypeLabel;
	
	public JobType() {
		mJobTypeId = 0;
		mJobTypeLabel = "";
	}
	
	public JobType(Integer job_type_id, String job_type_label) {
		mJobTypeId = job_type_id;
		mJobTypeLabel = job_type_label;
		
	}
	
	public Integer getJobTypeId() {
		return mJobTypeId;
	}
	
	public void setJobTypeId(Integer job_type_id) {
		mJobTypeId = job_type_id;
	}
	
	public String getJobTypeLabel() {
		return mJobTypeLabel;
	}
	
	public void setJobTypeLabel(String job_type_label) {
		mJobTypeLabel = job_type_label;
	}
}
