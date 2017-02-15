/**
 *   A maintained version of the Factions fork FactionsUUID.
 *   Copyright (C) Olof Larsson 2011, and contributors 
 *
 *   This program is free software: you can redistribute it and/or modify
 *   it under the terms of the GNU General Public License as published by
 *   the Free Software Foundation, either version 3 of the License, or
 *   (at your option) any later version.
 *
 *   This program is distributed in the hope that it will be useful,
 *   but WITHOUT ANY WARRANTY; without even the implied warranty of
 *   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *   GNU General Public License for more details.
 *
 *   You should have received a copy of the GNU General Public License
 *   along with this program.  If not, see <http://www.gnu.org/licenses/>.
 *
 */
package com.massivecraft.legacyfactions;

import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.massivecraft.legacyfactions.cmd.CmdAutoHelp;
import com.massivecraft.legacyfactions.cmd.FCmdRoot;
import com.massivecraft.legacyfactions.entity.Board;
import com.massivecraft.legacyfactions.entity.Conf;
import com.massivecraft.legacyfactions.entity.FPlayer;
import com.massivecraft.legacyfactions.entity.FPlayerColl;
import com.massivecraft.legacyfactions.entity.Faction;
import com.massivecraft.legacyfactions.entity.FactionColl;
import com.massivecraft.legacyfactions.integration.Integrations;
import com.massivecraft.legacyfactions.integration.dynmap.DynmapIntegration;
import com.massivecraft.legacyfactions.integration.essentials.EssentialsIntegration;
import com.massivecraft.legacyfactions.integration.metrics.MetricsIntegration;
import com.massivecraft.legacyfactions.integration.playervaults.PlayerVaultsIntegration;
import com.massivecraft.legacyfactions.integration.vault.VaultIntegration;
import com.massivecraft.legacyfactions.integration.worldguard.WorldGuardIntegration;
import com.massivecraft.legacyfactions.listeners.*;
import com.massivecraft.legacyfactions.util.*;

import net.milkbowl.vault.permission.Permission;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.player.AsyncPlayerChatEvent;

import java.lang.reflect.Modifier;
import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;

public class Factions extends FactionsPluginBase {

	// ----------------------------------------
	// INSTANCE & CONSTRUCT
	// ----------------------------------------
	
	private static Factions instance;
	public Factions() { instance = this; }
	public static Factions get() { return instance; }
	
	// ----------------------------------------
	// FIELDS
	// ----------------------------------------

	public Permission perms = null;
	private boolean locked = false;
	private Integer AutoLeaveTask = null;
	public FCmdRoot cmdBase;
	public CmdAutoHelp cmdAutoHelp;
	private GsonBuilder gsonBuilder = null;
	
	// ----------------------------------------
	// METHODS
	// ----------------------------------------

	public boolean getLocked() {
		return this.locked;
	}

	public void setLocked(boolean val) {
		this.locked = val;
		this.setAutoSave(val);
	}
	
	@Override
	public void onEnable() {
		if (!preEnable()) {
			return;
		}
		
		this.loadSuccessful = false;
		saveDefaultConfig();

		// Load Conf from disk
		Conf.load();
		
		FPlayerColl.getInstance().load();
		FactionColl.getInstance().load();
		for (FPlayer fPlayer : FPlayerColl.getInstance().getAllFPlayers()) {
			Faction faction = FactionColl.getInstance().getFactionById(fPlayer.getFactionId());
			if (faction == null) {
				log("Invalid faction id on " + fPlayer.getName() + ":" + fPlayer.getFactionId());
				fPlayer.resetFactionData(false);
				continue;
			}
			faction.addFPlayer(fPlayer);
		}
		Board.getInstance().load();
		Board.getInstance().clean();

		// Add base commands.
		this.cmdBase = new FCmdRoot();
		this.cmdAutoHelp = new CmdAutoHelp();
		this.getBaseCommands().add(cmdBase);

		// Add our integrations.
		Integrations.add(VaultIntegration.get());
		Integrations.add(WorldGuardIntegration.get());
		Integrations.add(DynmapIntegration.get());
		Integrations.add(EssentialsIntegration.get());
		Integrations.add(PlayerVaultsIntegration.get());
		Integrations.add(MetricsIntegration.get());

		// start up task which runs the autoLeaveAfterDaysOfInactivity routine
		startAutoLeaveTask(false);

		// Register Event Handlers
		getServer().getPluginManager().registerEvents(new FactionsPlayerListener(), this);
		getServer().getPluginManager().registerEvents(new FactionsChatListener(), this);
		getServer().getPluginManager().registerEvents(new FactionsEntityListener(), this);
		getServer().getPluginManager().registerEvents(new FactionsExploitListener(), this);
		getServer().getPluginManager().registerEvents(new FactionsBlockListener(), this);

		// since some other plugins execute commands directly through this command interface, provide it
		this.getCommand(this.refCommand).setExecutor(this);

		postEnable();
		this.loadSuccessful = true;
	}
	
	@Override
	public GsonBuilder getGsonBuilder() {
		if (this.gsonBuilder == null) {
			Type mapFLocToStringSetType = new TypeToken<Map<FLocation, Set<String>>>() { }.getType();
			
			this.gsonBuilder = new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().excludeFieldsWithModifiers(Modifier.TRANSIENT, Modifier.VOLATILE)
					.registerTypeAdapter(LazyLocation.class, new MyLocationTypeAdapter())
					.registerTypeAdapter(mapFLocToStringSetType, new MapFLocToStringSetTypeAdapter())
					.registerTypeAdapterFactory(EnumTypeAdapter.ENUM_FACTORY);
		}

		return this.gsonBuilder;
	}

	@Override
	public void onDisable() {
		// only save data if plugin actually completely loaded successfully
		if (this.loadSuccessful) {
			Conf.save();
		}
		if (AutoLeaveTask != null) {
			this.getServer().getScheduler().cancelTask(AutoLeaveTask);
			AutoLeaveTask = null;
		}

		super.onDisable();
	}

	public void startAutoLeaveTask(boolean restartIfRunning) {
		if (AutoLeaveTask != null) {
			if (!restartIfRunning) {
				return;
			}
			this.getServer().getScheduler().cancelTask(AutoLeaveTask);
		}

		if (Conf.autoLeaveRoutineRunsEveryXMinutes > 0.0) {
			long ticks = (long) (20 * 60 * Conf.autoLeaveRoutineRunsEveryXMinutes);
			AutoLeaveTask = getServer().getScheduler().scheduleSyncRepeatingTask(this, new AutoLeaveTask(), ticks, ticks);
		}
	}

	@Override
	public void postAutoSave() {
		Conf.save();
	}

	@Override
	public boolean logPlayerCommands() {
		return Conf.logPlayerCommands;
	}

	@Override
	public boolean handleCommand(CommandSender sender, String commandString, boolean testOnly) {
		return sender instanceof Player && FactionsPlayerListener.preventCommand(commandString, (Player) sender) || super.handleCommand(sender, commandString, testOnly);
	}

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] split) {
		if (split.length == 0) {
			return handleCommand(sender, "/f help", false);
		}

		// otherwise, needs to be handled; presumably another plugin directly ran the command
		String cmd = Conf.baseCommandAliases.isEmpty() ? "/f" : "/" + Conf.baseCommandAliases.get(0);
		return handleCommand(sender, cmd + " " + TextUtil.implode(Arrays.asList(split), " "), false);
	}


	// -------------------------------------------- //
	// Functions for other plugins to hook into
	// -------------------------------------------- //

	// If another plugin is handling insertion of chat tags, this should be used to notify Factions
	public void handleFactionTagExternally(boolean notByFactions) {
		Conf.chatTagHandledByAnotherPlugin = notByFactions;
	}

	// Simply put, should this chat event be left for Factions to handle? For now, that means players with Faction Chat
	// enabled or use of the Factions f command without a slash; combination of isPlayerFactionChatting() and isFactionsCommand()

	public boolean shouldLetFactionsHandleThisChat(AsyncPlayerChatEvent event) {
		return event != null && (isPlayerFactionChatting(event.getPlayer()) || isFactionsCommand(event.getMessage()));
	}

	// Does player have Faction Chat enabled? If so, chat plugins should preferably not do channels,
	// local chat, or anything else which targets individual recipients, so Faction Chat can be done
	public boolean isPlayerFactionChatting(Player player) {
		if (player == null) {
			return false;
		}
		FPlayer me = FPlayerColl.getInstance().getByPlayer(player);

		return me != null && me.getChatMode().isAtLeast(ChatMode.ALLIANCE);
	}

	// Is this chat message actually a Factions command, and thus should be left alone by other plugins?

	// TODO: GET THIS BACK AND WORKING

	public boolean isFactionsCommand(String check) {
		return !(check == null || check.isEmpty()) && this.handleCommand(null, check, true);
	}

	// Get a player's faction tag (faction name), mainly for usage by chat plugins for local/channel chat
	public String getPlayerFactionTag(Player player) {
		return getPlayerFactionTagRelation(player, null);
	}

	// Same as above, but with relation (enemy/neutral/ally) coloring potentially added to the tag
	public String getPlayerFactionTagRelation(Player speaker, Player listener) {
		String tag = "~";

		if (speaker == null) {
			return tag;
		}

		FPlayer me = FPlayerColl.getInstance().getByPlayer(speaker);
		if (me == null) {
			return tag;
		}

		// if listener isn't set, or config option is disabled, give back uncolored tag
		if (listener == null || !Conf.chatTagRelationColored) {
			tag = me.getChatTag().trim();
		} else {
			FPlayer you = FPlayerColl.getInstance().getByPlayer(listener);
			if (you == null) {
				tag = me.getChatTag().trim();
			} else  // everything checks out, give the colored tag
			{
				tag = me.getChatTag(you).trim();
			}
		}
		if (tag.isEmpty()) {
			tag = "~";
		}

		return tag;
	}

	// Get a player's title within their faction, mainly for usage by chat plugins for local/channel chat
	public String getPlayerTitle(Player player) {
		if (player == null) {
			return "";
		}

		FPlayer me = FPlayerColl.getInstance().getByPlayer(player);
		if (me == null) {
			return "";
		}

		return me.getTitle().trim();
	}

	// Get a list of all faction tags (names)
	public Set<String> getFactionTags() {
		return FactionColl.getInstance().getFactionTags();
	}

	// Get a list of all players in the specified faction
	public Set<String> getPlayersInFaction(String factionTag) {
		Set<String> players = new HashSet<String>();
		Faction faction = FactionColl.getInstance().getByTag(factionTag);
		if (faction != null) {
			for (FPlayer fplayer : faction.getFPlayers()) {
				players.add(fplayer.getName());
			}
		}
		return players;
	}

	// Get a list of all online players in the specified faction
	public Set<String> getOnlinePlayersInFaction(String factionTag) {
		Set<String> players = new HashSet<String>();
		Faction faction = FactionColl.getInstance().getByTag(factionTag);
		if (faction != null) {
			for (FPlayer fplayer : faction.getFPlayersWhereOnline(true)) {
				players.add(fplayer.getName());
			}
		}
		return players;
	}
	
	public String getPrimaryGroup(OfflinePlayer player) {
		return perms == null || !perms.hasGroupSupport() ? " " : perms.getPrimaryGroup(Bukkit.getWorlds().get(0).toString(), player);
	}

	public void debug(Level level, String s) {
		if (getConfig().getBoolean("debug", false)) {
			getLogger().log(level, s);
		}
	}

}
