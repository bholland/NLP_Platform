package tf_idf;

import java.io.InputStream;

import org.deeplearning4j.text.tokenization.tokenizer.TokenPreProcess;
import org.deeplearning4j.text.tokenization.tokenizer.Tokenizer;

import opennlp.tools.tokenize.TokenizerFactory;

public class NLPTokenizerFactory implements org.deeplearning4j.text.tokenization.tokenizerfactory.TokenizerFactory {

    public NLPTokenizerFactory() {
        // TODO Auto-generated constructor stub
    }

    @Override
    public Tokenizer create(String toTokenize) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Tokenizer create(InputStream toTokenize) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void setTokenPreProcessor(TokenPreProcess preProcessor) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public TokenPreProcess getTokenPreProcessor() {
        // TODO Auto-generated method stub
        return null;
    }

}
