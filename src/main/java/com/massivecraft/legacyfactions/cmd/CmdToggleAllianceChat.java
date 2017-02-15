package com.massivecraft.legacyfactions.cmd;

import com.massivecraft.legacyfactions.Permission;
import com.massivecraft.legacyfactions.TL;
import com.massivecraft.legacyfactions.entity.Conf;

public class CmdToggleAllianceChat extends FCommand {

    public CmdToggleAllianceChat() {
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
