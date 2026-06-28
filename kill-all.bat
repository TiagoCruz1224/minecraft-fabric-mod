@echo off
REM Usa PowerShell num processo separado para matar javaw e cmd
REM (assim o próprio cmd não se mata antes de terminar)
powershell -NoProfile -Command "Get-Process javaw -ErrorAction SilentlyContinue | Stop-Process -Force; Start-Sleep 1; Get-Process cmd -ErrorAction SilentlyContinue | Stop-Process -Force"
