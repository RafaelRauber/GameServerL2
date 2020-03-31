/*
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any later
 * version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU General Public License for more
 * details.
 *
 * You should have received a copy of the GNU General Public License along with
 * this program. If not, see <http://www.gnu.org/licenses/>.
 */
package com.guardian.gameserver.handler.itemhandlers;

import com.guardian.Config;
import com.guardian.gameserver.handler.IItemHandler;
import com.guardian.gameserver.model.actor.instance.L2ItemInstance;
import com.guardian.gameserver.model.actor.instance.L2PcInstance;
import com.guardian.gameserver.model.actor.instance.L2PlayableInstance;
import com.guardian.gameserver.network.serverpackets.InventoryUpdate;
import com.guardian.gameserver.network.serverpackets.SocialAction;
import com.guardian.gameserver.network.serverpackets.StatusUpdate;
import com.guardian.gameserver.thread.ThreadPoolManager;

/**
 * @author ???????
 */
public  class ChangeSexItem implements IItemHandler
{

    private static final int ITEM_IDS[] =
    {
            Config.CHANGESEX_COIN_ID
    };

    @Override
	public void useItem(L2PlayableInstance playable, final L2ItemInstance item)
{
    	
        if (!(playable instanceof L2PcInstance))
          {
            return;
          }
          final L2PcInstance activeChar = (L2PcInstance)playable;
          if (activeChar.isInOlympiadMode()) 
          {
            activeChar.sendMessage("Este item nao pode ser usado nas Olimpiadas.");
          }
		  
          else
          {  
        if ( Config.ENABLE_CHANGESEX_COIN || (activeChar.getInventory().getItemByItemId(Config.CHANGESEX_COIN_ID) != null) && (activeChar.getInventory().getItemByItemId(Config.CHANGESEX_COIN_ID).getCount() >= Config.CHANGESEX_COIN_AMOUNT))
        {
          activeChar.getInventory().destroyItemByItemId("ChangeSex", Config.CHANGESEX_COIN_ID, Config.CHANGESEX_COIN_AMOUNT, activeChar, activeChar.getTarget());
          activeChar.broadcastPacket(new SocialAction(activeChar.getObjectId(), 16));
          activeChar.getAppearance().setSex(!activeChar.getAppearance().getSex());
          L2PcInstance.setSexDB(activeChar, 1);
          activeChar.sendMessage("Parabens! O Sexo de seu personagem foi alterado com sucesso.");
          activeChar.sendMessage("Voce precisa dar restart em seu Personagem.");
          activeChar.spawnMe(activeChar.getX(), activeChar.getY(), activeChar.getZ());
          activeChar.sendPacket(new InventoryUpdate());
          activeChar.sendPacket(new StatusUpdate(activeChar));
          activeChar.sendMessage("O Sexo foi alterado. Voce sera desconectado em 3 segundos!");
          activeChar.broadcastUserInfo();
          activeChar.decayMe();
          activeChar.spawnMe();
          ThreadPoolManager.getInstance().scheduleGeneral(new Runnable()
          {
              @Override
              public void run()
              {
            	  activeChar.logout(false);
              }
          }, 3000);
        }
          }
    return;
  
}

    @Override
    public int[] getItemIds()
    {
        return ITEM_IDS;
    }
}