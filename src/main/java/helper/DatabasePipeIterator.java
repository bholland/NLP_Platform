/*******************************************************************************
 * Copyright (C) 2018 by Benedict M. Holland <benedict.m.holland@gmail.com>
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 ******************************************************************************/
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
