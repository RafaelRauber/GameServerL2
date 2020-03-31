package com.guardian.AchievementEngine;

import com.guardian.gameserver.model.actor.instance.L2PcInstance;
import com.guardian.AchievementEngine.Condition;


  public class ClanLeader extends Condition
   {
  public ClanLeader(Object value)
    {
    super(value);
    setName("Be Clan Leader");
   }

  @Override
  public boolean meetConditionRequirements(L2PcInstance player)
  {
    if (getValue() == null) {
      return false;
    }

    return (player.getClan() != null) && 
      (player.isClanLeader());
    }
  }