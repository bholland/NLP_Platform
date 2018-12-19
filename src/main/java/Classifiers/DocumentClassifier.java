package Classifiers;

import java.io.IOException;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;

import opennlp.tools.doccat.DoccatFactory;
import opennlp.tools.doccat.DoccatModel;
import opennlp.tools.doccat.DocumentCategorizerME;
import opennlp.tools.doccat.DocumentSample;
import opennlp.tools.util.ObjectStream;
import opennlp.tools.util.TrainingParameters;
import opennlp.uima.doccat.DocumentCategorizer;
import opennlp.uima.doccat.DocumentCategorizerTrainer;

public class DocumentClassifier implements ObjectStream<DocumentSample> {
    
    private ArrayList<DocumentSample> mDocumentSampleList;
    private Integer mDocumentSampleListIdx;
    
    private HashMap<Integer, String> mCategoryMap;
    private HashMap<Integer, ArrayList<Integer>> mCategoryTextMap;
    private HashMap<Integer, String[]> mTextTokens;
    
    
    private Connection mSqlConnection;
    
    public HashMap<Integer, String> GetCategories() {
        return mCategoryMap;
    }
    
    public void Setup() throws SQLException {

        CallableStatement sp_call = mSqlConnection.prepareCall("{call select_training_data()}");
        boolean res = sp_call.execute();
        ResultSet rs = sp_call.getResultSet();
        
        while (rs.next()) {
            Integer text_id = rs.getInt(1);
            Integer category_id = rs.getInt(2);
            String category_name = rs.getString(3);
            boolean is_in_category = rs.getBoolean(4);
            
            if (!is_in_category) {
                continue;
            }
            //any key value in mCategoryMap denotes a category_id - text_id inclusive mapping
            //Leave out any category_id - text_id pairings that are not in the category
            //but are still in the training data. It will get added to the is_not model category. 
            if (!mCategoryMap.containsKey(category_id)) {
                mCategoryMap.put(category_id, category_name);
            }
           
            if (!mCategoryTextMap.containsKey(category_id)) {
                mCategoryTextMap.put(category_id, new ArrayList<Integer>());
            }
            //HERE, figure out how to deal with the is_in_category == false. 
            ArrayList<Integer> text_id_list = mCategoryTextMap.get(category_id);
            text_id_list.add(text_id);
            
            ArrayList<String> tokens_list = new ArrayList<String>();
            
            CallableStatement token_sp = mSqlConnection.prepareCall("{call select_category_text_tokens(?)}");
            token_sp.setInt(1, text_id);
            token_sp.execute();
            ResultSet rs_tokens = token_sp.getResultSet();
            while (rs_tokens.next()) {
                Integer sentence_index = rs_tokens.getInt(1); //not used yet
                String token = rs_tokens.getString(2);
                String pos_tag = rs_tokens.getString(3); //not used yet but I don't know why. 
                tokens_list.add(token);
            }
            
            String[] tokens = tokens_list.toArray(new String[0]);
            
            if (!mTextTokens.containsKey(text_id)) {
                mTextTokens.put(text_id, tokens);
            }
        }
    }
    
    public DocumentClassifier(Connection sql_connection) throws SQLException {
        mDocumentSampleList = new ArrayList<DocumentSample>();
        mDocumentSampleListIdx = 0;
        
        mCategoryMap = new HashMap<Integer, String>();
        mTextTokens = new HashMap<Integer, String[]>();
        
        mCategoryTextMap = new HashMap<Integer, ArrayList<Integer>>();
        
        mSqlConnection = sql_connection;
        
        Setup();
    }

    @Override
    public DocumentSample read() throws IOException {
        if (mDocumentSampleListIdx == mDocumentSampleList.size()) {
            return null;
        } else {
            DocumentSample ret = mDocumentSampleList.get(mDocumentSampleListIdx);
            mDocumentSampleListIdx++;
            return ret;
        }
    }
    
    @Override
    public void reset() throws IOException, UnsupportedOperationException {
        mDocumentSampleListIdx = 0;
    }
    
    @Override
    public void close() throws IOException {
        mDocumentSampleList = null;
        mDocumentSampleListIdx = null;
    }
    
    public void SetupTrainer(Integer category_id) {
        mDocumentSampleList = new ArrayList<DocumentSample>();
        mDocumentSampleListIdx = 0;
        
        /**
         * debugging only
        for (Integer key: mCategoryTextMap.keySet()) {
            System.out.println(String.format("%s: %s", key, mCategoryTextMap.get(key).size()));
        }
        */
        ArrayList<Integer> text_ids_in_category = mCategoryTextMap.get(category_id);
        for (Integer text_id: mTextTokens.keySet()) {
            if (text_ids_in_category.contains(text_id)) {
                mDocumentSampleList.add(new DocumentSample(String.format("is_%s", category_id), mTextTokens.get(text_id)));
            } else {
                mDocumentSampleList.add(new DocumentSample(String.format("is_not_%s", category_id), mTextTokens.get(text_id)));
            }
        }
        
    }
    
    public DoccatModel train(Integer category_id) throws IOException {
        TrainingParameters tp = TrainingParameters.defaultParams();
        tp.put(TrainingParameters.CUTOFF_PARAM, 1);
        tp.put(TrainingParameters.ITERATIONS_PARAM, 500);
        DoccatModel model = DocumentCategorizerME.train("en", this, tp, new DoccatFactory());
        return model;
    }
}
