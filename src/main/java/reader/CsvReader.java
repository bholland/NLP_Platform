package reader;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.Map;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.apache.commons.lang.StringEscapeUtils;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.cas.FSIterator;
import org.apache.uima.collection.CollectionException;
import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.tcas.Annotation;

import objects.CsvObject;

public class CsvReader extends Reader_ImplBase {
    
    private CsvObject mCsvObject = null;
    
    public CsvReader() {
        
    }
    
    public CsvReader(CsvObject csvObject) {
        mCsvObject = csvObject;
    }

    @Override
    public void ReadText() {
        mTextObjects = new ArrayList<TextObject>();
        try (Reader csv_file_input = new FileReader(mCsvObject.getCsvFile())) {
            try (CSVParser parser = new CSVParser(csv_file_input, CSVFormat.EXCEL.withHeader())) {
                for (CSVRecord record : parser) {
                    
                    String id = StringEscapeUtils.escapeSql(record.get(mCsvObject.getCsvIdColumn()));
                    String text = StringEscapeUtils.escapeSql(record.get(mCsvObject.getCsvTextColumn()));
                    Boolean is_model_text = mCsvObject.getIsModelData();
                    TextObject text_object = new TextObject(id, text, is_model_text);
                    mTextObjects.add(text_object);
                }
            }
        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
    
    public ArrayList<TextObject> ReadText(File file, String[] csvIdHeaders, String[] csvTextHeaders, Boolean isModelData, Boolean isSourceData) throws FileNotFoundException, IOException, CollectionException {
        
        if (isModelData == false && isSourceData == false) {
            throw new CollectionException("Model text and source text are both false. At least 1 should be true.", new Object[] {} );
        }
        
        try (Reader csv_file_input = new FileReader(file)) {
            try (CSVParser parser = new CSVParser(csv_file_input, CSVFormat.EXCEL.withHeader())) {
                Map<String, Integer> headers = parser.getHeaderMap();
    
                String id_column = null;
                String text_column = null;
                for (String id_header : csvIdHeaders) {
                    if (headers.containsKey(id_header)) {
                        id_column = id_header;
                    }
                }
                if (id_column == null) {
                    throw new CollectionException(String.format("CSV file %s does not have a recognized id header (Headers are: %s). Make sure to edit the XML file to provide an id header.", file.getAbsolutePath(), headers), new Object[] {} );
                }
                
                for (String text_header : csvTextHeaders) {
                    if (headers.containsKey(text_header)) {
                        text_column = text_header;
                    }
                }
                if (text_column == null) {
                    throw new CollectionException(String.format("CSV file %s does not have a recognized text header (Headers are: %s). Make sure to edit the XML file to provide a text header.", file.getAbsolutePath(), headers), new Object[] {} );
                }
                
                ArrayList<TextObject> ret = new ArrayList<TextObject>();
                for (CSVRecord record : parser) {
                    
                    String id = StringEscapeUtils.escapeSql(record.get(id_column));
                    String text = StringEscapeUtils.escapeSql(record.get(text_column));
                    if (isModelData == true) { 
                        TextObject text_object = new TextObject(id, text, true);
                        ret.add(text_object);
                    }
                    
                    if (isSourceData == true) { 
                        TextObject text_object = new TextObject(id, text, false);
                        ret.add(text_object);
                    }
                }
                return ret;
            }
        }
    }
}
