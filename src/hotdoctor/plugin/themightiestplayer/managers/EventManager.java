package hotdoctor.plugin.themightiestplayer.managers;

import java.text.ParseException;
import java.util.Date;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitTask;

import hotdoctor.plugin.themightiestplayer.Main;
import hotdoctor.plugin.themightiestplayer.event_types.CustomEvent;
import hotdoctor.plugin.themightiestplayer.progress.EventProgress;
import hotdoctor.plugin.themightiestplayer.utils.databasetypes.Database;
import hotdoctor.plugin.themightiestplayer.utils.databasetypes.DatabaseTypes;
import hotdoctor.plugin.themightiestplayer.utils.databasetypes.GoalTypes;
import hotdoctor.plugin.themightiestplayer.utils.databasetypes.MightiestDataPlayer;

public class EventManager {
	
	EventProgress eventProgress;
	GoalTypes goalTypesEvent;
	DatabaseTypes databaseTypes;
	String eventType;
	String properties;
	String startDate;
	String endDate;
	Database database1;
	String id;
	Main plugin;
	private int minPoints;
	private boolean hasMinPoints;
	private boolean finalReward;
	private int points = 0;
	public EventManager(String id, Main plugin) {
		this.id = id;
		this.plugin = plugin;
	}
	
	public void setMAXGlobalPoints(int value) {
		this.points = value;
	}
	public int getMAXGlobalPoints() {
		return points;
	}
	
	
	public void setBooleanPoints(boolean bool) {
		hasMinPoints = bool;
	}
	
	public boolean isMinPointsEnabled() {
		return hasMinPoints;
	}
	
	public void setFinalRewardEnabled(boolean bool) {
		finalReward = bool;
	}
	public boolean isFinalRewardEnabled() {
		return finalReward;
	}
	public void setMinPoints(int points) {
		if(hasMinPoints) {
			this.minPoints = points;
		}
	}
	
	@SuppressWarnings("null")
	public int getMinPoints() {
		if(hasMinPoints) {
			return minPoints;
		}else{
			return (Integer) null;
		}
	}
	public void setEventType(String eventType) {
		this.eventType = eventType;
	}
	public String getEventType() {
		return eventType;
	}
	
	public void setProperties(String proper) {
		this.properties = proper;
	}
	
	public String getProperties() {
		return this.properties;
	}
	public void setEndEventDate(String date) {
		this.endDate = date;
	}
	public String getEndEventDate() {
		return this.endDate;
	}
	public void setStartEventDate(String date) {
		startDate = date;
	}
	public String getStartEventDate() {
		return startDate;
	}
	public String getID() {
		return id;
	}
	
	public Database getDatabase() {
		return database1;
	}
	
	public void setDatabase(Database database) {
		this.database1 = database;
	}
	
	public DatabaseTypes getDatabaseTypes() {
		return databaseTypes;
	}
	
	public void setDatabaseTypes(DatabaseTypes databaseType) {
		this.databaseTypes = databaseType;
	}
	public GoalTypes getGoalTypeEvent() {
		return goalTypesEvent;
	}
	
	public void setGoalTypesEvent(GoalTypes goal) {
		this.goalTypesEvent = goal;
	}
	
	public EventProgress getEventProgress() {
		return eventProgress;
	}
	private BukkitTask taskID;
	
	private void runTask() {
		taskID = Bukkit.getScheduler().runTaskTimerAsynchronously(plugin, () -> {
			
			try {
				
				String dateForStart = "";
				if(this.getEventProgress().equals(EventProgress.NOT_STARTED)) {
					dateForStart = this.getStartEventDate();
				}else if(this.getEventProgress().equals(EventProgress.STARTED)) {
					dateForStart = this.getEndEventDate();
				}
				Date date = DateManager.getDate(dateForStart);
				String remaining = plugin.dateManager.getRemainingTime(date);
				switch(remaining) {
				case "0":
					if(this.getEventProgress().equals(EventProgress.NOT_STARTED)) {
						this.setEventProgress(EventProgress.STARTED);
					}else if(this.getEventProgress().equals(EventProgress.STARTED)) {
						this.setEventProgress(EventProgress.FINISHED);
						
					}
				}
				
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			if(this.getEventProgress().equals(EventProgress.FINISHED) || this.getEventProgress().equals(EventProgress.DISABLED)) {
				Bukkit.getScheduler().cancelTask(taskID.getTaskId());
			
			}
			
		}, 20, 20);
	}
	public void setEventProgress(EventProgress event) {
		EventProgress lastEvent = null;
		if(this.getEventProgress() != null) {
			lastEvent = this.getEventProgress();
		}
		this.eventProgress = event;
		if(event.equals(EventProgress.STARTED)) {
			if(existsEventType(this.getEventType())) {
				plugin.event.enableEvent(getCustomEvent(this.getEventType()), id);
			}
		}
		if(lastEvent != null) {
			if(lastEvent.equals(EventProgress.STARTED) && event.equals(EventProgress.FINISHED)) {
				if(existsEventType(this.getEventType())) {
					plugin.event.disableEvent(getCustomEvent(this.getEventType()));
				}
				for(Player p : Bukkit.getOnlinePlayers()) {
					this.getDatabase().saveData(p);
				}
			}
		}
		if(event.equals(EventProgress.NOT_STARTED) || event.equals(EventProgress.STARTED)) {
			if(taskID == null) {
				runTask();
			}
		}
		
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	private CustomEvent getCustomEvent(String eventType) {
		for(CustomEvent event : Main.eventList) {
			if(event.getName().equalsIgnoreCase(eventType)) {
				return event;
			}
			
		}
		return null;
	}
	private boolean existsEventType(String eventType) {
		for(CustomEvent event : Main.eventList) {
			if(event.getName().equalsIgnoreCase(eventType)) {
				return true;
			}
			
		}
		return false;
	}

}
