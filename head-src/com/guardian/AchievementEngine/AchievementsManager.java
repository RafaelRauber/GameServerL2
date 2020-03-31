 package com.guardian.AchievementEngine;
 
 import java.io.File;
 
 import java.sql.Connection;
 import java.sql.SQLException;
 import java.sql.Statement;
 import java.util.Map;
 import java.util.logging.Logger;
 
 import javax.xml.parsers.DocumentBuilderFactory;
 
 import javolution.util.FastList;
 import javolution.util.FastMap;
 
 import org.w3c.dom.Document;
 import org.w3c.dom.NamedNodeMap;
 import org.w3c.dom.Node;
 
 
 
 import com.guardian.Config;
 import com.guardian.util.database.L2DatabaseFactory;
 import com.guardian.gameserver.model.actor.instance.L2PcInstance;
 import com.guardian.AchievementEngine.Achievement;
 import com.guardian.AchievementEngine.Condition;
 import com.guardian.AchievementEngine.Adena;
 import com.guardian.AchievementEngine.ItemsCount;
 import com.guardian.AchievementEngine.Level;
 import com.guardian.AchievementEngine.WeaponEnchant;
 import com.guardian.AchievementEngine.EnchantArmor.Chest;
 import com.guardian.AchievementEngine.EnchantArmor.Feet;
 import com.guardian.AchievementEngine.EnchantArmor.Gloves;
 import com.guardian.AchievementEngine.EnchantArmor.Head;
 import com.guardian.AchievementEngine.EnchantArmor.Legs;
 
 /**
  * @author Matim,Wallister
  * @version v1
  */
 public class AchievementsManager
 {
         private Map<Integer, Achievement> _achievementList = new FastMap<Integer, Achievement>();
         
         
         private static Logger _log = Logger.getLogger(AchievementsManager.class.getName());
         
         public AchievementsManager()
         {
                 loadAchievements();
         }
         
         private void loadAchievements()
         {
                 DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
                 factory.setValidating(false);
                factory.setIgnoringComments(true);
                 
                 File file = new File(Config.DATAPACK_ROOT + "/config/achievements.xml");
                 
                 if (!file.exists())
                 {
                         _log.warning("[AchievementsEngine] Erro: arquivo achievements xml nao existe, cheque seus arquivos!");
                 }
                 try
                 {
                         Document doc = factory.newDocumentBuilder().parse(file);
                         
                         for (Node list = doc.getFirstChild(); list != null; list = list.getNextSibling())
                         {
                                        if("list".equalsIgnoreCase(list.getNodeName()))
                                 {
                                         for (Node achievement = list.getFirstChild(); achievement != null; achievement = achievement.getNextSibling())
                                        {
                                                                if("achievement".equalsIgnoreCase(achievement.getNodeName()))
                                                 {
                                                         int id = checkInt(achievement, "id");
                                                         
                                                         String name = String.valueOf(achievement.getAttributes().getNamedItem("name").getNodeValue());
                                                         String description = String.valueOf(achievement.getAttributes().getNamedItem("description").getNodeValue());
                                                         String reward = String.valueOf(achievement.getAttributes().getNamedItem("reward").getNodeValue());
                                                         boolean repeat = checkBoolean(achievement, "repeatable");
                                                         
                                                         FastList<Condition> conditions = conditionList(achievement.getAttributes());
                                                         
                                                         _achievementList.put(id, new Achievement(id, name, description, reward, repeat, conditions));
                                                         alterTable(id);
                                                 }
                                         }
                                 }
                         }
                         _log.info("[AchievementsEngine] Carregado: " + getAchievementList().size() + " achievements do arquivo xml!");
                 }
                 catch (Exception e)
                 {
                         _log.warning("[AchievementsEngine] Erro: " + e);
                         e.printStackTrace();
                 }
         }
         
         public void rewardForAchievement(int achievementID, L2PcInstance player)
         {
                 Achievement achievement = _achievementList.get(achievementID);
                 
                 for (int id: achievement.getRewardList().keySet())
                 player.addItem(achievement.getName(), id, achievement.getRewardList().get(id), player, true);
         }
         
         private boolean checkBoolean(Node d, String nodename)
         {
                 boolean b = false;
                 
                 try
                 {
                         b = Boolean.valueOf(d.getAttributes().getNamedItem(nodename).getNodeValue());
                 }
                 catch (Exception e)
                 {
                         
                 }
                 return b;
         }
         
         private int checkInt(Node d, String nodename)
         {
                 int i = 0;
                 
                 try
                 {
                         i = Integer.valueOf(d.getAttributes().getNamedItem(nodename).getNodeValue());
                 }
                 catch (Exception e)
                 {
                         
                 }
                 return i;
         }
         
         /**
          * Alter table, catch exception if already exist.
          * @param fieldID
          */
         private void alterTable(int fieldID)
         {
                 Connection con = null;
                 try
                 {
                         con = L2DatabaseFactory.getInstance().getConnection();
                         Statement statement = con.createStatement();
                         statement.executeUpdate("ALTER TABLE achievements ADD a" + fieldID + " INT DEFAULT 0");
                         statement.close();
                 }
                 catch (SQLException e)
                 {
 
                 }
                 finally
                 {
                         L2DatabaseFactory.close(con);
                }
         }
         
         public FastList<Condition> conditionList(NamedNodeMap attributesList)
         {
                 FastList<Condition> conditions = new FastList<Condition>();
                 
            for (int j = 0; j < attributesList.getLength(); j++)
             {
                 addToConditionList(attributesList.item(j).getNodeName(), attributesList.item(j).getNodeValue(), conditions);
             }
                 
                 return conditions;
         }
         
         public Map<Integer, Achievement> getAchievementList()
         {
                 return _achievementList;
         }
         
         public static AchievementsManager getInstance()
         {
                 return SingletonHolder._instance;
         }
                 
         private static class SingletonHolder
         {
                 protected static final AchievementsManager _instance = new AchievementsManager();
        }
         
         private void addToConditionList(String nodeName, Object value, FastList<Condition> conditions)
         {
              if (nodeName.equals("minLevel"))
                 conditions.add(new Level(value));
              
         else if (nodeName.equals("minPvPCount"))
                conditions.add(new Pvp(value));
              
         else if (nodeName.equals("minPkCount"))
                 conditions.add(new Pk(value));
              
         else if (nodeName.equals("minClanLevel"))
                 conditions.add(new ClanLevel(value));
              
         else if (nodeName.equals("mustBeHero"))
                 conditions.add(new Hero(value));
              
         else if (nodeName.equals("mustBeNoble"))
                 conditions.add(new Noble(value));
              
         else if (nodeName.equals("mustBeClanLeader"))
                 conditions.add(new ClanLeader(value));
              
         else if (nodeName.equals("minWeaponEnchant"))
                 conditions.add(new WeaponEnchant(value));
              
         else if (nodeName.equals("minKarmaCount"))
                 conditions.add(new Karma(value));
              
         else if (nodeName.equals("minAdenaCount"))
                 conditions.add(new Adena(value));
              
         else if (nodeName.equals("minClanMembersCount"))
                 conditions.add(new MinCMcount(value));

         else if (nodeName.equals("maxHP"))
                 conditions.add(new Maxhp(value));
              
         else if (nodeName.equals("maxMP"))
                 conditions.add(new Maxmp(value));
              
         else if (nodeName.equals("maxCP"))
                 conditions.add(new Maxcp(value));
              
         else if (nodeName.equals("mustBeMarried"))
                 conditions.add(new Marry(value));
              
         else if (nodeName.equals("itemAmmount"))
                 conditions.add(new ItemsCount(value));
              
         else if (nodeName.equals("crpAmmount"))
                  conditions.add(new Crp(value));
              
         else if (nodeName.equals("lordOfCastle"))
                  conditions.add(new Castle(value));
              
         else if (nodeName.equals("mustBeMageClass"))
                  conditions.add(new Mageclass(value));
              
         else if (nodeName.equals("mustBeVip"))
                  conditions.add(new Vip(value));
              
         else if (nodeName.equals("raidToKill"))
                  conditions.add(new RaidKill(value));
         
         else if (nodeName.equals("CompleteAchievements"))
                  conditions.add(new CompleteAchievements(value));
              
         else if (nodeName.equals("minSubclassCount"))
                  conditions.add(new Sub(value));
              
         else if (nodeName.equals("minSkillEnchant"))
                  conditions.add(new SkillEnchant(value));
              
         else if (nodeName.equals("minOnlineTime"))
                  conditions.add(new OnlineTime(value));
              
         else if (nodeName.equals("Cursedweapon"))
                  conditions.add(new CursedWeapon(value));
              
         else if (nodeName.equals("minHeadEnchant"))
                  conditions.add(new Head(value));
              
         else if (nodeName.equals("minChestEnchant"))
                  conditions.add(new Chest(value));
              
         else if (nodeName.equals("minFeetEnchant"))
                  conditions.add(new Feet(value));
              
         else if (nodeName.equals("minLegsEnchant"))
                  conditions.add(new Legs(value));
              
         else if (nodeName.equals("minGlovestEnchant"))
                  conditions.add(new Gloves(value));
            
              
             }
         }
 
 /**
  * @authorDesenvolvedor desse mod Matim && Wallister
  * @version v1
  *
  */