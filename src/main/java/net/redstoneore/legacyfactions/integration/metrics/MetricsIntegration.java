package net.redstoneore.legacyfactions.integration.metrics;

import net.redstoneore.legacyfactions.config.Config;
import net.redstoneore.legacyfactions.integration.Integration;
public class MetricsIntegration extends Integration {
	
	private static MetricsIntegration i = new MetricsIntegration();
	public static MetricsIntegration get() { return i; }
		
	@Override
	public String getName() {
		return "Metrics";
	}
	
	@Override
	public boolean isEnabled() {
		return Config.logStatistics;
	}

	@Override
	public void init() {
		MetricsEngine.start();
		this.notifyEnabled();
	}

}
