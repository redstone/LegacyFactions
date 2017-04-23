package net.redstoneore.legacyfactions.cmd;

import net.redstoneore.legacyfactions.Factions;
import net.redstoneore.legacyfactions.Permission;
import net.redstoneore.legacyfactions.TL;

public class CmdFactionsBypass extends FCommand {

    public CmdFactionsBypass() {
        super();
        this.aliases.add("bypass");

        //this.requiredArgs.add("");
        this.optionalArgs.put("on/off", "flip");

        this.permission = Permission.BYPASS.node;
        this.disableOnLock = false;

        senderMustBePlayer = true;
        senderMustBeMember = false;
        senderMustBeModerator = false;
        senderMustBeAdmin = false;
    }

    @Override
    public void perform() {
        fme.setIsAdminBypassing(this.argAsBool(0, !fme.isAdminBypassing()));

        // TODO: Move this to a transient field in the model??
        if (fme.isAdminBypassing()) {
            fme.msg(TL.COMMAND_BYPASS_ENABLE.toString());
            Factions.get().log(fme.getName() + TL.COMMAND_BYPASS_ENABLELOG.toString());
        } else {
            fme.msg(TL.COMMAND_BYPASS_DISABLE.toString());
            Factions.get().log(fme.getName() + TL.COMMAND_BYPASS_DISABLELOG.toString());
        }
    }

    @Override
    public TL getUsageTranslation() {
        return TL.COMMAND_BYPASS_DESCRIPTION;
    }
}
