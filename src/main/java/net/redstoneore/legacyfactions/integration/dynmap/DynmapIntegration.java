package net.redstoneore.legacyfactions.integration.dynmap;

import org.bukkit.Bukkit;

import net.redstoneore.legacyfactions.Factions;
import net.redstoneore.legacyfactions.integration.Integration;

public class DynmapIntegration extends Integration {

	// -------------------------------------------------- //
	// INSTANCE
	// -------------------------------------------------- //
	
	private static DynmapIntegration instance = new DynmapIntegration();
	public static DynmapIntegration get() { return instance; }
	
	// -------------------------------------------------- //
	// CONSTANTS
	// -------------------------------------------------- //
	
	public final static int BLOCKS_PER_CHUNK = 16;
			
	public final static String FACTIONS_MARKERSET = "factions_markerset";
	
	public final static String FACTIONS_HOME = "factions_home";
	
	public final static String FACTIONS_PLAYERSET = "factions_playerset";
	
	public final static String FACTIONS_AREA = "factions_area";
	
	// -------------------------------------------------- //
	// METHODS
	// -------------------------------------------------- //
	
	@Override
	public String getName() {
		return "Dynmap";
	}

	@Override
	public boolean isEnabled() {
		return Bukkit.getPluginManager().isPluginEnabled("dynmap");
	}

	@Override
	public void init() {
		DynmapEngine.get().runTaskTimerAsynchronously(Factions.get(), 10, 20 * 15);
		this.notifyEnabled();
	}
	
}
