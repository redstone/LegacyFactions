package net.redstoneore.legacyfactions.cmd;

import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;

import net.redstoneore.legacyfactions.Factions;
import net.redstoneore.legacyfactions.Permission;
import net.redstoneore.legacyfactions.config.CommandAliases;
import net.redstoneore.legacyfactions.config.Config;
import net.redstoneore.legacyfactions.entity.FPlayer;
import net.redstoneore.legacyfactions.entity.FPlayerColl;
import net.redstoneore.legacyfactions.entity.Faction;
import net.redstoneore.legacyfactions.event.EventFactionsChange;
import net.redstoneore.legacyfactions.event.EventFactionsChange.ChangeReason;
import net.redstoneore.legacyfactions.flag.Flags;
import net.redstoneore.legacyfactions.lang.Lang;
import net.redstoneore.legacyfactions.util.TextUtil;

public class CmdFactionsJoin extends FCommand {
	
	// -------------------------------------------------- //
	// INSTANCE
	// -------------------------------------------------- //
	
	private static CmdFactionsJoin instance = new CmdFactionsJoin();
	public static CmdFactionsJoin get() { return instance; }
	
	// -------------------------------------------------- //
	// CONSTRUCT
	// -------------------------------------------------- //

	private CmdFactionsJoin() {
		this.aliases.addAll(CommandAliases.cmdAliasesJoin);

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
				final String playerName = this.argAsString(1);
				
				this.argAsPlayerToMojangUUID(1, null, (uuid, exception) -> {
					if (exception.isPresent()) {
						exception.get().printStackTrace();
						return;
					}
					
					if (uuid == null) {
						fsender.sendMessage(Lang.COMMAND_JOIN_NOT_PLAYER.toString().replace("<name>", playerName));
						return;
					}
					
					resume(fsender, uuid, newFaction, commandSender);
				});
				return;
			}
		}
		
		// Go as normal
		resume(this.fme, fplayer, faction, this.sender);
	}
	
	private static final void resume(FPlayer fsender, UUID uuidPlayer, Faction faction, CommandSender commandSender) {
		FPlayer fplayer = FPlayerColl.get(Bukkit.getOfflinePlayer(uuidPlayer));
		resume(fsender, fplayer, faction, commandSender);		
	}
	
	private static final void resume(FPlayer fsender, FPlayer fplayer, Faction faction, CommandSender commandSender) {
		boolean samePlayer = fplayer == fsender;
		
		if (faction.isBanned(fplayer)) {
			if (samePlayer) {
				fsender.sendMessage(TextUtil.parseColor(Lang.COMMAND_JOIN_YOUBANNED.toString()));
				return;
			} 
			fsender.sendMessage(TextUtil.parseColor(Lang.COMMAND_JOIN_ISBANNED.toString()).replace("<player>", fplayer.getName()));

			return;
		}
		
		if (!samePlayer && !Permission.JOIN_OTHERS.has(commandSender, false)) {
			fsender.sendMessage(Lang.COMMAND_JOIN_CANNOTFORCE);
			return;
		}

		if (!faction.isNormal()) {
			fsender.sendMessage(Lang.COMMAND_JOIN_SYSTEMFACTION);
			return;
		}

		if (faction == fplayer.getFaction()) {
			String message;
			if (samePlayer) {
				message = Lang.COMMAND_JOIN_ALREADYMEMBER_YOU.toString();
			} else {
				message = Lang.COMMAND_JOIN_ALREADYMEMBER_SOMEONE.toString();
			}
			message = message.replace("<player>", fplayer.describeTo(fsender, true));
			message = message.replace("<faction>", faction.getTag(fsender));
			
			fsender.sendMessage(TextUtil.parseColor(message));
			return;
		}

		if (faction.getFlag(Flags.PEACEFUL) && Config.factionMemberLimitPeaceful > 0) {
			if (faction.getMembers().size() >= Config.factionMemberLimitPeaceful) {
				fsender.sendMessage(Lang.COMMAND_JOIN_ATLIMIT, faction.getTag(fsender), Config.factionMemberLimitPeaceful, fplayer.describeTo(fsender, false));
				return;
			}
		} else if (Config.factionMemberLimit > 0 && faction.getMembers().size() >= Config.factionMemberLimit) {
			fsender.sendMessage(Lang.COMMAND_JOIN_ATLIMIT, faction.getTag(fsender), Config.factionMemberLimit, fplayer.describeTo(fsender, false));
			return;
		}

		if (fplayer.hasFaction()) {
			String message;
			if (samePlayer) {
				message = Lang.COMMAND_JOIN_INOTHERFACTION_YOU.toString();
			} else {
				message = Lang.COMMAND_JOIN_INOTHERFACTION_SOMEONE.toString();
			}
			message = message.replace("<player>", fplayer.describeTo(fsender, true));
			fsender.sendMessage(TextUtil.parseColor(message));
			return;
		}

		if (!Config.canLeaveWithNegativePower && fplayer.getPower() < 0) {
			fsender.sendMessage(Lang.COMMAND_JOIN_NEGATIVEPOWER, fplayer.describeTo(fsender, true));
			return;
		}

		if (!(faction.getFlag(Flags.OPEN) || faction.isInvited(fplayer) || fsender.isAdminBypassing() || Permission.JOIN_ANY.has(commandSender, false))) {
			fsender.sendMessage(Lang.COMMAND_JOIN_REQUIRESINVITATION);
			if (samePlayer) {
				faction.sendMessage(Lang.COMMAND_JOIN_ATTEMPTEDJOIN, fplayer.describeTo(faction, true));
			}
			return;
		}

		// if economy is enabled, they're not on the bypass list, and this command has a cost set, make sure they can pay
		if (samePlayer && !fsender.canAffordCommand(Config.econCostJoin, Lang.COMMAND_JOIN_TOJOIN.toString())) {
			return;
		}

		// trigger the join event (cancellable)
		EventFactionsChange event = new EventFactionsChange(fsender, fsender.getFaction(), faction, true, ChangeReason.COMMAND);
		Bukkit.getServer().getPluginManager().callEvent(event);
		if (event.isCancelled()) {
			return;
		}

		// then make 'em pay (if applicable)
		if (samePlayer && !fsender.payForCommand(Config.econCostJoin, Lang.COMMAND_JOIN_TOJOIN.toString(), Lang.COMMAND_JOIN_FORJOIN.toString())) {
			return;
		}

		fsender.sendMessage(Lang.COMMAND_JOIN_SUCCESS, fplayer.describeTo(fsender, true), faction.getTag(fsender));

		if (!samePlayer) {
			fplayer.sendMessage(Lang.COMMAND_JOIN_MOVED, fsender.describeTo(fplayer, true), faction.getTag(fplayer));
		}
		faction.sendMessage(Lang.COMMAND_JOIN_JOINED, fplayer.describeTo(faction, true));

		fplayer.resetFactionData();
		fplayer.setFaction(faction);
		faction.uninvite(fplayer);

		if (Config.logFactionJoin) {
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
