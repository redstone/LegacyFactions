package net.redstoneore.legacyfactions.cmd;

import net.redstoneore.legacyfactions.Lang;
import net.redstoneore.legacyfactions.Permission;
import net.redstoneore.legacyfactions.entity.Conf;

public class CmdFactionsLeave extends FCommand {

	// -------------------------------------------------- //
	// CONSTRUCT
	// -------------------------------------------------- //

	public CmdFactionsLeave() {
		this.aliases.addAll(Conf.cmdAliasesLeave);

		this.permission = Permission.LEAVE.getNode();
		this.disableOnLock = true;

		this.senderMustBePlayer = true;
		this.senderMustBeMember = true;
		this.senderMustBeModerator = false;
		this.senderMustBeColeader = false;
		this.senderMustBeAdmin = false;
	}

	// -------------------------------------------------- //
	// METHODS
	// -------------------------------------------------- //

	@Override
	public void perform() {
		this.fme.leave(true);
	}

	@Override
	public String getUsageTranslation() {
		return Lang.LEAVE_DESCRIPTION.toString();
	}

}
