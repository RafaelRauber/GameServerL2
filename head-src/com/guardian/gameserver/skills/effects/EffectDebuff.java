package com.guardian.gameserver.skills.effects;

import com.guardian.gameserver.model.L2Effect;
import com.guardian.gameserver.skills.Env;

public class EffectDebuff extends L2Effect
{
	
	public EffectDebuff(final Env env, final EffectTemplate template)
	{
		super(env, template);
	}
	
	@Override
	public EffectType getEffectType()
	{
		return EffectType.DEBUFF;
	}
	
	@Override
	public boolean onActionTime()
	{
		// stop effect
		return false;
	}
}
