package net.redstoneore.legacyfactions.integration.venturechat;

import org.bukkit.Bukkit;

import net.redstoneore.legacyfactions.Factions;
import net.redstoneore.legacyfactions.integration.Integration;

public class VentureChatIntegration extends Integration {

	private static VentureChatIntegration i = new VentureChatIntegration();
	public static VentureChatIntegration get() { return i; }
	
	@Override
	public String getName() {
		return "VentureChat";
	}

	@Override
	public boolean isEnabled() {
		return Bukkit.getPluginManager().isPluginEnabled(this.getName());
	}

	@Override
	public void init() {
		Bukkit.getServer().getPluginManager().registerEvents(VentureChatEngine.get(), Factions.get());
	}

}
