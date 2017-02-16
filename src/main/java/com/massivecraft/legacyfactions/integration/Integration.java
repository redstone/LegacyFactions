package com.massivecraft.legacyfactions.integration;

import org.bukkit.ChatColor;

import com.massivecraft.legacyfactions.Factions;

public abstract class Integration {

	public abstract String getName();
	
	public abstract boolean isEnabled();
	public abstract void init();
	
	public void notifyEnabled() {
		Factions.get().log(ChatColor.GOLD + "Integration " + this.getName() + " is enabled");
	}
}
