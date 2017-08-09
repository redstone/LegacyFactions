package net.redstoneore.legacyfactions.locality;

import java.util.HashMap;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;

public class LocalityMemoryMap extends HashMap<Locality, String> {
	
	// -------------------------------------------------- //
	// STATIC FIELDS
	// -------------------------------------------------- //
	
	private static final long serialVersionUID = -6689617828610585368L;

	// -------------------------------------------------- //
	// FIELDS
	// -------------------------------------------------- //

	private Multimap<String, Locality> localities = HashMultimap.create();

	// -------------------------------------------------- //
	// METHODS
	// -------------------------------------------------- //

	@Override
	public String put(Locality locality, String factionId) {
		String previousValue = super.put(locality, factionId);
		
		if (previousValue != null) {
			this.localities.remove(previousValue, locality);
		}

		this.localities.put(factionId, locality);
		return previousValue;
	}

	@Override
	public String remove(Object key) {
		String result = super.remove(key);
		if (result != null) {
			Locality locality = (Locality) key;
			this.localities.remove(result, locality);
		}

		return result;
	}

	@Override
	public void clear() {
		super.clear();
		this.localities.clear();
	}

	public int getOwnedLandCount(String factionId) {
		return this.localities.get(factionId).size();
	}

	public void removeFaction(String factionId) {
		this.localities.removeAll(factionId).forEach(locality -> super.remove(locality));
	}
	
}
