#!/bin/bash
echo "Maven build file. S.Chekanov" 
M2_OUT="../../OUTPUT/lib/freehep/"
echo "Output=$M2_OUT"

find . -name "*.jar" -exec rm -rf {} \;

# files to copy to the final repository
# declare -a arr=( "jas-plotter-*" "freehep-util-*" "freehep-xml-*" "freehep-swing-*" "freehep-record-*" "freehep-application-*" "freehep-commanddispatcher-*"  )
declare -a arr=( "freehep-util-*" "freehep-xml-*" "freehep-swing-*" "freehep-record-*" "freehep-application-*" "freehep-commanddispatcher-*"  )

PWD=`pwd`
export M2_REPO=$PWD
parentdir="$(dirname "$(pwd)")" 
parentdir="$(dirname $parentdir)"
export M2_REP=$parentdir/REPO/
echo "use repository=$M2_REP"
#mvn --batch-mode release:update-versions -DdevelopmentVersion=50.0 -Dmaven.repo.local=$M2_REP -Dmaven.test.skip=true 
# mvn -U clean -Dmaven.repo.local=$M2_REP -Dmaven.test.skip=true

mvn -U clean install -Dmaven.repo.local=$M2_REP -Dmaven.test.skip=true 
#mvn versions:set -DnewVersion=4.0.0 -Dmaven.repo.local=$M2_REP
#mvn versions:commit              -Dmaven.repo.local=$M2_REP 
#mvn ant:ant -Dmaven.repo.local=$M2_REP
echo "Repository=$M2_REP"


for i in "${arr[@]}"
do
  echo "Replacing $i"
  rm -f $M2_OUT/$i
  find $PWD -type f -name $i -exec cp -fv {} $M2_OUT/ \;
done



