#!/bin/bash
echo "Maven build file. S.Chekanov" 
ln -s ../OUTPUT/lib lib
ant clean
ant
