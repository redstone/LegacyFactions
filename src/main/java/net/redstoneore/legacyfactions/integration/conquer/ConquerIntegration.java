package net.redstoneore.legacyfactions.integration.conquer;

import org.bukkit.Bukkit;

import net.redstoneore.legacyfactions.integration.Integration;

public class ConquerIntegration extends Integration {
	
	private static ConquerIntegration instance = new ConquerIntegration();
	public static ConquerIntegration get() { return instance; }
	
	@Override
	public String getName() {
		return "Conquer";
	}
	
	@Override
	public boolean isEnabled() {
		return Bukkit.getPluginManager().isPluginEnabled(this.getName());
	}

	@Override
	public void init() {
		ConquerEngine.get().enable();
		this.notifyEnabled();
	}
	
}
