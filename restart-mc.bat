@echo off
set JAVA_HOME=C:\Program Files\Eclipse Adoptium\jdk-21.0.11.10-hotspot
cd /d C:\Users\tiago\minecraft-fabric-mod

echo [Ascendant] A matar Minecraft e Gradle anteriores...
powershell -NoProfile -WindowStyle Hidden -Command "Get-Process javaw -EA SilentlyContinue | Stop-Process -Force"
timeout /t 1 /nobreak >nul
powershell -NoProfile -WindowStyle Hidden -Command "Get-WmiObject Win32_Process -Filter \"Name='cmd.exe' AND CommandLine LIKE '%%gradlew%%'\" | ForEach-Object { $_.Terminate() } | Out-Null"
timeout /t 2 /nobreak >nul

echo [Ascendant] A lancar Minecraft com novo HUD...
call gradlew.bat runClient
