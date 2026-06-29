@echo off
cd /d C:\Users\tiago\minecraft-fabric-mod
del .git\index.lock 2>nul
del .git\HEAD.lock 2>nul
git add -A
git commit -m "fix: HUD redesign - HP/MP bars top-left, ability hotbar limpo; remove G/F keybinds (apenas Z+1-9)"
echo.
echo Commit feito! A fazer push...
git push
echo.
echo Concluido!
pause
