#!/bin/bash

java -jar nlp_platform-0.0.3-SNAPSHOT.jar \
     ./desc/ContentProcessingEngine/project/Collection_Processor_Document_Data_Ingest.xml \
     ./desc/ContentProcessingEngine/project/Collection_Processor_Document_Data_Process.xml \
     ./desc/ContentProcessingEngine/project/Collection_Processor_Model_Data_Ingest.xml \
     ./desc/ContentProcessingEngine/project/Collection_Processor_Model_Data_Process.xml
