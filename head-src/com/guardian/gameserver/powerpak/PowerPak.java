/*
 * L2Guardian - MrFreedomFights 
 * 
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2, or (at your option)
 * any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA
 * 02111-1307, USA.
 *
 * http://www.gnu.org/copyleft/gpl.html
 */
package com.guardian.gameserver.powerpak;

/**
 * L2JFrozen
 */
import org.apache.log4j.Logger;

import com.guardian.Config;
import com.guardian.gameserver.communitybbs.CommunityBoard;
import com.guardian.gameserver.datatables.BufferSkillsTable;
import com.guardian.gameserver.datatables.CharSchemesTable;
import com.guardian.gameserver.handler.AutoVoteRewardHandler;
import com.guardian.gameserver.handler.VoicedCommandHandler;
import com.guardian.gameserver.handler.custom.CustomBypassHandler;
import com.guardian.gameserver.handler.voicedcommandhandlers.Repair;
import com.guardian.gameserver.model.actor.instance.L2PcInstance;
import com.guardian.gameserver.powerpak.Buffer.BuffHandler;
import com.guardian.gameserver.powerpak.Buffer.BuffTable;
import com.guardian.gameserver.powerpak.RaidInfo.RaidInfoHandler;
import com.guardian.gameserver.powerpak.engrave.EngraveManager;
import com.guardian.gameserver.powerpak.globalGK.GKHandler;
import com.guardian.gameserver.powerpak.gmshop.GMShop;
import com.guardian.gameserver.powerpak.vote.L2TopDeamon;
import com.guardian.gameserver.powerpak.xmlrpc.XMLRPCServer;

public class PowerPak
{
	private final Logger LOGGER = Logger.getLogger(PowerPak.class);
	private static PowerPak _instance = null;
	
	public static PowerPak getInstance()
	{
		if (_instance == null)
		{
			_instance = new PowerPak();
		}
		return _instance;
	}
	
	private PowerPak()
	{
		if (Config.POWERPAK_ENABLED)
		{
			PowerPakConfig.load();
			if (PowerPakConfig.BUFFER_ENABLED)
			{
				LOGGER.info("Buffer is Enabled.");
				BuffTable.getInstance();
				if ((PowerPakConfig.BUFFER_COMMAND != null && PowerPakConfig.BUFFER_COMMAND.length() > 0) || PowerPakConfig.BUFFER_USEBBS)
				{
					
					final BuffHandler handler = new BuffHandler();
					if (PowerPakConfig.BUFFER_USECOMMAND && PowerPakConfig.BUFFER_COMMAND != null && PowerPakConfig.BUFFER_COMMAND.length() > 0)
					{
						VoicedCommandHandler.getInstance().registerVoicedCommandHandler(handler);
					}
					
					if (PowerPakConfig.BUFFER_USEBBS)
					{
						CommunityBoard.getInstance().registerBBSHandler(handler);
					}
					CustomBypassHandler.getInstance().registerCustomBypassHandler(handler);
					
				}
				
				BufferSkillsTable.getInstance();
				CharSchemesTable.getInstance();
			}
			
			if (PowerPakConfig.GLOBALGK_ENABDLED)
			{
				final GKHandler handler = new GKHandler();
				if (PowerPakConfig.GLOBALGK_COMMAND != null && PowerPakConfig.GLOBALGK_COMMAND.length() > 0)
				{
					VoicedCommandHandler.getInstance().registerVoicedCommandHandler(handler);
				}
				
				if (PowerPakConfig.GLOBALGK_USEBBS)
				{
					CommunityBoard.getInstance().registerBBSHandler(handler);
				}
				CustomBypassHandler.getInstance().registerCustomBypassHandler(handler);
				LOGGER.info("Global Gatekeeper is Enabled.");
			}
			
			if (PowerPakConfig.GMSHOP_ENABLED)
			{
				final GMShop gs = new GMShop();
				CustomBypassHandler.getInstance().registerCustomBypassHandler(gs);
				if (PowerPakConfig.GLOBALGK_COMMAND != null && PowerPakConfig.GLOBALGK_COMMAND.length() > 0)
				{
					VoicedCommandHandler.getInstance().registerVoicedCommandHandler(gs);
				}
				
				if (PowerPakConfig.GMSHOP_USEBBS)
				{
					CommunityBoard.getInstance().registerBBSHandler(gs);
				}
				LOGGER.info("GM Shop is Enabled.");
			}
			
			if (PowerPakConfig.ENGRAVER_ENABLED)
			{
				EngraveManager.getInstance();
				LOGGER.info("Engrave System is Enabled.");
			}
			
			if (PowerPakConfig.L2TOPDEMON_ENABLED)
			{
				L2TopDeamon.getInstance();
				LOGGER.info("L2TOPDEMON is Enabled.");
			}
			
			if (PowerPakConfig.XMLRPC_ENABLED)
			{
				XMLRPCServer.getInstance();
				LOGGER.info("XMLRPC is Enabled.");
			}
			
			final RaidInfoHandler handler = new RaidInfoHandler();
			CustomBypassHandler.getInstance().registerCustomBypassHandler(handler);
			LOGGER.info("Raid Info is Enabled.");
			
			if (PowerPakConfig.CHAR_REPAIR)
			{
				final Repair repair_handler = new Repair();
				VoicedCommandHandler.getInstance().registerVoicedCommandHandler(repair_handler);
				CustomBypassHandler.getInstance().registerCustomBypassHandler(repair_handler);
				LOGGER.info("Char Repair is Enabled.");
			}
			
			// Vote Reward System
			if (PowerPakConfig.AUTOVOTEREWARD_ENABLED)
				AutoVoteRewardHandler.getInstance();
		}
	}
	
	public void chatHandler(final L2PcInstance sayer, final int chatType, final String message)
	{
	}
}