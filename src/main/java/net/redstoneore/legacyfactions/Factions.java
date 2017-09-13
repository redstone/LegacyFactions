package net.redstoneore.legacyfactions;

import java.io.IOException;
import java.lang.reflect.Modifier;
import java.lang.reflect.Type;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Map;
import java.util.Set;

import com.google.common.collect.Lists;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import net.redstoneore.legacyfactions.adapter.CrossColourAdapter;
import net.redstoneore.legacyfactions.adapter.CrossEntityTypeAdapter;
import net.redstoneore.legacyfactions.adapter.LazyLocationAdapter;
import net.redstoneore.legacyfactions.adapter.MapFlocationSetAdapter;
import net.redstoneore.legacyfactions.cmd.CmdFactions;
import net.redstoneore.legacyfactions.entity.Board;
import net.redstoneore.legacyfactions.entity.CommandAliases;
import net.redstoneore.legacyfactions.entity.Conf;
import net.redstoneore.legacyfactions.entity.FPlayer;
import net.redstoneore.legacyfactions.entity.FPlayerColl;
import net.redstoneore.legacyfactions.entity.Faction;
import net.redstoneore.legacyfactions.entity.FactionColl;
import net.redstoneore.legacyfactions.entity.Meta;
import net.redstoneore.legacyfactions.entity.persist.SaveTask;
import net.redstoneore.legacyfactions.entity.persist.memory.json.FactionsJSON;
import net.redstoneore.legacyfactions.entity.persist.memory.json.JSONBoard;
import net.redstoneore.legacyfactions.entity.persist.memory.json.JSONFPlayerColl;
import net.redstoneore.legacyfactions.entity.persist.memory.json.JSONFactionColl;
import net.redstoneore.legacyfactions.expansion.FactionsExpansions;
import net.redstoneore.legacyfactions.expansion.Provider;
import net.redstoneore.legacyfactions.expansion.chat.ChatMode;
import net.redstoneore.legacyfactions.flag.Flags;
import net.redstoneore.legacyfactions.integration.Integrations;
import net.redstoneore.legacyfactions.integration.bstats.BStatsIntegration;
import net.redstoneore.legacyfactions.integration.dynmap.DynmapIntegration;
import net.redstoneore.legacyfactions.integration.essentials.EssentialsIntegration;
import net.redstoneore.legacyfactions.integration.metrics.MetricsIntegration;
import net.redstoneore.legacyfactions.integration.novucsftop.NovucsFactionsTopIntegration;
import net.redstoneore.legacyfactions.integration.playervaults.PlayerVaultsIntegration;
import net.redstoneore.legacyfactions.integration.vault.VaultIntegration;
import net.redstoneore.legacyfactions.integration.venturechat.VentureChatIntegration;
import net.redstoneore.legacyfactions.integration.worldguard.WorldGuardIntegration;
import net.redstoneore.legacyfactions.listeners.AbstractConditionalListener;
import net.redstoneore.legacyfactions.listeners.FactionsArmorStandListener;
import net.redstoneore.legacyfactions.listeners.FactionsBlockListener;
import net.redstoneore.legacyfactions.listeners.FactionsCommandsListener;
import net.redstoneore.legacyfactions.listeners.FactionsEntityListener;
import net.redstoneore.legacyfactions.listeners.FactionsExploitListener;
import net.redstoneore.legacyfactions.listeners.FactionsPlayerListener;
import net.redstoneore.legacyfactions.placeholder.FactionsPlaceholders;
import net.redstoneore.legacyfactions.task.AutoLeaveTask;
import net.redstoneore.legacyfactions.util.LazyLocation;
import net.redstoneore.legacyfactions.util.LibraryUtil;
import net.redstoneore.legacyfactions.util.TextUtil;
import net.redstoneore.legacyfactions.util.cross.CrossColour;
import net.redstoneore.legacyfactions.util.cross.CrossEntityType;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

/**
 *  A high-performance maintained version of Factions 1.6.<br>
 *  Copyright (C) 2011 Olof Larsson  <br>
 *  Copyright (C) 2011 - 2017 contributors <br>
 *  <br>
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.<br>
 *  <br>
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 *  GNU General Public License for more details.<br>
 *  <br>
 *  You should have received a copy of the GNU General Public License
 *  along with this program.  If not, see http://www.gnu.org/licenses/
 *
 */
public class Factions extends FactionsPluginBase {

	// -------------------------------------------------- //
	// INSTANCE & CONSTRUCT 
	// -------------------------------------------------- //
	
	private static Factions instance = null;
	public static Factions get() { return instance; }
	
	// -------------------------------------------------- //
	// FIELDS
	// -------------------------------------------------- //

	private GsonBuilder gsonBuilder = null;

	private Integer taskAutoLeave = null;
		
	private Integer saveTask = null;
	
    private Gson gson = this.getGsonBuilder().create();
    	
	protected boolean loadSuccessful = false;
	
	// -------------------------------------------------- //
	// METHODS
	// -------------------------------------------------- //
    
	/**
	 * Check if writing is currently locked.
	 * @return true if locked
	 */
	public boolean isLocked() {
		return Volatile.get().locked();
	}
	
	/**
	 * Lock the database from writing. 
	 * @param locked 
	 */
	public void setLocked(boolean locked) {
		Volatile.get().locked(locked);
		this.setAutoSave(locked);
	}
	
	/**
	 * Get the autosave state.
	 * @return autosave state
	 */
	public boolean getAutoSave() {
		return Volatile.get().autosave();
	}

	/**
	 * Set the autosave state.
	 * @param autoSave the autosave state.
	 */
	public void setAutoSave(boolean autoSave) {
		Volatile.get().autosave(autoSave);
	}
	
	public Path getPluginFolder() {
		return Paths.get(this.getDataFolder().getAbsolutePath());
	}
	
	@Override
	public void enable() throws Exception {
		if (instance != null || Volatile.get().provider() != null) {
			this.warn("Unsafe reload detected!");
		}
		instance = this;
		
		this.loadSuccessful = false;
		
		this.loadLibraries();
		
		Volatile.get().provider(Provider.of("LegacyFactions", this));
		
		// Ensure plugin folder exists
		this.getDataFolder().mkdirs();
		
		this.migrations();
		
		this.timeEnableStart = System.currentTimeMillis();
						
		Lang.reload();
		
		// Load Conf from disk
		Conf.loadSave();
		
		// Load command aliases from disk
		CommandAliases.load();
		CommandAliases.save();
		if (CommandAliases.baseCommandAliases == null || CommandAliases.baseCommandAliases.isEmpty()) {
			Factions.get().warn("Base command arguments were null or empty, reset to 'f'");
			CommandAliases.baseCommandAliases = Lists.newArrayList("f");
		}
		
		// Load meta from disk
		Meta.get().load().save();
		
		// Initialise our persist handler
		Conf.backEnd.getHandler().init();
		
		if (this.getDescription().getVersion().contains("RC") || this.getDescription().getVersion().contains("SNAPSHOT")) {
			Conf.debug = true;
			this.debug("Debug mode has been enabled for this snapshot.");
			this.debug("conf.json 'debug' has been set to 'true' ");
			this.debug("Please put this entire log file on pastebin.com when reporting an issue.");
			this.debug("To disable this type use the command `/f config debug false`");
			Conf.save();
		}
		
		FPlayerColl.load();
		FactionColl.get().load();
		
		FPlayerColl.all(fplayer -> {
			Faction faction = fplayer.getFaction();
			if (faction == null) {
				this.log("Invalid faction id on " + fplayer.getName() + ":" + fplayer.getFactionId());
				fplayer.resetFactionData();
				return;
			}
			faction.addFPlayer(fplayer);
		});
		
		Board.get().load();
		Board.get().clean();

		// Start tasks.
		this.startTasks();

		// Add base commands.
		Volatile.get().baseCommands().add(CmdFactions.get());

		// Add our integrations.
		Integrations.add(
			VaultIntegration.get(),
			WorldGuardIntegration.get(),
			EssentialsIntegration.get(),
			PlayerVaultsIntegration.get(),
			MetricsIntegration.get(),
			BStatsIntegration.get(),
			VentureChatIntegration.get(),
			NovucsFactionsTopIntegration.get(),
			DynmapIntegration.get()
		);
		
		// Sync expansions
		FactionsExpansions.sync();
		
		// Add our placeholders.
		FactionsPlaceholders.get().init();
		FactionsPlaceholders.get().adaptAll();
		
		// Register Event Handlers
		this.register(
			FactionsPlayerListener.get(),
			FactionsEntityListener.get(),
			FactionsExploitListener.get(),
			FactionsBlockListener.get(),
			FactionsArmorStandListener.get(),
			FactionsCommandsListener.get()
		);
		
		// since some other plugins execute commands directly through this command interface, provide it
		CommandAliases.baseCommandAliases.forEach(ref -> {
			if (ref != null) {
				this.getCommand(ref).setExecutor(this);
			}
		});
				
		this.loadSuccessful = true;
		
		FactionColl.all();
		
		// Initialise default flags.
		Flags.init();
	}
	
	private void migrations() throws IOException {
		// Move all database files in database folder 
		if (!Files.exists(FactionsJSON.getDatabasePath())) {
			Files.createDirectories(FactionsJSON.getDatabasePath());
		}
		
		Path oldBoardJson = Paths.get(this.getDataFolder().toString(), "board.json");
		Path oldFactionsJson = Paths.get(this.getDataFolder().toString(), "factions.json");
		Path oldPlayersJson = Paths.get(this.getDataFolder().toString(), "players.json");
		
		if (Files.exists(oldBoardJson)) {
			if (Files.exists(JSONBoard.getJsonFile())) {
				Files.move(JSONBoard.getJsonFile(), Paths.get(JSONBoard.getJsonFile().toString() + ".backup"));
				Factions.get().log("Moving 'database/board.json' -> 'database/board.json.backup'");
			}
			
			Files.move(oldBoardJson, JSONBoard.getJsonFile());
			Factions.get().log("Moving 'board.json' -> 'database/board.json'");
		}
		
		if (Files.exists(oldFactionsJson)) {
			if (Files.exists(JSONFactionColl.getJsonFile())) {
				Files.move(JSONFactionColl.getJsonFile(), Paths.get(JSONFactionColl.getJsonFile().toString() + ".backup"));
				Factions.get().log("Moving 'database/factions.json' -> 'database/factions.json.backup'");
			}
			
			Files.move(oldFactionsJson, JSONFactionColl.getJsonFile());
			Factions.get().log("Moving 'factions.json' -> 'database/factions.json'");
		}
		
		if (Files.exists(oldPlayersJson)) {
			if (Files.exists(JSONFPlayerColl.getJsonFile())) {
				Files.move(JSONFPlayerColl.getJsonFile(), Paths.get(JSONFPlayerColl.getJsonFile().toString() + ".backup"));
				Factions.get().log("Moving 'database/players.json' -> 'database/players.json.backup'");
			}
			
			Files.move(oldPlayersJson, JSONFPlayerColl.getJsonFile());
			Factions.get().log("Moving 'players.json' -> 'database/players.json'");
		}
	}
	
	public Gson getGson() {
		return this.gson;
	}
	
	public GsonBuilder getGsonBuilder() {
		if (this.gsonBuilder == null) {
			Type mapFLocToStringSetType = new TypeToken<Map<FLocation, Set<String>>>() { }.getType();
			GsonBuilder builder = new GsonBuilder().setPrettyPrinting();
			
			try {
				builder.setLenient();
			} catch (NoSuchMethodError e) {
				// older minecraft plugins don't have this in their version of Gson
			}
			
			this.gsonBuilder = builder.disableHtmlEscaping().excludeFieldsWithModifiers(Modifier.TRANSIENT, Modifier.VOLATILE)
					.registerTypeAdapter(LazyLocation.class, new LazyLocationAdapter())
					.registerTypeAdapter(mapFLocToStringSetType, new MapFlocationSetAdapter())
					.registerTypeAdapter(CrossColour.class, new CrossColourAdapter())
					.registerTypeAdapter(CrossEntityType.class, new CrossEntityTypeAdapter())
					;
		}

		return this.gsonBuilder;
	}

	@Override
	public void onDisable() {
		// only save data if plugin actually completely loaded successfully
		if (this.loadSuccessful) {
			Conf.save();
			CommandAliases.save();
			FactionColl.get().forceSave();
			FPlayerColl.save();
			Board.get().forceSave();
		}
		
		this.stopTasks();
		
		this.log("Disabled");
	}

	public void startAutoLeaveTask(boolean restartIfRunning) {
		if (taskAutoLeave != null) {
			if (!restartIfRunning) return;
			this.getServer().getScheduler().cancelTask(this.taskAutoLeave);
		}

		if (Conf.autoLeaveRoutineRunsEveryXMinutes > 0.0) {
			long ticks = (long) (20 * 60 * Conf.autoLeaveRoutineRunsEveryXMinutes);
			this.taskAutoLeave = getServer().getScheduler().scheduleSyncRepeatingTask(this, new AutoLeaveTask(), ticks, ticks);
		}
	}

	@Override
	public boolean handleCommand(CommandSender sender, String commandString, boolean testOnly) {
		return sender instanceof Player && FactionsCommandsListener.get().preventCommand(commandString, (Player) sender, testOnly) || super.handleCommand(sender, commandString, testOnly);
	}

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] split) {
		if (split.length == 0) return this.handleCommand(sender, "/f help", false);
		
		// otherwise, needs to be handled; presumably another plugin directly ran the command
		String cmd = CommandAliases.baseCommandAliases.isEmpty() ? "/f" : "/" + CommandAliases.baseCommandAliases.get(0);
		return this.handleCommand(sender, cmd + " " + TextUtil.implode(Arrays.asList(split), " "), false);
	}

	// -------------------------------------------------- //
	// Functions for other plugins to hook into
	// -------------------------------------------------- //
	// TODO: move to mixins
	
	// Simply put, should this chat event be left for Factions to handle? For now, that means players with Faction Chat
	// enabled or use of the Factions f command without a slash; combination of isPlayerFactionChatting() and isFactionsCommand()

	public boolean shouldLetFactionsHandleThisChat(AsyncPlayerChatEvent event) {
		return event != null && (this.isPlayerFactionChatting(event.getPlayer()) || this.isFactionsCommand(event.getPlayer(), event.getMessage()));
	}

	// Does player have Faction Chat enabled? If so, chat plugins should preferably not do channels,
	// local chat, or anything else which targets individual recipients, so Faction Chat can be done
	public boolean isPlayerFactionChatting(Player player) {
		if (player == null) return false;
		
		FPlayer me = FPlayerColl.get(player);

		return me != null && me.getChatMode() != ChatMode.PUBLIC;
	}

	// Is this chat message actually a Factions command, and thus should be left alone by other plugins?
	public boolean isFactionsCommand(Player player, String check) {
		return !(check == null || check.isEmpty()) && this.handleCommand(player, check, true);
	}

	// Get a player's faction tag (faction name), mainly for usage by chat plugins for local/channel chat
	public String getPlayerFactionTag(Player player) {
		return this.getPlayerFactionTagRelation(player, null);
	}

	// Same as above, but with relation (enemy/neutral/ally) coloring potentially added to the tag
	public String getPlayerFactionTagRelation(Player speaker, Player listener) {
		String tag = "~";
		
		// Invalid speaker, use default tag
		if (speaker == null) return tag;
		
		FPlayer me = FPlayerColl.get(speaker);
		
		// Invalid FPlayer, use default tag
		if (me == null) return tag;
		
		// if listener isn't set, or config option is disabled, give back uncolored tag
		if (listener == null) {
			tag = me.getChatTag().trim();
		} else {
			FPlayer you = FPlayerColl.get(listener);
			if (you == null) {
				tag = me.getChatTag().trim();
			} else {
				tag = me.getChatTag(you).trim();
			}
		}
		
		if (tag.isEmpty()) {
			tag = "~";
		}
		
		return tag;
	}
	
	private void loadLibraries() {
		if (!classExists("com.github.benmanes.caffeine.cache.Caffeine")) {
			try {
				LibraryUtil.loadLibrary("https://repo1.maven.org/maven2/com/github/ben-manes/caffeine/caffeine/2.5.5/caffeine-2.5.5.jar");
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
	
	private boolean classExists(String name) {
		try {
			Class.forName(name);
			return true;
		} catch (Exception e) {
			return false;
		}
	}
	
	/**
	 * Start recurring tasks
	 */
	private void startTasks() {
		// start up task which runs the autoLeaveAfterDaysOfInactivity routine
		this.startAutoLeaveTask(false);
		
		if (this.saveTask == null && Conf.saveToFileEveryXMinutes > 0.0) {
			long saveTicks = (long) (20 * 60 * Conf.saveToFileEveryXMinutes); // Approximately every 30 min by default
			this.saveTask = Bukkit.getServer().getScheduler().scheduleSyncRepeatingTask(this, new SaveTask(), saveTicks, saveTicks);
		}
	}
	
	/**
	 * Stop recurring tasks
	 */
	private void stopTasks() {
		if (this.taskAutoLeave != null) {
			this.getServer().getScheduler().cancelTask(this.taskAutoLeave);
			this.taskAutoLeave = null;
		}

		if (this.saveTask != null) {
			this.getServer().getScheduler().cancelTask(this.saveTask);
			this.saveTask = null;
		}
	}
	
	// -------------------------------------------------- //
	// LISTENERS
	// -------------------------------------------------- //
	
	public void register(Listener... listeners) {
		for (Listener listener : listeners) {
			if (listener instanceof AbstractConditionalListener) {
				AbstractConditionalListener conditionalListener = (AbstractConditionalListener) listener;
				if (conditionalListener.shouldEnable()) {
					this.getServer().getPluginManager().registerEvents(listener, this);
				}
			} else {
				this.getServer().getPluginManager().registerEvents(listener, this);
			}
		}
	}
	
}

