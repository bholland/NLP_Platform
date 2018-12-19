package annotators;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;


import org.apache.uima.UimaContext;
import org.apache.uima.analysis_component.JCasAnnotator_ImplBase;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.cas.FSIterator;
import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.tcas.Annotation;
import org.apache.uima.resource.ResourceInitializationException;

import database.DatabaseConnector;
import objects.DatabaseConnection;
import objects.Sentence;
import objects.UnprocessedText;

import org.apache.commons.text.similarity.LevenshteinDistance;

import opennlp.tools.dictionary.Dictionary;
import opennlp.tools.namefind.DictionaryNameFinder;
import opennlp.tools.sentdetect.SentenceDetectorME;
import opennlp.tools.sentdetect.SentenceModel;
import opennlp.tools.util.Span;
import opennlp.tools.util.StringList;

public class Dedupe extends JCasAnnotator_ImplBase {
    
    public static final String PARAM_RANGE_PLUS_MINUS = "RangePlusMinus";
    public static final String PARAM_RATIO = "LevenshteinDistanceRatio";
    public static final String PARAM_TABLE_NAME = "DataTableName";
    
    private Integer mRange;
    private Long mMinTokens;
    private Long mMaxTokens;
    
    private Double mMinPct;
    private Double mMaxPct;
    
    private Double mLevenshteinDistanceRatio;
    
    private String mTableName;
    
    @Override
    public void initialize(UimaContext aContext) throws ResourceInitializationException {
        super.initialize(aContext);
        mRange = (Integer) getContext().getConfigParameterValue(PARAM_RANGE_PLUS_MINUS);
        
        if (mRange < 0 || mRange > 100) throw new ResourceInitializationException("Invalid value for mRange. mRange must be between 0 and 100.", new Object[0]);
        double range_pct = (double)mRange / 100.0; //0.15
        mMinPct = 1 - range_pct;
        mMaxPct = 1 + range_pct;
        
        Integer ratio = (Integer) getContext().getConfigParameterValue(PARAM_RATIO);
        
        mLevenshteinDistanceRatio = ratio / 100.0;
        
        mTableName = (String) getContext().getConfigParameterValue(PARAM_TABLE_NAME);
        
    }
    

    @Override
    public void process(JCas aJCas) throws AnalysisEngineProcessException {
        FSIterator<Annotation> sentences = aJCas.getAnnotationIndex(Sentence.type).iterator();
        
        int text_id = 0;
        if (sentences.hasNext()) {
            Sentence sentence = (Sentence) sentences.next();
            text_id = sentence.getDocumentID();
        }
        
        DatabaseConnection database_connection = DatabaseConnector.GetDatabaseConnection(aJCas);
        if (database_connection == null) {
            throw new AnalysisEngineProcessException("A database object was not configured for this CAS" , new Object[] {} );
        }
        
        FSIterator<Annotation> ut = aJCas.getAnnotationIndex(UnprocessedText.type).iterator();
        assert (ut.hasNext()) : "Sentence splitting annotator does not have an associated unprocessed text document";

        UnprocessedText raw_text = (UnprocessedText) ut.next();
        Integer unprocessed_text_id = raw_text.getTextId();
        Integer num_tokens = raw_text.getNumTokens();
        
        String test_string = aJCas.getDocumentText();
        
        mMinTokens = Math.round(num_tokens * mMinPct);
        mMaxTokens = Math.round(num_tokens * mMaxPct);
        
        assert(ut.hasNext() == false) : "More than one raw text document exists in the aJCas object.";
        
        try (DatabaseConnector mysql_connector = new DatabaseConnector(database_connection)) {
            mysql_connector.connect();
            Connection sql_connection = mysql_connector.getConnection();
            CallableStatement sp_call = sql_connection.prepareCall("{call select_text_of_similar_length(?, ?, ?)}");
            sp_call.setString(1, mTableName);
            sp_call.setLong(2, mMinTokens);
            sp_call.setLong(3, mMaxTokens);
            boolean res = sp_call.execute();
            ResultSet rs = sp_call.getResultSet();
            LevenshteinDistance calculator = new LevenshteinDistance();
            while (rs.next()) {
                //id, text_string, num_tokens
                Integer id = rs.getInt(1);
                String  compare_text = rs.getString(2);
                Integer compare_tokens = rs.getInt(3);
                Integer distance = calculator.apply(test_string, compare_text);
                double ratio = (double)distance / (double)test_string.length();
                if (ratio < mLevenshteinDistanceRatio) {
                    CallableStatement insert_statement = sql_connection.prepareCall("{call insert_duplicate(?, ?)}");
                    insert_statement.setInt(1,  unprocessed_text_id);
                    insert_statement.setInt(2, id);
                    insert_statement.execute();
                }
            }
            
            
        } catch (SQLException | IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

}



