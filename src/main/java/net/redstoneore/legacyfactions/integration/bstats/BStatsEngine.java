package net.redstoneore.legacyfactions.integration.bstats;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.stream.Collectors;

import net.redstoneore.legacyfactions.Factions;
import net.redstoneore.legacyfactions.entity.Board;
import net.redstoneore.legacyfactions.entity.Conf;
import net.redstoneore.legacyfactions.entity.FactionColl;
import net.redstoneore.legacyfactions.integration.IntegrationEngine;

public class BStatsEngine extends IntegrationEngine {
	private static Metrics metrics = null;
	
	public static void start() {
		try {
			metrics = new Metrics(Factions.get());
			addCustomCharts();
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
	
	public static void addCustomCharts() {
		// total factions
		metrics.addCustomChart(new Metrics.SimplePie("legacyfactions_total_factions", new Callable<String>() {
			@Override
			public String call() throws Exception {
				return String.valueOf(FactionColl.all().size());
			}
		}));
		
		// total warps
		metrics.addCustomChart(new Metrics.SimplePie("legacyfactions_total_warps", new Callable<String>() {
			@Override
			public String call() throws Exception {
				return FactionColl.all().stream()
						.map(faction -> faction.warps().size())
						.collect(Collectors.summingInt(Integer::intValue))
						.toString();
			}
		}));
		
		// total claims
		metrics.addCustomChart(new Metrics.SimplePie("legacyfactions_total_claims", new Callable<String>() {
			@Override
			public String call() throws Exception {
				return String.valueOf(Board.get().getAllClaims().size());
			}
		}));
		
		// expansion: factions fly
		metrics.addCustomChart(new Metrics.SimpleBarChart("legacyfactions_expansion_fly", new Callable<Map<String, Integer>>() {
			@Override
			public Map<String, Integer> call() throws Exception {
				Map<String, Integer> map = new HashMap<>();
				if (Conf.expansionFactionsFly.enabled) {
					map.put("FactionsFly", 1);					
				} else {
					map.put("FactionsFly", 0);
				}
				return map;
			}
		}));
		
		// expansion: factions chat
		metrics.addCustomChart(new Metrics.SimpleBarChart("legacyfactions_expansion_chat", new Callable<Map<String, Integer>>() {
			@Override
			public Map<String, Integer> call() throws Exception {
				Map<String, Integer> map = new HashMap<>();
				if (Conf.expansionsFactionsChat.enabled) {
					map.put("FactionsChat", 1);					
				} else {
					map.put("FactionsChat", 0);
				}
				return map;
			}
		}));
		
	}
	
}
