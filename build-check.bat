@echo off
set JAVA_HOME=C:\Program Files\Eclipse Adoptium\jdk-21.0.11.10-hotspot
cd /d C:\Users\tiago\minecraft-fabric-mod
call gradlew.bat compileJava compileClientJava 2>&1 | findstr /I "error: warning: BUILD"
pause
