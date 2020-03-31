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

import com.guardian.gameserver.Shutdown;
import com.guardian.gameserver.taskmanager.Task;
import com.guardian.gameserver.taskmanager.TaskManager.ExecutedTask;

/**
 * @author Layane
 */
public class TaskShutdown extends Task
{
	private static final Logger LOGGER = Logger.getLogger(TaskShutdown.class);
	public static final String NAME = "shutdown";
	
	/*
	 * (non-Javadoc)
	 * @see com.guardian.gameserver.taskmanager.Task#getName()
	 */
	@Override
	public String getName()
	{
		return NAME;
	}
	
	/*
	 * (non-Javadoc)
	 * @see com.guardian.gameserver.taskmanager.Task#onTimeElapsed(com.guardian.gameserver.taskmanager.TaskManager.ExecutedTask)
	 */
	@Override
	public void onTimeElapsed(final ExecutedTask task)
	{
		LOGGER.info("[GlobalTask] Server Shutdown launched.");
		
		final Shutdown handler = new Shutdown(Integer.valueOf(task.getParams()[2]), false, true, false);
		handler.start();
	}
}