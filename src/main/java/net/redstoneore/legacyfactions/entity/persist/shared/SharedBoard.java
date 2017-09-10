package net.redstoneore.legacyfactions.entity.persist.shared;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;

import net.redstoneore.legacyfactions.FLocation;
import net.redstoneore.legacyfactions.Relation;
import net.redstoneore.legacyfactions.entity.Board;
import net.redstoneore.legacyfactions.entity.Conf;
import net.redstoneore.legacyfactions.entity.Faction;
import net.redstoneore.legacyfactions.entity.FactionColl;
import net.redstoneore.legacyfactions.locality.Locality;
import net.redstoneore.legacyfactions.util.AsciiCompass;
import net.redstoneore.legacyfactions.util.TextUtil;

public abstract class SharedBoard extends Board {
	
	// ---------------------------------------------- //
	// GET AND SET 
	// ---------------------------------------------- //

	@Override
	public String getIdAt(Locality locality) {
		FLocation flocation = new FLocation(locality.getWorld().getName(), locality.getChunkX(), locality.getChunkZ());
		return this.getIdAt(flocation);
	}
	
	@Override
	public Faction getFactionAt(FLocation flocation) {
		return FactionColl.get().getFactionById(this.getIdAt(flocation));
	}
	
	@Override
	public Faction getFactionAt(Locality locality) {
		return FactionColl.get().getFactionById(this.getIdAt(locality));
	}
	
	@Override
	public void setFactionAt(Faction faction, Locality locality) {
		this.setIdAt(faction.getId(), locality);
	}
	
	@Override
	public void setFactionAt(Faction faction, FLocation flocation) {
		this.setIdAt(faction.getId(), Locality.of(flocation.getChunk()));
	}
	
	@Override
	public int getFactionCoordCountInWorld(Faction faction, String worldName) {
		return this.getFactionCoordCountInWorld(faction, Bukkit.getWorld(worldName));
	}

	@Override
	public void unclaimAll(String factionId) {
		Faction faction = FactionColl.get().getFactionById(factionId);
		if (faction != null && faction.isNormal()) {
			faction.clearAllClaimOwnership();
			faction.warps().deleteAll();
		}
		this.clean(factionId);
	}

	@Override
	public Set<FLocation> getAllClaims(Faction faction) {
		return getAllClaims(faction.getId());
	}
	
	// ---------------------------------------------- //
	// OWNERSHIP
	// ---------------------------------------------- //
	
	// not to be confused with claims, ownership referring to further member-specific ownership of a claim
	@Override
	public void clearOwnershipAt(Locality locality) {
		Faction faction = this.getFactionAt(locality);
		if (faction != null && faction.isNormal()) {
			faction.clearClaimOwnership(locality);
		}
	}
	
	@Override
	public void clearOwnershipAt(FLocation flocation) {
		Faction faction = getFactionAt(flocation);
		if (faction != null && faction.isNormal()) {
			faction.clearClaimOwnership(flocation);
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
	public boolean isBorderLocation(FLocation flocation) {
		Faction faction = getFactionAt(flocation);
		FLocation a = flocation.getRelative(1, 0);
		FLocation b = flocation.getRelative(-1, 0);
		FLocation c = flocation.getRelative(0, 1);
		FLocation d = flocation.getRelative(0, -1);
		return faction != getFactionAt(a) || faction != getFactionAt(b) || faction != getFactionAt(c) || faction != getFactionAt(d);
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
	public boolean isConnectedLocation(FLocation flocation, Faction faction) {
		FLocation a = flocation.getRelative(1, 0);
		FLocation b = flocation.getRelative(-1, 0);
		FLocation c = flocation.getRelative(0, 1);
		FLocation d = flocation.getRelative(0, -1);
		return faction == getFactionAt(a) || faction == getFactionAt(b) || faction == getFactionAt(c) || faction == getFactionAt(d);
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
	
	@Override
	@Deprecated
	public boolean hasFactionWithin(FLocation flocation, Faction faction, int radius) {
		return this.hasFactionWithin(Locality.of(flocation.getChunk()), faction, radius);
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

		int halfWidth = Conf.mapWidth / 2;
		int halfHeight = Conf.mapHeight / 2;
		Locality topLeft = locality.getRelative(-halfWidth, -halfHeight);
		
		int mapWidth = halfWidth * 2 + 1;
		int mapHeight = halfHeight * 2 + 1;

		// If we're not showing the key, we don't need this row
		if (Conf.showMapFactionKey) {
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
									   (Conf.showNeutralFactionsOnMap && relation.equals(Relation.NEUTRAL)) ||
									   (Conf.showEnemyFactionsOnMap && relation.equals(Relation.ENEMY))) {
						if (!factionList.containsKey(factionHere.getTag())) {
							if (factionHere.hasForcedMapCharacter()) {
								factionList.put(factionHere.getTag(), factionHere.getForcedMapCharacter());								
							} else {
								factionList.put(factionHere.getTag(), Conf.mapKeyChrs[Math.min(chrIdx++, Conf.mapKeyChrs.length - 1)]);								
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
		if (Conf.showMapFactionKey) {
			String row = "";
			for (String key : factionList.keySet()) {
				row += String.format("%s%s: %s ", ChatColor.GRAY, factionList.get(key), key);
			}
			lines.add(row);
		}

		return lines;	
	}
	
	@Override
	public ArrayList<String> getMap(Faction faction, FLocation flocation, double inDegrees) {
		return this.getMap(faction, Locality.of(flocation.getChunk()), inDegrees);
	}
	
}
