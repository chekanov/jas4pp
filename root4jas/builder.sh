#!/bin/bash
# DataMelt execution for Linux/Mac

# assume this script in this directory
CFILE="./build.xml"
export JAS4PP=`pwd`
if [ ! -f $CFILE ];
then
   export JAS4PP="$( cd "$( echo "${BASH_SOURCE[0]%/*}" )" && pwd )"
fi

arguments="$@"


if which java >/dev/null; then
    _java=java
elif [[ -n "$JAVA_HOME" ]] && [[ -x "$JAVA_HOME/bin/java" ]];  then
    _java="$JAVA_HOME/bin/java"
else
    echo "No java detected! Please install it from https://java.com/download"; exit 0
fi


################## do not edit ###############################
JAVA_HEAP_SIZE=2048
CLASSPATH=$JAS4PP:$CLASSPATH

# Add in your .jar files first
for i in ${JAS4PP}/*.jar
do
      CLASSPATH="$i":$CLASSPATH
done

# Add in your .jar files first
for i in ${JAS4PP}/lib/*.jar
do
      CLASSPATH="$i":$CLASSPATH
done

rm -f *.proxy.*
OPTJJ=" -Dapplication.home=${JAS4PP} -Dorg.apache.commons.logging.Log=org.apache.commons.logging.impl.NoOpLog"
$_java -mx${JAVA_HEAP_SIZE}m -cp $CLASSPATH $OPTJJ  hep.io.root.util.InterfaceBuilder Example.root
