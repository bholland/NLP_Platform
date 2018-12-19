package tf_idf;

import java.io.IOException;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import org.deeplearning4j.text.documentiterator.LabelAwareIterator;
import org.deeplearning4j.text.documentiterator.LabelledDocument;
import org.deeplearning4j.text.documentiterator.LabelsSource;

import database.DatabaseConnector;

public class DocumentIterator implements LabelAwareIterator {
    private ArrayList<LabelledDocument> mText;
    private int mTextIndex;
    private ArrayList<String> mDocumentLabels;
    
    public DocumentIterator() {
        
        mText = new ArrayList<LabelledDocument>();
        mDocumentLabels = new ArrayList<String>();
        mTextIndex = 0;
        
        
        //String database_server, String port, String database, String user_name, String password) {
        try (DatabaseConnector connector = new DatabaseConnector("pgsql", "localhost", "5432", "nlp_testing", "root", "password")) {
            connector.connect();
            Connection connection = connector.getConnection();
            
            CallableStatement sp_call = connection.prepareCall("{call select_mass_campaign_text()}");
            sp_call.execute();
            ResultSet rs = sp_call.getResultSet();
            while (rs.next()) {
                Integer id = rs.getInt(1);
                String text = rs.getString(2);
                LabelledDocument insert = new LabelledDocument();
                insert.setContent(text);
                String string_id = String.format("%s", id);
                insert.addLabel(string_id);
                mText.add(insert);
                mDocumentLabels.add(string_id);
            }
            
            
        } catch (SQLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        } catch (ClassNotFoundException e2) {
            // TODO Auto-generated catch block
            e2.printStackTrace();
        }
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
