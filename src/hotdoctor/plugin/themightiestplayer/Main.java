package hotdoctor.plugin.themightiestplayer;

import java.io.File;

import java.io.IOException;
import java.sql.SQLException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import hotdoctor.plugin.themightiestplayer.commands.AdminCommands;
import hotdoctor.plugin.themightiestplayer.event_types.CustomEvent;
import hotdoctor.plugin.themightiestplayer.event_types.Event;
import hotdoctor.plugin.themightiestplayer.extensions.MightiestPlaceholder;
import hotdoctor.plugin.themightiestplayer.listeners.MenuListener;
import hotdoctor.plugin.themightiestplayer.managers.DateManager;
import hotdoctor.plugin.themightiestplayer.managers.EventManager;
import hotdoctor.plugin.themightiestplayer.progress.EventProgress;
import hotdoctor.plugin.themightiestplayer.utils.EventCreator;
import hotdoctor.plugin.themightiestplayer.utils.MenuCreator;
import me.clip.placeholderapi.PlaceholderAPI;
import net.md_5.bungee.api.ChatColor;

public class Main extends JavaPlugin{
	public String rutaConfig;
	public static File configyml;
	public static YamlConfiguration configuration;
	public static List<String> enabledEvents = new ArrayList<>();
	public static List<CustomEvent> eventList = new ArrayList<>();
	
	public static YamlConfiguration plugindata;
	public static File datayml;
	
	public DateManager dateManager;
	String rutaConfig2;
	
	
	
	public static List<String> enabledEventsID = new ArrayList<>();
	private MightiestPlaceholder hook;
	
	
	public EventCreator eventCreator;
	public Event event;
	public MenuCreator menu = null;
	public String version = "1.0BETA";
	
	public void onEnable() {
		configyml = new File(this.getDataFolder(), "config.yml");
		datayml = new File(this.getDataFolder(), "plugin-data.yml");
		registerConfiguration();
		this.registerData();
		configuration = YamlConfiguration.loadConfiguration(configyml);
		plugindata = YamlConfiguration.loadConfiguration(datayml);
		registerAdminCommands();
		menu = new MenuCreator(this);
		dateManager = new DateManager(this);
		event = new Event(this);
		try {
			eventCreator = new EventCreator(this);
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		registerMenuListeners();
		
		if (Bukkit.getPluginManager().getPlugin("PlaceholderAPI") != null) {
			hook = new MightiestPlaceholder();
			hook.register();
		}
		
	}
	
	public void onDisable() {
		for(String s : enabledEventsID) {
			EventManager manager = EventCreator.cachedInfo.get(s);
			if(manager.getEventProgress().equals(EventProgress.STARTED)) {
				for(Player p : Bukkit.getOnlinePlayers()) {
					manager.getDatabase().saveData(p);
				}
			}
		}
		Bukkit.broadcastMessage(fancy("console", "&8[&6TheMightiestPlayer&8] &9&lCONFIG PROGRESS&6 >> &bFinishing events..."));
		for(String id : Main.enabledEventsID) {
			EventManager manager = EventCreator.cachedInfo.get(id);
			manager.setEventProgress(EventProgress.FINISHED);
			
		}
		if(eventCreator.mysql != null) {
			Bukkit.getConsoleSender().sendMessage(fancy("console", "&8[&6TheMightiestPlayer&8] &9&lCONFIG PROGRESS&6 >> &bDisconnecting from MySQL Database..."));
			try {
				eventCreator.mysql.Disconnect();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		if (Bukkit.getPluginManager().getPlugin("PlaceholderAPI") != null) {
			Bukkit.broadcastMessage(fancy("console", "&8[&6TheMightiestPlayer&8] &9&lCONFIG PROGRESS&6 >> &cUnregistering placeholders..."));
			hook.unregister();
		}
		Bukkit.broadcastMessage(fancy("console", "&8[&6TheMightiestPlayer&8] &9&lCONFIG PROGRESS&6 >> &cPlugin disabled correctly.!"));
		
	}
	@SuppressWarnings("deprecation")
	public String fancy(String p, String message) {
		message = ChatColor.translateAlternateColorCodes('&', message);
		if(!p.equalsIgnoreCase("console")) {
			  if (Bukkit.getPluginManager().getPlugin("PlaceholderAPI") != null) {
				  message = PlaceholderAPI.setPlaceholders(Bukkit.getOfflinePlayer(p), message);
			  }
		}
        return message;
	}
	public void sendError(String message){
		Bukkit.getConsoleSender().sendMessage(fancy("console", message));
	}

	
	
	
	
	
	
	
	
	
	
	public void registerMenuListeners() {
		Bukkit.getPluginManager().registerEvents(new MenuListener(this), this);
	}
	
	public void registerAdminCommands() {
		this.getCommand("TheMightiestPlayer").setExecutor(new AdminCommands(this));
	}
	
	
	
	
	
	
	
	
	
	
	public void registerData() {
		rutaConfig2 = datayml.getAbsolutePath();
		if(!(datayml.exists())) {
			setDataDefaults();
			
		}
	}
	public void setDataDefaults() {
		this.saveResource("plugin-data.yml", true);
	}
	public void saveData() {
		try {
			plugindata.save(datayml);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	
	
	public void registerConfiguration() {
		rutaConfig = configyml.getAbsolutePath();
		if(!(configyml.exists())) {
			setConfigurationDefaults();
			
		}
	}
	public void setConfigurationDefaults() {
		this.saveResource("config.yml", true);
	}
	public void saveConfiguration() {
		try {
			configuration.save(configyml);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
