package net.redstoneore.legacyfactions.cmd;

import mkremins.fanciful.FancyMessage;
import net.redstoneore.legacyfactions.Factions;
import net.redstoneore.legacyfactions.Permission;
import net.redstoneore.legacyfactions.Role;
import net.redstoneore.legacyfactions.Lang;
import net.redstoneore.legacyfactions.entity.Conf;
import net.redstoneore.legacyfactions.entity.FPlayer;
import net.redstoneore.legacyfactions.entity.Faction;
import net.redstoneore.legacyfactions.entity.FactionColl;
import net.redstoneore.legacyfactions.event.EventFactionsChange;
import net.redstoneore.legacyfactions.event.EventFactionsChange.ChangeReason;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;

public class CmdFactionsKick extends FCommand {
	
	// -------------------------------------------------- //
	// CONSTRUCT
	// -------------------------------------------------- //

	public CmdFactionsKick() {
		this.aliases.addAll(Conf.cmdAliasesKick);

		this.optionalArgs.put("player name", "player name");
		//this.optionalArgs.put("", "");

		this.permission = Permission.KICK.getNode();
		this.disableOnLock = false;

		this.senderMustBePlayer = true;
		this.senderMustBeMember = false;
		this.senderMustBeModerator = true;
		this.senderMustBeColeader = false;
		this.senderMustBeAdmin = false;
	}

	// -------------------------------------------------- //
	// METHODS
	// -------------------------------------------------- //

	@Override
	public void perform() {
		FPlayer toKick = this.argIsSet(0) ? this.argAsBestFPlayerMatch(0) : null;
		if (toKick == null) {
			FancyMessage msg = new FancyMessage(Lang.COMMAND_KICK_CANDIDATES.toString()).color(ChatColor.GOLD);

			for (FPlayer player : myFaction.getFPlayersWhereRole(Role.NORMAL)) {
				String name = player.getName();
				msg.then(name + " ").color(ChatColor.WHITE).tooltip(Lang.COMMAND_KICK_CLICKTOKICK.toString() + name).command("/" + Conf.baseCommandAliases.get(0) + " kick " + name);
			}

			if (fme.getRole().isAtLeast(Role.COLEADER)) {
				for (FPlayer player : myFaction.getFPlayersWhereRole(Role.MODERATOR)) {
					String s = player.getName();
					msg.then(s + " ").color(ChatColor.GRAY).tooltip(Lang.COMMAND_KICK_CLICKTOKICK.toString() + s).command("/" + Conf.baseCommandAliases.get(0) + " kick " + s);
				}
			}

			if(fme.getRole() == Role.ADMIN) {
				for (FPlayer player : myFaction.getFPlayersWhereRole(Role.COLEADER)) {
					String s = player.getName();
					msg.then(s + " ").color(ChatColor.GRAY).tooltip(Lang.COMMAND_KICK_CLICKTOKICK.toString() + s).command("/" + Conf.baseCommandAliases.get(0) + " kick " + s);
				}
			}

			sendFancyMessage(msg);
			return;
		}

		if (fme == toKick) {
			sendMessage(Lang.COMMAND_KICK_SELF);
			sendMessage(Lang.GENERIC_YOUMAYWANT.toString() + CmdFactions.get().cmdLeave.getUseageTemplate(false));
			return;
		}

		Faction toKickFaction = toKick.getFaction();

		if (toKickFaction.isWilderness()) {
			sender.sendMessage(Lang.COMMAND_KICK_NONE.toString());
			return;
		}

		// players with admin-level "disband" permission can bypass these requirements
		if (!Permission.KICK_ANY.has(sender)) {
			if (toKickFaction != myFaction) {
				sendMessage(Lang.COMMAND_KICK_NOTMEMBER, toKick.describeTo(fme, true), myFaction.describeTo(fme));
				return;
			}

			if (toKick.getRole().isAtLeast(fme.getRole())) {
				sendMessage(Lang.COMMAND_KICK_INSUFFICIENTRANK);
				return;
			}

			if (!Conf.canLeaveWithNegativePower && toKick.getPower() < 0) {
				sendMessage(Lang.COMMAND_KICK_NEGATIVEPOWER);
				return;
			}
		}

		// if economy is enabled, they're not on the bypass list, and this command has a cost set, make sure they can pay
		if (!canAffordCommand(Conf.econCostKick, Lang.COMMAND_KICK_TOKICK.toString())) {
			return;
		}

		// trigger the leave event (cancellable) [reason:kicked]
		EventFactionsChange event = new EventFactionsChange(toKick, toKick.getFaction(), FactionColl.get().getWilderness(), true, ChangeReason.KICKED);
		Bukkit.getServer().getPluginManager().callEvent(event);
		if (event.isCancelled()) {
			return;
		}

		// then make 'em pay (if applicable)
		if (!payForCommand(Conf.econCostKick, Lang.COMMAND_KICK_TOKICK.toString(), Lang.COMMAND_KICK_FORKICK.toString())) {
			return;
		}

		toKickFaction.sendMessage(Lang.COMMAND_KICK_FACTION, fme.describeTo(toKickFaction, true), toKick.describeTo(toKickFaction, true));
		toKick.sendMessage(Lang.COMMAND_KICK_KICKED, fme.describeTo(toKick, true), toKickFaction.describeTo(toKick));
		if (toKickFaction != myFaction) {
			fme.sendMessage(Lang.COMMAND_KICK_KICKS, toKick.describeTo(fme), toKickFaction.describeTo(fme));
		}

		if (Conf.logFactionKick) {
			// TODO:TL
			Factions.get().log((senderIsConsole ? "A console command" : fme.getName()) + " kicked " + toKick.getName() + " from the faction: " + toKickFaction.getTag());
		}

		if (toKick.getRole() == Role.ADMIN) {
			toKickFaction.promoteNewLeader();
		}

		toKickFaction.deinvite(toKick);
		toKick.resetFactionData();
	}

	@Override
	public String getUsageTranslation() {
		return Lang.COMMAND_KICK_DESCRIPTION.toString();
	}

}
