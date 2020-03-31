package com.guardian.gameserver.monumentstatuet;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Calendar;
import java.util.Map;

import javolution.util.FastMap;

import com.guardian.Config;
import com.guardian.gameserver.datatables.sql.ItemTable;
import com.guardian.gameserver.datatables.sql.NpcTable;
import com.guardian.gameserver.datatables.sql.SpawnTable;
import com.guardian.gameserver.managers.CustomNpcInstanceManager;
import com.guardian.gameserver.model.L2World;
import com.guardian.gameserver.model.actor.instance.L2PcInstance;
import com.guardian.gameserver.model.spawn.L2Spawn;
import com.guardian.gameserver.network.serverpackets.MagicSkillUser;
import com.guardian.gameserver.templates.L2Item;
import com.guardian.gameserver.templates.L2NpcTemplate;
import com.guardian.gameserver.thread.ThreadPoolManager;
import com.guardian.util.CloseUtil;
import com.guardian.util.database.L2DatabaseFactory;

public class EventMonumentStatuet
{
  public static EventMonumentStatuet _instance;
  public static L2Spawn PK_SPAWN;
  public static L2Spawn PVP_SPAWN;
  public boolean inPeriodOk = true;
  private Map<Integer, int[]> startup_players;
  private PreparedStatement statement;
  
  public EventMonumentStatuet()
  {
    startup_players = new FastMap();
  }
  
  public static EventMonumentStatuet getInstance()
  {
    if (_instance == null) {
      _instance = new EventMonumentStatuet();
    }
    return _instance;
  }
  
  public void htmlPKnpc(L2PcInstance pl) {}
  
  public void htmlPVPnpc(L2PcInstance pl) {}
  
  public void spawnStatuets()
  {
    spawnNpcPVP();
    spawnNpcPK();
  }
  
  private static void spawnNpcPVP()
  {
    L2NpcTemplate tmpl = NpcTable.getInstance().getTemplate(Config.NPC_TEMPLATE_ID_PVP);
    try
    {
      PVP_SPAWN = new L2Spawn(tmpl);
      PVP_SPAWN.setLocx(83215);
      PVP_SPAWN.setLocy(148393);
      PVP_SPAWN.setLocz(-3373);
      PVP_SPAWN.setAmount(1);
      PVP_SPAWN.setRespawnDelay(1);
      SpawnTable.getInstance().addNewSpawn(PVP_SPAWN, false);
      PVP_SPAWN.init();
      PVP_SPAWN.getLastSpawn().getStatus().setCurrentHp(999999999.0D);
      PVP_SPAWN.getLastSpawn().isAggressive();
      PVP_SPAWN.getLastSpawn().Monument_Npc_PVP = true;
      PVP_SPAWN.getLastSpawn().decayMe();
      PVP_SPAWN.getLastSpawn().spawnMe(PVP_SPAWN.getLastSpawn().getX(), PVP_SPAWN.getLastSpawn().getY(), PVP_SPAWN.getLastSpawn().getZ());
      
      PVP_SPAWN.getLastSpawn().broadcastPacket(new MagicSkillUser(PVP_SPAWN.getLastSpawn(), PVP_SPAWN.getLastSpawn(), 1034, 1, 1, 1));
      
      System.out.println("[EventMonumentStatuet]:Statue pvp spawned.");
    }
    catch (Exception e)
    {
      System.out.println("[EventMonumentStatuet]:Fail to spawn statue pvp.");
    }
  }
  
  private static void spawnNpcPK()
  {
    L2NpcTemplate tmpl = NpcTable.getInstance().getTemplate(Config.NPC_TEMPLATE_ID_PK);
    try
    {
      PK_SPAWN = new L2Spawn(tmpl);
      PK_SPAWN.setLocx(83220);
      PK_SPAWN.setLocy(147909);
      PK_SPAWN.setLocz(-3373);
      PK_SPAWN.setAmount(1);
      PK_SPAWN.setRespawnDelay(1);
      SpawnTable.getInstance().addNewSpawn(PK_SPAWN, false);
      PK_SPAWN.init();
      PK_SPAWN.getLastSpawn().getStatus().setCurrentHp(999999999.0D);
      PK_SPAWN.getLastSpawn().isAggressive();
      PK_SPAWN.getLastSpawn().Monument_Npc_PK = true;
      PK_SPAWN.getLastSpawn().decayMe();
      PK_SPAWN.getLastSpawn().spawnMe(PK_SPAWN.getLastSpawn().getX(), PK_SPAWN.getLastSpawn().getY(), PK_SPAWN.getLastSpawn().getZ());
      
      PK_SPAWN.getLastSpawn().broadcastPacket(new MagicSkillUser(PK_SPAWN.getLastSpawn(), PK_SPAWN.getLastSpawn(), 1034, 1, 1, 1));
      
      System.out.println("[EventMonumentStatuet]:Statue pk spawned.");
    }
    catch (Exception e)
    {
      System.out.println("[EventMonumentStatuet]:Fail to spawn statue pk.");
    }
    finally
    {
      tmpl = null;
    }
  }
  
  public void reloadAllNpc()
  {
    unspawnNpcPK();
    unspawnNpcPVP();
    
    PK_SPAWN = null;
    PVP_SPAWN = null;
    
    spawnNpcPK();
    spawnNpcPVP();
  }
  
  private static void unspawnNpcPK()
  {
    if ((PK_SPAWN == null) || (PK_SPAWN.getLastSpawn() == null)) {
      return;
    }
    PK_SPAWN.getLastSpawn().deleteMe();
    PK_SPAWN.stopRespawn();
    SpawnTable.getInstance().deleteSpawn(PK_SPAWN, true);
  }
  
  private static void unspawnNpcPVP()
  {
    if ((PVP_SPAWN == null) || (PVP_SPAWN.getLastSpawn() == null)) {
      return;
    }
    PVP_SPAWN.getLastSpawn().deleteMe();
    PVP_SPAWN.stopRespawn();
    SpawnTable.getInstance().deleteSpawn(PVP_SPAWN, true);
  }
  
  public void onEnterPlayer(L2PcInstance pl)
  {
    if ((inPeriodOk) && (startup_players.get(Integer.valueOf(pl.getObjectId())) == null))
    {
      int[] stats = { pl.getObjectId(), pl.getPvpKills(), pl.getPkKills() };
      startup_players.put(Integer.valueOf(pl.getObjectId()), stats);
      storeInDataInfo(pl);
    }
  }
  
  public void ConfiguratePereiod()
  {
    String hour = Config.RATE_HOUR_STATUET_REFRESH.split(":")[0];
    String minutes = Config.RATE_HOUR_STATUET_REFRESH.split(":")[1];
    
    int h = 0;int m = 0;
    long timeL = 0L;
    try
    {
      h = Integer.valueOf(hour).intValue();
      m = Integer.valueOf(minutes).intValue();
      Calendar currentTime = Calendar.getInstance();
      Calendar testStartTime = Calendar.getInstance();
      testStartTime.setLenient(true);
      
      testStartTime.set(11, h);
      testStartTime.set(12, m);
      testStartTime.set(13, 0);
      if (testStartTime.getTimeInMillis() < currentTime.getTimeInMillis()) {
        testStartTime.add(5, 1);
      }
      timeL = testStartTime.getTimeInMillis() - currentTime.getTimeInMillis();
      
      ThreadPoolManager.getInstance().scheduleGeneral(new refreshStatuet(), timeL);
      
      System.out.println("[EventMonumentStatuet]Periodo to refresh statuet: " + testStartTime.getTime());
    }
    catch (Exception e)
    {
      System.out.println("[EventMonumentStatuet]:ConfiguratePereiod()]Fail to load period");
      e.printStackTrace();
    }
  }
  
  private void storeInDataInfo(L2PcInstance pl)
  {
    Connection con = null;
    try
    {
      con = L2DatabaseFactory.getInstance().getConnection();
      
      PreparedStatement statement = con.prepareStatement("INSERT INTO monuments_startup (obj_id, pvp, pk) VALUES (?, ? , ?)");
      statement.setInt(1, pl.getObjectId());
      statement.setInt(2, pl.getPvpKills());
      statement.setInt(3, pl.getPkKills());
      statement.execute();
      statement.close();
    }
    catch (Exception e)
    {
      System.out.println("[EventMonumentStatuet]Error in store!");
      e.printStackTrace();
    }
    finally
    {
      CloseUtil.close(con);
      con = null;
    }
  }
  
  public void load()
  {
    Connection con = null;
    String SQL = "SELECT obj_id, pvp, pk FROM monuments_startup ORDER BY obj_id";
    if (startup_players.isEmpty()) {
      startup_players.clear();
    }
    try
    {
      con = L2DatabaseFactory.getInstance().getConnection();
      
      PreparedStatement statement = con.prepareStatement(SQL);
      
      ResultSet rs = statement.executeQuery();
      while (rs.next())
      {
        int obj_id = rs.getInt("obj_id");
        int pvp = rs.getInt("pvp");
        int pk = rs.getInt("pk");
        
        int[] stats = { obj_id, pvp, pk };
        startup_players.put(Integer.valueOf(obj_id), stats);
      }
      statement.close();
      rs.close();
      
      System.out.println("[EventMonumentStatuet]Loaded " + startup_players.size() + " competitors.");
    }
    catch (Exception e)
    {
      System.out.println("[EventMonumentStatuet]Error in load competitors!");
      e.printStackTrace();
    }
    finally
    {
      CloseUtil.close(con);
      con = null;
    }
  }
  
  public void StartNewPeriod()
  {
    String monuments_startup_del = "TRUNCATE TABLE monuments_startup";
    

    Connection con = null;
    statement = null;
    try
    {
      System.out.println("[EventMonumentStatuet]Delete all elements from table monuments_startup.");
      con = L2DatabaseFactory.getInstance().getConnection();
      statement = con.prepareStatement(monuments_startup_del);
      statement.execute();
      try
      {
        statement.close();
        
        CloseUtil.close(con);
        con = null;
        
        statement = null;
        monuments_startup_del = null;
      }
      catch (Exception e) {}
      startup_players.clear();
    }
    catch (Exception e)
    {
      System.out.println("[EventMonumentStatuet]Error in delete all elements from table monuments_startup.");
      e.printStackTrace();
    }
    finally
    {
      try
      {
        statement.close();
        
        CloseUtil.close(con);
        con = null;
        
        statement = null;
        monuments_startup_del = null;
      }
      catch (Exception e) {}
    }
    System.out.println("[EventMonumentStatuet]Adding all objects online.");
    int[] stats = new int[3];
    for (L2PcInstance pl : L2World.getInstance().getAllPlayers())
    {
      stats[0] = pl.getObjectId();
      stats[1] = pl.getPvpKills();
      stats[2] = pl.getPkKills();
      
      startup_players.put(Integer.valueOf(pl.getObjectId()), stats);
      
      storeInDataInfo(pl);
    }
    System.out.println("[EventMonumentStatuet]Added " + startup_players.size() + " objects online.");
  }
  
  private class refreshStatuet
    implements Runnable
  {
    public refreshStatuet() {}
    
    public void run()
    {
      EventMonumentStatuet.this.inPeriodOk = false;
      EventMonumentStatuet.this.CalculateWins();
      EventMonumentStatuet.this.StartNewPeriod();
      EventMonumentStatuet.this.inPeriodOk = true;
    }
  }
  
  public void CalculateWins()
  {
    if (startup_players.size() <= 1)
    {
      System.out.println("[EventMonumentStatuet]:No participants in event, no statue formed.");
      return;
    }
    Connection con = null;
    String SQL = "SELECT obj_Id, pvpkills, pkkills FROM characters WHERE";
    
    int count = 0;
    for (int[] a : startup_players.values())
    {
      SQL = SQL + (count == 0 ? " obj_Id=" + a[0] : new StringBuilder().append(" or obj_Id=").append(a[0]).toString());
      
      count++;
    }
    int pvp_maior = 0;int pk_maior = 0;
    int[] obj_pvp = { 0, 0 };int[] obj_pk = { 0, 0 };
    System.out.println("[EventMonumentStatuet]:Calculando player wins.");
    try
    {
      con = L2DatabaseFactory.getInstance().getConnection();
      
      PreparedStatement statement = con.prepareStatement(SQL);
      
      ResultSet rs = statement.executeQuery();
      while (rs.next())
      {
        int obj_id = rs.getInt("obj_id");
        int pvp = rs.getInt("pvpkills");
        int pk = rs.getInt("pkkills");
        
        int[] dados = (int[])startup_players.get(Integer.valueOf(obj_id));
        
        int diference_pvp = pvp - dados[1];
        int diference_pk = pk - dados[2];
        if (diference_pvp > pvp_maior)
        {
          pvp_maior = diference_pvp;
          obj_pvp[0] = obj_id;
          obj_pvp[1] = diference_pvp;
        }
        if (diference_pk > pk_maior)
        {
          pk_maior = diference_pk;
          obj_pk[0] = obj_id;
          obj_pk[1] = diference_pk;
        }
      }
      statement.close();
      rs.close();
      System.out.println("[EventMonumentStatuet]:Wins a pvp player:" + obj_pvp[0] + " with " + obj_pvp[1] + " differ.");
      System.out.println("[EventMonumentStatuet]:Wins a pk player:" + obj_pk[0] + " with " + obj_pk[1] + " differ.");
    }
    catch (Exception e)
    {
      System.out.println("[EventMonumentStatuet]Error in createPvpStatuet!");
      e.printStackTrace();
    }
    finally
    {
      CloseUtil.close(con);
      con = null;
    }
    createStatuet(obj_pvp, obj_pk);
  }
  
  private void createStatuet(int[] dados_pvp, int[] dados_pk)
  {
    Connection con = null;
    
    Map<Integer, Items> items_mp_pvp = null;
    Map<Integer, Items> items_mp_pk = null;
    



    L2Item item = null;
    
    System.out.println("[EventMonumentStatuet]: Creating map items for: " + dados_pvp[0] + " and " + dados_pk[0]);
    if (dados_pvp[0] != 0)
    {
      String SQL = "SELECT item_id, enchant_level FROM items WHERE owner_id=" + dados_pvp[0] + " AND loc='PAPERDOLL'";
      items_mp_pvp = new FastMap();
      try
      {
        con = L2DatabaseFactory.getInstance().getConnection();
        PreparedStatement st = con.prepareStatement(SQL);
        ResultSet rs = st.executeQuery();
        while (rs.next())
        {
          int item_id = rs.getInt("item_id");
          int enchant_level = rs.getInt("enchant_level");
          
          item = ItemTable.getInstance().getTemplate(item_id);
          if (item != null) {
            if (item.getType2() == 0) {
              items_mp_pvp.put(Integer.valueOf(items_mp_pvp.size() + 1), new Weapon(item_id, enchant_level, item.getBodyPart()));
            } else if (item.getType2() == 2) {
              items_mp_pvp.put(Integer.valueOf(items_mp_pvp.size() + 1), new Acessory(item_id, item.getBodyPart()));
            } else if (item.getType2() == 1) {
              items_mp_pvp.put(Integer.valueOf(items_mp_pvp.size() + 1), new ArmororShield(item_id, item.getBodyPart()));
            }
          }
        }
        st.close();
        st = null;
        rs.close();
        rs = null;
        
        System.out.println("[EventMonumentStatuet]:Map Items pvp created with " + items_mp_pvp.size() + " items.");
      }
      catch (Exception e)
      {
    	  System.out.println("[EventMonumentStatuet]: Error in createStatuet 1:");
        e.printStackTrace();
      }
      finally
      {
        CloseUtil.close(con);
        con = null;
      }
    }
    if (dados_pk[0] != 0)
    {
      items_mp_pk = new FastMap();
      String SQL = "SELECT item_id, enchant_level FROM items WHERE owner_id=" + dados_pk[0] + " AND loc='PAPERDOLL'";
      try
      {
        con = L2DatabaseFactory.getInstance().getConnection();
        PreparedStatement st = con.prepareStatement(SQL);
        ResultSet rs = st.executeQuery();
        while (rs.next())
        {
          int item_id = rs.getInt("item_id");
          int enchant_level = rs.getInt("enchant_level");
          
          item = ItemTable.getInstance().getTemplate(item_id);
          if (item != null) {
            if (item.getType2() == 0) {
              items_mp_pk.put(Integer.valueOf(items_mp_pk.size() + 1), new Weapon(item_id, enchant_level, item.getBodyPart()));
            } else if (item.getType2() == 2) {
              items_mp_pk.put(Integer.valueOf(items_mp_pk.size() + 1), new Acessory(item_id, item.getBodyPart()));
            } else if (item.getType2() == 1) {
              items_mp_pk.put(Integer.valueOf(items_mp_pk.size() + 1), new ArmororShield(item_id, item.getBodyPart()));
            }
          }
        }
        st.close();
        st = null;
        rs.close();
        rs = null;
        
        System.out.println("[EventMonumentStatuet]:Map Items pk created with " + items_mp_pk.size() + " items.");
      }
      catch (Exception e)
      {
    	  System.out.println("[EventMonumentStatuet]: Error in createStatuet 2:");
        e.printStackTrace();
      }
      finally
      {
        CloseUtil.close(con);
        con = null;
      }
    }
    if ((items_mp_pvp != null) && (items_mp_pk != null))
    {
      System.out.println("[EventMonumentStatuet]:Calling data from the statue new MonumentCreator()");
      MonumentCreator alpha = new MonumentCreator();
      if (dados_pvp[0] != 0)
      {
        int[] dados_pvp_int = new int[3];
        dados_pvp_int[0] = dados_pvp[0];
        dados_pvp_int[1] = 1;
        dados_pvp_int[2] = dados_pvp[1];
        
        alpha.create(dados_pvp_int, items_mp_pvp);
        
        alpha = null;
      }
      if (dados_pk[0] != 0)
      {
        int[] dados_pk_int = new int[3];
        alpha = new MonumentCreator();
        dados_pk_int[0] = dados_pk[0];
        dados_pk_int[1] = 2;
        dados_pk_int[2] = dados_pk[1];
        
        alpha.create(dados_pk_int, items_mp_pk);
        
        alpha = null;
      }
      CustomNpcInstanceManager.getInstance().reload();
      
      reloadAllNpc();
    }
  }
  
  public class Items
  {
    int _item_id;
    int _slot;
    
    public Items(int item_id, int slot)
    {
      _item_id = item_id;
      _slot = slot;
    }
    
    public int getItemId()
    {
      return _item_id;
    }
    
    public int getItemSlot()
    {
      return _slot;
    }
  }
  
  public class Weapon
    extends EventMonumentStatuet.Items
  {
    int _enchant;
    
    public Weapon(int item_id, int enchant, int slot)
    {
      super(item_id, slot);
      
      System.out.println("Added weapon slot:" + slot);
      _enchant = enchant;
    }
    
    public int getItemEnchant()
    {
      return _enchant;
    }
  }
  
  public class ArmororShield
    extends EventMonumentStatuet.Items
  {
    public ArmororShield(int item_id, int slot)
    {
      super(item_id, slot);
    }
  }
  
  public class Acessory
    extends EventMonumentStatuet.Items
  {
    public Acessory(int item_id, int slot)
    {
      super(item_id, slot);
    }
  }
}