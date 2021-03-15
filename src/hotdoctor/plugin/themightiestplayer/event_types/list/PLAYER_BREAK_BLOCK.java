package hotdoctor.plugin.themightiestplayer.event_types.list;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.plugin.Plugin;

import hotdoctor.plugin.themightiestplayer.event_types.CustomEvent;
import net.md_5.bungee.api.ChatColor;

public class PLAYER_BREAK_BLOCK extends CustomEvent implements Listener{

	public PLAYER_BREAK_BLOCK(Plugin plugin) {
		super(plugin, "PLAYER_BREAK_BLOCK", "Executed when player breaks a block");
		// TODO Auto-generated constructor stub
	}

	@Override
	public void onEnable() {
		Bukkit.getServer().getPluginManager().registerEvents(this, this.getPlugin());
	}
	
	@EventHandler
	public void romper(BlockBreakEvent e) {
		if(this.isEventActive()) {
			Player p = e.getPlayer();
			String properties = this.getArguments();
			if(this.isValid(p, properties, e.getBlock())) {
				this.addPoints(p, getPointsFromConfig());
			}
			
			
		}
	}
	
	
	@SuppressWarnings("deprecation")
	public boolean isValid(Player p, String properties, Block e){
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
				case "BLOCK_TYPE":
					if(!e.getType().equals(Material.valueOf(propertyType[1]))) {
						return false;
					}
					break var;
				case "BLOCK_TYPES":
					String[] separador4 = propertyType[1].split(";");
					List<String> blockTypes = new ArrayList<>();
					for(String s : separador4) {
						blockTypes.add(s);
					}
					if(!blockTypes.contains(e.getType().toString())) {
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
				case "BLOCK_TYPE":
					if(!e.getType().equals(Material.valueOf(propertyType[1]))) {
						return false;
					}
					break var;
				case "BLOCK_TYPES":
					String[] separador4 = propertyType[1].split(";");
					List<String> blockTypes = new ArrayList<>();
					for(String s : separador4) {
						blockTypes.add(s);
					}
					if(!blockTypes.contains(e.getType().toString())) {
						return false;
					}
					break var;
				case "TOOL_CONTAIN":
					if(!p.getInventory().getItemInHand().getType().toString().contains(propertyType[1])) {
						return false;
					}
					break var;
				case "BLOCK_CONTAIN":
					if(!e.getType().toString().contains(propertyType[1])) {
						return false;
						
					}
					break var;
				case "BLOCK_CONTAINS":
					String[] separador5 = propertyType[1].split(";");
					List<String> blockContains = new ArrayList<>();
					for(String s : separador5) {
						blockContains.add(s);
					}
					if(!blockContains.contains(e.getType().toString())) {
						return false;
					}
					break var;
				
					
				}
			}
		}
		return true;
	}

}
