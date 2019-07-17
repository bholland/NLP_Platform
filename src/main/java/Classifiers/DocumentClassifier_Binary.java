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

public class DocumentClassifier_Binary extends DocumentClassifier_ImplBase {
    
    public DocumentClassifier_Binary(Connection sql_connection, Integer user_id) throws SQLException {
        super(sql_connection, user_id);
        Setup();
    }

    @Override
    public void SetActiveCategoryId(Integer category_id) {
    	activeCategoryId = category_id;
    }
    
    /**
     * This is a binary classifier where text is either in the category
     * defined by activeCategoryId or not. 
     */
    @Override
    public void SetupTrainer() {
        /**
         * debugging only
        for (Integer key: mCategoryTextMap.keySet()) {
            System.out.println(String.format("%s: %s", key, mCategoryTextMap.get(key).size()));
        }
        */
        ArrayList<Integer> text_ids_in_category = categoryTextMap.get(activeCategoryId);
        for (Integer text_id: textTokens.keySet()) {
            if (text_ids_in_category.contains(text_id)) {
                documentSampleList.add(new DocumentSample(String.format("is_%s", activeCategoryId), textTokens.get(text_id)));
            } else {
                documentSampleList.add(new DocumentSample(String.format("is_not_%s", activeCategoryId), textTokens.get(text_id)));
            }
        }
        
    }
}
