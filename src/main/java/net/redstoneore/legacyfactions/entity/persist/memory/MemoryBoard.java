package net.redstoneore.legacyfactions.entity.persist.memory;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;

import net.redstoneore.legacyfactions.*;
import net.redstoneore.legacyfactions.entity.Faction;
import net.redstoneore.legacyfactions.entity.FactionColl;
import net.redstoneore.legacyfactions.entity.persist.shared.SharedBoard;
import net.redstoneore.legacyfactions.locality.Locality;
import net.redstoneore.legacyfactions.warp.FactionWarp;

import org.bukkit.World;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map.Entry;
import java.util.Set;

/**
 * MemoryBoard should be used carefully by developers. You should be able to do what you want
 * with the available methods in Board. If something is missing, open an issue on GitHub.<br>
 * <br>
 * Do not store references to any fields. Always use the methods available.  
 */
public abstract class MemoryBoard extends SharedBoard {

	public MemoryBoardMap flocationIds = new MemoryBoardMap();
	
	// -------------------------------------------------- //
	// GETTERS AND SETTERS
	// -------------------------------------------------- //
		
	public String getIdAt(FLocation flocation) {
		if (!flocationIds.containsKey(flocation)) {
			return "0";
		}
		
		return flocationIds.get(flocation);
	}
	
	@Override
	public void setIdAt(String id, Locality locality) {
		this.clearOwnershipAt(locality);

		if (id.equals("0")) {
			this.removeAt(locality);
		}

		this.flocationIds.put(new FLocation(locality.getChunk()), id);
	}
	
	@Override
	public void setIdAt(String id, FLocation flocation) {
		this.clearOwnershipAt(flocation);

		if (id.equals("0")) {
			this.removeAt(flocation);
		}

		this.flocationIds.put(flocation, id);
	}
	
	@Override
	public void removeAt(Locality locality) {
		Faction faction = this.getFactionAt(locality);
		Collection<FactionWarp> warps = faction.warps().getAll();
		
		warps.stream()
			.filter(warp -> locality.isInChunk(Locality.of(warp.getLocation())))
			.forEach(warp -> warp.delete());
		
		this.clearOwnershipAt(locality);
		flocationIds.remove(new FLocation(locality.getChunk()));
	}
	
	@Override
	public void removeAt(FLocation flocation) {
		this.removeAt(Locality.of(flocation.getChunk()));
	}

	@Override
	public Set<FLocation> getAllClaims(String factionId) {
		Set<FLocation> locs = new HashSet<>();
		Iterator<Entry<FLocation, String>> iter = flocationIds.entrySet().iterator();
		while (iter.hasNext()) {
			Entry<FLocation, String> entry = iter.next();
			if (entry.getValue().equals(factionId)) {
				locs.add(entry.getKey());
			}
		}
		return locs;
	}

	@Override
	public Set<FLocation> getAllClaims() {
		Set<FLocation> claims = new HashSet<>();
		claims.addAll(flocationIds.keySet());
		
		return claims;
	}
	
    // ---------------------------------------------- //
	// CLEANER
    // ---------------------------------------------- //

	@Override
	public void clean() {
		Iterator<Entry<FLocation, String>> iter = flocationIds.entrySet().iterator();
		while (iter.hasNext()) {
			Entry<FLocation, String> entry = iter.next();
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
		return flocationIds.getOwnedLandCount(factionId);
	}
	
	public int getFactionCoordCountInWorld(Faction faction, World world) {
		String factionId = faction.getId();
		int ret = 0;
		Iterator<Entry<FLocation, String>> iter = flocationIds.entrySet().iterator();
		while (iter.hasNext()) {
			Entry<FLocation, String> entry = iter.next();
			if (entry.getValue().equals(factionId) && entry.getKey().getWorld().getUID() == world.getUID()) {
				ret += 1;
			}
		}
		return ret;
	}
	
	public abstract void convertFrom(MemoryBoard old);

	// TODO: move out of this class into its own.
	public class MemoryBoardMap extends HashMap<FLocation, String> {
		private static final long serialVersionUID = -6689617828610585368L;

		Multimap<String, FLocation> factionToLandMap = HashMultimap.create();

		 @Override
		 public String put(FLocation floc, String factionId) {
			 String previousValue = super.put(floc, factionId);
			 if (previousValue != null) {
				 factionToLandMap.remove(previousValue, floc);
			 }

			 factionToLandMap.put(factionId, floc);
			 return previousValue;
		 }

		 @Override
		 public String remove(Object key) {
			 String result = super.remove(key);
			 if (result != null) {
				 FLocation floc = (FLocation) key;
				 factionToLandMap.remove(result, floc);
			 }

			 return result;
		 }

		 @Override
		 public void clear() {
			 super.clear();
			 factionToLandMap.clear();
		 }

		 public int getOwnedLandCount(String factionId) {
			 return factionToLandMap.get(factionId).size();
		 }

		 public void removeFaction(String factionId) {
			 Collection<FLocation> flocations = factionToLandMap.removeAll(factionId);
			 for (FLocation floc : flocations) {
				 super.remove(floc);
			 }
		 }
	}
	
}
