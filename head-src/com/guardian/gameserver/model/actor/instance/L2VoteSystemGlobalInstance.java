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
package com.guardian.gameserver.model.actor.instance;

import java.util.StringTokenizer;

import org.apache.log4j.Logger;

import br.ms.jmods.leonetosoft.votesystem.VoteSystem;
import br.ms.jmods.leonetosoft.votesystem.enums.TOP;
import br.ms.jmods.leonetosoft.votesystem.objects.IRetorno;
import br.ms.jmods.leonetosoft.votesystem.objects.VoteAutorized;
import br.ms.jmods.leonetosoft.votesystem.objects.VoteFail;
import br.ms.jmods.leonetosoft.votesystem.objects.VoteIpNotAutorized;
import br.ms.jmods.leonetosoft.votesystem.objects.VoteOk;
import br.ms.jmods.leonetosoft.votesystem.objects.VotePlayerNotAutorized;

import com.guardian.Config;
import com.guardian.gameserver.datatables.GmListTable;
import com.guardian.gameserver.model.L2World;
import com.guardian.gameserver.network.SystemMessageId;
import com.guardian.gameserver.network.clientpackets.Say2;
import com.guardian.gameserver.network.serverpackets.CreatureSay;
import com.guardian.gameserver.network.serverpackets.ItemList;
import com.guardian.gameserver.network.serverpackets.SystemMessage;
import com.guardian.gameserver.templates.L2NpcTemplate;
import com.guardian.gameserver.thread.ThreadPoolManager;

/**
 * Vote System Global Licensing in: votesystemglobal.com
 * @author leonetosoft
 */
public class L2VoteSystemGlobalInstance extends L2NpcInstance
{
	private static final Logger LOGGER = Logger.getLogger(L2VoteSystemGlobalInstance.class);
	private int TOTAL_VOTES = 0;
	private int PLAYERS_IN_VOTE = 0;
	
	public L2VoteSystemGlobalInstance(int objectID, L2NpcTemplate template)
	{
		super(objectID, template);
	}
	
	/**
	 * ByPass use
	 * 	<a action="bypass -h npc_%objectId%_vote 1"><font color="FFFF00">VOTE FOR TOPZONE</font></a><br1>
	 *	<a action="bypass -h npc_%objectId%_vote 2"><font color="FFFF00">VOTE FOR TOPSERVERS200</font></a><br1>
	 *	<a action="bypass -h npc_%objectId%_vote 3"><font color="FFFF00">VOTE FOR L2JBRASIL</font></a><br1>
	 *	<a action="bypass -h npc_%objectId%_vote 4"><font color="FFFF00">VOTE FOR L2NETWORK</font></a><br1>
	 *	<a action="bypass -h npc_%objectId%_vote 5"><font color="FFFF00">VOTE FOR HOPZONE</font></a><br1>
	 *  ...
	 */
	@Override
	public void onBypassFeedback(final L2PcInstance player, String command)
	{
		StringTokenizer st = new StringTokenizer(command, " ");
		String currentcommand = st.nextToken();
		
		if (VoteSystem.getInstance().getKey() == null)
		{
			configureSystem();
		}
		
		try
		{
			if (currentcommand.startsWith("vote"))
			{
				if (Config.VOTE_SYSTEM_GLOBAL_MIN_LV != 0 && player.getLevel() < Config.VOTE_SYSTEM_GLOBAL_MIN_LV)
				{
					player.sendMessage("Min Lv to vote is " + Config.VOTE_SYSTEM_GLOBAL_MIN_LV + ".");
					return;
				}
				if (Config.VOTE_SYSTEM_GLOBAL_MIN_PVP != 0 && player.getPvpKills() < Config.VOTE_SYSTEM_GLOBAL_MIN_PVP)
				{
					player.sendMessage("Min PvP to vote is " + Config.VOTE_SYSTEM_GLOBAL_MIN_PVP + ".");
					return;
				}
				if (player.isVoting())
				{
					player.sendMessage("You should expect another validation process.");
					return;
			}
				
				int top = Integer.parseInt(st.nextToken());
				String ip = player.getClient().getConnection().getInetAddress().getHostAddress();
				
				TOP voteTop = null;
				switch (top)
				{
					case 1: // TOPZONE
						if (Config.VOTE_SYSTEM_GLOBAL_TOPZONE_ENABLE)
							voteTop = TOP.TOPZONE;
						break;
					
					case 2:// TOPSERVERS200
						if (Config.VOTE_SYSTEM_GLOBAL_TOPSERVERS200_ENABLE)
							voteTop = TOP.TOPSERVERS200;
						break;
					
					case 3:// L2JBRASIL
						if (Config.VOTE_SYSTEM_GLOBAL_L2JBRASIL_ENABLE)
							voteTop = TOP.L2JBRASIL;
						break;
					
					case 4:// L2NETWORK
						if (Config.VOTE_SYSTEM_GLOBAL_L2NETWORK_ENABLE)
							voteTop = TOP.L2NETWORK;
						break;
					
					case 5:// HOPZONE
						if (Config.VOTE_SYSTEM_GLOBAL_HOPZONE_ENABLE)
							voteTop = TOP.HOPZONE;
						break;
					
					case 6:// TOPEXPERT
						if (Config.VOTE_SYSTEM_GLOBAL_TOPEXPERT_ENABLE)
							voteTop = TOP.TOPEXPERT200;
						break;
						
					case 7:// TOP100ARENA
						if (Config.VOTE_SYSTEM_GLOBAL_TOP100ARENA_ENABLE)
							voteTop = TOP.TOP100ARENA;
						break;

					case 8://L2TOPBRASIL
						if (Config.VOTE_SYSTEM_GLOBAL_L2TOPBRASIL_ENABLE)
							voteTop = TOP.L2TOPBRASIL;
						break;
				}
				
				if (voteTop == null)
				{
					say(player, "Vote selected is deactived.");
					return;
				}
				
				if (!canVote())
				{
					say(player, "The NPC reached the amount of simultaneous vote, please wait.");
					return;
				}
				
				addPlayerVote();
				player.isVoting(true);
				
				IRetorno ret = VoteSystem.getInstance().autorizeVote(player.getObjectId(), player.getName(), ip, voteTop);
				
				if (ret == null)
				{
					decrementPlayerVote();
					player.isVoting(false);
					say(player, "Vote not autorized in " + voteTop);
					return;
				}
				
				if (ret instanceof VotePlayerNotAutorized)
				{
					player.isVoting(false);
					decrementPlayerVote();
					String nextvote = ((VotePlayerNotAutorized) ret).getNextVote();
					say(player, "Vote not autorized, please await next vote in " + nextvote);
					return;
				}
				
				if (ret instanceof VoteIpNotAutorized)
				{
					player.isVoting(false);
					decrementPlayerVote();
					String nextvote = ((VoteIpNotAutorized) ret).getAutorizeDate();
					say(player, "Ip not autorized, please await next vote in " + nextvote);
					return;
				}
				
				if (ret instanceof VoteAutorized)
				{
					say(player, "Authorized vote, your vote in " + voteTop.toString() + " will be validated in " + Config.VOTE_SYSTEM_GLOBA_DELAY_CONFIRM + " seconds, we will vote now to earn your reward.");
					say(player, "Your protocol for this vote is: #" + ((VoteAutorized) ret).getVoteId());
					ThreadPoolManager.getInstance().scheduleGeneral(new ValidVote(player.getName(), voteTop), Config.VOTE_SYSTEM_GLOBA_DELAY_CONFIRM * 1000);
				}
			}
			else
				super.onBypassFeedback(player, command);
		}
		catch (Exception e)
		{
			player.sendMessage("NPC ESTA APRESENTANDO ERRO, INFORME O ADMIN!");
			e.printStackTrace();
		}
		
	}
	
	public synchronized boolean canVote()
	{
		if (this.PLAYERS_IN_VOTE < Config.VOTE_SYSTEM_GLOBAL_TOTAL_VOTE_SIMULTANEOUS)
		{
			return true;
		}
		
		return false;
	}
	
	public synchronized void addPlayerVote()
	{
		this.PLAYERS_IN_VOTE++;
	}
	
	public synchronized void decrementPlayerVote()
	{
		this.PLAYERS_IN_VOTE--;
	}
	
	void say(L2PcInstance player, String msg)
	{
		CreatureSay statusItemMsg = new CreatureSay(0, Say2.TELL, this.getTemplate().getName(), msg);
		
		player.sendPacket(statusItemMsg);
	}
	
	/**
	 * Packet Configure API
	 */
	public void configureSystem()
	{
		LOGGER.info("Vote System Global: Configuring system params.");
		VoteSystem.getInstance().setKey(Config.VOTE_SYSTEM_GLOBAL_CLIENT_KEY);
		VoteSystem.getInstance().setServerApi(Config.VOTE_SYSTEM_GLOBAL_API_URL);
		VoteSystem.getInstance().configureApi(Config.VOTE_SYSTEM_GLOBAL_TOPZONE_ID, Config.VOTE_SYSTEM_GLOBAL_TOPSERVERS200_ID, Config.VOTE_SYSTEM_GLOBAL_L2JBRASIL_ID, Config.VOTE_SYSTEM_GLOBAL_L2NETWORK_ID, Config.VOTE_SYSTEM_GLOBAL_HOPZONE_ID, Config.VOTE_SYSTEM_GLOBAL_TOPEXPERT_ID, Config.VOTE_SYSTEM_GLOBAL_TOP100ARENA_ID, Config.VOTE_SYSTEM_GLOBAL_L2TOPBRASIL_ID);
		LOGGER.info("Vote System Global: Couting total votes confirmed.");
		setTotalVotes(VoteSystem.getInstance().getVotesCount());
		LOGGER.info("Vote System Global: Total Votes confirmed is: #" + getTotalVotes());
		
	}
	
	/**
	 * Add item
	 * @param player
	 * @param id
	 * @param count
	 */
	public synchronized void AddItem(L2PcInstance player, int id, int count)
	{
		try
		{
			player.getInventory().addItem("cassino", id, count, player, null);
			player.getInventory().updateDatabase();
			player.sendPacket(new ItemList(player, true));
			
			if (id == 57)
			{
				SystemMessage smsg = new SystemMessage(SystemMessageId.EARNED_ADENA);
				smsg.addNumber(count);
				player.sendPacket(smsg);
				smsg = null;
			}
			// Otherwise, send message of object reward to client
			else
			{
				if (count > 1)
				{
					SystemMessage smsg = new SystemMessage(SystemMessageId.EARNED_S2_S1_S);
					smsg.addItemName(id);
					smsg.addNumber(count);
					player.sendPacket(smsg);
					smsg = null;
				}
				else
				{
					SystemMessage smsg = new SystemMessage(SystemMessageId.EARNED_ITEM);
					smsg.addItemName(id);
					player.sendPacket(smsg);
					smsg = null;
				}
			}
		}
		catch (Exception e)
		{
			LOGGER.error("Vote System Global: Error While add reward from player!!!");
			e.printStackTrace();
		}
	}
	
	public int getTotalVotes()
	{
		return TOTAL_VOTES;
	}
	
	public void setTotalVotes(int tOTAL_VOTES)
	{
		TOTAL_VOTES = tOTAL_VOTES;
	}
	
	/**
	 * Thread vote validation
	 * - Check if player is online
	 * @author leonetosoft
	 */
	private class ValidVote implements Runnable
	{
		private String player_name;
		private TOP top;
		
		public ValidVote(String player_name, TOP top)
		{
			this.player_name = player_name;
			this.top = top;
		}
	
		@Override
		public void run()
		{
			decrementPlayerVote();
			
			L2PcInstance player = L2World.getInstance().getPlayer(player_name);
			
			if (player == null || (player.getClient() == null || player.isOnline() == 0))
			{
				System.out.println("Vote System Global: Warning player " + player_name + " current offline.");
				return;
			}
			
			player.isVoting(false);
			
			IRetorno ret = VoteSystem.getInstance().validVote(player.getObjectId(), player.getClient().getConnection().getInetAddress().getHostAddress(), top);

			if (ret == null)
			{
				say(player, "Your vote in " + top.toString() + " is not validated.");
				return;
			}
			
			if (ret instanceof VoteFail)
			{
				say(player, "Your vote in " + top.toString() + " has NOT been confirmed!.");
				return;
			}
			
			if (ret instanceof VoteOk)
			{
				setTotalVotes(((VoteOk) ret).getTotalVotes());
				say(player, "Obrigado pelo seu voto! Voto numero #" + ((VoteOk) ret).getTotalVotes());
				AddItem(player, Config.VOTE_SYSTEM_GLOBAL_REWARD_ID, Config.VOTE_SYSTEM_GLOBAL_REWARD_AMOUNT);
				say(player, "Proximo voto em " + top.toString() + ": " + ((VoteOk) ret).getNextVote());
				GmListTable.broadcastMessageToGMs("Vote System Global: Player " + player.getName() + " confirmou o voto. Total de votos confirmados: " + getTotalVotes());
			}
			
		}
		
	}
	
}