package hotdoctor.plugin.themightiestplayer.utils;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.text.ParseException;
import java.util.Date;
import java.util.HashMap;

import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import hotdoctor.plugin.themightiestplayer.Main;
import hotdoctor.plugin.themightiestplayer.managers.DateManager;
import hotdoctor.plugin.themightiestplayer.managers.EventManager;
import hotdoctor.plugin.themightiestplayer.progress.EventProgress;
import hotdoctor.plugin.themightiestplayer.utils.databasetypes.Database;
import hotdoctor.plugin.themightiestplayer.utils.databasetypes.DatabaseTypes;
import hotdoctor.plugin.themightiestplayer.utils.databasetypes.GoalTypes;
import hotdoctor.plugin.themightiestplayer.utils.databasetypes.MightiestDataPlayer;

public class EventCreator {
	public static HashMap<String, EventManager> cachedInfo = new HashMap<>();
	private Main plugin;
	
	public EventCreator(Main plugin) throws ParseException {
		this.plugin = plugin;
		plugin.sendError("&8[&6TheMightiestPlayer&8] &9&lCONFIG PROGRESS&6 >> &bStarting loading EventCreator Manager");
		plugin.sendError("&8[&6TheMightiestPlayer&8] &9&lCONFIG PROGRESS&6 >> &bLoading events from config.yml...");
		for(String id : Main.configuration.getConfigurationSection("events").getKeys(false)) {
			plugin.sendError("&8[&6TheMightiestPlayer&8] &9&lCONFIG PROGRESS&6 >> &bLoading EventID &6"+id+" &bfrom your config.yml...");
			createEvent(id, "events."+id+".event-specifications");
			
		}
	}
	
	public Database getDatabase(Class<? extends Database> clase, String eventID) {
		Database data = null;
		try {
			data = clase.getConstructor(String.class, Plugin.class).newInstance(eventID, plugin);
		} catch (InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException
				| NoSuchMethodException | SecurityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return data;
	}
	
	public void createEvent(String eventID, String path) throws ParseException {
		if(!Main.enabledEventsID.contains(eventID)) {
			
			EventManager evento = new EventManager(eventID, plugin);
			cachedInfo.put(eventID, evento);
			Main.enabledEventsID.add(eventID);
			plugin.sendError("&8[&6TheMightiestPlayer&8] &9&lCONFIG PROGRESS&6 >> &bGetting database Type...");
			DatabaseTypes type = DatabaseTypes.valueOf(Main.configuration.getString(path+".TYPE"));
			evento.setDatabaseTypes(type);
			GoalTypes goal = GoalTypes.valueOf(Main.configuration.getString(path+".GOAL_TYPE"));
			
			evento.setGoalTypesEvent(goal);
			plugin.sendError("&8[&6TheMightiestPlayer&8] &9&lCONFIG PROGRESS&6 >> &bGetting min-points Boolean and final-reward boolean");
			evento.setBooleanPoints(Main.configuration.getBoolean(path+".min-points-reward.enabled"));
			if(evento.isMinPointsEnabled()) {
				plugin.sendError("&8[&6TheMightiestPlayer&8] &9&lCONFIG PROGRESS&6 >> &bGetting min-points value...");
				evento.setMinPoints(Main.configuration.getInt(path+".min-points-reward.min-points"));
			}
			
			evento.setFinalRewardEnabled(Main.configuration.getBoolean(path+".finish-event-reward.enabled"));
			if(type.equals(DatabaseTypes.MULTIWORLD)) {
				if(goal.equals(GoalTypes.INDIVIDUAL)) {
					plugin.sendError("&8[&6TheMightiestPlayer&8] &9&lCONFIG PROGRESS&6 >> &bGenerating Customed Privated Database...");
					evento.setDatabase(getDatabase(MightiestDataPlayer.class, eventID));
					plugin.sendError("&8[&6TheMightiestPlayer&8] &e&lCONFIG &3&lSUCESS&6 >> &bLoaded privated database without problems.");
			
				}else if(goal.equals(GoalTypes.GLOBAL)) {
					int max_points = 100;
					if(Main.configuration.contains(path+".MAX_POINTS")) {
						max_points = Main.configuration.getInt(path+".MAX_POINTS");
					}
					plugin.sendError("&8[&6TheMightiestPlayer&8] &9&lCONFIG PROGRESS&6 >> &bGenerating Global Customed Privated Database...");
					evento.setDatabase(getDatabase(MightiestDataPlayer.class, eventID));
					evento.setMAXGlobalPoints(max_points);
					plugin.sendError("&8[&6TheMightiestPlayer&8] &e&lCONFIG &3&lSUCESS&6 >> &bLoaded privated global database without problems.");
				}
			}else if(type.equals(DatabaseTypes.SHARED)) {
				if(goal.equals(GoalTypes.INDIVIDUAL)) {
					
				}else if(goal.equals(GoalTypes.GLOBAL)) {
					
				}
				
				
			}
			plugin.sendError("&8[&6TheMightiestPlayer&8] &9&lCONFIG PROGRESS&6 >> &bGetting EventType and Event Properties...");
			String EventType = Main.configuration.getString(path+".EVENT_TYPE");
			if(!EventType.contains(":")) {
				evento.setEventType(EventType);
				evento.setProperties("");
			}else {
				String[] split = EventType.split(":");
				evento.setEventType(split[0]);
				evento.setProperties(split[1]);
			}
			plugin.sendError("&8[&6TheMightiestPlayer&8] &e&lCONFIG &3&lSUCESS&6 >> &bPlugin has saved correctly in the EventManager the EventType and EventProperties of the selected event.");
			plugin.sendError("&8[&6TheMightiestPlayer&8] &9&lCONFIG PROGRESS&6 >> &bGetting Start and End date...");
			String startDate = Main.configuration.getString(path+".starts");
			String endDate = Main.configuration.getString(path+".ends");
			evento.setStartEventDate(startDate);
			evento.setEndEventDate(endDate);
			Date date = DateManager.getDate(startDate);
			if(plugin.dateManager.getRemainingTime(date) != "0"){
				evento.setEventProgress(EventProgress.NOT_STARTED);
			}else {
				Date date2 = DateManager.getDate(endDate);
				if(plugin.dateManager.getRemainingTime(date2) != "0"){
					evento.setEventProgress(EventProgress.STARTED);
				}else {
					evento.setEventProgress(EventProgress.FINISHED);
				}
			}
			plugin.sendError("&8[&6TheMightiestPlayer&8] &e&lCONFIG &3&lSUCESS&6 >> &bCompleted, Event progress has been setted as &6"+evento.getEventProgress().toString());
			if(!evento.getEventProgress().equals(EventProgress.DISABLED)) {
				plugin.sendError("&8[&6TheMightiestPlayer&8] &9&lCONFIG PROGRESS&6 >> &bEvent &6"+eventID+" &bhas been loaded correctly in the plugin database!");
				plugin.menu.addEventToMenu(eventID);
			}
		}else {
			plugin.sendError("&8[&6TheMightiestPlayer&8] &c&lCONFIG ERROR&6 >> &bError while loading eventID &6"+eventID+" &bis already registered in the plugin!");
		}
	}
	
	public static int getPointsEvent(String id, Player p) throws IOException {
		EventManager info = cachedInfo.get(id);
		return info.getDatabase().getPoints(p);
		
	}
	public static void addPointsEvent(String id, Player p, int value, int individual) {
		EventManager info = cachedInfo.get(id);
		if(info.getEventProgress().equals(EventProgress.STARTED)) {
			info.getDatabase().addPoints(p, value, "update", individual);
		}
		
	}
	
	public static void removePointsEvent(String id, Player p, int value, int individual) {
		EventManager info = cachedInfo.get(id);
		if(info.getEventProgress().equals(EventProgress.STARTED)) {
			info.getDatabase().removePoints(p, value, "update", individual);
		}
	}
	
	public static void setPointsEvent(String id, Player p, int value, int individual) {
		EventManager info = cachedInfo.get(id);
		if(info.getEventProgress().equals(EventProgress.STARTED)) {
			info.getDatabase().setPoints(p, value, "update", individual);
		}
	}
	
	
	
	
}
