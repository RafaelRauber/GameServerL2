 package com.guardian.AchievementEngine;

import java.util.Iterator;
import java.util.Map;
import com.guardian.gameserver.managers.RaidBossPointsManager;
import com.guardian.gameserver.model.actor.instance.L2PcInstance;
import com.guardian.AchievementEngine.Condition;

public class RaidKill extends Condition
{
  public RaidKill(Object value)
  {
    super(value);
    setName("Raid Kill");
  }

  @SuppressWarnings({
	"rawtypes",
	"static-access", "cast"
  })
  @Override
  public boolean meetConditionRequirements(L2PcInstance player)
  {
    if (getValue() == null) {
      return false;
    }
    int val = Integer.parseInt(getValue().toString());
    Map list = RaidBossPointsManager.getInstance().getList(player);
    Iterator i$;
    if (list != null)
    {
      for (i$ = list.keySet().iterator(); i$.hasNext(); ) { int bid = ((Integer)i$.next()).intValue();

        if (bid == val)
        {
          if (((Integer)RaidBossPointsManager.getInstance().getList(player).get(Integer.valueOf(bid))).intValue() > 0)
            return true;
        }
      }
    }
    return false;
  }
}