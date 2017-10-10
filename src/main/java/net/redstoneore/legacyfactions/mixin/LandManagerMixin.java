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
	
	public static List<LandManager> getLandManagers() {
		List<LandManager> landManagers = new ArrayList<>();
		
		if (Bukkit.getPluginManager().isPluginEnabled("WorldEdit")) {
			landManagers.add(new LandManagerWorldEdit());
		}
		
		return landManagers;
	}
	
}
