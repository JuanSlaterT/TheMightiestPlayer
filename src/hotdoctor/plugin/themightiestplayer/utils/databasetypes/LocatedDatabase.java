package hotdoctor.plugin.themightiestplayer.utils.databasetypes;

import java.util.HashMap;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;

import hotdoctor.plugin.themightiestplayer.Main;
import hotdoctor.plugin.themightiestplayer.managers.EventManager;
import hotdoctor.plugin.themightiestplayer.utils.EventCreator;


public class LocatedDatabase implements Listener{
	
	Database clase;
	private Plugin plugin;
	
	private HashMap<String, Integer> storedInRam = new HashMap<>();
	private HashMap<String, Boolean> storedInRam2 = new HashMap<>();
	private HashMap<String, Boolean> alreadyRewarded = new HashMap<>();
	private HashMap<String, Boolean> finalReward = new HashMap<>();
	
	private HashMap<String, Integer> global = new HashMap<>();
	
	public LocatedDatabase(Plugin plugin,  Database clase) {
		this.clase = clase;
		this.plugin = plugin;
		Bukkit.getServer().getPluginManager().registerEvents(this, plugin);
		init();
	}
	
	public void init() {
		Bukkit.getScheduler().runTaskTimer(plugin, () -> {
			if(!Bukkit.getOnlinePlayers().isEmpty()) {
				for(Player p : Bukkit.getOnlinePlayers()) {
					clase.saveData(p);
				}
			}
			
		}, 0, 300*20);
	}
	
	@EventHandler
	public void salir(PlayerQuitEvent e) {
		EventManager manager = EventCreator.cachedInfo.get(clase.getEventID());
		if(manager.getDatabaseTypes().equals(DatabaseTypes.MULTIWORLD)) {
			if(!timer.containsKey(e.getPlayer())) {
				wait(e.getPlayer());
				clase.saveData(e.getPlayer());
			}
		}else {
			clase.saveData(e.getPlayer());
		}
	}
	
	@EventHandler 
	public void entrar(PlayerJoinEvent e) {
		EventManager manager = EventCreator.cachedInfo.get(clase.getEventID());
		if(manager.getDatabaseTypes().equals(DatabaseTypes.MULTIWORLD)) {
			if(!this.everythingHasData(e.getPlayer())) {
				clase.loadData(e.getPlayer());
			}
		}else {
			clase.loadData(e.getPlayer());
		}
	}
	
	
	private HashMap<Player, Integer> timer = new HashMap<>();
	public void wait(Player p) {
		int value = 10;
		if(!timer.containsKey(p)) {
			timer.put(p, value);
			new BukkitRunnable() {

				@Override
				public void run() {
					int time = timer.get(p);
					if(time == 0) {
						timer.remove(p);
						this.cancel();
					}else {
						time = time - 1;
						timer.remove(p);
						timer.put(p, time);
					}
					
				}
				
			}.runTaskTimerAsynchronously(plugin, 20, 20);
		}
		
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	public boolean everythingHasData(Player p) {
		if(storedInRam.containsKey(p.getName()+";;"+clase.getEventID()) && storedInRam2.containsKey(p.getName()+";;"+clase.getEventID()) && alreadyRewarded.containsKey(p.getName()+";;"+clase.getEventID())) {
			EventManager manager = EventCreator.cachedInfo.get(clase.getEventID());
			if(manager.getGoalTypeEvent().equals(GoalTypes.GLOBAL)) {
				if(!global.containsKey(clase.getEventID())) {
					return false;
				}
			}
			return true;
		}
		return false;
	}
	
	
	
	///////////////////////////////////////////////
	
	public void setPoints(Player p, int value, String property, int individual) {
		if(Main.enabledEventsID.contains(clase.getEventID())) {
			if(!storedInRam.containsKey(p.getName()+";;"+clase.getEventID())) {
				storedInRam.put(p.getName()+";;"+clase.getEventID(), value);
			}else {
				storedInRam.remove(p.getName()+";;"+clase.getEventID());
				storedInRam.put(p.getName()+";;"+clase.getEventID(), value);
			}
			EventManager manager = EventCreator.cachedInfo.get(clase.getEventID());
			if(manager.getGoalTypeEvent().equals(GoalTypes.GLOBAL)) {
				if(property.equalsIgnoreCase("update")) {
					this.setGlobalPoints(this.getGlobalPoints() + individual);
					if(manager.isMinPointsEnabled()) {
						if(!this.isAlreadyRewarded(p)) {
							if(value == manager.getMinPoints() || value > manager.getMinPoints()) {
								this.setCompletedStatus(p, true);
							}else {
								this.setCompletedStatus(p, false);
							}
						}else {
							this.setCompletedStatus(p, true);
						}
					}
				}
			}else if(manager.getGoalTypeEvent().equals(GoalTypes.INDIVIDUAL)) {
				if(property.equalsIgnoreCase("update")) {
					if(manager.isMinPointsEnabled()) {
						if(!this.isAlreadyRewarded(p)) {
							if(value == manager.getMinPoints() || value > manager.getMinPoints()) {
								this.setCompletedStatus(p, true);
							}else {
								this.setCompletedStatus(p, false);
							}
						}else {
							this.setCompletedStatus(p, true);
						}
					}else {
						if(this.isAlreadyRewarded(p)) {
							this.setCompletedStatus(p, true);
						}else {
							this.setCompletedStatus(p, false);
						}
					}
				}
			}
			

		}
	}
	
	public int getPoints(Player p) {
		if(Main.enabledEventsID.contains(clase.getEventID())) {
			if(!everythingHasData(p)) {
				clase.loadData(p);
			}
			if(!storedInRam.containsKey(p.getName()+";;"+clase.getEventID())) {
				storedInRam.put(p.getName()+";;"+clase.getEventID(), 0);
			}
			return storedInRam.get(p.getName()+";;"+clase.getEventID());
		}
		return 0;
	}
	
	//////////////////////////////////////////////
	
	public void setGlobalPoints(int value) {
		if(Main.enabledEventsID.contains(clase.getEventID())) {
			EventManager manager = EventCreator.cachedInfo.get(clase.getEventID());
			if(manager.getGoalTypeEvent().equals(GoalTypes.GLOBAL)) {
				if(!global.containsKey(clase.getEventID())) {
					global.put(clase.getEventID(), value);
				}else {
					global.remove(clase.getEventID());
					global.put(clase.getEventID(), value);
					
				}
			}
			
		}
	}
	
	public int getGlobalPoints() {
		if(Main.enabledEventsID.contains(clase.getEventID())) {
			EventManager manager = EventCreator.cachedInfo.get(clase.getEventID());
			if(manager.getGoalTypeEvent().equals(GoalTypes.GLOBAL)) {
				if(global.containsKey(clase.getEventID())) {
					return global.get(clase.getEventID());
				}
			}
		}
		return 0;
	}
	
	//////////////////////////////////////////////
	
	
	
	public Boolean isCompletedStatus(Player p) {
		if(!everythingHasData(p)) {
			clase.loadData(p);
		}
		if(Main.enabledEventsID.contains(clase.getEventID())) {
			if(!storedInRam2.containsKey(p.getName()+";;"+clase.getEventID())) {
				storedInRam2.put(p.getName()+";;"+clase.getEventID(), false);
			}
			return storedInRam2.get(p.getName()+";;"+clase.getEventID());
		}
		
		return false;
	}
	
	public void setCompletedStatus(Player p, boolean bool) {
		if(Main.enabledEventsID.contains(clase.getEventID())) {
			if(!storedInRam2.containsKey(p.getName()+";;"+clase.getEventID())) {
				storedInRam2.put(p.getName()+";;"+clase.getEventID(), bool);
				return;
			}
			storedInRam2.remove(p.getName()+";;"+clase.getEventID());
			storedInRam2.put(p.getName()+";;"+clase.getEventID(), bool);
		}
	}
	
	//////////////////////////////////////////////
	
	
	
	
	public Boolean isAlreadyRewarded(Player p) {
		if(!everythingHasData(p)) {
			clase.loadData(p);
		}
		if(Main.enabledEventsID.contains(clase.getEventID())) {
			if(!alreadyRewarded.containsKey(p.getName()+";;"+clase.getEventID())) {
				alreadyRewarded.put(p.getName()+";;"+clase.getEventID(), false);
			}
			return alreadyRewarded.get(p.getName()+";;"+clase.getEventID());
		}
		
		return false;
	}
	
	public void setAlreadyRewarded(Player p, boolean bool) {
		if(Main.enabledEventsID.contains(clase.getEventID())) {
			if(!alreadyRewarded.containsKey(p.getName()+";;"+clase.getEventID())) {
				alreadyRewarded.put(p.getName()+";;"+clase.getEventID(), bool);
				return;
			}
			alreadyRewarded.remove(p.getName()+";;"+clase.getEventID());
			alreadyRewarded.put(p.getName()+";;"+clase.getEventID(), bool);
		}
	}
	
	
	//////////////////////////////////////////////
	
	
	
	
	public Boolean isFinalReward(Player p) {
		if(!everythingHasData(p)) {
			clase.loadData(p);
		}
		if(Main.enabledEventsID.contains(clase.getEventID())) {
			if(!finalReward.containsKey(p.getName()+";;"+clase.getEventID())) {
				finalReward.put(p.getName()+";;"+clase.getEventID(), false);
			}
			return finalReward.get(p.getName()+";;"+clase.getEventID());
		}
		
		return false;
	}
	
	public void setFinalReward(Player p, boolean bool) {
		if(Main.enabledEventsID.contains(clase.getEventID())) {
			if(!finalReward.containsKey(p.getName()+";;"+clase.getEventID())) {
				finalReward.put(p.getName()+";;"+clase.getEventID(), bool);
				return;
			}
			finalReward.remove(p.getName()+";;"+clase.getEventID());
			finalReward.put(p.getName()+";;"+clase.getEventID(), bool);
		}
	}

}
