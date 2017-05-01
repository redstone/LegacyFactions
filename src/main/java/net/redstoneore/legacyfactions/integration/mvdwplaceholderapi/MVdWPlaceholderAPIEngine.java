package net.redstoneore.legacyfactions.integration.mvdwplaceholderapi;

import be.maximvdw.placeholderapi.PlaceholderAPI;
import be.maximvdw.placeholderapi.PlaceholderReplaceEvent;
import be.maximvdw.placeholderapi.PlaceholderReplacer;
import net.redstoneore.legacyfactions.Factions;
import net.redstoneore.legacyfactions.entity.FPlayerColl;
import net.redstoneore.legacyfactions.integration.IntegrationEngine;

public class MVdWPlaceholderAPIEngine extends IntegrationEngine {
	
	public static void start() {
		PlaceholderAPI.registerPlaceholder(Factions.get(), "factions_faction_id", new PlaceholderReplacer() {
			@Override
			public String onPlaceholderReplace(PlaceholderReplaceEvent event) {
				return FPlayerColl.get(event.getPlayer()).getFactionId();
			}
		});
		
		PlaceholderAPI.registerPlaceholder(Factions.get(), "factions_faction_name", new PlaceholderReplacer() {
			@Override
			public String onPlaceholderReplace(PlaceholderReplaceEvent event) {
				return FPlayerColl.get(event.getPlayer()).getFaction().getTag();
			}
		});
		
		PlaceholderAPI.registerPlaceholder(Factions.get(), "factions_faction_description", new PlaceholderReplacer() {
			@Override
			public String onPlaceholderReplace(PlaceholderReplaceEvent event) {
				return FPlayerColl.get(event.getPlayer()).getFaction().getTag();
			}
		});
	}
	
	public static void stop() {
		
	}
}
