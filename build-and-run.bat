@echo off
set JAVA_HOME=C:\Program Files\Eclipse Adoptium\jdk-21.0.11.10-hotspot
cd /d C:\Users\Tiago\Documents\GitHub\minecraft-fabric-mod
echo [Ascendant] A limpar build anterior...
call gradlew.bat clean
echo [Ascendant] A compilar...
if %ERRORLEVEL% NEQ 0 (
    echo BUILD FALHOU!
    pause
    exit /b 1
)
echo [Ascendant] Build OK! A abrir Minecraft...
call gradlew.bat runClient
pause
