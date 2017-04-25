package net.redstoneore.legacyfactions.cmd;

import net.redstoneore.legacyfactions.Permission;
import net.redstoneore.legacyfactions.Lang;
import net.redstoneore.legacyfactions.entity.Conf;
import net.redstoneore.legacyfactions.entity.FPlayer;
import net.redstoneore.legacyfactions.entity.FPlayerColl;

public class CmdFactionsOpen extends FCommand {

    public CmdFactionsOpen() {
        super();
        this.aliases.add("open");

        //this.requiredArgs.add("");
        this.optionalArgs.put("yes/no", "flip");

        this.permission = Permission.OPEN.node;
        this.disableOnLock = false;

        senderMustBePlayer = true;
        senderMustBeMember = false;
        senderMustBeModerator = true;
        senderMustBeAdmin = false;
    }

    @Override
    public void perform() {
        // if economy is enabled, they're not on the bypass list, and this command has a cost set, make 'em pay
        if (!payForCommand(Conf.econCostOpen, Lang.COMMAND_OPEN_TOOPEN, Lang.COMMAND_OPEN_FOROPEN)) {
            return;
        }

        myFaction.setOpen(this.argAsBool(0, !myFaction.getOpen()));

        String open = myFaction.getOpen() ? Lang.COMMAND_OPEN_OPEN.toString() : Lang.COMMAND_OPEN_CLOSED.toString();

        // Inform
        for (FPlayer fplayer : FPlayerColl.getAllOnline()) {
            if (fplayer.getFactionId().equals(myFaction.getId())) {
                fplayer.msg(Lang.COMMAND_OPEN_CHANGES, fme.getName(), open);
                continue;
            }
            fplayer.msg(Lang.COMMAND_OPEN_CHANGED, myFaction.getTag(fplayer.getFaction()), open);
        }
    }

    @Override
    public String getUsageTranslation() {
        return Lang.COMMAND_OPEN_DESCRIPTION.toString();
    }

}
