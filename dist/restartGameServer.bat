@echo off
color 3
title L2Guardian: Game Server Restart
echo L2Guardian: Game Server Restart
echo ATTENTION: It needs XMLRPC Server Enabled in Powerpak in order to work

REM -------------------------------------
REM Default parameters for a basic server.
java -Dfile.encoding=UTF8 -cp ./lib/*;l2jguardian-core.jar com.guardian.gameserver.powerpak.xmlrpc.XMLRPCClient_ManagementTester
REM
