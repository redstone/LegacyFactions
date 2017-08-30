package net.redstoneore.legacyfactions.exception;

import org.bukkit.plugin.java.JavaPlugin;

public class PluginAlreadyInitiatedException extends Exception {

	// -------------------------------------------------- //
	// STATIC FIELDS
	// -------------------------------------------------- //
	
	private static final long serialVersionUID = 1L;

	// -------------------------------------------------- //
	// CONSTRUCT
	// -------------------------------------------------- //
	
	public PluginAlreadyInitiatedException(JavaPlugin plugin) {
		super(plugin.getName() + " has already been initialised!");
		this.plugin = plugin;
	}
	
	// -------------------------------------------------- //
	// FIELDS
	// -------------------------------------------------- //
	
	private JavaPlugin plugin;
	
	// -------------------------------------------------- //
	// METHODS
	// -------------------------------------------------- //

	public JavaPlugin getPlugin() {
		return this.plugin;
	}

}
