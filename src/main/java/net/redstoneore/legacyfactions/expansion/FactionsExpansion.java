package net.redstoneore.legacyfactions.expansion;

import java.util.Collection;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;

import net.redstoneore.legacyfactions.Factions;
import net.redstoneore.legacyfactions.cmd.CmdFactions;
import net.redstoneore.legacyfactions.cmd.CmdFactionsHelp;
import net.redstoneore.legacyfactions.cmd.FCommand;

/**
 * Create an expansion for LegacyFactions using this class.
 */
public abstract class FactionsExpansion {

	// -------------------------------------------------- //
	// FIELDS
	// -------------------------------------------------- //

	private boolean enabled = false;
	
	// -------------------------------------------------- //
	// METHODS
	// -------------------------------------------------- //
	
	public final boolean isEnabled() {
		return this.enabled;
	}
	
	public final void enable() {
		this.onPreEnable();
		
		if (this.getCommands().size() > 0) {
			this.getCommands().forEach(command -> 
				CmdFactions.get().addSubCommand(command)
			);
			
			// Clear the help page cache.
			CmdFactionsHelp.get().clearHelpPageCache();
			
			this.onCommandsEnabled();
		}
		
		this.getListeners().forEach(listener -> 
			Bukkit.getPluginManager().registerEvents(listener, Factions.get())
		);
		
		this.onListenersEnabled();
		
		this.onPostEnable();
		
		this.enabled = true;
		Factions.get().log(ChatColor.DARK_PURPLE + "[Expansion] " + ChatColor.WHITE + this.getName() + " Enabled");
	}
	
	public final void disable() {
		this.onPreDisable();
		
		if (this.getCommands().size() > 0) {
			this.getCommands().forEach(command -> 
				CmdFactions.get().removeSubcommand(command)
			);
			
			// Clear the help page cache.
			CmdFactionsHelp.get().clearHelpPageCache();
			
			this.onCommandsDisabled();
		}
		
		this.getListeners().forEach(listener -> 
			HandlerList.unregisterAll(listener)
		);
		
		this.onListenersDisabled();

		this.onPostDisable();
		
		this.enabled = false;
	}
	
	// -------------------------------------------------- //
	// OPTIONAL OVERRIDES
	// -------------------------------------------------- //
	
	public abstract String getName();
	
	public void onPreEnable() { }
	public void onPostEnable() { }

	public void onCommandsEnabled() { }
	public void onListenersEnabled() { }
	
	public void onPreDisable() { }
	public void onPostDisable() { }

	public void onCommandsDisabled() { }
	public void onListenersDisabled() { }
	
	
	// -------------------------------------------------- //
	// ABSTRACT METHODS
	// -------------------------------------------------- //
	
	/**
	 * Get commands for this expansion, can be an empty collection.
	 * @return commands for this expansion
	 */
	public abstract Collection<FCommand> getCommands();
	
	
	/**
	 * Get listeners for this expansion, can be an empty collection.
	 * @return listeners for this expansion
	 */
	public abstract Collection<Listener> getListeners();
	
	/**
	 * Should this expansion be enabled?
	 * @return true if should be enabled
	 */
	public abstract boolean shouldEnable();
	
}
