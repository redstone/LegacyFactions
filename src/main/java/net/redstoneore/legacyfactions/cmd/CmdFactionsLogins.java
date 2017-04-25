package net.redstoneore.legacyfactions.cmd;

import net.redstoneore.legacyfactions.Permission;
import net.redstoneore.legacyfactions.Lang;

public class CmdFactionsLogins extends FCommand {

    public CmdFactionsLogins() {
        super();
        this.aliases.add("login");
        this.aliases.add("logins");
        this.aliases.add("logout");
        this.aliases.add("logouts");
        this.senderMustBePlayer = true;
        this.senderMustBeMember = true;
        this.permission = Permission.MONITOR_LOGINS.node;
    }

    @Override
    public void perform() {
        boolean monitor = fme.isMonitoringJoins();
        fme.msg(Lang.COMMAND_LOGINS_TOGGLE, String.valueOf(!monitor));
        fme.setMonitorJoins(!monitor);
    }

    @Override
    public String getUsageTranslation() {
        return Lang.COMMAND_LOGINS_DESCRIPTION.toString();
    }
}
