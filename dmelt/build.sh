#!/bin/bash
echo "Build file. S.Chekanov"
ln -s ../OUTPUT/lib/freehep freehep 
ln -s ../OUTPUT/lib/groovy  groovy 
ln -s ../OUTPUT/lib/jas     jas 
ln -s ../OUTPUT/lib/jas_ext jas_ext 
ln -s ../OUTPUT/lib/jython jython 
ln -s ../OUTPUT/lib/physics physics
ant -f build.xml
cp -rv lib/dmelt ../OUTPUT/lib/
