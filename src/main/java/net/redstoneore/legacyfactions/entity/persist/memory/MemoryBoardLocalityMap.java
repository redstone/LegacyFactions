package net.redstoneore.legacyfactions.entity.persist.memory;

import java.util.HashMap;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;

import net.redstoneore.legacyfactions.locality.Locality;

public class MemoryBoardLocalityMap extends HashMap<Locality, String> {
	
	// -------------------------------------------------- //
	// STATIC FIELDS
	// -------------------------------------------------- //
	
	private static final long serialVersionUID = -6689617828610585368L;

	// -------------------------------------------------- //
	// FIELDS
	// -------------------------------------------------- //
	
	private Multimap<String, Locality> factionToLandMap = HashMultimap.create();

	// -------------------------------------------------- //
	// METHODS
	// -------------------------------------------------- //
	
	@Override
	public String put(Locality locality, String factionId) {
		String previousValue = super.put(locality, factionId);
		if (previousValue != null) {
			this.factionToLandMap.remove(previousValue, locality);
		}

		this.factionToLandMap.put(factionId, locality);
		return previousValue;
	}

	@Override
	public String remove(Object key) {
		String result = super.remove(key);
		if (result != null) {
			this.factionToLandMap.remove(result, (Locality) key);
		}

		return result;
	}

	@Override
	public void clear() {
		super.clear();
		this.factionToLandMap.clear();
	}
	 
	public int getOwnedLandCount(String factionId) {
		return this.factionToLandMap.get(factionId).size();
	}

	public void removeFaction(String factionId) {
		this.factionToLandMap.removeAll(factionId).forEach(super::remove);
	}
	 
	public Multimap<String, Locality> getUnderlying() {
		return this.factionToLandMap;
	}
	 
	 public void setUnderlying(Multimap<String, Locality> factionToLandMap) {
		 this.factionToLandMap = factionToLandMap;
	 }
}
