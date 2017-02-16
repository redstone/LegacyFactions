package com.massivecraft.legacyfactions.integration.vault;

import org.bukkit.Bukkit;

import com.massivecraft.legacyfactions.Factions;
import com.massivecraft.legacyfactions.integration.Integration;

public class VaultIntegration  extends Integration {
	
	private static VaultIntegration i = new VaultIntegration();
	public static VaultIntegration get() { return i; }
		
	@Override
	public String getName() {
		return "Vault";
	}
	
	@Override
	public boolean isEnabled() {
		return Bukkit.getPluginManager().isPluginEnabled(this.getName());
	}

	@Override
	public void init() {
		VaultEngine.setup();
		this.notifyEnabled();
	}
	
	public Boolean hasPermissions() {
		return Factions.get().perms != null;
	}

}
