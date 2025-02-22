package reader;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import org.apache.uima.collection.CollectionException;

public class TextReader extends Reader_ImplBase {

    @Override
    public void ReadText() {
        // TODO Auto-generated method stub

    }
    /**
     * @TODO: Make this have a category classification. 
     * @param file
     * @param isModelData
     * @param isSourceData
     * @return
     * @throws FileNotFoundException
     * @throws IOException
     * @throws CollectionException
     */
    public ArrayList<TextObject> ReadText(File file, Boolean isModelData, Boolean isSourceData) throws FileNotFoundException, IOException, CollectionException {
        
        if (isModelData == false && isSourceData == false) {
            throw new CollectionException("Model text and source text are both false. At least 1 should be true.", new Object[] {} );
        }
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            ArrayList<TextObject> ret = new ArrayList<TextObject>();
            String line;
            while ((line = reader.readLine()) != null) {
                line = line.trim();
                if (isModelData == true) {
                    ret.add(new TextObject(null, line, file.getAbsolutePath(), true, null, null));
                }
                if (isSourceData == true) {
                    ret.add(new TextObject(null, line, file.getAbsolutePath(), false, null, null));
                }
            }
            return ret;
        }
    }
}
