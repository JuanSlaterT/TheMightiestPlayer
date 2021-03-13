package hotdoctor.plugin.themightiestplayer.utils.databasetypes;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import hotdoctor.plugin.themightiestplayer.Main;
import hotdoctor.plugin.themightiestplayer.managers.EventManager;
import hotdoctor.plugin.themightiestplayer.utils.EventCreator;

public class MightiestDataPlayer extends Database{
	
	private Main plugin;
	public MightiestDataPlayer(String eventID, Plugin plugin) {
		super(eventID, plugin);
		this.plugin = (Main) plugin;
	}





	@Override
	public void saveData(Player p) {
		if(!this.ifPlayerHasFile(p)) {
			try {
				this.createFile(p);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		if(!this.getRAMDatabase().everythingHasData(p)) {
			this.loadData(p);
		}
			int value = this.getRAMDatabase().getPoints(p);
			boolean valor = this.getRAMDatabase().isCompletedStatus(p);
			boolean isRewarded = this.getRAMDatabase().isAlreadyRewarded(p);
			boolean isFinalReward = this.getRAMDatabase().isFinalReward(p);
			YamlConfiguration yml = this.getData(p);
			yml.set("event.stats."+this.getEventID()+".value", value);
			yml.set("event.stats."+this.getEventID()+".isCompleted", valor);
			yml.set("event.stats."+this.getEventID()+".alreadyRewarded", isRewarded);
			yml.set("event.stats."+this.getEventID()+".isFinalRewarded", isFinalReward);
			try {
				yml.save(this.getFile(p));
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			EventManager manager = EventCreator.cachedInfo.get(this.getEventID());
			if(manager.getGoalTypeEvent().equals(GoalTypes.GLOBAL)) {
				int global = this.getRAMDatabase().getGlobalPoints();
				Main.plugindata.set("events."+getEventID()+".value", global);
				try {
					Main.plugindata.save(Main.datayml);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		
	}

	@Override
	public void loadData(Player p) {
		if(!this.ifPlayerHasFile(p)) {
			try {
				this.createFile(p);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		YamlConfiguration yml = this.getData(p);
		int valor = 0;
		if(!yml.contains("event.stats."+this.getEventID()+".value")) {
			valor = 0;
			yml.set("event.stats."+this.getEventID()+".value", 0);
			try {
				yml.save(this.getFile(p));
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}else {
			valor =  yml.getInt("event.stats."+this.getEventID()+".value");
		}
		this.setPoints(p, valor, "no", 0);
		boolean valor2 = false;
		if(!yml.contains("event.stats."+this.getEventID()+".isCompleted")) {
			valor2 = false;
			yml.set("event.stats."+this.getEventID()+".isCompleted", false);
			try {
				yml.save(this.getFile(p));
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}else {
			valor2 = yml.getBoolean("event.stats."+this.getEventID()+".isCompleted");
		}
		this.setCompletedStatus(p, valor2);
		boolean rewarded = false;
		if(!yml.contains("event.stats."+this.getEventID()+".alreadyRewarded")) {
			yml.set("event.stats."+this.getEventID()+".alreadyRewarded", false);
			try {
				yml.save(this.getFile(p));
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}else {
			rewarded = yml.getBoolean("event.stats."+this.getEventID()+".alreadyRewarded");
		}
		this.setIsRewarded(p, rewarded);
		boolean finalr = false;
		if(!yml.contains("event.stats."+this.getEventID()+".isFinalRewarded")) {
			yml.set("event.stats."+this.getEventID()+".isFinalRewarded", false);
			try {
				yml.save(this.getFile(p));
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}else {
			finalr = yml.getBoolean("event.stats."+this.getEventID()+".isFinalRewarded");
		}
		this.setFinalRewarded(p, finalr);
		
	}
	
	
	
    public void createFile(Entity p) throws IOException {
    	File dir = new File(plugin.getDataFolder().getAbsolutePath(), "users-data");
    	if(!(dir.exists())) {
    		if(dir.mkdir()) {
    			// NOTHING
    		}else {
    			// NOTHING
    		}
    	}
		File data = new File(dir.getAbsolutePath(), p.getName()+".yml");
		data.createNewFile();
		if(data.exists()) {
			String fileData = "#This configuration is made by the plugin automatically, please dont use/modify these datas because you could break the process of the plugin.";
			FileOutputStream fos = new FileOutputStream(data);
			fos.write(fileData.getBytes());
			fos.flush();
			fos.close();
		}
    }
    
    public boolean ifPlayerHasFile(Entity p) {
    	File dir = new File(plugin.getDataFolder().getAbsolutePath(), "users-data");
    	File data = new File(dir.getAbsolutePath(), p.getName()+".yml");
    	if(data.exists()) {
    		return true;
    	}else {
    		return false;
    	}
    }
    
    public void deleteFile(Player p) {
    	File archivo = new File(plugin.getDataFolder().getAbsolutePath(), "users-data");
		String path = archivo.getAbsolutePath();
		File data = new File(path, p.getName()+".yml");
		if(data.exists()) {
			data.delete();
		}
    }
    
    public File getFile(Entity p) {
		File archivo = new File(plugin.getDataFolder().getAbsolutePath(), "users-data");
		String path = archivo.getAbsolutePath();
		File data = new File(path, p.getName()+".yml");
		return data;
    }
	public YamlConfiguration getData(Entity p) {
		File archivo = new File(plugin.getDataFolder().getAbsolutePath(), "users-data");
		String path = archivo.getAbsolutePath();
		File data = new File(path, p.getName()+".yml");
		YamlConfiguration c = YamlConfiguration.loadConfiguration(data);
		return c;
		
	}






}
