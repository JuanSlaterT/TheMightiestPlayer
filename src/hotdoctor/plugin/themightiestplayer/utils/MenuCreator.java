package hotdoctor.plugin.themightiestplayer.utils;


import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.Sound;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scheduler.BukkitTask;

import hotdoctor.plugin.themightiestplayer.Main;
import hotdoctor.plugin.themightiestplayer.managers.DateManager;
import hotdoctor.plugin.themightiestplayer.managers.EventManager;
import hotdoctor.plugin.themightiestplayer.progress.EventProgress;
import hotdoctor.plugin.themightiestplayer.utils.databasetypes.GoalTypes;
import net.md_5.bungee.api.ChatColor;

public class MenuCreator implements InventoryHolder{
	public boolean Error = false;
	public List<Integer> usingSlots = new ArrayList<>();
	private HashMap<Integer, String> pathIDByInt = new HashMap<>();
	private HashMap<Player, BukkitTask> taskID = new HashMap<>();
	private HashMap<Player, BukkitTask> taskID2 = new HashMap<>();
	private Inventory inventory;
	private String title;
	
	public void openMenu(Player p) {
		Inventory clonedInventory = Bukkit.createInventory(this, inventory.getSize(), this.title);
		clonedInventory.setContents(inventory.getContents());
		refresh(p, clonedInventory);
		
		p.openInventory(clonedInventory);
		
		
	}
	
	public void closeMenu(Player p) {
		if(taskID.containsKey(p)) {
			Bukkit.getScheduler().cancelTask(taskID.get(p).getTaskId());
			taskID.remove(p);
		}
	}
	private void performAction(Player p, String action) {
		String[] split = action.split("\\|");
		String accion = split[0];
		String proceso = plugin.fancy(p.getName(), split[1]);
		switch(accion) {
		case "CONSOLE":
			Bukkit.dispatchCommand(Bukkit.getConsoleSender(), proceso);
			break;
		case "MESSAGE":
			p.sendMessage(proceso);
			break;
		case "PLAYER_COMMAND":
			p.performCommand(proceso);
			break;
		case "SOUND":
			p.playSound(p.getLocation(), Sound.valueOf(proceso), 3, 3);
			break;
		case "CLOSE":
			p.closeInventory();
			break;
		}
		
			
		
	}
	public void closeEvent(InventoryCloseEvent e) {
		closeMenu((Player) e.getPlayer());
	}
	public void clickEvent(InventoryClickEvent e) {
		if(usingSlots.contains(e.getSlot())) {
			String path = pathIDByInt.get(e.getSlot());
			Player p = (Player) e.getWhoClicked();
			if(Main.configuration.contains(path+".ACTIONS") && Main.configuration.isList(path+".ACTIONS")) {
				String[] split = path.split("\\.");
				if(split[0].equalsIgnoreCase("gui-settings")) {
					for(String action : Main.configuration.getStringList(path+".ACTIONS")) {
						performAction(p, action);
					}
				}
			
			}else {
				String[] split = path.split("\\.");
				
				if(split[0].equalsIgnoreCase("events")) {
					String eventID = split[1];
					if(EventCreator.cachedInfo.containsKey(eventID)) {
						EventManager manager = EventCreator.cachedInfo.get(eventID);
						switch(manager.getEventProgress()) {
						case DISABLED:
							break;
						case FINISHED:
							if(manager.isFinalRewardEnabled()) {
								if(!manager.getDatabase().getFinalRewarded(p)) {
									if(manager.getGoalTypeEvent().equals(GoalTypes.INDIVIDUAL)) {
										manager.getDatabase().setFinalRewarded(p, true);
										for(String action : Main.configuration.getStringList(path+".finish-event-reward.ACTIONS")) {
											performAction(p, action);
										}
									}else if(manager.getGoalTypeEvent().equals(GoalTypes.GLOBAL)){
										if(manager.getMAXGlobalPoints() <= manager.getDatabase().getGlobalPoints()) {
											manager.getDatabase().setFinalRewarded(p, true);
											for(String action : Main.configuration.getStringList(path+".finish-event-reward.ACTIONS")) {
												performAction(p, action);
											}
										}
									}
								}
							}
						case NOT_STARTED:
							break;
						case STARTED:
							if(manager.isMinPointsEnabled()) {
								if(manager.getDatabase().getCompletedStatus(p)) {
									if(!manager.getDatabase().getIsRewarded(p)) {
										manager.getDatabase().setIsRewarded(p, true);
										for(String action : Main.configuration.getStringList(path+".min-points-reward.ACTIONS")) {
											performAction(p, action);
										}
									}
								}
							}
						
						}
					}
				}
			}
			
		}
		e.setCancelled(true);
	}
	
	public void refresh(Player p, Inventory cloned) {
		BukkitTask ID;
		ID = Bukkit.getScheduler().runTaskTimerAsynchronously(plugin, () -> {
			for(int i : usingSlots) {
				String path = pathIDByInt.get(i);
				String[] split = path.split("\\.");
				if(split[0].equalsIgnoreCase("gui-settings")) {
					ItemStack item = cloned.getItem(i);
					ItemMeta meta = item.getItemMeta();
					if(meta.hasDisplayName()) {
						meta.setDisplayName(plugin.fancy(p.getName(), meta.getDisplayName()));
					}
					if(meta.hasLore()) {
						List<String> nuevoLore = new ArrayList<>();
						for(String string : meta.getLore()) {
							nuevoLore.add(plugin.fancy(p.getName(), string));
						}
						meta.setLore(nuevoLore);
					}
					item.setItemMeta(meta);
					cloned.setItem(i, item);
				}else if(split[0].equalsIgnoreCase("events")) {
					String eventID = split[1];
					EventManager manager = EventCreator.cachedInfo.get(eventID);
					String newPath = split[0]+"."+split[1]+".gui-specifications";
					switch(manager.getEventProgress()) {
					case DISABLED:
						break;
					case FINISHED:
						if(manager.getGoalTypeEvent().equals(GoalTypes.INDIVIDUAL)) {
							if(manager.getDatabase().getCompletedStatus(p)) {
								newPath = newPath+".ended-completed";
							}else {
								newPath = newPath+".ended-not-completed";
							}
						}else if(manager.getGoalTypeEvent().equals(GoalTypes.GLOBAL)) {
							if(manager.getMAXGlobalPoints() <= manager.getDatabase().getGlobalPoints()) {
								newPath = newPath+".ended-completed";
							}else {
								newPath = newPath+".ended-not-completed";
							}
						}
						
						break;
					case NOT_STARTED:
						newPath = newPath+".not-started-event";
						break;
					case STARTED:
						if(manager.getDatabase().getCompletedStatus(p)) {
							newPath = newPath+".started-completed";
						}else {
							newPath = newPath+".started-but-not-completed";
						}
						break;
					}
					ItemStack item = getItemModel(p, newPath);
					cloned.setItem(i, item);
					
				}
				
			}
		}, 0, 20);
		if(!taskID.containsKey(p)) {
			taskID.put(p, ID);
		}else {
			taskID.remove(p);
			taskID.put(p, ID);
		}
		

	}
	
	public MenuCreator(Main plugin) {
		this.plugin = plugin;
		plugin.sendError("&8[&6TheMightiestPlayer&8] &9&lCONFIG PROGRESS&6 >> &bStarting the creation of menu for the plugin...");
		plugin.sendError("&8[&6TheMightiestPlayer&8] &9&lCONFIG PROGRESS&6 >> &bGetting title...");
		String title = ChatColor.translateAlternateColorCodes('&', Main.configuration.getString("gui-settings.title"));
		plugin.sendError("&8[&6TheMightiestPlayer&8] &9&lCONFIG PROGRESS&6 >> &bGetting rows...");
		int rows = 0;
		if(this.isInteger(Main.configuration.getString("gui-settings.rows"))){
			rows = Main.configuration.getInt("gui-settings.rows");
			if(rows > 6 || rows < 0 || rows == 0) {
				plugin.sendError("&8[&6TheMightiestPlayer&8] &c&lCONFIG ERROR&6 >> &bError while loading the menu system, Number of rows can not be more than 6, lower than 0 and/or equals to 0.");
				Error = true;
			}
		}else {
			plugin.sendError("&8[&6TheMightiestPlayer&8] &c&lCONFIG ERROR&6 >> &bError while loading the menu system, Please set a valid number of rows.");
			Error = true;
		}
		if(Error) {
			plugin.sendError("&8[&6TheMightiestPlayer&8] &4&lCONFIGURATION FAILURE&6 >> &bPlugin has not beed enabled totally, please fix the problems first for make this plugin works perfectly.");
			return;
		}
		plugin.sendError("&8[&6TheMightiestPlayer&8] &9&lCONFIG PROGRESS&6 >> &bCreating menu...");
		inventory = Bukkit.createInventory(this, rows*9, title);
		this.title = title;
		plugin.sendError("&8[&6TheMightiestPlayer&8] &9&lCONFIG PROGRESS&6 >> &bMenu created, Loading items from gui-settings...");
		for(String id : Main.configuration.getConfigurationSection("gui-settings.items").getKeys(false)) {
			plugin.sendError("&8[&6TheMightiestPlayer&8] &9&lCONFIG PROGRESS&6 >> &bGenerating item id &9"+id);
			generateMenu("gui-settings.items."+id, id, inventory);
		}
		
		
		
		
		
	}
	
	private Main plugin;
	
	
	@SuppressWarnings("deprecation")
	public void addEventToMenu(String id) {
		List<Integer> slots = new ArrayList<>();
		String oldpath = "events."+id+".gui-specifications";
		if(Main.configuration.contains(oldpath+".SLOTS") && Main.configuration.contains(oldpath+".SLOT")) {
			plugin.sendError("&8[&6TheMightiestPlayer&8] &c&lCONFIG ERROR&6 >> &bError while loading event-item &c"+id+"&b You can not have SLOT and SLOTS path in a same configuration.");
		}else {
			if(Main.configuration.contains(oldpath+".SLOTS")) {
				if(Main.configuration.isList(oldpath+".SLOTS")) {
					for(String integer : Main.configuration.getStringList(oldpath+".SLOTS")) {
						if(isInteger(integer)) {
							if(!slots.contains(Integer.parseInt(integer))) {
								slots.add(Integer.parseInt(integer));
							}else {
								plugin.sendError("&8[&6TheMightiestPlayer&8] &e&lCONFIG WARNING&6 >> &bWarning while loading event-item &c"+id+"&b You have repeated SLOTS in a same configuration, will just skip...");
								continue;
							}
						}else {
							plugin.sendError("&8[&6TheMightiestPlayer&8] &c&lCONFIG ERROR&6 >> &bError while loading event-item &c"+id+"&b The value &c"+integer+" &bdoes not count as a number for be setted in a gui SLOT.");
							return;
						}
					}
				}else {
					plugin.sendError("&8[&6TheMightiestPlayer&8] &c&lCONFIG ERROR&6 >> &bError while loading event-item &c"+id+"&b SLOTS option must be a List, not a String.");
					return;
				}
			}else if(Main.configuration.contains(oldpath+".SLOT")) {
				if(Main.configuration.isList(oldpath+".SLOT")) {
					plugin.sendError("&8[&6TheMightiestPlayer&8] &c&lCONFIG ERROR&6 >> &bError while loading event-item &c"+id+"&b SLOT option cannot be a List.");
					return;
				}
				String integer = Main.configuration.getString(oldpath+".SLOT");
				if(isInteger(integer)) {
					slots.add(Integer.parseInt(integer));
				}else {
					plugin.sendError("&8[&6TheMightiestPlayer&8] &c&lCONFIG ERROR&6 >> &bError while loading event-item &c"+id+"&b The value &c"+integer+" &bdoes not count as a number for be setted in a gui SLOT.");
					return;
				}
				
			}else {
				plugin.sendError("&8[&6TheMightiestPlayer&8] &c&lCONFIG ERROR&6 >> &bError while loading event-item &c"+id+"&b There should be a slot/slots number for set this item in gui.");
			}
		}
		
		EventManager manager = EventCreator.cachedInfo.get(id);
		String path = oldpath;
		switch(manager.getEventProgress()) {
		case DISABLED:
			break;
		case FINISHED:
			path = path+".ended-not-completed";
			break;
		case NOT_STARTED:
			path = path+".not-started-event";
			break;
		case STARTED:
			path = path+".started-but-not-completed";
			break;
				
		}
		Material material = null;
		if(Main.configuration.contains(path+".MATERIAL")) {
			if(!Main.configuration.isList(path+".MATERIAL")) {
				material = Material.valueOf(Main.configuration.getString(path+".MATERIAL").toUpperCase());
			}
		}else {
			plugin.sendError("&8[&6TheMightiestPlayer&8] &c&lCONFIG ERROR&6 >> &bError while loading event-item &c"+id+"&b Item must have a material type.");
			
		}
		ItemStack item = new ItemStack(material);
		if(Main.configuration.contains(path+".AMOUNT") && Main.configuration.isInt(path+".AMOUNT")) {
			int amount = Main.configuration.getInt(path+".AMOUNT");
			item.setAmount(amount);
		}
		if(Main.configuration.contains(path+".DATA") && Main.configuration.isInt(path+".DATA")) {
			int amount = Main.configuration.getInt(path+".DATA");
			item.getData().setData((byte) amount); 
		}
		ItemMeta meta = item.getItemMeta();
		if(Main.configuration.contains(path+".ITEM_NAME") && Main.configuration.isString(path+".ITEM_NAME")) {
			String name = Main.configuration.getString(path+".ITEM_NAME");
			meta.setDisplayName(plugin.fancy("console", name));
		}
		
		if(Main.configuration.contains(path+".LORE") && Main.configuration.isList(path+".LORE")) {
			List<String> nuevoLore = new ArrayList<>();
			for(String lore : Main.configuration.getStringList(path+".LORE")) {
				nuevoLore.add(plugin.fancy("console", lore));
			}
			meta.setLore(nuevoLore);
		}
		if(Main.configuration.contains(path+".ENCHANTS") && Main.configuration.isList(path+".ENCHANTS")) {
			for(String enchantment : Main.configuration.getStringList(path+".ENCHANTS")) {
				String[] split = enchantment.split(":");
				Enchantment enchant = null;
				if(!Bukkit.getBukkitVersion().contains("1.8")) {
					enchant = Enchantment.getByKey(NamespacedKey.minecraft(split[0]));
				}else {
					enchant = Enchantment.getByName(split[0].toUpperCase());
				}
				meta.addEnchant(enchant, Integer.valueOf(split[1]), true);
				
				
				
				
			}
		}
		item.setItemMeta(meta);
		for(Integer slot : slots) {
			if(inventory.getItem(slot) == null || inventory.getItem(slot).getType() == Material.AIR) {
				inventory.setItem(slot, item);
				usingSlots.add(slot);
				this.pathIDByInt.put(slot, "events."+id+".event-specifications");
				plugin.sendError("&8[&6TheMightiestPlayer&8] &e&lCONFIG &3&lSUCESS&6 >> &bSetted event-item &6"+id+" &bin the slot &9"+slot+" &bwithout problems.");
			}else {
				plugin.sendError("&8[&6TheMightiestPlayer&8] &e&lCONFIG WARNING&6 >> &bWarning while loading item &c"+id+"&b You already have setted an item in the slot &6"+slot+" &bI will just skip this...");
			}
		}
		
	}
	@SuppressWarnings("deprecation")
	public void generateMenu(String path, String id, Inventory menu) {
		List<Integer> slots = new ArrayList<>();
		if(Main.configuration.contains(path+".SLOTS") && Main.configuration.contains(path+".SLOT")) {
			plugin.sendError("&8[&6TheMightiestPlayer&8] &c&lCONFIG ERROR&6 >> &bError while loading item &c"+id+"&b You can not have SLOT and SLOTS path in a same configuration.");
			return;
		}else {
			if(Main.configuration.contains(path+".SLOTS")) {
				if(Main.configuration.isList(path+".SLOTS")) {
					for(String integer : Main.configuration.getStringList(path+".SLOTS")) {
						if(isInteger(integer)) {
							if(!slots.contains(Integer.parseInt(integer))) {
								slots.add(Integer.parseInt(integer));
							}else {
								plugin.sendError("&8[&6TheMightiestPlayer&8] &e&lCONFIG WARNING&6 >> &bWarning while loading item &c"+id+"&b You have repeated SLOTS in a same configuration, will just skip...");
								continue;
							}
						}else {
							plugin.sendError("&8[&6TheMightiestPlayer&8] &c&lCONFIG ERROR&6 >> &bError while loading item &c"+id+"&b The value &c"+integer+" &bdoes not count as a number for be setted in a gui SLOT.");
							return;
						}
					}
				}else {
					plugin.sendError("&8[&6TheMightiestPlayer&8] &c&lCONFIG ERROR&6 >> &bError while loading item &c"+id+"&b SLOTS option must be a List, not a String.");
					return;
				}
			}else if(Main.configuration.contains(path+".SLOT")) {
				if(Main.configuration.isList(path+".SLOT")) {
					plugin.sendError("&8[&6TheMightiestPlayer&8] &c&lCONFIG ERROR&6 >> &bError while loading item &c"+id+"&b SLOT option cannot be a List.");
					return;
				}
				String integer = Main.configuration.getString(path+".SLOT");
				if(isInteger(integer)) {
					slots.add(Integer.parseInt(integer));
				}else {
					plugin.sendError("&8[&6TheMightiestPlayer&8] &c&lCONFIG ERROR&6 >> &bError while loading item &c"+id+"&b The value &c"+integer+" &bdoes not count as a number for be setted in a gui SLOT.");
					return;
				}
				
			}else {
				plugin.sendError("&8[&6TheMightiestPlayer&8] &c&lCONFIG ERROR&6 >> &bError while loading item &c"+id+"&b There should be a slot/slots number for set this item in gui.");
			}
			Material material = null;
			if(Main.configuration.contains(path+".MATERIAL")) {
				if(!Main.configuration.isList(path+".MATERIAL")) {
					material = Material.valueOf(Main.configuration.getString(path+".MATERIAL").toUpperCase());
				}
			}else {
				plugin.sendError("&8[&6TheMightiestPlayer&8] &c&lCONFIG ERROR&6 >> &bError while loading item &c"+id+"&b Item must have a material type.");
				
			}
			ItemStack item = new ItemStack(material);
			if(Main.configuration.contains(path+".AMOUNT") && Main.configuration.isInt(path+".AMOUNT")) {
				int amount = Main.configuration.getInt(path+".AMOUNT");
				item.setAmount(amount);
			}
			if(Main.configuration.contains(path+".DATA") && Main.configuration.isInt(path+".DATA")) {
				int amount = Main.configuration.getInt(path+".DATA");
				item.getData().setData((byte) amount); 
			}
			ItemMeta meta = item.getItemMeta();
			if(Main.configuration.contains(path+".ITEM_NAME") && Main.configuration.isString(path+".ITEM_NAME")) {
				String name = Main.configuration.getString(path+".ITEM_NAME");
				meta.setDisplayName(plugin.fancy("console", name));
			}
			if(Main.configuration.contains(path+".LORE") && Main.configuration.isList(path+".LORE")) {
				List<String> nuevoLore = new ArrayList<>();
				for(String lore : Main.configuration.getStringList(path+".LORE")) {
					nuevoLore.add(plugin.fancy("console", lore));
				}
				meta.setLore(nuevoLore);
			}
			if(Main.configuration.contains(path+".ENCHANTS") && Main.configuration.isList(path+".ENCHANTS")) {
				for(String enchantment : Main.configuration.getStringList(path+".ENCHANTS")) {
					String[] split = enchantment.split(":");
					Enchantment enchant = null;
					if(!Bukkit.getBukkitVersion().contains("1.8")) {
						enchant = Enchantment.getByKey(NamespacedKey.minecraft(split[0]));
					}else {
						enchant = Enchantment.getByName(split[0].toUpperCase());
					}
					meta.addEnchant(enchant, Integer.valueOf(split[1]), true);
					
					
					
					
				}
			}
			item.setItemMeta(meta);
			for(Integer slot : slots) {
				if(menu.getItem(slot) == null || menu.getItem(slot).getType() == Material.AIR) {
					menu.setItem(slot, item);
					usingSlots.add(slot);
					this.pathIDByInt.put(slot, path);
					plugin.sendError("&8[&6TheMightiestPlayer&8] &e&lCONFIG &3&lSUCESS&6 >> &bSetted item &6"+id+" &bin the slot &9"+slot+" &bwithout problems.");
				}else {
					plugin.sendError("&8[&6TheMightiestPlayer&8] &e&lCONFIG WARNING&6 >> &bWarning while loading item &c"+id+"&b You already have setted an item in the slot &6"+slot+" &bI will just skip this...");
				}
			}
			
		}
		
		
		
		return;
		
	}
	public String replacer(Player p, String text, String eventID) {
		String toReturn = text;
		EventManager manager = EventCreator.cachedInfo.get(eventID);
		toReturn = toReturn.replace("%tmp-player-value%", ""+manager.getDatabase().getPoints(p));
		try {
			toReturn = toReturn.replace("%date-starts%", plugin.dateManager.getRemainingTime(DateManager.getDate(manager.getStartEventDate())));
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		try {
			toReturn = toReturn.replace("%date-ends%", plugin.dateManager.getRemainingTime(DateManager.getDate(manager.getEndEventDate())));
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		toReturn = toReturn.replace("%tmp-global-value%", ""+manager.getDatabase().getGlobalPoints());
		int value = manager.getDatabase().getGlobalPoints();
		int max = manager.getMAXGlobalPoints();
		if(max <= value) {
			toReturn = toReturn.replace("%tmp-global-value-limited%", ""+manager.getMAXGlobalPoints());
		}else {
			toReturn = toReturn.replace("%tmp-global-value-limited%", ""+manager.getDatabase().getGlobalPoints());
		}
		
		return toReturn;
	}
	
	@SuppressWarnings("deprecation")
	public ItemStack getItemModel(Player p, String path) {
		String[] separator = path.split("\\.");
		String id = separator[1];
		Material material = null;
		if(Main.configuration.contains(path+".MATERIAL")) {
			if(!Main.configuration.isList(path+".MATERIAL")) {
				material = Material.valueOf(Main.configuration.getString(path+".MATERIAL").toUpperCase());
			}
		}else {
			plugin.sendError("&8[&6TheMightiestPlayer&8] &c&lCONFIG ERROR&6 >> &bError while loading event-item &c"+id+"&b Item must have a material type.");
			
		}
		ItemStack item = new ItemStack(material);
		if(Main.configuration.contains(path+".AMOUNT") && Main.configuration.isInt(path+".AMOUNT")) {
			int amount = Main.configuration.getInt(path+".AMOUNT");
			item.setAmount(amount);
		}
		if(Main.configuration.contains(path+".DATA") && Main.configuration.isInt(path+".DATA")) {
			int amount = Main.configuration.getInt(path+".DATA");
			item.getData().setData((byte) amount); 
		}
		ItemMeta meta = item.getItemMeta();
		if(Main.configuration.contains(path+".ITEM_NAME") && Main.configuration.isString(path+".ITEM_NAME")) {
			String name = Main.configuration.getString(path+".ITEM_NAME");
			name = replacer(p, name, id);
			meta.setDisplayName(plugin.fancy(p.getName(), name));
		}
		if(Main.configuration.contains(path+".LORE") && Main.configuration.isList(path+".LORE")) {
			List<String> nuevoLore = new ArrayList<>();
			for(String lore : Main.configuration.getStringList(path+".LORE")) {
				lore = replacer(p, lore, id);
				nuevoLore.add(plugin.fancy(p.getName(), lore));
			}
			meta.setLore(nuevoLore);
		}
		if(Main.configuration.contains(path+".ENCHANTS") && Main.configuration.isList(path+".ENCHANTS")) {
			for(String enchantment : Main.configuration.getStringList(path+".ENCHANTS")) {
				String[] split = enchantment.split(":");
				Enchantment enchant = null;
				if(!Bukkit.getBukkitVersion().contains("1.8")) {
					enchant = Enchantment.getByKey(NamespacedKey.minecraft(split[0]));
				}else {
					enchant = Enchantment.getByName(split[0].toUpperCase());
				
				}
				meta.addEnchant(enchant, Integer.valueOf(split[1]), true);
				
				
				
				
			}
		}
		item.setItemMeta(meta);
		return item;
	}
	
	public boolean isInteger(String s) {
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

	@Override
	public Inventory getInventory() {
		// TODO Auto-generated method stub
		return null;
	}

	
}
