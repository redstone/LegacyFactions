package net.redstoneore.legacyfactions.cmd;

import net.redstoneore.legacyfactions.Permission;
import net.redstoneore.legacyfactions.Relation;
import net.redstoneore.legacyfactions.Lang;
import net.redstoneore.legacyfactions.entity.Conf;
import net.redstoneore.legacyfactions.event.EventFactionsWarpCreate;
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
    	// Make sure they can set here
        if (!(fme.getRelationToLocation() == Relation.MEMBER)) {
            fme.msg(Lang.COMMAND_SETFWARP_NOTCLAIMED);
            return;
        }
        
        // Check the limit 
        if (Conf.warpsMax <= myFaction.warps().size()) {
            fme.msg(Lang.COMMAND_SETFWARP_LIMIT, Conf.warpsMax);
            return;
        }

        // Determine other information for warp 
        String warpName = argAsString(0);
        LazyLocation warpLocation = new LazyLocation(fme.getPlayer().getLocation());
        Double warpCost = Conf.warpCost.get("set");
        
        // Check for password
        String warpPassword = argAsString(1);
        if (warpPassword != null && warpPassword.trim() != "") {
        	
        	if (!this.fme.getPlayer().hasPermission("factions.warp.passwords")) {
        		fme.msg(Lang.COMMAND_SETFWARP_NOPASSWORD);
        		return;
        	}
        	
        	// Passwords are not case sensitive
        	warpPassword = warpPassword.toLowerCase().trim();
        }
        
        // If in admin bypass, charge nothing
        if (this.fme.isAdminBypassing()) warpCost = 0.0;
        
        // Call our event
        EventFactionsWarpCreate event = new EventFactionsWarpCreate(this.myFaction, this.fme, warpName, warpPassword, warpLocation, warpCost);
        event.call();
        if (event.isCancelled()) return;
        
        // Check for new cost, and pay for it if required
        if (warpCost > 0 && !this.fme.isAdminBypassing() && !payForCommand(Conf.warpCost.get("set"), Lang.COMMAND_SETFWARP_TOSET.toString(), Lang.COMMAND_SETFWARP_FORSET.toString())) return;
        
        // Get new values from event
        warpName = event.getName();
        warpPassword = event.getPassword();
        warpLocation = event.getLocation();
        
        // Set the warp
        myFaction.warps().setWarp(warpName, warpLocation, warpPassword);
        fme.msg(Lang.COMMAND_SETFWARP_SET, warpName);
    }

    @Override
    public String getUsageTranslation() {
        return Lang.COMMAND_SETFWARP_DESCRIPTION.toString();
    }
}
