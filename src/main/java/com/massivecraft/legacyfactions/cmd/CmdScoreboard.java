package com.massivecraft.legacyfactions.cmd;

import com.massivecraft.legacyfactions.Permission;
import com.massivecraft.legacyfactions.TL;
import com.massivecraft.legacyfactions.scoreboards.FScoreboard;

public class CmdScoreboard extends FCommand {

    public CmdScoreboard() {
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
            me.sendMessage(TL.COMMAND_TOGGLESB_DISABLED.toString());
        } else {
            me.sendMessage(TL.TOGGLE_SB.toString().replace("{value}", String.valueOf(toggleTo)));
            board.setSidebarVisibility(toggleTo);
        }
        fme.setShowScoreboard(toggleTo);
    }

    @Override
    public TL getUsageTranslation() {
        return TL.COMMAND_SCOREBOARD_DESCRIPTION;
    }
}
