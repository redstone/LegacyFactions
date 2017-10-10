package net.redstoneore.legacyfactions.landmanager;

import java.util.List;

import org.bukkit.Chunk;

import net.redstoneore.legacyfactions.mixin.LandManagerMixin;

public interface LandManager {
	
	// -------------------------------------------------- //
	// STATIC
	// -------------------------------------------------- //
	
	public static List<LandManager> getLandManagers() {
		return LandManagerMixin.getLandManagers();
	}
	
	// -------------------------------------------------- //
	// METHODS
	// -------------------------------------------------- //
	
	/**
	 * Rebuild a chunk
	 * @param chunk The chunk to rebuild.
	 */
	void rebuild(Chunk chunk);
	
}
