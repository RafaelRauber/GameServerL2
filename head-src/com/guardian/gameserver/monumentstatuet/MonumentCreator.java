package com.guardian.gameserver.monumentstatuet;

import com.guardian.Config;
import com.guardian.util.CloseUtil;
import com.guardian.util.database.L2DatabaseFactory;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Map;
import javolution.util.FastMap;

public class MonumentCreator
{
  int _right_hand = 0;
  int _female = 0;
  int _hair2 = 0;
  int[] dados = new int[3];
  int _class_id = 0;
  int _hair_style = 0;
  int _hair_color = 0;
  int _face = 0;
  int _hero = 0;
  int _wpn_enchant = 0;
  int _feet = 0;
  int _left_hand = 0;
  int _gloves = 0;
  int _chest = 0;
  int _legs = 0;
  int _hair = 0;
  String _name;
  String _title;
  FastMap<Integer, EventMonumentStatuet.Items> _items;
  
  public void create(int[] obj_id, Map<Integer, EventMonumentStatuet.Items> items)
  {
    dados = obj_id;
    
    _items = ((FastMap)items);
    
    load();
  }
  
  private void load()
  {
    Connection con = null;
    String SQL = "SELECT char_name,face,hairStyle,hairColor,sex,classid FROM characters WHERE obj_Id=" + dados[0];
    try
    {
      con = L2DatabaseFactory.getInstance().getConnection();
      PreparedStatement st = con.prepareStatement(SQL);
      ResultSet rs = st.executeQuery();
      while (rs.next())
      {
        _name = rs.getString("char_name");
        _face = rs.getInt("face");
        _hair_style = rs.getInt("hairStyle");
        _hair_color = rs.getInt("hairColor");
        _female = rs.getInt("sex");
        _class_id = rs.getInt("classid");
      }
      st.close();
      rs.close();
    }
    catch (Exception e)
    {
      System.out.println("[MonumentCreator]: Error in load SQL FROM characters...");
      e.printStackTrace();
    }
    finally
    {
      CloseUtil.close(con);
      con = null;
    }
    System.out.println("[Form Monument]: Load info char ok...");
    
    _title = (dados[1] + " pvp's in 24h");
    _title = (dados[2] + " pk's in 24h");
    for (EventMonumentStatuet.Items item : _items.values())
    {
      switch (item.getItemSlot())
      {
      case 16384: 
        _left_hand = item.getItemId();
        _right_hand = item.getItemId();
        break;
      case 128: 
        _right_hand = item.getItemId();
        break;
      case 262144: 
        _hair = item.getItemId();
        break;
      case 65536: 
        _hair2 = item.getItemId();
        break;
      case 1024: 
        _chest = item.getItemId();
        break;
      case 512: 
        _gloves = item.getItemId();
        break;
      case 4096: 
        _feet = item.getItemId();
        break;
      case 64: 
        _hair = item.getItemId();
        break;
      case 32768: 
        _chest = item.getItemId();
        break;
      case 2048: 
        _legs = item.getItemId();
      }
      if ((item instanceof EventMonumentStatuet.Weapon)) {
        _wpn_enchant = ((EventMonumentStatuet.Weapon)item).getItemEnchant();
      }
    }
    System.out.println("[Form Monument]: Aparence information OK...");
    
    putIntoDb();
    
    System.out.println("[Form Monument]: Data Saved...");
  }
  
  public void putIntoDb()
  {
    Connection con = null;
    try
    {
      con = L2DatabaseFactory.getInstance().getConnection();
      
      PreparedStatement statement = con.prepareStatement("UPDATE npc_to_pc_polymorph SET name=?,title=?,class_id=?,female=?,hair_style=?,hair_color=?,face=?,hero=?,wpn_enchant=?,right_hand=?,left_hand=?,gloves=?,chest=?,legs=?,feet=?,hair=?,hair2=? WHERE template=" + (dados[1] == 1 ? Config.NPC_TEMPLATE_ID_PVP : Config.NPC_TEMPLATE_ID_PK));
      




      statement.setString(1, _name);
      statement.setString(2, _title);
      statement.setInt(3, _class_id);
      statement.setInt(4, _female);
      statement.setInt(5, _hair_style);
      statement.setInt(6, _hair_color);
      statement.setInt(7, _face);
      statement.setInt(8, _hero);
      statement.setInt(9, _wpn_enchant);
      statement.setInt(10, _right_hand);
      statement.setInt(11, _left_hand);
      
      statement.setInt(12, _gloves);
      statement.setInt(13, _chest);
      statement.setInt(14, _legs);
      statement.setInt(15, _feet);
      
      statement.setInt(16, _hair);
      statement.setInt(17, _hair2);
      
      statement.execute();
      statement.close();
    }
    catch (Exception e)
    {
      System.out.println("[MonumentCreator]: Failt to putIntoDb...");
      e.printStackTrace();
    }
    finally
    {
      CloseUtil.close(con);
      con = null;
    }
  }
}