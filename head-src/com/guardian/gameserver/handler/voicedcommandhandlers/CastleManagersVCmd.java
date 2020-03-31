package com.guardian.gameserver.handler.voicedcommandhandlers;

import com.guardian.gameserver.handler.IVoicedCommandHandler;
import com.guardian.gameserver.managers.CastleManager;
import com.guardian.gameserver.model.actor.instance.L2PcInstance;
import com.guardian.gameserver.model.entity.siege.Castle;
import com.guardian.gameserver.network.SystemMessageId;
import com.guardian.gameserver.network.serverpackets.NpcHtmlMessage;
import com.guardian.gameserver.network.serverpackets.SiegeInfo;

public class CastleManagersVCmd implements IVoicedCommandHandler
{
 private static final String[] _voicedCommands = {"castlemanager","siege_gludio","siege_dion","siege_giran","siege_oren","siege_aden","siege_innadril","siege_goddard","siege_rune","siege_schuttgart"};
      
       @Override
       public boolean useVoicedCommand(String command, L2PcInstance activeChar, String target)
       {
               if (command.startsWith("castlemanager"))
               {
                       sendHtml(activeChar);
               }
              
               if (command.startsWith("siege_"))
               {
                       if (activeChar.getClan() != null && !activeChar.isClanLeader())
                       {
                               activeChar.sendPacket(SystemMessageId.YOU_ARE_NOT_AUTHORIZED_TO_DO_THAT);
                               return false;
                       }
                      
                       int castleId = 0;
                      
                       if (command.startsWith("siege_gludio"))
                               castleId = 1;
                       else if (command.startsWith("siege_dion"))
                               castleId = 2;
                       else if (command.startsWith("siege_giran"))
                               castleId = 3;
                       else if (command.startsWith("siege_oren"))
                               castleId = 4;
                       else if (command.startsWith("siege_aden"))
                               castleId = 5;
                       else if (command.startsWith("siege_innadril"))
                               castleId = 6;
                       else if (command.startsWith("siege_goddard"))
                               castleId = 7;
                       else if (command.startsWith("siege_rune"))
                               castleId = 8;
                       else if (command.startsWith("siege_schuttgart"))
                               castleId = 9;
                      
                       Castle castle = CastleManager.getInstance().getCastleById(castleId);
                       if(castle != null && castleId != 0)
                               activeChar.sendPacket(new SiegeInfo(castle));
               }
               return true;
       }
       
       private static void sendHtml(L2PcInstance activeChar)
       {
               String htmFile = "data/html/mods/CastleManager.htm";
              
               NpcHtmlMessage msg = new NpcHtmlMessage(5);
               msg.setFile(htmFile);
               activeChar.sendPacket(msg);
       }
      
       @Override
       public String[] getVoicedCommandList()
       {
        return _voicedCommands;
       }
}