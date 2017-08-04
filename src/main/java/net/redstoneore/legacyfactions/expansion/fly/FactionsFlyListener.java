package net.redstoneore.legacyfactions.expansion.fly;

import org.bukkit.Location;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.event.player.PlayerTeleportEvent.TeleportCause;

import net.redstoneore.legacyfactions.FLocation;
import net.redstoneore.legacyfactions.Lang;
import net.redstoneore.legacyfactions.entity.Conf;
import net.redstoneore.legacyfactions.entity.FPlayerColl;

public class FactionsFlyListener implements Listener {

	// -------------------------------------------------- //
	// INSTANCE
	// -------------------------------------------------- //
	
	private static FactionsFlyListener i = new FactionsFlyListener();
	public static FactionsFlyListener get() { return i; }
	
	// -------------------------------------------------- //
	// METHODS
	// -------------------------------------------------- //
	
	@EventHandler
	public void onPlayerMove(PlayerMoveEvent event) {
		if (!Conf.factionsFlyExpansionEnabled) return;
		
		// Max Y
		if (Conf.factionsFlyMaxY > 0) {
			if (event.getTo().getY() >= Conf.factionsFlyMaxY) {
				event.setTo(event.getFrom().add(0, -1, 0));
				return;
			}
		}
		
		if (!FactionsFly.canFlyHere(FPlayerColl.get(event.getPlayer()), FLocation.valueOf(event.getTo()))) {
			Location location = FactionsFly.getFloor(event.getTo());
			event.setTo(location);
		}
	}
	
	@EventHandler
	public void onPlayerEnderpearl(PlayerTeleportEvent event) {
		if (!Conf.factionsFlyExpansionEnabled) return;
		
		if (!Conf.factionsFlyNoEnderpearl) return;
		if (!event.getPlayer().isFlying()) return;
		if (event.getCause() != TeleportCause.ENDER_PEARL) return;
		
		event.setCancelled(true);
		event.getPlayer().sendMessage(Lang.EXPANSION_FACTIONS_FLY_NO_ENDERPEARL.toString());
	}
	
	@EventHandler
	public void onPlayerChorusFruit(PlayerTeleportEvent event) {
		if (!Conf.factionsFlyExpansionEnabled) return;
		
		if (!Conf.factionsFlyNoChorusFruit) return;
		if (!event.getPlayer().isFlying()) return;
		if (event.getCause() != TeleportCause.CHORUS_FRUIT) return;
		
		event.setCancelled(true);
		event.getPlayer().sendMessage(Lang.EXPANSION_FACTIONS_FLY_NO_CHORUSFRUIT.toString());
	}
	
}
