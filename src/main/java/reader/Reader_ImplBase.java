package reader;

import java.util.ArrayList;

import org.apache.uima.jcas.JCas;

public abstract class Reader_ImplBase {
    
    protected ArrayList<TextObject> mTextObjects;
    
    /**
     * Read text from the CAS object
     */
    abstract public void ReadText();
    
    public ArrayList<TextObject> getTextObjects() {
        return mTextObjects;
    }

}
