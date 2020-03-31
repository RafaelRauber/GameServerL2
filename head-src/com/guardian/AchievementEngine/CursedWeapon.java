package com.guardian.AchievementEngine;

import com.guardian.gameserver.model.actor.instance.L2PcInstance;
import com.guardian.AchievementEngine.Condition;

public class CursedWeapon extends Condition
{
  public CursedWeapon(Object value)
  {
    super(value);
    setName("Cursed Weapon");
  }

  @Override
public boolean meetConditionRequirements(L2PcInstance player)
  {
    if (getValue() == null) {
      return false;
    }

    return player.isCursedWeaponEquiped();
  }
}
