package net.redstoneore.legacyfactions.cmd;

import net.redstoneore.legacyfactions.Factions;
import net.redstoneore.legacyfactions.Permission;
import net.redstoneore.legacyfactions.config.CommandAliases;
import net.redstoneore.legacyfactions.config.Config;
import net.redstoneore.legacyfactions.config.meta.Meta;
import net.redstoneore.legacyfactions.lang.Lang;

public class CmdFactionsReload extends FCommand {

	// -------------------------------------------------- //
	// INSTANCE
	// -------------------------------------------------- //
	
	private static CmdFactionsReload instance = new CmdFactionsReload();
	public static CmdFactionsReload get() { return instance; }
	
	// -------------------------------------------------- //
	// CONSTRUCT
	// -------------------------------------------------- //

	private CmdFactionsReload() {
		this.aliases.addAll(CommandAliases.cmdAliasesReload);

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
		Config.load();
		Factions.get().reloadConfig();
		
		Lang.reload(Meta.get().lang.getPath());
		
		long timeReload = (System.currentTimeMillis() - timeInitStart);

		CmdFactionsHelp.get().clearHelpPageCache();
		
		this.sendMessage(Lang.COMMAND_RELOAD_TIME, timeReload);
	}

	@Override
	public String getUsageTranslation() {
		return Lang.COMMAND_RELOAD_DESCRIPTION.toString();
	}
	
}
