package hotdoctor.plugin.themightiestplayer.commands;

import java.io.File;
import java.sql.SQLException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryView;

import hotdoctor.plugin.themightiestplayer.Main;
import hotdoctor.plugin.themightiestplayer.event_types.CustomEvent;
import hotdoctor.plugin.themightiestplayer.managers.EventManager;
import hotdoctor.plugin.themightiestplayer.progress.EventProgress;
import hotdoctor.plugin.themightiestplayer.utils.EventCreator;
import hotdoctor.plugin.themightiestplayer.utils.MenuCreator;

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
						break;
					case "about":
						if(args[1] != null) {
							for(CustomEvent e : Main.eventList) {
								if(args[1].equalsIgnoreCase(e.getName())) {
									p.sendMessage(plugin.fancy(p.getName(), "&8&m===================================="));
									p.sendMessage(plugin.fancy(p.getName(), "            &8[&6TheMightiestPlayer&8]          "));
									p.sendMessage(plugin.fancy(p.getName(), "&7Information about CustomEvent &6"+e.getName()));
									p.sendMessage(plugin.fancy(p.getName(), ""));
									p.sendMessage(plugin.fancy(p.getName(), "&7Description: &b"+e.getDescription()));
									p.sendMessage(plugin.fancy(p.getName(), ""));
									p.sendMessage(plugin.fancy(p.getName(), "&8&m===================================="));
									return true;
								}
							}
							p.sendMessage(plugin.fancy(p.getName(), "&8[&6TheMightiestPlayer&8] &6&l>> &cThe entered CustomEvent is not valid"));
							p.sendMessage(plugin.fancy(p.getName(), "&8[&6TheMightiestPlayer&8] &6&l>> &cplease use &b/tmp customEventList &cfor more information."));
						}else {
							p.sendMessage(plugin.fancy(p.getName(), "&8[&6TheMightiestPlayer&8] &6&l>> &cYou must specify a CustomEvent"));
							p.sendMessage(plugin.fancy(p.getName(), "&8[&6TheMightiestPlayer&8] &6&l>> &cplease use &b/tmp customEventList &cfor more information."));
						}
						break;
					case "customEventList":
						p.sendMessage(plugin.fancy(p.getName(), "&8&m===================================="));
						p.sendMessage(plugin.fancy(p.getName(), "            &8[&6TheMightiestPlayer&8]          "));
						p.sendMessage(plugin.fancy(p.getName(), "&6List of events: "));
						for(int i = 0; i<Main.eventList.size(); i++) {
							p.sendMessage(plugin.fancy(p.getName(), " &f&l+ &a"+Main.eventList.get(i).getName()));
							if(i==Main.eventList.size()-1) {
								p.sendMessage(plugin.fancy(p.getName(), "&8&m===================================="));
							}
						}
						break;
					case "reload":
						for(Player players : Bukkit.getOnlinePlayers()) {
							InventoryView view = players.getOpenInventory();
							if(view == null) {
								  continue;
							}
							Inventory inv = view.getTopInventory();
							if(inv.getHolder() instanceof MenuCreator) {
								view.close();
							}
						}
						p.sendMessage(plugin.fancy(p.getName(), "&8&m===================================="));
						p.sendMessage(plugin.fancy(p.getName(), "            &8[&6TheMightiestPlayer&8] &6&lPROGRESS         "));
						p.sendMessage(plugin.fancy(p.getName(), "Reloading configuration files..."));
						Main.configyml = new File(plugin.getDataFolder(), "config.yml");
						Main.datayml = new File(plugin.getDataFolder(), "plugin-data.yml");
						Main.configuration = YamlConfiguration.loadConfiguration(Main.configyml);
						Main.plugindata = YamlConfiguration.loadConfiguration(Main.datayml);
						p.sendMessage(plugin.fancy(p.getName(), "Finishing events..."));
						for(String id : Main.enabledEventsID) {
							EventManager manager = EventCreator.cachedInfo.get(id);
							manager.setEventProgress(EventProgress.FINISHED);
							
						}
						if(plugin.eventCreator.mysql != null) {
							p.sendMessage(plugin.fancy(p.getName(), "Disconnecting from MySQL Database..."));
							try {
								plugin.eventCreator.mysql.Disconnect();
							} catch (SQLException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
						}
						p.sendMessage(plugin.fancy(p.getName(), "Reloading and Deleting Cached information..."));
						Main.enabledEventsID = new ArrayList<>();
						p.sendMessage(plugin.fancy(p.getName(), "Reloading Menu Information..."));
						plugin.menu = new MenuCreator(plugin);
						p.sendMessage(plugin.fancy(p.getName(), "Reloading Events Information..."));
						try {
							plugin.eventCreator = new EventCreator(plugin);
						} catch (ParseException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						p.sendMessage(plugin.fancy(p.getName(), "Plugin reloaded correctly."));
						p.sendMessage(plugin.fancy(p.getName(), "&8&m===================================="));
						break;
					default:
						p.sendMessage(plugin.fancy(p.getName(), "&8[&6TheMightiestPlayer&8] &6&l>> &cThis is not a valid command in the plugin"));
						break;
					}
					
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
		toReturn.add("");
		toReturn.add("&8&l&m=======================");
		return toReturn;
	}

}
