package net.redstoneore.legacyfactions.cmd;

import net.redstoneore.legacyfactions.Permission;
import net.redstoneore.legacyfactions.Lang;
import net.redstoneore.legacyfactions.entity.Conf;

public class CmdFactionsBoom extends FCommand {

	// -------------------------------------------------- //
	// CONSTRUCT
	// -------------------------------------------------- //

	public CmdFactionsBoom() {
		this.aliases.addAll(Conf.cmdAliasesBoom);

		this.optionalArgs.put("on/off", "flip");

		this.permission = Permission.NO_BOOM.getNode();
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
		if (!myFaction.isPeaceful()) {
			fme.msg(Lang.COMMAND_BOOM_PEACEFULONLY);
			return;
		}

		// if economy is enabled, they're not on the bypass list, and this command has a cost set, make 'em pay
		if (!payForCommand(Conf.econCostNoBoom, Lang.COMMAND_BOOM_TOTOGGLE, Lang.COMMAND_BOOM_FORTOGGLE)) {
			return;
		}

		myFaction.setPeacefulExplosionsEnabled(this.argAsBool(0, !myFaction.getPeacefulExplosionsEnabled()));

		String enabled = myFaction.noExplosionsInTerritory() ? Lang.GENERIC_DISABLED.toString() : Lang.GENERIC_ENABLED.toString();

		// Inform
		myFaction.msg(Lang.COMMAND_BOOM_ENABLED, fme.describeTo(myFaction), enabled);
	}

	@Override
	public String getUsageTranslation() {
		return Lang.COMMAND_BOOM_DESCRIPTION.toString();
	}
	
}
