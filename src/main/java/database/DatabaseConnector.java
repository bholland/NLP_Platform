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
package database;

import java.io.Closeable;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import org.apache.uima.cas.FSIterator;
import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.tcas.Annotation;

import objects.DatabaseConnection;

public class DatabaseConnector implements Closeable{
    
    private Connection mSqlConnection;
    private String mConnectionStr;
    private String mUrl;
    
    
    /**
     * @param type: the type of the database to connect to. This should be either mysql or pgsql. 
     * @param database_server: the server to connect to (e.g., localhost)
     * @param port: the port to connect to (MySQL defaults to 3306) 
     * @param database: the database name
     * @param user_name: the database user name used when we connect to the database 
     * @param password: the database user's password
     * @throws ClassNotFoundException 
     */
    
    public DatabaseConnector(String type, String database_server, String port, String database, String user_name, String password) throws ClassNotFoundException {
        setStrings(type, database_server, port, database, user_name, password);
         
    }
    
    public DatabaseConnector(DatabaseConnection database_connection) throws ClassNotFoundException {
        String type = database_connection.getDatabaseType();
        String database_server = database_connection.getDatabaseServer();
        String port = database_connection.getPort();
        String database = database_connection.getDatabase();
        String user_name = database_connection.getUserName();
        String password = database_connection.getPassword();
        
        setStrings(type, database_server, port, database, user_name, password);
    }
    
    private void setStrings(String type, String database_server, String port, String database, String user_name, String password) throws ClassNotFoundException {
        if (type.equals("mysql")) {
            Class.forName("org.mysql.Driver");
            mConnectionStr = String.format("jdbc:mysql://%s:%s/%s?user=%s&password=%s&useSSL=false",
                    database_server, port, database, user_name, password);
            mUrl = String.format("jdbc:mysql://%s:%s/%s", database_server, port, database);
        } else if (type.equals("pgsql")) {
            Class.forName("org.postgresql.Driver");
            mConnectionStr = String.format("jdbc:postgresql://%s:%s/%s?user=%s&password=%s&useSSL=false",
                    database_server, port, database, user_name, password);
            mUrl = String.format("jdbc:postgresql://%s:%s/%s", database_server, port, database);
        }
    }
    
    public void connect() throws SQLException {
        mSqlConnection = DriverManager.getConnection(mConnectionStr);
    }
    
    public Connection getConnection() {
        return mSqlConnection;
    }
    
    public String getConenctionString() {
        return mConnectionStr;
    }
    
    public String getUrl() {
        return mUrl;
    }
    
    static public DatabaseConnection GetDatabaseConnection(JCas aJCas) {
        FSIterator<Annotation> dc = aJCas.getAnnotationIndex(DatabaseConnection.type).iterator();
        if (dc.hasNext()) {
            DatabaseConnection database_connection = (DatabaseConnection) dc.next();
            assert (dc.hasNext() == false) : "Only 1 database connection allowed per JCas object";
            return database_connection;
        }
        return null;
    }

    @Override
    public void close() throws IOException {
        if (mSqlConnection != null) {
            try {
                mSqlConnection.close();
            } catch (SQLException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
        
    }
}
