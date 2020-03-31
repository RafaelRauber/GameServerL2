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
package com.guardian.gameserver.handler.voicedcommandhandlers;

import com.guardian.gameserver.handler.IVoicedCommandHandler;
import com.guardian.gameserver.managers.CastleManager;
import com.guardian.gameserver.model.actor.instance.L2DoorInstance;
import com.guardian.gameserver.model.actor.instance.L2PcInstance;
import com.guardian.gameserver.model.entity.siege.Castle;
import com.guardian.gameserver.network.serverpackets.Ride;

public class CastleCmd implements IVoicedCommandHandler
{
	private static final String[] VOICED_COMMANDS =
	{
		"open doors",
		"close doors",
		"ride wyvern"
	};
	
	@Override
	public boolean useVoicedCommand(final String command, final L2PcInstance activeChar, final String target)
	{
		if (command.startsWith("open doors") && target.equals("castle") && activeChar.isClanLeader())
		{
			L2DoorInstance door = (L2DoorInstance) activeChar.getTarget();
			Castle castle = CastleManager.getInstance().getCastleById(activeChar.getClan().getHasCastle());
			
			if (door == null || castle == null)
				return false;
			
			if (castle.checkIfInZone(door.getX(), door.getY(), door.getZ()))
			{
				door.openMe();
			}
			
			door = null;
			castle = null;
		}
		else if (command.startsWith("close doors") && target.equals("castle") && activeChar.isClanLeader())
		{
			L2DoorInstance door = (L2DoorInstance) activeChar.getTarget();
			Castle castle = CastleManager.getInstance().getCastleById(activeChar.getClan().getHasCastle());
			
			if (door == null || castle == null)
				return false;
			
			if (castle.checkIfInZone(door.getX(), door.getY(), door.getZ()))
			{
				door.closeMe();
			}
			door = null;
			castle = null;
		}
		else if (command.startsWith("ride wyvern") && target.equals("castle"))
		{
			if (activeChar.getClan().getHasCastle() > 0 && activeChar.isClanLeader())
			{
				if (!activeChar.disarmWeapons())
					return false;
				
				Ride mount = new Ride(activeChar.getObjectId(), Ride.ACTION_MOUNT, 12621);
				activeChar.sendPacket(mount);
				activeChar.broadcastPacket(mount);
				activeChar.setMountType(mount.getMountType());
				
				mount = null;
			}
		}
		
		return true;
	}
	
	@Override
	public String[] getVoicedCommandList()
	{
		return VOICED_COMMANDS;
	}
}
