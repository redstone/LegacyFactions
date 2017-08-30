package net.redstoneore.legacyfactions.expansion;

import org.bukkit.plugin.java.JavaPlugin;

import net.redstoneore.legacyfactions.Factions;
import net.redstoneore.legacyfactions.Volatile;
import net.redstoneore.legacyfactions.exception.PluginAlreadyInitiatedException;

public class Provider {

	// -------------------------------------------------- //
	// STATIC METHODS
	// -------------------------------------------------- //
	
	public static Provider of(String name, JavaPlugin plugin) throws PluginAlreadyInitiatedException {
		if (plugin == Factions.get()) {
			if (Volatile.get().provider() != null) {
				throw new PluginAlreadyInitiatedException(plugin);
			}
		}
		return new Provider(name, plugin);
	}
	
	// -------------------------------------------------- //
	// CONSTRUCT
	// -------------------------------------------------- //
	
	private Provider(String uniqueName, JavaPlugin plugin) {
		this.uniqueName = uniqueName;
		this.plugin = plugin;

	}
	
	// -------------------------------------------------- //
	// FIELDS
	// -------------------------------------------------- //
	
	private final String uniqueName;
	private final JavaPlugin plugin;
	
	// -------------------------------------------------- //
	// METHODS
	// -------------------------------------------------- //
	
	public String getUniqueName() {
		return this.uniqueName;
	}
	
	public String getSimpleName() {
		if (this.plugin != Factions.get()) {
			return "external_" + this.uniqueName.toLowerCase();
		}
		return this.uniqueName;
	}
	
	public JavaPlugin getPlugin() {
		return this.plugin;
	}
	
	public boolean isPluginEnabled() {
		return this.plugin.isEnabled();
	}
	
}
