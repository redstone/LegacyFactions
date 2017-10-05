package net.redstoneore.legacyfactions.cmd;

import net.redstoneore.legacyfactions.Permission;
import net.redstoneore.legacyfactions.Relation;
import net.redstoneore.legacyfactions.config.CommandAliases;
import net.redstoneore.legacyfactions.config.Config;
import net.redstoneore.legacyfactions.event.EventFactionsWarpCreate;
import net.redstoneore.legacyfactions.lang.Lang;
import net.redstoneore.legacyfactions.util.LazyLocation;

public class CmdFactionsSetwarp extends FCommand {

	// -------------------------------------------------- //
	// INSTANCE
	// -------------------------------------------------- //
	
	private static CmdFactionsSetwarp instance = new CmdFactionsSetwarp();
	public static CmdFactionsSetwarp get() { return instance; }
	
	// -------------------------------------------------- //
	// CONSTRUCT
	// -------------------------------------------------- //

	private CmdFactionsSetwarp() {
		this.aliases.addAll(CommandAliases.cmdAliasesSetwarp);
		
		this.requiredArgs.add("warp name");
		this.optionalArgs.put("password", "password");
		
		this.senderMustBeMember = true;
		this.senderMustBeModerator = true;
		this.senderMustBePlayer = true;
		this.permission = Permission.SETWARP.getNode();
	}

	// -------------------------------------------------- //
	// METHODS
	// -------------------------------------------------- //

	@Override
	public void perform() {
		// Make sure they can set here
		if (!(fme.getRelationToLocation() == Relation.MEMBER)) {
			fme.sendMessage(Lang.COMMAND_SETFWARP_NOTCLAIMED);
			return;
		}
		
		// Check the limit 
		if (Config.warpsMax <= myFaction.warps().size()) {
			fme.sendMessage(Lang.COMMAND_SETFWARP_LIMIT, Config.warpsMax);
			return;
		}

		// Determine other information for warp 
		String warpName = argAsString(0);
		LazyLocation warpLocation = new LazyLocation(fme.getPlayer().getLocation());
		Double warpCost = Config.warpCost.get("set");
		
		// Check for password
		String warpPassword = argAsString(1);
		if (warpPassword != null && warpPassword.trim() != "") {
			
			if (!Permission.WARPPASSWORD.has(this.me)) {
				fme.sendMessage(Lang.COMMAND_SETFWARP_NOPASSWORD);
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
		if (warpCost > 0 && !this.fme.isAdminBypassing() && !payForCommand(Config.warpCost.get("set"), Lang.COMMAND_SETFWARP_TOSET.toString(), Lang.COMMAND_SETFWARP_FORSET.toString())) return;
		
		// Get new values from event
		warpName = event.getName();
		warpPassword = event.getPassword();
		warpLocation = event.getLocation();
		
		// Set the warp
		myFaction.warps().setWarp(warpName, warpLocation, warpPassword);
		fme.sendMessage(Lang.COMMAND_SETFWARP_SET, warpName);
	}

	@Override
	public String getUsageTranslation() {
		return Lang.COMMAND_SETFWARP_DESCRIPTION.toString();
	}
	
}
