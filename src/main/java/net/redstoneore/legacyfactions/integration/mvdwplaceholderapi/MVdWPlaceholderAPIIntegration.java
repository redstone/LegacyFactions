package net.redstoneore.legacyfactions.integration.mvdwplaceholderapi;

import org.bukkit.Bukkit;

import net.redstoneore.legacyfactions.integration.Integration;

public class MVdWPlaceholderAPIIntegration extends Integration {

	private static MVdWPlaceholderAPIIntegration i = new MVdWPlaceholderAPIIntegration();
	public static MVdWPlaceholderAPIIntegration get() { return i; }
	
	@Override
	public String getName() {
		return "MVdWPlaceholderAPI";
	}

	@Override
	public boolean isEnabled() {
		return Bukkit.getPluginManager().getPlugin(this.getName()).isEnabled();
	}

	@Override
	public void init() {
		MVdWPlaceholderAPIEngine.start();
	}

}
