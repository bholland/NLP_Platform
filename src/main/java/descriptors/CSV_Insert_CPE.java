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

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;

import org.apache.uima.cas.CAS;
import org.apache.uima.cas.CASException;
import org.apache.uima.collection.CollectionException;
import org.apache.uima.jcas.JCas;
import org.apache.uima.resource.ResourceInitializationException;
import org.apache.uima.util.Progress;
import org.apache.uima.util.ProgressImpl;

import database.DatabaseConnector;
import helper.CleanText;
import helper.DatabaseHelper;
import objects.UnprocessedText;

import org.apache.commons.lang.StringEscapeUtils;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;

import java.sql.*;


public class CSV_Insert_CPE extends DatabaseCollectionReader_ImplBase {
    
    /**
     * The CSV file path.   
     */
    public static final String PARAM_CSV_FILE = "CsvFile";

    /**
     * The CSV id column. This ID will not be used as the ID of the database. 
     */
    public static final String PARAM_CSV_ID_COLUMN = "CsvIdColumn";
    
    /**
     * The CSV text column.   
     */
    public static final String PARAM_CSV_TEXT_COLUMN = "CsvTextColumn";
    
    /**
     * This is true if the associated csv file is model data.  
     */
    public static final String PARAM_IS_MODEL_DATA = "IsModelData";
    
    private String[] mCsvFile;
    private String[] mCsvIdColumn;
    private String[] mCsvTextColumn;
    private Boolean[] mIsModelData;

    private ArrayList<Integer> mTextIDs;
    private ArrayList<Boolean> mIsSource;
    private int mTextIndex;

    public void insertCSVFile(Connection connection, String csv_file, String id_column, String text_column, boolean is_model_text) throws SQLException, FileNotFoundException, IOException {
        try (Reader csv_file_input = new FileReader(csv_file)) {
            CSVParser parser = new CSVParser(csv_file_input, CSVFormat.EXCEL.withHeader());
            for (CSVRecord record : parser) {
                
                String id = StringEscapeUtils.escapeSql(record.get(id_column));
                String text = StringEscapeUtils.escapeSql(record.get(text_column));
                
                if (is_model_text == false) {
                    int text_id = DatabaseHelper.insertSourceText(connection, id, text);
                    mTextIDs.add(text_id);
                    mIsSource.add(true);
                } else {
                    int text_id = DatabaseHelper.insertCategoryText(connection, id, text);
                    mTextIDs.add(text_id);
                    mIsSource.add(false);
                }
            }
        }
    }
    
    @Override
    public void initialize() throws ResourceInitializationException {
        mCsvFile = (String[]) getConfigParameterValue(PARAM_CSV_FILE);
        mCsvIdColumn = (String[]) getConfigParameterValue(PARAM_CSV_ID_COLUMN);
        mCsvTextColumn = (String[]) getConfigParameterValue(PARAM_CSV_TEXT_COLUMN);
        mIsModelData = (Boolean[]) getConfigParameterValue(PARAM_IS_MODEL_DATA);
        
        mTextIDs = new ArrayList<Integer>();
        mIsSource = new ArrayList<Boolean>();
        mTextIndex = 0;
        
        try (DatabaseConnector connector = getDatabaseConnector()) { 
            connector.connect();
            Connection connection = connector.getConnection();
            /**
             * create_data_table
             * in in_drop_table tinyint
             * in in_table_name varchar(100)
             */
            
            DatabaseHelper.cleanAllText(connection);
            for (int x = 0; x < mCsvFile.length; x++) {
                insertCSVFile(connection, mCsvFile[x], mCsvIdColumn[x], mCsvTextColumn[x], mIsModelData[x]);
            }
        } catch (IOException | SQLException | ClassNotFoundException e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
            throw new ResourceInitializationException();
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
        int current_index = mTextIndex++;
        Integer text_id = mTextIDs.get(current_index);
        boolean is_source = mIsSource.get(current_index);
        try (DatabaseConnector connector = getDatabaseConnector()) {
            connector.connect();
            Connection connection = connector.getConnection();
            String text = null;
            if (is_source) {
                text = DatabaseHelper.getSourceTextFromID(connection, text_id);
            } else {
                text = DatabaseHelper.getCategoryTextFromID(connection, text_id);
            }
            
            jcas.setDocumentText(CleanText.Standardize(text));
            UnprocessedText unprocessed_text = new UnprocessedText(jcas);
            unprocessed_text.setTextId(text_id);
            unprocessed_text.setRawTextString(text);
            unprocessed_text.setIsSource(is_source);
            unprocessed_text.addToIndexes();
            
            jcas = addDatabaseToCas(jcas);
        } catch (SQLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            throw new CollectionException();
        } catch (ClassNotFoundException e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        }
    }

    @Override
    public void close() throws IOException { 
        // TODO Auto-generated method stub

    }

    @Override
    public Progress[] getProgress() {
        return new Progress[] { new ProgressImpl(mTextIndex, mTextIDs.size(), Progress.ENTITIES) };
    }

    @Override
    public boolean hasNext() throws IOException, CollectionException {
        return mTextIndex < mTextIDs.size();
    }
}
