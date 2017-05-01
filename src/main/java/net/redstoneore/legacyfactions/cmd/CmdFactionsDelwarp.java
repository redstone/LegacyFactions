package net.redstoneore.legacyfactions.cmd;

import java.util.Optional;

import net.redstoneore.legacyfactions.Permission;
import net.redstoneore.legacyfactions.Lang;
import net.redstoneore.legacyfactions.entity.Conf;
import net.redstoneore.legacyfactions.event.EventFactionsWarpDelete;
import net.redstoneore.legacyfactions.warp.FactionWarp;

public class CmdFactionsDelwarp extends FCommand {

    public CmdFactionsDelwarp() {
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
            FactionWarp warp = owarp.get();
            Double cost = Conf.warpCost.get("delete");
            
            if (fme.isAdminBypassing()) cost = 0.0;
            
            EventFactionsWarpDelete event = new EventFactionsWarpDelete(myFaction, fme, warp, cost);
            event.call();
            if (event.isCancelled()) return;
            
            if (cost > 0 && !fme.isAdminBypassing() && !this.payForCommand(Conf.warpCost.get("delete"), Lang.COMMAND_DELFWARP_TODELETE.toString(), Lang.COMMAND_DELFWARP_FORDELETE.toString())) return;
            
            warp.delete();
            fme.msg(Lang.COMMAND_DELFWARP_DELETED, name);
        } else {
            fme.msg(Lang.COMMAND_DELFWARP_INVALID, name);
        }
    }

    @Override
    public String getUsageTranslation() {
        return Lang.COMMAND_DELFWARP_DESCRIPTION.toString();
    }
}
