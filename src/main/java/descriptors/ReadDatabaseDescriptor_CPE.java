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
package descriptors;

import java.io.IOException;
import java.util.ArrayList;

import org.apache.uima.cas.CAS;
import org.apache.uima.cas.CASException;
import org.apache.uima.collection.CollectionException;
import org.apache.uima.collection.CollectionReader_ImplBase;
import org.apache.uima.jcas.JCas;
import org.apache.uima.resource.ResourceInitializationException;
import org.apache.uima.util.Level;
import org.apache.uima.util.Progress;
import org.apache.uima.util.ProgressImpl;

import database.DatabaseConnector;
import objects.DatabaseConnection;
import objects.UnprocessedText;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;


public class ReadDatabaseDescriptor_CPE extends CollectionReader_ImplBase {
    
    /**
     * Database server location (probably localhost. 
     */
    public static final String PARAM_DATABASE_SERVER = "DatabaseServer";
    
    /**
     * The database to connect to
     */
    public static final String PARAM_DATABASE = "Database";
    
    /**
     * Database user name
     */
    public static final String PARAM_DATABASE_USER_NAME = "DatabaseUserName";
    
    /**
     * Database password
     */
    public static final String PARAM_DATABASE_PASSWORD = "DatabasePassword";

    /**
     * The database server port
     */
    public static final String PARAM_DATABASE_SERVER_PORT = "DatabasePort";
    
    /**
     * The database server type
     */
    public static final String PARAM_DATABASE_TYPE = "DatabaseType";
    
    /**
     * The optional size of a pull. This is going to pull N rows at a time.
     */
    public static final String PARAM_BATCH_PROCESS_SIZE = "BatchProcessSize";
    
    /**
     * Table to use to pull ids. 
     */
    public static final String PARAM_TABLE_NAME = "DataTableName";
   
        
    private String mDatabaseServer;
    private String mDatabase;
    private String mUserName;
    private String mPassword;
    
    private String mPort;
    private String mType;
    
    private ArrayList<Integer> mTextIds;
    private int mTextIdsIndex;
    
    @Override
    public void initialize() throws ResourceInitializationException {
        mDatabaseServer = (String) getConfigParameterValue(PARAM_DATABASE_SERVER);
        mDatabase = (String) getConfigParameterValue(PARAM_DATABASE);
        mUserName = (String) getConfigParameterValue(PARAM_DATABASE_USER_NAME);
        mPassword = (String) getConfigParameterValue(PARAM_DATABASE_PASSWORD);
        
        mPort = (String) getConfigParameterValue(PARAM_DATABASE_SERVER_PORT);
        mType = (String) getConfigParameterValue(PARAM_DATABASE_TYPE);
        
        if (mPort == null && mType == null) {
            throw new ResourceInitializationException("DatabasePort or DatabaseType was not passed as a parameter.",
                    new Object[] { });
        }
        
        if (mPort == null) {
            if (mType.toLowerCase().equals("mysql")) {
                mPort = "3306";
            } else if (mType.toLowerCase().equals("postgresql") || mType.toLowerCase().equals("pgsql")) {
                mPort = "5432";
                mType = "pgsql";
            } else {
                 throw new ResourceInitializationException("DatabasePort was not passed and DatabaseType is not mysql or psql.",
                    new Object[] { });
            }
        } else if (mPort.trim().equals("3306")) {
            mType = "mysql";
        }
        else if (mPort.trim().equals("5432")) {
            mType = "pgsql";
        }
        
        try (DatabaseConnector connector = new DatabaseConnector(mType, mDatabaseServer, mPort, mDatabase, mUserName, mPassword)) {
            connector.connect();
            Connection connection = connector.getConnection();
            mTextIdsIndex = 0;
            mTextIds = new ArrayList<Integer>();
        
            CallableStatement sp_call = connection.prepareCall("{call select_source_text_ids()}");
            sp_call.execute();
            ResultSet rs = sp_call.getResultSet();
            while (rs.next()) {
                mTextIds.add(rs.getInt(1));
            }
        } catch (SQLException | IOException | ClassNotFoundException e) {
            e.printStackTrace();
            throw new ResourceInitializationException("Cannot connect to database with parameters Server: {0}, Port: {1}, Database: {2}, User Name: {3} or invalid password",
                    new Object[] {mDatabaseServer, mPort, mDatabase, mUserName});
        }
    }
    
    @Override
    public void getNext(CAS aCAS) throws IOException, CollectionException {
        JCas jcas;
        try {
            jcas = aCAS.getJCas();
        } catch (CASException e) {
            throw new CollectionException(e);
        }
        Integer id = mTextIds.get(mTextIdsIndex++);
        try (DatabaseConnector connector = new DatabaseConnector(mType, mDatabaseServer, mPort, mDatabase, mUserName, mPassword)) {
            connector.connect(); 
            Connection connection = connector.getConnection();
            CallableStatement sp_call = connection.prepareCall("{call select_text_from_text_id(?)}");

            sp_call.setInt(2, id);
            sp_call.execute();
                
            ResultSet rs = sp_call.getResultSet();
            while (rs.next()) {
                String text_string = rs.getString(1);
                Integer num_tokens = rs.getInt(2);
                jcas.setDocumentText(text_string);
                UnprocessedText unprocessed_text = new UnprocessedText(jcas);
                unprocessed_text.setTextId(id);
                unprocessed_text.setNumTokens(num_tokens);
                unprocessed_text.addToIndexes();
            }
        } catch (SQLException | ClassNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            getUimaContext().getLogger().log(Level.WARNING, "Stored procedure call failed.");
        }

        DatabaseConnection db_conn = new DatabaseConnection(jcas);
        db_conn.setDatabaseServer(mDatabaseServer);
        db_conn.setPort(mPort);
        db_conn.setDatabase(mDatabase);
        db_conn.setUserName(mUserName);
        db_conn.setPassword(mPassword);
        db_conn.addToIndexes();
    }

    @Override
    public void close() throws IOException { 
        // TODO Auto-generated method stub

    }

    @Override
    public Progress[] getProgress() {
        return new Progress[] { new ProgressImpl(mTextIdsIndex, mTextIds.size(), Progress.ENTITIES) };
    }

    @Override
    public boolean hasNext() throws IOException, CollectionException {
        return mTextIdsIndex < mTextIds.size();
    }
    
    public String getDatabaseServer() {
        return mDatabaseServer;
    }
    
    public String getDatabase() {
        return mDatabase;
    }
    
    public String getUserName() {
        return mUserName;
    }
    
    public String getPassword() {
        return mPassword;
    }
    
    public String getPort() {
        return mPort;
    }
}
