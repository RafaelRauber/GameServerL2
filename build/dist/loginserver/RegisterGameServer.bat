@echo off
color 3
@java -Djava.util.logging.config.file=console.cfg -cp lib/*;lib/l2jguardian-core.jar com.guardian.gsregistering.GameServerRegister
@pause
