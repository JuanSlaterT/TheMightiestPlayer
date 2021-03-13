package hotdoctor.plugin.themightiestplayer.event_types;

import java.lang.reflect.InvocationTargetException;

import java.util.Arrays;
import java.util.HashMap;

import org.bukkit.plugin.Plugin;

import hotdoctor.plugin.themightiestplayer.Main;
import hotdoctor.plugin.themightiestplayer.event_types.list.PLAYER_BREAK_BLOCK;
import hotdoctor.plugin.themightiestplayer.event_types.list.PLAYER_KILL;
import hotdoctor.plugin.themightiestplayer.event_types.list.PLAYER_KILL_ENTITY;
import hotdoctor.plugin.themightiestplayer.managers.EventManager;
import hotdoctor.plugin.themightiestplayer.progress.EventProgress;
import hotdoctor.plugin.themightiestplayer.utils.EventCreator;

public class Event {
	
	
	public Event(Main plugin) {
		plugin.sendError("&8[&6TheMightiestPlayer&8] &a&lPLUGIN PROGRESS&6 >> &bStarting loading CustomEvent class and Classes made by default extended with CustomEvent");
		this.plugin = plugin;
		extracted();
	}

	@SuppressWarnings("unchecked")
	private void extracted() {
		loadEvents(PLAYER_KILL.class, PLAYER_BREAK_BLOCK.class, PLAYER_KILL_ENTITY.class);
		for(CustomEvent event : Main.eventList) {
			plugin.sendError("&8[&6TheMightiestPlayer&8] &a&lPLUGIN PROGRESS&6 >> &bCustomEvent "+event.getName()+" &bloaded without problems");
			plugin.sendError("&8[&6TheMightiestPlayer&8] &7&lPLUGIN EVENT INFORMATION&6 >> &bDescription of this event: "+event.getDescription());
			plugin.sendError("&8[&6TheMightiestPlayer&8] &7&lPLUGIN EVENT INFORMATION&6 >> &bUsage of this event: "+event.getUsage());
		}
		plugin.sendError("&8[&6TheMightiestPlayer&8] &a&lPLUGIN &6&lSUCESS&6 >> &bLoaded Event and CustomEvents classes made by the plugin correctly.");
		
	}
	
	private Main plugin;
	
	
	public void loadEvents(@SuppressWarnings("unchecked") Class<? extends CustomEvent>... clases) {
		
		Arrays.stream(clases).forEach(Eventclass -> {
			try {
				CustomEvent evento = Eventclass.getConstructor(Plugin.class).newInstance(plugin);
				Main.eventList.add(evento);
			} catch (InstantiationException | IllegalAccessException | IllegalArgumentException
					| InvocationTargetException | NoSuchMethodException | SecurityException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		});
		
	}
	
	private HashMap<CustomEvent, Boolean> alreadyEnabled = new HashMap<>();
	public void enableEvent(CustomEvent event, String eventID) {
		if(!Main.enabledEvents.contains(event.getName()+";;"+event.getDescription()+";;"+event.getUsage())) {
			
			Main.enabledEvents.add(event.getName()+";;"+event.getDescription()+";;"+event.getUsage());
			if(!alreadyEnabled.containsKey(event)) {
				alreadyEnabled.put(event, true);
				event.onEnable();
			}
		}else {
			plugin.sendError("&8[&6TheMightiestPlayer&8] &c&lCONFIG ERROR&6 >> &bError while enabling eventID &6"+eventID+" &bThe CustomEvent &c"+event.getName()+" &bis already being used by another eventID.");
			EventManager manager = EventCreator.cachedInfo.get(eventID);
			manager.setEventProgress(EventProgress.DISABLED);
			plugin.sendError("&8[&6TheMightiestPlayer&8] &7&lPLUGIN EVENT INFORMAION &6>> &bEvent "+eventID+" progress has been setted as &6"+manager.getEventProgress().toString()+" &bdue the last error.");
		}
		
		
	}
	
	public void disableEvent(CustomEvent event) {
		if(Main.enabledEvents.contains(event.getName()+";;"+event.getDescription()+";;"+event.getUsage())) {
			Main.enabledEvents.remove(event.getName()+";;"+event.getDescription()+";;"+event.getUsage());
		}
	}


}
