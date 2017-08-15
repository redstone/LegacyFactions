package net.redstoneore.legacyfactions.cmd;

import net.redstoneore.legacyfactions.Lang;
import net.redstoneore.legacyfactions.Permission;
import net.redstoneore.legacyfactions.entity.Conf;
import net.redstoneore.legacyfactions.mixin.DebugMixin;

public class CmdFactionsDebug extends FCommand {
	
	// -------------------------------------------------- //
	// SINGLETON
	// -------------------------------------------------- //
	
	private static CmdFactionsDebug instance = new CmdFactionsDebug();
	public static CmdFactionsDebug get() { return instance; }
	
	// -------------------------------------------------- //
	// CONSTRUCT
	// -------------------------------------------------- //

	public CmdFactionsDebug() {
		this.aliases.addAll(Conf.cmdAliasesDebug);

		this.permission = Permission.DEBUG.getNode();
		this.disableOnLock = false;

		this.senderMustBePlayer = false;
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
		if (!this.senderIsConsole) {
			this.sendMessage(Lang.GENERIC_CONSOLEONLY.toString());
			return;
		}
		
		DebugMixin.sendToConsole();
	}

	@Override
	public String getUsageTranslation() {
		return Lang.COMMAND_SAVEALL_DESCRIPTION.toString();
	}

}
