#!/bin/bash

# Thanks to http://linuxcommand.org/lc3_wss0120.php

nlp_platform_repo = "https://github.com/bholland/NLP_Platform.git"


if [ $# == 1 ]; then
    echo "Please provide the url of the reposatory to clone."
    exit 0
fi

# clone the github reposatory
( git clone nlp_platform_repo ) || exit 1

# add the passed repo to the project_xml_descriptors
( git config --file=.gitmodules submodule.project_xml_descriptors.url $1) || exit 1

# track the master branch
( git config --file=.gitmodules submodule.project_xml_descriptors.branch master ) || exit 1

# sync the submodules
( git submodule sync ) || exit 1

# update the submodules
( git submodule update --init --remote ) || exit 1

# fetch the new repo
( cd project_xml_descriptors && git fetch ) || exit 1

# reset the head master
( cd project_xml_descriptors && git reset --hard origin/master ) || exit 1

# check out the master
( git checkout master ) || exit 1
