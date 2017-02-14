package com.massivecraft.factions.integration.essentials;

import org.bukkit.Bukkit;

import com.massivecraft.factions.integration.Integration;

public class EssentialsIntegration extends Integration {
	
	private static EssentialsIntegration i = new EssentialsIntegration();
	public static EssentialsIntegration get() { return i; }
	
	private String pluginName = "Essentials";
	
	@Override
	public boolean isEnabled() {
		return Bukkit.getPluginManager().isPluginEnabled(this.pluginName);
	}

	@Override
	public void init() {
		EssentialsEngine.setup();
	}

}
