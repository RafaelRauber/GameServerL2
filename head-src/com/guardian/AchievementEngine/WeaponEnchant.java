package com.guardian.AchievementEngine;
 
import com.guardian.gameserver.model.actor.instance.L2ItemInstance;
import com.guardian.gameserver.model.actor.instance.L2PcInstance;
import com.guardian.AchievementEngine.Condition;
import com.guardian.gameserver.model.Inventory;
 
 /**
  * @author Matim,Wallister
  * @version v1
  */
 public class WeaponEnchant extends Condition
 {
         public WeaponEnchant(Object value)
         {
                 super(value);
                 setName("Weapon Enchant");
         }
 
         @Override
         public boolean meetConditionRequirements(L2PcInstance player)
         {
                 if (getValue() == null)
                 {
                         return false;
                 }
                 
                 int val = Integer.parseInt(getValue().toString());
                 
                 L2ItemInstance weapon = player.getInventory().getPaperdollItem(Inventory.PAPERDOLL_RHAND);
                 
                 if (weapon != null)
                 {
                         if (weapon.getEnchantLevel() >= val)
                         {
                                 return true;
                         }
                 }
                 return false;
         }
 }