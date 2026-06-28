@echo off
set JAVA_HOME=C:\Program Files\Eclipse Adoptium\jdk-21.0.11.10-hotspot
cd /d C:\Users\tiago\minecraft-fabric-mod
echo [Ascendant] A fechar Minecraft anterior...
taskkill /F /IM javaw.exe >nul 2>&1
timeout /t 2 /nobreak >nul
echo [Ascendant] A lançar Minecraft com novo HUD...
call gradlew.bat runClient
