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

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;

import org.apache.uima.cas.CAS;
import org.apache.uima.cas.CASException;
import org.apache.uima.collection.CollectionException;
import org.apache.uima.jcas.JCas;
import org.apache.uima.resource.ResourceInitializationException;
import org.apache.uima.util.Progress;
import org.apache.uima.util.ProgressImpl;


import jgibblda.LDACmdOption;


/**
 * @author Ben Holland
 * This class reads from a database, populates the LDA objects, and runs the LDA. 
 * This does not implement a getNext() method as this is a post-processor object that
 * does not populate a CAS object. 
 * 
 * This requires a populated database. The LDA is an unsupervised learning model. It
 * accepts an entire populated dataset and will not split any text. I placed it here
 * because this heavily relies on the DatabaseCollectionReader_ImplBase.  
 *
 */

public class PostProcessorReader_GibbsLDA extends DatabaseCollectionReader_ImplBase {
    private Integer mOptionsIndex;
    private ArrayList<LDACmdOption> mOptions;
    
   
    @Override
    public void initialize() throws ResourceInitializationException {
        mOptions = new ArrayList<LDACmdOption>();
        mOptionsIndex = 0;
        mOptions.add(new LDACmdOption());
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
        mOptionsIndex++;
        try {
            getDatabaseConnector();
            jcas = addDatabaseToCas(jcas);
            jcas.setDocumentText("");
        } catch (ClassNotFoundException | SQLException e) {
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
        return new Progress[] { new ProgressImpl(mOptionsIndex, mOptions.size(), Progress.ENTITIES) };
    }

    @Override
    public boolean hasNext() throws IOException, CollectionException {
        return mOptionsIndex < mOptions.size();
    }
}
