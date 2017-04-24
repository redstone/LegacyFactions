package net.redstoneore.legacyfactions.integration.bstats;

import net.redstoneore.legacyfactions.Factions;
import net.redstoneore.legacyfactions.integration.IntegrationEngine;

public class BStatsEngine extends IntegrationEngine {
	private static Metrics metrics = null;
	
	public static void start() {
	    try {
	        metrics = new Metrics(Factions.get());
	    } catch (Exception e) {
	    	// fail silently 
	    }
	}
	
	public static void stop() {
		if (metrics != null) {
			try {
				metrics = null;
			} catch (Exception e) {
				// fail silently 
			}
		}
	}
}
