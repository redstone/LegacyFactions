package com.massivecraft.legacyfactions.cmd;

import com.massivecraft.legacyfactions.Permission;
import com.massivecraft.legacyfactions.TL;
import com.massivecraft.legacyfactions.entity.Board;
import com.massivecraft.legacyfactions.entity.Conf;
import com.massivecraft.legacyfactions.entity.FPlayerColl;
import com.massivecraft.legacyfactions.entity.FactionColl;

public class CmdSaveAll extends FCommand {

    public CmdSaveAll() {
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
        FactionColl.getInstance().forceSave(false);
        Board.getInstance().forceSave(false);
        Conf.save();
        msg(TL.COMMAND_SAVEALL_SUCCESS);
    }

    @Override
    public TL getUsageTranslation() {
        return TL.COMMAND_SAVEALL_DESCRIPTION;
    }

}