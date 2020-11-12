#!/bin/bash
echo "Maven build file. S.Chekanov" 

PROJECT="lib/jas_ext"

PWD=`pwd`
find . -name "*.jar" -exec rm -rf {} \;
export M2_REPO=$PWD
parentdir="$(dirname "$(pwd)")" 
#parentdir="$(dirname $parentdir)"
export M2_REP=$parentdir/REPO/
export M2_OUT=$parentdir/OUTPUT/$PROJECT/
mkdir -p $parentdir/OUTPUT/$PROJECT

echo "use repository=$M2_REP"
mvn -U -o clean install -Dmaven.repo.local=$M2_REP -Dmaven.test.skip=true
#mvn versions:set -DnewVersion=4.0.0 -Dmaven.repo.local=$M2_REP
#mvn versions:commit              -Dmaven.repo.local=$M2_REP 
#mvn ant:ant -Dmaven.repo.local=$M2_REP
mvn dependency:tree -Dmaven.repo.local=$M2_REP -Dmaven.test.skip=true

echo "Repository=$M2_REP"
find $PWD -type f -name '*.jar' -exec cp -fv {} $M2_OUT/ \;

echo "Clean some unneeded libraries.."
rm -f $M2_OUT/lcsim-dist*
rm -f $M2_OUT/lcsim-detector-framework-4.0.jar

