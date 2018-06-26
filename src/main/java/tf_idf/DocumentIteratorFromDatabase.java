/**
 * This class depends on a standard creation of database tables where there exists a stored procedure called select_text_from_table.
 * This stored procedure selects an id and text_string from the passed table name. 
 * The creation object accepts various database parameters. 
 * 
 *  This will load all text into this application and iterate over it. If there are
 *  especially large tables, we might have to split it up into batches using the getNext()
 *  method.   
 */
package tf_idf;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import org.deeplearning4j.text.documentiterator.LabelAwareIterator;
import org.deeplearning4j.text.documentiterator.LabelledDocument;
import org.deeplearning4j.text.documentiterator.LabelsSource;

import helper.CleanText;

public class DocumentIteratorFromDatabase implements LabelAwareIterator {
    private ArrayList<LabelledDocument> mText;
    private int mTextIndex;
    private ArrayList<String> mDocumentLabels;
    
    public DocumentIteratorFromDatabase() { } 
    
    public DocumentIteratorFromDatabase(Connection connection, boolean drawFromCategory) throws SQLException {
        
        mText = new ArrayList<LabelledDocument>();
        mDocumentLabels = new ArrayList<String>();
        mTextIndex = 0;
        
        CallableStatement sp_call;
        
        if (drawFromCategory == true)
            sp_call = connection.prepareCall("{call select_category_text()}");
        else
            sp_call = connection.prepareCall("{call select_source_text()}");
        sp_call.execute();
        
        ResultSet rs = sp_call.getResultSet();
        while (rs.next()) {
            Integer id = rs.getInt(1);
            String text = rs.getString(2);
            text = CleanText.Standardize(text);
            LabelledDocument insert = new LabelledDocument();
            insert.setContent(text);
            String string_id = String.format("%s", id);
            insert.addLabel(string_id);
            mText.add(insert);
            mDocumentLabels.add(string_id);
        }
        System.out.println(String.format("Number of documents: %s", mText.size()));
    }
    
    @Override
    public boolean hasNext() {
        if (mTextIndex == mText.size()) {
            return false;
        }
        return true;
    }

    @Override
    public LabelledDocument next() {
        return mText.get(mTextIndex++);
    }

    @Override
    public boolean hasNextDocument() {
        return hasNext();
    }

    @Override
    public LabelledDocument nextDocument() {
        return next();
    }

    @Override
    public void reset() {
        // TODO Auto-generated method stub
        mTextIndex = 0;
    }

    @Override
    public LabelsSource getLabelsSource() {
        LabelsSource source = new LabelsSource(mDocumentLabels);
        return source;
    }

    @Override
    public void shutdown() {
        
    }

}
