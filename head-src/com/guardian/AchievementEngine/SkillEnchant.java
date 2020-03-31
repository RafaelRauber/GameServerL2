package com.guardian.AchievementEngine;

import com.guardian.gameserver.model.L2Skill;
import com.guardian.gameserver.model.actor.instance.L2PcInstance;
import com.guardian.AchievementEngine.Condition;

public class SkillEnchant extends Condition
{
  public SkillEnchant(Object value)
  {
    super(value);
    setName("Skill Enchant");
  }

  @Override
public boolean meetConditionRequirements(L2PcInstance player)
  {
    if (getValue() == null) {
      return false;
    }
    int val = Integer.parseInt(getValue().toString());

    for (L2Skill s : player.getAllSkills())
    {
      String lvl = String.valueOf(s.getLevel());
      if (lvl.length() <= 2)
        continue;
      int sklvl = Integer.parseInt(lvl.substring(1));
      if (sklvl >= val) {
        return true;
      }
    }

    return false;
  }
}