package com.guardian.AchievementEngine;

import java.util.Map;
import com.guardian.gameserver.model.actor.instance.L2PcInstance;
import com.guardian.AchievementEngine.Condition;

@SuppressWarnings("unused")
public class Sub extends Condition
{
  public Sub(Object value)
  {
    super(value);
    setName("Subclass Count");
  }

  @Override
public boolean meetConditionRequirements(L2PcInstance player)
  {
    if (getValue() == null) {
      return false;
    }
    int val = Integer.parseInt(getValue().toString());

    return player.getSubClasses().size() >= val;
  }
}