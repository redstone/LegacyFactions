package net.redstoneore.legacyfactions.integration.bstats;

import net.redstoneore.legacyfactions.config.Config;
import net.redstoneore.legacyfactions.integration.Integration;

public class BStatsIntegration extends Integration {
	
	// -------------------------------------------------- //
	// INSTANCE
	// -------------------------------------------------- //
	
	private static BStatsIntegration instance = new BStatsIntegration();
	public static BStatsIntegration get() { return instance; }
	
	// -------------------------------------------------- //
	// METHODS
	// -------------------------------------------------- //
	
	@Override
	public String getName() {
		return "bStats";
	}
	
	@Override
	public boolean isEnabled() {
		return Config.logStatistics;
	}

	@Override
	public void init() {
		BStatsEngine.start();
		this.notifyEnabled();
	}

}
