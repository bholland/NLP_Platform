package project;

import java.util.ArrayList;

public abstract class Project_ImplBase {
	public final String STATUS_INIT = "initialize";
	public final String STATUS_FIRST_REVIEWER = "first reviewed";
	public final String STATUS_FINAL_REVIEW = "ready for review";
	
	public final String PROJECT_DUAL_VALIDATION = "dual_validation"; 
	
	protected ArrayList<DocumentClassificationStatus> mClassificationStatus;
	protected String mName;
	protected Integer mReadyForReviewId;
	
}
