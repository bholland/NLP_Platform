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
import objects.NLPModel;
import objects.UnprocessedText;

public class DocumentClassifier_CPE extends DatabaseCollectionReader_ImplBase {
    
    
    
    HashMap<Integer, String> mCategories;
    Set<Integer> mKeys;
    Iterator<Integer> mKeysIter;
    Integer mKeysIndex;
    Integer mBatchNumber;
    
    @Override
    public void initialize() throws ResourceInitializationException {
        super.initialize();
        
        try (DatabaseConnector sql_connector = getDatabaseConnector()) {
            sql_connector.connect();
            Connection sql_connection = sql_connector.getConnection();
            mLoggingUserId = DatabaseHelper.getLoggingUserNameId(sql_connection, LOGGING_USER);
            try (DocumentClassifier_Binary classifier = new DocumentClassifier_Binary(sql_connection, mLoggingUserId)) {
                mCategories = classifier.GetCategories(); 
                mKeys = mCategories.keySet();
                mKeysIter = mKeys.iterator();
                mKeysIndex = 0;
            }
            mBatchNumber = DatabaseHelper.selectBatchNumber(sql_connection, mLoggingUserId);
            DatabaseHelper.incrementBatchNumber(sql_connection, mLoggingUserId);
            
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
        JCas jcas;
        try {
            jcas = aCAS.getJCas();
        } catch (CASException e) {
            throw new CollectionException(e);
        }
        Integer id = mKeysIter.next();
        mKeysIndex++;
        
        NLPModel nlp_model = new NLPModel(jcas);
        nlp_model.setCategoryName(mCategories.get(id));
        nlp_model.setBatchNumber(mBatchNumber);
        nlp_model.addToIndexes();
        
        DatabaseConnection db_conn = new DatabaseConnection(jcas);
        db_conn.setDatabaseServer(mDatabaseServer);
        db_conn.setPort(mPort);
        db_conn.setDatabaseType(mType);
        db_conn.setDatabase(mDatabase);
        db_conn.setUserName(mUserName);
        db_conn.setPassword(mPassword);
        db_conn.setLoggingUserId(mLoggingUserId);
        db_conn.addToIndexes();
    }


    @Override
    public boolean hasNext() throws IOException, CollectionException {
        // TODO Auto-generated method stub
        return mKeysIter.hasNext();
    }

    @Override
    public Progress[] getProgress() {
        return new Progress[] { new ProgressImpl(mKeysIndex, mKeys.size(), Progress.ENTITIES) };
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
