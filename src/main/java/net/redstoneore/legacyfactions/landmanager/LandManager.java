package net.redstoneore.legacyfactions.landmanager;

import java.util.List;

import org.bukkit.Chunk;

import net.redstoneore.legacyfactions.mixin.LandManagerMixin;

public interface LandManager {
	
	// -------------------------------------------------- //
	// STATIC
	// -------------------------------------------------- //
	
	public static LandManager get() {
		return LandManagerMixin.getLandManager();
	}
	
	public static List<LandManager> getLandManagers() {
		return LandManagerMixin.getLandManagers();
	}
	
	// -------------------------------------------------- //
	// METHODS
	// -------------------------------------------------- //
	
	void rebuild(Chunk chunk);
	
}
