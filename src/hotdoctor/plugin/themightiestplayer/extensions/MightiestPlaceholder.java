package hotdoctor.plugin.themightiestplayer.extensions;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;

import hotdoctor.plugin.themightiestplayer.Main;
import hotdoctor.plugin.themightiestplayer.managers.EventManager;
import hotdoctor.plugin.themightiestplayer.utils.EventCreator;
import hotdoctor.plugin.themightiestplayer.utils.databasetypes.GoalTypes;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;

public class MightiestPlaceholder extends PlaceholderExpansion{

	@Override
	public @NotNull String getAuthor() {
		// TODO Auto-generated method stub
		return "HotDoctor";
	}

	@Override
	public @NotNull String getIdentifier() {
		// TODO Auto-generated method stub
		return "TMP";
	}

	@Override
	public @NotNull String getVersion() {
		// TODO Auto-generated method stub
		return "1.0.1";
	}
	
    @Override
    public String onRequest(OfflinePlayer player, String identifier){
  
        if(identifier.contains("value_")) {
        	String[] separador = identifier.split("_");
        	String eventID = separador[1];
        	if(Main.enabledEventsID.contains(eventID)) {
        		EventManager manager = EventCreator.cachedInfo.get(eventID);
        		return ""+manager.getDatabase().getPoints(Bukkit.getPlayer(player.getUniqueId()));
        	}else {
        		return "P1 ERROR: UNABLE TO GET EVENT ID, ID="+separador[1];
        	}
        }else if(identifier.contains("global_")){
        	String[] separador = identifier.split("_");
        	String eventID = separador[1];
        	if(Main.enabledEventsID.contains(eventID)) {
        		EventManager manager = EventCreator.cachedInfo.get(eventID);
        		if(manager.getGoalTypeEvent().equals(GoalTypes.GLOBAL)) {
        			return ""+manager.getDatabase().getGlobalPoints();
        		}
        			
        		return "0";
        		
        	}else {
        		return "P2 ERROR: UNABLE TO GET EVENT ID";
        	}
        }else if(identifier.contains("maxglobal_")) {
        	String[] separador = identifier.split("_");
        	String eventID = separador[1];
        	if(Main.enabledEventsID.contains(eventID)) {
        		EventManager manager = EventCreator.cachedInfo.get(eventID);
        		if(manager.getGoalTypeEvent().equals(GoalTypes.GLOBAL)) {
        			if(manager.getMAXGlobalPoints() <= manager.getDatabase().getGlobalPoints()) {
        				return ""+manager.getMAXGlobalPoints();
        			}
        			return ""+manager.getDatabase().getGlobalPoints();
        			
        		}else {
        			return "0";
        		}
        	}else {
        		return "P3 ERROR: UNABLE TO GET EVENT ID";
        	}
        }

        // We return null if an invalid placeholder (f.e. %example_placeholder3%) 
        // was provided
        return null;
    }

}
