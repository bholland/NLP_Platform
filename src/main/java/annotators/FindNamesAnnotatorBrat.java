package annotators;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
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
import opennlp.tools.dictionary.Dictionary;
import opennlp.tools.namefind.DictionaryNameFinder;
import opennlp.tools.util.Span;
import opennlp.tools.util.StringList;

public class FindNamesAnnotatorBrat extends JCasAnnotator_ImplBase {
    
    public static final String PARAM_NAME_FILE = "NameFile";
    
    public static final String PARAM_MED_FILE = "MedFile";
    
    public static final String PARAM_ILLNESS_FILE = "IllnessFile";
    
    public static final String PARAM_OUTPUT_FOLDER = "OutFolder";
    
    private DictionaryNameFinder mNameFinder;
    
    private DictionaryNameFinder mMedFinder;
    
    private DictionaryNameFinder mIllnessFinder;
    
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

        Dictionary NameDict = new Dictionary();
        Dictionary MedDict = new Dictionary();
        Dictionary IllnessDict = new Dictionary();
        
        String NameFileName = (String) getContext().getConfigParameterValue(PARAM_NAME_FILE);
        try (BufferedReader br = new BufferedReader(new FileReader(NameFileName))) {
            
            @SuppressWarnings("unused")
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
        
        
        if (text_id < 10) {
            file_name = String.format("%s/brat_sentences_0000%s.txt", mOutputFolder, text_id);
            ann_file_name = String.format("%s/brat_sentences_0000%s.ann", mOutputFolder, text_id);
        } else if (text_id < 100) {
            file_name = String.format("%s/brat_sentences_000%s.txt", mOutputFolder, text_id);
            ann_file_name = String.format("%s/brat_sentences_000%s.ann", mOutputFolder, text_id);
        } else if (text_id < 1000) {
            file_name = String.format("%s/brat_sentences_00%s.txt", mOutputFolder, text_id);
            ann_file_name = String.format("%s/brat_sentences_00%s.ann", mOutputFolder, text_id);
        
        } else {
            file_name = String.format("%s/brat_sentences_%s.txt", mOutputFolder, text_id);
            ann_file_name = String.format("%s/brat_sentences_%s.ann", mOutputFolder, text_id);
        }
        
        try (BufferedWriter out_file = new BufferedWriter(new FileWriter(file_name));
                BufferedWriter ann_file = new BufferedWriter(new FileWriter(ann_file_name)) ) { 
            sentences.moveToFirst();
            
            //where we are in the output file. 
            int file_offset = 0;
            int t_num = 1;            
            while (sentences.hasNext()) {
                Sentence sentence = (Sentence) sentences.next();
                String[] tokens = sentence.getWords().toArray();
                Span[] names = mNameFinder.find(tokens);
                Span[] meds = mMedFinder.find(tokens);
                Span[] illnesses = mIllnessFinder.find(tokens);
                
                names = combineSpans(names);
                meds = combineSpans(meds);
                illnesses = combineSpans(illnesses);
                
                if (names.length == 0 && meds.length == 0 && illnesses.length == 0) {
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
                    
                    for (int x = 0; x < tokens.length; x++) {
                        if (names.length > 0) {
                            for (Span s: names) {
                                if (x == s.getStart()) {
                                    start = file_offset + tokenized_sentence.length();
                                }
                                if (x == s.getEnd()) {
                                    //start of the line + the length of the current sentence - space
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
                                
                                if (x >= s.getStart() && x < s.getEnd()) {
                                    reference = String.format("%s%s ", reference, tokens[x]);
                                }
                            }
                        }
                    
                        if (meds.length > 0) {
                            for (Span s: meds) {
                                if (x == s.getStart()) {
                                    start = file_offset + tokenized_sentence.length();
                                }
                                if (x == s.getEnd()) {
                                    //start of the line + the length of the current sentence - space
                                    end = file_offset + tokenized_sentence.length() - 1;
                                    String out = String.format("T%s\tmedication %s %s\t%s\n", 
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
                                
                                if (x >= s.getStart() && x < s.getEnd()) {
                                    reference = String.format("%s%s ", reference, tokens[x]);
                                }
                            }
                        }
                        
                        if (illnesses.length > 0) {
                            for (Span s: illnesses) {
                                if (x == s.getStart()) {
                                    start = file_offset + tokenized_sentence.length();
                                }
                                if (x == s.getEnd()) {
                                    //start of the line + the length of the current sentence - space
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
                                
                                if (x >= s.getStart() && x < s.getEnd()) {
                                    reference = String.format("%s%s ", reference, tokens[x]);
                                }
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



