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
import opennlp.tools.doccat.FeatureGenerator;
import opennlp.tools.doccat.NGramFeatureGenerator;
import opennlp.tools.util.ObjectStream;
import opennlp.tools.util.TrainingParameters;
import opennlp.uima.doccat.DocumentCategorizer;
import opennlp.uima.doccat.DocumentCategorizerTrainer;

public abstract class DocumentClassifier_ImplBase implements ObjectStream<DocumentSample> {
    
    protected ArrayList<DocumentSample> documentSampleList;
    protected Integer documentSampleListIdx;
    
    protected HashMap<Integer, String> categoryMap;
    protected HashMap<Integer, ArrayList<Integer>> categoryTextMap;
    protected HashMap<Integer, String[]> textTokens;
    
    protected Integer activeCategoryId;
    
    
    protected Connection mSqlConnection;
    
    public HashMap<Integer, String> GetCategories() {
        return categoryMap;
    }
    
    /**
     * Setup: This function will set up the training datasets and models
     * @param user_id: The logging user id. 
     * @throws SQLException
     */
    
    public void Setup(Integer user_id) throws SQLException {
    	/**
    	 * @TODO: Unit test this. 
    	 */
        CallableStatement sp_call = mSqlConnection.prepareCall("{call select_training_data(?)}");
        sp_call.setInt(1,  user_id);
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
            if (!categoryMap.containsKey(category_id)) {
                categoryMap.put(category_id, category_name);
            }
           
            if (!categoryTextMap.containsKey(category_id)) {
                categoryTextMap.put(category_id, new ArrayList<Integer>());
            }
            //HERE, figure out how to deal with the is_in_category == false. 
            ArrayList<Integer> text_id_list = categoryTextMap.get(category_id);
            text_id_list.add(text_id);
            
            ArrayList<String> tokens_list = new ArrayList<String>();
            /**
        	 * @TODO: Unit test this. 
        	 */
            CallableStatement token_sp = mSqlConnection.prepareCall("{call select_category_text_tokens(?, ?)}");
            token_sp.setInt(1, user_id);
            token_sp.setInt(2, text_id);
            token_sp.execute();
            ResultSet rs_tokens = token_sp.getResultSet();
            while (rs_tokens.next()) {
                Integer sentence_index = rs_tokens.getInt(1); //not used yet
                String token = rs_tokens.getString(2);
                String pos_tag = rs_tokens.getString(3); //not used yet but I don't know why. 
                tokens_list.add(token);
            }
            
            String[] tokens = tokens_list.toArray(new String[0]);
            
            if (!textTokens.containsKey(text_id)) {
                textTokens.put(text_id, tokens);
            }
        }
    }
    
    public DocumentClassifier_ImplBase(Connection sql_connection, Integer user_id) throws SQLException {
        documentSampleList = new ArrayList<DocumentSample>();
        documentSampleListIdx = 0;
        
        categoryMap = new HashMap<Integer, String>();
        textTokens = new HashMap<Integer, String[]>();
        
        categoryTextMap = new HashMap<Integer, ArrayList<Integer>>();
        
        mSqlConnection = sql_connection;
        
        documentSampleList = new ArrayList<DocumentSample>();
        documentSampleListIdx = 0;
        
        activeCategoryId = null;
        
        Setup(user_id);
    }

    @Override
    public DocumentSample read() throws IOException {
        if (documentSampleListIdx == documentSampleList.size()) {
            return null;
        } else {
            DocumentSample ret = documentSampleList.get(documentSampleListIdx);
            documentSampleListIdx++;
            return ret;
        }
    }
    
    @Override
    public void reset() throws IOException, UnsupportedOperationException {
        documentSampleListIdx = 0;
    }
    
    @Override
    public void close() throws IOException {
        documentSampleList = null;
        documentSampleListIdx = null;
    }
    
    /**
     * This is really the only class that the user need to set up. 
     * This will set up the document classifier data for the maxent model.
     * This can basically be anything but it must create and populate 
     * documentSampleList and documentSampleListIdx.
     * 
     * This function sets up and defines the ObjectStream<DocumentStample>
     * portion of this object that gets passed to the DocumentCategorizerME.train() 
     * function.
     */
    public abstract void SetupTrainer();
    
    /**
     * @param category_id: The category_id to use
     * For models that use a specific category_id, use this to set it up.  
     */
    public abstract void SetActiveCategoryId(Integer category_id);
        
    
    public DoccatModel train(Integer category_id) throws IOException {
        TrainingParameters tp = TrainingParameters.defaultParams();
        tp.put(TrainingParameters.CUTOFF_PARAM, 1);
        tp.put(TrainingParameters.ITERATIONS_PARAM, 500);
        //This is where you create custom features to calculate as part of the model. 
        //Think through experimenting with this. It can get fairly intense. 
        FeatureGenerator[] features = {new NGramFeatureGenerator(1, 1), new NGramFeatureGenerator(2, 3)};
        DoccatModel model = DocumentCategorizerME.train("en", this, tp, new DoccatFactory(features));
        return model;
    }
}
