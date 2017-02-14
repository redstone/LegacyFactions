package com.massivecraft.factions.cmd;

import com.massivecraft.factions.Factions;
import com.massivecraft.factions.Permission;
import com.massivecraft.factions.TL;
import com.massivecraft.factions.entity.Board;
import com.massivecraft.factions.entity.Conf;
import com.massivecraft.factions.entity.FactionColl;

public class CmdSafeunclaimall extends FCommand {

    public CmdSafeunclaimall() {
        this.aliases.add("safeunclaimall");
        this.aliases.add("safedeclaimall");

        //this.requiredArgs.add("");
        //this.optionalArgs.put("radius", "0");

        this.permission = Permission.MANAGE_SAFE_ZONE.node;
        this.disableOnLock = true;

        senderMustBePlayer = false;
        senderMustBeMember = false;
        senderMustBeModerator = false;
        senderMustBeAdmin = false;

    }

    @Override
    public void perform() {
        Board.getInstance().unclaimAll(FactionColl.getInstance().getSafeZone().getId());
        msg(TL.COMMAND_SAFEUNCLAIMALL_UNCLAIMED);

        if (Conf.logLandUnclaims) {
            Factions.get().log(TL.COMMAND_SAFEUNCLAIMALL_UNCLAIMEDLOG.format(sender.getName()));
        }
    }

    @Override
    public TL getUsageTranslation() {
        return TL.COMMAND_SAFEUNCLAIMALL_DESCRIPTION;
    }

}
