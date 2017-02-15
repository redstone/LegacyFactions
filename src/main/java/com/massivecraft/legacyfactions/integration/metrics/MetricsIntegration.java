package com.massivecraft.legacyfactions.integration.metrics;

import com.massivecraft.legacyfactions.entity.Conf;
import com.massivecraft.legacyfactions.integration.Integration;
public class MetricsIntegration extends Integration {
	
	private static MetricsIntegration i = new MetricsIntegration();
	public static MetricsIntegration get() { return i; }
		
	@Override
	public boolean isEnabled() {
		return Conf.enableMetrics;
	}

	@Override
	public void init() {
		MetricsEngine.start();
	}

}
