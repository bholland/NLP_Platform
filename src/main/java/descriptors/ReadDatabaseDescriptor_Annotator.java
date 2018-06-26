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

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;


import org.apache.uima.jcas.JCas;
import org.apache.uima.resource.ResourceInitializationException;
import org.apache.uima.util.Level;

import objects.UnprocessedText;

import org.apache.uima.UimaContext;
import org.apache.uima.analysis_component.JCasAnnotator_ImplBase;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import java.sql.CallableStatement;
import java.sql.Types;

public class ReadDatabaseDescriptor_Annotator extends JCasAnnotator_ImplBase {
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
	public static final String PARAM_DATABASE_SERVER_PORT = "DatabaseServerPort";
	
	/**
	 * The database server type
	 */
	public static final String PARAM_DATABASE_TYPE = "DatabaseServerPort";
	
	/**
	 * The optional size of a pull. This is going to pull N rows at a time.
	 */
	public static final String PARAM_BATCH_PROCESS_SIZE = "BatchProcessSize";
	
	private Connection mSqlConnection = null;
	private String mDatabaseServer;
    private String mDatabase;
    private String mUserName;
    private String mPassword;
    
    private String mPort;
    private String mType;
	
	@Override
    public void initialize(UimaContext aContext) throws ResourceInitializationException {
	    super.initialize(aContext);
	    
	    mDatabaseServer = (String) aContext.getConfigParameterValue(PARAM_DATABASE_SERVER);
	    mDatabase = (String) aContext.getConfigParameterValue(PARAM_DATABASE);
	    mUserName = (String) aContext.getConfigParameterValue(PARAM_DATABASE_USER_NAME);
	    mPassword = (String) aContext.getConfigParameterValue(PARAM_DATABASE_PASSWORD);
	    
	    mPort = (String) aContext.getConfigParameterValue(PARAM_DATABASE_SERVER_PORT);
	    mType = (String) aContext.getConfigParameterValue(PARAM_DATABASE_TYPE);
	    
	    if (mPort == null && mType == null) {
	    	throw new ResourceInitializationException("DatabasePort or DatabaseType was not passed as a parameter.",
	                new Object[] { });
	    }
	    
	    if (mPort == null) {
	    	boolean valid_type = false;
	    	if (mType.toLowerCase().equals("mysql")) {
	    		valid_type = true;
	    		mPort = "3306";
	    	}
	    	if (valid_type == false) {
	    	 	throw new ResourceInitializationException("DatabasePort was not passed and DatabaseType is not mysql.",
	                new Object[] { });
	    	}
	    }
	    
	    try {
			mSqlConnection = DriverManager.getConnection(String.format("jdbc:mysql://%s:%s/%s?user=%s&password=%s", 
					mDatabaseServer, mPort, mDatabase, mUserName, mPassword));
		} catch (SQLException e) {
			e.printStackTrace();
			throw new ResourceInitializationException("Cannot connect to database with parameters Server: {0}, Port: {1}, Database: {2}, User Name: {3} or invalid password",
					new Object[] {mDatabaseServer, mPort, mDatabase, mUserName});
		}
	}

	@Override
	public void process(JCas aJCas) throws AnalysisEngineProcessException {
		// TODO Auto-generated method stub
		try {
			CallableStatement sp_call = mSqlConnection.prepareCall("{call select_all_text()}");
			sp_call.registerOutParameter(1, Types.INTEGER);
			sp_call.registerOutParameter(2, Types.VARCHAR);
			boolean has_results = sp_call.execute();
			while (has_results) {
				ResultSet rs = sp_call.getResultSet();
				
				Integer id = rs.getInt(1);
				String text_string = rs.getString(2);
				UnprocessedText text = new UnprocessedText(aJCas);
				text.setTextId(id);
				text.setRawTextString(text_string);
				text.addToIndexes();
				
				has_results = sp_call.getMoreResults();
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			getContext().getLogger().log(Level.WARNING, "Stored procedure call failed.");
		}
	}
}
