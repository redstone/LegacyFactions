package net.redstoneore.legacyfactions;

import java.util.*;
import java.util.logging.Level;

import net.redstoneore.legacyfactions.cmd.FCommand;
import net.redstoneore.legacyfactions.entity.Conf;
import net.redstoneore.legacyfactions.entity.persist.Persist;
import net.redstoneore.legacyfactions.mixin.DebugMixin;
import net.redstoneore.legacyfactions.util.PermUtil;
import net.redstoneore.legacyfactions.util.TextUtil;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.java.JavaPlugin;

public abstract class FactionsPluginBase extends JavaPlugin {
	
	// -------------------------------------------------- //
	// CONSTANTS
	// -------------------------------------------------- //

	public static final String LOG_PREFIX = ChatColor.AQUA+""+ ChatColor.BOLD + "[LegacyFactions] " + ChatColor.RESET;
	public static final String WARN_PREFIX = ChatColor.GOLD+""+ ChatColor.BOLD + "[WARN] " + ChatColor.RESET;
	public static final String ERROR_PREFIX = ChatColor.RED+""+ ChatColor.BOLD + "[ERROR] " + ChatColor.RESET;
	public static final String DEBUG_PREFIX = ChatColor.LIGHT_PURPLE+""+ ChatColor.BOLD + "[DEBUG] " + ChatColor.RESET;
	
	// -------------------------------------------------- //
	// FIELDS
	// -------------------------------------------------- //
	
	protected long timeEnableStart;
	
	// -------------------------------------------------- //
	// ENABLE
	// -------------------------------------------------- //
	
	@Override
	public final void onEnable() {
		this.simpleLog(ChatColor.WHITE + "=== ENABLE START ===");
		
		try {
			this.enable();
		} catch (Throwable e) {
			this.simpleLog(ChatColor.RED + "=== ENABLE FAILED ===");
			e.printStackTrace();
			Bukkit.getServer().getPluginManager().disablePlugin(this);
			
			try {
				DebugMixin.sendToConsole();
			} catch (Throwable e2) {
				this.simpleLog(ChatColor.RED + "=== DEBUG FAILED ===");
				e2.printStackTrace();
				this.simpleLog(ChatColor.RED + "=== DEBUG HALTED ===");
			}
			this.simpleLog(ChatColor.RED + "=== ENABLE HALTED ===");
			return;
		}
		
		this.log(ChatColor.WHITE + "=== ENABLE DONE (Took " + (System.currentTimeMillis() - this.timeEnableStart) + "ms) ===");	
	}
	
	public abstract void enable() throws Exception;
	
	// -------------------------------------------------- //
	// COMMAND HANDLING
	// -------------------------------------------------- //
	

	public boolean handleCommand(CommandSender sender, String commandString, boolean testOnly) {
		return handleCommand(sender, commandString, testOnly, false);
	}

	public boolean handleCommand(final CommandSender sender, String commandString, boolean testOnly, boolean async) {
		boolean noSlash = true;
		if (commandString.startsWith("/")) {
			noSlash = false;
			commandString = commandString.substring(1);
		}

		for (final FCommand command : Volatile.get().baseCommands()) {
			if (noSlash && !command.allowNoSlashAccess()) {
				continue;
			}

			for (String alias : command.aliases) {
				// disallow double-space after alias, so specific commands can be prevented (preventing "f home" won't prevent "f  home")
				if (commandString.startsWith(alias + "  ")) {
					return false;
				}

				if (commandString.startsWith(alias + " ") || commandString.equals(alias)) {
					final List<String> arguments = new ArrayList<>(Arrays.asList(commandString.split("\\s+")));
					arguments.remove(0);

					if (testOnly) {
						return true;
					}

					if (async) {
						Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(this, () -> {
							command.execute(sender, arguments);
						});
					} else {
						command.execute(sender, arguments);
					}

					return true;
				}
			}
		}
		return false;
	}

	public boolean handleCommand(CommandSender sender, String commandString) {
		return this.handleCommand(sender, commandString, false);
	}

	// -------------------------------------------------- //
	// LOGGING
	// -------------------------------------------------- //
	
	public void log(String msg) {
		this.getServer().getConsoleSender().sendMessage(LOG_PREFIX + TextUtil.get().parse(msg));
	}
	
	public void log(String str, Object... args) {
		this.getServer().getConsoleSender().sendMessage(LOG_PREFIX + TextUtil.get().parse(str, args));
	}
	
	public void warn(String str, Object... args) {
		// Do not use internal libraries for warnings, just send to the logger.
		Factions.get().getLogger().log(Level.WARNING, LOG_PREFIX + WARN_PREFIX + String.format(str, args));
	}
	
	public void error(String str, Object... args) {
		// Do not use internal libraries for errors, just send to the logger.
		Factions.get().getLogger().log(Level.WARNING, LOG_PREFIX + ERROR_PREFIX + String.format(str, args));
	}

	public void debug(String message) {
		if (!Conf.debug) return;
		this.getLogger().log(Level.FINE, message);
		this.getServer().getConsoleSender().sendMessage(LOG_PREFIX + DEBUG_PREFIX + message);
	}
	
	public void debug(Level level, String s) {
		if (!Conf.debug) return;
		this.debug(s);
	}
	
	/**
	 * Very simple log utility used in {@link enable} method only.
	 * @param message
	 */
	private void simpleLog(String message) {
		Bukkit.getServer().getConsoleSender().sendMessage(LOG_PREFIX + message);
	}
	
	// -------------------------------------------- //
	// DEPRECATED METHODS
	// -------------------------------------------- //
	
	/**
	 * Deprecated, use {@link TextUtil#get()}
	 */
	@Deprecated
	public TextUtil getTextUtil() {
		return TextUtil.get();
	}
	
	/**
	 * Deprecated, use {@link PermUtil#get()}
	 */
	@Deprecated
	public PermUtil getPermUtil() {
		return PermUtil.get();
	}
	
	/**
	 * Deprecated, use {@link Persist#get()}
	 */
	@Deprecated
	public Persist getPersist() {
		return Persist.get();
	}

}
