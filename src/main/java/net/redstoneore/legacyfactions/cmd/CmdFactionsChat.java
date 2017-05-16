package net.redstoneore.legacyfactions.cmd;

import net.redstoneore.legacyfactions.ChatMode;
import net.redstoneore.legacyfactions.Permission;
import net.redstoneore.legacyfactions.Lang;
import net.redstoneore.legacyfactions.entity.Conf;
import net.redstoneore.legacyfactions.event.EventFactionsChatModeChange;

public class CmdFactionsChat extends FCommand {

    public CmdFactionsChat() {
        this.aliases.addAll(Conf.cmdAliasesChat);

        this.optionalArgs.put("mode", "next");

        this.permission = Permission.CHAT.node;
        this.disableOnLock = false;

        senderMustBePlayer = true;
        senderMustBeMember = true;
        senderMustBeModerator = false;
        senderMustBeAdmin = false;
    }

    @Override
    public void perform() {
        if (!Conf.factionOnlyChat) {
            msg(Lang.COMMAND_CHAT_DISABLED.toString());
            return;
        }

        String modeString = this.argAsString(0);
        ChatMode modeTarget = fme.getChatMode().getNext();

        if (modeString != null) {
            modeString = modeString.toLowerCase();
            if (modeString.startsWith("p")) {
                modeTarget = ChatMode.PUBLIC;
            } else if (modeString.startsWith("a")) {
                modeTarget = ChatMode.ALLIANCE;
            } else if (modeString.startsWith("f")) {
                modeTarget = ChatMode.FACTION;
            } else if (modeString.startsWith("t")) {
                modeTarget = ChatMode.TRUCE;
            } else {
                msg(Lang.COMMAND_CHAT_INVALIDMODE);
                return;
            }
        }
        
        EventFactionsChatModeChange event = new EventFactionsChatModeChange(this.myFaction, this.fme, modeTarget);
        event.call();
        if (event.isCancelled()) return;
        
        fme.setChatMode(modeTarget);

        if (!event.isSilent()) {
	        if (fme.getChatMode() == ChatMode.PUBLIC) {
	            msg(Lang.COMMAND_CHAT_MODE_PUBLIC);
	        } else if (fme.getChatMode() == ChatMode.ALLIANCE) {
	            msg(Lang.COMMAND_CHAT_MODE_ALLIANCE);
	        } else if (fme.getChatMode() == ChatMode.TRUCE) {
	            msg(Lang.COMMAND_CHAT_MODE_TRUCE);
	        } else {
	            msg(Lang.COMMAND_CHAT_MODE_FACTION);
	        }
        }
    }

    @Override
    public String getUsageTranslation() {
        return Lang.COMMAND_CHAT_DESCRIPTION.toString();
    }
}
