package com.massivecraft.legacyfactions.util;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.massivecraft.legacyfactions.Factions;
import com.massivecraft.legacyfactions.entity.Conf;

public class MigrationUtil {

	private static Path PATH_FACTIONSUUID = Paths.get(Factions.get().getDataFolder().getParent(), "FactionsUUID");
	private static Path PATH_FACTIONSUUID_CONFIGYML = Paths.get(PATH_FACTIONSUUID.toString(), "config.yml");
	
	/**
	 * Checks if FactionsUUID exists
	 * @return true if FactionsUUID folder exists
	 */
	public static boolean isPreviousFactionUUID() {
		return Files.isDirectory(PATH_FACTIONSUUID);
	}
	
	/**
	 * Checks if FactionsUUID folder with config.yml exists
	 * @return true if FactionsUUID/config.yml file exists
	 */
	public static boolean shouldMigrateConfigYML() {
		if ( ! isPreviousFactionUUID()) return false;
		
		if ( ! Files.isDirectory(PATH_FACTIONSUUID_CONFIGYML)) return false;
		
		return true;
	}
	
	/**
	 * This method will merge config.yml into conf.json (there is no need for these to be seperate)
	 */
	public static void migrateConfigYML() {
		long timeEnableStart = System.currentTimeMillis();
        Factions.get().log("=== MIGRATION START ===");

		Conf.debug = Factions.get().getConfig().getBoolean("debug", false);
		Conf.findFactionsExploitCooldown = Factions.get().getConfig().getLong("findfactionsexploit.cooldown", 2000);
		Conf.findFactionsExploitLog = Factions.get().getConfig().getBoolean("findfactionsexploit.log", false);
		Conf.factionDefaultRelation = Factions.get().getConfig().getString("default-relation", "neutral");
		Conf.portalsLimit = Factions.get().getConfig().getBoolean("portals.limit", false);
		Conf.portalsMinimumRelation = Factions.get().getConfig().getString("portals.minimum-relation", "MEMBER");
		Conf.warpsMax = Factions.get().getConfig().getInt("max-warps", 5);
		
		Map<String, Double> warpCost = null;
		if (Factions.get().getConfig().getBoolean("warp-cost", false)) {
			warpCost = MiscUtil.map(
				"set", Factions.get().getConfig().getDouble("warp-cost.setwarp", 0),
				"delete", Factions.get().getConfig().getDouble("warp-cost.delwarp", 0),
				"use", Factions.get().getConfig().getDouble("warp-cost.warp", 0)
			);	
		} else {
			Factions.get().log("Warp costs disabled, values set to 0");
			Factions.get().log("+  warpCost:set     " + Factions.get().getConfig().getDouble("warp-cost.setwarp", 0) + " -> 0");
			Factions.get().log("+  warpCost:delete  " + Factions.get().getConfig().getDouble("warp-cost.delwarp", 0) + " -> 0");
			Factions.get().log("+  warpCost:use     " + Factions.get().getConfig().getDouble("warp-cost.warp", 0) + " -> 0");
			Factions.get().log("");
			
			warpCost = MiscUtil.map(
				"set", 0.0,
				"delete", 0.0,
				"use", 0.0
			);
		}
		
		Conf.warpCost = warpCost;
		Conf.disablePistonsInTerritory = Factions.get().getConfig().getBoolean("disable-pistons-in-territory", false);
		
		Map<String, List<String>> tooltips = new HashMap<String, List<String>>();
		tooltips.put("show",  Factions.get().getConfig().getStringList("tooltips.show"));
		tooltips.put("list",  Factions.get().getConfig().getStringList("tooltips.list"));
		
		Conf.tooltips = tooltips;
		
		Conf.scoreboardInChat = Factions.get().getConfig().getBoolean("scoreboard.also-send-chat", false);
		Conf.scoreboardExpires =  Factions.get().getConfig().getLong("scoreboard.expiration", 6);
		Conf.scoreboardInfoEnabled = Factions.get().getConfig().getBoolean("scoreboard.finfo-enabled", false);
		Conf.scoreboardInfo = Factions.get().getConfig().getStringList("scoreboard.finfo");
		
		Conf.save();
		
        Factions.get().log("=== MIGRATION DONE (Took " + (System.currentTimeMillis() - timeEnableStart) + "ms) ===");

	}
	
}
