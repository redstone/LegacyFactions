package net.redstoneore.legacyfactions;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import net.redstoneore.legacyfactions.cmd.MCommand;
import net.redstoneore.legacyfactions.entity.Board;
import net.redstoneore.legacyfactions.entity.Conf;
import net.redstoneore.legacyfactions.entity.FPlayerColl;
import net.redstoneore.legacyfactions.entity.FactionColl;
import net.redstoneore.legacyfactions.entity.persist.Persist;
import net.redstoneore.legacyfactions.entity.persist.SaveTask;
import net.redstoneore.legacyfactions.listeners.FactionsCommandsListener;
import net.redstoneore.legacyfactions.util.PermUtil;
import net.redstoneore.legacyfactions.util.TextUtil;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.*;
import java.lang.reflect.Modifier;
import java.lang.reflect.Type;
import java.util.*;
import java.util.Map.Entry;
import java.util.logging.Level;

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
    public final Gson gson = this.getGsonBuilder().create();
    private Integer saveTask = null;
    private boolean autoSave = true;
    protected boolean loadSuccessful = false;

    public boolean getAutoSave() {
        return this.autoSave;
    }

    public void setAutoSave(boolean val) {
        this.autoSave = val;
    }
    
    // Listeners
    private FactionsCommandsListener mPluginSecretPlayerListener;

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
    // ENABLE
    // -------------------------------------------------- //
    private long timeEnableStart;

    public boolean preEnable() {
        
        log("=== ENABLE START ===");
        
        this.timeEnableStart = System.currentTimeMillis();

        // Ensure basefolder exists!
        this.getDataFolder().mkdirs();
        
        // Create and register player command listener
        this.mPluginSecretPlayerListener = new FactionsCommandsListener(this);
        this.getServer().getPluginManager().registerEvents(this.mPluginSecretPlayerListener, this);

        // Register recurring tasks
        if (saveTask == null && Conf.saveToFileEveryXMinutes > 0.0) {
            long saveTicks = (long) (20 * 60 * Conf.saveToFileEveryXMinutes); // Approximately every 30 min by default
            saveTask = Bukkit.getServer().getScheduler().scheduleSyncRepeatingTask(this, new SaveTask(this), saveTicks, saveTicks);
        }

        this.loadLang();

        this.loadSuccessful = true;
        
        return true;
    }

    public void postEnable() {
        log("=== ENABLE DONE (Took " + (System.currentTimeMillis() - timeEnableStart) + "ms) ===");
    }

    public void loadLang() {
        File lang = new File(getDataFolder(), "lang.yml");
        OutputStream out = null;
        InputStream defLangStream = this.getResource("lang.yml");
        if (!lang.exists()) {
            try {
                getDataFolder().mkdir();
                lang.createNewFile();
                if (defLangStream != null) {
                    out = new FileOutputStream(lang);
                    int read;
                    byte[] bytes = new byte[1024];

                    while ((read = defLangStream.read(bytes)) != -1) {
                        out.write(bytes, 0, read);
                    }
                    
                    YamlConfiguration defConfig = YamlConfiguration.loadConfiguration(defLangStream);
                    Lang.setFile(defConfig);
                }
            } catch (IOException e) {
                e.printStackTrace(); // So they notice
                getLogger().severe("[Factions] Couldn't create language file.");
                getLogger().severe("[Factions] This is a fatal error. Now disabling");
                this.setEnabled(false); // Without it loaded, we can't send them messages
            } finally {
                if (defLangStream != null) {
                    try {
                        defLangStream.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                if (out != null) {
                    try {
                        out.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                }
            }
        }

        YamlConfiguration conf = YamlConfiguration.loadConfiguration(lang);
        for (Lang item : Lang.values()) {
            if (conf.getString(item.getPath()) == null) {
                conf.set(item.getPath(), item.getDefault());
            }
        }

        // Remove this here because I'm sick of dealing with bug reports due to bad decisions on my part.
        if (conf.getString(Lang.COMMAND_SHOW_POWER.getPath(), "").contains("%5$s")) {
            conf.set(Lang.COMMAND_SHOW_POWER.getPath(), Lang.COMMAND_SHOW_POWER.getDefault());
            log("Removed errant format specifier from f show power.");
        }

        Lang.setFile(conf);
        try {
            conf.save(lang);
        } catch (IOException e) {
            getLogger().log(Level.WARNING, "Factions: Failed to save lang.yml.");
            getLogger().log(Level.WARNING, "Factions: Report this stack trace to drtshock.");
            e.printStackTrace();
        }
    }

    public void onDisable() {
        if (saveTask != null) {
            this.getServer().getScheduler().cancelTask(saveTask);
            saveTask = null;
        }
        // only save data if plugin actually loaded successfully
        if (loadSuccessful) {
            FactionColl.get().forceSave();
            FPlayerColl.save();
            Board.get().forceSave();
        }
        log("Disabled");
    }

    public void suicide() {
        this.getServer().getPluginManager().disablePlugin(this);
    }

    // -------------------------------------------------- //
    // Some inits...
    // You are supposed to override these in the plugin if you aren't satisfied with the defaults
    // The goal is that you always will be satisfied though.
    // -------------------------------------------------- //

    public GsonBuilder getGsonBuilder() {
        return new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().serializeNulls().excludeFieldsWithModifiers(Modifier.TRANSIENT, Modifier.VOLATILE);
    }

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

        Map<String, String> tagsFromFile = this.getPersist().load(type, "tags");
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
                    final List<String> args = new ArrayList<String>(Arrays.asList(commandString.split("\\s+")));
                    args.remove(0);

                    if (testOnly) {
                        return true;
                    }

                    if (async) {
                        Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(this, new Runnable() {
                            @Override
                            public void run() {
                                command.execute(sender, args);
                            }
                        });
                    } else {
                        command.execute(sender, args);
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

	public void debug(String msg) {
		if ( ! Conf.debug) return;
		
    	this.getServer().getConsoleSender().sendMessage(LOG_PREFIX + DEBUG_PREFIX + msg);
	}
    

}
