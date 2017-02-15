package com.massivecraft.legacyfactions.integration.metrics;

import com.massivecraft.legacyfactions.Factions;
import com.massivecraft.legacyfactions.integration.IntegrationEngine;

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
				metrics.start();
			} catch (Exception e) {
				// fail silently 
			}
		}
	}
}
