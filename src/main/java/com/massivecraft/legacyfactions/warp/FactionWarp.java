package com.massivecraft.legacyfactions.warp;

import org.bukkit.Location;

import com.massivecraft.legacyfactions.entity.Faction;
import com.massivecraft.legacyfactions.util.LazyLocation;

public class FactionWarp {

	public FactionWarp(Faction faction, String name, LazyLocation location) {
		this.faction = faction;
		this.name = name;
		this.location = location;
	}
	
	private final Faction faction;
	private String name;
	private LazyLocation location;
	
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
		this.faction.asMemoryFaction().removeWarp(this.getName());
	}
	
}
