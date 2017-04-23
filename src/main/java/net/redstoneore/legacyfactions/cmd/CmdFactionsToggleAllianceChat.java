package net.redstoneore.legacyfactions.cmd;

import net.redstoneore.legacyfactions.Permission;
import net.redstoneore.legacyfactions.TL;
import net.redstoneore.legacyfactions.entity.Conf;

public class CmdFactionsToggleAllianceChat extends FCommand {

    public CmdFactionsToggleAllianceChat() {
        super();
        this.aliases.add("tac");
        this.aliases.add("togglealliancechat");
        this.aliases.add("ac");

        this.disableOnLock = false;

        this.permission = Permission.TOGGLE_ALLIANCE_CHAT.node;
        this.disableOnLock = false;

        senderMustBePlayer = true;
        senderMustBeMember = true;
        senderMustBeModerator = false;
        senderMustBeAdmin = false;
    }

    @Override
    public TL getUsageTranslation() {
        return TL.COMMAND_TOGGLEALLIANCECHAT_DESCRIPTION;
    }

    @Override
    public void perform() {
        if (!Conf.factionOnlyChat) {
            msg(TL.COMMAND_CHAT_DISABLED.toString());
            return;
        }

        boolean ignoring = fme.isIgnoreAllianceChat();

        msg(ignoring ? TL.COMMAND_TOGGLEALLIANCECHAT_UNIGNORE : TL.COMMAND_TOGGLEALLIANCECHAT_IGNORE);
        fme.setIgnoreAllianceChat(!ignoring);
    }
}
