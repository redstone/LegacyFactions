package net.redstoneore.legacyfactions.cmd;

import net.redstoneore.legacyfactions.Permission;
import net.redstoneore.legacyfactions.entity.Conf;
import net.redstoneore.legacyfactions.Lang;

public class CmdFactionsLogins extends FCommand {

	// -------------------------------------------------- //
	// CONSTRUCT
	// -------------------------------------------------- //

	public CmdFactionsLogins() {
		this.aliases.addAll(Conf.cmdAliasesLogins);
		
		this.senderMustBePlayer = true;
		this.senderMustBeMember = true;
		this.permission = Permission.MONITOR_LOGINS.node;
	}

	// -------------------------------------------------- //
	// METHODS
	// -------------------------------------------------- //

	@Override
	public void perform() {
		boolean monitor = fme.isMonitoringJoins();
		fme.msg(Lang.COMMAND_LOGINS_TOGGLE, String.valueOf(!monitor));
		fme.setMonitorJoins(!monitor);
	}

	@Override
	public String getUsageTranslation() {
		return Lang.COMMAND_LOGINS_DESCRIPTION.toString();
	}
	
}
