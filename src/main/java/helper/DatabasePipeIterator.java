package helper;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;

import cc.mallet.types.Instance;

public class DatabasePipeIterator implements java.util.Iterator<Instance> {

    /**
     * Create a new database pipeline iterator to pass to pipes. This will populate LDA objects. 
     * @param connection: A sql database connection
     * @param sql_fucntion: The funciton to call to populate stuff. This function should take no objects and return a set of id, text pairs. 
     */
    
    private ResultSet mResultSet;
    private boolean mHasNext;
    
    public DatabasePipeIterator(Connection connection, String sql_fucntion) throws SQLException {
        CallableStatement sp_call = connection.prepareCall(String.format("{call %s()}", sql_fucntion), ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
        sp_call.executeQuery();
        
        //get the results from the stored procedure
        mResultSet = sp_call.getResultSet();
        mHasNext = mResultSet.next();
    }
        
    @Override
    public boolean hasNext() {
        return mHasNext;
    }

    @Override
    public Instance next() {
        try {
            int id = mResultSet.getInt(1);
            String text = mResultSet.getString(2);
            mHasNext = mResultSet.next();
            return new Instance (text, id, String.format("Text_%s", id), text);
        } catch (SQLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            mHasNext = false;
            return null;
        }
        

    }

}
