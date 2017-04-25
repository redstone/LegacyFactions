package net.redstoneore.legacyfactions.cmd;

import net.redstoneore.legacyfactions.Factions;
import net.redstoneore.legacyfactions.Permission;
import net.redstoneore.legacyfactions.TL;
import net.redstoneore.legacyfactions.entity.Board;
import net.redstoneore.legacyfactions.entity.Conf;
import net.redstoneore.legacyfactions.entity.FactionColl;

public class CmdFactionsSafeunclaimall extends FCommand {

    public CmdFactionsSafeunclaimall() {
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
        Board.get().unclaimAll(FactionColl.get().getSafeZone().getId());
        msg(TL.COMMAND_SAFEUNCLAIMALL_UNCLAIMED);

        if (Conf.logLandUnclaims) {
            Factions.get().log(TL.COMMAND_SAFEUNCLAIMALL_UNCLAIMEDLOG.format(sender.getName()));
        }
    }

    @Override
    public String getUsageTranslation() {
        return TL.COMMAND_SAFEUNCLAIMALL_DESCRIPTION.toString();
    }

}
