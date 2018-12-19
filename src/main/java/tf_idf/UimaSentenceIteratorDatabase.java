package tf_idf;

import java.io.File;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;

import org.apache.uima.analysis_engine.AnalysisEngine;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.cas.CAS;
import org.apache.uima.cas.CASException;
import org.apache.uima.collection.CollectionReader;
import org.apache.uima.fit.factory.AnalysisEngineFactory;
import org.apache.uima.fit.util.JCasUtil;
import org.apache.uima.resource.ResourceInitializationException;
import org.cleartk.token.type.Sentence;
import org.cleartk.util.cr.FilesCollectionReader;
import org.deeplearning4j.text.annotator.SentenceAnnotator;
import org.deeplearning4j.text.annotator.TokenizerAnnotator;
import org.deeplearning4j.text.documentiterator.LabelledDocument;
import org.deeplearning4j.text.sentenceiterator.BaseSentenceIterator;
import org.deeplearning4j.text.sentenceiterator.SentenceIterator;
import org.deeplearning4j.text.sentenceiterator.SentencePreProcessor;
import org.deeplearning4j.text.sentenceiterator.UimaSentenceIterator;
import org.deeplearning4j.text.sentenceiterator.labelaware.LabelAwareSentenceIterator;
import org.deeplearning4j.text.uima.UimaResource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import helper.CleanText;

public class UimaSentenceIteratorDatabase extends BaseSentenceIterator implements LabelAwareSentenceIterator {
    
    protected volatile CollectionReader reader;
    protected volatile Iterator<String> sentences;
    protected String path;
    private static final Logger log = LoggerFactory.getLogger(UimaSentenceIterator.class);
    private static AnalysisEngine defaultAnalysisEngine;
    private UimaResource resource;
    
    //private ArrayList<LabelledDocument> mText;
    private int mCurrentDocument;
    private String mDocumentLabel;
    private ArrayList<LabelledDocument> mText;
    private ArrayList<String> mDocumentLabels;
    
    /**
     * @param preProcessor - Can be null
     * @param connection - the database connection
     * @param drawFromCategory - true to draw from the category table, false to draw from the source table
     * @param resource - a UIMA resource
     * @throws SQLException
     * @throws ResourceInitializationException
     * @throws AnalysisEngineProcessException
     */
    public UimaSentenceIteratorDatabase(SentencePreProcessor preProcessor, Connection connection, boolean drawFromCategory)
            throws SQLException, ResourceInitializationException, AnalysisEngineProcessException {
        
        super(preProcessor);
        
        mText = new ArrayList<LabelledDocument>();
        mDocumentLabels = new ArrayList<String>();
        mCurrentDocument = 0;
        
        CallableStatement sp_call;
        
        if (drawFromCategory == true)
            sp_call = connection.prepareCall("{call select_category_text()}");
        else
            sp_call = connection.prepareCall("{call select_source_text()}");
        sp_call.execute();
        
        /**
         * Set up the UIMA resource. 
         * @TODO: Look into how to actually generate this from model files. 
         */
        resource = new UimaResource(AnalysisEngineFactory.createEngine(
                AnalysisEngineFactory.createEngineDescription(TokenizerAnnotator.getDescription(),
                        SentenceAnnotator.getDescription())));

        /**
         * @TODO: This is really bad. Fix this so that I don't have to load
         * the entire database into memory. 
         */
        
        ResultSet rs = sp_call.getResultSet();
        while (rs.next()) {
            Integer id = rs.getInt(1);
            String text = rs.getString(2);
            text = CleanText.Standardize(text);
            
            CAS cas = resource.retrieve();
            cas.setDocumentText(text);
            resource.getAnalysisEngine().process(cas);

            
            try {
                String string_id = String.format("%s", id);
                for (Sentence sentence : JCasUtil.select(cas.getJCas(), Sentence.class)) {
                    
                    LabelledDocument insert = new LabelledDocument();
                    insert.setContent(sentence.getCoveredText());
                    insert.addLabel(string_id);
                    mText.add(insert);
                    mDocumentLabels.add(string_id);
                }
            } catch (CASException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
        System.out.println(String.format("Number of documents: %s", mText.size()));
    }
    
    @Override
    public String nextSentence() {
        mDocumentLabel = mDocumentLabels.get(mCurrentDocument);
        LabelledDocument ret = mText.get(mCurrentDocument);
        mCurrentDocument++; 
        return ret.getContent();
    }

    @Override
    public boolean hasNext() {
        return mCurrentDocument != mText.size();
    }

    @Override
    public void reset() {
        mCurrentDocument = 0;
    }

    @Override
    public String currentLabel() {
        return mDocumentLabel;
    }

    @Override
    public List<String> currentLabels() {
        return null;
    }
}
