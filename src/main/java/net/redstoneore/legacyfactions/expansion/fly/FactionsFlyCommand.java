package net.redstoneore.legacyfactions.expansion.fly;

import java.util.UUID;

import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;

import net.redstoneore.legacyfactions.Factions;
import net.redstoneore.legacyfactions.entity.FPlayer;
import net.redstoneore.legacyfactions.entity.FPlayerColl;
import net.redstoneore.legacyfactions.lang.Lang;
import net.redstoneore.legacyfactions.locality.Locality;
import net.redstoneore.legacyfactions.util.TextUtil;

public class FactionsFlyCommand implements Listener {

	// -------------------------------------------------- //
	// INSTANCE
	// -------------------------------------------------- // 
	
	private static FactionsFlyCommand i = new FactionsFlyCommand();
	public static FactionsFlyCommand get() { return i; }
		
	// -------------------------------------------------- //
	// METHODS
	// -------------------------------------------------- // 
	
	@EventHandler
	public void processFlyCommand(PlayerCommandPreprocessEvent event) {
		// Find command to take over
		// TODO: add `/fly auto` for auto flight
		String command = event.getMessage().trim().toLowerCase();
		if (command.contains(" ")) {
			command = command.split(" ")[0];
		}
		
		Factions.get().debug(event.getMessage());
		Factions.get().debug(command);
		
		// Check if this is a fly command.
		if (!command.equalsIgnoreCase("/fly")) return;
		
		// It is, cancel the event.
		event.setCancelled(true);
		
		Player player = event.getPlayer();
		FPlayer fplayer = FPlayerColl.get(player);
		
		if (!player.getAllowFlight()) {
			// If they aren't already flying, get them flying
			if (!FactionsFly.canFlyHere(fplayer, Locality.of(player.getLocation()), true)) {
				fplayer.sendMessage(TextUtil.get().parse(Lang.EXPANSION_FACTIONSFLY_NOT_HERE.toString()));
			} else {
				// TODO: add a check for relations in radius
				player.setAllowFlight(true);
				player.setFlying(true);
				
				fplayer.sendMessage(TextUtil.get().parse(Lang.EXPANSION_FACTIONSFLY_ENABLED.toString()));
			}
		} else {
			// Just cancel
			FactionsFly.get().cancelFlightFor(fplayer);
		}
	}
	
	@EventHandler
	public void blockFirstFallDamage(EntityDamageEvent event) {
		if (event.getEntityType() != EntityType.PLAYER) return;
		
		Player player = (Player) event.getEntity();
		UUID playerUuid = player.getUniqueId();
		
		if (!FactionsFly.get().isFalling(playerUuid)) return;
		
		FactionsFly.get().removeFalling(playerUuid);
		
		event.setCancelled(true);
	}
	
}
