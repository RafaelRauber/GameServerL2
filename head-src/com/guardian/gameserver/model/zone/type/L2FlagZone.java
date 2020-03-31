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
 * this program. If not, see <[url="http://www.gnu.org/licenses/>."]http://www.gnu.org/licenses/>.[/url]
 */
package com.guardian.gameserver.model.zone.type;
 
import java.util.concurrent.Future;
 
import com.guardian.gameserver.datatables.SkillTable;
import com.guardian.gameserver.model.L2Character;
import com.guardian.gameserver.model.L2Skill;
import com.guardian.gameserver.model.actor.instance.L2MonsterInstance;
import com.guardian.gameserver.model.actor.instance.L2PcInstance;
import com.guardian.gameserver.model.actor.instance.L2PlayableInstance;
import com.guardian.gameserver.model.zone.L2ZoneType;
import com.guardian.gameserver.thread.ThreadPoolManager;
import com.guardian.gameserver.network.SystemMessageId;
import com.guardian.gameserver.network.serverpackets.SystemMessage;
import com.guardian.util.random.Rnd;
 
/**
 * @author Strato
 * @author Elfocrash (for the correction)
 */
public class L2FlagZone extends L2ZoneType {
private int _skillId;
private int _chance;
private int _initialDelay;
private int _skillLvl;
private int _reuse;
private boolean _enabled;
private String _target;
private Future<?> _task;
 
public L2FlagZone(int id) {
super(id);
_skillId = 1323;
_skillLvl = 1;
_chance = 100;
_initialDelay = 0;
_reuse = 30000;
_enabled = false;
_target = "pc";
}
 
@Override
public void setParameter(String name, String value) {
if (name.equals("skillId")) {
_skillId = Integer.parseInt(value);
} else if (name.equals("skillLvl")) {
_skillLvl = Integer.parseInt(value);
} else if (name.equals("chance")) {
_chance = Integer.parseInt(value);
} else if (name.equals("initialDelay")) {
_initialDelay = Integer.parseInt(value);
} else if (name.equals("default_enabled")) {
_enabled = Boolean.parseBoolean(value);
} else if (name.equals("target")) {
_target = String.valueOf(value);
} else if (name.equals("reuse")) {
_reuse = Integer.parseInt(value);
} else {
super.setParameter(name, value);
}
}
 
@Override
protected void onEnter(L2Character character) {
if (character instanceof L2PcInstance) {
// Set pvp flag
((L2PcInstance) character).stopPvPFlag();
((L2PcInstance) character).setPvpFlag(1);
((L2PcInstance) character).sendPacket(new SystemMessage(SystemMessageId.ENTERED_COMBAT_ZONE));
((L2PcInstance) character).broadcastUserInfo();
if ((character instanceof L2PlayableInstance && _target.equalsIgnoreCase("pc")
|| character instanceof L2PcInstance && _target.equalsIgnoreCase("pc_only")
|| character instanceof L2MonsterInstance && _target.equalsIgnoreCase("npc")) && _task == null) {
_task = ThreadPoolManager.getInstance().scheduleGeneralAtFixedRate(new ApplySkill(/* this */),
_initialDelay, _reuse);
}
}
}
 
@Override
protected void onExit(L2Character character) {
if (character instanceof L2PcInstance) {
((L2PcInstance) character).setPvpFlag(0);
((L2PcInstance) character).updatePvPStatus();
((L2PcInstance) character).sendPacket(new SystemMessage(SystemMessageId.LEFT_COMBAT_ZONE));
((L2PcInstance) character).broadcastUserInfo();
}
if (_characterList.isEmpty() && _task != null) {
_task.cancel(true);
_task = null;
}
}
 
public L2Skill getSkill() {
return SkillTable.getInstance().getInfo(_skillId, _skillLvl);
}
 
public String getTargetType() {
return _target;
}
 
public boolean isEnabled() {
return _enabled;
}
 
public int getChance() {
return _chance;
}
 
public void setZoneEnabled(boolean val) {
_enabled = val;
}
 
class ApplySkill implements Runnable {
@Override
public void run() {
if (isEnabled()) {
for (L2Character temp : _characterList.values()) {
if (temp != null && !temp.isDead()) {
if ((temp instanceof L2PlayableInstance && getTargetType().equalsIgnoreCase("pc")
|| temp instanceof L2PcInstance && getTargetType().equalsIgnoreCase("pc_only")
|| temp instanceof L2MonsterInstance && getTargetType().equalsIgnoreCase("npc"))
&& Rnd.get(100) < getChance()) {
L2Skill skill = null;
if ((skill = getSkill()) == null) {
System.out.println("ATTENTION: error on zone with id " + getId());
System.out
.println("Skill " + _skillId + "," + _skillLvl + " not present between skills");
} else
skill.getEffects(temp, temp);
}
}
}
}
}
}
 
@Override
public void onDieInside(L2Character character) {
 
}
 
public static void waitSecs(int i) {
try {
Thread.sleep(i * 1000);
} catch (InterruptedException ie) {
ie.printStackTrace();
}
}
 
@Override
public void onReviveInside(L2Character character) {
onEnter(character);
}
}