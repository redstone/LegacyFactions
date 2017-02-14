package com.massivecraft.factions.cmd;

import com.massivecraft.factions.Permission;
import com.massivecraft.factions.TL;
import com.massivecraft.factions.entity.Board;
import com.massivecraft.factions.entity.Conf;
import com.massivecraft.factions.entity.FPlayerColl;
import com.massivecraft.factions.entity.FactionColl;

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
        FPlayerColl.getInstance().forceSave(false);
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