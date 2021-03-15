package hotdoctor.plugin.themightiestplayer.utils.databasetypes;

import java.io.IOException;

import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import hotdoctor.plugin.themightiestplayer.Main;
import hotdoctor.plugin.themightiestplayer.managers.EventManager;
import hotdoctor.plugin.themightiestplayer.utils.EventCreator;


public abstract class Database {
	
	private String eventID;
	private Plugin plugin;
	private LocatedDatabase data;
	public Database(String eventID, Plugin plugin) {
		this.eventID = eventID;
		this.plugin = plugin;
		data = new LocatedDatabase(plugin, this);
		this.loadGData();
	}
	
	
	public LocatedDatabase getRAMDatabase() {
		return data;
	}
	
	public String getEventID() {
		return eventID;
	}
	
	public Plugin plugin() {
		return this.plugin;
	}

	public boolean isInteger(String s) {
	    try { 
	        Integer.parseInt(s); 
	    } catch(NumberFormatException e) { 
	        return false; 
	    } catch(NullPointerException e) {
	        return false;
	    }
	    // only got here if we didn't return false
	    return true;
	}
	
	public int getPoints(Player p) {
		LocatedDatabase data = this.getRAMDatabase();
		int value = data.getPoints(p);
		return value;
	}
	
	public void setPoints(Player p, int value, String property, int individual) {
		LocatedDatabase data = this.getRAMDatabase();
		data.setPoints(p, value, property, individual);
	}
	
	public void addPoints(Player p, int value, String property, int individual) {
		LocatedDatabase data = this.getRAMDatabase();
		int valor = data.getPoints(p);
		int total = value + valor;
		data.setPoints(p, total, property, individual);
		
	}
	
	public void removePoints(Player p, int value, String property, int individual) {
		LocatedDatabase data = this.getRAMDatabase();
		int valor = data.getPoints(p);
		int total = value - valor;
		data.setPoints(p, total, property, individual);
	}
	
	public boolean getCompletedStatus(Player p) {
		LocatedDatabase data = this.getRAMDatabase();
		return data.isCompletedStatus(p);
	}
	
	public void setCompletedStatus(Player p, Boolean bool) {
		
		LocatedDatabase data = this.getRAMDatabase();
		data.setCompletedStatus(p, bool);
	}
	
	public boolean getIsRewarded(Player p) {
		LocatedDatabase data = this.getRAMDatabase();
		return data.isAlreadyRewarded(p);
	}
	
	public void setIsRewarded(Player p, boolean bool) {
		LocatedDatabase data = this.getRAMDatabase();
		data.setAlreadyRewarded(p, bool);
	}
	
	public boolean getFinalRewarded(Player p) {
		LocatedDatabase data = this.getRAMDatabase();
		return data.isFinalReward(p);
	}
	
	public void setFinalRewarded(Player p, boolean bool) {
		LocatedDatabase data = this.getRAMDatabase();
		data.setFinalReward(p, bool);
	}
	
	public void setGlobalPoints(int i) {
		this.getRAMDatabase().setGlobalPoints(i);
	}
	
	public int getGlobalPoints() {
		return this.getRAMDatabase().getGlobalPoints();
	}
	
	public void loadGData() {
			EventManager manager = EventCreator.cachedInfo.get(this.getEventID());
			if(manager.getDatabaseTypes().equals(DatabaseTypes.MULTIWORLD)) {
				if(manager.getGoalTypeEvent().equals(GoalTypes.GLOBAL)) {
					int value = 0;
					if(!Main.plugindata.contains("events."+getEventID()+".value")) {
						Main.plugindata.set("events."+this.getEventID()+".value", value);
						try {
							Main.plugindata.save(Main.datayml);
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}else {
						value = Main.plugindata.getInt("events."+getEventID()+".value");
					}
					this.setGlobalPoints(value);
					
				}
			}

		
	}
	
	public abstract void saveData(Player p);
	
	public abstract void loadData(Player p);
	
	
}
