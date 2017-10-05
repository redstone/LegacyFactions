package net.redstoneore.legacyfactions.cmd;

import net.redstoneore.legacyfactions.Permission;
import net.redstoneore.legacyfactions.config.CommandAliases;
import net.redstoneore.legacyfactions.lang.Lang;

public class CmdFactionsLeave extends FCommand {

	// -------------------------------------------------- //
	// INSTANCE
	// -------------------------------------------------- //
	
	private static CmdFactionsLeave instance = new CmdFactionsLeave();
	public static CmdFactionsLeave get() { return instance; }
	
	// -------------------------------------------------- //
	// CONSTRUCT
	// -------------------------------------------------- //

	private CmdFactionsLeave() {
		this.aliases.addAll(CommandAliases.cmdAliasesLeave);

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
