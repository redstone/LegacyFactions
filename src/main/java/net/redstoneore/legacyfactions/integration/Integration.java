package net.redstoneore.legacyfactions.integration;

import org.bukkit.ChatColor;

import net.redstoneore.legacyfactions.Factions;

public abstract class Integration {

	public abstract String getName();
	
	public abstract boolean isEnabled();
	public abstract void init();
	
	public void notifyEnabled() {
		Factions.get().log(ChatColor.GOLD + "Integration " + this.getName() + " is enabled");
	}
}
