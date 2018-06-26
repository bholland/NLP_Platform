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
package reader;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;

import org.apache.uima.UimaContext;
import org.apache.uima.analysis_component.JCasAnnotator_ImplBase;
import org.apache.uima.analysis_component.JCasMultiplier_ImplBase;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.cas.AbstractCas;
import org.apache.uima.cas.FSIterator;
import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.tcas.Annotation;
import org.apache.uima.resource.ResourceInitializationException;

import database.DatabaseConnector;
import helper.CleanText;
import helper.DatabaseHelper;
import objects.CsvObject;
import objects.PdfObject;
import objects.UnprocessedText;

public class ReaderAnnotator extends JCasMultiplier_ImplBase {

    /*
    @Override
    public void process(JCas aJCas) throws AnalysisEngineProcessException {
        
    }*/
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
    
    private Reader_ImplBase mReader = null;
    private Integer mListIndex = null; 
    
    private String mDatabaseServer;
    private String mDatabase;
    private String mUserName;
    private String mPassword;
    
    private String mPort;
    private String mType;
    
    //Most of this code was taken from the DatabaseCollectionReader_ImplBase class
    //Perhaps refactor this eventually or create an interface that we could implement. 
    //an interface would actually work really well. 
    
    public DatabaseConnector getDatabaseConnector() throws SQLException, ClassNotFoundException {
        return getDatabaseConnector(mType, mDatabaseServer, mPort, mDatabase, mUserName, mPassword);
    }
    
    public DatabaseConnector getDatabaseConnector(String type, String database_server, 
            String port, String database, String user_name, String password) throws SQLException, ClassNotFoundException {
        
        mDatabaseServer = database_server;
        mDatabase = database;
        mUserName = user_name;
        mPassword = password;
        mPort = port;
        mType = type;
                
        if (mPort == null && mType == null) {
            throw new SQLException();
        }
        if (mPort == null) {
            if (mType.toLowerCase().equals("mysql")) {
                mPort = "3306";
            } else if (mType.toLowerCase().equals("postgresql") || mType.toLowerCase().equals("pgsql")) {
                mPort = "5432";
                mType = "pgsql";
            } else {
                 throw new SQLException();
            }
        } else if (mPort.trim().equals("3306")) {
            mType = "mysql";
        }
        else if (mPort.trim().equals("5432")) {
            mType = "pgsql";
        }
        return new DatabaseConnector(mType, mDatabaseServer, mPort, mDatabase, mUserName, mPassword);
    }
    
    @Override 
    public void initialize(UimaContext aContext) throws ResourceInitializationException {
        super.initialize(aContext);
        mListIndex = 0;
        String database_server = (String) aContext.getConfigParameterValue(PARAM_DATABASE_SERVER);
        String database = (String) aContext.getConfigParameterValue(PARAM_DATABASE);
        String user_name = (String) aContext.getConfigParameterValue(PARAM_DATABASE_USER_NAME);
        String password = (String) aContext.getConfigParameterValue(PARAM_DATABASE_PASSWORD);
        
        String port = (String) aContext.getConfigParameterValue(PARAM_DATABASE_SERVER_PORT);
        String type = (String) aContext.getConfigParameterValue(PARAM_DATABASE_TYPE);
        try {
            DatabaseConnector connector  = getDatabaseConnector(type, database_server, port, database, user_name, password);
            connector.connect();
            Connection connection = connector.getConnection();
            DatabaseHelper.cleanAllText(connection);
        } catch (ClassNotFoundException | SQLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
    
    @Override
    public void process(JCas aJCas) throws AnalysisEngineProcessException {
        FSIterator<Annotation> csv_iterator = aJCas.getAnnotationIndex(CsvObject.type).iterator();
        FSIterator<Annotation> pdf_iterator = aJCas.getAnnotationIndex(PdfObject.type).iterator();
        
        
        if (csv_iterator.hasNext()) {
            CsvObject csv = (CsvObject)csv_iterator.next();
            mReader = new CsvReader(csv);
        } else if (pdf_iterator.hasNext()) {
            PdfObject pdf = (PdfObject)pdf_iterator.next();
            mReader = new PdfReader(pdf);
        }
        mReader.ReadText();
    }
    
    @Override
    public boolean hasNext() throws AnalysisEngineProcessException {
        return mListIndex < mReader.getTextObjects().size();
    }

    @Override
    public AbstractCas next() throws AnalysisEngineProcessException {
        JCas jcas = getEmptyJCas();
        TextObject text_object = mReader.getTextObjects().get(mListIndex++);
        
        
        try (DatabaseConnector connector = getDatabaseConnector()) {
            connector.connect();
            Connection connection = connector.getConnection();
        
            Integer database_text_id = null;
            
            if (text_object.getIsModelData()) {
                database_text_id = DatabaseHelper.insertCategoryText(connection, text_object.getId(), text_object.getText());
            } else {
                database_text_id = DatabaseHelper.insertSourceText(connection, text_object.getId(), text_object.getText());
            }            
    
            String text = text_object.getText();
    
            UnprocessedText ut = new UnprocessedText(jcas);
            ut.setTextId(database_text_id);
            ut.setRawTextString(text); //DO NOT CLEAN THIS TEXT. UnprocessedText SHOULD NEVER BE PROCESSED. 
            ut.addToIndexes();
            
            text = CleanText.Standardize(text);
            jcas.setDocumentText(text);
        } catch (ClassNotFoundException | IOException | SQLException e) {
            e.printStackTrace();
            throw new AnalysisEngineProcessException();
        }
        return jcas;
    }
}
