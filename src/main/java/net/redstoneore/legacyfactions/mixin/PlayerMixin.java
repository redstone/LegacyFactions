package net.redstoneore.legacyfactions.mixin;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

public class PlayerMixin {

	// -------------------------------------------------- //
	// STATIC FIELDS
	// -------------------------------------------------- //
	
	private static boolean supportsOffHand = true;
	
	// -------------------------------------------------- //
	// STATIC LOGIC
	// -------------------------------------------------- //
	
	static {
		try {
			PlayerInventory.class.getMethod("getItemInOffHand");
			supportsOffHand = true;
		} catch (Exception e) {
			supportsOffHand = false;
		}
	}
	
	// -------------------------------------------------- //
	// METHODS
	// -------------------------------------------------- //
	
	/**
	 * Get item in the main hand of a player
	 * @param player
	 * @return
	 */
	@SuppressWarnings("deprecation")
	public static ItemStack getItemInMainHand(Player player) {
		if (supportsOffHand) {
			return player.getInventory().getItemInMainHand();
		} else {
			// use the older method
			return player.getItemInHand();
		}
	}
	
	/**
	 * Get item in the off hand of a player
	 * @param player
	 * @return air if off hand not supported
	 */
	public static ItemStack getItemInOffHand(Player player) {
		if (supportsOffHand) {
			return player.getInventory().getItemInOffHand();
		} else {
			// Some older versions of minecraft don't use off hand, so return nothing (air) instead
			return new ItemStack(Material.AIR);
		}
	}
	
}
