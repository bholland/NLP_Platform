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

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.apache.uima.UimaContext;
import org.apache.uima.analysis_component.JCasAnnotator_ImplBase;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.cas.FSIterator;
import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.cas.BooleanArray;
import org.apache.uima.jcas.cas.StringArray;
import org.apache.uima.jcas.tcas.Annotation;
import org.apache.uima.resource.ResourceInitializationException;
import org.languagetool.AnalyzedToken;
import org.languagetool.AnalyzedTokenReadings;
import org.languagetool.JLanguageTool;
import org.languagetool.chunking.ChunkTag;
import org.languagetool.language.AmericanEnglish;
import org.languagetool.rules.RuleMatch;
import org.languagetool.rules.spelling.SpellingCheckRule;

import database.DatabaseConnector;
import objects.DatabaseConnection;
import objects.PdfObject;
import objects.Sentence;
import objects.UnprocessedText;
import opennlp.tools.chunker.ChunkerME;
import opennlp.tools.chunker.ChunkerModel;
import opennlp.tools.namefind.NameFinderME;
import opennlp.tools.namefind.TokenNameFinderModel;
import opennlp.tools.postag.POSModel;
import opennlp.tools.postag.POSTaggerME;
import opennlp.tools.sentdetect.SentenceDetectorME;
import opennlp.tools.sentdetect.SentenceModel;
import opennlp.tools.tokenize.TokenizerME;
import opennlp.tools.tokenize.TokenizerModel;
import opennlp.tools.util.Span;

import org.languagetool.rules.Category;
import org.languagetool.rules.Rule;



public class SentenceSplittingAnnotator extends JCasAnnotator_ImplBase {
    
    private class SentenceReturn {
        String[] mTokens;
        String[] mTags;
        String[] mChunks;
        ArrayList<String> mNamesList; 
        Span[] mNamesSpan;
        Span[] mHospitalSpan;
        Span[] mIllnessSpan;
        Span[] mPersonSpan;
        
        
        private TokenizerME mTokenizer = null;
        private POSTaggerME mPOSTagger = null;
        private NameFinderME mNameFinder = null;
        private ChunkerME mChunker = null;
        private NameFinderME mHospitalFinder = null;
        private NameFinderME mIllnessFinder = null;
        private NameFinderME mPersonFinder = null;
        
        public SentenceReturn(TokenizerME Tokenizer, POSTaggerME POSTagger,
                ChunkerME Chunker, NameFinderME NameFinder) {
            mTokenizer = Tokenizer;
            mPOSTagger = POSTagger;
            mNameFinder = NameFinder;
            mChunker = Chunker;
        }
        
        public SentenceReturn(TokenizerME Tokenizer, POSTaggerME POSTagger,
                ChunkerME Chunker, NameFinderME HospitalFinder,
                NameFinderME IllnessFinder, NameFinderME PersonFinder) {
            mTokenizer = Tokenizer;
            mPOSTagger = POSTagger;
            mHospitalFinder = HospitalFinder;
            mIllnessFinder = IllnessFinder;
            mPersonFinder = PersonFinder;
            mChunker = Chunker;
        }
        
        public void process(String s) {
            mTokens = mTokenizer.tokenize(s);
            mTags = mPOSTagger.tag(mTokens);
            mChunks = mChunker.chunk(mTokens, mTags);
            mNamesSpan = (mNameFinder != null) ? mNameFinder.find(mTokens) : null;
            mHospitalSpan = (mHospitalFinder != null) ? mHospitalFinder.find(mTokens) : null;
            mIllnessSpan = (mIllnessFinder != null) ? mIllnessFinder.find(mTokens) : null;
            mPersonSpan = (mPersonFinder != null) ? mPersonFinder.find(mTokens) : null;
        }
        
        public String[] getTokens() {
            return mTokens;
        }
        
        public String[] getTags() {
            return mTags;
        }
        
        public String[] getChunks() {
            return mChunks;
        }
        
        public Span[] getNamesSpan() {
            return mNamesSpan;
        }
        
        public Span[] getHospitalSpan() {
            return mHospitalSpan;
        }
        
        public Span[] getIllnessSpan() {
            return mIllnessSpan;
        }
        
        public Span[] getPersonSpan() {
            return mPersonSpan;
        }
    }
    
    /**
     * The sentence model file, probably english. This model file is either provided by OpenNLP
     * of created by OpenNLP sentence model creators.  
     */
    public static final String PARAM_SENTENCE_MODEL_FILE = "SentenceModelFile";
    
    /**
     * Token model file. This is going to be provided by OpenNLP
     * or created using an OpenNLP tokenizer model creator. 
     */
    public static final String PARAM_TOKEN_MODEL_FILE = "TokenModelFile";
    
    /**
     * POSTagger model file. This is going to be provided by OpenNLP
public Span[] getNamesSpan() {
            return mNamesSpan;
        }     * or created using an OpenNLP POSTagger model creator. 
     */
    public static final String PARAM_POS_TAGGER_MODEL_FILE = "POSTagger";
    
    /**
     * NameFinderModelFile. This will be provided by OpenNLP or it 
     * will have to be created by OpenNLP. 
     */
    public static final String PARAM_NAME_FINDER_MODEL_FILE = "NameFinderModelFile";
    
    /**
     * Chunker model file. This will be provided by OpenNLP or it
     * will have to be created by OpenNLP. 
     */
    public static final String PARAM_CHUNKER_MODEL_FILE = "ChunkerModelFile";
    
    public static final String PARAM_HOSPITAL_MODEL_FILE = "HospitalFinderModelFile";
    
    public static final String PARAM_ILLNESS_MODEL_FILE = "IllnessFinderModelFile";
    
    public static final String PARAM_PERSON_MODEL_FILE = "PersonFinderModelFile";
            
    public static final String PARAM_IGNORE_LIST_FILE = "IgnoreListFile";
    
    private SentenceDetectorME mSentenceDetector;
    private TokenizerME mTokenizer;
    private POSTaggerME mPOSTagger;
    private NameFinderME mNameFinder;
    private ChunkerME mChunker;
    
    private NameFinderME mHospitalFinder;
    private NameFinderME mIllnessFinder;
    private NameFinderME mPersonFinder; 
    
    private ArrayList<String> mIgnoreList; 
    
    private BooleanArray convertBooleanArray(Span[] input_span, JCas aJCas, int copy_size) {
        boolean[] span_array = new boolean[copy_size];
        for (int x = 0; x < span_array.length; x++) {
            span_array[x] = false;
        }
        if (input_span != null) {
            for (Span s: input_span) {
                for (int x = s.getStart(); x < s.getEnd(); x++) {
                    span_array[x] = true;
                }
            }
        }
        BooleanArray return_array = new BooleanArray(aJCas, copy_size);
        return_array.copyFromArray(span_array, 0, 0, copy_size);
        return return_array;
    }
    
    
    private int processSentence(JCas aJCas, Sentence sentence, String toProcess, SentenceReturn sr) {
        sr.process(toProcess);
        
        int copy_size = sr.getTokens().length;
        
        StringArray token_array = new StringArray(aJCas, copy_size);
        token_array.copyFromArray(sr.getTokens(), 0, 0, copy_size);
        sentence.setWords(token_array);
        
        StringArray tags_array = new StringArray(aJCas, copy_size);
        tags_array.copyFromArray(sr.getTags(), 0, 0, copy_size);
        sentence.setPos_tags(tags_array);
        
        StringArray chunk_array = new StringArray(aJCas, copy_size);
        chunk_array.copyFromArray(sr.getChunks(), 0, 0, copy_size);
        sentence.setChunks(chunk_array);
        
        ArrayList<String> names_list = new ArrayList<String>();
        boolean[] is_name_list = new boolean[copy_size];
        boolean[] is_hospital_list = new boolean[copy_size];
        boolean[] is_illness_list = new boolean[copy_size];
        boolean[] is_person_list = new boolean[copy_size];
        
        for (int x = 0; x < copy_size; x++) {
            is_name_list[x] = false;
        }
        
        /*
        Span[] names_span = sr.getNamesSpan();
        
        for (Span ns: names_span) {
            String name = new String();
            for (int x = ns.getStart(); x < ns.getEnd(); x++) {
                is_name_list[x] = true;
                name = name.concat(String.format("%s ", sr.getTokens()[x]));
            }
            name = name.substring(0, name.lastIndexOf(" "));
            names_list.add(name);
        }
        StringArray names_array = new StringArray(aJCas, names_list.size());
        if (names_list.size() > 0) {
            String[] tmp = new String[0];
            tmp = names_list.toArray(tmp);
            names_array.copyFromArray(tmp, 0, 0, tmp.length);    
        }
        sentence.setNames(names_array);

        BooleanArray is_name_array = new BooleanArray(aJCas, is_name_list.length);
        is_name_array.copyFromArray(is_name_list, 0, 0, is_name_list.length);
        sentence.setIsName(is_name_array);
        
        
        sentence.setIsName(is_name_array);
        */
        sentence.setIsName(convertBooleanArray(sr.getNamesSpan(), aJCas, copy_size));
        sentence.setIsHospital(convertBooleanArray(sr.getHospitalSpan(), aJCas, copy_size));
        sentence.setIsIllness(convertBooleanArray(sr.getIllnessSpan(), aJCas, copy_size));
        sentence.setIsPerson(convertBooleanArray(sr.getPersonSpan(), aJCas, copy_size));
        
        sentence.addToIndexes();
        return token_array.size();
    }
    
    private String spellcheckText(int text_id, String text) {
        try {
            //This is very important. Check spelling before text gets processed. This will fix
            //annoying issues like a period not followed by a space.  
            JLanguageTool langTool = new JLanguageTool(new AmericanEnglish());
            for (Rule rule : langTool.getAllActiveRules()) {
                if (rule instanceof SpellingCheckRule && mIgnoreList.size() > 0) {
                    ((SpellingCheckRule)rule).addIgnoreTokens(mIgnoreList);
                }
            }
            
            StringBuffer correct_text = new StringBuffer(text);
            int offset = 0;
            List<RuleMatch> matches = langTool.check(text);
            Logger logger = Logger.getRootLogger();
            for (RuleMatch match : matches) {
                Category c = match.getRule().getCategory();
                //System.out.println(String.format("Cat: %s \nMessage: %s \nShort: %s \nSuggestions: %s", c.getName(), match.getMessage(), match.getShortMessage(), match.getSuggestedReplacements() ));
                String recomendation = "";
                if (match.getSuggestedReplacements().size() > 0) {
                    correct_text.replace(match.getFromPos() - offset, match.getToPos() - offset, match.getSuggestedReplacements().get(0));
                    offset += (match.getToPos() - match.getFromPos() - match.getSuggestedReplacements().get(0).length());
                    recomendation = match.getSuggestedReplacements().get(0);
                }
                
                AnalyzedTokenReadings[] tokens = match.getSentence().getTokensWithoutWhitespace();
                for (AnalyzedTokenReadings token: tokens) {
                    if (token.getStartPos() == match.getFromPos()) {
                        for (int x = 0; x < token.getReadingsLength(); x++) {
                            AnalyzedToken t = token.getAnalyzedToken(0);
                            //System.out.println(String.format("%s: %s", t.getToken(), t.getPOSTag()));
                            if (t.getPOSTag() != null && (t.getPOSTag().equals("NNS") || t.getPOSTag().equals("NN"))) {
                                String message = String.format("Error found with text id: %s\n" +
                                        "Message: %s\n" +
                                        "Sentence: %s\n" +
                                        "Problem string: %s\n" +
                                        "Recomendations: %s\n" +
                                        "POS: %s" +
                                        "", text_id, match.getMessage(), match.getSentence(),
                                        text.substring(match.getFromPos(),  match.getToPos()),
                                        recomendation, t.getPOSTag());
                                logger.warn(message);
                                
                            }
                        }
                        
                        /*List<ChunkTag> tags = token.getChunkTags();
                        for (ChunkTag tag: tags) {
                            System.out.println(String.format("Tag: %s", tag.getChunkTag()));
                        }*/
                    }
                }
            }
            text = correct_text.toString();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (NoSuchMethodError e) {
            //I don't know why we are here.
            /*System.out.println(text);
            JLanguageTool langTool = new JLanguageTool(new AmericanEnglish());
            try {
                List<RuleMatch> matches = langTool.check(text);
            } catch (IOException e1) {
                // TODO Auto-generated catch block
                e1.printStackTrace();
            }*/
        }
        return text;
    }
    
    private int processText(JCas aJCas, String text, int text_id) {
        SentenceReturn sr = null;
        if (mHospitalFinder != null) {
            sr = new SentenceReturn(mTokenizer, mPOSTagger, mChunker, mHospitalFinder, mIllnessFinder, mPersonFinder);
        } else {
            sr = new SentenceReturn(mTokenizer, mPOSTagger, mChunker, mNameFinder);
        }
        
        String[] setences = mSentenceDetector.sentDetect(text);
        
        boolean write_out = false;
        if (write_out) {
            try (BufferedWriter file_out = new BufferedWriter(new FileWriter("/home/ben/nlp/sent_detect/en-sent.train", true))) {
                for (String s: setences) {
                    file_out.write(String.format("%s\n", s));
                }
                file_out.write("\n");
            } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
            }
            
        }
        
        int sent_num = 0;
        int num_tokens = 0;
        
        for (String s: setences) {
            Sentence sentence = new Sentence(aJCas);
            sentence.setText_string(s);
            sentence.setDocumentID(text_id);
            sentence.setSentenceNumber(sent_num);
            int num_sent_tokens = processSentence(aJCas, sentence, s, sr);
            sent_num++;
            num_tokens += num_sent_tokens;
        }
        return num_tokens;
    }
    
    private String cleanText(String s) {
        CharSequence br_part = "<br";
        CharSequence br = "<br>";
        CharSequence replace = "\n";
        s = s.replace(br, replace);
        s = s.replace(br_part, replace);
        
        CharSequence br_part_upper = "<BR";
        CharSequence br_upper = "<BR>";
        CharSequence br_upper_escape = "<BR/>";
        
        s = s.replace(br_upper, replace);
        s = s.replace(br_upper_escape, replace);
        s = s.replace(br_part_upper, replace);
        
        CharSequence REASON = "REASON";
        CharSequence Reason = "Reason";
        s = s.replace(REASON, Reason);
        
        CharSequence name = "[NAME]";
        CharSequence name_replace = " [NAME] ";
        s = s.replace(name, name_replace);
        
        CharSequence amp = "&amp";
        CharSequence amp_replace = "&";
        s = s.replace(amp, amp_replace);
        return s;
    }
    
    
    @Override
    public void initialize(UimaContext aContext) throws ResourceInitializationException {
        super.initialize(aContext);
        try {
            String model_file = (String) getContext().getConfigParameterValue(PARAM_SENTENCE_MODEL_FILE);
            File in_file = new File(model_file);
            FileInputStream is = new FileInputStream(in_file);
            mSentenceDetector = new SentenceDetectorME(new SentenceModel(is));
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        
        try {
            String model_file = (String) getContext().getConfigParameterValue(PARAM_TOKEN_MODEL_FILE);
            File in_file = new File(model_file);
            FileInputStream is = new FileInputStream(in_file);
            mTokenizer = new TokenizerME(new TokenizerModel(is));
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        
        try {
            String model_file = (String) getContext().getConfigParameterValue(PARAM_POS_TAGGER_MODEL_FILE);
            File in_file = new File(model_file);
            FileInputStream is = new FileInputStream(in_file);
            mPOSTagger = new POSTaggerME(new POSModel(is));
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        
        try {
            String model_file = (String) getContext().getConfigParameterValue(PARAM_NAME_FINDER_MODEL_FILE);
            File in_file = new File(model_file);
            FileInputStream is = new FileInputStream(in_file);
            mNameFinder = new NameFinderME(new TokenNameFinderModel(is));
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        
        try {
            String model_file = (String) getContext().getConfigParameterValue(PARAM_HOSPITAL_MODEL_FILE);
            File in_file = new File(model_file);
            FileInputStream is = new FileInputStream(in_file);
            mHospitalFinder = new NameFinderME(new TokenNameFinderModel(is));
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        
        try {
            String model_file = (String) getContext().getConfigParameterValue(PARAM_ILLNESS_MODEL_FILE);
            File in_file = new File(model_file);
            FileInputStream is = new FileInputStream(in_file);
            mIllnessFinder = new NameFinderME(new TokenNameFinderModel(is));
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        
        try {
            String model_file = (String) getContext().getConfigParameterValue(PARAM_PERSON_MODEL_FILE);
            File in_file = new File(model_file);
            FileInputStream is = new FileInputStream(in_file);
            mPersonFinder = new NameFinderME(new TokenNameFinderModel(is));
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        
        try {
            String model_file = (String) getContext().getConfigParameterValue(PARAM_CHUNKER_MODEL_FILE);
            File in_file = new File(model_file);
            FileInputStream is = new FileInputStream(in_file);
            mChunker = new ChunkerME(new ChunkerModel(is));
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        
        String ignore_file = (String) getContext().getConfigParameterValue(PARAM_IGNORE_LIST_FILE);
        mIgnoreList = new ArrayList<String>();
        if (ignore_file != null ) {
            try (BufferedReader br = new BufferedReader(new FileReader(ignore_file))) {
                String line;
                while ((line = br.readLine()) != null) {
                    line = line.trim();
                    if (line.length() > 0) {
                        mIgnoreList.add(line);
                    }
                }
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
        
    }
    
    @Override
    public void process(JCas aJCas) throws AnalysisEngineProcessException {
        FSIterator<Annotation> text = aJCas.getAnnotationIndex(UnprocessedText.type).iterator();
        String input_document = aJCas.getDocumentText();
        if (input_document == null) {
            return;
        }
        input_document = cleanText(input_document);
               
        FSIterator<Annotation> ut = aJCas.getAnnotationIndex(UnprocessedText.type).iterator();
        assert (ut.hasNext()) : "Sentence splitting annotator does not have an associated unprocessed text document";

        UnprocessedText raw_text = (UnprocessedText) ut.next();
        Integer unprocessed_text_id = raw_text.getTextId();
        
        Logger logger = Logger.getRootLogger();
        logger.info(String.format("Working on ID: %s", unprocessed_text_id));

        assert(ut.hasNext() == false) : "More than one raw text document exists in the aJCas object.";
        
        /**
         * Clean the database for this document id.
         * 1. Remove all sentences.
         * 2. Remove all sentence metadata (sentence deletion cascades on foreign keys. 
         */
        DatabaseConnection database_connection = DatabaseConnector.GetDatabaseConnection(aJCas);
        if (database_connection == null) {
            throw new AnalysisEngineProcessException("A database object was not configured for this CAS" , new Object[] {} );
        }
        
        try (DatabaseConnector sql_connector = new DatabaseConnector(database_connection)) {
            sql_connector.connect();
            Connection sql_connection = sql_connector.getConnection();
            CallableStatement sp_call = null;
            if (raw_text.getIsSource()) {
                sp_call = sql_connection.prepareCall("{call clean_source_sentences(?)}");
            } else {
                sp_call = sql_connection.prepareCall("{call clean_category_sentences(?)}");
            }
            sp_call.setInt(1, unprocessed_text_id);
            sp_call.execute();
        } catch (SQLException | IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
        
        input_document = spellcheckText(unprocessed_text_id, input_document);
        int num_tokens = processText(aJCas, input_document, unprocessed_text_id);
        raw_text.setNumTokens(num_tokens);
        
        try (DatabaseConnector mysql_connector = new DatabaseConnector(database_connection)) {
            mysql_connector.connect();
            Connection sql_connection = mysql_connector.getConnection();
            CallableStatement sp_call = null;
            if (raw_text.getIsSource()) {
                sp_call = sql_connection.prepareCall("{call insert_source_token_count(?, ?, ?)}");
            } else {
                sp_call = sql_connection.prepareCall("{call insert_category_token_count(?, ?, ?)}");
            }
            sp_call.setInt(1, unprocessed_text_id);
            sp_call.setString(2,  input_document);
            sp_call.setInt(3, num_tokens);
            sp_call.execute();
        } catch (SQLException | IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
        
        logger.info(String.format("Finished ID: %s", unprocessed_text_id));
    }
}
