package net.redstoneore.legacyfactions.cmd;

import net.redstoneore.legacyfactions.Factions;
import net.redstoneore.legacyfactions.Permission;
import net.redstoneore.legacyfactions.Lang;
import net.redstoneore.legacyfactions.entity.Conf;

public class CmdFactionsReload extends FCommand {

	// -------------------------------------------------- //
	// CONSTRUCT
	// -------------------------------------------------- //

	public CmdFactionsReload() {
		this.aliases.addAll(Conf.cmdAliasesReload);

		this.optionalArgs.put("file", "all");

		this.permission = Permission.RELOAD.getNode();
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
		long timeInitStart = System.currentTimeMillis();
		Conf.load();
		Factions.get().reloadConfig();
		
		Lang.reload();
		
		long timeReload = (System.currentTimeMillis() - timeInitStart);

		msg(Lang.COMMAND_RELOAD_TIME, timeReload);
	}

	@Override
	public String getUsageTranslation() {
		return Lang.COMMAND_RELOAD_DESCRIPTION.toString();
	}
	
}
