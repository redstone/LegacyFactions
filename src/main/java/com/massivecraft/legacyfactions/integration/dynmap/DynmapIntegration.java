package com.massivecraft.legacyfactions.integration.dynmap;

import org.bukkit.Bukkit;

import com.massivecraft.legacyfactions.integration.Integration;

public class DynmapIntegration extends Integration {

	private static DynmapIntegration i = new DynmapIntegration();
	public static DynmapIntegration get() { return i; }
	
	@Override
	public String getName() {
		return "Dynmap";
	}

	@Override
	public boolean isEnabled() {
		return Bukkit.getPluginManager().isPluginEnabled(this.getName());
	}

	@Override
	public void init() {
        DynmapEngine.getInstance().init();		
        this.notifyEnabled();
	}

}
