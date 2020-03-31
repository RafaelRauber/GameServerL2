 package com.guardian.AchievementEngine;
 
 import com.guardian.gameserver.model.actor.instance.L2PcInstance;
 import com.guardian.AchievementEngine.Condition;

public class Mageclass extends Condition
{
  public Mageclass(Object value)
  {
    super(value);
    setName("Mage Class");
  }

  @Override
public boolean meetConditionRequirements(L2PcInstance player)
  {
    if (getValue() == null) {
      return false;
    }

    return player.isMageClass();
  }
}