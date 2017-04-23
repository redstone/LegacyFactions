package net.redstoneore.legacyfactions.cmd;

import net.redstoneore.legacyfactions.Permission;
import net.redstoneore.legacyfactions.TL;

public class CmdFactionsLeave extends FCommand {

    public CmdFactionsLeave() {
        super();
        this.aliases.add("leave");

        //this.requiredArgs.add("");
        //this.optionalArgs.put("", "");

        this.permission = Permission.LEAVE.node;
        this.disableOnLock = true;

        senderMustBePlayer = true;
        senderMustBeMember = true;
        senderMustBeModerator = false;
        senderMustBeAdmin = false;
    }

    @Override
    public void perform() {
        fme.leave(true);
    }

    @Override
    public TL getUsageTranslation() {
        return TL.LEAVE_DESCRIPTION;
    }

}
