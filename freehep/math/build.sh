#!/bin/bash
echo "Maven build file. S.Chekanov" 
PWD=`pwd`
export M2_REPO=$PWD
parentdir="$(dirname "$(pwd)")" 
parentdir="$(dirname $parentdir)"
export M2_REP=$parentdir/REPO/
echo "use repository=$M2_REP"
mvn -U clean install -Dmaven.repo.local=$M2_REP -Dmaven.test.skip=true
#mvn versions:set -DnewVersion=4.0.0 -Dmaven.repo.local=$M2_REP
#mvn versions:commit              -Dmaven.repo.local=$M2_REP 
#mvn ant:ant -Dmaven.repo.local=$M2_REP
echo "Repository=$M2_REP"

