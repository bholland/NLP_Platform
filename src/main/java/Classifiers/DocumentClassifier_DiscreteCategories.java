package Classifiers;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;

import opennlp.tools.doccat.DocumentSample;

public class DocumentClassifier_DiscreteCategories extends DocumentClassifier_ImplBase {
    
    public DocumentClassifier_DiscreteCategories(Connection sql_connection) throws SQLException {
        super(sql_connection);
        Setup();
    }

    @Override
    public void SetupTrainer() {
        documentSampleList = new ArrayList<DocumentSample>();
        documentSampleListIdx = 0;
        
        for (Integer category_id : categoryTextMap.keySet()) {
        	for (Integer text_id : categoryTextMap.get(category_id)) {
        		documentSampleList.add(new DocumentSample(String.format("%s", category_id), textTokens.get(text_id)));
        	}
        }
    }

	@Override
	public void SetActiveCategoryId(Integer category_id) {
		// TODO Auto-generated method stub
		
	}
}
