@echo off
color 3
title L2Guardian: Login Server Console
:start

java -Dfile.encoding=UTF8 -Xms128m -Xmx128m -cp ./lib/*;l2jguardian-core.jar com.guardian.loginserver.L2LoginServer

if ERRORLEVEL 2 goto restart
if ERRORLEVEL 1 goto error
goto end
:restart
echo.
echo Admin Restarted ...
ping -n 5 localhost > nul
echo.
goto start
:error
echo.
echo LoginServer terminated abnormaly
ping -n 5 localhost > nul
echo.
goto start
:end
echo.
echo LoginServer terminated
echo.
:question
set choix=q
set /p choix=Restart(r) ou Sair(s)
if /i %choix%==r goto start
if /i %choix%==s goto exit
:exit
exit
pause
