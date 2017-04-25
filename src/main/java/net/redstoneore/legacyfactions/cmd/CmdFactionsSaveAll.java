package net.redstoneore.legacyfactions.cmd;

import net.redstoneore.legacyfactions.Permission;
import net.redstoneore.legacyfactions.Lang;
import net.redstoneore.legacyfactions.entity.Board;
import net.redstoneore.legacyfactions.entity.Conf;
import net.redstoneore.legacyfactions.entity.FPlayerColl;
import net.redstoneore.legacyfactions.entity.FactionColl;

public class CmdFactionsSaveAll extends FCommand {

    public CmdFactionsSaveAll() {
        super();
        this.aliases.add("saveall");
        this.aliases.add("save");

        //this.requiredArgs.add("");
        //this.optionalArgs.put("", "");

        this.permission = Permission.SAVE.node;
        this.disableOnLock = false;

        senderMustBePlayer = false;
        senderMustBeMember = false;
        senderMustBeModerator = false;
        senderMustBeAdmin = false;
    }

    @Override
    public void perform() {
        FPlayerColl.save(false);
        FactionColl.get().forceSave(false);
        Board.get().forceSave(false);
        Conf.save();
        msg(Lang.COMMAND_SAVEALL_SUCCESS);
    }

    @Override
    public String getUsageTranslation() {
        return Lang.COMMAND_SAVEALL_DESCRIPTION.toString();
    }

}