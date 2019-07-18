package annotators;

import java.io.IOException;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Types;

import org.apache.log4j.Logger;
import org.apache.uima.UimaContext;
import org.apache.uima.analysis_component.JCasAnnotator_ImplBase;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.cas.FSIterator;
import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.cas.BooleanArray;
import org.apache.uima.jcas.cas.StringArray;
import org.apache.uima.jcas.tcas.Annotation;
import org.apache.uima.resource.ResourceInitializationException;

import database.DatabaseConnector;
import helper.DatabaseHelper;
import objects.DatabaseConnection;
import objects.Sentence;
import objects.UnprocessedText;
import opennlp.tools.util.Span;

public class SentenceToDatabaseAnnotator extends JCasAnnotator_ImplBase {
    
    @Override
    public void initialize(UimaContext aContext) throws ResourceInitializationException {
        super.initialize(aContext);
    }
    
    private void InsertSentence(boolean is_document, objects.Sentence sentence, DatabaseConnector connector) throws SQLException {
        Integer unprocessed_text_id = sentence.getDocumentID();
        String sentence_string = sentence.getText_string();
        
        Connection sql_connection = connector.getConnection();
        Integer sentence_id = null;
        if (is_document) {
        	//the job queue might fall out of sync with all of the cleaning issues. 
        	//Just return if the text id doesn't exist 
        	if (DatabaseHelper.getDocumentTextFromID(sql_connection, connector.getLoggingUserId(), unprocessed_text_id) == null) {
        		return;
        	}
        	sentence_id = DatabaseHelper.insertDocumentSentence(sql_connection, connector.getLoggingUserId(), sentence_string, unprocessed_text_id, sentence.getSentenceNumber());
            
        } else {
        	if (DatabaseHelper.getCategoryTextFromID(sql_connection, connector.getLoggingUserId(), unprocessed_text_id) == null) {
        		return;
        	}
            sentence_id = DatabaseHelper.insertCategorySentence(sql_connection, connector.getLoggingUserId(), sentence_string, unprocessed_text_id, sentence.getSentenceNumber()); 
        }
        sentence.setSentence_id(sentence_id);
        
        StringArray words = sentence.getWords();
        StringArray stemmed_words = sentence.getLemma_tags();
        StringArray tags = sentence.getPos_tags();
        StringArray chunks = sentence.getChunks();
        
        for (int x = 0; x < words.size(); x++) {
            if (is_document) {
                DatabaseHelper.insertDocumentSentenceMetadata(sql_connection, connector.getLoggingUserId(), sentence_id, x, words.get(x), tags.get(x), chunks.get(x), stemmed_words.get(x), sentence.getIsRawSentence());
            } else {
                DatabaseHelper.insertCategorySentenceMetadata(sql_connection, connector.getLoggingUserId(), sentence_id, x, words.get(x), tags.get(x), chunks.get(x), stemmed_words.get(x), sentence.getIsRawSentence());    
            }
        }
    }
    
    @Override
    public void process(JCas aJCas) throws AnalysisEngineProcessException {
        String input_document = aJCas.getDocumentText();
        if (input_document == null) {
            return;
        }
        DatabaseConnection database_connection = DatabaseConnector.GetDatabaseConnection(aJCas);
        if (database_connection == null) {
            throw new AnalysisEngineProcessException("A database object was not configured for this CAS" , new Object[] {} );
        }
        
        FSIterator<Annotation> unprocessed_text_iterator = aJCas.getAnnotationIndex(UnprocessedText.type).iterator();
        assert (unprocessed_text_iterator.hasNext()) : "No unprocessed texts are assocaited with this CAS object.";
        UnprocessedText unprocessed_text = (UnprocessedText) unprocessed_text_iterator.next();
        
        FSIterator<Annotation> sentence_iterator = aJCas.getAnnotationIndex(Sentence.type).iterator();
        assert (sentence_iterator.hasNext()) : "No setences are assocaited with this CAS object.";
        
        try (DatabaseConnector connector = new DatabaseConnector(database_connection)) {
            connector.connect();
            while (sentence_iterator.hasNext()) {
                Sentence sentence = (Sentence) sentence_iterator.next();
                InsertSentence(unprocessed_text.getIsDocument(), sentence, connector);
            }
        } catch (SQLException | IOException | ClassNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            throw new AnalysisEngineProcessException();
        }
    }

}
