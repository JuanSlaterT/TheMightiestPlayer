package hotdoctor.plugin.themightiestplayer.event_types;


import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import hotdoctor.plugin.themightiestplayer.Main;
import hotdoctor.plugin.themightiestplayer.managers.EventManager;
import hotdoctor.plugin.themightiestplayer.progress.EventProgress;
import hotdoctor.plugin.themightiestplayer.utils.EventCreator;

public abstract class CustomEvent{
	
	private String name;
	private String description;
	private Plugin plugin;
	
	public CustomEvent(Plugin plugin, String name, String description) {
		this.name = name;
		this.description = description;
		this.plugin = plugin;
	}
	
	public abstract void onEnable();
	
	
	public boolean isEventActive() {
		return Main.enabledEvents.contains(name+";;"+description);
	}
	
	public int getPointsFromConfig() {
		for(String id : Main.enabledEventsID) {
			EventManager manager = EventCreator.cachedInfo.get(id);
			if(manager.getEventType().equalsIgnoreCase(this.getName())){
				if(!manager.getEventProgress().equals(EventProgress.STARTED)) {
					continue;
				}
				if(!manager.getProperties().contains("POINTS=")) {
					return 1;
				}
				if(!manager.getProperties().contains(",")) {
					String[] split2 = manager.getProperties().split("=");
					if(isInteger(split2[1])) {
						return Integer.valueOf(split2[1]);
					}else {
						return 1;
					}
				}else {
					String[] separador = manager.getProperties().split(",");
					for(int i=0; i<separador.length; i++) {
						if(separador[i].contains("POINTS=")) {
							String[] split2 = separador[i].split("=");
							if(isInteger(split2[1])) {
								return Integer.valueOf(split2[1]);
							}else {
								return 1;
							}
						}
					}
				}
				
			}
		}
		return 0;
	}
	private boolean isInteger(String s) {
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
	public String getArguments() {
		for(String id : Main.enabledEventsID) {
			EventManager manager = EventCreator.cachedInfo.get(id);
			if(!manager.getEventProgress().equals(EventProgress.STARTED)) {
				continue;
			}
			if(manager.getEventType().equalsIgnoreCase(this.getName())){
				return manager.getProperties();
			}
		}
		return null;
		
	}
	public void addPoints(Player p, int amount) {
		for(String id : Main.enabledEventsID) {
			EventManager manager = EventCreator.cachedInfo.get(id);
			if(!manager.getEventProgress().equals(EventProgress.STARTED)) {
				continue;
			}
			if(manager.getEventType().equalsIgnoreCase(this.getName())){
				EventCreator.addPointsEvent(id, p, amount, amount);
				return;
			}
		}
	}
	
	public void removePoints(Player p, int amount, int individual) {
		for(String id : Main.enabledEventsID) {
			EventManager manager = EventCreator.cachedInfo.get(id);
			if(!manager.getEventProgress().equals(EventProgress.STARTED)) {
				continue;
			}
			if(manager.getEventType().equalsIgnoreCase(this.getName())){
				EventCreator.removePointsEvent(id, p, amount, individual);
				return;
			}
		}
	}
	
	public void setPoints(Player p, int amount, int individual) {
		for(String id : Main.enabledEventsID) {
			EventManager manager = EventCreator.cachedInfo.get(id);
			if(!manager.getEventProgress().equals(EventProgress.STARTED)) {
				continue;
			}
			if(manager.getEventType().equalsIgnoreCase(this.getName())){
				EventCreator.setPointsEvent(id, p, amount, individual);
				return;
			}
		}
	}
	
	public Plugin getPlugin() {
		return plugin;
	}
	public String getName() {
		return name;
	}
	public String getDescription() {
		return description;
	}
	
	
	

}
