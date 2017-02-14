package com.massivecraft.factionsuuid.cmd;

import com.massivecraft.factionsuuid.Permission;
import com.massivecraft.factionsuuid.TL;

public class CmdLogins extends FCommand {

    public CmdLogins() {
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
        fme.msg(TL.COMMAND_LOGINS_TOGGLE, String.valueOf(!monitor));
        fme.setMonitorJoins(!monitor);
    }

    @Override
    public TL getUsageTranslation() {
        return TL.COMMAND_LOGINS_DESCRIPTION;
    }
}
