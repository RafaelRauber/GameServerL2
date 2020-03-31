/* L2Guardian - MrFreedomFights 
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
package com.guardian.gameserver.model.zone.type;

import com.guardian.gameserver.model.L2Character;
import com.guardian.gameserver.model.actor.instance.L2PcInstance;
import com.guardian.gameserver.model.base.Race;
import com.guardian.gameserver.model.zone.L2ZoneType;
import com.guardian.gameserver.network.SystemMessageId;
import com.guardian.gameserver.network.serverpackets.SystemMessage;

/**
 * A mother-trees zone
 * @author durgus
 */
public class L2MotherTreeZone extends L2ZoneType
{
	public L2MotherTreeZone(final int id)
	{
		super(id);
	}
	
	@Override
	protected void onEnter(final L2Character character)
	{
		if (character instanceof L2PcInstance)
		{
			L2PcInstance player = (L2PcInstance) character;
			
			if (player.isInParty())
			{
				for (final L2PcInstance member : player.getParty().getPartyMembers())
					if (member.getRace() != Race.elf)
						return;
			}
			
			player.setInsideZone(L2Character.ZONE_MOTHERTREE, true);
			player.sendPacket(new SystemMessage(SystemMessageId.ENTER_SHADOW_MOTHER_TREE));
			
			player = null;
		}
	}
	
	@Override
	protected void onExit(final L2Character character)
	{
		if (character instanceof L2PcInstance && character.isInsideZone(L2Character.ZONE_MOTHERTREE))
		{
			character.setInsideZone(L2Character.ZONE_MOTHERTREE, false);
			((L2PcInstance) character).sendPacket(new SystemMessage(SystemMessageId.EXIT_SHADOW_MOTHER_TREE));
		}
	}
	
	@Override
	protected void onDieInside(final L2Character character)
	{
	}
	
	@Override
	protected void onReviveInside(final L2Character character)
	{
	}
	
}
