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

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import org.apache.uima.UimaContext;
import org.apache.uima.analysis_component.JCasAnnotator_ImplBase;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.cas.FSIterator;
import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.tcas.Annotation;
import org.apache.uima.resource.ResourceInitializationException;

import objects.Sentence;
import opennlp.tools.util.Span;

public class TagElementsBrat extends JCasAnnotator_ImplBase {
    
    public static final String PARAM_OUTPUT_FOLDER = "OutFolder";
    
    private String mOutputFolder;
    
    
    
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
        mOutputFolder = (String) getContext().getConfigParameterValue(PARAM_OUTPUT_FOLDER);
    }
    

    @Override
    public void process(JCas aJCas) throws AnalysisEngineProcessException {
        FSIterator<Annotation> sentences = aJCas.getAnnotationIndex(Sentence.type).iterator();
        
        int text_id = 0;
        if (sentences.hasNext()) {
            Sentence sentence = (Sentence) sentences.next();
            text_id = sentence.getDocumentID();
        }
        
        String file_name = String.format("%s/brat_sentences_%s.txt", mOutputFolder, text_id);
        String ann_file_name = String.format("%s/brat_sentences_%s.ann", mOutputFolder, text_id);
        
        try (BufferedWriter out_file = new BufferedWriter(new FileWriter(file_name));
                BufferedWriter ann_file = new BufferedWriter(new FileWriter(ann_file_name)) ) { 
            sentences.moveToFirst();
            
            //where we are in the output file. 
            int file_offset = 0;
            int t_num = 1;            
            while (sentences.hasNext()) {
                Sentence sentence = (Sentence) sentences.next();
                String[] tokens = sentence.getWords().toArray();
                
                boolean[] isHospital = sentence.getIsHospital().toArray();
                boolean[] isIllness = sentence.getIsIllness().toArray();
                boolean[] isPerson = sentence.getIsPerson().toArray();
                
                if (isHospital.length == 0 && isIllness.length == 0 && isPerson.length == 0) {
                    String tokenized_sentence = "";
                    for (int x = 0; x < tokens.length; x++) {
                        tokenized_sentence = String.format("%s%s ", tokenized_sentence, tokens[x]);
                    }
                    tokenized_sentence = String.format("%s\n", tokenized_sentence);
                    file_offset += tokenized_sentence.length();
                    
                    out_file.write(tokenized_sentence);
                } else {
                    String tokenized_sentence = "";
                    int start = 0;
                    int end = 0;
                    String reference = "";
                    
                    boolean in_hospital= false;
                    boolean in_illness = false;
                    boolean in_person = false;
                            
                    
                    for (int x = 0; x < tokens.length; x++) {
                        if (isHospital[x] == true || in_hospital == true) {
                            if (isHospital[x] == true && in_hospital == false) {
                                in_hospital = true;
                                start = file_offset + tokenized_sentence.length();
                                reference = String.format("%s%s ", reference, tokens[x]);
                            } else if (isHospital[x] == true && in_hospital == true) {
                                reference = String.format("%s%s ", reference, tokens[x]);
                            } else if (isHospital[x] == false && in_hospital == true) {
                                in_hospital = false;
                                end = file_offset + tokenized_sentence.length() - 1;
                                String out = String.format("T%s\thospital %s %s\t%s\n", 
                                        t_num, start, end, reference);
                                
                                try {
                                    ann_file.write(out);
                                } catch (IOException e) {
                                    // TODO Auto-generated catch block
                                    e.printStackTrace();
                                }
                                
                                t_num++;
                                start = 0;
                                end = 0;
                                reference = "";
                            }
                        }
                        
                        if (isIllness[x] == true || in_illness == true) {
                            if (isIllness[x] == true && in_illness == false) {
                                in_illness = true;
                                start = file_offset + tokenized_sentence.length();
                                reference = String.format("%s%s ", reference, tokens[x]);
                            } else if (isIllness[x] == true && in_illness == true) {
                                reference = String.format("%s%s ", reference, tokens[x]);
                            } else if (isIllness[x] == false && in_illness == true) {
                                in_illness = false;
                                end = file_offset + tokenized_sentence.length() - 1;
                                String out = String.format("T%s\tillness %s %s\t%s\n", 
                                        t_num, start, end, reference);
                                
                                try {
                                    ann_file.write(out);
                                } catch (IOException e) {
                                    // TODO Auto-generated catch block
                                    e.printStackTrace();
                                }
                                
                                t_num++;
                                start = 0;
                                end = 0;
                                reference = "";
                            }
                        }
                        
                        if (isPerson[x] == true || in_person == true) {
                            if (isPerson[x] == true && in_person == false) {
                                in_person = true;
                                start = file_offset + tokenized_sentence.length();
                                reference = String.format("%s%s ", reference, tokens[x]);
                            } else if (isPerson[x] == true && in_person == true) {
                                reference = String.format("%s%s ", reference, tokens[x]);
                            } else if (isPerson[x] == false && in_person == true) {
                                in_person = false;
                                end = file_offset + tokenized_sentence.length() - 1;
                                String out = String.format("T%s\tperson %s %s\t%s\n", 
                                        t_num, start, end, reference);
                                
                                try {
                                    ann_file.write(out);
                                } catch (IOException e) {
                                    // TODO Auto-generated catch block
                                    e.printStackTrace();
                                }
                                
                                t_num++;
                                start = 0;
                                end = 0;
                                reference = "";
                            }
                        }
                        tokenized_sentence = String.format("%s%s ", tokenized_sentence, tokens[x]);
                    }
                    out_file.write(tokenized_sentence);
                    file_offset += tokenized_sentence.length();
                }
            }
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        
        
    }

}



