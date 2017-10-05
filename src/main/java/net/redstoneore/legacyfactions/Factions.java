package net.redstoneore.legacyfactions;

import java.io.IOException;
import java.lang.reflect.Modifier;
import java.lang.reflect.Type;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Arrays;
import java.util.Map;
import java.util.Set;

import com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.google.common.collect.Lists;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import net.redstoneore.legacyfactions.adapter.CrossColourAdapter;
import net.redstoneore.legacyfactions.adapter.CrossEntityTypeAdapter;
import net.redstoneore.legacyfactions.adapter.LazyLocationAdapter;
import net.redstoneore.legacyfactions.adapter.MapFlocationSetAdapter;
import net.redstoneore.legacyfactions.adapter.MapLocalitySetAdapter;
import net.redstoneore.legacyfactions.cmd.CmdFactions;
import net.redstoneore.legacyfactions.config.CommandAliases;
import net.redstoneore.legacyfactions.config.Config;
import net.redstoneore.legacyfactions.config.meta.Meta;
import net.redstoneore.legacyfactions.entity.Board;
import net.redstoneore.legacyfactions.entity.FPlayerColl;
import net.redstoneore.legacyfactions.entity.FactionColl;
import net.redstoneore.legacyfactions.entity.persist.memory.json.FactionsJSON;
import net.redstoneore.legacyfactions.entity.persist.memory.json.JSONBoard;
import net.redstoneore.legacyfactions.entity.persist.memory.json.JSONFPlayerColl;
import net.redstoneore.legacyfactions.entity.persist.memory.json.JSONFactionColl;
import net.redstoneore.legacyfactions.expansion.FactionsExpansions;
import net.redstoneore.legacyfactions.expansion.Provider;
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
import net.redstoneore.legacyfactions.lang.Lang;
import net.redstoneore.legacyfactions.listeners.AbstractConditionalListener;
import net.redstoneore.legacyfactions.listeners.FactionsArmorStandListener;
import net.redstoneore.legacyfactions.listeners.FactionsBlockListener;
import net.redstoneore.legacyfactions.listeners.FactionsCommandsListener;
import net.redstoneore.legacyfactions.listeners.FactionsEntityListener;
import net.redstoneore.legacyfactions.listeners.FactionsExploitListener;
import net.redstoneore.legacyfactions.listeners.FactionsPermissionGroups;
import net.redstoneore.legacyfactions.listeners.FactionsPlayerListener;
import net.redstoneore.legacyfactions.locality.Locality;
import net.redstoneore.legacyfactions.mixin.PlayerMixin;
import net.redstoneore.legacyfactions.placeholder.FactionsPlaceholders;
import net.redstoneore.legacyfactions.task.TaskManager;
import net.redstoneore.legacyfactions.util.LazyLocation;
import net.redstoneore.legacyfactions.util.LibraryUtil;
import net.redstoneore.legacyfactions.util.MiscUtil;
import net.redstoneore.legacyfactions.util.TextUtil;
import net.redstoneore.legacyfactions.util.cross.CrossColour;
import net.redstoneore.legacyfactions.util.cross.CrossEntityType;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.msgpack.jackson.dataformat.MessagePackFactory;

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
	
    private Gson gson = this.getGsonBuilder().create();
    	
    private ObjectMapper objectMapper = new ObjectMapper()
    		.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
    		.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false)
    		.setVisibility(PropertyAccessor.ALL, Visibility.NONE)
    		.setVisibility(PropertyAccessor.FIELD, Visibility.ANY)
    		
    		;
    
    private ObjectMapper msgPackobjectMapper = new ObjectMapper(new MessagePackFactory())
    		.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
    		.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false)
    		.setVisibility(PropertyAccessor.ALL, Visibility.NONE)
    		.setVisibility(PropertyAccessor.FIELD, Visibility.ANY)
    		
    		;
    
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
		
		this.getDataFolder().mkdirs();
		
		this.migrations();
		
		this.timeEnableStart = System.currentTimeMillis();
					
		// Load meta from disk
		Meta.get().load().save();
		
		Lang.reload(null);
		
		// Load Conf from disk
		Config.loadSave();
		
		// Load command aliases from disk
		CommandAliases.load();
		CommandAliases.save();
		if (CommandAliases.baseCommandAliases == null || CommandAliases.baseCommandAliases.isEmpty()) {
			Factions.get().warn("Base command arguments were null or empty, reset to 'f'");
			CommandAliases.baseCommandAliases = Lists.newArrayList("f");
		}
		
		
		// Initialise our persist handler
		Config.backEnd.getHandler().init();
		
		if (this.getDescription().getVersion().contains("RC") || this.getDescription().getVersion().contains("SNAPSHOT")) {
			Config.debug = true;
			this.debug("Debug mode has been enabled for this snapshot.");
			this.debug("conf.json 'debug' has been set to 'true' ");
			this.debug("Please put this entire log file on pastebin.com when reporting an issue.");
			this.debug("To disable this type use the command `/f config debug false`");
			Config.save();
		}
		
		FPlayerColl.load();
		
		FactionColl.get().load();
		FactionColl.get().validate();
		
		Board.get().load();
		Board.get().clean();
		
		TaskManager.get().startTasks();
		
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
			FactionsCommandsListener.get(),
			FactionsPermissionGroups.get()
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
		
		Path oldBoardJson = this.getPluginFolder().resolve("board.json");
		Path oldFactionsJson = this.getPluginFolder().resolve("factions.json");
		Path oldPlayersJson = this.getPluginFolder().resolve("players.json");
		
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
		
		// using new .js format
		
		Path oldConfJson = this.getPluginFolder().resolve("conf.json");
		Path oldCommandAliasesJson = this.getPluginFolder().resolve("commandAliases.json");
		Path oldTagsJson = this.getPluginFolder().resolve("tags.json");

		Path newConfigJs = this.getPluginFolder().resolve("config.js");
		Path newCommandAliasesJs = this.getPluginFolder().resolve("commandAliases.js");
		Path newTagsJs = this.getPluginFolder().resolve("tags.js");
		
		if (Files.exists(oldConfJson)) {
			if (Files.exists(newConfigJs)) {
				Files.move(newConfigJs, Paths.get(newConfigJs.toString() + ".backup"), StandardCopyOption.REPLACE_EXISTING);
				Factions.get().log("Moving 'config.js' -> 'config.js.backup'");
			}
			Files.move(oldConfJson, newConfigJs, StandardCopyOption.REPLACE_EXISTING);
			Factions.get().log("Moving 'config.json' -> 'config.js'");
		}
		
		if (Files.exists(oldCommandAliasesJson)) {
			if (Files.exists(newCommandAliasesJs)) {
				Files.move(newCommandAliasesJs, Paths.get(newCommandAliasesJs.toString() + ".backup"), StandardCopyOption.REPLACE_EXISTING);
				Factions.get().log("Moving 'commandAliases.js' -> 'commandAliases.js.backup'");
			}
			Files.move(oldCommandAliasesJson, newCommandAliasesJs, StandardCopyOption.REPLACE_EXISTING);
			Factions.get().log("Moving 'commandAliases.json' -> 'commandAliases.js'");
		}
		
		if (Files.exists(oldTagsJson)) {
			if (Files.exists(newTagsJs)) {
				Files.move(newTagsJs, Paths.get(newTagsJs.toString() + ".backup"), StandardCopyOption.REPLACE_EXISTING);
				Factions.get().log("Moving 'tags.js' -> 'tags.js.backup'");
			}
			Files.move(oldTagsJson, newTagsJs, StandardCopyOption.REPLACE_EXISTING);
			Factions.get().log("Moving 'tags.json' -> 'tags.js'");
		}
		
		// Move lang.yml to locale.yml
		
		Path localeOld = this.getPluginFolder().resolve("lang.yml");
		Path localeNew = this.getPluginFolder().resolve("locale.yml");
		
		if (Files.exists(localeOld)) {
			if (Files.exists(localeNew)) {
				Files.move(localeNew, Paths.get(localeNew.toString() + ".backup"), StandardCopyOption.REPLACE_EXISTING);
				Factions.get().log("Moving 'locale.yml' -> 'locale.yml.backup'");
			}
			Files.move(localeOld, localeNew, StandardCopyOption.REPLACE_EXISTING);
			Factions.get().log("Moving 'lang.yml' -> 'locale.yml'");
		}

	}
	
	public Gson getGson() {
		return this.gson;
	}
	
	public ObjectMapper getObjectMapper() {
		return this.objectMapper;
	}
	
	public ObjectMapper getMsgPackObjectMapper() {
		return this.msgPackobjectMapper;
	}
	
	public GsonBuilder getGsonBuilder() {
		if (this.gsonBuilder == null) {
			Type mapFLocToStringSetType = new TypeToken<Map<FLocation, Set<String>>>() { }.getType();
			Type mapLocalityToStringSetType = new TypeToken<Map<Locality, Set<String>>>() { }.getType();
			GsonBuilder builder = new GsonBuilder().setPrettyPrinting();
			
			try {
				builder.setLenient();
			} catch (NoSuchMethodError e) {
				// older minecraft plugins don't have this in their version of Gson
			}
			
			this.gsonBuilder = builder.disableHtmlEscaping().excludeFieldsWithModifiers(Modifier.TRANSIENT, Modifier.VOLATILE)
					.registerTypeAdapter(LazyLocation.class, new LazyLocationAdapter())
					.registerTypeAdapter(mapFLocToStringSetType, new MapFlocationSetAdapter())
					.registerTypeAdapter(mapLocalityToStringSetType, new MapLocalitySetAdapter())
					.registerTypeAdapter(CrossColour.class, new CrossColourAdapter())
					.registerTypeAdapter(CrossEntityType.class, new CrossEntityTypeAdapter())
					
					;
		}

		return this.gsonBuilder;
	}

	@Override
	public void onDisable() {
		TaskManager.get().stopTasks();
		
		if (this.loadSuccessful) {
			// Only save data if plugin actually completely loaded successfully
			Config.save();
			CommandAliases.save();
			FactionColl.get().forceSave();
			FPlayerColl.save();
			Board.get().forceSave();
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
	
	private void loadLibraries() {
		if (!MiscUtil.classExists("com.github.benmanes.caffeine.cache.Caffeine")) {
			try {
				LibraryUtil.loadLibrary("https://repo1.maven.org/maven2/com/github/ben-manes/caffeine/caffeine/2.5.5/caffeine-2.5.5.jar");
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		
		if (!MiscUtil.classExists("org.apache.commons.io.Charsets")) {
			try {
				LibraryUtil.loadLibrary("https://repo1.maven.org/maven2/org/apache/commons/commons-io/1.3.2/commons-io-1.3.2.jar");
			} catch (Exception e) {
				e.printStackTrace();
			}
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
	
	// -------------------------------------------------- //
	// DEPRECATED
	// -------------------------------------------------- //

	/**
	 * Deprecated, use {@link PlayerMixin#shouldLetFactionsHandleThisChat(AsyncPlayerChatEvent)}
	 */
	@Deprecated
	public boolean shouldLetFactionsHandleThisChat(AsyncPlayerChatEvent event) {
		return PlayerMixin.shouldLetFactionsHandleThisChat(event);
	}
	
	/**
	 * Deprecated, use {@link PlayerMixin#isPlayerFactionChatting(Player)}
	 */	
	@Deprecated
	public boolean isPlayerFactionChatting(Player player) {
		return PlayerMixin.isPlayerFactionChatting(player);
	}

	/**
	 * Deprecated, use {@link PlayerMixin#isFactionsCommand(Player, String)}
	 */	
	@Deprecated
	public boolean isFactionsCommand(Player player, String check) {
		return PlayerMixin.isFactionsCommand(player, check);
	}

	/**
	 * Deprecated, use {@link PlayerMixin#getPlayerFactionTag(Player)}
	 */	
	@Deprecated
	public String getPlayerFactionTag(Player player) {
		return PlayerMixin.getPlayerFactionTag(player);
	}

	/**
	 * Deprecated, use {@link PlayerMixin#getPlayerFactionTagRelation(Player, Player)}
	 */
	@Deprecated
	public String getPlayerFactionTagRelation(Player speaker, Player listener) {
		return PlayerMixin.getPlayerFactionTagRelation(speaker, listener);
	}
}

