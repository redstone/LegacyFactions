package net.redstoneore.legacyfactions.cmd;

import net.redstoneore.legacyfactions.Permission;
import net.redstoneore.legacyfactions.Lang;
import net.redstoneore.legacyfactions.entity.Board;
import net.redstoneore.legacyfactions.entity.Conf;
import net.redstoneore.legacyfactions.entity.FPlayerColl;
import net.redstoneore.legacyfactions.entity.FactionColl;

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
		this.aliases.addAll(Conf.cmdAliasesSaveAll);

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
		FPlayerColl.save(false);
		FactionColl.get().forceSave(false);
		Board.get().forceSave(false);
		Conf.save();
		sendMessage(Lang.COMMAND_SAVEALL_SUCCESS);
	}

	@Override
	public String getUsageTranslation() {
		return Lang.COMMAND_SAVEALL_DESCRIPTION.toString();
	}

}