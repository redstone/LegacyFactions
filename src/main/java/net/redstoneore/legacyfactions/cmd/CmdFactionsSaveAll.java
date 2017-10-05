package net.redstoneore.legacyfactions.cmd;

import net.redstoneore.legacyfactions.Permission;
import net.redstoneore.legacyfactions.config.CommandAliases;
import net.redstoneore.legacyfactions.config.Config;

import org.bukkit.command.CommandSender;

import net.redstoneore.legacyfactions.entity.Board;
import net.redstoneore.legacyfactions.entity.FPlayerColl;
import net.redstoneore.legacyfactions.entity.FactionColl;
import net.redstoneore.legacyfactions.lang.Lang;

public class CmdFactionsSaveAll extends FCommand {

	// -------------------------------------------------- //
	// INSTANCE
	// -------------------------------------------------- //
	
	private static CmdFactionsSaveAll instance = new CmdFactionsSaveAll();
	public static CmdFactionsSaveAll get() { return instance; }
	
	// -------------------------------------------------- //
	// CONSTRUCT
	// -------------------------------------------------- //

	private CmdFactionsSaveAll() {
		this.aliases.addAll(CommandAliases.cmdAliasesSaveAll);

		this.permission = Permission.SAVE.getNode();
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
		this.perform(false, this.sender);
	}
	
	public void perform(boolean sync, CommandSender notify) {
		FPlayerColl.save(sync);
		FactionColl.get().forceSave(sync);
		Board.get().forceSave(sync);
		Config.save();
		
		if (notify != null) {
			Lang.COMMAND_SAVEALL_SUCCESS.getBuilder()
			.sendTo(notify);			
		}
	}

	@Override
	public String getUsageTranslation() {
		return Lang.COMMAND_SAVEALL_DESCRIPTION.toString();
	}

}