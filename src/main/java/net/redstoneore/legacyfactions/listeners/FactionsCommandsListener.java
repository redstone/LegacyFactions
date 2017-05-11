package net.redstoneore.legacyfactions.listeners;

import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;

import net.redstoneore.legacyfactions.Factions;

public class FactionsCommandsListener implements Listener {
	
	// -------------------------------------------------- //
	// INSTANCE
	// -------------------------------------------------- //
	
	private static FactionsCommandsListener i = new FactionsCommandsListener();
	public static FactionsCommandsListener get() { return i; }
	
	// -------------------------------------------------- //
	// METHODS
	// -------------------------------------------------- //

	@EventHandler(priority = EventPriority.LOW, ignoreCancelled = true)
	public void onPlayerCommandPreprocess(PlayerCommandPreprocessEvent event) {
		// If we're preventing this command ...
		if (!FactionsPlayerListener.preventCommand(event.getMessage(), event.getPlayer())) return;
		
		// ... cancel this event ...
		event.setCancelled(true);
		
		// ... and we're logging ...
		if (!Factions.get().logPlayerCommands()) return;
		
		// ... log it.
		Bukkit.getLogger().info("[PLAYER_COMMAND] " + event.getPlayer().getName() + ": " + event.getMessage());
	}

	@EventHandler(priority = EventPriority.LOW, ignoreCancelled = true)
	public void onPlayerChat(AsyncPlayerChatEvent event) {
		// If we're preventing this command ... 
		if (!Factions.get().handleCommand(event.getPlayer(), event.getMessage(), false, true)) return;
		
		// ... cancel this event ...
		event.setCancelled(true);
		
		// ... and we're logging ...
		if (!Factions.get().logPlayerCommands()) return;
		
		
		// ... log it.
		Bukkit.getLogger().info("[PLAYER_COMMAND] " + event.getPlayer().getName() + ": " + event.getMessage());
	}
	
}
