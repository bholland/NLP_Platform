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
package descriptors;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;

import org.apache.uima.cas.CAS;
import org.apache.uima.cas.CASException;
import org.apache.uima.collection.CollectionException;
import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.cas.IntegerArray;
import org.apache.uima.jcas.cas.StringArray;
import org.apache.uima.resource.ResourceInitializationException;
import org.apache.uima.util.Progress;
import org.apache.uima.util.ProgressImpl;
import org.deeplearning4j.bagofwords.vectorizer.TfidfVectorizer;
import org.deeplearning4j.text.documentiterator.LabelledDocument;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.factory.Nd4j;

import database.DatabaseConnector;
import helper.ArrayHelper;
import helper.CleanText;
import objects.DatabaseConnection;
import objects.ModelObject;
import objects.UnprocessedText;
import tf_idf.DocumentIteratorFromDatabase;
import tf_idf.UimaLemmaFactory;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;

public class PostProcessor_CosDistance extends DatabaseCollectionReader_ImplBase {
    private int[] mModelIDs;
    //private String[] mModelText;
        
    private ArrayList<Integer> mTextIDs;
    private Integer mTextIndex;
    
    public void CleanSortedIndex(Connection connection) throws SQLException {
        CallableStatement clean_sorted_index = connection.prepareCall("{call clean_sorted_index()}");
        clean_sorted_index.execute();
    }
    
    public TfidfVectorizer fitVectorized(Connection connection, DocumentIteratorFromDatabase doc_iter) throws ResourceInitializationException, SQLException {
        doc_iter.reset();
        UimaLemmaFactory tf = new UimaLemmaFactory();
        TfidfVectorizer vec = new TfidfVectorizer.Builder()
                .setIterator(doc_iter)
                .setMinWordFrequency(1)
                .setStopWords(new ArrayList<String>())
                .setTokenizerFactory(tf)
                .build();
        vec.fit();
        return vec;
    }
    
    public INDArray makeINDArray(TfidfVectorizer vec, DocumentIteratorFromDatabase doc_iter) {
        doc_iter.reset();
        ArrayList<INDArray> model_text_tmp = new ArrayList<INDArray>();
        doc_iter.reset();
        
        while (doc_iter.hasNext()) {
            LabelledDocument doc = doc_iter.next();
            INDArray array = vec.transform(doc.getContent());
            double scaling = ArrayHelper.ArrayMagnitude(array);
            array = array.divi(scaling);
            model_text_tmp.add(array);
        }
        INDArray text_mat = Nd4j.vstack(model_text_tmp);
        return text_mat;
    }
    
    public void makeOutputFiles(File model_file, File matrix_file, TfidfVectorizer vec, INDArray text_mat) throws IOException {
        try (FileOutputStream vec_file = new FileOutputStream(model_file)) {
            ObjectOutputStream o_vec_file = new ObjectOutputStream(vec_file);
            o_vec_file.writeObject(vec);
        }
        
        try (FileOutputStream array_file = new FileOutputStream(matrix_file)) {
            ObjectOutputStream o_array_file = new ObjectOutputStream(array_file);
            o_array_file.writeObject(text_mat);
        }
    }

    @Override
    public void initialize() throws ResourceInitializationException {
        mModelIDs = null;
        //mModelText = null;
        mTextIDs = new ArrayList<Integer>();
        mTextIndex = 0;
        
        //("DatabasePort was not passed and DatabaseType is not mysql or psql.",
        //new Object[] { })
        try (DatabaseConnector connector = getDatabaseConnector()) {
            connector.connect();
            Connection connection = connector.getConnection();
            
            CleanSortedIndex(connection);
            
            File model_file = new File(mModelFileName);
            File array_file = new File(String.format("%s_array", mModelFileName));
            TfidfVectorizer vec = null;
            INDArray text_mat = null;
            
            if (model_file.exists() && mRecreateModel == false) {
                try (FileInputStream f = new FileInputStream(model_file)) { 
                    ObjectInputStream o = new ObjectInputStream(f);
                    vec = (TfidfVectorizer) o.readObject();
                    f.close();
                }
                try (FileInputStream f = new FileInputStream(array_file)) { 
                    ObjectInputStream o = new ObjectInputStream(f);
                    vec = (TfidfVectorizer) o.readObject();
                    f.close();
                }
            } else {
                //Build the vectorizer here. Any model can technically go here, but the only model supported so far is the tf_idf model. 
                /**
                 * @TODO: Make this into an interface or an abstract class. 
                 */
                DocumentIteratorFromDatabase doc_iter = new DocumentIteratorFromDatabase(connection, true);
                vec = fitVectorized(connection, doc_iter);
                text_mat = makeINDArray(vec, doc_iter);
                makeOutputFiles(model_file, array_file, vec, text_mat);
            }
            assert vec != null : "vec not initalized";
            assert text_mat != null : "text_mat not initalized";
            
            
            //once the model is built, construct the data id set. 
            mModelIDs = getCategoryTextIDs(connection);
            mTextIDs = getSourceTextIDs(connection);
            
        } catch (SQLException | IOException | ClassNotFoundException e) {
            e.printStackTrace();
            throw new ResourceInitializationException("There was a problem generating or loading the model.",
                    new Object[] {});
        }
        
    }
    
    /**
     * This is largely not unit tested. It is very hard to
     * unit test this particular framework mostly because we would have
     * to create a CAS object. CAS objects are created in protected 
     * classes. In addition, much of this functionality is provided by UIMA
     * and UIMA is unit tested. This code does 1 very specific thing and
     * that is to create a new populated cas object based on source text. 
     */
    @Override
    public void getNext(CAS aCAS) throws IOException, CollectionException {
        JCas jcas;
        try {
            jcas = aCAS.getJCas();
        } catch (CASException e) {
            throw new CollectionException(e);
        }
        
        //create the model object and place it in the cas
        ModelObject model_object = new ModelObject(jcas);
        int size = mModelIDs.length;
        
        IntegerArray array = new IntegerArray(jcas, size);
        array.copyFromArray(mModelIDs, 0, 0, size);
        model_object.setIdArray(array);
        model_object.setModelFile(mModelFileName);
        model_object.addToIndexes();
        
        jcas = addDatabaseToCas(jcas);
        
        //populate the cas with a text string from the database.
        try (DatabaseConnector connector = getDatabaseConnector()) {
            connector.connect();
            Connection connection = connector.getConnection();
            Integer id =  mTextIDs.get(mTextIndex++);
            String text = getSourceTextFromID(connection, id);

            UnprocessedText ut = new UnprocessedText(jcas);
            ut.setTextId(id);
            ut.setRawTextString(text); //DO NOT CLEAN THIS TEXT. UnprocessedText SHOULD NEVER BE PROCESSED. 
            ut.addToIndexes();
            
            text = CleanText.Standardize(text);
            jcas.setDocumentText(text);
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
            throw new CollectionException();
        }
    }

    @Override
    public void close() throws IOException { 
        // TODO Auto-generated method stub

    }

    @Override
    public Progress[] getProgress() {
        return new Progress[] { new ProgressImpl(mTextIndex, mTextIDs.size(), Progress.ENTITIES) };
    }

    @Override
    public boolean hasNext() throws IOException, CollectionException {
        return mTextIndex < mTextIDs.size();
    }
}
