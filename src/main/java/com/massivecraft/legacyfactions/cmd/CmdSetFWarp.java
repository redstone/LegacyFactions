package com.massivecraft.legacyfactions.cmd;

import com.massivecraft.legacyfactions.Permission;
import com.massivecraft.legacyfactions.Relation;
import com.massivecraft.legacyfactions.TL;
import com.massivecraft.legacyfactions.entity.Conf;
import com.massivecraft.legacyfactions.entity.FPlayer;
import com.massivecraft.legacyfactions.util.LazyLocation;

public class CmdSetFWarp extends FCommand {

    public CmdSetFWarp() {
        super();
        this.aliases.add("setwarp");
        this.aliases.add("sw");
        
        this.requiredArgs.add("warp name");
        this.optionalArgs.put("password", "password");
        
        this.senderMustBeMember = true;
        this.senderMustBeModerator = true;
        this.senderMustBePlayer = true;
        this.permission = Permission.SETWARP.node;
    }

    @Override
    public void perform() {
        if (!(fme.getRelationToLocation() == Relation.MEMBER)) {
            fme.msg(TL.COMMAND_SETFWARP_NOTCLAIMED);
            return;
        }

        if (Conf.warpsMax <= myFaction.warps().size()) {
            fme.msg(TL.COMMAND_SETFWARP_LIMIT, Conf.warpsMax);
            return;
        }

        String warpPassword = argAsString(1);
        if (warpPassword != null && warpPassword.trim() != "") {
        	
        	if (!this.fme.getPlayer().hasPermission("warp.passwords")) {
        		fme.msg(TL.COMMAND_SETFWARP_NOPASSWORD);
        		return;
        	}
        	
        	warpPassword = warpPassword.toLowerCase().trim();
        }
        
        if (!transact(fme)) return;

        String warpName = argAsString(0);
        LazyLocation location = new LazyLocation(fme.getPlayer().getLocation());
        myFaction.warps().setWarp(warpName, location);
        fme.msg(TL.COMMAND_SETFWARP_SET, warpName);
    }

    private boolean transact(FPlayer player) {
        return Conf.warpCost.get("set") == 0 || player.isAdminBypassing() || payForCommand(Conf.warpCost.get("set"), TL.COMMAND_SETFWARP_TOSET.toString(), TL.COMMAND_SETFWARP_FORSET.toString());
    }

    @Override
    public TL getUsageTranslation() {
        return TL.COMMAND_SETFWARP_DESCRIPTION;
    }
}
