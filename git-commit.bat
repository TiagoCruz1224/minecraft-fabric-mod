@echo off
cd /d C:\Users\tiago\minecraft-fabric-mod
del .git\index.lock 2>nul
del cleanup.bat 2>nul
git rm --cached build-output.log 2>nul
git add -A
git commit -m "feat: escudo 3D (esfera paralelos/meridianos animados, raio 1.35, AFTER_ENTITIES, posicao interpolada); DEVLOG sessao 6 + ideias RPG; .gitignore build-output.log; remove cleanup.bat"
echo.
echo Commit feito! A fazer push...
git push
echo.
echo Concluido!
pause
