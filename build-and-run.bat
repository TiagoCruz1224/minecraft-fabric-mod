@echo off
set JAVA_HOME=C:\Program Files\Eclipse Adoptium\jdk-21.0.11.10-hotspot
cd /d C:\Users\tiago\minecraft-fabric-mod

echo [Ascendant] A compilar...
call gradlew.bat compileJava compileClientJava
if %ERRORLEVEL% NEQ 0 (
    echo.
    echo BUILD FALHOU! Ver erros acima.
    pause
    exit /b 1
)
echo.
echo [Ascendant] Build OK! A lancar Minecraft...
call gradlew.bat runClient
