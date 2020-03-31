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

import java.util.StringTokenizer;

import javolution.text.TextBuilder;

import com.guardian.gameserver.handler.IVoicedCommandHandler;
import com.guardian.gameserver.model.actor.instance.L2PcInstance;
import com.guardian.gameserver.network.clientpackets.Say2;
import com.guardian.gameserver.network.serverpackets.CreatureSay;
import com.guardian.gameserver.network.serverpackets.NpcHtmlMessage;

/**
* @author Elfocrash
*
*/
public class ClanMessageCmd implements IVoicedCommandHandler
{
private static String[] VOICED_COMMANDS =
{ "setclanmes", "setmes" , "clanmes" };


@Override
public boolean useVoicedCommand(String command, L2PcInstance activeChar, String target)
{
if(command.equals("clanmes"))
{
if(activeChar.getClan() != null)
{
if(activeChar.getClan().getClanMes() != null)
{
CreatureSay np = new CreatureSay(0, Say2.ALLIANCE,activeChar.getClan().getLeader().getName() , activeChar.getClan().getClanMes());
activeChar.sendPacket(np);
}
}
return true;
}


if(command.equals("setclanmes"))
{
if(activeChar.isClanLeader())
mainHtml(activeChar);
}

if(command.startsWith("setmes"))
{
if(activeChar.isClanLeader())
{
String clanmes = "";
StringTokenizer s = new StringTokenizer(command);
s.nextToken();

try{

while(s.hasMoreTokens())
clanmes = clanmes + s.nextToken() + " ";
activeChar.getClan().setClanMes(clanmes);
mainHtml(activeChar);
return true;

}
catch(Exception e)
{
e.printStackTrace();
}
return true;
}
activeChar.sendMessage("Voce nao e o lider do clan. Nao pode inserir uma nova mensagem.");
return false;
}


return true;
}

public static void mainHtml(L2PcInstance activeChar)
{
String clanMes = null;
clanMes = activeChar.getClan().getClanMes();
if(clanMes == null)
clanMes = "Nenhuma Mensagem de Clan.";
NpcHtmlMessage nhm = new NpcHtmlMessage(5);
TextBuilder tb = new TextBuilder("");

tb.append("<html><head><title>Clan Mensagem</title></head><body>");
tb.append("<center>");
tb.append("<table width=\"250\" cellpadding=\"5\" bgcolor=\"000000\">");
tb.append("<tr>");
tb.append("<td width=\"45\" valign=\"top\" align=\"center\"><img src=\"L2ui_ch3.menubutton4\" width=\"38\" height=\"38\"></td>");
tb.append("<td valign=\"top\"><font color=\"FF6600\">Inserir Mensagem de Clan</font>");
tb.append("<br1><font color=\"00FF00\">"+activeChar.getName()+"</font>, Aqui voce pode colocar uma mensagem para o Clan.<br1></td>");
tb.append("</tr>");
tb.append("</table>");
tb.append("Mensagem Atual: <br><font color=\"77FB99\">" + clanMes+"</font>");
tb.append("<multiedit var=\"mes\" width=170 height=20><br>");
tb.append("<a action=\"bypass voiced_setmes $mes\">Salvar Mensagem</a></center>");

tb.append("</center>");
tb.append("</body></html>");


nhm.setHtml(tb.toString());
activeChar.sendPacket(nhm);

}

@Override
public String[] getVoicedCommandList()
{
return VOICED_COMMANDS;
}
}
