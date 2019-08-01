#!/bin/bash

#Make sure to change the xml descriptor objects to point to the proper
#path. This file gets copied into the target folder by the build script. 

#project example

#java -jar nlp_platform-0.0.3-SNAPSHOT.jar \
#     ./desc/ContentProcessingEngine/project/Collection_Processor_Document_Data_Ingest.xml \
#     ./desc/ContentProcessingEngine/project/Collection_Processor_Document_Data_Process.xml \
#     ./desc/ContentProcessingEngine/project/Collection_Processor_Model_Data_Ingest.xml \
#     ./desc/ContentProcessingEngine/project/Collection_Processor_Model_Data_Process.xml

#fjsp example
java -jar nlp_platform-0.0.3-SNAPSHOT.jar ./desc/ContentProcessingEngine/FJSP/FJSP_Collection_Processor_Document_Data_Ingest.xml \
     ./desc/ContentProcessingEngine/FJSP/FJSP_Collection_Processor_Document_Data_Process.xml \
     ./desc/ContentProcessingEngine/FJSP/FJSP_Collection_Processor_Model_Data_Ingest.xml \
     ./desc/ContentProcessingEngine/FJSP/FJSP_Collection_Processor_Model_Data_Process.xml
