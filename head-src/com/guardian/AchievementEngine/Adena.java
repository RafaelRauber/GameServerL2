package com.guardian.AchievementEngine;
 
import com.guardian.gameserver.model.actor.instance.L2PcInstance;
import com.guardian.AchievementEngine.Condition;
import com.guardian.gameserver.model.PcInventory;

 @SuppressWarnings("unused")
 public class Adena extends Condition
 {
         public Adena(Object value)
         {
                 super(value);
                 setName("Adena");
         }
 
         @Override
         public boolean meetConditionRequirements(L2PcInstance player)
         {
                 if (getValue() == null)
                 {
                         return false;
                 }
                                long val = Integer.parseInt(getValue().toString());
                               
                                if (player.getInventory().getAdena() >= val)
                                        return true;
                 return false;
         }
 }