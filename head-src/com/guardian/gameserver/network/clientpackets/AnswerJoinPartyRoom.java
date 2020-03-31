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
package com.guardian.gameserver.network.clientpackets;

import com.guardian.gameserver.model.L2World;
import com.guardian.gameserver.model.PartyMatchRoom;
import com.guardian.gameserver.model.PartyMatchRoomList;
import com.guardian.gameserver.model.PartyMatchWaitingList;
import com.guardian.gameserver.model.actor.instance.L2PcInstance;
import com.guardian.gameserver.network.SystemMessageId;
import com.guardian.gameserver.network.serverpackets.ExManagePartyRoomMember;
import com.guardian.gameserver.network.serverpackets.ExPartyRoomMember;
import com.guardian.gameserver.network.serverpackets.PartyMatchDetail;
import com.guardian.gameserver.network.serverpackets.SystemMessage;

public final class AnswerJoinPartyRoom extends L2GameClientPacket
{
	private int _answer; // 1 or 0
	
	@Override
	protected void readImpl()
	{
		_answer = readD();
	}
	
	@Override
	protected void runImpl()
	{
		final L2PcInstance player = getClient().getActiveChar();
		if (player == null)
			return;
		
		final L2PcInstance partner = player.getActiveRequester();
		if (partner == null)
		{
			// Partner hasn't be found, cancel the invitation
			player.sendPacket(new SystemMessage(SystemMessageId.TARGET_IS_NOT_FOUND_IN_THE_GAME));
			player.setActiveRequester(null);
			return;
		}
		else if (L2World.getInstance().getPlayer(partner.getObjectId()) == null)
		{
			// Partner hasn't be found, cancel the invitation
			player.sendPacket(new SystemMessage(SystemMessageId.TARGET_IS_NOT_FOUND_IN_THE_GAME));
			player.setActiveRequester(null);
			return;
		}
		
		// If answer is positive, join the requester's PartyRoom.
		if (_answer == 1 && !partner.isRequestExpired())
		{
			final PartyMatchRoom _room = PartyMatchRoomList.getInstance().getRoom(partner.getPartyRoom());
			if (_room == null)
				return;
			
			if ((player.getLevel() >= _room.getMinLvl()) && (player.getLevel() <= _room.getMaxLvl()))
			{
				// Remove from waiting list
				PartyMatchWaitingList.getInstance().removePlayer(player);
				
				player.setPartyRoom(partner.getPartyRoom());
				
				player.sendPacket(new PartyMatchDetail(player, _room));
				player.sendPacket(new ExPartyRoomMember(player, _room, 0));
				
				for (final L2PcInstance _member : _room.getPartyMembers())
				{
					if (_member == null)
						continue;
					
					_member.sendPacket(new ExManagePartyRoomMember(player, _room, 0));
					_member.sendPacket(new SystemMessage(SystemMessageId.S1_ENTERED_PARTY_ROOM).addString(player.getName()));
				}
				_room.addMember(player);
				
				// Info Broadcast
				player.broadcastUserInfo();
			}
			else
				player.sendPacket(new SystemMessage(SystemMessageId.CANT_ENTER_PARTY_ROOM));
		}
		// Else, send a message to requester.
		else
			partner.sendPacket(new SystemMessage(SystemMessageId.PARTY_MATCHING_REQUEST_NO_RESPONSE));
		
		// reset transaction timers
		player.setActiveRequester(null);
		partner.onTransactionResponse();
	}
	
	@Override
	public String getType()
	{
		return "[C] D0:15 AnswerJoinPartyRoom";
	}
}