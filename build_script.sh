#!/bin/bash

#Comment this thing. 
[ -d "NLP_Platform_Installer" ] && rm -rf "NLP_Platform_Installer"
[ ! -d "NLP_Platform_Installer" ] && git clone https://github.com/bholland/NLP_Platform_Installer.git

[ ! -d Main ] && ln -s NLP_Platform_Installer/src/Main ./

( [ ! -e "config.yaml" ] || [ ! -e "clean_xml.yaml" ] ) && python3 -m Main.main && echo "config.yaml and clean_xml.yaml files created. Edit these config files now under the current directory and rerun this file." && exit 0

rm -rf target
mvn -P project clean compile prepare-package package

cp -rL models target/
cp -r project_training_data target/

mkdir target/src
cp ./src/logging.properties target/src/logging.properties

#Create the descriptors because the yaml configs exist
( python3 -m Main.main ) || exit 1

#copy the run script to output
( cp run_script.sh target/ ) || exit 1

#copy the python yaml generator to output
( cp -r "NLP_Platform_Installer/src/Main" target/ ) || exit 1

( cp rebuild_xml.sh target/) || exit 1
( cd target && mkdir orginal_configs ) || exit 1
( cp config.yaml target/orginal_configs/ ) || exit 1
( cp clean_xml.yaml target/orginal_configs/ ) || exit 1
