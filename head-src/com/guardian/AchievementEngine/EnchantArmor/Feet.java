package com.guardian.AchievementEngine.EnchantArmor;
 
import com.guardian.gameserver.model.actor.instance.L2ItemInstance;
import com.guardian.gameserver.model.actor.instance.L2PcInstance;
import com.guardian.AchievementEngine.Condition;
import com.guardian.gameserver.model.Inventory;
 
 /**
  * @author Matim,Wallister
  * @version v1
  */
 public class Feet extends Condition
 {
         public Feet(Object value)
         {
                 super(value);
                 setName("Boots");
         }
 
         @Override
         public boolean meetConditionRequirements(L2PcInstance player)
         {
                 if (getValue() == null)
                 {
                         return false;
                 }
                 
                 int val = Integer.parseInt(getValue().toString());
                 
                 L2ItemInstance armor = player.getInventory().getPaperdollItem(Inventory.PAPERDOLL_FEET);
                 
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