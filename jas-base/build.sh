#!/bin/bash
echo "Maven build file. S.Chekanov" 

PROJECT="jas_ext"


PWD=`pwd`
find . -name "*.jar" -exec rm -rf {} \;

export M2_REPO=$PWD
parentdir="$(dirname "$(pwd)")" 
#parentdir="$(dirname $parentdir)"
export M2_REP=$parentdir/REPO/
export M2_OUT=$parentdir/OUTPUT/lib/$PROJECT/ 
mkdir -p $parentdir/OUTPUT/lib/$PROJECT

echo "use repository=$M2_REP"
mvn -U clean install -Dmaven.repo.local=$M2_REP -Dmaven.test.skip=true
#mvn versions:set -DnewVersion=4.0.0 -Dmaven.repo.local=$M2_REP
#mvn versions:commit              -Dmaven.repo.local=$M2_REP 
#mvn ant:ant -Dmaven.repo.local=$M2_REP
mvn dependency:tree -Dmaven.repo.local=$M2_REP -Dmaven.test.skip=false

echo "Repository=$M2_REP"
mv -fv ./assembly/target/jas-assembly-3.1.4-distribution/jas-assembly-3.1.4/lib/* ../OUTPUT/lib/jas/
chmod -R 755 ../OUTPUT/lib/jas
#cp -f extensions/JasPlotter/target/jas-plotter-2.2.9.jar ../OUTPUT/lib/jas_ext/
cp -f extensions/JasPlotter/target/jas-plotter-2.2.9.jar ../OUTPUT/lib/jas/
rm -f ../OUTPUT/lib/jas/freehep-commanddispatcher-*
find $PWD -type f -name '*.jar' -exec cp -fv {} $M2_OUT/ \;
echo "cleaning.."
rm -f $M2_OUT/unprocessed*
rm -f $M2_OUT/MacOS*.jar
rm -f $M2_OUT/jas-conditions*.jar
rm -f $M2_OUT/jas-plotter-*jar
rm -f $M2_OUT/jas-core-*jar
rm -f $M2_OUT/jython-*jar
rm -f $M2_OUT/guava-*jar # comes with jython 2.7
rm -f $M2_OUT/jline-*    # comes with jython 2.7
rm -f ../OUTPUT/lib/jas/freehep-application-*jar
echo "Jar files are in $M2_OUT/"
