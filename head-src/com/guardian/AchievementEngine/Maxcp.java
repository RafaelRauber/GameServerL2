package com.guardian.AchievementEngine;

import com.guardian.gameserver.model.actor.instance.L2PcInstance;
import com.guardian.AchievementEngine.Condition;

public class Maxcp extends Condition
{
  public Maxcp(Object value)
  {
    super(value);
    setName("Max CP");
  }

  @Override
public boolean meetConditionRequirements(L2PcInstance player)
  {
    if (getValue() == null) {
      return false;
    }
    int val = Integer.parseInt(getValue().toString());

    return player.getMaxCp() >= val;
  }
}