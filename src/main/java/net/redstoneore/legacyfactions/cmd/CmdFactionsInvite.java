package net.redstoneore.legacyfactions.cmd;

import mkremins.fanciful.FancyMessage;

import net.redstoneore.legacyfactions.Permission;
import net.redstoneore.legacyfactions.config.CommandAliases;
import net.redstoneore.legacyfactions.config.Config;
import net.redstoneore.legacyfactions.entity.FPlayer;
import net.redstoneore.legacyfactions.lang.Lang;
import net.redstoneore.legacyfactions.util.TextUtil;

import org.bukkit.ChatColor;

public class CmdFactionsInvite extends FCommand {

	// -------------------------------------------------- //
	// INSTANCE
	// -------------------------------------------------- //
	
	private static CmdFactionsInvite instance = new CmdFactionsInvite();
	public static CmdFactionsInvite get() { return instance; }
	
	// -------------------------------------------------- //
	// CONSTRUCT
	// -------------------------------------------------- //

	private CmdFactionsInvite() {
		this.aliases.addAll(CommandAliases.cmdAliasesInvite);

		this.requiredArgs.add("player name");

		this.permission = Permission.INVITE.getNode();
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
		FPlayer who = this.argAsBestFPlayerMatch(0, null, true);

		if (who == null) return;
		
		if (who.getFaction() == myFaction) {
			this.sendMessage(Lang.COMMAND_INVITE_ALREADYMEMBER, who.getName(), myFaction.getTag());
			this.sendMessage(Lang.GENERIC_YOUMAYWANT.toString() + CmdFactionsKick.get().getUseageTemplate(false));
			return;
		}
		
		if (this.myFaction.isBanned(who)) {
			this.fme.sendMessage(TextUtil.parseColor(Lang.COMMAND_JOIN_ISBANNED.toString()).replace("<player>", who.getName()));

			return;
		}

		// if economy is enabled, they're not on the bypass list, and this command has a cost set, make 'em pay
		if (!payForCommand(Config.econCostInvite, Lang.COMMAND_INVITE_TOINVITE.toString(), Lang.COMMAND_INVITE_FORINVITE.toString())) {
			return;
		}

		this.myFaction.invite(who);
		if (!who.isOnline()) return;
		
		// Tooltips, colors, and commands only apply to the string immediately before it.
		FancyMessage message = new FancyMessage(fme.describeTo(who, true))
				.tooltip(Lang.COMMAND_INVITE_CLICKTOJOIN.toString())
				.command("/" + CommandAliases.baseCommandAliases.get(0) + " " + CommandAliases.cmdAliasesJoin.get(0) + " " + this.myFaction.getTag())
			.then(Lang.COMMAND_INVITE_INVITEDYOU.toString())
				.color(ChatColor.YELLOW).tooltip(Lang.COMMAND_INVITE_CLICKTOJOIN.toString())
				.command("/" + CommandAliases.baseCommandAliases.get(0) + " " + CommandAliases.cmdAliasesJoin.get(0) + " " + this.myFaction.getTag())
			.then(myFaction.describeTo(who)).tooltip(Lang.COMMAND_INVITE_CLICKTOJOIN.toString())
				.command("/" + CommandAliases.baseCommandAliases.get(0) + " " + CommandAliases.cmdAliasesJoin.get(0) + " " + this.myFaction.getTag());

		message.send(who.getPlayer());

		this.myFaction.sendMessage(Lang.COMMAND_INVITE_INVITED, fme.describeTo(myFaction, true), who.describeTo(myFaction));
	}

	@Override
	public String getUsageTranslation() {
		return Lang.COMMAND_INVITE_DESCRIPTION.toString();
	}

}
