package project;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;

import helper.DatabaseHelper;

public class DualValidation extends Project_ImplBase {
	public final String STATUS_SECOND_REVIEWER = "second reviewed";
	
	
	public void CleanProject(Connection connection, int user_id) throws SQLException {
		DatabaseHelper.cleanProjects(connection, user_id);
	}
	
	public DualValidation(String name, Connection connection, Integer user_id) throws SQLException {
		mClassificationStatus = new ArrayList<DocumentClassificationStatus>();
		mName = name;
		
		Integer init = DatabaseHelper.insertMultiuserDocumentClassificationStatusLabel(connection, user_id, STATUS_INIT, false, true);
		Integer first = DatabaseHelper.insertMultiuserDocumentClassificationStatusLabel(connection, user_id, STATUS_FIRST_REVIEWER, false, false);
		Integer second = DatabaseHelper.insertMultiuserDocumentClassificationStatusLabel(connection, user_id, STATUS_SECOND_REVIEWER, false, false);
		mReadyForReviewId  = DatabaseHelper.insertMultiuserDocumentClassificationStatusLabel(connection, user_id, STATUS_FINAL_REVIEW, true, false);
		
		DatabaseHelper.insertMultiuserDocumentClassificationStatusNext(connection, user_id, init, first);
		DatabaseHelper.insertMultiuserDocumentClassificationStatusNext(connection, user_id, first, second);
		DatabaseHelper.insertMultiuserDocumentClassificationStatusNext(connection, user_id, second, mReadyForReviewId);
		
	}
	
	public DualValidation(String name) {
		mClassificationStatus = new ArrayList<DocumentClassificationStatus>();
		mName = name;
		mReadyForReviewId = null;
	}
	
	public Integer getReadyForReviewId() {
		return mReadyForReviewId;
	}
	
}
