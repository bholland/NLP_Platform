package job_queue;

public class JobParameter {
	public static final String JOB_PROCESS_DOCUMENT = "process_document";
	public static final String JOB_CREATE_MODEL = "create_model";
	
	private Integer mJobQueueId;
	private String mParamName;
	private String mParamValue;
	private Long mParamObjectOID;
	
	public JobParameter() {
		mJobQueueId = 0;
		mParamName = "";
		mParamValue = null;
		mParamObjectOID = null;
		
	}
	
	public JobParameter(Integer job_queue_id, String param_name, String param_value, Long param_object_oid) {
		mJobQueueId = job_queue_id;
		mParamName = param_name;
		mParamValue = param_value;
		mParamObjectOID = param_object_oid;
		
	}

	public Integer getmJobQueueId() {
		return mJobQueueId;
	}

	public void setmJobQueueId(Integer mJobQueueId) {
		this.mJobQueueId = mJobQueueId;
	}

	public String getmParamName() {
		return mParamName;
	}

	public void setmParamName(String mParamName) {
		this.mParamName = mParamName;
	}

	public String getmParamValue() {
		return mParamValue;
	}

	public void setmParamValue(String mParamValue) {
		this.mParamValue = mParamValue;
	}

	public Long getmParamObjectOID() {
		return mParamObjectOID;
	}

	public void setmParamObjectOID(Long mParamObjectOID) {
		this.mParamObjectOID = mParamObjectOID;
	}
	
}
