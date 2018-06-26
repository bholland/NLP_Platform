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
package tf_idf;

import java.io.IOException;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import database.DatabaseConnector;

public class DatabaseText {
    
    private ArrayList<String> mText;
    private ArrayList<Integer> mID;
    private int mTextIndex;
    private int mCurrentID;

    public DatabaseText(String stored_procedure) {
        
        mText = new ArrayList<String>();
        mID = new ArrayList<Integer>();
        mTextIndex = 0;
        
        
        //String database_server, String port, String database, String user_name, String password) {
        try (DatabaseConnector connector = new DatabaseConnector("pgsql", "localhost", "5432", "nlp_testing", "root", "password")) {
            connector.connect();
            Connection connection = connector.getConnection();
            
            CallableStatement sp_call = connection.prepareCall(String.format("{call %s}", stored_procedure));
            sp_call.execute();
            ResultSet rs = sp_call.getResultSet();
            while (rs.next()) {
                Integer id = rs.getInt(1);
                String text = rs.getString(2);
                mText.add(text);
                mID.add(id);
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
    
    public boolean hasNext() {
        if (mTextIndex == mText.size()) {
            return false;
        }
        return true;
    }
    
    public String next() {
        String ret = mText.get(mTextIndex);
        mCurrentID = mID.get(mTextIndex);
        mTextIndex++;
        return ret;
    }
    
    public Integer get_id() {
        return mCurrentID;
    }
}
