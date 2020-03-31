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
  * this program. If not, see <http://www.gnu.org/licenses/>.
  */
 package com.guardian.AchievementEngine;
 
 import com.guardian.gameserver.model.actor.instance.L2PcInstance;
 
/**
  * @author Matim,Wallister
  * @version v1
  */
 public abstract class Condition
 {
         private Object _value;
         private String _name;
         
         public Condition(Object value)
         {
                 _value = value;
         }
         
         public abstract boolean meetConditionRequirements(L2PcInstance paramL2PcInstance);

         public Object getValue() {
           return _value;
         }

         public void setName(String s)
         {
           _name = s;
         }

         public String getName()
         {
           return _name;
         }
       }