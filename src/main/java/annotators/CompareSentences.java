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
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;

import org.apache.uima.analysis_component.JCasAnnotator_ImplBase;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.cas.FSIterator;
import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.tcas.Annotation;
import org.apache.uima.util.Level;

import database.DatabaseConnector;
import objects.DatabaseConnection;
import objects.Sentence;
import opennlp.tools.util.Span;

public class CompareSentences extends JCasAnnotator_ImplBase {

    @Override
    public void process(JCas aJCas) throws AnalysisEngineProcessException {
        /**
         * Clean the database for this document id.
         * 1. Remove all sentences.
         * 2. Remove all sentence metadata (sentence deletion cascades on foreign keys. 
         */
        DatabaseConnection database_connection = DatabaseConnector.GetDatabaseConnection(aJCas);
        if (database_connection == null) {
            throw new AnalysisEngineProcessException("A database object was not configured for this CAS" , new Object[] {} );
        }
        
        try (DatabaseConnector mysql_connector = new DatabaseConnector(database_connection)) {
            mysql_connector.connect();
        } catch (SQLException | IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
        
        HashMap<Integer, HashMap<Integer, ArrayList<Sentence>>> dict = new HashMap<Integer, HashMap<Integer, ArrayList<Sentence>>>();
        
        FSIterator<Annotation> sentences = aJCas.getAnnotationIndex(Sentence.type).iterator();
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
         ******************************************************************************/        while (sentences.hasNext()) {
            Sentence sentence = (Sentence) sentences.next();
            
            if (dict.get(sentence.getDocumentID()) == null) {
                dict.put(sentence.getDocumentID(), new HashMap<Integer, ArrayList<Sentence>>());
            }
            HashMap<Integer, ArrayList<Sentence>> s = dict.get(sentence.getDocumentID());
            if (s.get(sentence.getSentenceNumber()) == null) {
                s.put(sentence.getSentenceNumber(), new ArrayList<Sentence>());
            }
            ArrayList<Sentence> sentence_list = s.get(sentence.getSentenceNumber());
            sentence_list.add(sentence);
        }
        
        for (Integer document_id: dict.keySet()) {
            int successes = 0;
            int failures = 0;
            int warnings = 0;
            
            getContext().getLogger().log(Level.INFO, String.format("Text_id: %s", document_id));
            HashMap<Integer, ArrayList<Sentence>> sentence_map = dict.get(document_id);
            for (Integer sentence_id: sentence_map.keySet()) {
                ArrayList<Sentence> sentence_list = sentence_map.get(sentence_id);
                Sentence s1 = sentence_list.get(0);
                Sentence s2 = sentence_list.get(1);
                
                if (s2.getText_string().contains("[NAME]")) {
                    Sentence tmp = s1;
                    s1 = s2;
                    s2 = tmp;
                }
                
                
                
                
                //if [NAME] exists in the sentence, s1 ALWAYS contains the processed text;
                int x_s1 = 0;
                int x_s2 = 0;
                String[] token_s1 = s1.getWords().toArray();
                String[] token_s2 = s2.getWords().toArray();
                boolean[] isName_s1 = s1.getIsName().toArray();
                boolean[] isName_s2 = s2.getIsName().toArray();

                boolean problem = false;
                ArrayList<Span> names_span = new ArrayList<Span>();
                while (x_s1 < token_s1.length && x_s2 < token_s2.length) {
                    String t1 = token_s1[x_s1];
                    String t2 = token_s2[x_s2];
                    
                    if (t1.equals(t2)) {
                        x_s1++;
                        x_s2++;
                        continue;
                    }
                    
                    int start_name_span = 0;
                    int end_name_span = 0;
                    
                    if (t1.equals("[NAME]")) {
                        int s1_add = 1;
                        int s2_add = 0;
                        
                        //span is [s2_add, num_tokens);
                        if (x_s1 + s1_add >= token_s1.length) {
                            start_name_span = s2_add;
                            end_name_span = token_s2.length-x_s2;
                        } else {
                            start_name_span = s2_add;
                            while (!t2.equals(token_s1[x_s1+s1_add])) {
                                s2_add++;
                                if (x_s2 + s2_add < token_s2.length) {
                                    t2 = token_s2[x_s2 + s2_add];
                                } else {
                                    s2_add = 0;
                                    s1_add++;
                                    t2 = token_s2[x_s2 + s2_add];
                                    if (x_s1 + s1_add >= token_s1.length) {
                                        problem = true;
                                        getContext().getLogger().log(Level.SEVERE, String.format(
                                                "Document: %s Sentence: %s - Tokens cannot find matches after finding a [NAME].",
                                                document_id, sentence_id));
                                        break;
                                    }
                                }
                            }
                            end_name_span = s2_add;
                        }
                        if (problem == true) {
                            break;
                        }
                        
                        start_name_span = x_s2 + start_name_span;
                        end_name_span = x_s2 + end_name_span;
                        names_span.add(new Span(start_name_span, end_name_span));
                        
                        for (int x = start_name_span; x < end_name_span; x++) {
                            
                            if (isName_s2[x] == true) {
                                String s = String.format(
                                        "SUCCESS: Token \"%s\" is redacted as a name and defined as a name", token_s2[x]);
                                getContext().getLogger().log(Level.INFO, String.format("Document: %s Sentence: %s - %s", document_id, sentence_id, s));
                                
                                try (DatabaseConnector mysql_connector = new DatabaseConnector(database_connection)) {
                                    mysql_connector.connect();
                                    Connection sql_connection = mysql_connector.getConnection();
                                    CallableStatement sp_call = sql_connection.prepareCall("{call log(?, ?, ?, ?, ?)}");
                                    sp_call.setInt(1,  Level.INFO_INT);
                                    sp_call.setString(2, Level.INFO.toString());
                                    sp_call.setInt(3, document_id);
                                    sp_call.setInt(4, sentence_id);
                                    sp_call.setString(5, s);
                                    sp_call.execute();
                                } catch (SQLException | IOException | ClassNotFoundException e) {
                                    e.printStackTrace();
                                }
                                
                                successes++;
                            } else {
                                String s = String.format(
                                        "FAILURE: Token \"%s\" is redacted as a name but NOT defined as a name", token_s2[x]);
                                getContext().getLogger().log(Level.INFO, String.format("Document: %s Sentence: %s - %s", document_id, sentence_id, s));
                                
                                try (DatabaseConnector mysql_connector = new DatabaseConnector(database_connection)) {
                                    
                                    mysql_connector.connect();
                                    Connection sql_connection = mysql_connector.getConnection();
                                    CallableStatement sp_call = sql_connection.prepareCall("{call log(?, ?, ?, ?, ?)}");
                                    sp_call.setInt(1,  Level.INFO_INT);
                                    sp_call.setString(2, Level.INFO.toString());
                                    sp_call.setInt(3, document_id);
                                    sp_call.setInt(4, sentence_id);
                                    sp_call.setString(5, s);
                                    sp_call.execute();
                                } catch (SQLException | IOException | ClassNotFoundException e) {
                                    e.printStackTrace();
                                }
                                
                                failures++;
                            }

                        }
                        
                        
                        x_s1 = x_s1 + s1_add;
                        x_s2 = x_s2 + s2_add;
                    } else {
                        String s = String.format(
                                "Strings do not match. Quitting document.\n" + 
                                        "s1: %s\n" + 
                                        "s2: %s\n\n", s1.getText_string(), s2.getText_string());
                        getContext().getLogger().log(Level.SEVERE, String.format("Document: %s Sentence: %s - %s", document_id, sentence_id, s));
                        
                        try (DatabaseConnector mysql_connector = new DatabaseConnector(database_connection)) {
                            mysql_connector.connect();
                            Connection sql_connection = mysql_connector.getConnection();
                            CallableStatement sp_call = sql_connection.prepareCall("{call log(?, ?, ?, ?, ?)}");
                            sp_call.setInt(1,  Level.SEVERE_INT);
                            sp_call.setString(2, Level.SEVERE.toString());
                            sp_call.setInt(3, document_id);
                            sp_call.setInt(4, sentence_id);
                            sp_call.setString(5, s);
                            sp_call.execute();
                        } catch (SQLException | IOException | ClassNotFoundException e) {
                            e.printStackTrace();
                        }
                        
                        
                        warnings++;
                        problem = true;
                        break;
                    }
                }
                if (problem == true) {
                    break; //irrecoverable problem, break to next document.
                }
                
                for (int x = 0; x < isName_s2.length; x++) {
                    if (isName_s2[x] == true) {
                        boolean in_spans = false; 
                        for (Span s: names_span) {
                            if (x >= s.getStart() && x < s.getEnd()) {
                                in_spans = true;
                                break;
                            }
                        }
                        
                        if (in_spans == false) {
                            try (DatabaseConnector mysql_connector = new DatabaseConnector(database_connection)) {
                                String s = String.format("POSSIBLE ERROR: Token \"%s\" is not redacted but is tagged as a name. ", 
                                        token_s2[x]);
                                getContext().getLogger().log(Level.WARNING, String.format("Document: %s Sentence: %s - %s", document_id, sentence_id, s));
                            
                                
                                mysql_connector.connect();
                                Connection sql_connection = mysql_connector.getConnection();
                                CallableStatement sp_call = sql_connection.prepareCall("{call log(?, ?, ?, ?, ?)}");
                                sp_call.setInt(1,  Level.SEVERE_INT);
                                sp_call.setString(2, Level.SEVERE.toString());
                                sp_call.setInt(3, document_id);
                                sp_call.setInt(4, sentence_id);
                                sp_call.setString(5, s);
                                sp_call.execute();
                            } catch (SQLException | IOException | ClassNotFoundException e) {
                                e.printStackTrace();
                            }
                            
                            warnings++;
                        }
                    }
                }
            }
            
            try (DatabaseConnector mysql_connector = new DatabaseConnector(database_connection)) {
                mysql_connector.connect();
                Connection sql_connection = mysql_connector.getConnection();
                CallableStatement sp_call = sql_connection.prepareCall("{call update_test_data(?, ?, ?, ?)}");
                sp_call.setInt(1, document_id);
                sp_call.setInt(2, successes);
                sp_call.setInt(3, failures);
                sp_call.setInt(4, warnings);
                sp_call.execute();
            } catch (SQLException | IOException | ClassNotFoundException e) {
                e.printStackTrace();
            }
        }
    }

}
