package net.redstoneore.legacyfactions.cmd;

import net.redstoneore.legacyfactions.Permission;
import net.redstoneore.legacyfactions.entity.Conf;
import net.redstoneore.legacyfactions.Lang;
import net.redstoneore.legacyfactions.scoreboards.FScoreboard;
import net.redstoneore.legacyfactions.scoreboards.FScoreboards;

public class CmdFactionsScoreboard extends FCommand {

	// -------------------------------------------------- //
	// CONSTRUCT
	// -------------------------------------------------- //

	public CmdFactionsScoreboard() {
		this.aliases.addAll(Conf.cmdAliasesScoreboard);
		
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
