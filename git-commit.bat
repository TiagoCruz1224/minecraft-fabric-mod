@echo off
cd /d C:\Users\tiago\minecraft-fabric-mod
del .git\index.lock 2>nul
del cleanup.bat 2>nul
git rm --cached build-output.log 2>nul
git add -A
git commit -m "feat: ability hotbar 9-slot SL:Reawakening style (R=toggle, Z=use); 24 class abilities 8 classes; keybind redesign; fix MobEffects 1.21.4 renames + Wolf API + ALL_ABILITIES; DEVLOG sessao 7"
echo.
echo Commit feito! A fazer push...
git push
echo.
echo Concluido