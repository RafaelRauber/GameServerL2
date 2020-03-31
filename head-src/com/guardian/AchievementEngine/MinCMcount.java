package com.guardian.AchievementEngine;

import com.guardian.gameserver.model.actor.instance.L2PcInstance;
import com.guardian.AchievementEngine.Condition;

 public class MinCMcount extends Condition
 {
  public MinCMcount(Object value)
  {
    super(value);
    setName("Clan Members Count");
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

      if (player.getClan().getMembersCount() >= val)
        return true;
    }
    return false;
  }
 }