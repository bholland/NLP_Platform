package job_queue;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;

import helper.DatabaseHelper;

public class JobQueue {
	
	Integer mJobQueueId;
	Integer mUserId;
	Integer mJobTypeId;
	String mJobLabel;
	Integer mJobStatusId;
	String mJobStatusLabel;
	Integer mNextStatusId;
	
	ArrayList<JobParameter> mJobParameters;
	
	public JobQueue () {
		mJobQueueId = null;
		mUserId = null;
		mJobTypeId = null;
		mJobStatusId = null;
		mJobStatusLabel = null;
		mNextStatusId = null;
		mJobParameters = null;
	}

	public Integer getJobQueueId() {
		return mJobQueueId;
	}

	public void setJobQueueId(Integer mJobQueueId) {
		this.mJobQueueId = mJobQueueId;
	}

	public Integer getUserId() {
		return mUserId;
	}

	public void setUserId(Integer mUserId) {
		this.mUserId = mUserId;
	}

	public Integer getJobTypeId() {
		return mJobTypeId;
	}

	public void setJobTypeId(Integer mJobTypeId) {
		this.mJobTypeId = mJobTypeId;
	}

	public String getJobLabel() {
		return mJobLabel;
	}

	public void setJobLabel(String mJobLabel) {
		this.mJobLabel = mJobLabel;
	}

	public Integer getJobStatusId() {
		return mJobStatusId;
	}

	public void setJobStatusId(Integer mJobStatusId) {
		this.mJobStatusId = mJobStatusId;
	}

	public String getJobStatusLabel() {
		return mJobStatusLabel;
	}

	public void setJobStatusLabel(String mJobStatusLabel) {
		this.mJobStatusLabel = mJobStatusLabel;
	}

	public Integer getNextStatusId() {
		return mNextStatusId;
	}

	public void setNextStatusId(Integer mNextStatusId) {
		this.mNextStatusId = mNextStatusId;
	}

	public ArrayList<JobParameter> getJobParameters() {
		return mJobParameters;
	}

	public void setJobParameters(ArrayList<JobParameter> mJobParameters) {
		this.mJobParameters = mJobParameters;
	}
}
