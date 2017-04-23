package net.redstoneore.legacyfactions.integration.essentials;

import org.bukkit.Bukkit;

import net.redstoneore.legacyfactions.integration.Integration;

public class EssentialsIntegration extends Integration {
	
	private static EssentialsIntegration i = new EssentialsIntegration();
	public static EssentialsIntegration get() { return i; }
		
	@Override
	public String getName() {
		return "Essentials";
	}
	
	@Override
	public boolean isEnabled() {
		return Bukkit.getPluginManager().isPluginEnabled(this.getName());
	}

	@Override
	public void init() {
		EssentialsEngine.setup();
		this.notifyEnabled();
	}

}
