package net.redstoneore.legacyfactions.expansion.chat.cmd;

import net.redstoneore.legacyfactions.Permission;
import net.redstoneore.legacyfactions.cmd.FCommand;
import net.redstoneore.legacyfactions.config.CommandAliases;
import net.redstoneore.legacyfactions.config.Config;
import net.redstoneore.legacyfactions.lang.Lang;

public class CmdFactionsToggleAllianceChat extends FCommand {

    // -------------------------------------------------- //
    // INSTANCE
    // -------------------------------------------------- //
	
	private static CmdFactionsToggleAllianceChat instance = new CmdFactionsToggleAllianceChat();
	public static CmdFactionsToggleAllianceChat get() { return instance; }
	
	// -------------------------------------------------- //
	// CONSTRUCT
	// -------------------------------------------------- //

	private CmdFactionsToggleAllianceChat() {
		this.aliases.addAll(CommandAliases.cmdAliasesToggleAllianceChat);

		this.disableOnLock = false;

		this.permission = Permission.TOGGLE_ALLIANCE_CHAT.getNode();
		this.disableOnLock = false;

		this.senderMustBePlayer = true;
		this.senderMustBeMember = true;
		this.senderMustBeModerator = false;
		this.senderMustBeAdmin = false;
	}

	// -------------------------------------------------- //
	// METHODS
	// -------------------------------------------------- //

	@Override
	public void perform() {
		if (!Config.expansionsFactionsChat.enableAllianceChat) {
			sendMessage(Lang.COMMAND_CHAT_DISABLED.toString());
			return;
		}

		boolean ignoring = fme.isIgnoreAllianceChat();

		sendMessage(ignoring ? Lang.COMMAND_TOGGLEALLIANCECHAT_UNIGNORE : Lang.COMMAND_TOGGLEALLIANCECHAT_IGNORE);
		fme.setIgnoreAllianceChat(!ignoring);
	}
	
	@Override
	public String getUsageTranslation() {
		return Lang.COMMAND_TOGGLEALLIANCECHAT_DESCRIPTION.toString();
	}
	
}
