/*
 * L2Guardian - MrFreedomFights 
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
package com.guardian.gameserver;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Calendar;
import java.util.logging.LogManager;

import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

import com.guardian.Config;
import com.guardian.FService;
import com.guardian.L2Guardian;
import com.guardian.ServerType;
import com.guardian.crypt.nProtect;
import com.guardian.gameserver.ai.special.manager.AILoader;
import com.guardian.gameserver.cache.CrestCache;
import com.guardian.gameserver.cache.HtmCache;
import com.guardian.gameserver.communitybbs.Manager.ForumsBBSManager;
import com.guardian.gameserver.controllers.GameTimeController;
import com.guardian.gameserver.controllers.RecipeController;
import com.guardian.gameserver.controllers.TradeController;
import com.guardian.gameserver.datatables.AntiBotTable;
import com.guardian.gameserver.datatables.GmListTable;
import com.guardian.gameserver.datatables.HeroSkillTable;
import com.guardian.gameserver.datatables.NobleSkillTable;
import com.guardian.gameserver.datatables.OfflineTradeTable;
import com.guardian.gameserver.datatables.SkillTable;
import com.guardian.gameserver.datatables.csv.DoorTable;
import com.guardian.gameserver.datatables.csv.ExtractableItemsData;
import com.guardian.gameserver.datatables.csv.FishTable;
import com.guardian.gameserver.datatables.csv.HennaTable;
import com.guardian.gameserver.datatables.csv.MapRegionTable;
import com.guardian.gameserver.datatables.csv.NpcWalkerRoutesTable;
import com.guardian.gameserver.datatables.csv.RecipeTable;
import com.guardian.gameserver.datatables.csv.StaticObjects;
import com.guardian.gameserver.datatables.csv.SummonItemsData;
import com.guardian.gameserver.datatables.sql.AccessLevels;
import com.guardian.gameserver.datatables.sql.AdminCommandAccessRights;
import com.guardian.gameserver.datatables.sql.ArmorSetsTable;
import com.guardian.gameserver.datatables.sql.CharNameTable;
import com.guardian.gameserver.datatables.sql.CharTemplateTable;
import com.guardian.gameserver.datatables.sql.ClanTable;
import com.guardian.gameserver.datatables.sql.CustomArmorSetsTable;
import com.guardian.gameserver.datatables.sql.HelperBuffTable;
import com.guardian.gameserver.datatables.sql.HennaTreeTable;
import com.guardian.gameserver.datatables.sql.ItemTable;
import com.guardian.gameserver.datatables.sql.L2PetDataTable;
import com.guardian.gameserver.datatables.sql.LevelUpData;
import com.guardian.gameserver.datatables.sql.NpcTable;
import com.guardian.gameserver.datatables.sql.SkillSpellbookTable;
import com.guardian.gameserver.datatables.sql.SkillTreeTable;
import com.guardian.gameserver.datatables.sql.SpawnTable;
import com.guardian.gameserver.datatables.sql.TeleportLocationTable;
import com.guardian.gameserver.datatables.xml.AugmentationData;
import com.guardian.gameserver.datatables.xml.ExperienceData;
import com.guardian.gameserver.datatables.xml.ZoneData;
import com.guardian.gameserver.geo.GeoData;
import com.guardian.gameserver.geo.geoeditorcon.GeoEditorListener;
import com.guardian.gameserver.geo.pathfinding.PathFinding;
import com.guardian.gameserver.handler.AdminCommandHandler;
import com.guardian.gameserver.handler.AutoAnnouncementHandler;
import com.guardian.gameserver.handler.AutoChatHandler;
import com.guardian.gameserver.handler.ItemHandler;
import com.guardian.gameserver.handler.SkillHandler;
import com.guardian.gameserver.handler.UserCommandHandler;
import com.guardian.gameserver.handler.VoicedCommandHandler;
import com.guardian.gameserver.idfactory.IdFactory;
import com.guardian.gameserver.managers.AuctionManager;
import com.guardian.gameserver.managers.AutoSaveManager;
import com.guardian.gameserver.managers.AwayManager;
import com.guardian.gameserver.managers.BoatManager;
import com.guardian.gameserver.managers.CastleManager;
import com.guardian.gameserver.managers.CastleManorManager;
import com.guardian.gameserver.managers.ClanHallManager;
import com.guardian.gameserver.managers.ClassDamageManager;
import com.guardian.gameserver.managers.CoupleManager;
import com.guardian.gameserver.managers.CrownManager;
import com.guardian.gameserver.managers.CursedWeaponsManager;
import com.guardian.gameserver.managers.DayNightSpawnManager;
import com.guardian.gameserver.managers.DimensionalRiftManager;
import com.guardian.gameserver.managers.DuelManager;
import com.guardian.gameserver.managers.FortManager;
import com.guardian.gameserver.managers.FortSiegeManager;
import com.guardian.gameserver.managers.FourSepulchersManager;
import com.guardian.gameserver.managers.GrandBossManager;
import com.guardian.gameserver.managers.IrcManager;
import com.guardian.gameserver.managers.ItemsOnGroundManager;
import com.guardian.gameserver.managers.MercTicketManager;
import com.guardian.gameserver.managers.PetitionManager;
import com.guardian.gameserver.managers.QuestManager;
import com.guardian.gameserver.managers.RaidBossPointsManager;
import com.guardian.gameserver.managers.RaidBossSpawnManager;
import com.guardian.gameserver.managers.SiegeManager;
import com.guardian.gameserver.model.L2Manor;
import com.guardian.gameserver.model.L2World;
import com.guardian.gameserver.model.PartyMatchRoomList;
import com.guardian.gameserver.model.PartyMatchWaitingList;
import com.guardian.gameserver.model.entity.Announcements;
import com.guardian.gameserver.model.entity.Hero;
import com.guardian.gameserver.model.entity.MonsterRace;
import com.guardian.gameserver.model.entity.event.EvtArenaManager;
import com.guardian.gameserver.model.entity.event.manager.EventManager;
import com.guardian.gameserver.model.entity.olympiad.Olympiad;
import com.guardian.gameserver.model.entity.sevensigns.SevenSigns;
import com.guardian.gameserver.model.entity.sevensigns.SevenSignsFestival;
import com.guardian.gameserver.model.entity.siege.clanhalls.BanditStrongholdSiege;
import com.guardian.gameserver.model.entity.siege.clanhalls.DevastatedCastle;
import com.guardian.gameserver.model.entity.siege.clanhalls.FortressOfResistance;
import com.guardian.gameserver.model.multisell.L2Multisell;
import com.guardian.gameserver.model.spawn.AutoSpawn;
import com.guardian.gameserver.monumentstatuet.EventMonumentStatuet;
import com.guardian.gameserver.network.L2GameClient;
import com.guardian.gameserver.network.L2GamePacketHandler;
import com.guardian.gameserver.powerpak.PowerPak;
import com.guardian.gameserver.script.EventDroplist;
import com.guardian.gameserver.script.faenor.FaenorScriptEngine;
import com.guardian.gameserver.scripting.CompiledScriptCache;
import com.guardian.gameserver.scripting.L2ScriptEngineManager;
import com.guardian.gameserver.taskmanager.TaskManager;
import com.guardian.gameserver.thread.LoginServerThread;
import com.guardian.gameserver.thread.ThreadPoolManager;
import com.guardian.gameserver.thread.daemons.DeadlockDetector;
import com.guardian.gameserver.thread.daemons.ItemsAutoDestroy;
import com.guardian.gameserver.thread.daemons.PcPoint;
import com.guardian.gameserver.util.DynamicExtension;
import com.guardian.gameserver.util.sql.SQLQueue;
import com.guardian.netcore.NetcoreConfig;
import com.guardian.netcore.SelectorConfig;
import com.guardian.netcore.SelectorThread;
import com.guardian.status.Status;
import com.guardian.util.IPv4Filter;
import com.guardian.util.Memory;
import com.guardian.util.Util;
import com.guardian.util.database.L2DatabaseFactory;

public class GameServer
{
	private static Logger LOGGER = Logger.getLogger("Loader");
	private static SelectorThread<L2GameClient> _selectorThread;
	private static LoginServerThread _loginThread;
	private static L2GamePacketHandler _gamePacketHandler;
	private static Status _statusServer;
	
	public static final Calendar dateTimeServerStarted = Calendar.getInstance();
	
	public static void main(final String[] args) throws Exception
	{
		PropertyConfigurator.configure(FService.LOG_CONF_FILE);
		ServerType.serverMode = ServerType.MODE_GAMESERVER;
		
		final String LOG_FOLDER_BASE = "log"; // Name of folder for LOGGER base file
		final File logFolderBase = new File(LOG_FOLDER_BASE);
		logFolderBase.mkdir();
		
		// Local Constants
		final String LOG_FOLDER = "log/game";
		
		// Load GameServer Configs
		Config.load();
		
		// Create LOGGER folder
		File logFolder = new File(LOG_FOLDER);
		logFolder.mkdir();
		
		// Create input stream for LOGGER file -- or store file data into memory
		
		// check for legacy Implementation
		File log_conf_file = new File(FService.LOG_CONF_FILE);
		if (!log_conf_file.exists())
		{
			// old file position
			log_conf_file = new File(FService.LEGACY_LOG_CONF_FILE);
		}
		
		InputStream is = new FileInputStream(log_conf_file);
		LogManager.getLogManager().readConfiguration(is);
		is.close();
		is = null;
		logFolder = null;
		
		final long serverLoadStart = System.currentTimeMillis();
		
		Util.printSection("Team");
		
		// Print L2jfrozen's Logo
		L2Guardian.info();
		
		
		Util.printSection("Database");
		L2DatabaseFactory.getInstance();
		LOGGER.info("L2DatabaseFactory: loaded.");
		
		Util.printSection("Threads");
		ThreadPoolManager.getInstance();
		if (Config.DEADLOCKCHECK_INTIAL_TIME > 0)
		{
			ThreadPoolManager.getInstance().scheduleGeneralAtFixedRate(DeadlockDetector.getInstance(), Config.DEADLOCKCHECK_INTIAL_TIME, Config.DEADLOCKCHECK_DELAY_TIME);
		}
		new File(Config.DATAPACK_ROOT, "data/clans").mkdirs();
		new File(Config.DATAPACK_ROOT, "data/crests").mkdirs();
		new File(Config.DATAPACK_ROOT, "data/pathnode").mkdirs();
		new File(Config.DATAPACK_ROOT, "data/geodata").mkdirs();
		
		HtmCache.getInstance();
		CrestCache.getInstance();
		L2ScriptEngineManager.getInstance();
		
		nProtect.getInstance();
		if (nProtect.isEnabled())
			LOGGER.info("nProtect System Enabled");
		
		Util.printSection("World");
		L2World.getInstance();
		MapRegionTable.getInstance();
		Announcements.getInstance();
		AutoAnnouncementHandler.getInstance();
		if (!IdFactory.getInstance().isInitialized())
		{
			LOGGER.info("Could not read object IDs from DB. Please Check Your Data.");
			throw new Exception("Could not initialize the ID factory");
		}
		StaticObjects.getInstance();
		TeleportLocationTable.getInstance();
		PartyMatchWaitingList.getInstance();
		PartyMatchRoomList.getInstance();
		GameTimeController.getInstance();
		CharNameTable.getInstance();
		ExperienceData.getInstance();
		DuelManager.getInstance();
		
		if (Config.ENABLE_CLASS_DAMAGES)
			ClassDamageManager.loadConfig();
		
		if (Config.AUTOSAVE_DELAY_TIME > 0)
		{
			AutoSaveManager.getInstance().startAutoSaveManager();
		}
		
		Util.printSection("Skills");
		if (!SkillTable.getInstance().isInitialized())
		{
			LOGGER.info("Could not find the extraced files. Please Check Your Data.");
			throw new Exception("Could not initialize the skill table");
		}
		SkillTreeTable.getInstance();
		SkillSpellbookTable.getInstance();
		NobleSkillTable.getInstance();
		HeroSkillTable.getInstance();
		LOGGER.info("Skills: All skills loaded.");
		
		Util.printSection("Items");
		if (!ItemTable.getInstance().isInitialized())
		{
			LOGGER.info("Could not find the extraced files. Please Check Your Data.");
			throw new Exception("Could not initialize the item table");
		}
		ArmorSetsTable.getInstance();
		if (Config.CUSTOM_ARMORSETS_TABLE)
		{
			CustomArmorSetsTable.getInstance();
		}
		ExtractableItemsData.getInstance();
		SummonItemsData.getInstance();
		if (Config.ALLOWFISHING)
			FishTable.getInstance();
		
		Util.printSection("Npc");
		NpcWalkerRoutesTable.getInstance().load();
		if (!NpcTable.getInstance().isInitialized())
		{
			LOGGER.info("Could not find the extraced files. Please Check Your Data.");
			throw new Exception("Could not initialize the npc table");
		}
		
		Util.printSection("Characters");
		if (Config.COMMUNITY_TYPE.equals("full"))
		{
			ForumsBBSManager.getInstance().initRoot();
		}
		
		ClanTable.getInstance();
		CharTemplateTable.getInstance();
		LevelUpData.getInstance();
		if (!HennaTable.getInstance().isInitialized())
		{
			throw new Exception("Could not initialize the Henna Table");
		}
		
		if (!HennaTreeTable.getInstance().isInitialized())
		{
			throw new Exception("Could not initialize the Henna Tree Table");
		}
		
		if (!HelperBuffTable.getInstance().isInitialized())
		{
			throw new Exception("Could not initialize the Helper Buff Table");
		}
		
		Util.printSection("Geodata");
		GeoData.getInstance();
		if (Config.GEODATA == 2)
		{
			PathFinding.getInstance();
		}
		
		Util.printSection("Economy");
		TradeController.getInstance();
		L2Multisell.getInstance();
		LOGGER.info("Multisell: loaded.");
		
		Util.printSection("Clan Halls");
		ClanHallManager.getInstance();
		FortressOfResistance.getInstance();
		DevastatedCastle.getInstance();
		BanditStrongholdSiege.getInstance();
		AuctionManager.getInstance();
		
		Util.printSection("Zone");
		ZoneData.getInstance();
		
		Util.printSection("Spawnlist");
		if (!Config.ALT_DEV_NO_SPAWNS)
		{
			SpawnTable.getInstance();
		}
		else
		{
			LOGGER.info("Spawn: disable load.");
		}
		if (!Config.ALT_DEV_NO_RB)
		{
			RaidBossSpawnManager.getInstance();
			GrandBossManager.getInstance();
			RaidBossPointsManager.init();
		}
		else
		{
			LOGGER.info("RaidBoss: disable load.");
		}
		DayNightSpawnManager.getInstance().notifyChangeMode();
		
		Util.printSection("Dimensional Rift");
		DimensionalRiftManager.getInstance();
		
		Util.printSection("Misc");
		RecipeTable.getInstance();
		RecipeController.getInstance();
		EventDroplist.getInstance();
		AugmentationData.getInstance();
		MonsterRace.getInstance();
		MercTicketManager.getInstance();
		PetitionManager.getInstance();
		CursedWeaponsManager.getInstance();
		TaskManager.getInstance();
		L2PetDataTable.getInstance().loadPetsData();
		SQLQueue.getInstance();
		if (Config.ACCEPT_GEOEDITOR_CONN)
		{
			GeoEditorListener.getInstance();
		}
		if (Config.SAVE_DROPPED_ITEM)
		{
			ItemsOnGroundManager.getInstance();
		}
		if (Config.AUTODESTROY_ITEM_AFTER > 0 || Config.HERB_AUTO_DESTROY_TIME > 0)
		{
			ItemsAutoDestroy.getInstance();
		}
		Util.printSection("Manor");
		L2Manor.getInstance();
		CastleManorManager.getInstance();
		
		Util.printSection("Castles");
		CastleManager.getInstance();
		SiegeManager.getInstance();
		FortManager.getInstance();
		FortSiegeManager.getInstance();
		CrownManager.getInstance();
		
		Util.printSection("Boat");
		BoatManager.getInstance();
		
		Util.printSection("Doors");
		DoorTable.getInstance().parseData();
		
		Util.printSection("Four Sepulchers");
		FourSepulchersManager.getInstance();
		
		Util.printSection("Seven Signs");
		SevenSigns.getInstance();
		SevenSignsFestival.getInstance();
		AutoSpawn.getInstance();
		AutoChatHandler.getInstance();
		
		Util.printSection("Olympiad System");
		Olympiad.getInstance();
		Hero.getInstance();
		
		Util.printSection("Access Levels");
		AccessLevels.getInstance();
		AdminCommandAccessRights.getInstance();
		GmListTable.getInstance();
		
		Util.printSection("AntiBot");
	    AntiBotTable.getInstance().loadImage();
		
		Util.printSection("Handlers");
		ItemHandler.getInstance();
		SkillHandler.getInstance();
		AdminCommandHandler.getInstance();
		UserCommandHandler.getInstance();
		VoicedCommandHandler.getInstance();
		
		LOGGER.info("AutoChatHandler : Loaded " + AutoChatHandler.getInstance().size() + " handlers in total.");
		LOGGER.info("AutoSpawnHandler : Loaded " + AutoSpawn.getInstance().size() + " handlers in total.");
		
		Runtime.getRuntime().addShutdownHook(Shutdown.getInstance());
		
		try
		{
			DoorTable doorTable = DoorTable.getInstance();
			
			// Opened by players like L2OFF
			// doorTable.getDoor(19160010).openMe();
			// doorTable.getDoor(19160011).openMe();
			
			doorTable.getDoor(19160012).openMe();
			doorTable.getDoor(19160013).openMe();
			doorTable.getDoor(19160014).openMe();
			doorTable.getDoor(19160015).openMe();
			doorTable.getDoor(19160016).openMe();
			doorTable.getDoor(19160017).openMe();
			doorTable.getDoor(24190001).openMe();
			doorTable.getDoor(24190002).openMe();
			doorTable.getDoor(24190003).openMe();
			doorTable.getDoor(24190004).openMe();
			doorTable.getDoor(23180001).openMe();
			doorTable.getDoor(23180002).openMe();
			doorTable.getDoor(23180003).openMe();
			doorTable.getDoor(23180004).openMe();
			doorTable.getDoor(23180005).openMe();
			doorTable.getDoor(23180006).openMe();
			doorTable.checkAutoOpen();
			doorTable = null;
		}
		catch (final NullPointerException e)
		{
			LOGGER.info("There is errors in your Door.csv file. Update door.csv");
			if (Config.ENABLE_ALL_EXCEPTIONS)
				e.printStackTrace();
		}
		
		Util.printSection("AI");
		if (!Config.ALT_DEV_NO_AI)
		{
			AILoader.init();
		}
		else
		{
			LOGGER.info("AI: disable load.");
		}
		
		Util.printSection("Scripts");
		if (!Config.ALT_DEV_NO_SCRIPT)
		{
			final File scripts = new File(Config.DATAPACK_ROOT, "data/scripts.cfg");
			L2ScriptEngineManager.getInstance().executeScriptsList(scripts);
			
			final CompiledScriptCache compiledScriptCache = L2ScriptEngineManager.getInstance().getCompiledScriptCache();
			if (compiledScriptCache == null)
				LOGGER.info("Compiled Scripts Cache is disabled.");
			else
			{
				compiledScriptCache.purge();
				if (compiledScriptCache.isModified())
				{
					compiledScriptCache.save();
					LOGGER.info("Compiled Scripts Cache was saved.");
				}
				else
					LOGGER.info("Compiled Scripts Cache is up-to-date.");
			}
			FaenorScriptEngine.getInstance();
		}
		else
		{
			LOGGER.info("Script: disable load.");
		}
		
		/* QUESTS */
		Util.printSection("Quests");
		if (!Config.ALT_DEV_NO_QUESTS)
		{
			
			if (QuestManager.getInstance().getQuests().size() == 0)
				QuestManager.getInstance().reloadAllQuests();
			else
				QuestManager.getInstance().report();
			
		}
		else
		{
			QuestManager.getInstance().unloadAllQuests();
		}
		
		Util.printSection("Game Server");
		
		if (Config.IRC_ENABLED)
			IrcManager.getInstance().getConnection().sendChan(Config.IRC_MSG_START);
		
		LOGGER.info("IdFactory: Free ObjectID's remaining: " + IdFactory.getInstance().size());
		try
		{
			DynamicExtension.getInstance();
		}
		catch (final Exception ex)
		{
			if (Config.ENABLE_ALL_EXCEPTIONS)
				ex.printStackTrace();
			
			LOGGER.info("DynamicExtension could not be loaded and initialized" + ex);
		}
		
		Util.printSection("Custom Mods");
		
		if (Config.L2JMOD_ALLOW_WEDDING || Config.ALLOW_AWAY_STATUS || Config.PCB_ENABLE || Config.POWERPAK_ENABLED)
		{
			if (Config.L2JMOD_ALLOW_WEDDING)
				CoupleManager.getInstance();
			
			if (Config.ALLOW_AWAY_STATUS)
				AwayManager.getInstance();
			
			if (Config.PCB_ENABLE)
				ThreadPoolManager.getInstance().scheduleGeneralAtFixedRate(PcPoint.getInstance(), Config.PCB_INTERVAL * 1000, Config.PCB_INTERVAL * 1000);
			
			if (Config.POWERPAK_ENABLED)
				PowerPak.getInstance();
		}
		else
			LOGGER.info("All custom mods are Disabled.");
		
		Util.printSection("EventManager");
		EventManager.getInstance().startEventRegistration();
		
		if (EventManager.TVT_EVENT_ENABLED || EventManager.CTF_EVENT_ENABLED || EventManager.DM_EVENT_ENABLED)
		{
			if (EventManager.TVT_EVENT_ENABLED)
				LOGGER.info("TVT Event is Enabled.");
			if (EventManager.CTF_EVENT_ENABLED)
				LOGGER.info("CTF Event is Enabled.");
			if (EventManager.DM_EVENT_ENABLED)
				LOGGER.info("DM Event is Enabled.");
		}
		else
			LOGGER.info("All events are Disabled.");
		
		   if(Config.ARENA_EVENT_ENABLED) {
				ThreadPoolManager.getInstance().scheduleGeneral(EvtArenaManager.getInstance(), 60000);
			}
		
		   if (Config.EVENT_MONUMENT_STATUET)
		    {
		      Util.printSection("Monument Statuet Event");
		      EventMonumentStatuet.getInstance().load();
		      EventMonumentStatuet.getInstance().ConfiguratePereiod();
		      EventMonumentStatuet.getInstance().spawnStatuets();
		    }
		
		if ((Config.OFFLINE_TRADE_ENABLE || Config.OFFLINE_CRAFT_ENABLE) && Config.RESTORE_OFFLINERS)
			OfflineTradeTable.restoreOfflineTraders();
		
		 Util.printSection("Event Manager");
		 if(Config.EVENT_BY_TIME_OF_DAY)
		 InitialBossEvent.getInstance().StartCalculationOfNextEventTime();
		 else
		 LOGGER.info("# Auto Evento Lost Boss: [Desativado] #");
		
		System.gc();
		
		Util.printSection("Restart Manager");
		if(Config.RESTART_BY_TIME_OF_DAY)
		   Restart.getInstance().StartCalculationOfNextRestartTime();
		  else
		 LOGGER.info("# Auto Restart System is Disabled #");
		       
		   System.gc();
		Util.printSection("Protection");
		
		if (Config.CHECK_SKILLS_ON_ENTER)
			LOGGER.info("Check skills on enter actived.");
		
		if (Config.CHECK_NAME_ON_LOGIN)
			LOGGER.info("Check bad name on enter actived.");
		
		if (Config.PROTECTED_ENCHANT)
			LOGGER.info("Check OverEnchant items on enter actived.");
		
		if (Config.BYPASS_VALIDATION)
			LOGGER.info("Bypass Validation actived.");
		
		if (Config.L2WALKER_PROTEC)
			LOGGER.info("L2Walker protection actived.");
		
		if (Config.BOT_PROTECTOR)
			LOGGER.info("Bot Protection actived.");
		
		if (!NetcoreConfig.getInstance().DISABLE_FULL_PACKETS_FLOOD_PROTECTOR)
			LOGGER.info("Full packets flood protector actived.");
		
		if (NetcoreConfig.ENABLE_CLIENT_FLOOD_PROTECTION)
			LOGGER.info("Client flood protection actived.");
		
		Util.printSection("Info");
		LOGGER.info("Operating System: " + Util.getOSName() + " " + Util.getOSVersion() + " " + Util.getOSArch());
		LOGGER.info("Available CPUs: " + Util.getAvailableProcessors());
		LOGGER.info("Maximum Numbers of Connected Players: " + Config.MAXIMUM_ONLINE_USERS);
		LOGGER.info("GameServer Started, free memory " + Memory.getFreeMemory() + " Mb of " + Memory.getTotalMemory() + " Mb");
		LOGGER.info("Used memory: " + Memory.getUsedMemory() + " MB");
		
		Util.printSection("Java specific");
		LOGGER.info("JRE name: " + System.getProperty("java.vendor"));
		LOGGER.info("JRE specification version: " + System.getProperty("java.specification.version"));
		LOGGER.info("JRE version: " + System.getProperty("java.version"));
		LOGGER.info("--- Detecting Java Virtual Machine (JVM)");
		LOGGER.info("JVM installation directory: " + System.getProperty("java.home"));
		LOGGER.info("JVM Avaible Memory(RAM): " + Runtime.getRuntime().maxMemory() / 1048576 + " MB");
		LOGGER.info("JVM specification version: " + System.getProperty("java.vm.specification.version"));
		LOGGER.info("JVM specification vendor: " + System.getProperty("java.vm.specification.vendor"));
		LOGGER.info("JVM specification name: " + System.getProperty("java.vm.specification.name"));
		LOGGER.info("JVM implementation version: " + System.getProperty("java.vm.version"));
		LOGGER.info("JVM implementation vendor: " + System.getProperty("java.vm.vendor"));
		LOGGER.info("JVM implementation name: " + System.getProperty("java.vm.name"));
		
		Util.printSection("Status");
		System.gc();
		LOGGER.info("Server Loaded in " + (System.currentTimeMillis() - serverLoadStart) / 1000 + " seconds");
		ServerStatus.getInstance();
		
		// Load telnet status
		Util.printSection("Telnet");
		if (Config.IS_TELNET_ENABLED)
		{
			_statusServer = new Status(ServerType.serverMode);
			_statusServer.start();
		}
		else
		{
			LOGGER.info("Telnet server is disabled.");
		}
		
		Util.printSection("Login");
		_loginThread = LoginServerThread.getInstance();
		_loginThread.start();
		
		final SelectorConfig sc = new SelectorConfig();
		sc.setMaxReadPerPass(NetcoreConfig.getInstance().MMO_MAX_READ_PER_PASS);
		sc.setMaxSendPerPass(NetcoreConfig.getInstance().MMO_MAX_SEND_PER_PASS);
		sc.setSleepTime(NetcoreConfig.getInstance().MMO_SELECTOR_SLEEP_TIME);
		sc.setHelperBufferCount(NetcoreConfig.getInstance().MMO_HELPER_BUFFER_COUNT);
		
		_gamePacketHandler = new L2GamePacketHandler();
		
		_selectorThread = new SelectorThread<>(sc, _gamePacketHandler, _gamePacketHandler, _gamePacketHandler, new IPv4Filter());
		
		InetAddress bindAddress = null;
		if (!Config.GAMESERVER_HOSTNAME.equals("*"))
		{
			try
			{
				bindAddress = InetAddress.getByName(Config.GAMESERVER_HOSTNAME);
			}
			catch (final UnknownHostException e1)
			{
				if (Config.ENABLE_ALL_EXCEPTIONS)
					e1.printStackTrace();
				
				LOGGER.warn("The GameServer bind address is invalid, using all avaliable IPs. Reason: ", e1);
			}
		}
		
		try
		{
			_selectorThread.openServerSocket(bindAddress, Config.PORT_GAME);
		}
		catch (final IOException e)
		{
			if (Config.ENABLE_ALL_EXCEPTIONS)
				e.printStackTrace();
			
			LOGGER.fatal("Failed to open server socket. Reason: ", e);
			System.exit(1);
		}
		_selectorThread.start();
	}
	
	public static SelectorThread<L2GameClient> getSelectorThread()
	{
		return _selectorThread;
	}
}