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

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import org.apache.uima.UimaContext;
import org.apache.uima.analysis_component.JCasAnnotator_ImplBase;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.cas.FSIterator;
import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.tcas.Annotation;
import org.apache.uima.resource.ResourceInitializationException;

import database.DatabaseConnector;
import objects.DatabaseConnection;
import objects.Sentence;
import opennlp.tools.dictionary.Dictionary;
import opennlp.tools.namefind.DictionaryNameFinder;
import opennlp.tools.util.Span;
import opennlp.tools.util.StringList;

public class FindNamesAnnotator extends JCasAnnotator_ImplBase {
    
    public static final String PARAM_NAME_FILE = "NameFile";
    
    public static final String PARAM_MED_FILE = "MedFile";
    
    public static final String PARAM_ILLNESS_FILE = "IllnessFile";
    
    public DictionaryNameFinder mNameFinder;
    
    public DictionaryNameFinder mMedFinder;
    
    public DictionaryNameFinder mIllnessFinder;
    
    private Span[] combineSpans(Span[] spans) {
        if (spans.length <= 1) {
            return spans;
        }
        /**
         * span_list is essentially a queue.
         * First, populate the queue.
         * Remove the first object and compare it to all other objects.
         * If there is a chain (end of 1 span is the start of another span) then remove the chaining
         * span (s below) and merge the two.   
         */
        ArrayList<Span> span_list = new ArrayList<Span>();
        ArrayList<Span> return_list = new ArrayList<Span>();
        for (Span s: spans) {
            span_list.add(s);
        }
        
        
        while (span_list.size() > 0) {
            boolean modified = false;
            Span s1 = span_list.remove(0);
            for (Span s: span_list) {
                if (s1.getEnd() == s.getStart()) {
                    modified = true;
                    span_list.remove(s);
                    Span new_span = new Span(s1.getStart(), s.getEnd());
                    span_list.add(0, new_span);
                    break;
                }
            }
            if (modified == false) {
                return_list.add(s1);
            }
        }
        
        Span[] t = new Span[0];
        t = return_list.toArray(t);
        return t;
        
        
    }
    
    @Override
    public void initialize(UimaContext aContext) throws ResourceInitializationException {
        super.initialize(aContext);

        Dictionary NameDict = new Dictionary();
        Dictionary MedDict = new Dictionary();
        Dictionary IllnessDict = new Dictionary();
        
        String NameFileName = (String) getContext().getConfigParameterValue(PARAM_NAME_FILE);
        try (BufferedReader br = new BufferedReader(new FileReader(NameFileName))) {
            
            @SuppressWarnings("unused") //It might unused for now, but not forever. 
            String header = br.readLine(); //first line is always a header
            
            for(String line; (line = br.readLine()) != null; ) {
                String[] s = line.split(" ");
                NameDict.put(new StringList(s));
            }
            mNameFinder = new DictionaryNameFinder(NameDict);
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        
        String MedFileName = (String) getContext().getConfigParameterValue(PARAM_MED_FILE);
        try (BufferedReader br = new BufferedReader(new FileReader(MedFileName))) {

            @SuppressWarnings("unused")
            String header = br.readLine(); //first line is always a header
            
            for(String line; (line = br.readLine()) != null; ) {
                String[] s = line.split(" ");
                MedDict.put(new StringList(s));
            }
            mMedFinder = new DictionaryNameFinder(MedDict);
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        
        String IllnessFileName = (String) getContext().getConfigParameterValue(PARAM_ILLNESS_FILE);
        try (BufferedReader br = new BufferedReader(new FileReader(IllnessFileName))) {

            @SuppressWarnings("unused")
            String header = br.readLine(); //first line is always a header
            
            for(String line; (line = br.readLine()) != null; ) {
                String[] s = line.split(" ");
                IllnessDict.put(new StringList(s));
            }
            mIllnessFinder = new DictionaryNameFinder(IllnessDict);
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
    

    @Override
    public void process(JCas aJCas) throws AnalysisEngineProcessException {
        FSIterator<Annotation> sentences = aJCas.getAnnotationIndex(Sentence.type).iterator();
        
        DatabaseConnection database_connection = DatabaseConnector.GetDatabaseConnection(aJCas);
        if (database_connection == null) {
            throw new AnalysisEngineProcessException("A database object was not configured for this CAS" , new Object[] {} );
        }
        
        try (DatabaseConnector mysql_connector = new DatabaseConnector(database_connection)) {
            mysql_connector.connect();
            Connection sql_connection = mysql_connector.getConnection();
            
            while (sentences.hasNext()) {
                Sentence sentence = (Sentence) sentences.next();
                String[] tokens = sentence.getWords().toArray();
                Span[] names = mNameFinder.find(tokens);
                Span[] meds = mMedFinder.find(tokens);
                Span[] illnesses = mIllnessFinder.find(tokens);
                
                names = combineSpans(names);
                meds = combineSpans(meds);
                illnesses = combineSpans(illnesses);
                
                ArrayList<String> token_sentence = new ArrayList<String>();
                boolean update = false;
                
                if (names.length == 0 && meds.length == 0 && illnesses.length == 0) {
                    for (int x = 0 ; x < tokens.length; x++) {
                        token_sentence.add(tokens[x]);
                    }
                } else {
                    update = true;
                    for (int x = 0; x < tokens.length; x++) {
                        if (names.length > 0) {
                            for (Span s: names) {
                                if (x == s.getStart()) {
                                    token_sentence.add("<START:person>");
                                }
                                if (x == s.getEnd()) {
                                    token_sentence.add("<END>");
                                }
                            }
                        }
                    
                        if (meds.length > 0) {
                            for (Span s: meds) {
                                if (x == s.getStart()) {
                                    token_sentence.add("<START:medication>");
                                }
                                if (x == s.getEnd()) {
                                    token_sentence.add("<END>");
                                }
                            }
                        }
                        
                        if (illnesses.length > 0) {
                            for (Span s: meds) {
                                if (x == s.getStart()) {
                                    token_sentence.add("<START:illness>");
                                }
                                if (x == s.getEnd()) {
                                    token_sentence.add("<END>");
                                }
                            }
                        }
                        
                        token_sentence.add(tokens[x]);                        
                    }
                    
                }
                
                String tokenized_sentence = new String();
                
                for (int x = 0; x < token_sentence.size(); x++) {
                    tokenized_sentence = String.format("%s%s ", tokenized_sentence, token_sentence.get(x));
                }

                if (update == true) {
                    CallableStatement sp_call = sql_connection.prepareCall("{call insert_training_data(?, ?, ?, ?)}");
                    sp_call.setInt(1, sentence.getDocumentID());
                    sp_call.setInt(2,  sentence.getSentence_id());
                    sp_call.setInt(3,  sentence.getSentenceNumber());
                    sp_call.setString(4,  tokenized_sentence);
                    sp_call.execute();
                }
            }
        } catch (SQLException | IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

}



