package com.massivecraft.factionsuuid.integration.vault;

import org.bukkit.Bukkit;

import com.massivecraft.factionsuuid.Factions;
import com.massivecraft.factionsuuid.integration.Integration;


public class VaultIntegration  extends Integration {
	
	private static VaultIntegration i = new VaultIntegration();
	public static VaultIntegration get() { return i; }
		
	private String pluginName = "Vault";
	
	@Override
	public boolean isEnabled() {
		return Bukkit.getPluginManager().isPluginEnabled(this.pluginName);
	}

	@Override
	public void init() {
		VaultEngine.setup();
	}
	
	public Boolean hasPermissions() {
		return Factions.get().perms != null;
	}

}
