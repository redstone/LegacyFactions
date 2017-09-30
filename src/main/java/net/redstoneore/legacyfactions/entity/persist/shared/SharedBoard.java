package net.redstoneore.legacyfactions.entity.persist.shared;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.World;

import net.redstoneore.legacyfactions.Relation;
import net.redstoneore.legacyfactions.config.Config;
import net.redstoneore.legacyfactions.entity.Board;
import net.redstoneore.legacyfactions.entity.Faction;
import net.redstoneore.legacyfactions.entity.FactionColl;
import net.redstoneore.legacyfactions.locality.Locality;
import net.redstoneore.legacyfactions.mixin.DebugMixin;
import net.redstoneore.legacyfactions.util.AsciiCompass;
import net.redstoneore.legacyfactions.util.TextUtil;
import net.redstoneore.legacyfactions.warp.FactionWarp;

public abstract class SharedBoard extends Board {
	
	// ---------------------------------------------- //
	// GET AND SET 
	// ---------------------------------------------- //
	
	@Override
	public Faction getFactionAt(Locality locality) {
		return FactionColl.get().getFactionById(this.getIdAt(locality));
	}
	
	@Override
	public void setFactionAt(Faction faction, Locality locality) {
		this.setIdAt(faction.getId(), locality);
	}
		
	@Override
	public int getFactionCoordCountInWorld(Faction faction, String worldName) {
		return this.getFactionCoordCountInWorld(faction, Bukkit.getWorld(worldName));
	}

	@Override
	public void unclaimAll(String factionId) {
		Faction faction = FactionColl.get().getFactionById(factionId);
		if (faction != null && faction.isNormal()) {
			faction.ownership().clearAll();
			faction.warps().deleteAll();
		}
		this.clean(factionId);
	}

	@Override
	public void unclaimAll(String factionId, World world) {
		Faction faction = FactionColl.get().getFactionById(factionId);
		if (faction != null && faction.isNormal()) {
			faction.ownership().getAll().entrySet().stream()
				.filter(entry -> entry.getKey().getWorldUID() == world.getUID())
				.collect(Collectors.toList())
				.forEach(entry -> faction.ownership().clearAt(entry.getKey()));
			
			faction.warps().getAll().stream()
				.filter(warp -> warp.getLazyLocation().getWorldName() == world.getName())
				.collect(Collectors.toList())
				.forEach(FactionWarp::delete);
		}
		
		faction.getClaims().stream()
			.filter(claim -> claim.getWorldUID() == world.getUID())
			.forEach(claim -> this.removeAt(claim));
	}
		
	// ---------------------------------------------- //
	// OWNERSHIP
	// ---------------------------------------------- //
	
	// not to be confused with claims, ownership referring to further member-specific ownership of a claim
	@Override
	public void clearOwnershipAt(Locality locality) {
		Faction faction = this.getFactionAt(locality);
		if (faction != null && faction.isNormal()) {
			faction.ownership().clearAt(locality);
		}
	}
	
	// ---------------------------------------------- //
	// UTIL
	// ---------------------------------------------- //
	
	@Override
	public boolean isBorderLocation(Locality locality) {
		Faction faction = this.getFactionAt(locality);
		Locality a = locality.getRelative(1, 0);
		Locality b = locality.getRelative(-1, 0);
		Locality c = locality.getRelative(0, 1);
		Locality d = locality.getRelative(0, -1);
		return faction != this.getFactionAt(a) || faction != this.getFactionAt(b) || faction != this.getFactionAt(c) || faction != this.getFactionAt(d);
	}

	@Override
	public boolean isConnectedLocation(Locality locality, Faction faction) {
			Locality a = locality.getRelative(1, 0);
			Locality b = locality.getRelative(-1, 0);
			Locality c = locality.getRelative(0, 1);
			Locality d = locality.getRelative(0, -1);
		return faction == this.getFactionAt(a) || faction == this.getFactionAt(b) || faction == this.getFactionAt(c) || faction == this.getFactionAt(d);
	}
	
	@Override
	public boolean hasFactionWithin(Locality locality, Faction faction, int radius) {
		for (int x = -radius; x <= radius; x++) {
			for (int z = -radius; z <= radius; z++) {
				if (x == 0 && z == 0) {
					continue;
				}

				Locality relative = locality.getRelative(x, z);
				Faction other = getFactionAt(relative);

				if (other.isNormal() && other != faction) {
					return true;
				}
			}
		}
		return false;
	}
	
	// -------------------------------------------------- //
	// COORD COUNT
	// -------------------------------------------------- //
	
	@Override
	public int getFactionCoordCount(Faction faction) {
		return this.getFactionCoordCount(faction.getId());
	}

	// -------------------------------------------------- //
	// MAP GENERATION
	// -------------------------------------------------- //
	
	@Override
	public ArrayList<String> getMap(Faction faction, Locality locality, double inDegrees) {
		Faction factionAtLocation = this.getFactionAt(locality);
		
		ArrayList<String> lines = new ArrayList<String>();
		lines.add(TextUtil.get().titleize("(" + locality.getCoordString() + ") " + factionAtLocation.getTag(faction)));

		int halfWidth = Config.mapWidth / 2;
		int halfHeight = Config.mapHeight / 2;
		Locality topLeft = locality.getRelative(-halfWidth, -halfHeight);
		
		int mapWidth = halfWidth * 2 + 1;
		int mapHeight = halfHeight * 2 + 1;

		// If we're not showing the key, we don't need this row
		if (Config.showMapFactionKey) {
			mapHeight--;
		}

		Map<String, Character> factionList = new HashMap<>();
		int chrIdx = 0;

		// For each row
		for (int dz = 0; dz < mapHeight; dz++) {
			// Draw and add that row
			String row = "";
			for (int dx = 0; dx < mapWidth; dx++) {
				if (dx == halfWidth && dz == halfHeight) {
					row += ChatColor.AQUA + "+";
				} else {
					Locality flocationHere = topLeft.getRelative(dx, dz);
					Faction factionHere = getFactionAt(flocationHere);
					Relation relation = faction.getRelationTo(factionHere);
					
					// Wilderness, safezone, and warzone all have forced colours and characters
					if (factionHere.isWilderness() || factionHere.isSafeZone() || factionHere.isWarZone()) {
						row += factionHere.getForcedMapColour() + "" + factionHere.getForcedMapCharacter();
					} else if (factionHere == faction ||
									   factionHere == factionAtLocation ||
									   relation.isAtLeast(Relation.ALLY) ||
									   (Config.showNeutralFactionsOnMap && relation.equals(Relation.NEUTRAL)) ||
									   (Config.showEnemyFactionsOnMap && relation.equals(Relation.ENEMY))) {
						if (!factionList.containsKey(factionHere.getTag())) {
							if (factionHere.hasForcedMapCharacter()) {
								factionList.put(factionHere.getTag(), factionHere.getForcedMapCharacter());								
							} else {
								factionList.put(factionHere.getTag(), Config.mapKeyChrs[Math.min(chrIdx++, Config.mapKeyChrs.length - 1)]);								
							}
						}
						char mapCharacter = factionList.get(factionHere.getTag());
						
						if (factionHere.hasForcedMapColour()){
							row += factionHere.getForcedMapColour() + "" + mapCharacter;
						} else {
							row += factionHere.getColorTo(faction) + "" + mapCharacter;							
						}
					} else {
						// Assume wilderness
						row += FactionColl.get().getWilderness().getForcedMapColour() + "" + FactionColl.get().getWilderness().getForcedMapCharacter();
					}
				}
			}
			lines.add(row);
		}

		// Get the compass
		ArrayList<String> asciiCompass = AsciiCompass.getAsciiCompass(inDegrees, ChatColor.RED, TextUtil.get().parse("<a>"));

		// Add the compass
		lines.set(1, asciiCompass.get(0) + lines.get(1).substring(3 * 3));
		lines.set(2, asciiCompass.get(1) + lines.get(2).substring(3 * 3));
		lines.set(3, asciiCompass.get(2) + lines.get(3).substring(3 * 3));

		// Add the faction key
		if (Config.showMapFactionKey) {
			String row = "";
			for (String key : factionList.keySet()) {
				row += String.format("%s%s: %s ", ChatColor.GRAY, factionList.get(key), key);
			}
			lines.add(row);
		}

		return lines;	
	}
	
	@Override
	public Set<Locality> getAll(Faction faction) {
		return this.getAll(faction.getId());
	}
	// -------------------------------------------------- //
	// DEPRECATED
	// -------------------------------------------------- //
	
	@Deprecated
	@Override
	public Set<net.redstoneore.legacyfactions.FLocation> getAllClaims(Faction faction) {
		DebugMixin.deprecatedWarning("Board#getAllClaims(Faction)", "Board#getAll(Faction)");
		return this.getAll(faction).stream()
				.map(locality -> (net.redstoneore.legacyfactions.FLocation) locality)
				.collect(Collectors.toSet());
	}
	
	@Deprecated
	@Override
	public Set<net.redstoneore.legacyfactions.FLocation> getAllClaims() {
		DebugMixin.deprecatedWarning("Board#getAllClaims(Faction)", "Board#getAll(Faction)");
		return this.getAll().stream()
				.map(locality -> (net.redstoneore.legacyfactions.FLocation) locality)
				.collect(Collectors.toSet());
	}
	
	@Deprecated
	@Override
	public Set<net.redstoneore.legacyfactions.FLocation> getAllClaims(String id) {
		DebugMixin.deprecatedWarning("Board#getAllClaims(Faction)", "Board#getAll(Faction)");
		return this.getAll(id).stream()
				.map(locality -> (net.redstoneore.legacyfactions.FLocation) locality)
				.collect(Collectors.toSet());
	}
	
	
	
}
