package com.guardian.gameserver.handler.voicedcommandhandlers;

import javolution.text.TextBuilder;

import org.apache.log4j.Logger;

import com.guardian.Config;
import com.guardian.gameserver.datatables.sql.NpcTable;
import com.guardian.gameserver.handler.IVoicedCommandHandler;
import com.guardian.gameserver.managers.GrandBossManager;
import com.guardian.gameserver.managers.RaidBossSpawnManager;
import com.guardian.gameserver.model.actor.instance.L2PcInstance;
import com.guardian.gameserver.network.serverpackets.NpcHtmlMessage;
import com.guardian.gameserver.powerpak.RaidInfo.RaidInfoHandler;
import com.guardian.gameserver.templates.L2NpcTemplate;
import com.guardian.gameserver.templates.StatsSet;

public class Raid implements IVoicedCommandHandler
{
    private static Logger LOGGER = Logger.getLogger(RaidInfoHandler.class);
    private static final String[] _voicedCommands =
    {
        "raid"
    };
    
    @Override
    public boolean useVoicedCommand(String command, L2PcInstance activeChar, String target)
    {
        TextBuilder tb = new TextBuilder();
        NpcHtmlMessage msg = new NpcHtmlMessage(5);
        tb.append("<html><title>Grand Boss</title><body><br><center>");
        tb.append("<img src=\"L2UI_CH3.herotower_deco\" width=256 height=32><br><br>");

        for (final int boss : Config.RAID_INFO_IDS_LIST)
        {
            String name = "";
            L2NpcTemplate template = null;
            if ((template = NpcTable.getInstance().getTemplate(boss)) != null)
            {
                name = template.getName();
            }
            else
            {
                LOGGER.warn("[RaidInfoHandler][sendInfo] Raid Boss with ID " + boss + " is not defined into NpcTable");
                continue;
            }
            
            StatsSet actual_boss_stat = null;
            GrandBossManager.getInstance().getStatsSet(boss);
            long delay = 0;
            
            if (NpcTable.getInstance().getTemplate(boss).type.equals("L2RaidBoss"))
            {
                actual_boss_stat = RaidBossSpawnManager.getInstance().getStatsSet(boss);
                if (actual_boss_stat != null)
                    delay = actual_boss_stat.getLong("respawnTime");
            }
            else if (NpcTable.getInstance().getTemplate(boss).type.equals("L2GrandBoss"))
            {
                actual_boss_stat = GrandBossManager.getInstance().getStatsSet(boss);
                if (actual_boss_stat != null)
                    delay = actual_boss_stat.getLong("respawn_time");
            }
            else
                continue;
            
            if (delay <= System.currentTimeMillis())
            {
                tb.append("<font color=\"00C3FF\">" + name + "</color>: " + "<font color=\"9CC300\">Esta Vivo</color>" + "<br1>");
            }
            else
            {
                final int hours = (int) ((delay - System.currentTimeMillis()) / 1000 / 60 / 60);
                final int mins = (int) (((delay - (hours * 60 * 60 * 1000)) - System.currentTimeMillis()) / 1000 / 60);
                final int seconts = (int) (((delay - ((hours * 60 * 60 * 1000) + (mins * 60 * 1000))) - System.currentTimeMillis()) / 1000);
                tb.append("<font color=\"00C3FF\">" + name + "</color>" + "<font color=\"FFFFFF\">" + " " + "Respawn:</color>" + " " + " <font color=\"32C332\">" + hours + " : " + mins + " : " + seconts + "</color><br1>");
            }
        }
                
        tb.append("<br><img src=\"L2UI_CH3.herotower_deco\" width=256 height=32><br>");
        tb.append("</center></body></html>");
        msg.setHtml(tb.toString());
        activeChar.sendPacket(msg);
        return false;
    }
    
    @Override
    public String[] getVoicedCommandList()
    {
     return _voicedCommands;
    }    
}