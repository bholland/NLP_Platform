package descriptors;

import java.io.IOException;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;

import org.apache.uima.UimaContext;
import org.apache.uima.analysis_component.JCasAnnotator_ImplBase;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.cas.CAS;
import org.apache.uima.cas.CASException;
import org.apache.uima.collection.CollectionException;
import org.apache.uima.collection.CollectionReader_ImplBase;
import org.apache.uima.jcas.JCas;
import org.apache.uima.resource.ResourceInitializationException;
import org.apache.uima.util.Level;
import org.apache.uima.util.Progress;
import org.apache.uima.util.ProgressImpl;

import Classifiers.DocumentClassifier_Binary;
import database.DatabaseConnector;
import helper.DatabaseHelper;
import objects.DatabaseConnection;
import job_queue.JobStatus;
import job_queue.JobType;
import objects.NLPModel;
import objects.UnprocessedText;
import project.DualValidation;

/**
 * @author ben
 * This is the setup content processing engine. getNext() returns nothing, hasNext is false.
 * All setup for the database etc. happens within initialize commands.  
 */
public class Setup_CPE extends DatabaseCollectionReader_ImplBase {
    public static final String PROJECT_NAME = "ProjectName";
    public static final String PROJECT_TYPE = "ProjectType";
    public static final String CHECKOUT_TIMEOUT = "CheckoutTimeout";
    public static final String PROJECT_OWNER_USER_NAME = "ProjectOwnerUserName";
    public static final String PROJECT_OWNER_EMAIL = "ProjectOwnerEmail";
    public static final String PROJECT_OWNER_FIRST_NAME = "ProjectOwnerFirstName";
    public static final String PROJECT_OWNER_LAST_NAME = "ProjectOwnerLastName";
    
    private String mProjectName;
    private String mProjectType;
    private Integer mCheckoutTimeout;
    private String mProjectOwnerUserName;
    private String mProjectOwnerEmail;
    private String mProjectOwnerFirstName;
    private String mProjectOwnerLastName;
    private Integer mProjectUserId;
	
	
	public void setupJobStatus(Connection connection, Integer user_id) throws SQLException {
		/**
		 * @TODO: add a clean flag
		 * @TODO: Unit test this functionality
		 */
		DatabaseHelper.deleteJobStatus(connection, user_id);
		Integer not_started = DatabaseHelper.insertJobStatus(connection, user_id, JobStatus.JOB_STATUS_NOT_STARTED);
		Integer started = DatabaseHelper.insertJobStatus(connection, user_id, JobStatus.JOB_STATUS_STARTED);
		Integer running = DatabaseHelper.insertJobStatus(connection, user_id, JobStatus.JOB_STATUS_RUNNING);
		Integer cleaning_up = DatabaseHelper.insertJobStatus(connection, user_id, JobStatus.JOB_STATUS_CLEANING_UP);
		Integer fininshed = DatabaseHelper.insertJobStatus(connection, user_id, JobStatus.JOB_STATUS_FINISHED);
		
		DatabaseHelper.insertJobStatusNext(connection, user_id, not_started, started);
		DatabaseHelper.insertJobStatusNext(connection, user_id, started, running);
		DatabaseHelper.insertJobStatusNext(connection, user_id, running, cleaning_up);
		DatabaseHelper.insertJobStatusNext(connection, user_id, cleaning_up, fininshed);		
	}
	
	public void setupJobTypes(Connection connection, Integer user_id) throws SQLException {
		/**
		 * @TODO: add a clean flag
		 * @TODO: Unit test this functionality
		 */
		DatabaseHelper.deleteJobTypes(connection, user_id);
		Integer create_model_id = DatabaseHelper.insertJobTypes(connection, user_id, JobType.JOB_CREATE_MODEL);
		Integer process_document_id = DatabaseHelper.insertJobTypes(connection, user_id, JobType.JOB_PROCESS_DOCUMENT);
	}
	
	public void setupProject(
			Connection connection,
			String project_name,
			Integer user_id,
			Integer owner_user_id, 
			Integer ready_for_review_id, 
			Integer checkout_timeout_seconds) throws SQLException {
		
		
		DatabaseHelper.insertProject(connection, project_name, user_id, owner_user_id, ready_for_review_id, checkout_timeout_seconds);
		
	}
	
    @Override
    public void initialize() throws ResourceInitializationException {
        super.initialize();
        
        mProjectName = (String) getConfigParameterValue(PROJECT_NAME);
        mProjectType = (String) getConfigParameterValue(PROJECT_TYPE);
        mCheckoutTimeout = (Integer) getConfigParameterValue(CHECKOUT_TIMEOUT);
        mProjectOwnerUserName = (String) getConfigParameterValue(PROJECT_OWNER_USER_NAME);
        mProjectOwnerEmail = (String) getConfigParameterValue(PROJECT_OWNER_EMAIL);
        mProjectOwnerFirstName = (String) getConfigParameterValue(PROJECT_OWNER_FIRST_NAME);
        mProjectOwnerLastName = (String) getConfigParameterValue(PROJECT_OWNER_LAST_NAME); 
        
        try (DatabaseConnector connector = getDatabaseConnector()) {
            connector.connect();
            Connection connection = connector.getConnection();
            mLoggingUserId = DatabaseHelper.getLoggingUserNameId(connection, LOGGING_USER);
            DatabaseHelper.cleanProjects(connection, mLoggingUserId);
            setupJobStatus(connection, mLoggingUserId);
            setupJobTypes(connection, mLoggingUserId);
            
            DualValidation dv = new DualValidation(mProjectName, connection, mLoggingUserId);
            
            mProjectUserId = DatabaseHelper.getLoggingUserNameId(connection, mProjectOwnerUserName);
            if (mProjectUserId == null) {
            	mProjectUserId = DatabaseHelper.addNewUser(connection, mProjectOwnerUserName, mProjectOwnerEmail, mProjectOwnerFirstName, mProjectOwnerLastName);
            }
            setupProject(connection, mProjectName, mLoggingUserId, mProjectUserId, dv.getReadyForReviewId(), mCheckoutTimeout);
            
        } catch (ClassNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (SQLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
    
    @Override
    public void getNext(CAS aCAS) throws IOException, CollectionException {
        return;
    }


    @Override
    public boolean hasNext() throws IOException, CollectionException {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public Progress[] getProgress() {
        return new Progress[] { new ProgressImpl(0, 0, Progress.ENTITIES) };
    }

    @Override
    public void close() throws IOException {
        
    }

	@Override
	public void getNextJob(Connection connection, Integer user_id) throws SQLException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public HashMap<String, String> getJobParameters(Connection connection, Integer user_id, Integer job_queue_id)
			throws SQLException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void createJob(Connection connection, Integer user_id) throws SQLException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setJobParameters(Connection connection, Integer user_id, Integer job_queue_id,
			HashMap<String, String> params) throws SQLException {
		// TODO Auto-generated method stub
		
	}
}
