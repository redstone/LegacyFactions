package net.redstoneore.legacyfactions.integration.vault.util;

import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;

import net.redstoneore.legacyfactions.entity.FPlayer;

public abstract class VaultUtilPlayer extends VaultUtilNamed {

	/**
	 * Get the user friendly representation of a players balance
	 * @param uuid UUID of player to get friendly balance of.
	 * @return String with user friendly balance
	 */
	public String getFriendlyBalance(UUID uuid) {
		OfflinePlayer offline = Bukkit.getOfflinePlayer(uuid);
		if (offline == null) return DEFAULT_BALANCE_STRING;
		
		return this.moneyString(this.econ.getBalance(offline));
	}

	/**
	 * Get the user friendly representation of a players balance
	 * @param player Player to get friendly balance of.
	 * @return String with user friendly balance
	 */
	public String getFriendlyBalance(FPlayer player) {
		return this.getFriendlyBalance(UUID.fromString(player.getId()));
	}
	
}
