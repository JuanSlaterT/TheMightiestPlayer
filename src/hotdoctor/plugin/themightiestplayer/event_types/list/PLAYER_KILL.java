package hotdoctor.plugin.themightiestplayer.event_types.list;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.plugin.Plugin;

import hotdoctor.plugin.themightiestplayer.event_types.CustomEvent;
import net.md_5.bungee.api.ChatColor;

public class PLAYER_KILL extends CustomEvent implements Listener{
	
	private Plugin plugin;
	public PLAYER_KILL(Plugin plugin) {
		super(plugin, "PLAYER_KILL", "Executed when player kills another player");
		this.plugin = plugin;
		// TODO Auto-generated constructor stub
	}

	@Override
	public void onEnable() {
		Bukkit.getServer().getPluginManager().registerEvents(this, plugin);
		
	}
	@EventHandler
	public void pvp(PlayerDeathEvent e) {
		if(this.isEventActive()) {
			if(e.getEntity().getKiller() instanceof Player) {
				Player killer = e.getEntity().getKiller();
				String properties = this.getArguments();
				if(isValid(killer, properties)) {
					this.addPoints(killer, this.getPointsFromConfig());
				}
			}
		}
	}
	
	@SuppressWarnings("deprecation")
	public boolean isValid(Player p, String properties){
		if(properties.contains(",")) {
			String[] split = properties.split(",");
			for(int i=0; i<split.length; i++) {
				String property = split[i];
				String propertyType[] = property.split("=");
				var : switch (propertyType[0]) {
				case "WORLD_NAME":
					if(!p.getWorld().getName().equalsIgnoreCase(propertyType[1])){
						return false;
					}
					break var;
				case "TOOL":
					if(!p.getInventory().getItemInHand().getType().equals(Material.valueOf(propertyType[1]))) {
						return false;
					}
					break var;
				case "PERMISSION":
					if(!p.hasPermission(propertyType[1])) {
						return false;
					}
					break var;
				case "TOOL_NAME":
					if(!p.getInventory().getItemInHand().getItemMeta().getDisplayName().equalsIgnoreCase(ChatColor.translateAlternateColorCodes('&', propertyType[1]))) {
						return false;
					}
					break var;
				case "WORLDS_NAME":
					String[] separador = propertyType[1].split(";");
					List<String> worldNames = new ArrayList<>();
					for(String s : separador) {
						worldNames.add(s);
					}
					if(!worldNames.contains(p.getWorld().getName())) {
						return false;
					}
					break var;
				case "TOOLS":
					String[] separador2 = propertyType[1].split(";");
					List<String> materialNames = new ArrayList<>();
					for(String s : separador2) {
						materialNames.add(s);
					}
					if(!materialNames.contains(p.getInventory().getItemInHand().getType().toString())) {
						return false;
					}
					break var;
				case "TOOL_NAMES":
					String[] separador3 = propertyType[1].split(";");
					List<String> toolNames = new ArrayList<>();
					for(String s : separador3) {
						toolNames.add(s);
					}
					if(!toolNames.contains(p.getInventory().getItemInHand().getItemMeta().getDisplayName())) {
						return false;
					}
					break var;
				case "TOOL_CONTAIN":
					if(!p.getInventory().getItemInHand().getType().toString().contains(propertyType[1])) {
						return false;
					}
					break var;
				}

				
			}
		}else {
			if(properties.contains("=")) {
				String propertyType[] = properties.split("=");
				var : switch (propertyType[0]) {
				case "WORLD_NAME":
					if(!p.getWorld().getName().equalsIgnoreCase(propertyType[1])){
						return false;
					}
					break var;
				case "TOOL":
					if(!p.getInventory().getItemInHand().getType().equals(Material.valueOf(propertyType[1]))) {
						return false;
					}
					break var;
				case "PERMISSION":
					if(!p.hasPermission(propertyType[1])) {
						return false;
					}
					break var;
				case "TOOL_NAME":
					if(!p.getInventory().getItemInHand().getItemMeta().getDisplayName().equalsIgnoreCase(ChatColor.translateAlternateColorCodes('&', propertyType[1]))) {
						return false;
					}
					break var;
				case "WORLDS_NAME":
					String[] separador = propertyType[1].split(";");
					List<String> worldNames = new ArrayList<>();
					for(String s : separador) {
						worldNames.add(s);
					}
					if(!worldNames.contains(p.getWorld().getName())) {
						return false;
					}
					break var;
				case "TOOLS":
					String[] separador2 = propertyType[1].split(";");
					List<String> materialNames = new ArrayList<>();
					for(String s : separador2) {
						materialNames.add(s);
					}
					if(!materialNames.contains(p.getInventory().getItemInHand().getType().toString())) {
						return false;
					}
					break var;
				case "TOOL_NAMES":
					String[] separador3 = propertyType[1].split(";");
					List<String> toolNames = new ArrayList<>();
					for(String s : separador3) {
						toolNames.add(s);
					}
					if(!toolNames.contains(p.getInventory().getItemInHand().getItemMeta().getDisplayName())) {
						return false;
					}
					break var;
				case "TOOL_CONTAIN":
					if(!p.getInventory().getItemInHand().getType().toString().contains(propertyType[1])) {
						return false;
					}
					break var;
					
				}
			}
		}
		return true;
	}
	
}
