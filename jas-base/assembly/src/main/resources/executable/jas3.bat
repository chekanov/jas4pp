@echo off
set JASROOT=$~dp0\..
set JASJAR="jas-core-${project.version}.jar"
java -Xmx${java.memory} -Dapplication.home=%JASROOT% %JASJVM_ARGS% -jar %JASROOT%/lib/%JASJAR% %*