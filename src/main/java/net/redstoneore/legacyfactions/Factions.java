/**
 *   A high-performance maintained version of Factions 1.6.
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
package net.redstoneore.legacyfactions;

import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import net.redstoneore.legacyfactions.adapter.EnumAdapter;
import net.redstoneore.legacyfactions.adapter.LazyLocationAdapter;
import net.redstoneore.legacyfactions.adapter.MapFlocationSetAdapter;
import net.redstoneore.legacyfactions.cmd.CmdFactions;
import net.redstoneore.legacyfactions.entity.Board;
import net.redstoneore.legacyfactions.entity.Conf;
import net.redstoneore.legacyfactions.entity.FPlayer;
import net.redstoneore.legacyfactions.entity.FPlayerColl;
import net.redstoneore.legacyfactions.entity.Faction;
import net.redstoneore.legacyfactions.entity.FactionColl;
import net.redstoneore.legacyfactions.integration.Integrations;
import net.redstoneore.legacyfactions.integration.bstats.BStatsIntegration;
import net.redstoneore.legacyfactions.integration.dynmap.DynmapIntegration;
import net.redstoneore.legacyfactions.integration.essentials.EssentialsIntegration;
import net.redstoneore.legacyfactions.integration.metrics.MetricsIntegration;
import net.redstoneore.legacyfactions.integration.playervaults.PlayerVaultsIntegration;
import net.redstoneore.legacyfactions.integration.vault.VaultIntegration;
import net.redstoneore.legacyfactions.integration.worldguard.WorldGuardIntegration;
import net.redstoneore.legacyfactions.listeners.*;
import net.redstoneore.legacyfactions.task.AutoLeaveTask;
import net.redstoneore.legacyfactions.util.*;

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

	// -------------------------------------------------- //
	// INSTANCE & CONSTRUCT 
	// -------------------------------------------------- //
	
	private static Factions instance;
	public Factions() { instance = this; }
	public static Factions get() { return instance; }
	
	// -------------------------------------------------- //
	// FIELDS
	// -------------------------------------------------- //

	private GsonBuilder gsonBuilder = null;

	public Permission perms = null;
	private Integer taskAutoLeave = null;
	
	private boolean locked = false;
		
	// -------------------------------------------------- //
	// METHODS
	// -------------------------------------------------- //

	/**
	 * is saving locked
	 * @return true if locked
	 */
	public boolean isLocked() {
		return this.locked;
	}
	
	/**
	 * set lock state
	 * @param val 
	 */
	public void setLocked(boolean locked) {
		this.locked = locked;
		this.setAutoSave(locked);
	}
	
	@Override
	public void onEnable() {
		if (!preEnable()) return;
		
		this.loadSuccessful = false;
		saveDefaultConfig();

		// Load Conf from disk
		Conf.load();
		
		FPlayerColl.load();
		FactionColl.get().load();
		
		
		FPlayerColl.all(fplayer -> {
			Faction faction = fplayer.getFaction();
			if (faction == null) {
				log("Invalid faction id on " + fplayer.getName() + ":" + fplayer.getFactionId());
				fplayer.resetFactionData(false);
				return;
			}
			faction.addFPlayer(fplayer);
		});
		
		Board.get().load();
		Board.get().clean();

		// Add base commands.
		this.getBaseCommands().add(CmdFactions.get());

		// Add our integrations.
		Integrations.add(VaultIntegration.get());
		Integrations.add(WorldGuardIntegration.get());
		Integrations.add(DynmapIntegration.get());
		Integrations.add(EssentialsIntegration.get());
		Integrations.add(PlayerVaultsIntegration.get());
		Integrations.add(MetricsIntegration.get());
		Integrations.add(BStatsIntegration.get());

		// start up task which runs the autoLeaveAfterDaysOfInactivity routine
		startAutoLeaveTask(false);

		// Register Event Handlers
		this.register(
			FactionsPlayerListener.get(),
			FactionsChatListener.get(),
			FactionsEntityListener.get(),
			FactionsExploitListener.get(),
			FactionsBlockListener.get()
		);
		
		// since some other plugins execute commands directly through this command interface, provide it
		for (String refCommand : Conf.baseCommandAliases) {
			this.getCommand(refCommand).setExecutor(this);
		}

		postEnable();
		this.loadSuccessful = true;
	}
	
	@Override
	public GsonBuilder getGsonBuilder() {
		if (this.gsonBuilder == null) {
			Type mapFLocToStringSetType = new TypeToken<Map<FLocation, Set<String>>>() { }.getType();
			
			this.gsonBuilder = new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().excludeFieldsWithModifiers(Modifier.TRANSIENT, Modifier.VOLATILE)
					.registerTypeAdapter(LazyLocation.class, new LazyLocationAdapter())
					.registerTypeAdapter(mapFLocToStringSetType, new MapFlocationSetAdapter())
					.registerTypeAdapterFactory(EnumAdapter.ENUM_FACTORY);
		}

		return this.gsonBuilder;
	}

	@Override
	public void onDisable() {
		// only save data if plugin actually completely loaded successfully
		if (this.loadSuccessful) {
			Conf.save();
		}
		if (taskAutoLeave != null) {
			this.getServer().getScheduler().cancelTask(taskAutoLeave);
			taskAutoLeave = null;
		}

		super.onDisable();
	}

	public void startAutoLeaveTask(boolean restartIfRunning) {
		if (taskAutoLeave != null) {
			if (!restartIfRunning) {
				return;
			}
			this.getServer().getScheduler().cancelTask(taskAutoLeave);
		}

		if (Conf.autoLeaveRoutineRunsEveryXMinutes > 0.0) {
			long ticks = (long) (20 * 60 * Conf.autoLeaveRoutineRunsEveryXMinutes);
			taskAutoLeave = getServer().getScheduler().scheduleSyncRepeatingTask(this, new AutoLeaveTask(), ticks, ticks);
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
		FPlayer me = FPlayerColl.get(player);

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

		FPlayer me = FPlayerColl.get(speaker);
		if (me == null) {
			return tag;
		}

		// if listener isn't set, or config option is disabled, give back uncolored tag
		if (listener == null || !Conf.chatTagRelationColored) {
			tag = me.getChatTag().trim();
		} else {
			FPlayer you = FPlayerColl.get(listener);
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

		FPlayer me = FPlayerColl.get(player);
		if (me == null) {
			return "";
		}

		return me.getTitle().trim();
	}

	// Get a list of all faction tags (names)
	public Set<String> getFactionTags() {
		return FactionColl.get().getFactionTags();
	}

	// Get a list of all players in the specified faction
	public Set<String> getPlayersInFaction(String factionTag) {
		Set<String> players = new HashSet<>();
		Faction faction = FactionColl.get().getByTag(factionTag);
		if (faction != null) {
			for (FPlayer fplayer : faction.getFPlayers()) {
				players.add(fplayer.getName());
			}
		}
		return players;
	}

	// Get a list of all online players in the specified faction
	public Set<String> getOnlinePlayersInFaction(String factionTag) {
		Set<String> players = new HashSet<>();
		Faction faction = FactionColl.get().getByTag(factionTag);
		if (faction != null) {
			for (FPlayer fplayer : faction.getFPlayersWhereOnline(true)) {
				players.add(fplayer.getName());
			}
		}
		return players;
	}
	
	public void debug(Level level, String s) {
		if (getConfig().getBoolean("debug", false)) {
			getLogger().log(level, s);
		}
	}

}
