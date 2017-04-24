package net.redstoneore.legacyfactions.integration.metrics;

import net.redstoneore.legacyfactions.Factions;
import net.redstoneore.legacyfactions.integration.IntegrationEngine;

public class MetricsEngine extends IntegrationEngine {
	private static Metrics metrics = null;
	
	public static void start() {
	    try {
	        metrics = new Metrics(Factions.get());
	        metrics.start();
	    } catch (Exception e) {
	    	// fail silently 
	    }
	}
	
	public static void stop() {
		if (metrics != null) {
			try {
				
			} catch (Exception e) {
				// fail silently 
			}
		}
	}
}
