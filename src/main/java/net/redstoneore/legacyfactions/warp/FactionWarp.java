package net.redstoneore.legacyfactions.warp;

import org.bukkit.Location;

import net.redstoneore.legacyfactions.entity.Faction;
import net.redstoneore.legacyfactions.entity.persist.shared.SharedFaction;
import net.redstoneore.legacyfactions.util.LazyLocation;

public class FactionWarp {

	// -------------------------------------------------- //
	// CONSTRUCT
	// -------------------------------------------------- //
	
	public FactionWarp(Faction faction, String name, LazyLocation location) {
		this.faction = faction;
		this.name = name;
		this.location = location;
	}
	
	// -------------------------------------------------- //
	// FIELDS
	// -------------------------------------------------- //
	
	private final Faction faction;
	private String name;
	private LazyLocation location;
	
	// -------------------------------------------------- //
	// METHODS
	// -------------------------------------------------- //
	
	public String getName() {
		return this.name;
	}
	
	public LazyLocation getLazyLocation() {
		return this.location;
	}
	
	public Location getLocation() {
		return this.location.getLocation();
	}
	
	public void delete() {
		
		this.shared().removeWarp(this.getName());
	}
	
	public Boolean hasPassword() {
		return this.shared().getWarpPassword(this.getName()).isPresent() && this.shared().getWarpPassword(this.getName()).get() != null;
	}
	
	public Boolean isPassword(String password) {
		return this.shared().getWarpPassword(this.getName()).get().equalsIgnoreCase(password.toLowerCase());
	}
	
	public String getPassword() {
		return this.shared().getWarpPassword(this.getName()).get();
	}
	
	private SharedFaction shared() {
		return (SharedFaction) this.faction;
	}
	
}
