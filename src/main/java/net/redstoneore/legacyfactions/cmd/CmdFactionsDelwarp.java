package net.redstoneore.legacyfactions.cmd;

import java.util.Optional;

import net.redstoneore.legacyfactions.Permission;
import net.redstoneore.legacyfactions.TL;
import net.redstoneore.legacyfactions.entity.Conf;
import net.redstoneore.legacyfactions.entity.FPlayer;
import net.redstoneore.legacyfactions.warp.FactionWarp;

public class CmdFactionsDelwarp extends FCommand {

    public CmdFactionsDelwarp() {
        super();
        this.aliases.add("delwarp");
        this.aliases.add("dw");
        this.aliases.add("deletewarp");
        this.requiredArgs.add("warp name");
        this.senderMustBeMember = true;
        this.senderMustBeModerator = true;
        this.senderMustBePlayer = true;
        this.permission = Permission.SETWARP.node;
    }

    @Override
    public void perform() {
        String name = argAsString(0);
        Optional<FactionWarp> owarp = myFaction.warps().get(name);
        if (owarp.isPresent()) {
            if (!transact(fme)) {
                return;
            }
            owarp.get().delete();
            fme.msg(TL.COMMAND_DELFWARP_DELETED, name);
        } else {
            fme.msg(TL.COMMAND_DELFWARP_INVALID, name);
        }
    }

    private boolean transact(FPlayer player) {
        return Conf.warpCost.get("delete") == 0 || player.isAdminBypassing() || payForCommand(Conf.warpCost.get("delete"), TL.COMMAND_DELFWARP_TODELETE.toString(), TL.COMMAND_DELFWARP_FORDELETE.toString());
    }

    @Override
    public TL getUsageTranslation() {
        return TL.COMMAND_DELFWARP_DESCRIPTION;
    }
}
