package net.redstoneore.legacyfactions;

import java.lang.reflect.Type;
import java.util.*;
import java.util.logging.Level;

import net.redstoneore.legacyfactions.cmd.MCommand;
import net.redstoneore.legacyfactions.entity.Conf;
import net.redstoneore.legacyfactions.entity.persist.Persist;
import net.redstoneore.legacyfactions.mixin.DebugMixin;
import net.redstoneore.legacyfactions.util.MiscUtil;
import net.redstoneore.legacyfactions.util.PermUtil;
import net.redstoneore.legacyfactions.util.TextUtil;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.java.JavaPlugin;

import com.google.gson.reflect.TypeToken;

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
	
	private Persist utilPersist = null;
	private TextUtil utilText = null;
	private PermUtil utilPerm = null;
	
	public Map<String, String> rawTags = MiscUtil.newMap(
			"l", "<green>",
			"a", "<gold>",
			"b", "<silver>",
			"i", "<yellow>",
			"g", "<lime>",
			"b", "<rose>",
			"h", "<pink>",
			"c", "<aqua>",
			"p", "<teal>"
		);
	
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
	
	public abstract void enable();

	// -------------------------------------------------- //
	// LANG AND TAGS
	// -------------------------------------------------- //
	
	public TextUtil getTextUtil() {
		if (this.utilText == null) {
			// Init this now
			TextUtil txt = new TextUtil();

			Type type = new TypeToken<Map<String, String>>() { }.getType();
			
			Map<String, String> tagsFromFile = null;
			try { 
				tagsFromFile = this.getPersist().load(type, "tags");
			} catch (Exception e) {
				// Fail silently
			}
			
			if (tagsFromFile != null) {
				this.rawTags.putAll(tagsFromFile);
			}
			this.getPersist().save(this.rawTags, "tags");
			
			this.rawTags.entrySet().forEach(rawTag -> {
				txt.tags.put(rawTag.getKey(), TextUtil.parseColor(rawTag.getValue()));
			});
			
			this.utilText = txt;
		}
		return this.utilText;
	}
	
	public PermUtil getPermUtil() {
		if (this.utilPerm == null) {
			this.utilPerm = new PermUtil();
		}
		return this.utilPerm;
	}
	
	public Persist getPersist() {
		if (this.utilPersist == null) {
			this.utilPersist = new Persist();
		}
		return this.utilPersist;
	}
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

		for (final MCommand<?> command : Volatile.get().baseCommands()) {
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
		this.getServer().getConsoleSender().sendMessage(LOG_PREFIX + this.getTextUtil().parse(msg));
	}
	
	public void log(String str, Object... args) {
		this.getServer().getConsoleSender().sendMessage(LOG_PREFIX + this.getTextUtil().parse(str, args));
	}
	
	public void warn(String str, Object... args) {
		this.getServer().getConsoleSender().sendMessage(LOG_PREFIX + WARN_PREFIX + this.getTextUtil().parse(str, args));
	}
	
	public void error(String str, Object... args) {
		this.getServer().getConsoleSender().sendMessage(LOG_PREFIX + ERROR_PREFIX + this.getTextUtil().parse(str, args));
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
	
	@Deprecated
	public Map<UUID, Integer> getStuckMap() {
		return Volatile.get().stuckMap();
	}

	@Deprecated
	public Map<UUID, Long> getTimers() {
		return Volatile.get().stuckTimers();
	}
	
	@Deprecated
	public List<MCommand<?>> getBaseCommands() {
		return Volatile.get().baseCommands();
	}

}
