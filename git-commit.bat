@echo off
cd /d C:\Users\tiago\minecraft-fabric-mod
git restore --staged build-output.log 2>nul
git add -A
git commit -m "fix: corrigir ficheiros truncados da migracao de PC - completar 9 ficheiros cortados pela migracao: AscendantPlayerData, ServerNetworking, SyncPlayerDataPacket, Ascendant, AscendantServerTickEvents, AscendantClient, AscendantKeyBindings, ClientPlayerData, AscendantHud. BUILD SUCCESSFUL."
echo.
echo Commit feito! A fazer push...
git push
echo.
echo Concluido!
pause
