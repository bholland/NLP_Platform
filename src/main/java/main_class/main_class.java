/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package main_class;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;
import org.apache.uima.UIMAFramework;
import org.apache.uima.cas.CAS;
import org.apache.uima.collection.CollectionProcessingEngine;
import org.apache.uima.collection.EntityProcessStatus;
import org.apache.uima.collection.StatusCallbackListener;
import org.apache.uima.collection.metadata.CpeDescription;
import org.apache.uima.resource.ResourceInitializationException;
import org.apache.uima.util.InvalidXMLException;
import org.apache.uima.util.XMLInputSource;

/**
 * Main Class that runs a Collection Processing Engine (CPE). This class reads a CPE Descriptor as a
 * command-line argument and instantiates the CPE. It also registers a callback listener with the
 * CPE, which will print progress and statistics to System.out.
 * 
 * 
 */
public class main_class extends Thread {
    
    private class RunObject {
        /**
         * The CPE instance.
         */
        private CollectionProcessingEngine mCPE;

        /**
         * File name
         */
        private String mFileName;
        private StatusCallbackListenerImpl mStatusCallbackListener;
        
        public RunObject(String CpeDescriptor) throws InvalidXMLException, IOException, ResourceInitializationException {
            mFileName = CpeDescriptor;
            
            //create the CPE descriptor file from input. 
            CpeDescription cpeDesc = UIMAFramework.getXMLParser().parseCpeDescription(new XMLInputSource(CpeDescriptor));
            
            //parse CPE descriptor
            mCPE = UIMAFramework.produceCollectionProcessingEngine(cpeDesc);
            
            mStatusCallbackListener = new StatusCallbackListenerImpl();
            
            // Create and register a Status Callback Listener
            mCPE.addStatusCallbackListener(mStatusCallbackListener);
        }
        
        public void process() throws ResourceInitializationException {
            // Start Processing
            mCPE.process();
        }
        
        public boolean isProcessing() {
            return mStatusCallbackListener.isProcessing();
        }
        
        /*public void Performance() {
            Logger logger = Logger.getRootLogger();
            logger.debug("\n\n ------------------ PERFORMANCE REPORT ------------------\n");
            logger.debug(mCPE.getPerformanceReport().toString());
        }*/
        
        public String getName() {
            return mFileName;
        }
    }
    
    private ArrayList<RunObject> run_list; 
  

    /**
     * Constructor for the class.
     * 
     * @param args
     *          command line arguments into the program - see class description
    */
    public main_class(String args[]) throws Exception {
        
        // check command line args
        if (args.length < 1) {
            printUsageMessage();
            System.exit(1);
        }
        run_list = new ArrayList<RunObject>();
        for (String cpe_desc: args) {
            RunObject o = new RunObject(cpe_desc);
            run_list.add(o);
        }
        
        Logger logger = Logger.getRootLogger();
        
        for (RunObject runner : run_list) {
            logger.debug(String.format("Starting %s", runner.getName()));
            runner.process();
            while (runner.isProcessing()) {
                sleep(5000);
            }
            logger.debug(String.format("Finished %s", runner.getName()));
        }
    }

  
    private static void printUsageMessage() {
        Logger logging = Logger.getRootLogger();
        logging.debug(" Arguments to the program are as follows : \n"
                + "args[0] : path to Content Processing Engine descriptor file \n"
                + "args[*] : optional path to Content Processing Engine descriptor file(s)");
    }

    /**
     *  main class.
     * 
     * @param args
     *          Command line arguments - see class description
     */
    public static void main(String[] args) throws Exception {
        PropertyConfigurator.configure("/home/ben/workspace/opennlp_processing/src/logging.properties");
        new main_class(args);
        System.exit(0); //oddly, this is needed. Maven will hang without an exit call.  
    }
    
    

  /**
   * Callback Listener. Receives event notifications from CPE.
   * 
   * 
   */
  class StatusCallbackListenerImpl implements StatusCallbackListener {
      int entityCount = 0;
      long size = 0;
      
      /**
       * Start time of CPE initialization
       */
      private long mStartTime;
      
      /**
       * Start time of the processing
       */
      private long mInitCompleteTime;
      
      private boolean mRunning;
      
      
      public StatusCallbackListenerImpl() {
          mStartTime = System.currentTimeMillis();
          mRunning = true;
      }
    
    

      /**
       * Called when the initialization is completed.
       * 
       * @see org.apache.uima.collection.processing.StatusCallbackListener#initializationComplete()
       */
      @Override
    public void initializationComplete() {
          Logger logger = Logger.getRootLogger();
          logger.debug("CPM Initialization Complete");
          mInitCompleteTime = System.currentTimeMillis();
      }

      /**
       * Called when the batchProcessing is completed.
       * 
       * @see org.apache.uima.collection.processing.StatusCallbackListener#batchProcessComplete()
       * 
       */
      @Override
    public void batchProcessComplete() {
          Logger logging = Logger.getRootLogger();
          logging.debug("Completed " + entityCount + " documents");
          if (size > 0) {
              System.out.print("; " + size + " characters");
          }
          long elapsedTime = System.currentTimeMillis() - mStartTime;
          logging.debug("Time Elapsed : " + elapsedTime + " ms ");
          mRunning = false;
      }

      /**
       * Called when the collection processing is completed.
       * 
       * @see org.apache.uima.collection.processing.StatusCallbackListener#collectionProcessComplete()
       */
      @Override
    public void collectionProcessComplete() {
          Logger logging = Logger.getRootLogger();
          long time = System.currentTimeMillis();
          System.out.print("Completed " + entityCount + " documents");
          if (size > 0) {
            System.out.print("; " + size + " characters");
          }
          long initTime = mInitCompleteTime - mStartTime; 
          long processingTime = time - mInitCompleteTime;
          long elapsedTime = initTime + processingTime;
          logging.debug("Total Time Elapsed: " + elapsedTime + " ms ");
          logging.debug("Initialization Time: " + initTime + " ms");
          logging.debug("Processing Time: " + processingTime + " ms");
          mRunning = false;
      }

      /**
       * Called when the CPM is paused.
       * 
       * @see org.apache.uima.collection.processing.StatusCallbackListener#paused()
       */
      @Override
    public void paused() {
          Logger logging = Logger.getRootLogger();
          logging.debug("Paused");
      }

      /**
       * Called when the CPM is resumed after a pause.
       * 
       * @see org.apache.uima.collection.processing.StatusCallbackListener#resumed()
      */
      @Override
    public void resumed() {
          Logger logging = Logger.getRootLogger();
          logging.debug("Resumed");
      }

      /**
       * Called when the CPM is stopped abruptly due to errors.
       * 
       * @see org.apache.uima.collection.processing.StatusCallbackListener#aborted()
       */
      @Override
    public void aborted() {
          Logger logging = Logger.getRootLogger();
          logging.debug("Aborted");
          // stop the JVM. Otherwise main thread will still be blocked waiting for
          // user to press Enter.
      }

      /**
       * Called when the processing of a Document is completed. <br>
       * The process status can be looked at and corresponding actions taken.
       * 
       * @param aCas
       *          CAS corresponding to the completed processing
       * @param aStatus
       *          EntityProcessStatus that holds the status of all the events for aEntity
       */
      @Override
    public void entityProcessComplete(CAS aCas, EntityProcessStatus aStatus) {
          if (aStatus.isException()) {
              List<Exception> exceptions = aStatus.getExceptions();
              for (int i = 0; i < exceptions.size(); i++) {
                  ((Throwable) exceptions.get(i)).printStackTrace();
              }
              return;
          }
          entityCount++;
          String docText = aCas.getDocumentText();
          if (docText != null) {
              size += docText.length();
          }
      }
      
      public boolean isProcessing() {
          return mRunning;
      }
  }

}
