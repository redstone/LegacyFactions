package net.redstoneore.legacyfactions.cmd;

import net.redstoneore.legacyfactions.Factions;
import net.redstoneore.legacyfactions.Permission;
import net.redstoneore.legacyfactions.config.CommandAliases;
import net.redstoneore.legacyfactions.lang.Lang;

public class CmdFactionsBypass extends FCommand {

	// -------------------------------------------------- //
	// INSTANCE
	// -------------------------------------------------- //
	
	private static CmdFactionsBypass instance = new CmdFactionsBypass();
	public static CmdFactionsBypass get() { return instance; }
	
	// -------------------------------------------------- //
	// CONSTRUCT
	// -------------------------------------------------- //

	private CmdFactionsBypass() {
		this.aliases.addAll(CommandAliases.cmdAliasesBypass);

		this.optionalArgs.put("on/off", "flip");

		this.permission = Permission.BYPASS.getNode();
		this.disableOnLock = false;

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
		fme.setIsAdminBypassing(this.argAsBool(0, !fme.isAdminBypassing()));

		// TODO: Move this to a transient field in the model??
		if (fme.isAdminBypassing()) {
			fme.sendMessage(Lang.COMMAND_BYPASS_ENABLE.toString());
			Factions.get().log(fme.getName() + Lang.COMMAND_BYPASS_ENABLELOG.toString());
		} else {
			fme.sendMessage(Lang.COMMAND_BYPASS_DISABLE.toString());
			Factions.get().log(fme.getName() + Lang.COMMAND_BYPASS_DISABLELOG.toString());
		}
	}

	@Override
	public String getUsageTranslation() {
		return Lang.COMMAND_BYPASS_DESCRIPTION.toString();
	}
	
}
