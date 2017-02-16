package com.massivecraft.legacyfactions.integration.worldguard;

import org.bukkit.Bukkit;

import com.massivecraft.legacyfactions.integration.Integration;

public class WorldGuardIntegration extends Integration {
	
	private static WorldGuardIntegration i = new WorldGuardIntegration();
	public static WorldGuardIntegration get() { return i; }
	
	@Override
	public String getName() {
		return "WorldGuard";
	}
	
	@Override
	public boolean isEnabled() {
		return Bukkit.getPluginManager().isPluginEnabled(this.getName());
	}

	@Override
	public void init() {
		WorldGuardEngine.init();
		this.notifyEnabled();
	}
	
}
