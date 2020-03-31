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
package com.guardian.gameserver.skills.l2skills;

import com.guardian.gameserver.model.L2Character;
import com.guardian.gameserver.model.L2Object;
import com.guardian.gameserver.model.L2Skill;
import com.guardian.gameserver.network.SystemMessageId;
import com.guardian.gameserver.network.serverpackets.ActionFailed;
import com.guardian.gameserver.network.serverpackets.SystemMessage;
import com.guardian.gameserver.templates.StatsSet;

public class L2SkillDefault extends L2Skill
{
	
	public L2SkillDefault(final StatsSet set)
	{
		super(set);
	}
	
	@Override
	public void useSkill(final L2Character caster, final L2Object[] targets)
	{
		caster.sendPacket(ActionFailed.STATIC_PACKET);
		final SystemMessage sm = new SystemMessage(SystemMessageId.S1_S2);
		sm.addString("Skill not implemented.  Skill ID: " + getId() + " " + getSkillType());
		caster.sendPacket(sm);
	}
	
}
