package net.redstoneore.legacyfactions.cmd;

import net.redstoneore.legacyfactions.Permission;
import net.redstoneore.legacyfactions.config.CommandAliases;
import net.redstoneore.legacyfactions.lang.Lang;

public class CmdFactionsLogins extends FCommand {

	// -------------------------------------------------- //
	// INSTANCE
	// -------------------------------------------------- //
	
	private static CmdFactionsLogins instance = new CmdFactionsLogins();
	public static CmdFactionsLogins get() { return instance; }
	
	// -------------------------------------------------- //
	// CONSTRUCT
	// -------------------------------------------------- //

	private CmdFactionsLogins() {
		this.aliases.addAll(CommandAliases.cmdAliasesLogins);
		
		this.senderMustBePlayer = true;
		this.senderMustBeMember = true;
		this.permission = Permission.MONITOR_LOGINS.getNode();
	}

	// -------------------------------------------------- //
	// METHODS
	// -------------------------------------------------- //

	@Override
	public void perform() {
		boolean monitor = fme.isMonitoringJoins();
		fme.sendMessage(Lang.COMMAND_LOGINS_TOGGLE, String.valueOf(!monitor));
		fme.setMonitorJoins(!monitor);
	}

	@Override
	public String getUsageTranslation() {
		return Lang.COMMAND_LOGINS_DESCRIPTION.toString();
	}
	
}
