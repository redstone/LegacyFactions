package net.redstoneore.legacyfactions.cmd;

import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;

import net.redstoneore.legacyfactions.*;
import net.redstoneore.legacyfactions.entity.Conf;
import net.redstoneore.legacyfactions.entity.FPlayer;
import net.redstoneore.legacyfactions.entity.FPlayerColl;
import net.redstoneore.legacyfactions.entity.Faction;
import net.redstoneore.legacyfactions.event.EventFactionsChange;
import net.redstoneore.legacyfactions.event.EventFactionsChange.ChangeReason;

public class CmdFactionsJoin extends FCommand {
	
	// -------------------------------------------------- //
	// CONSTRUCT
	// -------------------------------------------------- //

	public CmdFactionsJoin() {
		this.aliases.addAll(Conf.cmdAliasesJoin);

		this.requiredArgs.add("faction name");
		this.optionalArgs.put("player", "you");

		this.permission = Permission.JOIN.getNode();
		this.disableOnLock = true;

		this.senderMustBePlayer = true;
		this.senderMustBeMember = false;
		this.senderMustBeModerator = false;
		this.senderMustBeColeader = false;
		this.senderMustBeAdmin = false;
	}

	// -------------------------------------------------- //
	// METHODS
	// -------------------------------------------------- //

	@Override
	public void perform() {
		// First we need the faction
		Faction faction = this.argAsFaction(0, null, true);
		if (faction == null) return;
		
		// By default it's this player ...
		FPlayer fplayer = this.fme;
		
		// ... unless specified otherwise.
		if (this.argIsSet(1)) {
			
			// First we'll try doing this normally
			fplayer = this.argAsBestFPlayerMatch(1, null, false);
			if (fplayer == null) {
				// Ok, so the player hasn't joined the server before or something.
				// so, let's grab the UUID of this player.	
				
				// Switch to finals here for safety
				final FPlayer fsender = this.fme;
				final Faction newFaction = faction;
				final CommandSender commandSender = sender;
				
				this.argAsPlayerToMojangUUID(1, null, (uuid, exception) -> {
					if (exception.isPresent()) {
						return;
					}
					resume(fsender, uuid, newFaction, commandSender);
				});
				return;
			}
		}
		
		// Go as normal
		this.resume(this.fme, fplayer, faction, this.sender);
	}
	
	public final void resume(FPlayer fsender, UUID uuidPlayer, Faction faction, CommandSender commandSender) {
		FPlayer fplayer = FPlayerColl.get(Bukkit.getOfflinePlayer(uuidPlayer));
		this.resume(this.fme, fplayer, faction, this.sender);		
	}
	
	public final void resume(FPlayer fsender, FPlayer fplayer, Faction faction, CommandSender commandSender) {
		boolean samePlayer = fplayer == fsender;

		if (!samePlayer && !Permission.JOIN_OTHERS.has(commandSender, false)) {
			fsender.sendMessage(Lang.COMMAND_JOIN_CANNOTFORCE);
			return;
		}

		if (!faction.isNormal()) {
			fsender.sendMessage(Lang.COMMAND_JOIN_SYSTEMFACTION);
			return;
		}

		if (faction == fplayer.getFaction()) {
			// TODO:TL
			fsender.sendMessage(Lang.COMMAND_JOIN_ALREADYMEMBER, fplayer.describeTo(fsender, true), (samePlayer ? "are" : "is"), faction.getTag(fsender));
			return;
		}

		if (Conf.factionMemberLimit > 0 && faction.getFPlayers().size() >= Conf.factionMemberLimit) {
			fsender.sendMessage(Lang.COMMAND_JOIN_ATLIMIT, faction.getTag(fsender), Conf.factionMemberLimit, fplayer.describeTo(fsender, false));
			return;
		}

		if (fplayer.hasFaction()) {
			// TODO:TL
			fsender.sendMessage(Lang.COMMAND_JOIN_INOTHERFACTION, fplayer.describeTo(fsender, true), (samePlayer ? "your" : "their"));
			return;
		}

		if (!Conf.canLeaveWithNegativePower && fplayer.getPower() < 0) {
			fsender.sendMessage(Lang.COMMAND_JOIN_NEGATIVEPOWER, fplayer.describeTo(fsender, true));
			return;
		}

		if (!(faction.getOpen() || faction.isInvited(fplayer) || fsender.isAdminBypassing() || Permission.JOIN_ANY.has(commandSender, false))) {
			fsender.sendMessage(Lang.COMMAND_JOIN_REQUIRESINVITATION);
			if (samePlayer) {
				faction.sendMessage(Lang.COMMAND_JOIN_ATTEMPTEDJOIN, fplayer.describeTo(faction, true));
			}
			return;
		}

		// if economy is enabled, they're not on the bypass list, and this command has a cost set, make sure they can pay
		if (samePlayer && !fsender.canAffordCommand(Conf.econCostJoin, Lang.COMMAND_JOIN_TOJOIN.toString())) {
			return;
		}

		// trigger the join event (cancellable)
		EventFactionsChange event = new EventFactionsChange(fsender, fsender.getFaction(), faction, true, ChangeReason.COMMAND);
		Bukkit.getServer().getPluginManager().callEvent(event);
		if (event.isCancelled()) {
			return;
		}

		// then make 'em pay (if applicable)
		if (samePlayer && !fsender.payForCommand(Conf.econCostJoin, Lang.COMMAND_JOIN_TOJOIN.toString(), Lang.COMMAND_JOIN_FORJOIN.toString())) {
			return;
		}

		fsender.sendMessage(Lang.COMMAND_JOIN_SUCCESS, fplayer.describeTo(fsender, true), faction.getTag(fsender));

		if (!samePlayer) {
			fplayer.sendMessage(Lang.COMMAND_JOIN_MOVED, fsender.describeTo(fplayer, true), faction.getTag(fplayer));
		}
		faction.sendMessage(Lang.COMMAND_JOIN_JOINED, fplayer.describeTo(faction, true));

		fplayer.resetFactionData();
		fplayer.setFaction(faction);
		faction.deinvite(fplayer);

		if (Conf.logFactionJoin) {
			if (samePlayer) {
				Factions.get().log(Lang.COMMAND_JOIN_JOINEDLOG.toString(), fplayer.getName(), faction.getTag());
			} else {
				Factions.get().log(Lang.COMMAND_JOIN_MOVEDLOG.toString(), fsender.getName(), fplayer.getName(), faction.getTag());
			}
		}
	}


	@Override
	public String getUsageTranslation() {
		return Lang.COMMAND_JOIN_DESCRIPTION.toString();
	}
	
}
