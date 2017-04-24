package net.redstoneore.legacyfactions.warp;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Map.Entry;
import java.util.Optional;

import org.bukkit.Location;

import net.redstoneore.legacyfactions.entity.Faction;
import net.redstoneore.legacyfactions.util.LazyLocation;

public class FactionWarps {

	// -------------------------------------------------- //
	// CONSTRUCT
	// -------------------------------------------------- //

	public FactionWarps(Faction faction) {
		this.faction = faction;
	}
	
	// -------------------------------------------------- //
	// FIELDS
	// -------------------------------------------------- //

	private final Faction faction;
	
	// -------------------------------------------------- //
	// METHODS
	// -------------------------------------------------- //

	public Collection<FactionWarp> getAll() {
		Collection<FactionWarp> all = new ArrayList<FactionWarp>();
		
		for (Entry<String, LazyLocation> entry : faction.asMemoryFaction().getAllWarps().entrySet()) {
			FactionWarp warp = new FactionWarp(faction, entry.getKey(), entry.getValue());
			all.add(warp);
		}
		
		return all;
	}
	
	public Optional<FactionWarp> get(String name) {
		LazyLocation location = this.faction.asMemoryFaction().getWarp(name);
		
		if (location == null) return Optional.empty();
		
		FactionWarp warp = new FactionWarp(faction, name, location);
		
		return Optional.of(warp);
	}

	public void setWarp(String name, LazyLocation location) {
		this.setWarp(name, location, null);
	}
	
	public void setWarp(String name, LazyLocation location, String password) {
		this.faction.asMemoryFaction().setWarp(name, location, password);
	}
	
	public void setWarp(String name, Location location) {
		this.setWarp(name, location, null);
	}
	
	public void setWarp(String name, Location location, String password) {
		this.faction.asMemoryFaction().setWarp(name, new LazyLocation(location), password);
	}
	
	public boolean delete(String name) {
		Optional<FactionWarp> warp = this.get(name);
		if ( ! warp.isPresent()) return false;
		
		warp.get().delete();
		return true;
	}
	
	public boolean deleteAll() {
		this.faction.asMemoryFaction().clearWarps();
		return true;
	}
	
	public int size() {
		return this.faction.asMemoryFaction().getAllWarps().size();
	}
	
}
