package net.redstoneore.legacyfactions.cmd;

import java.util.ArrayList;

import net.redstoneore.legacyfactions.Factions;
import net.redstoneore.legacyfactions.Lang;
import net.redstoneore.legacyfactions.entity.CommandAliases;
import net.redstoneore.legacyfactions.util.TextUtil;

public class CmdFactionsAutohelp extends MCommand<Factions> {

	// -------------------------------------------------- //
	// INSTANCE
	// -------------------------------------------------- //
	
	private static CmdFactionsAutohelp instance = new CmdFactionsAutohelp();
	public static CmdFactionsAutohelp get() { return instance; }

	// -------------------------------------------------- //
	// CONSTRUCT
	// -------------------------------------------------- //

	private CmdFactionsAutohelp() {
		this.aliases.addAll(CommandAliases.cmdAliasesAutohelp);

		this.setHelpShort("");

		this.optionalArgs.put("page", "1");
	}

	// -------------------------------------------------- //
	// METHODS
	// -------------------------------------------------- //

	@Override
	public void perform() {
		if (this.commandChain.isEmpty()) return;
		
		MCommand<?> pcmd = this.commandChain.get(this.commandChain.size() - 1);

		ArrayList<String> lines = new ArrayList<String>();

		lines.addAll(pcmd.helpLong);

		for (MCommand<?> scmd : pcmd.subCommands) {
			if (scmd.visibility == CommandVisibility.VISIBLE || (scmd.visibility == CommandVisibility.SECRET && scmd.validSenderPermissions(sender, false))) {
				lines.add(scmd.getUseageTemplate(this.commandChain, true));
			}
		}

		sendMessage(TextUtil.get().getPage(lines, this.argAsInt(0, 1), Lang.COMMAND_AUTOHELP_HELPFOR.toString() + pcmd.aliases.get(0) + "\""));
	}

	@Override
	public String getUsageTranslation() {
		return Lang.COMMAND_HELP_DESCRIPTION.toString();
	}
	
}
