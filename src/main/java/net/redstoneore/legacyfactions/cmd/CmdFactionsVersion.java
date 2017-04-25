package net.redstoneore.legacyfactions.cmd;

import net.redstoneore.legacyfactions.Factions;
import net.redstoneore.legacyfactions.Permission;
import net.redstoneore.legacyfactions.Lang;


public class CmdFactionsVersion extends FCommand {

    public CmdFactionsVersion() {
        this.aliases.add("version");
        this.aliases.add("ver");

        //this.requiredArgs.add("");
        //this.optionalArgs.put("", "");

        this.permission = Permission.VERSION.node;
        this.disableOnLock = false;

        senderMustBePlayer = false;
        senderMustBeMember = false;
        senderMustBeModerator = false;
        senderMustBeAdmin = false;
    }

    @Override
    public void perform() {
        msg(Lang.COMMAND_VERSION_VERSION, Factions.get().getDescription().getFullName());
    }

    @Override
    public String getUsageTranslation() {
        return Lang.COMMAND_VERSION_DESCRIPTION.toString();
    }
}
