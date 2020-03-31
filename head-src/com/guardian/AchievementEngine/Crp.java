package com.guardian.AchievementEngine;

import com.guardian.gameserver.model.actor.instance.L2PcInstance;
import com.guardian.AchievementEngine.Condition;

public class Crp extends Condition
{
  public Crp(Object value)
  {
    super(value);
    setName("Clan Reputation");
  }

  @Override
public boolean meetConditionRequirements(L2PcInstance player)
  {
    if (getValue() == null) {
      return false;
    }
    if (player.getClan() != null)
    {
      int val = Integer.parseInt(getValue().toString());

      if (player.getClan().getReputationScore() >= val)
        return true;
    }
    return false;
  }
}