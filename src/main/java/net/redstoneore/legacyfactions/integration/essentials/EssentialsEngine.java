package net.redstoneore.legacyfactions.integration.essentials;

import com.earth2me.essentials.Essentials;
import com.earth2me.essentials.Teleport;
import com.earth2me.essentials.Trade;

import net.ess3.api.IEssentials;
import net.redstoneore.legacyfactions.config.Config;

import java.math.BigDecimal;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerTeleportEvent.TeleportCause;
import org.bukkit.plugin.java.JavaPlugin;

public class EssentialsEngine {

	private static IEssentials essentialsPlugins;

	public static IEssentials getEssentials() {
		return essentialsPlugins;
	}
	
	public static boolean setup() {
		IEssentials essentials = JavaPlugin.getPlugin(Essentials.class);
		if (essentials == null) return false;
		essentialsPlugins = (IEssentials) essentials;
		return true;
	}

	// return false if feature is disabled or Essentials isn't available
	public static boolean handleTeleport(Player player, Location loc) {
		if (!Config.homesTeleportCommandEssentialsIntegration || getEssentials() == null) {
			return false;
		}

		Teleport teleportRequest = getEssentials().getUser(player).getTeleport();
		Trade trade = new Trade(BigDecimal.valueOf(Config.econCostHome), getEssentials());
		
		try {
			teleportRequest.teleport(loc, trade, TeleportCause.PLUGIN);
		} catch (Exception ex) {
			player.sendMessage(ChatColor.RED.toString() + ex.getMessage());
			return false;
		}
		
		return true;
	}

	public static boolean isVanished(Player player) {
		if (getEssentials() == null) return false;
		
		return getEssentials().getUser(player).isVanished();
	}
}
