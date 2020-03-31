/*
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any later
 * version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU General Public License for more
 * details.
 *
 * You should have received a copy of the GNU General Public License along with
 * this program. If not, see <http://www.gnu.org/licenses/>.
 */
package com.guardian.gameserver.handler.voicedcommandhandlers;

import com.guardian.gameserver.handler.IVoicedCommandHandler;
import com.guardian.gameserver.model.L2World;
import com.guardian.gameserver.model.actor.instance.L2PcInstance;
import com.guardian.gameserver.network.serverpackets.NpcHtmlMessage;

/**
 *
 * @author Bluur
 * @version 1.0
 */

public class Menu implements IVoicedCommandHandler
{
    private static final String[] _voicedCommands =
    {
        "menu",
        "setPartyRefuse",
        "setTradeRefuse",    
        "setbuffsRefuse",
        "setMessageRefuse",
        "setxpnot",
        "setSsprot"
    };
    
    private static final String ACTIVED = "<font color=00FF00>ON</font>";
    private static final String DESATIVED = "<font color=FF0000>OFF</font>";
    
    @Override
    public boolean useVoicedCommand(String command, L2PcInstance activeChar, String target)
    {
        if (command.equals("menu"))
            showHtml(activeChar);        
                
        else if (command.equals("setPartyRefuse"))
        {
            if (activeChar.isPartyInvProt())
                activeChar.setIsPartyInvProt(false);
            else
                activeChar.setIsPartyInvProt(true);            
            showHtml(activeChar);
        }    
        else if (command.equals("setTradeRefuse"))
        {
            if (activeChar.isInTradeProt())
                activeChar.setIsInTradeProt(false);
            else
                activeChar.setIsInTradeProt(true);
            showHtml(activeChar);
        }        
        else if (command.equals("setMessageRefuse"))
        {        
            if (activeChar.getMessageRefusal())
                activeChar.setMessageRefusal(false);
            else
                activeChar.setMessageRefusal(true);
            showHtml(activeChar);
        }
        else if (command.equals("setbuffsRefuse"))
        {        
            if (activeChar.isBuffProtected())
                activeChar.setIsBuffProtected(false);
            else
                activeChar.setIsBuffProtected(true);
                activeChar.sendMessage("Buff protection.");
            showHtml(activeChar);
        }
        
        else if (command.equals("setxpnot"))
        {        
            if (activeChar.cantGainXP())
                activeChar.cantGainXP(false);
            else
                activeChar.cantGainXP(true);
                activeChar.sendMessage(" Xp effects.");
            showHtml(activeChar);
        }        
        else if (command.equals("setSsprot"))
        {        
            if (activeChar.isSSDisabled())
                activeChar.setIsSSDisabled(false);
            else
                activeChar.setIsSSDisabled(true);
                activeChar.sendMessage("Soulshots effects.");
            showHtml(activeChar);
        }        
        return true;
    }
    
    private static void showHtml(L2PcInstance activeChar)
    {
        NpcHtmlMessage html = new NpcHtmlMessage(0);
        html.setFile("data/html/mods/menu.htm");
        L2World.getInstance();
        html.replace("%online%", L2World.getAllPlayersCount());    
        html.replace("%partyRefusal%", activeChar.isPartyInvProt() ? ACTIVED : DESATIVED);
        html.replace("%tradeRefusal%", activeChar.isInTradeProt() ? ACTIVED : DESATIVED);
        html.replace("%buffsRefusal%", activeChar.isBuffProtected() ? ACTIVED : DESATIVED);
        html.replace("%messageRefusal%", activeChar.getMessageRefusal() ? ACTIVED : DESATIVED);    
        html.replace("%cantGainXP%", activeChar.cantGainXP() ? ACTIVED : DESATIVED);
        html.replace("%Eff.Ss%", activeChar.isSSDisabled() ? ACTIVED : DESATIVED);    
        
        activeChar.sendPacket(html);
    }
    
    @Override
    public String[] getVoicedCommandList()
    {
        return _voicedCommands;
    }
}