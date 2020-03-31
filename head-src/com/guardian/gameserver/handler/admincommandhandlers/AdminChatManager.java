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
package com.guardian.gameserver.handler.admincommandhandlers;

import java.util.StringTokenizer;

import com.guardian.gameserver.handler.IAdminCommandHandler;
import com.guardian.gameserver.model.actor.instance.L2PcInstance;
import com.guardian.gameserver.network.clientpackets.Say2;

public class AdminChatManager implements IAdminCommandHandler
{
	private static final String[] ADMIN_COMMANDS =
	{
		"admin_quiet"
	};

	@Override
	public boolean useAdminCommand(String command, L2PcInstance activeChar)
	{
		if (command.startsWith("admin_quiet"))
		{
			StringTokenizer st = new StringTokenizer(command);
			st.nextToken();

			try
			{
				String type = st.nextToken();
				if (type.startsWith("all"))
				{
					if (!Say2.isChatDisabled("all"))
					{
						Say2.setIsChatDisabled("all", true);
						activeChar.sendMessage("Todos os Chats foram desativados!	");
					}
					else
					{
						Say2.setIsChatDisabled("all", false);
						activeChar.sendMessage("Todos os Chats foram habilitados!");
					}
				}
				else if (type.startsWith("hero"))
				{
					if (!Say2.isChatDisabled("hero"))
					{
						Say2.setIsChatDisabled("hero", true);
						activeChar.sendMessage("O Chat Hero foi desativado!");
					}
					else
					{
						Say2.setIsChatDisabled("hero", false);
						activeChar.sendMessage("O Chat Hero foi habilitado!");
					}
				}
				else if (type.startsWith("trade"))
				{
					if (!Say2.isChatDisabled("trade"))
					{
						Say2.setIsChatDisabled("trade", true);
						activeChar.sendMessage("O Trade Chat foi desativado!");
					}
					else
					{
						Say2.setIsChatDisabled("trade", false);
						activeChar.sendMessage("O Trade Chat foi habilitado!");
					}
				}
				else if (type.startsWith("global"))
				{
					if (!Say2.isChatDisabled("global"))
					{
						Say2.setIsChatDisabled("global", true);
						activeChar.sendMessage("O Chat Global foi desativado!");
					}
					else
					{
						Say2.setIsChatDisabled("global", false);
						activeChar.sendMessage("O Chat Global foi habilitado!");
					}
				}
			}
			catch (Exception e)
			{
				activeChar.sendMessage("Use: //quiet <all|hero|trade|global>");
			}
		}
		return true;
	}

	@Override
	public String[] getAdminCommandList()
	{
		return ADMIN_COMMANDS;
	}
}