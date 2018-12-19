package tf_idf;

import org.apache.uima.analysis_engine.AnalysisEngine;
import org.apache.uima.fit.factory.AnalysisEngineFactory;
import org.apache.uima.resource.ResourceInitializationException;
import org.deeplearning4j.text.annotator.SentenceAnnotator;
import org.deeplearning4j.text.annotator.StemmerAnnotator;
import org.deeplearning4j.text.annotator.TokenizerAnnotator;
import org.deeplearning4j.text.tokenization.tokenizer.TokenPreProcess;
import org.deeplearning4j.text.tokenization.tokenizer.Tokenizer;
import org.deeplearning4j.text.tokenization.tokenizer.UimaTokenizer;
import org.deeplearning4j.text.tokenization.tokenizerfactory.UimaTokenizerFactory;
import org.deeplearning4j.text.uima.UimaResource;

/**
 * @author ben
 * @TODO: make UimaTokenizerFactory easier to be subclassed. 
 * This is the same implementation for the UimaTokenizerFactory
 * but it will also stem words. 
 */
public class UimaLemmaFactory extends UimaTokenizerFactory {
    
    private static AnalysisEngine defaultAnalysisEngine;
    private UimaResource uimaResource;
    private boolean checkForLabel;
    private TokenPreProcess preProcess;
    
    public UimaLemmaFactory() throws ResourceInitializationException {
        this(defaultAnalysisEngine(), true);
    }

    public UimaLemmaFactory(UimaResource resource) {
        this(resource, true);
    }

    public UimaLemmaFactory(AnalysisEngine tokenizer) throws ResourceInitializationException {
        this(tokenizer, true);
    }

    public UimaLemmaFactory(UimaResource resource, boolean checkForLabel) {
        super(resource, checkForLabel);
        this.uimaResource = resource;
        this.checkForLabel = checkForLabel;
    }

    public UimaLemmaFactory(boolean checkForLabel) throws ResourceInitializationException {
        this(defaultAnalysisEngine(), checkForLabel);
    }

    public UimaLemmaFactory(AnalysisEngine tokenizer, boolean checkForLabel) throws ResourceInitializationException{
        //super();
        this.checkForLabel = checkForLabel;
        try {
            this.uimaResource = new UimaResource(tokenizer);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
    
    public static AnalysisEngine defaultAnalysisEngine() {
        try {
            if (defaultAnalysisEngine == null)
                defaultAnalysisEngine = AnalysisEngineFactory.createEngine(
                                AnalysisEngineFactory.createEngineDescription(
                                        SentenceAnnotator.getDescription(),
                                        TokenizerAnnotator.getDescription(),
                                        StemmerAnnotator.getDescription()));

            return defaultAnalysisEngine;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
    
    @Override
    public Tokenizer create(String toTokenize) {
        if (toTokenize == null)
            throw new IllegalArgumentException("Unable to proceed; on sentence to tokenize");
        Tokenizer ret = new UimaTokenizer(toTokenize, uimaResource, checkForLabel);
        ret.setTokenPreProcessor(preProcess);
        return ret;
    }

    
    
}
