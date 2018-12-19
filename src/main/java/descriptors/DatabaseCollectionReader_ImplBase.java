package descriptors;

import java.io.IOException;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import org.apache.uima.cas.CAS;
import org.apache.uima.collection.CollectionException;
import org.apache.uima.collection.CollectionReader_ImplBase;
import org.apache.uima.jcas.JCas;
import org.apache.uima.resource.ResourceInitializationException;
import org.apache.uima.util.Progress;

import database.DatabaseConnector;
import helper.DatabaseHelper;
import objects.DatabaseConnection;

/**
 * @author Ben Holland
 * This class is an abstract class for the Collection_Reader_Cumulation CPE. 
 * I converted this into a superclass to filter out overlap between post-processing
 * analyses that require a source and category document set (cos similarity, search engines, 
 * supervised learning) from those that require only source document sets (LDA, unsupervised
 * learning).  
 */

public abstract class DatabaseCollectionReader_ImplBase extends CollectionReader_ImplBase {
    
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
     * Recreate the model.
     */
    public static final String PARAM_RECREATE_MODEL = "RecreateModel";
    
    /**
     * The model file name. 
     */
    public static final String PARAM_MODEL_FILE_NAME = "ModelFileName";
    
    
    /**
     * The table to use for the model generation. The stored procedure that 
     * we use to create this table is defined below. I called it create_data_table
     * 
     *   CREATE DEFINER=`root`@`localhost` PROCEDURE `create_data_table`(in in_drop_table tinyint, in in_table_name varchar(100))
     *   BEGIN
     *   
     *   #set @col_exists = 0;
     *   
     *   #SELECT 1 FROM INFORMATION_SCHEMA.COLUMNS
     *   #WHERE TABLE_NAME=in_table_name
     *   #and table_schema = database()
     *   #into @col_exists;
     *   
     *       if in_drop_table = 1 then
     *           SET @droptable = CONCAT ('DROP TABLE IF EXISTS  ', in_table_name);
     *           PREPARE deletetb FROM @droptable;
     *           EXECUTE deletetb ;
     *           DEALLOCATE PREPARE deletetb ;
     *       end if;
     *       
     *       
     *       set @col_exists = 0;
     *   
     *       SELECT 1 FROM INFORMATION_SCHEMA.COLUMNS
     *       WHERE TABLE_NAME=in_table_name
     *       and table_schema = database()
     *       into @col_exists;
     *       
     *       if @col_exists <> 1 then
     *           SET @createtable = CONCAT ('CREATE TABLE ', in_table_name, ' (`id` int(11) NOT NULL AUTO_INCREMENT, `csv_id` varchar(100) default NULL, `csv_title` varchar(100) default NULL, `text_string` text NOT NULL, `processed` tinyint(4) NOT NULL DEFAULT \'0\', `num_tokens` int(11) NOT NULL DEFAULT \'0\', PRIMARY KEY (`id`)) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;');
     *           PREPARE createtable FROM @createtable;
     *           EXECUTE createtable ;
     *           DEALLOCATE PREPARE createtable ;
     *       end if;
     *   END
     */
    public static final String PARAM_MODEL_TABLE_NAME = "ModelTableName";
    
    /**
     * Model type. Currently only tf_idf is supported. 
     */
    public static final String PARAM_MODEL_TYPE = "ModelType";
    
    /**
     * The data table. This should be created, like the ModelFileName  
     */
    public static final String PARAM_DATA_TABLE = "DataTable";
    
    /**
     * This is the user that exists in the users table that will log
     * all events from the stack. This user will also have the admin role.  
     */
    protected static final String LOGGING_USER = "nlp_stack";
    protected Integer mLoggingUserId;
    
    protected String mDatabaseServer;
    protected String mDatabase;
    protected String mUserName;
    protected String mPassword;
    
    protected String mPort;
    protected String mType;
    
    protected Boolean mRecreateModel;
    protected String mModelFileName;
    protected String mModelTableName;
    protected String mModelType;
    protected String mDataTable;
    
    @Override
    public void initialize() throws ResourceInitializationException {
        super.initialize();
        try {
            getDatabaseConnector();
        } catch (ClassNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            throw new ResourceInitializationException();
        } catch (SQLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            throw new ResourceInitializationException();
        } catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			throw new ResourceInitializationException();
		}
    }
    
    public DatabaseConnector getDatabaseConnector() throws SQLException, ClassNotFoundException, IOException {
        String database_server = (String) getConfigParameterValue(PARAM_DATABASE_SERVER);
        String database = (String) getConfigParameterValue(PARAM_DATABASE);
        String user_name = (String) getConfigParameterValue(PARAM_DATABASE_USER_NAME);
        String password = (String) getConfigParameterValue(PARAM_DATABASE_PASSWORD);
        
        String port = (String) getConfigParameterValue(PARAM_DATABASE_SERVER_PORT);
        String type = (String) getConfigParameterValue(PARAM_DATABASE_TYPE);
        
        setRecreateModel((Boolean) getConfigParameterValue(PARAM_RECREATE_MODEL));
        setModelFileName((String) getConfigParameterValue(PARAM_MODEL_FILE_NAME));
        
        return getDatabaseConnector(type, database_server, port, database, user_name, password);
    }
    
    /**
     * @todo: unit test this. 
     * @param type
     * @param database_server
     * @param port
     * @param database
     * @param user_name
     * @param password
     * @return
     * @throws SQLException
     * @throws ClassNotFoundException
     * @throws IOException
     */
    public DatabaseConnector getDatabaseConnector(String type, String database_server, 
            String port, String database, String user_name, String password) throws SQLException, ClassNotFoundException, IOException {
        
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
        
        DatabaseConnector connector = new DatabaseConnector(mType, mDatabaseServer, mPort, mDatabase, mUserName, mPassword);	
        if (mLoggingUserId == null) {
        	connector.connect();
        	Connection connection = connector.getConnection();
        	mLoggingUserId = DatabaseHelper.getLoggingUserNameId(connection, LOGGING_USER);
        	connector.setLoggingUserId(mLoggingUserId);
        	connection.close();
        }
        return connector;
    }
    
    public void setRecreateModel(Boolean recreational_model) {
        mRecreateModel = recreational_model;
    }
    
    public void setModelFileName(String model_file_name) {
        mModelFileName = model_file_name;
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
    
    public String getType() {
        return mType;
    }
    
    public JCas addDatabaseToCas(JCas jcas) {
        DatabaseConnection db_conn = new DatabaseConnection(jcas);
        db_conn.setDatabaseServer(mDatabaseServer);
        db_conn.setPort(mPort);
        db_conn.setDatabase(mDatabase);
        db_conn.setUserName(mUserName);
        db_conn.setPassword(mPassword);
        db_conn.setDatabaseType(mType);
        db_conn.setLoggingUserId(mLoggingUserId);
        db_conn.addToIndexes();
        return jcas;
    }
    
    @Override
    public abstract void getNext(CAS aCAS) throws IOException, CollectionException;

    @Override
    public abstract boolean hasNext() throws IOException, CollectionException;

    @Override
    public abstract Progress[] getProgress();
    
    @Override
    public abstract void close() throws IOException;
    
    /**
     * 
     * @param connection
     * @return all text ids corresponding to "category" text.  
     * @throws SQLException
     * 
     * Given a sql connection, get all category text ids. 
     */
    public int[] getCategoryTextIDs(Connection connection) throws SQLException {
        ArrayList<Integer> model_ids = new ArrayList<Integer>();
        ArrayList<String> model_text = new ArrayList<String>();
        CallableStatement select_text = connection.prepareCall("{call select_category_text()}");
        select_text.execute();
        ResultSet rs_model_text = select_text.getResultSet();
        while (rs_model_text.next()) {
            Integer id = rs_model_text.getInt(1);
            assert id != null : "The Text ID is null and it should never be null.";
            String text = rs_model_text.getString(2);
            model_ids.add(id);
            //text = CleanText.Standardize(text);
            //model_text.add(text);
        }
        //This is apparently how to map ArrayList<Integer> to int[];
        //I think it handles the null case but it seems to work. 
        //mModelIDs = model_ids.stream().mapToInt(Integer::intValue).toArray();
        //mModelText = model_text.toArray(new String[0]);
        return model_ids.stream().mapToInt(Integer::intValue).toArray();
    }
    
    /**
     * 
     * @param connection
     * @return all text ids corresponding to "source" text.  
     * @throws SQLException
     * 
     * Given a sql connection, return all source text ids. 
     */
    public ArrayList<Integer> getSourceTextIDs(Connection connection) throws SQLException {
        ArrayList<Integer> ret = new ArrayList<Integer>();
        CallableStatement select_text_ids = connection.prepareCall("{call select_source_text_ids()}");
        select_text_ids.execute();
        ResultSet rs = select_text_ids.getResultSet();
        while (rs.next()) {
            Integer id = rs.getInt(1);
            ret.add(id);
        }
        return ret;
    }
    
    /**
     * 
     * @param connection
     * @param id
     * @return
     * @throws SQLException
     * @throws CollectionException
     * 
     * Given a connection and a text id, return the source text for that id.
     * This should avoid loading all database text into memory. 
     */
    public String getSourceTextFromID(Connection connection, int id) throws SQLException, CollectionException {
        String ret = null;
        CallableStatement select_text = connection.prepareCall("{call select_source_text_from_id(?)}");
        select_text.setInt(1, id);
        select_text.execute();
        ResultSet rs = select_text.getResultSet();
        while (rs.next()) {
            ret = rs.getString(1);
        }
        if (ret == null) {
            throw new CollectionException();
        }
        return ret;
    }
}
