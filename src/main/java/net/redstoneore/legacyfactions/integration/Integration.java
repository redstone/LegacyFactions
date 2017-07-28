package net.redstoneore.legacyfactions.integration;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;

import net.redstoneore.legacyfactions.Factions;

public abstract class Integration {

	public abstract String getName();
		
	public abstract void init();
	
	public boolean isEnabled() {
		return Bukkit.getPluginManager().isPluginEnabled(this.getName());
	}
	
	public void notifyEnabled() {
		Factions.get().log(ChatColor.GOLD + "Integration " + this.getName() + " is enabled");
	}
}
