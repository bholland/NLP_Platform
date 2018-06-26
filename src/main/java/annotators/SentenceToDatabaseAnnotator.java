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
package annotators;

import java.io.IOException;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Types;

import org.apache.uima.UimaContext;
import org.apache.uima.analysis_component.JCasAnnotator_ImplBase;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.cas.FSIterator;
import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.cas.BooleanArray;
import org.apache.uima.jcas.cas.StringArray;
import org.apache.uima.jcas.tcas.Annotation;
import org.apache.uima.resource.ResourceInitializationException;

import database.DatabaseConnector;
import objects.DatabaseConnection;
import objects.Sentence;
import opennlp.tools.util.Span;

public class SentenceToDatabaseAnnotator extends JCasAnnotator_ImplBase {
    
    @Override
    public void initialize(UimaContext aContext) throws ResourceInitializationException {
        super.initialize(aContext);
    }
    
    private void InsertSentence(objects.Sentence sentence, DatabaseConnector mysql_connector) throws SQLException {
        Integer unprocessed_text_id = sentence.getDocumentID();
        String sentence_string = sentence.getText_string();
        
        Connection sql_connection = mysql_connector.getConnection();
        CallableStatement sp_call = sql_connection.prepareCall("{call insert_sentence(?, ?, ?, ?)}");
        sp_call.setString(1, sentence_string);
        sp_call.setInt(2, unprocessed_text_id);
        sp_call.setInt(3, sentence.getSentenceNumber());
        sp_call.registerOutParameter(4, Types.INTEGER);
        boolean has_results = sp_call.execute();
        Integer sentence_id = sp_call.getInt(4);
        sentence.setSentence_id(sentence_id);
        
        /*StringArray names = sentence.getNames();
        if (names.size() == 0) {
            return;
        }*/
        
        StringArray words = sentence.getWords();
        StringArray tags = sentence.getPos_tags();
        StringArray chunks = sentence.getChunks();
        BooleanArray is_names = sentence.getIsName();
        
        for (int x = 0; x < words.size(); x++) {
            sp_call = sql_connection.prepareCall("{call insert_sentence_metadata(?, ?, ?, ?, ?, ?)}");
            sp_call.setInt(1, sentence_id);
            sp_call.setInt(2, x);
            sp_call.setString(3, words.get(x));
            sp_call.setString(4, tags.get(x));
            sp_call.setString(5, chunks.get(x));
            sp_call.setBoolean(6, is_names.get(x));
            try {
                has_results = sp_call.execute();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
    
    @Override
    public void process(JCas aJCas) throws AnalysisEngineProcessException {
        String input_document = aJCas.getDocumentText();
        if (input_document == null) {
            return;
        }
        DatabaseConnection database_connection = DatabaseConnector.GetDatabaseConnection(aJCas);
        if (database_connection == null) {
            throw new AnalysisEngineProcessException("A database object was not configured for this CAS" , new Object[] {} );
        }
        
        FSIterator<Annotation> sentence_iterator = aJCas.getAnnotationIndex(Sentence.type).iterator();
        assert (sentence_iterator.hasNext()) : "No setences are assocaited with this CAS object.";
        
        try (DatabaseConnector connector = new DatabaseConnector(database_connection)) {
            connector.connect();
            while (sentence_iterator.hasNext()) {
                Sentence sentence = (Sentence) sentence_iterator.next();
                InsertSentence(sentence, connector);
            }
        } catch (SQLException | IOException | ClassNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            throw new AnalysisEngineProcessException();
        }
    }

}
