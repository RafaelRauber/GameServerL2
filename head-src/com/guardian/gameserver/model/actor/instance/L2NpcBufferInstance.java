/*
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2, or (at your option)
 * any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA
 * 02111-1307, USA.
 *
 * http://www.gnu.org/copyleft/gpl.html
 */
package com.guardian.gameserver.model.actor.instance;

import java.util.StringTokenizer;

import com.guardian.gameserver.ai.CtrlIntention;
import com.guardian.gameserver.datatables.SkillTable;
import com.guardian.gameserver.network.SystemMessageId;
import com.guardian.gameserver.network.serverpackets.ActionFailed;
import com.guardian.gameserver.network.serverpackets.MagicSkillUser;
import com.guardian.gameserver.network.serverpackets.MoveToPawn;
import com.guardian.gameserver.network.serverpackets.MyTargetSelected;
import com.guardian.gameserver.network.serverpackets.NpcHtmlMessage;
import com.guardian.gameserver.network.serverpackets.SocialAction;
import com.guardian.gameserver.network.serverpackets.SystemMessage;
import com.guardian.gameserver.network.serverpackets.ValidateLocation;
import com.guardian.gameserver.templates.L2NpcTemplate;
import com.guardian.util.random.Rnd;

public final class L2NpcBufferInstance extends L2NpcInstance
{
 public L2NpcBufferInstance(int objectId, L2NpcTemplate template)
 {
 super(objectId, template);
 }

 @Override
 public void onAction(L2PcInstance player)
 {
 // Check if the L2PcInstance already target the L2Npc
 if (player.getTarget() != this)
 {
 // Set the target of the L2PcInstance player
 player.setTarget(this);

 // Send MyTargetSelected to the L2PcInstance player
 player.sendPacket(new MyTargetSelected(getObjectId(), 0));

 player.sendPacket(new ValidateLocation(this));
 }
 else
 {
 // Calculate the distance between the L2PcInstance and the L2Npc
 if (canInteract(player))
 {
 SocialAction sa = new SocialAction(this.getObjectId(), (int) Rnd.get());
 broadcastPacket(sa);

 // Rotate the player to face the instance
 player.sendPacket(new MoveToPawn(player, this, 150));

 showMessageWindow(player);

 // Send ActionFailed to the player in order to avoid he stucks
 player.sendPacket(new ActionFailed());
 }
 else
 {
 player.getAI().setIntention(CtrlIntention.AI_INTENTION_INTERACT, this);
 // Send ActionFailed to the player in order to avoid he stucks
 player.sendPacket(new ActionFailed());
 }
 }
 }

 @Override
 public void onBypassFeedback(L2PcInstance player, String command)
 {
 StringTokenizer st = new StringTokenizer(command, " ");
 String actualCommand = st.nextToken();

 int buffId = 0;
 int buffLevel = 1;
 int buffPrice = 0;
 int nextWindow = 0;

 if (st.countTokens() == 4)
 {
 buffId = Integer.valueOf(st.nextToken());
 buffLevel = Integer.valueOf(st.nextToken());
 buffPrice = Integer.valueOf(st.nextToken());
 nextWindow = Integer.valueOf(st.nextToken());
 }
 else if (st.countTokens() == 3)
 {
 buffId = Integer.valueOf(st.nextToken());
 buffLevel = Integer.valueOf(st.nextToken());
 nextWindow = Integer.valueOf(st.nextToken());
 }
 else if (st.countTokens() == 2)
 {
 buffId = Integer.valueOf(st.nextToken());
 nextWindow = Integer.valueOf(st.nextToken());
 }
 else if (st.countTokens() == 1)
 nextWindow = Integer.valueOf(st.nextToken());

 if (actualCommand.equalsIgnoreCase("chat"))
 showChatWindow(player, nextWindow);
 else if (actualCommand.equalsIgnoreCase("vipbuff"))
 {
 if (!player.isDonator())
 {
 player.sendMessage("You must be vip to get this buff.");
 showChatWindow(player, nextWindow);
 return;
 }

 if (buffId != 0 && player.reduceAdena("vipbuff", buffPrice, player.getLastFolkNPC(), true))
 {
 player.broadcastPacket(new MagicSkillUser(this, player, buffId, buffLevel, 5, 0));
 player.sendPacket(new SystemMessage(SystemMessageId.YOU_FEEL_S1_EFFECT).addSkillName(buffId, buffLevel));
 SkillTable.getInstance().getInfo(buffId, buffLevel).getEffects(this, player);
 showChatWindow(player, nextWindow);
 }
 }
 else if (actualCommand.equalsIgnoreCase("buff") || actualCommand.equalsIgnoreCase("vipbuff"))
 {
 if (buffId != 0 && player.reduceAdena("buff", buffPrice, player.getLastFolkNPC(), true))
 {
 player.broadcastPacket(new MagicSkillUser(this, player, buffId, buffLevel, 5, 0));
 player.sendPacket(new SystemMessage(SystemMessageId.YOU_FEEL_S1_EFFECT).addSkillName(buffId, buffLevel));
 SkillTable.getInstance().getInfo(buffId, buffLevel).getEffects(this, player);
 showChatWindow(player, nextWindow);
 }
 }
 else if (actualCommand.equalsIgnoreCase("restore"))
 {
 player.broadcastPacket(new MagicSkillUser(this, player, 1218, 33, 100, 0));

 player.setCurrentCp(player.getMaxCp());
 player.setCurrentHp(player.getMaxHp());
 player.setCurrentCp(player.getMaxCp());

 showChatWindow(player, nextWindow);
 }
 else if (actualCommand.equalsIgnoreCase("cancel"))
 {
 player.broadcastPacket(new MagicSkillUser(this, player, 1056, 12, 100, 0));

 player.stopAllEffects();

 showChatWindow(player, nextWindow);
 }
 else if (actualCommand.equalsIgnoreCase("fighter"))
 {
 String buffs[] = "1204,2;1068,3;1040,3;1035,4;1036,2;1045,6;1086,2;1077,3;1240,3;1242,3;264,1;267,1;268,1;269,1;304,1;349,1;364,1;271,1;274,1;275,1;1363,1;1391,3;4699,1;4703,1;".split(";");
 for (String buffInfo : buffs)
 {
 buffInfo.replace(" ", "");
 buffId = Integer.parseInt(buffInfo.split(",")[0]);
 buffLevel = Integer.parseInt(buffInfo.split(",")[1]);
 SkillTable.getInstance().getInfo(buffId, buffLevel).getEffects(player, player);
 }

 showChatWindow(player, nextWindow);
 }
 else if (actualCommand.equalsIgnoreCase("mage"))
 {
 String buffs[] = "1204,2;1040,3;1035,4;1045,6;1048,6;1036,2;1303,2;1085,3;1059,3;1078,6;1062,2;1397,3;264,1;267,1;268,1;304,1;349,1;364,1;273,1;276,1;365,1;1413,1;1391,3;4703,1;".split(";");
 for (String buffInfo : buffs)
 {
 buffInfo.replace(" ", "");
 buffId = Integer.parseInt(buffInfo.split(",")[0]);
 buffLevel = Integer.parseInt(buffInfo.split(",")[1]);
 SkillTable.getInstance().getInfo(buffId, buffLevel).getEffects(player, player);
 }

 showChatWindow(player, nextWindow);
 }
 else
 super.onBypassFeedback(player, command);
 }

 public void showMessageWindow(L2PcInstance player)
 {
 String filename = "data/html/buffer/" + getNpcId() + ".htm";

 NpcHtmlMessage html = new NpcHtmlMessage(getObjectId());
 html.setFile(filename);
 html.replace("%objectId%", String.valueOf(getObjectId()));
 html.replace("%npcname%", getName());
 player.sendPacket(html);
 }

 @Override
 public String getHtmlPath(int npcId, int val)
 {
 String pom = "";
 if (val == 0)
 pom = "" + npcId;
 else
 pom = npcId + "-" + val;

 return "data/html/buffer/" + pom + ".htm";
 }
}