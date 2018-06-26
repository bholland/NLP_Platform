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

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.AbstractMap.SimpleEntry;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

import org.apache.uima.analysis_component.JCasAnnotator_ImplBase;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.cas.FSIterator;
import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.tcas.Annotation;
import org.apache.uima.resource.ResourceInitializationException;
import org.deeplearning4j.bagofwords.vectorizer.TfidfVectorizer;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.indexing.BooleanIndexing;
import org.nd4j.linalg.indexing.conditions.Conditions;

import database.DatabaseConnector;
import helper.ArrayHelper;
import objects.DatabaseConnection;
import objects.ModelObject;
import objects.UnprocessedText;
import tf_idf.UimaLemmaFactory;

public class TfIdfAnnotator extends JCasAnnotator_ImplBase {
    
    @Override
    public void process(JCas aJCas) throws AnalysisEngineProcessException {
        String input_document = aJCas.getDocumentText();
               
        FSIterator<Annotation> ut = aJCas.getAnnotationIndex(UnprocessedText.type).iterator();
        assert (ut.hasNext()) : "Sentence splitting annotator does not have an associated unprocessed text document";
        
        FSIterator<Annotation> mf = aJCas.getAnnotationIndex(ModelObject.type).iterator();
        assert (mf.hasNext()) : "No model file is associated with this jCas object.";

        FSIterator<Annotation> df = aJCas.getAnnotationIndex(DatabaseConnection.type).iterator();
        assert (df.hasNext()) : "No model file is associated with this jCas object.";
        
        UnprocessedText raw_text = (UnprocessedText) ut.next();
        assert(ut.hasNext() == false) : "More than one raw text document exists in the aJCas object.";
        
        ModelObject model_object = (ModelObject) mf.next();
        assert mf.hasNext() == false : "Only 1 model file is allowed per jCas.";
        
        DatabaseConnection database_connection = (DatabaseConnection) df.next();
        assert df.hasNext() == false : "Only 1 database connection is allowed per jCas.";
        
        Integer text_id = raw_text.getTextId();
        int[] document_ids = model_object.getIdArray().toArray();
        
        
        
        
        TfidfVectorizer vec = null;
        
        try (FileInputStream f = new FileInputStream(new File(model_object.getModelFile())) ) {
            try (ObjectInputStream o = new ObjectInputStream(f)) {
                vec = (TfidfVectorizer) o.readObject();
            } catch (ClassNotFoundException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } 
        
        assert vec != null : "Failed to read from the model file.";
        
        INDArray text_mat = null;
        String array_name = String.format("%s_array", model_object.getModelFile());
        try (FileInputStream f = new FileInputStream(new File(array_name)) ) {
            try (ObjectInputStream o = new ObjectInputStream(f)) {
                text_mat = (INDArray) o.readObject();
            } catch (ClassNotFoundException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        assert text_mat != null : "Failed to read from the model array file.";
        
        UimaLemmaFactory tf;
        try {
            tf = new UimaLemmaFactory();
        } catch (ResourceInitializationException e1) {
            e1.printStackTrace();
            throw new AnalysisEngineProcessException();
        }
        vec.setTokenizerFactory(tf);
        
        //DatabaseConnection database_connection = DatabaseConnector.GetDatabaseConnection(aJCas);
        
        //the result of vectorizing the data text. 
        INDArray data_array = vec.transform(input_document);
        
        double data_scale = ArrayHelper.ArrayMagnitude(data_array);
        data_array = data_array.divi(data_scale);
        
        /**
         * text_mat has each row as a document and each column as the number of words. 
         * data_array will have 1 row (one document) and the same number of columns.
         * We are multiplying a (D x w) X (1 x w) to find the best matching D. 
         * The result will be a (D x 1) column vector, which INDArray has as the 0 
         * index. 
         */
        //transpose the data array to allow it to be multiplied by the dot product. 
        INDArray dot_prod = text_mat.mmul(data_array.transpose());
        
        /**my guess is that is is possible the dot_prod could be above 1 by an epsilon.
         * That will cause an error with the arccos function.
         * My guess is also that 1 is exactly equal to 1.00000 as a double.
         * Any value greater than 1 is an error. Hard cap this at 1.   
         */
        BooleanIndexing.replaceWhere(dot_prod, 1, Conditions.greaterThan(1.0));
        
        assert dot_prod.columns() == 1; 
        assert dot_prod.rows() == text_mat.rows();
        
        List<Map.Entry<Integer, Double>> list = new ArrayList<>();
        for (int x = 0; x < dot_prod.rows(); x++) {
            list.add(new SimpleEntry<Integer, Double>(x, dot_prod.getDouble(x, 0)));
        }
        
        
        Collections.sort(list, new Comparator<Map.Entry<Integer, Double>>() {
            @Override
            public int compare(Map.Entry<Integer, Double> o1, Map.Entry<Integer, Double> o2) {
                return -1 * o1.getValue().compareTo(o2.getValue());
            }
        });
        
        Integer[] category_ids = new Integer[list.size()];
        Integer[] rank = new Integer[list.size()];
        Double[] cost_theta = new Double[list.size()];
        for (int x = 0; x < list.size(); x++) {
            //list is now the ordered set of results. 
            //row_number = list.get(x).getkey.
            Map.Entry<Integer, Double> e = list.get(x);
            
            //this links the most matching row id with the document id
            //This will get the row with the best match to least match.
            //Next, select the document id
            rank[x] = x; 
            category_ids[x] = document_ids[e.getKey()]; 
            cost_theta[x] = e.getValue();
        }
        
        try (DatabaseConnector connector = new DatabaseConnector(database_connection)) {
            connector.connect();
            Connection connection = connector.getConnection();
            
            CallableStatement select_text_ids = connection.prepareCall("{call insert_sorted_index(?, ?, ?, ?)}");
            /*
             * source_id integer,
             * category_ids integer[],
             * ranks integer[],
             * cos_thetas double precision[]
             */
            select_text_ids.setInt(1, text_id);
            select_text_ids.setArray(2, connection.createArrayOf("INTEGER", category_ids));
            select_text_ids.setArray(3, connection.createArrayOf("INTEGER", rank));
            select_text_ids.setArray(4, connection.createArrayOf("FLOAT", cost_theta));
            select_text_ids.execute();
            
        } catch (SQLException | IOException | ClassNotFoundException e) {
            e.printStackTrace();
            throw new AnalysisEngineProcessException();
        }
        
        //document_ids - category_id's 
        //text_id - the source id 
        
        /**
         * This next code will actually calculate the theta. 
         * We don't need to do that. The dot_prod is technically the
         * cos(theta), but it has the nice property where 
         * if this number is larger, there is a better match. 
         * The acos result is the opposite: if there is a larger number
         * then it is closer to pi/2 or 90 degrees or perfect dissimilarity. 
         */
        /*INDArray out = Nd4j.create(text_mat.rows(), 1);
        Nd4j.getExecutioner().exec(new ACos(dot_prod, out));
        System.out.println(String.format("rows: %s cols: %s", out.rows(), out.columns()));
        
        System.out.println("");
        System.out.println(String.format("dp1: %s", dot_prod));
        System.out.println(String.format("out: %s", out));
        System.out.println("");*/
    }

}
