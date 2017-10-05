package net.redstoneore.legacyfactions.cmd;

import net.redstoneore.legacyfactions.Permission;
import net.redstoneore.legacyfactions.config.CommandAliases;
import net.redstoneore.legacyfactions.lang.Lang;
import net.redstoneore.legacyfactions.scoreboards.FScoreboard;
import net.redstoneore.legacyfactions.scoreboards.FScoreboards;

public class CmdFactionsScoreboard extends FCommand {

	// -------------------------------------------------- //
	// INSTANCE
	// -------------------------------------------------- //
	
	private static CmdFactionsScoreboard instance = new CmdFactionsScoreboard();
	public static CmdFactionsScoreboard get() { return instance; }
	
	// -------------------------------------------------- //
	// CONSTRUCT
	// -------------------------------------------------- //

	private CmdFactionsScoreboard() {
		this.aliases.addAll(CommandAliases.cmdAliasesScoreboard);
		
		this.permission = Permission.SCOREBOARD.getNode();
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
		boolean scoreboardVisible = !fme.showScoreboard();
		FScoreboard scoreboard = FScoreboards.get(fme);
		if (scoreboard == null) {
			this.me.sendMessage(Lang.COMMAND_TOGGLESB_DISABLED.toString());
		} else {
			this.me.sendMessage(Lang.TOGGLE_SB.toString().replace("{value}", String.valueOf(scoreboardVisible)));
			scoreboard.setSidebarVisibility(scoreboardVisible);
		}
		this.fme.setShowScoreboard(scoreboardVisible);
	}

	@Override
	public String getUsageTranslation() {
		return Lang.COMMAND_SCOREBOARD_DESCRIPTION.toString();
	}
	
}
