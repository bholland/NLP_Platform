package helper;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;

import cc.mallet.extract.StringSpan;
import cc.mallet.extract.StringTokenization;
import cc.mallet.pipe.Pipe;
import cc.mallet.types.Instance;
import cc.mallet.types.Token;
import cc.mallet.types.TokenSequence;
import opennlp.tools.lemmatizer.DictionaryLemmatizer;
import opennlp.tools.lemmatizer.LemmatizerME;
import opennlp.tools.lemmatizer.LemmatizerModel;
import opennlp.tools.postag.POSModel;
import opennlp.tools.postag.POSTaggerME;
import opennlp.tools.sentdetect.SentenceDetectorME;
import opennlp.tools.sentdetect.SentenceModel;
import opennlp.tools.tokenize.TokenizerME;
import opennlp.tools.tokenize.TokenizerModel;

public class TextToTokensPipe extends Pipe implements Serializable
{

    /**
     * 
     */
    private static final long serialVersionUID = -8819543455292141725L;
    
    public TextToTokensPipe() { }
    
    public Instance pipe (Instance carrier)
    {
        DictionaryLemmatizer lem = null;
        try (InputStream modelIn = new FileInputStream("/home/ben/workspace/opennlp_processing/external_jar/opennlp.uima.OpenNlpTextAnalyzer/models/en-lemmatizer.dict")) {
          lem = new DictionaryLemmatizer(modelIn);
        } catch (FileNotFoundException e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        } catch (IOException e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        }
        
        POSTaggerME pos = null;
        try (InputStream modelIn = new FileInputStream("/home/ben/workspace/opennlp_processing/external_jar/opennlp.uima.OpenNlpTextAnalyzer/models/en-pos-maxent.bin")) {
            pos = new POSTaggerME(new POSModel(modelIn));
            
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        
        SentenceDetectorME sent = null;
        try (InputStream modelIn = new FileInputStream("/home/ben/workspace/opennlp_processing/external_jar/opennlp.uima.OpenNlpTextAnalyzer/models/en-sent.bin")) {
            sent = new SentenceDetectorME(new SentenceModel(modelIn));
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        /**
         * @TODO Hook this into the annotator pipelines. 
         */
        TokenizerME tokenizer = null;
        try {
            String model_file = "/home/ben/workspace/opennlp_processing/external_jar/opennlp.uima.OpenNlpTextAnalyzer/models/en-token.bin";
            File in_file = new File(model_file);
            FileInputStream is = new FileInputStream(in_file);
            tokenizer = new TokenizerME(new TokenizerModel(is));
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        
        String string = (String)carrier.getData();
        TokenSequence ts = new StringTokenization (string);
        for (String sentence: sent.sentDetect(string)) {
            String[] tokens = tokenizer.tokenize(sentence);
            String[] pos_tags = pos.tag(tokens);
            String[] stems = lem.lemmatize(tokens, pos_tags);
            for (String s : stems) {
                ts.add(new Token(s.toLowerCase()));
            }
        }
        carrier.setData(ts);
        return carrier;
    }
}
