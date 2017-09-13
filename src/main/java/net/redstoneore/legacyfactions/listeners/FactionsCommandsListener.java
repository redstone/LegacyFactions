package net.redstoneore.legacyfactions.listeners;

import java.util.Iterator;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;

import net.redstoneore.legacyfactions.Factions;
import net.redstoneore.legacyfactions.Lang;
import net.redstoneore.legacyfactions.Relation;
import net.redstoneore.legacyfactions.entity.Board;
import net.redstoneore.legacyfactions.entity.Conf;
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
		if (!Conf.logPlayerCommands) return;
		
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
		if (!Conf.logPlayerCommands) return;
		
		// ... log it.
		Bukkit.getLogger().info("[PLAYER_COMMAND] " + event.getPlayer().getName() + ": " + event.getMessage());
	}
	

	public boolean preventCommand(String fullCmd, Player player) {
		return preventCommand(fullCmd, player, false);
	}

	public boolean preventCommand(String fullCmd, Player player, Boolean silent) {
		if (((Conf.territoryNeutralDenyCommands == null || Conf.territoryNeutralDenyCommands.isEmpty()) &&
			 (Conf.territoryEnemyDenyCommands == null || Conf.territoryEnemyDenyCommands.isEmpty()) && 
			 (Conf.permanentFactionMemberDenyCommands == null || Conf.permanentFactionMemberDenyCommands.isEmpty()) && 
			 (Conf.warzoneDenyCommands == null || Conf.warzoneDenyCommands.isEmpty()))) {
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
					Conf.permanentFactionMemberDenyCommands != null &&
					!Conf.permanentFactionMemberDenyCommands.isEmpty() &&
					me.getFaction().getFlag(Flags.PERMANENT) &&
					isCommandInList(fullCmd, shortCmd, Conf.permanentFactionMemberDenyCommands.iterator())) {
			
			
			if (!silent) me.sendMessage(Lang.PLAYER_COMMAND_PERMANENT, fullCmd);
			return true;
		}

		Faction at = Board.get().getFactionAt(Locality.of(player.getLocation()));
		if (at.isWilderness() && Conf.wildernessDenyCommands != null && !Conf.wildernessDenyCommands.isEmpty() && !me.isAdminBypassing() && isCommandInList(fullCmd, shortCmd, Conf.wildernessDenyCommands.iterator())) {
			if (!silent) me.sendMessage(Lang.PLAYER_COMMAND_WILDERNESS, fullCmd);
			return true;
		}

		Relation rel = at.getRelationTo(me);
		if (at.isNormal() && rel.isAlly() && Conf.territoryAllyDenyCommands != null && !Conf.territoryAllyDenyCommands.isEmpty() && !me.isAdminBypassing() && isCommandInList(fullCmd, shortCmd, Conf.territoryAllyDenyCommands.iterator())) {
			if (!silent) me.sendMessage(Lang.PLAYER_COMMAND_ALLY, fullCmd);
			return false;
		}

		if (at.isNormal() && rel.isTruce() && Conf.territoryTruceDenyCommands != null && !Conf.territoryTruceDenyCommands.isEmpty() && !me.isAdminBypassing() && isCommandInList(fullCmd, shortCmd, Conf.territoryTruceDenyCommands.iterator())) {
			if (!silent) me.sendMessage(Lang.PLAYER_COMMAND_TRUCE, fullCmd);
			return false;
		}

		if (at.isNormal() && rel.isNeutral() && Conf.territoryNeutralDenyCommands != null && !Conf.territoryNeutralDenyCommands.isEmpty() && !me.isAdminBypassing() && isCommandInList(fullCmd, shortCmd, Conf.territoryNeutralDenyCommands.iterator())) {
			if (!silent) me.sendMessage(Lang.PLAYER_COMMAND_NEUTRAL, fullCmd);
			return true;
		}

		if (at.isNormal() && rel.isEnemy() && Conf.territoryEnemyDenyCommands != null && !Conf.territoryEnemyDenyCommands.isEmpty() && !me.isAdminBypassing() && isCommandInList(fullCmd, shortCmd, Conf.territoryEnemyDenyCommands.iterator())) {
			if (!silent) me.sendMessage(Lang.PLAYER_COMMAND_ENEMY, fullCmd);
			return true;
		}

		if (at.isWarZone() && Conf.warzoneDenyCommands != null && !Conf.warzoneDenyCommands.isEmpty() && !me.isAdminBypassing() && isCommandInList(fullCmd, shortCmd, Conf.warzoneDenyCommands.iterator())) {
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
	
}
