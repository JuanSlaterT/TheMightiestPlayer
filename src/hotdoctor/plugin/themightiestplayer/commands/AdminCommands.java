package hotdoctor.plugin.themightiestplayer.commands;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import hotdoctor.plugin.themightiestplayer.Main;

public class AdminCommands implements CommandExecutor{
	
	public AdminCommands(Main plugin){
		this.plugin = plugin;
	}
	private Main plugin;
	
	public boolean onCommand(CommandSender sender, Command comando, String label, String[] args) {
		if (!(sender instanceof Player)) {
			if (args.length <= 0) {
				List<String> comandos = commandhelp();
				for(String linea : comandos) {
					plugin.sendError(linea);
				}
			}else {
				switch(args[0]) {
				case "help":
					List<String> comandos = commandhelp();
					for(String linea : comandos) {
						plugin.sendError(linea);
					}
					return true;
				}
				plugin.sendError("&8[&6TheMightiestPlayer&8] &6&l>> &cThere are no commands for console at the momment.");
				
			}
			
		}else {
			Player p = (Player) sender;
			if (args.length <= 0) {
				plugin.menu.openMenu(p);
			}else {
				if(p.hasPermission("tmp.admin")) {
					switch(args[0]) {
					case "help":
						List<String> comandos = commandhelp();
						for(String linea : comandos) {
							p.sendMessage(plugin.fancy(p.getName(), linea));
						}
						return true;
					}
					p.sendMessage(plugin.fancy(p.getName(), "&8[&6TheMightiestPlayer&8] &6&l>> &cThis is not a valid command in the plugin"));
				}else {
					
				}
			}
		}
		return false;
	}
	
	
	
	public List<String> commandhelp(){
		List<String> toReturn = new ArrayList<>();
		toReturn.add("&8&l&m=======================");
		toReturn.add("&6TheMightiestPlayer &bv"+plugin.version);
		toReturn.add("&6&oAliases: &9&o/mightiestplayer, /mp, /tmp");
		toReturn.add("");
		toReturn.add("&a/themightiestplayer &7-- &b&oOpens the GUI of the plugin");
		toReturn.add("&a/themightiestplayer reload &7-- &b&oReloads the plugin configuration");
		toReturn.add("&a/themightiestplayer about &e<CustomEvent> &7-- &b&oProvides the information of a loaded CustomEvent");
		toReturn.add("&a/themightiestplayer customEventList &7-- &bReturns a list of loaded CustomEvents");
		toReturn.add("&a/themightiestplayer info &7-- &b&oReturns the customer information");
		toReturn.add("");
		toReturn.add("&8&l&m=======================");
		return toReturn;
	}

}
