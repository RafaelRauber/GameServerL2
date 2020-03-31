package com.guardian.AchievementEngine;

import com.guardian.gameserver.model.actor.instance.L2PcInstance;
import com.guardian.AchievementEngine.Condition;

public class Noble extends Condition
{
  public Noble(Object value)
  {
    super(value);
    setName("Noble");
  }

  @Override
public boolean meetConditionRequirements(L2PcInstance player)
  {
    if (getValue() == null) {
      return false;
    }

    return player.isNoble();
  }
}