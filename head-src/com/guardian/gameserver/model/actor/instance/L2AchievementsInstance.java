package com.guardian.gameserver.model.actor.instance;
 
import java.util.StringTokenizer;
 
import javolution.text.TextBuilder;
 
import com.guardian.gameserver.model.L2World;
import com.guardian.gameserver.model.entity.Announcements;
import com.guardian.AchievementEngine.AchievementsManager;
import com.guardian.AchievementEngine.Achievement;
import com.guardian.AchievementEngine.Condition;
import com.guardian.gameserver.model.Inventory;
import com.guardian.gameserver.network.serverpackets.NpcHtmlMessage;
import com.guardian.gameserver.templates.L2NpcTemplate;
 
 /**
  * @author Matim
  * @version 1.0
  */
 @SuppressWarnings("unused")
 public class L2AchievementsInstance extends L2FolkInstance
 {
         public L2AchievementsInstance(int objectId, L2NpcTemplate template)
         {
                 super(objectId, template);
         }
         
         @Override
         public void onBypassFeedback(L2PcInstance player, String command)
         {
                 if (player == null || player.getLastFolkNPC() == null || player.getLastFolkNPC().getObjectId() != this.getObjectId())
                 {
                         return;
                 }
                 if (command.startsWith("showMyAchievements"))
                 {                      
                         player.getAchievemntData();
                         showMyAchievements(player);
                 }
                 else if (command.startsWith("achievementInfo"))
                 {                      
                         StringTokenizer st = new StringTokenizer(command, " ");
                        st.nextToken();
                         int id = Integer.parseInt(st.nextToken());
                         
                         showAchievementInfo(id, player);
                }
                 else if (command.startsWith("topList"))
                 {                      
                         showTopListWindow(player);
                 }
                 else if (command.startsWith("showMainWindow"))
                 {
                         showChatWindow(player, 0);
                 }
                 else if (command.startsWith("getReward"))
                 {
                         StringTokenizer st = new StringTokenizer(command, " ");
                         st.nextToken();
                         int id = Integer.parseInt(st.nextToken());
                         AchievementsManager.getInstance().rewardForAchievement(id, player);
                         player.saveAchievementData(id);
                         showMyAchievements(player);
   
                 }
                 else if (command.startsWith ("showMyStats"))
                 {
                         showMyStatsWindow(player);
                 }
                 else if (command.startsWith("showHelpWindow"))
                 {
                         showHelpWindow(player);
                 }
         }
 
         @Override
         public void showChatWindow(L2PcInstance player, int val)
         {
                 TextBuilder tb = new TextBuilder();
                 tb.append("<html><title>Evento Achievements</title><body><center><br>");
                 tb.append("<img src=\"l2font-e.replay_logo-e\" width=250 height=80><br1><center><img src=\"L2UI.SquareGray\" width=300 height=1></center><table bgcolor=000000 width=319><tr><td><center><font color=\"LEVEL\">Ola <font color=\"LEVEL\">" + player.getName() + "</font></center></td></font></tr></table><center><img src=\"L2UI.SquareGray\" width=300 height=1></center>");
                 tb.append("<br><font color=\"LEVEL\">Esta procurando um desafio?</font>");
                 tb.append("<br><img src=\"l2ui.squaregray\" width=\"270\" height=\"1\"><br>");
                 tb.append("<button value=\"Meus Achievements\" action=\"bypass -h npc_%objectId%_showMyAchievements\" width=136 height=21 back=\"L2UI_ch3.BigButton3_over\" fore=\"L2UI_ch3.BigButton3\">");
                 tb.append("<button value=\"Statisticas\" action=\"bypass -h npc_%objectId%_showMyStats\" width=136 height=21 back=\"L2UI_ch3.BigButton3_over\" fore=\"L2UI_ch3.BigButton3\">");
                 tb.append("<button value=\"Ajuda\" action=\"bypass -h npc_%objectId%_showHelpWindow\" width=136 height=21 back=\"L2UI_ch3.BigButton3_over\" fore=\"L2UI_ch3.BigButton3\">");
                 tb.append("<br><img src=\"l2ui.squaregray\" width=\"270\" height=\"1\"><br>");
                 tb.append("<center><br><img src=l2ui.bbs_lineage2 height=16 width=80></center>");
                 
                 NpcHtmlMessage msg = new NpcHtmlMessage(this.getObjectId());
                 msg.setHtml(tb.toString());
                 msg.replace("%objectId%", String.valueOf(this.getObjectId()));
         
                 player.sendPacket(msg);
         }
         
         private void showMyAchievements(L2PcInstance player)
         {
                 TextBuilder tb = new TextBuilder();
                 tb.append("<html><title>Evento Achievements</title><body><br>");
                 
                 tb.append("<center><font color=\"LEVEL\">Meus Achievements</font>:</center><br>");
                 
                 if (AchievementsManager.getInstance().getAchievementList().isEmpty())
                 {
                        tb.append("Nao tem conquistas criadas ainda!");
                 }
                 else
                 {
                         int i = 0;
                         
                         tb.append("<table width=270 border=0 bgcolor=\"33FF33\">");
                         tb.append("<tr><td width=270 align=\"left\">Nome:</td><td width=60 align=\"right\">Info:</td><td width=200 align=\"center\">Status:</td></tr></table>");
                         tb.append("<br><img src=\"l2ui.squaregray\" width=\"270\" height=\"1\"><br>");
                         
                         for (Achievement a: AchievementsManager.getInstance().getAchievementList().values())
                         {
                                 tb.append(getTableColor(i));
                                 tb.append("<tr><td width=270 align=\"left\">" + a.getName() + "</td><td width=50 align=\"right\"><a action=\"bypass -h npc_%objectId%_achievementInfo "
                                 + a.getID() + "\">info</a></td><td width=200 align=\"center\">" + getStatusString(a.getID(), player) + "</td></tr></table>");
                                 i++;
                         }
                         
                         tb.append("<br><img src=\"l2ui.squaregray\" width=\"270\" height=\"1s\"><br>");
                         tb.append("<center><button value=\"Voltar\" action=\"bypass -h npc_%objectId%_showMainWindow\" width=134 height=21 back=\"L2UI_ch3.BigButton3_over\" fore=\"L2UI_ch3.BigButton3\"><center>");
                 }
                 
                 NpcHtmlMessage msg = new NpcHtmlMessage(this.getObjectId());
                 msg.setHtml(tb.toString());
                 msg.replace("%objectId%", String.valueOf(this.getObjectId()));
         
                 player.sendPacket(msg);
         }
         
         private void showAchievementInfo(int achievementID, L2PcInstance player)
         {
                 Achievement a = AchievementsManager.getInstance().getAchievementList().get(achievementID);
                 
                 TextBuilder tb = new TextBuilder();
                 tb.append("<html><title>Evento Achievements</title><body><br>");
                 
                 tb.append("<table width=270 border=0 bgcolor=\"33FF33\">");
                 tb.append("<tr><td width=270 align=\"center\">" + a.getName() + "</td></tr></table><br>");
                 tb.append("<center>Status: " + getStatusString(achievementID, player));
                 
                 if (a.meetAchievementRequirements(player) && !player.getCompletedAchievements().contains(achievementID))
                 {
                         tb.append("<button value=\"Receber Recompensa!\" action=\"bypass -h npc_%objectId%_getReward " + a.getID() + "\" width=136 height=21 back=\"L2UI_ch3.BigButton3_over\" fore=\"L2UI_ch3.BigButton3\">");
                 }
                 
                 tb.append("<br><img src=\"l2ui.squaregray\" width=\"270\" height=\"1s\"><br>");
                 
                 tb.append("<table width=270 border=0 bgcolor=\"33FF33\">");
                 tb.append("<tr><td width=270 align=\"center\">Descricao</td></tr></table><br>");
                 tb.append(a.getDescription());
                 tb.append("<br><img src=\"l2ui.squaregray\" width=\"270\" height=\"1s\"><br>");
                 
                 tb.append("<table width=270 border=0 bgcolor=\"33FF33\">");
                 tb.append("<tr><td width=270 align=\"left\">Condicao:</td><td width=100 align=\"right\">Valor:</td><td width=200 align=\"center\">Status:</td></tr></table>");
                 tb.append(getConditionsStatus(achievementID, player));
                 tb.append("<br><img src=\"l2ui.squaregray\" width=\"270\" height=\"1s\"><br>");
                 tb.append("<center><button value=\"Voltar\" action=\"bypass -h npc_%objectId%_showMyAchievements\" width=136 height=21 back=\"L2UI_ch3.BigButton3_over\" fore=\"L2UI_ch3.BigButton3\"><center>");
                 
                 NpcHtmlMessage msg = new NpcHtmlMessage(this.getObjectId());
                 msg.setHtml(tb.toString());
                 msg.replace("%objectId%", String.valueOf(this.getObjectId()));
         
                 player.sendPacket(msg);
         }
         
         private void showMyStatsWindow(L2PcInstance player)
         {
                 TextBuilder tb = new TextBuilder();
                 tb.append("<html><title>Evento Achievements</title><body><center><br>");
                 tb.append("Verifique seu <font color=\"LEVEL\">Achievements </font>Status:");
                 tb.append("<br><img src=\"l2ui.squaregray\" width=\"270\" height=\"1\"><br>");
                 
                 player.getAchievemntData();
                 int completedCount = player.getCompletedAchievements().size();
               
                 tb.append("Voce completou: " + completedCount + "/<font color=\"LEVEL\">" + AchievementsManager.getInstance().getAchievementList().size() + "</font>");
                 
                 tb.append("<br><img src=\"l2ui.squaregray\" width=\"270\" height=\"1s\"><br>");
                 tb.append("<center><button value=\"Voltar\" action=\"bypass -h npc_%objectId%_showMainWindow\" width=136 height=21 back=\"L2UI_ch3.BigButton3_over\" fore=\"L2UI_ch3.BigButton3\"><center>");
                 
                 NpcHtmlMessage msg = new NpcHtmlMessage(this.getObjectId());
                 msg.setHtml(tb.toString());
                 msg.replace("%objectId%", String.valueOf(this.getObjectId()));
         
                 player.sendPacket(msg);
         }
         
         private void showTopListWindow(L2PcInstance player )
         {
                 TextBuilder tb = new TextBuilder();
                 tb.append("<html><title>Evento Achievements</title><body><center><br>");
                 tb.append("Verifique seu <font color=\"LEVEL\">Achievements </font>Top List:");
                 tb.append("<br><img src=\"l2ui.squaregray\" width=\"270\" height=\"1\"><br>");
                 
                tb.append("List Player " + player.getCompletedAchievements() + " ");
                 
                 tb.append("<br><img src=\"l2ui.squaregray\" width=\"270\" height=\"1s\"><br>");
                 tb.append("<center><button value=\"Voltar\" action=\"bypass -h npc_%objectId%_showMainWindow\" width=136 height=21 back=\"L2UI_ch3.BigButton3_over\" fore=\"L2UI_ch3.BigButton3\"><center>");
                 
                 NpcHtmlMessage msg = new NpcHtmlMessage(this.getObjectId());
                 msg.setHtml(tb.toString());
                 msg.replace("%objectId%", String.valueOf(this.getObjectId()));
         
                 player.sendPacket(msg);
         }
         
         private void showHelpWindow(L2PcInstance player)
         {
                 TextBuilder tb = new TextBuilder();
                 tb.append("<html><title>Evento Achievements</title><body><center><br>");
                 tb.append("Achievements  <font color=\"LEVEL\">Ajuda </font>page:");
                 tb.append("<br><img src=\"l2ui.squaregray\" width=\"270\" height=\"1\"><br>");
                 
                 tb.append("<center>Voce pode verificar o status de seus Achievements, voce recebera a recompensa se todas as condicoes de achievements forem atendidas, se nao estiver completa verifique a condicao que se encontra usando o botao Informacao");
                 tb.append("<br><img src=\"l2ui.squaregray\" width=\"270\" height=\"1s\"><br>");
                 tb.append("<font color=\"FF0000\">Nao Completada</font> - Voce nao completou a tarefa.<br>");
                 tb.append("<font color=\"LEVEL\">Pegar Recompensa</font> - voce pode receber, click em info.<br>");
                 tb.append("<font color=\"5EA82E\">Completada</font> - Tarefa completa, receba a recompensa.<br></center>");
                 
                tb.append("Evento <font color=\"LEVEL\">Achievements</font>");
                tb.append("<br><img src=\"l2ui.squaregray\" width=\"270\" height=\"1s\"><br>");
                tb.append("<center><button value=\"Voltar\" action=\"bypass -h npc_%objectId%_showMainWindow\" width=136 height=21 back=\"L2UI_ch3.BigButton3_over\" fore=\"L2UI_ch3.BigButton3\"><center>");
                 
                 NpcHtmlMessage msg = new NpcHtmlMessage(this.getObjectId());
                 msg.setHtml(tb.toString());
                 msg.replace("%objectId%", String.valueOf(this.getObjectId()));
         
                 player.sendPacket(msg);
         }
         
         private String getStatusString(int achievementID, L2PcInstance player)
         {
                 if (player.getCompletedAchievements().contains(achievementID))
                 {
                         return "<font color=\"5EA82E\">Completada</font>";
                 }
                                if (AchievementsManager.getInstance().getAchievementList().get(achievementID).meetAchievementRequirements(player))
                                {
                                        return "<font color=\"LEVEL\">Pegar Recompensa</font>";
                                }
                                return "<font color=\"FF0000\">Nao Completada</font>";
         }
         
         private String getTableColor(int i)
         {
                 if (i % 2 == 0)
                         return "<table width=270 border=0 bgcolor=\"444444\">";
                                return "<table width=270 border=0>";
         }
         
         private String getConditionsStatus(int achievementID, L2PcInstance player)
         {
                 int i = 0;
                 String s = "</center>";
                 Achievement a = AchievementsManager.getInstance().getAchievementList().get(achievementID);
                 String completed = "<font color=\"5EA82E\">Completada</font></td></tr></table>";
                 String notcompleted = "<font color=\"FF0000\">Nao Completada</font></td></tr></table>";
                 
                 for (Condition c: a.getConditions())
                 {
                         s+= getTableColor(i);
                         s+= "<tr><td width=270 align=\"left\">" + c.getName() + "</td><td width=50 align=\"right\">" + c.getValue() + "</td><td width=200 align=\"center\">";
                         i++;
                         
                         if (c.meetConditionRequirements(player))
                                 s+= completed;
                         else
                                 s+= notcompleted;
                 }
                 return s;
         }
 }