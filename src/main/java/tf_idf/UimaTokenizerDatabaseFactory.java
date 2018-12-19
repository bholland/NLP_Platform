package tf_idf;

import org.apache.uima.analysis_engine.AnalysisEngine;
import org.apache.uima.fit.factory.AnalysisEngineFactory;
import org.apache.uima.resource.ResourceInitializationException;
import org.deeplearning4j.text.annotator.SentenceAnnotator;
import org.deeplearning4j.text.annotator.TokenizerAnnotator;
import org.deeplearning4j.text.tokenization.tokenizer.TokenPreProcess;
import org.deeplearning4j.text.tokenization.tokenizer.Tokenizer;
import org.deeplearning4j.text.tokenization.tokenizer.UimaTokenizer;
import org.deeplearning4j.text.tokenization.tokenizerfactory.TokenizerFactory;
import org.deeplearning4j.text.uima.UimaResource;

import java.io.InputStream;


/**
 * Uses a uima {@link AnalysisEngine} to 
 * tokenize text.
 *
 *
 * @author Adam Gibson
 *
 */
public class UimaTokenizerDatabaseFactory implements TokenizerFactory {

    private UimaResource uimaResource;
    private boolean checkForLabel;
    private static AnalysisEngine defaultAnalysisEngine;
    private TokenPreProcess preProcess;

    public UimaTokenizerDatabaseFactory() throws ResourceInitializationException {
        this(defaultAnalysisEngine(), true);
    }

    public UimaTokenizerDatabaseFactory(UimaResource resource) {
        this(resource, true);
    }

    public UimaTokenizerDatabaseFactory(AnalysisEngine tokenizer) {
        this(tokenizer, true);
    }

    public UimaTokenizerDatabaseFactory(UimaResource resource, boolean checkForLabel) {
        this.uimaResource = resource;
        this.checkForLabel = checkForLabel;
    }

    public UimaTokenizerDatabaseFactory(boolean checkForLabel) throws ResourceInitializationException {
        this(defaultAnalysisEngine(), checkForLabel);
    }

    public UimaTokenizerDatabaseFactory(AnalysisEngine tokenizer, boolean checkForLabel) {
        super();
        this.checkForLabel = checkForLabel;
        try {
            this.uimaResource = new UimaResource(tokenizer);
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

    public UimaResource getUimaResource() {
        return uimaResource;
    }


    /**
     * Creates a tokenization,/stemming pipeline
     * @return a tokenization/stemming pipeline
     */
    public static AnalysisEngine defaultAnalysisEngine() {
        try {
            if (defaultAnalysisEngine == null)
                defaultAnalysisEngine = AnalysisEngineFactory.createEngine(
                                AnalysisEngineFactory.createEngineDescription(SentenceAnnotator.getDescription(),
                                                TokenizerAnnotator.getDescription()));

            return defaultAnalysisEngine;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }


    @Override
    public Tokenizer create(InputStream toTokenize) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void setTokenPreProcessor(TokenPreProcess preProcessor) {
        this.preProcess = preProcessor;
    }

    /**
     * Returns TokenPreProcessor set for this TokenizerFactory instance
     *
     * @return TokenPreProcessor instance, or null if no preprocessor was defined
     */
    @Override
    public TokenPreProcess getTokenPreProcessor() {
        return preProcess;
    }


}
