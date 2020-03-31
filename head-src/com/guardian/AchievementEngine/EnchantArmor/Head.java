package com.guardian.AchievementEngine.EnchantArmor;
 
import com.guardian.gameserver.model.actor.instance.L2ItemInstance;
import com.guardian.gameserver.model.actor.instance.L2PcInstance;
import com.guardian.AchievementEngine.Condition;
import com.guardian.gameserver.model.Inventory;
 
 /**
  * @author Matim,Wallister
  * @version v1
  */
 public class Head extends Condition
 {
         public Head(Object value)
         {
                 super(value);
                 setName("Helmets");
         }
 
         @Override
         public boolean meetConditionRequirements(L2PcInstance player)
         {
                 if (getValue() == null)
                 {
                         return false;
                 }
                 
                 int val = Integer.parseInt(getValue().toString());
                 
                 L2ItemInstance armor = player.getInventory().getPaperdollItem(Inventory.PAPERDOLL_HEAD);
                 
                 if (armor != null)
                 {
                         if (armor.getEnchantLevel() >= val)
                         {
                                 return true;
                         }
                 }
                 return false;
         }
 }