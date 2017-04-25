package net.redstoneore.legacyfactions.cmd;

import net.redstoneore.legacyfactions.Permission;
import net.redstoneore.legacyfactions.Relation;
import net.redstoneore.legacyfactions.Lang;
import net.redstoneore.legacyfactions.entity.Conf;
import net.redstoneore.legacyfactions.entity.FPlayer;
import net.redstoneore.legacyfactions.util.LazyLocation;

public class CmdFactionsSetwarp extends FCommand {

    public CmdFactionsSetwarp() {
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
            fme.msg(Lang.COMMAND_SETFWARP_NOTCLAIMED);
            return;
        }

        if (Conf.warpsMax <= myFaction.warps().size()) {
            fme.msg(Lang.COMMAND_SETFWARP_LIMIT, Conf.warpsMax);
            return;
        }

        String warpPassword = argAsString(1);
        if (warpPassword != null && warpPassword.trim() != "") {
        	
        	if (!this.fme.getPlayer().hasPermission("warp.passwords")) {
        		fme.msg(Lang.COMMAND_SETFWARP_NOPASSWORD);
        		return;
        	}
        	
        	warpPassword = warpPassword.toLowerCase().trim();
        }
        
        if (!transact(fme)) return;

        String warpName = argAsString(0);
        LazyLocation location = new LazyLocation(fme.getPlayer().getLocation());
        myFaction.warps().setWarp(warpName, location, warpPassword);
        fme.msg(Lang.COMMAND_SETFWARP_SET, warpName);
    }

    private boolean transact(FPlayer player) {
        return Conf.warpCost.get("set") == 0 || player.isAdminBypassing() || payForCommand(Conf.warpCost.get("set"), Lang.COMMAND_SETFWARP_TOSET.toString(), Lang.COMMAND_SETFWARP_FORSET.toString());
    }

    @Override
    public String getUsageTranslation() {
        return Lang.COMMAND_SETFWARP_DESCRIPTION.toString();
    }
}
