package hotdoctor.plugin.themightiestplayer.managers;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.bukkit.Bukkit;

import hotdoctor.plugin.themightiestplayer.Main;

public class DateManager {
	
	private Main plugin;
	
	public DateManager(Main plugin) {
		plugin.sendError("&8[&6TheMightiestPlayer&8] &a&lPLUGIN PROGRESS&6 >> &bStarting loading DateManager class");
		this.plugin = plugin;
		plugin.sendError("&8[&6TheMightiestPlayer&8] &a&lPLUGIN PROGRESS&6 >> &bDateManager class loaded.");
	}
	
	public static Date getDate(String date) throws ParseException {
		SimpleDateFormat format = new SimpleDateFormat("dd-MM-yy_HH:mm");
		format.format(new Date());
		Date data = format.parse(date);
		return data;
	}
	
	
	public String getRemainingTime(Date end) {
		Date now = new Date();
		long difference = end.getTime() - now.getTime();
		int seconds = (int) (difference / 1000) %60;
		int minutes = (int) ((difference / (1000*60)) %60);
		int hours = (int) ((difference / (1000*60*60)) %24);
		int days = (int) ((difference / (1000*60*60*24) %365));
		String toReturn = days+"d, "+hours+"h, "+minutes+"m, "+seconds+"s";
		if(toReturn.contains("0d") && toReturn.contains(" 0h") && toReturn.contains(" 0m") && toReturn.contains(" 0s")) {
			return "0";
		}
		if(toReturn.contains("0d") && toReturn.contains(" 0h") && toReturn.contains(" 0m")) {
			toReturn = toReturn.replace(days+"d, ", "");
			toReturn = toReturn.replace(hours+"h, ", "");
			toReturn = toReturn.replace(minutes+"m, ", "");
		}
		if(toReturn.contains("0d") && toReturn.contains(" 0h")) {
			toReturn = toReturn.replace(days+"d, ", "");
			toReturn = toReturn.replace(hours+"h, ", "");
		}
		if(toReturn.contains("0d")) {
			toReturn = toReturn.replace(days+"d, ", "");
		}
		if(toReturn.contains(" 0m")) {
			toReturn = toReturn.replace(minutes+"m, ", "");
		}
		if(toReturn.contains(" 0h")) {
			toReturn = toReturn.replace(hours+"h, ", "");
		}
		if(difference < 0) {
			return "0";
		}
		return toReturn;
		
	}
	
	
	

}
