/* L2Guardian - MrFreedomFights 
 * 
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2, or (at your option)
 * any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA
 * 02111-1307, USA.
 *
 * http://www.gnu.org/copyleft/gpl.html
 */
package com.guardian.gameserver.taskmanager.tasks;

import org.apache.log4j.Logger;

import com.guardian.Config;
import com.guardian.gameserver.model.entity.olympiad.Olympiad;
import com.guardian.gameserver.taskmanager.Task;
import com.guardian.gameserver.taskmanager.TaskManager;
import com.guardian.gameserver.taskmanager.TaskManager.ExecutedTask;
import com.guardian.gameserver.taskmanager.TaskTypes;

/**
 * Updates all data of Olympiad nobles in db
 * @author godson
 */
public class TaskOlympiadSave extends Task
{
	private static final Logger LOGGER = Logger.getLogger(TaskOlympiadSave.class);
	public static final String NAME = "olympiadsave";
	
	@Override
	public String getName()
	{
		return NAME;
	}
	
	@Override
	public void onTimeElapsed(final ExecutedTask task)
	{
		try
		{
			if (Olympiad.getInstance().inCompPeriod())
			{
				Olympiad.getInstance().saveOlympiadStatus();
				LOGGER.info("[GlobalTask] Olympiad System save launched.");
			}
		}
		catch (final Exception e)
		{
			if (Config.ENABLE_ALL_EXCEPTIONS)
				e.printStackTrace();
			
			LOGGER.warn("Olympiad System: Failed to save Olympiad configuration: " + e);
		}
	}
	
	@Override
	public void initializate()
	{
		super.initializate();
		TaskManager.addUniqueTask(NAME, TaskTypes.TYPE_FIXED_SHEDULED, "900000", "1800000", "");
	}
	
}