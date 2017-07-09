package net.redstoneore.legacyfactions.cmd;

import net.redstoneore.legacyfactions.Lang;
import net.redstoneore.legacyfactions.Permission;
import net.redstoneore.legacyfactions.entity.Conf;

public class CmdFactionsLeave extends FCommand {

    public CmdFactionsLeave() {
        this.aliases.addAll(Conf.cmdAliasesLeave);

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
    public String getUsageTranslation() {
        return Lang.LEAVE_DESCRIPTION.toString();
    }

}
