@echo off
set JAVA_HOME=C:\Program Files\Eclipse Adoptium\jdk-21.0.11.10-hotspot
cd /d C:\Users\tiago\minecraft-fabric-mod
call gradlew.bat build > build-output.log 2>&1
echo Codigo de saida: %ERRORLEVEL% >> build-output.log
echo FEITO - ver build-output.log
