package net.redstoneore.legacyfactions.entity.persist.memory;

import net.redstoneore.legacyfactions.FLocation;
import net.redstoneore.legacyfactions.Factions;
import net.redstoneore.legacyfactions.entity.Board;
import net.redstoneore.legacyfactions.entity.Faction;
import net.redstoneore.legacyfactions.entity.FactionColl;
import net.redstoneore.legacyfactions.entity.persist.shared.SharedBoard;
import net.redstoneore.legacyfactions.locality.Locality;
import net.redstoneore.legacyfactions.locality.LocalityLazy;
import net.redstoneore.legacyfactions.mixin.DebugMixin;
import net.redstoneore.legacyfactions.warp.FactionWarp;

import org.bukkit.World;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;
import java.util.Set;
import java.util.TreeMap;

/**
 * MemoryBoard should be used carefully by developers. You should be able to do what you want
 * with the available methods in Board. If something is missing, open an issue on GitHub.<br>
 * <br>
 * Do not store references to any fields. Always use the methods available.  
 */
public abstract class MemoryBoard extends SharedBoard {

	// -------------------------------------------------- //
	// FIELDS
	// -------------------------------------------------- // 
	
	public MemoryBoardLocalityMap flocationIds = new MemoryBoardLocalityMap();
	
	// -------------------------------------------------- //
	// GETTERS AND SETTERS
	// -------------------------------------------------- //
		
	@Override
	public String getIdAt(Locality locality) {
		if (!flocationIds.containsKey(locality)) {
			return "0";
		}
		
		return flocationIds.get(locality);
	}
	
	@Override
	public void setIdAt(String id, Locality locality) {
		this.clearOwnershipAt(locality);

		if (id.equals("0")) {
			this.removeAt(locality);
		}

		this.flocationIds.put(locality, id);
	}
	
	
	@Override
	public void removeAt(Locality locality) {
		Faction faction = this.getFactionAt(locality);
		Collection<FactionWarp> warps = faction.warps().getAll();
		
		warps.stream()
			.filter(warp -> locality.isInChunk(Locality.of(warp.getLocation())))
			.forEach(warp -> warp.delete());
		
		this.clearOwnershipAt(locality);
		this.flocationIds.remove(locality);
	}

	
    // ---------------------------------------------- //
	// CLEANER
    // ---------------------------------------------- //

	@Override
	public void clean() {
		Iterator<Entry<Locality, String>> iter = flocationIds.entrySet().iterator();
		while (iter.hasNext()) {
			Entry<Locality, String> entry = iter.next();
			if (!FactionColl.get().isValidFactionId(entry.getValue())) {
				Factions.get().log("Board cleaner removed " + entry.getValue() + " from " + entry.getKey());
				iter.remove();
			}
		}
	}

	@Override
	public void clean(String factionId) {
		this.flocationIds.removeFaction(factionId);
	}
	
    // ---------------------------------------------- //
	// COORD COUNT
    // ---------------------------------------------- //

	public int getFactionCoordCount(String factionId) {
		return this.flocationIds.getOwnedLandCount(factionId);
	}
	
	public int getFactionCoordCountInWorld(Faction faction, World world) {
		String factionId = faction.getId();
		int ret = 0;
		Iterator<Entry<Locality, String>> iter = flocationIds.entrySet().iterator();
		while (iter.hasNext()) {
			Entry<Locality, String> entry = iter.next();
			if (entry.getValue().equals(factionId) && entry.getKey().getWorld().getUID() == world.getUID()) {
				ret += 1;
			}
		}
		return ret;
	}
	
	@Override
	public Set<Locality> getAll(String factionId) {
		return this.flocationIds.entrySet().stream()
			.filter(entry -> entry.getValue().equals(factionId))
			.map(entry -> entry.getKey())
			.collect(Collectors.toSet());
	}

	@Override
	public Set<Locality> getAll(Faction faction) {
		return this.getAll(faction.getId());		
	}

	@Override
	public Set<Locality> getAll() {
		return new HashSet<>(this.flocationIds.keySet());
	}
	
	// -------------------------------------------------- //
	// PERSISTANCE
	// -------------------------------------------------- //

	public void convertFrom(MemoryBoard old) {
		this.flocationIds = old.flocationIds;
		this.forceSave();
		Board.instance = this;
	}

	public Map<String, Map<String, String>> dumpAsSaveFormat() {
		Map<String, Map<String, String>> worldCoordIds = new HashMap<String, Map<String, String>>();

		String worldName, coords;
		String id;

		for (Entry<Locality, String> entry : flocationIds.entrySet()) {
			worldName = entry.getKey().getWorldName();
			coords = entry.getKey().getCoordString();
			id = entry.getValue();
			if (!worldCoordIds.containsKey(worldName)) {
				worldCoordIds.put(worldName, new TreeMap<String, String>());
			}

			worldCoordIds.get(worldName).put(coords, id);
		}

		return worldCoordIds;
	}

	public void loadFromSaveFormat(Map<String, Map<String, String>> worldCoordIds) {
		flocationIds.clear();

		String worldName;
		String[] coords;
		int chunkX, chunkZ;
		String factionId;

		for (Entry<String, Map<String, String>> entry : worldCoordIds.entrySet()) {
			worldName = entry.getKey();
			for (Entry<String, String> entry2 : entry.getValue().entrySet()) {
				coords = entry2.getKey().trim().split("[,\\s]+");
				chunkX = Integer.parseInt(coords[0]);
				chunkZ = Integer.parseInt(coords[1]);
				factionId = entry2.getValue();
				flocationIds.put(LocalityLazy.of(worldName, chunkX, chunkZ), factionId);
			}
		}
	}
	
	// -------------------------------------------------- //
	// DEPRECATED
	// -------------------------------------------------- //
	
	@Deprecated
	@Override
	public void removeAt(FLocation flocation) {
		DebugMixin.deprecatedWarning("Board#removeAt(FLocation)", "Board#removeAt(Locality)");
		this.removeAt(Locality.of(flocation.getChunk()));
	}

	@Deprecated
	@Override
	public Set<FLocation> getAllClaims(String factionId) {
		DebugMixin.deprecatedWarning("Board#getAllClaims(factionId)", "Board#getAll(factionId)");

		Set<FLocation> locs = new HashSet<>();
		Iterator<Entry<Locality, String>> iter = flocationIds.entrySet().iterator();
		while (iter.hasNext()) {
			Entry<Locality, String> entry = iter.next();
			if (entry.getValue().equals(factionId)) {
				locs.add((FLocation) entry.getKey());
			}
		}
		return locs;
	}

	@Deprecated
	@Override
	public final Set<FLocation> getAllClaims() {
		DebugMixin.deprecatedWarning("Board#getAllClaims()", "Board#getAll()");

		return this.flocationIds.keySet().stream()
			.map(locality -> (FLocation) locality)
			.collect(Collectors.toSet());
	}
	
	@Deprecated
	@Override
	public void setIdAt(String id, FLocation flocation) {
		DebugMixin.deprecatedWarning("Board#setIdAt(String, FLocation)", "Board#setIdAt(String, Locality)");

		this.setIdAt(id, (Locality) flocation);
	}
	
	@Deprecated
	public class MemoryBoardMap extends MemoryBoardLocalityMap {
		private static final long serialVersionUID = -6689617828610585368L;
		
		@Deprecated
		public String put(FLocation floc, String factionId) {
			DebugMixin.deprecatedWarning("Board.MemoryBoardMap#put(FLocation, String)", "DO NOT REFERENCE THIS CLASS TYPE");
			return super.put(floc, factionId);
		}
	}
	
}
