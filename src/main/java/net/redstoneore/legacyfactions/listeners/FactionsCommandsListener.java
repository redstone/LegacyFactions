package net.redstoneore.legacyfactions.listeners;

import java.util.Iterator;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerChatTabCompleteEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;

import com.google.common.collect.Lists;

import net.redstoneore.legacyfactions.Factions;
import net.redstoneore.legacyfactions.Lang;
import net.redstoneore.legacyfactions.Relation;
import net.redstoneore.legacyfactions.cmd.CmdFactions;
import net.redstoneore.legacyfactions.cmd.FCommand;
import net.redstoneore.legacyfactions.config.Config;
import net.redstoneore.legacyfactions.entity.Board;
import net.redstoneore.legacyfactions.entity.FPlayer;
import net.redstoneore.legacyfactions.entity.FPlayerColl;
import net.redstoneore.legacyfactions.entity.Faction;
import net.redstoneore.legacyfactions.flag.Flags;
import net.redstoneore.legacyfactions.locality.Locality;

public class FactionsCommandsListener implements Listener {
	
	// -------------------------------------------------- //
	// INSTANCE
	// -------------------------------------------------- //
	
	private static FactionsCommandsListener i = new FactionsCommandsListener();
	public static FactionsCommandsListener get() { return i; }
	
	// -------------------------------------------------- //
	// METHODS
	// -------------------------------------------------- //

	@EventHandler(priority = EventPriority.LOW, ignoreCancelled = true)
	public void onPlayerCommandPreprocess(PlayerCommandPreprocessEvent event) {
		// If we're preventing this command ...
		if (!this.preventCommand(event.getMessage(), event.getPlayer())) return;
		
		// ... cancel this event ...
		event.setCancelled(true);
		
		// ... and we're logging ...
		if (!Config.logPlayerCommands) return;
		
		// ... log it.
		Bukkit.getLogger().info("[PLAYER_COMMAND] " + event.getPlayer().getName() + ": " + event.getMessage());
	}

	@EventHandler(priority = EventPriority.LOW, ignoreCancelled = true)
	public void onPlayerChat(AsyncPlayerChatEvent event) {
		// If we're preventing this command ... 
		if (!Factions.get().handleCommand(event.getPlayer(), event.getMessage(), false, true)) return;
		
		// ... cancel this event ...
		event.setCancelled(true);
		
		// ... and we're logging ...
		if (!Config.logPlayerCommands) return;
		
		// ... log it.
		Bukkit.getLogger().info("[PLAYER_COMMAND] " + event.getPlayer().getName() + ": " + event.getMessage());
	}
	

	public boolean preventCommand(String fullCmd, Player player) {
		return preventCommand(fullCmd, player, false);
	}

	public boolean preventCommand(String fullCmd, Player player, Boolean silent) {
		if (((Config.territoryNeutralDenyCommands == null || Config.territoryNeutralDenyCommands.isEmpty()) &&
			 (Config.territoryEnemyDenyCommands == null || Config.territoryEnemyDenyCommands.isEmpty()) && 
			 (Config.permanentFactionMemberDenyCommands == null || Config.permanentFactionMemberDenyCommands.isEmpty()) && 
			 (Config.warzoneDenyCommands == null || Config.warzoneDenyCommands.isEmpty()))) {
			return false;
		}

		fullCmd = fullCmd.toLowerCase();

		FPlayer me = FPlayerColl.get(player);

		String shortCmd;  // command without the slash at the beginning
		if (fullCmd.startsWith("/")) {
			shortCmd = fullCmd.substring(1);
		} else {
			shortCmd = fullCmd;
			fullCmd = "/" + fullCmd;
		}

		if (me.hasFaction() &&
					!me.isAdminBypassing() &&
					Config.permanentFactionMemberDenyCommands != null &&
					!Config.permanentFactionMemberDenyCommands.isEmpty() &&
					me.getFaction().getFlag(Flags.PERMANENT) &&
					isCommandInList(fullCmd, shortCmd, Config.permanentFactionMemberDenyCommands.iterator())) {
			
			
			if (!silent) me.sendMessage(Lang.PLAYER_COMMAND_PERMANENT, fullCmd);
			return true;
		}

		Faction at = Board.get().getFactionAt(Locality.of(player.getLocation()));
		if (at.isWilderness() && Config.wildernessDenyCommands != null && !Config.wildernessDenyCommands.isEmpty() && !me.isAdminBypassing() && isCommandInList(fullCmd, shortCmd, Config.wildernessDenyCommands.iterator())) {
			if (!silent) me.sendMessage(Lang.PLAYER_COMMAND_WILDERNESS, fullCmd);
			return true;
		}

		Relation rel = at.getRelationTo(me);
		if (at.isNormal() && rel.isAlly() && Config.territoryAllyDenyCommands != null && !Config.territoryAllyDenyCommands.isEmpty() && !me.isAdminBypassing() && isCommandInList(fullCmd, shortCmd, Config.territoryAllyDenyCommands.iterator())) {
			if (!silent) me.sendMessage(Lang.PLAYER_COMMAND_ALLY, fullCmd);
			return false;
		}

		if (at.isNormal() && rel.isTruce() && Config.territoryTruceDenyCommands != null && !Config.territoryTruceDenyCommands.isEmpty() && !me.isAdminBypassing() && isCommandInList(fullCmd, shortCmd, Config.territoryTruceDenyCommands.iterator())) {
			if (!silent) me.sendMessage(Lang.PLAYER_COMMAND_TRUCE, fullCmd);
			return false;
		}

		if (at.isNormal() && rel.isNeutral() && Config.territoryNeutralDenyCommands != null && !Config.territoryNeutralDenyCommands.isEmpty() && !me.isAdminBypassing() && isCommandInList(fullCmd, shortCmd, Config.territoryNeutralDenyCommands.iterator())) {
			if (!silent) me.sendMessage(Lang.PLAYER_COMMAND_NEUTRAL, fullCmd);
			return true;
		}

		if (at.isNormal() && rel.isEnemy() && Config.territoryEnemyDenyCommands != null && !Config.territoryEnemyDenyCommands.isEmpty() && !me.isAdminBypassing() && isCommandInList(fullCmd, shortCmd, Config.territoryEnemyDenyCommands.iterator())) {
			if (!silent) me.sendMessage(Lang.PLAYER_COMMAND_ENEMY, fullCmd);
			return true;
		}

		if (at.isWarZone() && Config.warzoneDenyCommands != null && !Config.warzoneDenyCommands.isEmpty() && !me.isAdminBypassing() && isCommandInList(fullCmd, shortCmd, Config.warzoneDenyCommands.iterator())) {
			if (!silent) me.sendMessage(Lang.PLAYER_COMMAND_WARZONE, fullCmd);
			return true;
		}

		return false;
	}

	private static boolean isCommandInList(String fullCmd, String shortCmd, Iterator<String> iter) {
		String cmdCheck;
		while (iter.hasNext()) {
			cmdCheck = iter.next();
			if (cmdCheck == null) {
				iter.remove();
				continue;
			}

			cmdCheck = cmdCheck.toLowerCase();
			if (fullCmd.startsWith(cmdCheck) || shortCmd.startsWith(cmdCheck)) {
				return true;
			}
		}
		return false;
	}
	
	@EventHandler(priority = EventPriority.LOW, ignoreCancelled = true)
	public void onTabCommand(PlayerChatTabCompleteEvent event) {
		FCommand main = CmdFactions.get();
		
		if (event.getChatMessage().trim().contains(" ")) {
			List<String> parts = Lists.newArrayList(event.getChatMessage().split(" "));
			
			int index = 0;
			for (String part : parts) {
				
				if (index != parts.size()) {
					
				}
				
				index++; 
			}
		}
	}
	
}
