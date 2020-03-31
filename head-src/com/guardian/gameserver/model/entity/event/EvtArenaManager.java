package com.guardian.gameserver.model.entity.event;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import javolution.util.FastList;
import javolution.util.FastMap;

import com.guardian.Config;
import com.guardian.gameserver.model.actor.instance.L2ItemInstance;
import com.guardian.gameserver.model.actor.instance.L2PcInstance;
import com.guardian.gameserver.network.serverpackets.InventoryUpdate;
import com.guardian.gameserver.network.serverpackets.ItemList;
import com.guardian.gameserver.templates.L2Item;
import com.guardian.util.L2FastList;
import com.guardian.util.random.Rnd;

/**
 * Event Arena Manager.
 * 
 * 
 * This Event works only in pairs. Two players must to do a party and the leader register in the event
 * if any member of party already registered, then the registrations fails. Only leader can removes
 * from event. anyone can see the battles except the registered player in event.
 * The battle only finish when only one party keep alive. In the end of each battle, winners earn a reward.
 * 
 * The event is open all the time. have a little interval between call of battles of 1 minute
 * 
 * @author Alisson Oliveira
 *
 */
public class EvtArenaManager implements Runnable {
	
	// list of participants
	private List<Pair> registered;
	// number of Arenas 
	private int free =  Config.ARENA_EVENT_COUNT;
	// Arenas
	private Arena[] arenas = new Arena[Config.ARENA_EVENT_COUNT];
	// list of fights going on
	private Map<Integer, String> fights = new FastMap<Integer, String>(Config.ARENA_EVENT_COUNT);
	
	public EvtArenaManager() {
		registered = new L2FastList<Pair>();
		int[] coord;
		for(int i=0; i < Config.ARENA_EVENT_COUNT; i++) {
			coord = Config.ARENA_EVENT_LOCS[i];
			arenas[i] = new Arena(i, coord[0], coord[1], coord[2]);
		}
		System.out.println("Initialized Arena Event");
	}
	

	public static EvtArenaManager getInstance() {
		return SingletonHolder.INSTANCE;
	}

	public boolean register(L2PcInstance player, L2PcInstance assist) {
		for(Pair p : registered) {
			if(p.getLeader() == player || p.getAssist() == player) {
				player.sendMessage("You Already registered");
				return false;
			} else if (p.getLeader() == assist || p.getAssist() == assist) {
				player.sendMessage("Your Partner Already registered");
				return false;
			}
		}
		if(!Config.ARENA_ALLOW_S) {
			checkItems(player);
			checkItems(assist);
		}
		return registered.add(new Pair(player, assist));
	}
	
	private void checkItems(L2PcInstance player){
		L2ItemInstance item;
		int slot;
		for(int i=1; i < 15; i++){
			item = player.getInventory().getPaperdollItem(i);
			if(item==null)
				continue;
			if(item.getItem().getItemGrade() == L2Item.CRYSTAL_S){
				slot = item.getItem().getBodyPart();
				switch(item.getEquipSlot()) {
					case 1:
						slot = L2Item.SLOT_L_EAR;
						break;
					case 2:
						slot = L2Item.SLOT_R_EAR;
						break;
					case 4:
						slot = L2Item.SLOT_L_FINGER;
						break;
					case 5:
						slot = L2Item.SLOT_R_FINGER;
						break;
					default:
						break;
				}

				L2ItemInstance[] items = player.getInventory().unEquipItemInBodySlotAndRecord(slot);
	    		InventoryUpdate iu = new InventoryUpdate();
	    		iu.addItems(Arrays.asList(items));
	    		player.sendPacket(iu);
			}
		}
		player.sendPacket(new ItemList(player, true));
		player.broadcastUserInfo();
	}
	
	public boolean isRegistered(L2PcInstance player) {
		for(Pair p : registered) {
			if(p.getLeader() == player || p.getAssist() == player) {
				return true;
			}
		}
		return false;
	}

	public void addSpectator(L2PcInstance spec, int arenaId) {
		Arena arena = getArena(arenaId);
		if(arena != null)
			arena.addSpectator(spec);
	}
	
	private Arena getArena(int id) {
		for(Arena arena : arenas) {
			if(arena.id == id) {
				return arena;
			}
		}
		return null;
	}
	
	public Map<Integer, String> getFights() {
		return fights;
	}
	
	public boolean remove(L2PcInstance player) {
		for(Pair p : registered) {
			if(p.getLeader() == player) {
				registered.remove(p);
				return true;
			}
		}
		return false;
	}
	
	@Override
	public synchronized void run() {
		// while server is running
		while(true) {
			// if no have participants or arenas are busy wait 1 minute 
			if(registered.size() < 2 || free == 0) {
				try {
					Thread.sleep(Config.ARENA_CALL_INTERVAL); 
				} catch (InterruptedException e) {
					if(Config.ENABLE_ALL_EXCEPTIONS)
						e.printStackTrace();
				}
				continue;
			}
			List<Pair> opponents = selectOpponents();
			if(opponents != null && opponents.size() == 2) {
				Thread T = new Thread(new EvtArenaTask(opponents));
				T.setDaemon(true);
				T.start();
			}
			//wait 1 minute for not stress server
			try {
				Thread.sleep(Config.ARENA_CALL_INTERVAL); 
			} catch (InterruptedException e) {
				if(Config.ENABLE_ALL_EXCEPTIONS)
					e.printStackTrace();
			}
		}
	}
	
	private List<Pair> selectOpponents() {
		List<Pair> opponents = new FastList<Pair>();
		Pair pairOne=null, pairTwo=null;
		int tries = 3;
		do {
			int first = 0, second = 0;
			if(getRegisteredCount() < 2)
				return opponents;
			
			if(pairOne == null) {
				first = Rnd.get(getRegisteredCount());
				pairOne = registered.get(first);
				if(pairOne.check()) {
					opponents.add(0,pairOne);
					registered.remove(first);
				} else {
					pairOne = null;
					registered.remove(first);
					return null;
				}
				
			}
			if(pairTwo == null) {
				second = Rnd.get(getRegisteredCount());
				pairTwo = registered.get(second);
				if(pairTwo.check()){
					opponents.add(1, pairTwo);
					registered.remove(second);
				} else {
					pairTwo = null;
					registered.remove(second);
					return null;
				}
				
			}	
		} while ((pairOne == null || pairTwo == null) && --tries > 0);
		return opponents;
	}
	
	public int getRegisteredCount() {
		return registered.size();
	}
	
	private class Pair {
		private L2PcInstance leader, assist;
		
		public Pair(L2PcInstance leader, L2PcInstance assist) {
			this.leader = leader;
			this.assist = assist;
		}

		public L2PcInstance getAssist() {
			return assist;
		}

		public L2PcInstance getLeader() {
			return leader;
		}
		
		/*
		 * This method send messages to player, but not implement the remotion.
		 * must be removed from caller
		 * 
		 */
		public boolean check(){
			if((leader == null || leader.isOnline()==0) && (assist != null && assist.isOnline()==1)) {
				assist.sendMessage("ArenaEventManager: You participation in Event was Canceled");
				return false;
			} else if( (assist == null || assist.isOnline()==0) && (leader != null && leader.isOnline()==1)) {
				leader.sendMessage("ArenaEventManager: You participation in Event was Canceled");
				return false;
			}
			return true;
		}

		public boolean isAlive() {
			if((leader == null || leader.isOnline()==0) && (assist==null || assist.isOnline()==0)){
				return false;
			} else if((leader == null || leader.isOnline()==0)) {
				return !assist.isDead();
			} else if((assist == null || assist.isOnline()==0)) {
				return !leader.isDead();
			}
			return !(leader.isDead() && assist.isDead());
		}

		public void teleportTo(int x, int y, int z) {
			if(leader != null) {
				leader.restoreCP();
				leader.restoreHPMP();
				leader.teleToLocation(x, y, z);
			}
			if(assist != null) {
				assist.restoreCP();
				assist.restoreHPMP();
				assist.teleToLocation(x, y, z);
			}
		}

		public void rewards() { 
			if(leader != null) {
				leader.getInventory().addItem("Arena_Event", Config.ARENA_REWARD_ID, Config.ARENA_REWARD_COUNT, leader, null); 
				leader.sendPacket( new ItemList(leader, true));
				leader.increaseArenaWins();
			}
			if(assist != null) {
				assist.getInventory().addItem("Arena_Event",  Config.ARENA_REWARD_ID, Config.ARENA_REWARD_COUNT, assist, null);
				assist.sendPacket( new ItemList(assist, true));
				assist.increaseArenaWins();
			}
			sendMessage("Arena Event Manager: You Win!!!");
		}

		public void setInEvent(boolean val) {
			if(leader != null)
				leader.setInArenaEvent(val);
			if(assist != null)
				assist.setInArenaEvent(val);
		}

		public void revive() {
			if(leader != null)
				leader.doRevive();
			if(assist != null)
				assist.doRevive();
		}

		public void setImobilised(boolean val) {
			if(leader != null) {
				leader.setIsInvul(val);
				leader.setIsImobilised(val);
			}
			if(assist != null) {
				assist.setIsInvul(val);
				assist.setIsImobilised(val);
			}
		}

		public void sendMessage(String msg) {
			if(leader != null){
				leader.sendMessage(msg);
			}
			if(assist != null) {
				assist.sendMessage(msg);
			}
		}

		public void removeEffects() {
			if(leader != null) {
				leader.stopAllEffects();
			}
			if(assist != null) {
				assist.stopAllEffects();
			}
		}

		public void increasedefeats() {
			if(leader != null) 
				leader.increaseArenaDefeats();
			if(assist != null)
				assist.increaseArenaDefeats();
		}
	}
	
	private class EvtArenaTask implements Runnable {

		private final Pair pairOne;
		private final Pair pairTwo;
		private final int pOneX, pOneY, pOneZ, pTwoX, pTwoY, pTwoZ;
		private Arena arena;

		public EvtArenaTask(List<Pair> opponents) {
			pairOne = opponents.get(0);
			pairTwo = opponents.get(1);
			L2PcInstance leader = pairOne.getLeader();
			pOneX = leader.getX();
			pOneY = leader.getY();
			pOneZ = leader.getZ();
			leader = pairTwo.getLeader();
			pTwoX = leader.getX();
			pTwoY = leader.getY();
			pTwoZ = leader.getZ();
		}
		
		@Override
		public void run() {
			free--;
			portPairsToArena();
			pairOne.sendMessage("The battle starts in 20 seconds");
			pairTwo.sendMessage("The battle starts in 20 seconds");
			try {
				Thread.sleep(Config.ARENA_WAIT_INTERVAL);
			} catch (InterruptedException e1) {
				e1.printStackTrace();
			}
			pairOne.sendMessage("The battle starts now");
			pairTwo.sendMessage("The battle starts now");
			pairOne.setImobilised(false);
			pairTwo.setImobilised(false);
			
			while(check()) {
				// check players status each  seconds
				try {
					Thread.sleep(Config.ARENA_CHECK_INTERVAL);
				} catch (InterruptedException e) {
					if(Config.ENABLE_ALL_EXCEPTIONS)
						e.printStackTrace();
					break;
				}
			}
			finishDuel();
			free++;
		}
		
		private void finishDuel() {
			fights.remove(arena.id);
			rewardWinner();
			pairOne.revive();
			pairTwo.revive();
			pairOne.teleportTo(pOneX, pOneY, pOneZ);
			pairTwo.teleportTo(pTwoX, pTwoY, pTwoZ);
			pairOne.setInEvent(false);
			pairTwo.setInEvent(false);
			arena.setFree(true);
		}
		
		private void rewardWinner() {
			if(pairOne.isAlive() && !pairTwo.isAlive()){
				pairOne.rewards();
				pairTwo.increasedefeats();
			} else if(pairTwo.isAlive() && !pairOne.isAlive()){
				pairTwo.rewards();
				pairOne.increasedefeats();
			}
		}

		private boolean check() {
			return (pairOne.isAlive() && pairTwo.isAlive());
		}
		
		private void portPairsToArena() {
			for(Arena arena : arenas) {
				if(arena.isFree) {
					this.arena = arena;
					arena.setFree(false);
					pairOne.teleportTo(arena.x - 300, arena.y, arena.z);
					pairTwo.teleportTo(arena.x + 300, arena.y, arena.z);
					pairOne.removeEffects();
					pairTwo.removeEffects();
					pairOne.setImobilised(true);
					pairTwo.setImobilised(true);
					pairOne.setInEvent(true);
					pairTwo.setInEvent(true);
					fights.put(this.arena.id, pairOne.getLeader().getName() +" vs "+ pairTwo.getLeader().getName());
					break;
				}
			}
		}
	}
	
	private class Arena {
		protected int x, y, z;
		protected boolean isFree = true;
		private int id;
		
		public Arena(int id, int x, int y, int z) {
			this.id = id;
			this.x = x;
			this.y = y;
			this.z = z;
		}
		
		public void setFree(boolean val) {
			isFree = val;
		}
		
		public void addSpectator(L2PcInstance spec) {
			spec.enterObserverMode(x, y, z);
		}
	}

	private static class SingletonHolder {
		protected static final EvtArenaManager INSTANCE = new EvtArenaManager();
	}
}