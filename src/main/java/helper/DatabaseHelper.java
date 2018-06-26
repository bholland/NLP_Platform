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

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.apache.commons.lang.StringEscapeUtils;

public class DatabaseHelper {
    
    private DatabaseHelper() {
        
    }
    
    public static void cleanAllText(Connection connection) throws SQLException {
        CallableStatement clear_text = connection.prepareCall("{call clean_all_text()}");
        clear_text.execute();
    }
    
    public static int insertSourceText(Connection connection, String id, String text) throws SQLException {
        CallableStatement insert_statement = connection.prepareCall("{call insert_source_text(?, ?)}");
        insert_statement.setString(1, id);
        insert_statement.setString(2, text);
        insert_statement.execute();
        ResultSet rs = insert_statement.getResultSet();
        Integer ret = null;
        while (rs.next()) {
            ret = rs.getInt(1);
        }
        
        if (ret == null) {
            throw new SQLException();
        }
        return ret;
    }
    
    public static int insertCategoryText(Connection connection, String id, String text) throws SQLException {
        CallableStatement insert_statement = connection.prepareCall("{call insert_category_text(?, ?)}");
        insert_statement.setString(1, id);
        insert_statement.setString(2, text);
        insert_statement.execute();
        ResultSet rs = insert_statement.getResultSet();
        Integer ret = null;
        while (rs.next()) {
            ret = rs.getInt(1);
        }
        
        if (ret == null) {
            throw new SQLException();
        }
        return ret;
    }
    
    public static String getCategoryTextFromID(Connection connection, int id) throws SQLException {
        CallableStatement select_statement = connection.prepareCall("{call select_source_text_from_id(?)}");
        select_statement.setInt(1, id);
        select_statement.execute();
        
        ResultSet rs = select_statement.getResultSet();
        String ret = null;
        while (rs.next()) {
            ret = rs.getString(1);
        }
        
        if (ret == null) {
            throw new SQLException();
        }
        return ret;
    }
    
    public static String getSourceTextFromID(Connection connection, int id) throws SQLException {
        CallableStatement select_statement = connection.prepareCall("{call select_category_text_from_id(?)}");
        select_statement.setInt(1, id);
        select_statement.execute();
        
        ResultSet rs = select_statement.getResultSet();
        String ret = null;
        while (rs.next()) {
            ret = rs.getString(1);
        }
        
        if (ret == null) {
            throw new SQLException();
        }
        return ret;
    }

}
