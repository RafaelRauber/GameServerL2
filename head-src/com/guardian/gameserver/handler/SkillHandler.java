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
package com.guardian.gameserver.handler;

import java.util.Map;
import java.util.TreeMap;

import org.apache.log4j.Logger;

import com.guardian.gameserver.GameServer;
import com.guardian.gameserver.handler.skillhandlers.BalanceLife;
import com.guardian.gameserver.handler.skillhandlers.BeastFeed;
import com.guardian.gameserver.handler.skillhandlers.Blow;
import com.guardian.gameserver.handler.skillhandlers.Charge;
import com.guardian.gameserver.handler.skillhandlers.ClanGate;
import com.guardian.gameserver.handler.skillhandlers.CombatPointHeal;
import com.guardian.gameserver.handler.skillhandlers.Continuous;
import com.guardian.gameserver.handler.skillhandlers.CpDam;
import com.guardian.gameserver.handler.skillhandlers.Craft;
import com.guardian.gameserver.handler.skillhandlers.DeluxeKey;
import com.guardian.gameserver.handler.skillhandlers.Disablers;
import com.guardian.gameserver.handler.skillhandlers.DrainSoul;
import com.guardian.gameserver.handler.skillhandlers.Fishing;
import com.guardian.gameserver.handler.skillhandlers.FishingSkill;
import com.guardian.gameserver.handler.skillhandlers.GetPlayer;
import com.guardian.gameserver.handler.skillhandlers.Harvest;
import com.guardian.gameserver.handler.skillhandlers.Heal;
import com.guardian.gameserver.handler.skillhandlers.ManaHeal;
import com.guardian.gameserver.handler.skillhandlers.Manadam;
import com.guardian.gameserver.handler.skillhandlers.Mdam;
import com.guardian.gameserver.handler.skillhandlers.Pdam;
import com.guardian.gameserver.handler.skillhandlers.Recall;
import com.guardian.gameserver.handler.skillhandlers.Resurrect;
import com.guardian.gameserver.handler.skillhandlers.SiegeFlag;
import com.guardian.gameserver.handler.skillhandlers.Sow;
import com.guardian.gameserver.handler.skillhandlers.Spoil;
import com.guardian.gameserver.handler.skillhandlers.StrSiegeAssault;
import com.guardian.gameserver.handler.skillhandlers.SummonFriend;
import com.guardian.gameserver.handler.skillhandlers.SummonTreasureKey;
import com.guardian.gameserver.handler.skillhandlers.Sweep;
import com.guardian.gameserver.handler.skillhandlers.TakeCastle;
import com.guardian.gameserver.handler.skillhandlers.Unlock;
import com.guardian.gameserver.handler.skillhandlers.ZakenPlayer;
import com.guardian.gameserver.handler.skillhandlers.ZakenSelf;
import com.guardian.gameserver.model.L2Skill;
import com.guardian.gameserver.model.L2Skill.SkillType;

/**
 * This class ...
 * @version $Revision: 1.1.4.4 $ $Date: 2005/04/03 15:55:06 $
 */
public class SkillHandler
{
	private static final Logger LOGGER = Logger.getLogger(GameServer.class);
	
	private static SkillHandler _instance;
	
	private final Map<L2Skill.SkillType, ISkillHandler> _datatable;
	
	public static SkillHandler getInstance()
	{
		if (_instance == null)
		{
			_instance = new SkillHandler();
		}
		
		return _instance;
	}
	
	private SkillHandler()
	{
		_datatable = new TreeMap<>();
		registerSkillHandler(new Blow());
		registerSkillHandler(new Pdam());
		registerSkillHandler(new Mdam());
		registerSkillHandler(new CpDam());
		registerSkillHandler(new Manadam());
		registerSkillHandler(new Heal());
		registerSkillHandler(new CombatPointHeal());
		registerSkillHandler(new ManaHeal());
		registerSkillHandler(new BalanceLife());
		registerSkillHandler(new Charge());
		registerSkillHandler(new ClanGate());
		registerSkillHandler(new Continuous());
		registerSkillHandler(new Resurrect());
		registerSkillHandler(new Spoil());
		registerSkillHandler(new Sweep());
		registerSkillHandler(new StrSiegeAssault());
		registerSkillHandler(new SummonFriend());
		registerSkillHandler(new SummonTreasureKey());
		registerSkillHandler(new Disablers());
		registerSkillHandler(new Recall());
		registerSkillHandler(new SiegeFlag());
		registerSkillHandler(new TakeCastle());
		registerSkillHandler(new Unlock());
		registerSkillHandler(new DrainSoul());
		registerSkillHandler(new Craft());
		registerSkillHandler(new Fishing());
		registerSkillHandler(new FishingSkill());
		registerSkillHandler(new BeastFeed());
		registerSkillHandler(new DeluxeKey());
		registerSkillHandler(new Sow());
		registerSkillHandler(new Harvest());
		registerSkillHandler(new GetPlayer());
		registerSkillHandler(new ZakenPlayer());
		registerSkillHandler(new ZakenSelf());
		LOGGER.info("SkillHandler: Loaded " + _datatable.size() + " handlers.");
		
	}
	
	public void registerSkillHandler(final ISkillHandler handler)
	{
		SkillType[] types = handler.getSkillIds();
		
		for (final SkillType t : types)
		{
			_datatable.put(t, handler);
		}
		types = null;
	}
	
	public ISkillHandler getSkillHandler(final SkillType skillType)
	{
		return _datatable.get(skillType);
	}
	
	/**
	 * @return
	 */
	public int size()
	{
		return _datatable.size();
	}
}