package net.redstoneore.legacyfactions.cmd;

import net.redstoneore.legacyfactions.Permission;
import net.redstoneore.legacyfactions.Lang;
import net.redstoneore.legacyfactions.entity.Conf;
import net.redstoneore.legacyfactions.entity.FPlayer;
import net.redstoneore.legacyfactions.util.TextUtil;

public class CmdFactionsTitle extends FCommand {

	// -------------------------------------------------- //
	// CONSTRUCT
	// -------------------------------------------------- //

	public CmdFactionsTitle() {
		this.aliases.addAll(Conf.cmdAliasesTitle);

		this.requiredArgs.add("player name");
		this.optionalArgs.put("title", "");

		this.permission = Permission.TITLE.getNode();
		this.disableOnLock = true;

		this.senderMustBePlayer = true;
		this.senderMustBeMember = false;
		this.senderMustBeModerator = true;
		this.senderMustBeAdmin = false;
	}

	// -------------------------------------------------- //
	// METHODS
	// -------------------------------------------------- //

	@Override
	public void perform() {
		FPlayer you = this.argAsBestFPlayerMatch(0);
		if (you == null) return;

		this.args.remove(0);
		String title = TextUtil.implode(args, " ");
		
		if (!this.canIAdministerYou(this.fme, you)) return;

		// if economy is enabled, they're not on the bypass list, and this command has a cost set, make 'em pay
		if (!payForCommand(Conf.econCostTitle, Lang.COMMAND_TITLE_TOCHANGE, Lang.COMMAND_TITLE_FORCHANGE)) {
			return;
		}

		if (Conf.allowColourCodesInFactionTitle) {
			title = TextUtil.parseColor(title);
		}
		
		you.setTitle(title);

		// Inform
		this.myFaction.sendMessage(Lang.COMMAND_TITLE_CHANGED, this.fme.describeTo(this.myFaction, true), you.describeTo(this.myFaction, true));
	}

	@Override
	public String getUsageTranslation() {
		return Lang.COMMAND_TITLE_DESCRIPTION.toString();
	}

}
