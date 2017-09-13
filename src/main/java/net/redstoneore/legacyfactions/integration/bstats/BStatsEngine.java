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
					map.put("FactionsFly Enabled", 1);					
				} else {
					map.put("FactionsFly Disabled", 0);
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
					map.put("FactionsChat Enabled", 1);					
				} else {
					map.put("FactionsChat Disabled", 0);
				}
				return map;
			}
		}));
		
		// feature: coleaders
		metrics.addCustomChart(new Metrics.SimpleBarChart("legacyfactions_feature_coleaders", new Callable<Map<String, Integer>>() {
			@Override
			public Map<String, Integer> call() throws Exception {
				Map<String, Integer> map = new HashMap<>();
				if (Conf.enableColeaders) {
					map.put("Coleaders Enabled", 1);					
				} else {
					map.put("Coleaders Disabled", 0);
				}
				return map;
			}
		}));
				
		// feature: truces
		metrics.addCustomChart(new Metrics.SimpleBarChart("legacyfactions_feature_truces", new Callable<Map<String, Integer>>() {
			@Override
			public Map<String, Integer> call() throws Exception {
				Map<String, Integer> map = new HashMap<>();
				if (Conf.enableTruces) {
					map.put("Truces Enabled", 1);					
				} else {
					map.put("Truces Disabled", 0);
				}
				return map;
			}
		}));
				
		// feature: flags
		metrics.addCustomChart(new Metrics.SimpleBarChart("legacyfactions_feature_flags", new Callable<Map<String, Integer>>() {
			@Override
			public Map<String, Integer> call() throws Exception {
				Map<String, Integer> map = new HashMap<>();
				if (Conf.enableTruces) {
					map.put("Flags Enabled", 1);					
				} else {
					map.put("Flags Disabled", 0);
				}
				return map;
			}
		}));
						
				
		
	}
	
}
