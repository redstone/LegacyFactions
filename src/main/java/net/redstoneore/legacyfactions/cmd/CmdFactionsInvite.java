package net.redstoneore.legacyfactions.cmd;

import mkremins.fanciful.FancyMessage;

import net.redstoneore.legacyfactions.Permission;
import net.redstoneore.legacyfactions.Lang;
import net.redstoneore.legacyfactions.entity.Conf;
import net.redstoneore.legacyfactions.entity.FPlayer;

import org.bukkit.ChatColor;

public class CmdFactionsInvite extends FCommand {

	// -------------------------------------------------- //
	// CONSTRUCT
	// -------------------------------------------------- //

	public CmdFactionsInvite() {
		this.aliases.addAll(Conf.cmdAliasesInvite);

		this.requiredArgs.add("player name");

		this.permission = Permission.INVITE.node;
		this.disableOnLock = true;

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
		FPlayer who = this.argAsBestFPlayerMatch(0);

		if (who == null) return; // TODO: does this send a message?

		if (who.getFaction() == myFaction) {
			this.msg(Lang.COMMAND_INVITE_ALREADYMEMBER, who.getName(), myFaction.getTag());
			this.msg(Lang.GENERIC_YOUMAYWANT.toString() + CmdFactions.get().cmdKick.getUseageTemplate(false));
			return;
		}

		// if economy is enabled, they're not on the bypass list, and this command has a cost set, make 'em pay
		if (!payForCommand(Conf.econCostInvite, Lang.COMMAND_INVITE_TOINVITE.toString(), Lang.COMMAND_INVITE_FORINVITE.toString())) {
			return;
		}

		this.myFaction.invite(who);
		if (!who.isOnline()) return;
		
		// Tooltips, colors, and commands only apply to the string immediately before it.
		FancyMessage message = new FancyMessage(fme.describeTo(who, true))
				.tooltip(Lang.COMMAND_INVITE_CLICKTOJOIN.toString())
				.command("/" + Conf.baseCommandAliases.get(0) + " join " + myFaction.getTag())
			.then(Lang.COMMAND_INVITE_INVITEDYOU.toString())
				.color(ChatColor.YELLOW).tooltip(Lang.COMMAND_INVITE_CLICKTOJOIN.toString())
				.command("/" + Conf.baseCommandAliases.get(0) + " join " + myFaction.getTag())
			.then(myFaction.describeTo(who)).tooltip(Lang.COMMAND_INVITE_CLICKTOJOIN.toString())
				.command("/" + Conf.baseCommandAliases.get(0) + " join " + myFaction.getTag());

		message.send(who.getPlayer());

		this.myFaction.msg(Lang.COMMAND_INVITE_INVITED, fme.describeTo(myFaction, true), who.describeTo(myFaction));
	}

	@Override
	public String getUsageTranslation() {
		return Lang.COMMAND_INVITE_DESCRIPTION.toString();
	}

}
