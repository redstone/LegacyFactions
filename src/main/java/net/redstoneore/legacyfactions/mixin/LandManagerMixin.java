package net.redstoneore.legacyfactions.mixin;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;

import net.redstoneore.legacyfactions.landmanager.LandManager;
import net.redstoneore.legacyfactions.landmanager.LandManagerWorldEdit;

/**
 * This mixin provides methods used for LandManager, but you are free to use this for your own
 * plugins if needed. 
 */
public class LandManagerMixin {

	private static LandManager landManagerInstance = null;
	public static LandManager getLandManager() {
		if (landManagerInstance == null) {
			// If there are none, return null - maybe one will be available later?
			if (getLandManagers().isEmpty()) return null;
			landManagerInstance = getLandManagers().get(0);
		}
		
		return landManagerInstance;
	}
	public static List<LandManager> getLandManagers() {
		List<LandManager> landManagers = new ArrayList<>();
		
		if (Bukkit.getPluginManager().isPluginEnabled("WorldEdit")) {
			landManagers.add(new LandManagerWorldEdit());
		}
		
		return landManagers;
	}
	
}
