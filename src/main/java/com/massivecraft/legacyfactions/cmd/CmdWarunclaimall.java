package com.massivecraft.legacyfactions.cmd;

import com.massivecraft.legacyfactions.Factions;
import com.massivecraft.legacyfactions.Permission;
import com.massivecraft.legacyfactions.TL;
import com.massivecraft.legacyfactions.entity.Board;
import com.massivecraft.legacyfactions.entity.Conf;
import com.massivecraft.legacyfactions.entity.FactionColl;

public class CmdWarunclaimall extends FCommand {

    public CmdWarunclaimall() {
        this.aliases.add("warunclaimall");
        this.aliases.add("wardeclaimall");

        //this.requiredArgs.add("");
        //this.optionalArgs.put("", "");

        this.permission = Permission.MANAGE_WAR_ZONE.node;
        this.disableOnLock = true;

        senderMustBePlayer = false;
        senderMustBeMember = false;
        senderMustBeModerator = false;
        senderMustBeAdmin = false;
    }

    @Override
    public void perform() {
        Board.getInstance().unclaimAll(FactionColl.getInstance().getWarZone().getId());
        msg(TL.COMMAND_WARUNCLAIMALL_SUCCESS);

        if (Conf.logLandUnclaims) {
            Factions.get().log(TL.COMMAND_WARUNCLAIMALL_LOG.format(fme.getName()));
        }
    }

    @Override
    public TL getUsageTranslation() {
        return TL.COMMAND_WARUNCLAIMALL_DESCRIPTION;
    }

}
