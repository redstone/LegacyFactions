package net.redstoneore.legacyfactions.cmd;

import net.redstoneore.legacyfactions.Factions;
import net.redstoneore.legacyfactions.Permission;
import net.redstoneore.legacyfactions.config.CommandAliases;
import net.redstoneore.legacyfactions.lang.Lang;


public class CmdFactionsVersion extends FCommand {

	// -------------------------------------------------- //
	// INSTANCE
	// -------------------------------------------------- //
	
	private static CmdFactionsVersion instance = new CmdFactionsVersion();
	public static CmdFactionsVersion get() { return instance; }
	
	// -------------------------------------------------- //
	// CONSTRUCT
	// -------------------------------------------------- //

	private CmdFactionsVersion() {
		this.aliases.addAll(CommandAliases.cmdAliasesVersion);
		
		this.permission = Permission.VERSION.getNode();
		this.disableOnLock = false;

		this.senderMustBePlayer = false;
		this.senderMustBeMember = false;
		this.senderMustBeModerator = false;
		this.senderMustBeAdmin = false;
	}

	// -------------------------------------------------- //
	// METHODS
	// -------------------------------------------------- //

	@Override
	public void perform() {
		sendMessage(Lang.COMMAND_VERSION_VERSION, Factions.get().getDescription().getFullName());
	}

	@Override
	public String getUsageTranslation() {
		return Lang.COMMAND_VERSION_DESCRIPTION.toString();
	}
}
