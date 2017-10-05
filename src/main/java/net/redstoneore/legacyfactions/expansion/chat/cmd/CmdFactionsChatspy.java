package net.redstoneore.legacyfactions.expansion.chat.cmd;

import net.redstoneore.legacyfactions.Factions;
import net.redstoneore.legacyfactions.Permission;
import net.redstoneore.legacyfactions.cmd.FCommand;
import net.redstoneore.legacyfactions.config.CommandAliases;
import net.redstoneore.legacyfactions.lang.Lang;

public class CmdFactionsChatspy extends FCommand {

    // -------------------------------------------------- //
    // INSTANCE
    // -------------------------------------------------- //
	
	private static CmdFactionsChatspy instance = new CmdFactionsChatspy();
	public static CmdFactionsChatspy get() { return instance; }

	// -------------------------------------------------- //
	// CONSTRUCT
	// -------------------------------------------------- //

	private CmdFactionsChatspy() {
		this.aliases.addAll(CommandAliases.cmdAliasesChatspy);

		this.optionalArgs.put("on/off", "flip");

		this.permission = Permission.CHATSPY.getNode();
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
		this.fme.setSpyingChat(this.argAsBool(0, !this.fme.isSpyingChat()));

		if (this.fme.isSpyingChat()) {
			this.fme.sendMessage(Lang.COMMAND_CHATSPY_ENABLE);
			Factions.get().log(this.fme.getName() + Lang.COMMAND_CHATSPY_ENABLELOG.toString());
		} else {
			this.fme.sendMessage(Lang.COMMAND_CHATSPY_DISABLE);
			Factions.get().log(this.fme.getName() + Lang.COMMAND_CHATSPY_DISABLELOG.toString());
		}
	}

	@Override
	public String getUsageTranslation() {
		return Lang.COMMAND_CHATSPY_DESCRIPTION.toString();
	}
	
}
