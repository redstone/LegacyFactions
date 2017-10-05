package net.redstoneore.legacyfactions.expansion.fly;

import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.scheduler.BukkitRunnable;

import net.redstoneore.legacyfactions.Factions;
import net.redstoneore.legacyfactions.config.Config;
import net.redstoneore.legacyfactions.entity.FPlayer;
import net.redstoneore.legacyfactions.entity.FPlayerColl;
import net.redstoneore.legacyfactions.lang.Lang;
import net.redstoneore.legacyfactions.locality.Locality;
import net.redstoneore.legacyfactions.util.cross.CrossTeleportCause;

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
		if (!Config.expansionFactionsFly.enabled) return;
		if (event.getPlayer().getGameMode() == GameMode.CREATIVE) return;
		
		FPlayer fplayer = FPlayerColl.get(event.getPlayer());
		
		if (!fplayer.getPlayer().isFlying() && FactionsFly.get().isFalling(fplayer.getPlayer().getUniqueId())) {
			if (event.getTo().getBlock().getType() != Material.AIR) {
				// clean up in a few seconds
				new BukkitRunnable() {
					@Override
					public void run() {
						FactionsFly.get().removeFalling(fplayer.getPlayer().getUniqueId());
					}
				}.runTaskLater(Factions.get(), 20 * 3);
			}
		}
		
		// Max Y
		if (Config.expansionFactionsFly.maxY > 0) {
			if (event.getTo().getY() >= Config.expansionFactionsFly.maxY) {
				event.setTo(event.getFrom().add(0, -1, 0));
				return;
			}
		}
		
		if (!FactionsFly.canFlyHere(fplayer, Locality.of(event.getTo()))) {
			FactionsFly.get().cancelFlightFor(fplayer);
		}
	}
	
	@EventHandler
	public void onPlayerEnderpearl(PlayerTeleportEvent event) {
		if (!Config.expansionFactionsFly.enabled) return;
		
		if (!Config.expansionFactionsFly.disableEnderpearlWhileFlying) return;
		if (!event.getPlayer().isFlying()) return;
    
		// use cross teleport to support all teleport causes
		if (CrossTeleportCause.get(event.getCause()) != CrossTeleportCause.ENDER_PEARL) return; 
		
		event.setCancelled(true);
		event.getPlayer().sendMessage(Lang.EXPANSION_FACTIONSFLY_NO_ENDERPEARL.toString());
	}
	
	@EventHandler
	public void onPlayerChorusFruit(PlayerTeleportEvent event) {
		if (!Config.expansionFactionsFly.enabled) return;
		
		if (!Config.expansionFactionsFly.disableChorusFruitWhileFlying) return;
		if (!event.getPlayer().isFlying()) return;
    
		// use cross teleport to support all teleport causes
		if (CrossTeleportCause.get(event.getCause()) != CrossTeleportCause.CHORUS_FRUIT) return;
		
		event.setCancelled(true);
		event.getPlayer().sendMessage(Lang.EXPANSION_FACTIONSFLY_NO_CHORUSFRUIT.toString());
	}
	
}
