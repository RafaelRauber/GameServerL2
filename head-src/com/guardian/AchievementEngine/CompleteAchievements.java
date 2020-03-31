package com.guardian.AchievementEngine;

import java.util.List;
import com.guardian.gameserver.model.actor.instance.L2PcInstance;
import com.guardian.AchievementEngine.Condition;

@SuppressWarnings("unused")
public class CompleteAchievements extends Condition
{
  public CompleteAchievements(Object value)
  {
    super(value);
    setName("Complete Achievements");
  }

  @Override
public boolean meetConditionRequirements(L2PcInstance player)
  {
    if (getValue() == null) {
      return false;
    }
    int val = Integer.parseInt(getValue().toString());

    return player.getCompletedAchievements().size() >= val;
  }
}