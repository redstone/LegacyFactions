package net.redstoneore.legacyfactions.expansion.fly;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;

import net.redstoneore.legacyfactions.entity.FPlayer;
import net.redstoneore.legacyfactions.entity.FPlayerColl;

public class FactionsFlyCommand implements Listener {

	private static FactionsFlyCommand i = new FactionsFlyCommand();
	public static FactionsFlyCommand get() { return i; }
	
	@EventHandler
	public void onCommand(PlayerCommandPreprocessEvent event) {
		// Find command to take over
		// TODO: add `/fly auto` for auto flight
		String command = event.getMessage().trim().toLowerCase();
		if (command.contains(" ")) {
			command = command.split(" ")[0];
		}
		
		// Check if this is a fly command.
		if (command != "fly") return;
		
		// It is, cancel the event.
		event.setCancelled(true);
		
		Player player = event.getPlayer();
		FPlayer fplayer = FPlayerColl.get(player);
		
		if (!player.getAllowFlight()) {
			// If they aren't already flying, get them flying
			
			// TODO: add a check for relations in radius
			
			player.setAllowFlight(true);
			player.setFlying(true);
		} else {
			// Cancel flight, teleport to the ground
			player.setFlying(false);
			player.setAllowFlight(false);
			
			Location floor = FactionsFly.getFloor(player.getLocation());
			player.teleport(floor);
		}
	}
	
}
