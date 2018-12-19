package annotators;

import java.io.BufferedOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.sql.Connection;
import java.sql.SQLException;

import org.apache.uima.analysis_component.JCasAnnotator_ImplBase;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.cas.FSIterator;
import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.tcas.Annotation;

import Classifiers.DocumentClassifier;
import database.DatabaseConnector;
import helper.DatabaseHelper;
import objects.DatabaseConnection;
import objects.NLPModel;
import opennlp.tools.doccat.DoccatModel;

public class NLPModelTraining extends JCasAnnotator_ImplBase {

    @Override
    public void process(JCas aJCas) throws AnalysisEngineProcessException {
        FSIterator<Annotation> nlp_model_iter = aJCas.getAnnotationIndex(NLPModel.type).iterator();
        assert (nlp_model_iter.hasNext()) : "NLPModelTraining instance does not have an associated NLPModel object.";
        
        NLPModel nlp_model = (NLPModel) nlp_model_iter.next();
        
        DatabaseConnection database_connection = DatabaseConnector.GetDatabaseConnection(aJCas);
        if (database_connection == null) {
            throw new AnalysisEngineProcessException("A database object was not configured for this CAS" , new Object[] {} );
        }
        
        try (DatabaseConnector connector = new DatabaseConnector(database_connection)) {
        	connector.connect();
            Connection sql_connection = connector.getConnection();
            DocumentClassifier classifier = new DocumentClassifier(sql_connection);
            
            Integer category_id = DatabaseHelper.getCategoryId(sql_connection, connector.getLoggingUserId(), nlp_model.getCategoryName());
            classifier.SetupTrainer(category_id);
            DoccatModel model = classifier.train(category_id);
            
            /*OutputStream modelOut = null;
            try {
                modelOut = new BufferedOutputStream(new FileOutputStream(String.format("model_%s.bin", category_id)));
                model.serialize(modelOut);
            } catch (IOException e) {
                // Failed to save model
                e.printStackTrace();
            }*/
            DatabaseHelper.putDoccatModelFile(sql_connection, connector.getLoggingUserId(), category_id, model);
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
