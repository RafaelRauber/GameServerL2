/*
 * L2Guardian - MrFreedomFights 
 * 
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

import com.guardian.Config;
import com.guardian.gameserver.handler.IVoicedCommandHandler;
import com.guardian.gameserver.model.actor.instance.L2PcInstance;

/**
 * @author L2JFrozen
 */
public class VersionCmd implements IVoicedCommandHandler
{
	private static String[] _voicedCommands =
	{
		"version"
	};
	
	@Override
	public boolean useVoicedCommand(final String command, final L2PcInstance activeChar, final String target)
	{
		if (command.equalsIgnoreCase("version"))
		{
			activeChar.sendMessage("L2jFrozen core revision:        " + Config.SERVER_VERSION);
			activeChar.sendMessage("L2jFrozen datapack revision:    " + Config.DATAPACK_VERSION);
		}
		return true;
	}
	
	@Override
	public String[] getVoicedCommandList()
	{
		return _voicedCommands;
	}
}
