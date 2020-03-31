package com.guardian.AchievementEngine;
 
import com.guardian.gameserver.model.actor.instance.L2PcInstance;
import com.guardian.AchievementEngine.Condition;

public class Marry extends Condition
{
  public Marry(Object value)
  {
    super(value);
    setName("Married");
  }

  @Override
public boolean meetConditionRequirements(L2PcInstance player)
  {
    if (getValue() == null) {
      return false;
    }

    return player.isMarried();
  }
}