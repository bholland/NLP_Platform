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

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;

import org.apache.log4j.Logger;
import org.apache.uima.analysis_component.JCasAnnotator_ImplBase;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.jcas.JCas;
import org.cleartk.ml.Instances;

import database.DatabaseConnector;
import helper.DatabasePipeIterator;
import helper.TextToTokensPipe;
import objects.DatabaseConnection;
import cc.mallet.pipe.FeatureSequence2FeatureVector;
import cc.mallet.pipe.Pipe;
import cc.mallet.pipe.PrintInputAndTarget;
import cc.mallet.pipe.SerialPipes;
import cc.mallet.pipe.Target2Label;
import cc.mallet.pipe.TokenSequence2FeatureSequence;
import cc.mallet.pipe.TokenSequenceRemoveStopwords;
import cc.mallet.topics.ParallelTopicModel;
import cc.mallet.types.InstanceList;

public class GibbsLDAAnnotator extends JCasAnnotator_ImplBase {
    
    public Pipe buildPipe() {
        ArrayList<Pipe> pipeList = new ArrayList<Pipe>();
        
        pipeList.add(new TextToTokensPipe());
        
        // Remove stopwords from a standard English stoplist.
        //  options: [case sensitive] [mark deletions]
        pipeList.add(new TokenSequenceRemoveStopwords(false, false));

        // Rather than storing tokens as strings, convert 
        //  them to integers by looking them up in an alphabet.
        pipeList.add(new TokenSequence2FeatureSequence());

        // Do the same thing for the "target" field: 
        //  convert a class label string to a Label object,
        //  which has an index in a Label alphabet.
        pipeList.add(new Target2Label());

        // Now convert the sequence of features to a sparse vector,
        //  mapping feature IDs to counts.
        //pipeList.add(new FeatureSequence2FeatureVector());

        // Print out the features and the label
        //pipeList.add(new PrintInputAndTarget());

        return new SerialPipes(pipeList);
    }
    
    @Override
    public void process(JCas aJCas) throws AnalysisEngineProcessException {
        
        DatabaseConnection database_connection = DatabaseConnector.GetDatabaseConnection(aJCas);
        if (database_connection == null) {
            throw new AnalysisEngineProcessException("A database object was not configured for this CAS" , new Object[] {} );
        }
        
        try (DatabaseConnector connector = new DatabaseConnector(database_connection)) {
            connector.connect();
            Connection connection = connector.getConnection();
            /*
            LDACmdOption option = new LDACmdOption();
            option.est = true;
            option.inf = false;
            option.K = 20;
            option.niters = 100;
            option.dir = "LDA";
            option.sql_function = "select_source_text";
            option.connection = connection;
            
            Estimator estimator = new Estimator();
            estimator.init(option);
            estimator.estimate();
            */
            /*
            option.est = false;
            option.inf = true;
            option.modelName = "model-final";
            Inferencer inferencer = new Inferencer();
            inferencer.init(option);
            
            Model newModel = inferencer.inference();
        
            for (int i = 0; i < newModel.phi.length; ++i){
                //phi: K * V
                System.out.println("-----------------------\ntopic" + i  + " : ");
                for (int j = 0; j < 10; ++j){
                    System.out.println(inferencer.globalDict.id2word.get(j) + "\t" + newModel.phi[i][j]);
                }
            }
            */
            
            InstanceList instances = null;
            File file = new File("input_data.dat");
            boolean rebuild = false;
            if (file.exists() && rebuild == false) {
                System.out.println("File input_data.dat does exists.");
                instances = InstanceList.load(file);
            } else {
                System.out.println("File input_data.dat does not exist.");
                System.out.println("Setting up the iterator and instance.");
                DatabasePipeIterator iterator = new DatabasePipeIterator(connection, "select_source_text");
                instances = new InstanceList(buildPipe());
                
                // Now process each instance provided by the iterator.
                instances.addThruPipe(iterator);
                instances.save(file);
            }
            
            File likelihood = new File("likelihoods.dat");
            int previous_num_topics = 0;
            Double previous_loglikliood = null;
            //int counter = 0;
            //int num_topics = 16;
            try (BufferedWriter writer = new BufferedWriter(new FileWriter(likelihood))) {
                writer.write("NumTopics, liklihood\n");
                
                //while (counter < 50 && num_topics != previous_num_topics) {
                for (int num_topics = 10; num_topics < 200; num_topics += 10) {
                    ParallelTopicModel lda = new ParallelTopicModel(num_topics);
                    lda.printLogLikelihood = true;
                    //lda.setTopicDisplay(num_topics, 7);
                    lda.addInstances(instances);
                    
                    lda.setNumThreads(6);
                    lda.estimate();
                    
                    double lda_likilhood = lda.modelLogLikelihood();
                    File document_topics = new File(String.format("document_topics_%s.dat", num_topics));
                    lda.printDocumentTopics(document_topics);
                    
                    File words = new File(String.format("document_words_%d.dat", num_topics));
                    lda.printTopWords(words, 30, false);
                    
                    System.out.println("printing state");
                    lda.printState(new File(String.format("state_%d.gz", num_topics)));
                    System.out.println("finished printing");
                    
                    writer.write(String.format("%s, %s\n", num_topics, lda_likilhood));
                    
                    /*if (previous_loglikliood == null || previous_loglikliood < lda_likilhood) {
                        previous_num_topics = num_topics;
                        previous_loglikliood = lda_likilhood;
                        num_topics = num_topics * 2;
                    } else {
                        num_topics = previous_num_topics + ((num_topics - previous_num_topics) / 2);
                    }
                    counter = counter +  1;
                    System.out.println(String.format("Iteration: %s num_topics:%s ll: %s", counter, num_topics, previous_loglikliood));
                    */
                }
            }
            
            
            
            
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            throw new AnalysisEngineProcessException();
        } catch (IOException e) {
            e.printStackTrace();
            throw new AnalysisEngineProcessException();
        } catch (SQLException e) {
            e.printStackTrace();
            throw new AnalysisEngineProcessException();
        }
    }

}
