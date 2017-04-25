package net.redstoneore.legacyfactions.cmd;

import net.redstoneore.legacyfactions.Factions;
import net.redstoneore.legacyfactions.Permission;
import net.redstoneore.legacyfactions.TL;
import net.redstoneore.legacyfactions.entity.Board;
import net.redstoneore.legacyfactions.entity.Conf;
import net.redstoneore.legacyfactions.entity.FactionColl;

public class CmdFactionsWarunclaimall extends FCommand {

    public CmdFactionsWarunclaimall() {
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
        Board.get().unclaimAll(FactionColl.get().getWarZone().getId());
        msg(TL.COMMAND_WARUNCLAIMALL_SUCCESS);

        if (Conf.logLandUnclaims) {
            Factions.get().log(TL.COMMAND_WARUNCLAIMALL_LOG.format(fme.getName()));
        }
    }

    @Override
    public String getUsageTranslation() {
        return TL.COMMAND_WARUNCLAIMALL_DESCRIPTION.toString();
    }

}
