package annotators;

import java.io.BufferedOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;

import org.apache.uima.analysis_component.JCasAnnotator_ImplBase;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.cas.FSIterator;
import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.tcas.Annotation;

import Classifiers.DocumentClassifier_Binary;
import database.DatabaseConnector;
import helper.DatabaseHelper;
import objects.DatabaseConnection;
import objects.NLPModel;
import opennlp.tools.doccat.DoccatModel;
import opennlp.tools.doccat.DocumentCategorizerME;

public class NLPModelTagging extends JCasAnnotator_ImplBase {
    DatabaseConnection mDatabaseConnection;

    @Override
    public void process(JCas aJCas) throws AnalysisEngineProcessException {
        FSIterator<Annotation> nlp_model_iter = aJCas.getAnnotationIndex(NLPModel.type).iterator();
        assert (nlp_model_iter.hasNext()) : "NLPModelTraining instance does not have an associated NLPModel object.";
        
        NLPModel nlp_model = (NLPModel) nlp_model_iter.next();
        
        mDatabaseConnection = DatabaseConnector.GetDatabaseConnection(aJCas);
        if (mDatabaseConnection == null) {
            throw new AnalysisEngineProcessException("A database object was not configured for this CAS" , new Object[] {} );
        }
        
        try (DatabaseConnector connector = new DatabaseConnector(mDatabaseConnection)) {
        	connector.connect();
            Connection sql_connection = connector.getConnection();
            Integer mLoggingUserId = connector.getLoggingUserId();
            try (DocumentClassifier_Binary classifier = new DocumentClassifier_Binary(sql_connection, mLoggingUserId)) {
	            
	            Integer category_id = DatabaseHelper.getCategoryId(sql_connection, connector.getLoggingUserId(), nlp_model.getCategoryName());
	            Integer batch_number = nlp_model.getBatchNumber();
	            DoccatModel model = DatabaseHelper.getDoccatModel(sql_connection, connector.getLoggingUserId(), category_id);           
	            HashMap<Integer, ArrayList<String>> documents = DatabaseHelper.getDocumentTokens(sql_connection, connector.getLoggingUserId());
	            
	            for (Integer text_id: documents.keySet()) {
	                ArrayList<String> document =  documents.get(text_id);
	                String[] tmp = document.toArray(new String[0]);
	                
	                DocumentCategorizerME cat = new DocumentCategorizerME(model);
	                double[] outcomes = cat.categorize(tmp);
	                String cat1 = cat.getCategory(0);
	                String cat2 = cat.getCategory(1);
	                
	                //force the is_not category as cat1. 
	                if (cat1.startsWith("is_not")) {
	                    //System.out.println(String.format("%s: %s\n%s: %s", cat1, outcomes[0], cat2, outcomes[1]));
	                    DatabaseHelper.insertCategoryProbabilities(sql_connection, mLoggingUserId, batch_number, text_id, category_id, cat1, outcomes[0], cat2, outcomes[1]);
	                } else {
	                    DatabaseHelper.insertCategoryProbabilities(sql_connection, mLoggingUserId, batch_number, text_id, category_id, cat2, outcomes[1], cat1, outcomes[0]);
	                }
	            }
            }
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
    public void collectionProcessComplete() throws AnalysisEngineProcessException {
        try (DatabaseConnector connector = new DatabaseConnector(mDatabaseConnection)) {
        	connector.connect();
            Connection sql_connection = connector.getConnection();
            DatabaseHelper.incrementBatchNumber(sql_connection, connector.getLoggingUserId());
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

}
