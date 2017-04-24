package net.redstoneore.legacyfactions.integration.bstats;

import net.redstoneore.legacyfactions.entity.Conf;
import net.redstoneore.legacyfactions.integration.Integration;

public class BStatsIntegration extends Integration {
	
	private static BStatsIntegration i = new BStatsIntegration();
	public static BStatsIntegration get() { return i; }
		
	@Override
	public String getName() {
		return "bStats";
	}
	
	@Override
	public boolean isEnabled() {
		return Conf.enableMetrics;
	}

	@Override
	public void init() {
		BStatsEngine.start();
		this.notifyEnabled();
	}

}
