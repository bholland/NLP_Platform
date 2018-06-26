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
package main_class;

import java.io.IOException;
import java.util.List;
import java.util.Properties;

import org.apache.spark.api.java.function.ForeachFunction;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.SparkSession;
import org.apache.uima.UIMAFramework;
import org.apache.uima.UimaContextAdmin;
import org.apache.uima.analysis_engine.AnalysisEngine;
import org.apache.uima.cas.CAS;
import org.apache.uima.collection.CollectionProcessingEngine;
import org.apache.uima.collection.CollectionReader;
import org.apache.uima.collection.base_cpm.CasProcessor;
import org.apache.uima.collection.metadata.CpeDescription;
import org.apache.uima.jcas.JCas;
import org.apache.uima.resource.CasManager;
import org.apache.uima.resource.ResourceInitializationException;
import org.apache.uima.util.InvalidXMLException;
import org.apache.uima.util.XMLInputSource; 


public class main_spark implements java.io.Serializable {
    
    /**
     * 
     */
    private static final long serialVersionUID = -6745158941617220117L;

    private static void printUsageMessage() {
        System.out.println(" Arguments to the program are as follows : \n"
                + "args[0] : path to CPE descriptor file");
    }
    
    public main_spark(String args[]) throws InvalidXMLException, IOException, ResourceInitializationException {
        SparkSession spark = SparkSession.builder().appName("NLP Application")
                .config("spark.master", "local[6]")
                .config("spark.driver.maxResultSize", "1g")
                .config("spark.local.dir", "/tmp")
                .config("spark.submit.deployMode", "client").getOrCreate();
        
        if (args.length < 1) {
            printUsageMessage();
            System.exit(1);
        }
          
        // parse CPE descriptor
        System.out.println("Parsing CPE Descriptor");
        CpeDescription cpeDesc = UIMAFramework.getXMLParser().parseCpeDescription(
                new XMLInputSource(args[0]));
        //    instantiate CPE
        System.out.println("Instantiating CPE");
        CollectionProcessingEngine mCPE = UIMAFramework.produceCollectionProcessingEngine(cpeDesc);
        /*  
        ReadDatabaseDescriptor_CPE reader = (ReadDatabaseDescriptor_CPE) mCPE.getCollectionReader();
        DatabaseConnector connector = new DatabaseConnector(reader.getDatabaseType(), reader.getDatabaseServer(), reader.getPort(), reader.getDatabase(), reader.getUserName(), reader.getPassword());
        String url = connector.getUrl();
        Properties connection_properties = new Properties();
        connection_properties.put("user", reader.getUserName());
        connection_properties.put("password", reader.getPassword());
        connection_properties.put("useSSL", "false");
        //String logFile = "/home/ben/spark/spark-2.2.0-bin-hadoop2.7/spark.md";
        
        
        Dataset<Row> logData = spark.read().jdbc(url, "text_table", connection_properties);
        logData.foreach(new ForeachFunction<Row>() {
            
            private static final long serialVersionUID = 5970655879733717955L;

            @Override
            public void call(Row row) throws Exception {
                
             // parse CPE descriptor
                CpeDescription cpeDesc = UIMAFramework.getXMLParser().parseCpeDescription(
                        new XMLInputSource(args[0]));
                //    instantiate CPE
                CollectionProcessingEngine mCPE = UIMAFramework.produceCollectionProcessingEngine(cpeDesc);
                  
                ReadDatabaseDescriptor_CPE reader = (ReadDatabaseDescriptor_CPE) mCPE.getCollectionReader();
                
                Properties performanceTuningSettings = UIMAFramework.getDefaultPerformanceTuningProperties();
                CasManager aCasManager = reader.getCasManager();
                CAS cas = aCasManager.createNewCas(performanceTuningSettings);
                UimaContextAdmin context = ((CollectionReader) reader).getUimaContextAdmin();
                cas.setCurrentComponentInfo(context.getComponentInfo());
                
                JCas jcas = cas.getJCas();
                jcas.setDocumentText(row.getString(1));
                UnprocessedText unprocessed_text = new UnprocessedText(jcas);
                unprocessed_text.setText_id(row.getInt(0));
                unprocessed_text.addToIndexes();
                
                DatabaseConnection db_conn = new DatabaseConnection(jcas);
                db_conn.setDatabaseServer(reader.getDatabaseServer());
                db_conn.setPort(reader.getPort());
                db_conn.setDatabase(reader.getDatabase());
                db_conn.setUserName(reader.getUserName());
                db_conn.setPassword(reader.getPassword());
                db_conn.addToIndexes();
                
                //CollectionProcessingManager cpm = org.apache.uima.UIMAFramework.newCollectionProcessingManager();
                CasProcessor[] processors = mCPE.getCasProcessors();
                for (CasProcessor processor: processors) {
                    if (!processor.isReadOnly()) {
                        AnalysisEngine ae = (AnalysisEngine) processor;
                        ae.process(jcas);
                    }
                }
                
            }
        } );
        
        */
        spark.stop();
    }
    
    public static void main(String[] args) throws Exception {
        new main_spark(args);
    }
}
