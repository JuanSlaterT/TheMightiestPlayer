package hotdoctor.plugin.themightiestplayer.utils.databasetypes;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import hotdoctor.plugin.themightiestplayer.Main;

public class MightiestMySQLPlayer extends Database{

	
	private Main plugin;
	private MySQL connection;
	public MightiestMySQLPlayer(String eventID, Plugin plugin) {
		super(eventID, plugin);
		this.plugin = (Main) plugin;
		connection = this.plugin.eventCreator.mysql;
	}
	
	
	
	
	
	@Override
	public void saveData(Player p) {
		Bukkit.getScheduler().runTaskAsynchronously(plugin, ()-> {
			if(!this.getRAMDatabase().everythingHasData(p)) {
				this.loadData(p);
			}
			int value = this.getRAMDatabase().getPoints(p);
			boolean valor = this.getRAMDatabase().isCompletedStatus(p);
			boolean isRewarded = this.getRAMDatabase().isAlreadyRewarded(p);
			boolean isFinalReward = this.getRAMDatabase().isFinalReward(p);
			String TABLE = "Mightiest"+this.getEventID();
			String preparament = "INSERT INTO "+TABLE+" (player_uuid, points, iscompleted, alreadyrewarded, finalrewarded) VALUES (?, ?, ?, ?, ?)";
			this.save(preparament, TABLE, p.getUniqueId(), value, valor, isRewarded, isFinalReward);
			
		});
		
	}
	
	public void save(String preparament, String table, UUID uuid, int value, boolean Completed, boolean Rewarded, boolean Final) {
		String toDelete = "DELETE FROM "+table+" WHERE player_uuid=?";
		delete(toDelete, uuid);
		try {
			PreparedStatement st = this.connection.getConnection().prepareStatement(preparament);
			st.setString(1, uuid.toString());
			st.setString(2, String.valueOf(value));
			st.setString(3, String.valueOf(Completed));
			st.setString(4, String.valueOf(Rewarded));
			st.setString(5, String.valueOf(Final));
			st.execute();
			
		} catch (SQLException e) {
			plugin.sendError("&8[&6TheMightiestPlayer&8] &c&lCONFIG ERROR&6 >> &bError while saving player information: "+Bukkit.getPlayer(uuid)+ "in the eventID: "+this.getEventID());
			plugin.sendError("&8[&6TheMightiestPlayer&8] &c&lCONFIG ERROR&6 >> &bFull StackTrace:");
			e.printStackTrace();
		}
		
	}
	

	@Override
	public void loadData(Player p) {
		Bukkit.getScheduler().runTaskAsynchronously(plugin, ()-> {
			String puntos = getResult("points", p.getUniqueId());
			String iscompleted = getResult("iscompleted", p.getUniqueId());
			String alreadyrewarded = getResult("alreadyrewarded", p.getUniqueId());
			String finalrewarded = getResult("finalrewarded", p.getUniqueId());
			int points = 0;
			boolean completed = false;
			boolean alreadyRewarded = false;
			boolean finalRewarded = false;
			if(puntos != null) {
				points = Integer.valueOf(puntos);
			}
			if(iscompleted != null) {
				completed = Boolean.valueOf(iscompleted);
			}
			if(alreadyrewarded != null) {
				alreadyRewarded = Boolean.valueOf(alreadyrewarded);
			}
			if(finalrewarded != null) {
				finalRewarded = Boolean.valueOf(finalrewarded);
			}
			this.setPoints(p, points, "no", 0);
			this.setCompletedStatus(p, completed);
			this.setIsRewarded(p, alreadyRewarded);
			this.setFinalRewarded(p, finalRewarded);
			
		});
		
	}
	
	public String getResult(String key, UUID uuid) {
		String toReturn = "";
		String TABLE = "Mightiest"+this.getEventID();
		String command = "SELECT "+key+" FROM "+TABLE+" WHERE player_uuid='"+uuid.toString()+"'";
		ResultSet rs;
		try {
			rs = this.connection.executeQuery(command);
			if(rs.next()) {
				toReturn = rs.getString(key);
				
			}else {
				return null;
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			plugin.sendError("&8[&6TheMightiestPlayer&8] &c&lCONFIG ERROR&6 >> &bError while getting player information: "+Bukkit.getPlayer(uuid)+ "in the eventID: "+this.getEventID());
			plugin.sendError("&8[&6TheMightiestPlayer&8] &c&lCONFIG ERROR&6 >> &bKey Type: "+key);
			plugin.sendError("&8[&6TheMightiestPlayer&8] &c&lCONFIG ERROR&6 >> &bFull StackTrace:");
			e.printStackTrace();
		}
		
		return toReturn;
		
	}
	
	public void delete(String command, UUID uuid) {
		PreparedStatement st;
		try {
			st = this.connection.getConnection().prepareStatement(command);
			st.setString(1, uuid.toString());
			st.execute();
		} catch (SQLException e) {
			plugin.sendError("&8[&6TheMightiestPlayer&8] &c&lCONFIG ERROR&6 >> &bError while deleting player information: "+Bukkit.getPlayer(uuid)+ "in the eventID: "+this.getEventID());
			plugin.sendError("&8[&6TheMightiestPlayer&8] &c&lCONFIG ERROR&6 >> &bFull StackTrace:");
			e.printStackTrace();
		}
		
	}

}
