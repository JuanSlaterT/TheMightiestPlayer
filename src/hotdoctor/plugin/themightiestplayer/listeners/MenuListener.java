package hotdoctor.plugin.themightiestplayer.listeners;

import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;

import hotdoctor.plugin.themightiestplayer.Main;
import hotdoctor.plugin.themightiestplayer.utils.MenuCreator;

public class MenuListener implements Listener{
	
	public MenuListener(Main plugin) {
		this.plugin = plugin;
	}
	
	private Main plugin;
	
	@EventHandler
	public void clickMenu(InventoryClickEvent e) {
		if(e.getInventory().getHolder() instanceof MenuCreator && e.getClickedInventory() != null) {
			MenuCreator menu = (MenuCreator) e.getInventory().getHolder();
			boolean wasCancelled = e.isCancelled();
			e.setCancelled(true);
			menu.clickEvent(e);
			if (!wasCancelled && !e.isCancelled()) {
                e.setCancelled(false);
            }
		}
	}
	@EventHandler
	public void closeMenu(InventoryCloseEvent e) {
		if(e.getInventory().getHolder() instanceof MenuCreator) {
			
			MenuCreator menu = (MenuCreator) e.getInventory().getHolder();
			menu.closeEvent(e);
		}
	}

}
