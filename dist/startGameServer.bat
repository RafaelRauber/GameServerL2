@echo off
color 3
title L2Guardian: Game Server Console
mode con: cols=140 lines=40
:start
echo Starting L2Guardian Core Game Server.
echo Forum: http://www.l2jbrasil.com
echo ------------------------------
echo.


REM -------------------------------------
REM Default parameters for a basic server.
java -Dfile.encoding=UTF8 -Xms1024m -Xmx1024m -cp ./lib/*;./lib/uMad/*;l2jguardian-core.jar com.guardian.gameserver.GameServer
REM
REM If you have a big server and lots of memory, you could experiment for example with
REM java -server -Xmx1536m -Xms1024m -Xmn512m -XX:PermSize=256m -XX:SurvivorRatio=8 -Xnoclassgc -XX:+AggressiveOpts
REM -------------------------------------

if ERRORLEVEL 7 goto telldown
if ERRORLEVEL 6 goto tellrestart
if ERRORLEVEL 5 goto taskrestart
if ERRORLEVEL 4 goto taskdown
REM 3 - abort
if ERRORLEVEL 2 goto restart
if ERRORLEVEL 1 goto error
goto end
:tellrestart
echo.
echo Telnet server Restart ...
echo Send you bug to : http://www.l2jbrasil.com
echo.
goto start
:taskrestart
echo.
echo Auto Task Restart ...
echo Send you bug to : http://www.l2jbrasil.com
echo.
goto start
:restart
echo.
echo Admin Restart ...
echo Send you bug to : http://www.l2jbrasil.com
echo.
goto start
:taskdown
echo .
echo Server terminated (Auto task)
echo Send you bug to : http://www.l2jbrasil.com
echo .
:telldown
echo .
echo Server terminated (Telnet)
echo Send you bug to : http://www.l2jbrasil.com
echo .
:error
echo.
echo Server terminated abnormally
echo Send you bug to : http://www.l2jbrasil.com
echo.
:end
echo.
echo server terminated
echo Send you bug to : http://www.l2jbrasil.com
echo.
:question
set choix=q
set /p choix=Restart(r) ou Sair(s)
if /i %choix%==r goto start
if /i %choix%==s goto exit
:exit
exit
pause
