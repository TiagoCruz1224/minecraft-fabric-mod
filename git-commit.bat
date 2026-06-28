@echo off
cd /d C:\Users\tiago\minecraft-fabric-mod
git restore --staged build-output.log 2>nul
git add -A
git commit -m "feat: HUD - texto dentro das barras + icons de items para stats. HP e mana com valor centrado na barra; stats usam icons Minecraft (carne, water bucket, xp bottle, maca) em 2x2 acima da barra de mana."
echo.
echo Commit feito! A fazer push...
git push
echo.
echo Concluido!
pause
