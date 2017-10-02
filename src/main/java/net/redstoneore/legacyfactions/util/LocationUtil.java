package net.redstoneore.legacyfactions.util;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.block.BlockEvent;
import org.bukkit.event.entity.EntityEvent;
import org.bukkit.event.player.PlayerEvent;

import net.redstoneore.legacyfactions.config.Config;
import net.redstoneore.legacyfactions.entity.FPlayer;

public class LocationUtil {

	public static boolean isFactionsDisableIn(World world) {
		return Config.disableFactionsInWorlds.contains(world.getName());
	}
	
	public static boolean isFactionsDisableIn(Location location) {
		return isFactionsDisableIn(location.getWorld());
	}
	
	public static boolean isFactionsDisableIn(BlockEvent event) {
		return isFactionsDisableIn(event.getBlock().getLocation());
	}

	public static boolean isFactionsDisableIn(PlayerEvent event) {
		return isFactionsDisableIn(event.getPlayer());
	}

	public static boolean isFactionsDisableIn(EntityEvent event) {
		return isFactionsDisableIn(event.getEntity().getLocation());
	}
	
	public static boolean isFactionsDisableIn(Player player) {
		return isFactionsDisableIn(player.getLocation());
	}

	public static boolean isFactionsDisableIn(FPlayer fplayer) {
		return isFactionsDisableIn(fplayer.getPlayer());
	}
	
}
