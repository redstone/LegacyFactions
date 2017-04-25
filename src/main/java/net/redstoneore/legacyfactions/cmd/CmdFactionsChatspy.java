package net.redstoneore.legacyfactions.cmd;

import net.redstoneore.legacyfactions.Factions;
import net.redstoneore.legacyfactions.Permission;
import net.redstoneore.legacyfactions.TL;

public class CmdFactionsChatspy extends FCommand {

    public CmdFactionsChatspy() {
        super();
        this.aliases.add("chatspy");

        this.optionalArgs.put("on/off", "flip");

        this.permission = Permission.CHATSPY.node;
        this.disableOnLock = false;

        senderMustBePlayer = true;
        senderMustBeMember = false;
        senderMustBeModerator = false;
        senderMustBeAdmin = false;
    }

    @Override
    public void perform() {
        fme.setSpyingChat(this.argAsBool(0, !fme.isSpyingChat()));

        if (fme.isSpyingChat()) {
            fme.msg(TL.COMMAND_CHATSPY_ENABLE);
            Factions.get().log(fme.getName() + TL.COMMAND_CHATSPY_ENABLELOG.toString());
        } else {
            fme.msg(TL.COMMAND_CHATSPY_DISABLE);
            Factions.get().log(fme.getName() + TL.COMMAND_CHATSPY_DISABLELOG.toString());
        }
    }

    @Override
    public String getUsageTranslation() {
        return TL.COMMAND_CHATSPY_DESCRIPTION.toString();
    }
}