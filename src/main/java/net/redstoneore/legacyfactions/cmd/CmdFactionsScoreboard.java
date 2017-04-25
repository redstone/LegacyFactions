package net.redstoneore.legacyfactions.cmd;

import net.redstoneore.legacyfactions.Permission;
import net.redstoneore.legacyfactions.Lang;
import net.redstoneore.legacyfactions.scoreboards.FScoreboard;

public class CmdFactionsScoreboard extends FCommand {

    public CmdFactionsScoreboard() {
        this.aliases.add("sb");
        this.aliases.add("scoreboard");
        this.permission = Permission.SCOREBOARD.node;
        this.senderMustBePlayer = true;
    }

    @Override
    public void perform() {
        boolean toggleTo = !fme.showScoreboard();
        FScoreboard board = FScoreboard.get(fme);
        if (board == null) {
            me.sendMessage(Lang.COMMAND_TOGGLESB_DISABLED.toString());
        } else {
            me.sendMessage(Lang.TOGGLE_SB.toString().replace("{value}", String.valueOf(toggleTo)));
            board.setSidebarVisibility(toggleTo);
        }
        fme.setShowScoreboard(toggleTo);
    }

    @Override
    public String getUsageTranslation() {
        return Lang.COMMAND_SCOREBOARD_DESCRIPTION.toString();
    }
}
