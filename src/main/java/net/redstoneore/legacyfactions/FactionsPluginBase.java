package net.redstoneore.legacyfactions;

import java.lang.reflect.Type;
import java.util.*;
import java.util.Map.Entry;
import java.util.logging.Level;

import com.google.gson.reflect.TypeToken;

import net.redstoneore.legacyfactions.cmd.MCommand;
import net.redstoneore.legacyfactions.entity.Conf;
import net.redstoneore.legacyfactions.entity.persist.Persist;
import net.redstoneore.legacyfactions.util.PermUtil;
import net.redstoneore.legacyfactions.util.TextUtil;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

public abstract class FactionsPluginBase extends JavaPlugin {
	
	// -------------------------------------------------- //
	// STATIC FIELDS
	// -------------------------------------------------- //

	public static final String LOG_PREFIX = ChatColor.AQUA+""+ ChatColor.BOLD + "[LegacyFactions] " + ChatColor.RESET;
	public static final String WARN_PREFIX = ChatColor.GOLD+""+ ChatColor.BOLD + "[WARN] " + ChatColor.RESET;
	public static final String ERROR_PREFIX = ChatColor.RED+""+ ChatColor.BOLD + "[ERROR] " + ChatColor.RESET;
	public static final String DEBUG_PREFIX = ChatColor.LIGHT_PURPLE+""+ ChatColor.BOLD + "[DEBUG] " + ChatColor.RESET;
	 
	// -------------------------------------------------- //
	// UTILS
	// -------------------------------------------------- //

	// Utils
	private Persist persist = null;
	private TextUtil txt = null;
	private PermUtil perm = null;

	public TextUtil getTextUtil() {
		if (this.txt == null) {
			this.txt = this.initTXT();
		}
		return this.txt;
	}
	
	public PermUtil getPermUtil() {
		if (this.perm == null) {
			this.perm = new PermUtil();
		}
		return this.perm;
	}
	
	public Persist getPersist() {
		if (this.persist == null) {
			this.persist = new Persist();
		}
		
		return this.persist;
	}
	
	
	// Persist related
	private boolean autoSave = true;
	protected boolean loadSuccessful = false;

	public boolean getAutoSave() {
		return this.autoSave;
	}

	public void setAutoSave(boolean val) {
		this.autoSave = val;
	}
	
	// Our stored base commands
	private List<MCommand<?>> baseCommands = new ArrayList<>();

	public List<MCommand<?>> getBaseCommands() {
		return this.baseCommands;
	}

	// holds f stuck start times
	private Map<UUID, Long> timers = new HashMap<>();

	//holds f stuck taskids
	public Map<UUID, Integer> stuckMap = new HashMap<>();

	// -------------------------------------------------- //
	// LANG AND TAGS
	// -------------------------------------------------- //

	// These are not supposed to be used directly.
	// They are loaded and used through the TextUtil instance for the plugin.
	public Map<String, String> rawTags = new HashMap<>();

	public void addRawTags() {
		if (this.rawTags == null) {
			this.rawTags = new HashMap<>();
		}
		this.rawTags.put("l", "<green>"); // logo
		this.rawTags.put("a", "<gold>"); // art
		this.rawTags.put("n", "<silver>"); // notice
		this.rawTags.put("i", "<yellow>"); // info
		this.rawTags.put("g", "<lime>"); // good
		this.rawTags.put("b", "<rose>"); // bad
		this.rawTags.put("h", "<pink>"); // highligh
		this.rawTags.put("c", "<aqua>"); // command
		this.rawTags.put("p", "<teal>"); // parameter
	}

	public TextUtil initTXT() {
		TextUtil txt = new TextUtil();
		this.addRawTags();

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

		for (Entry<String, String> rawTag : this.rawTags.entrySet()) {
			txt.tags.put(rawTag.getKey(), TextUtil.parseColor(rawTag.getValue()));
		}
		
		return txt;
	}

	// -------------------------------------------------- //
	// COMMAND HANDLING
	// -------------------------------------------------- //

	// can be overridden by P method, to provide option
	public boolean logPlayerCommands() {
		return true;
	}

	public boolean handleCommand(CommandSender sender, String commandString, boolean testOnly) {
		return handleCommand(sender, commandString, testOnly, false);
	}

	public boolean handleCommand(final CommandSender sender, String commandString, boolean testOnly, boolean async) {
		boolean noSlash = true;
		if (commandString.startsWith("/")) {
			noSlash = false;
			commandString = commandString.substring(1);
		}

		for (final MCommand<?> command : this.getBaseCommands()) {
			if (noSlash && !command.allowNoSlashAccess) {
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
						Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(this, new Runnable() {
							@Override
							public void run() {
								command.execute(sender, arguments);
							}
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

	// -------------------------------------------- //
	// HOOKS
	// -------------------------------------------- //
	public void preAutoSave() {

	}

	public void postAutoSave() {

	}

	public Map<UUID, Integer> getStuckMap() {
		return this.stuckMap;
	}

	public Map<UUID, Long> getTimers() {
		return this.timers;
	}

	// -------------------------------------------- //
	// LOGGING
	// -------------------------------------------- //
	
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
	
	// -------------------------------------------- //
	// LISTENERS
	// -------------------------------------------- //
	
	public void register(Listener... listeners) {
		for (Listener listener : listeners) {
			this.getServer().getPluginManager().registerEvents(listener, this);
		}
	}

}
