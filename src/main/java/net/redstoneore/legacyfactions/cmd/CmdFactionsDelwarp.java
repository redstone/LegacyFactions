package net.redstoneore.legacyfactions.cmd;

import java.util.Optional;

import net.redstoneore.legacyfactions.Permission;
import net.redstoneore.legacyfactions.config.CommandAliases;
import net.redstoneore.legacyfactions.config.Config;
import net.redstoneore.legacyfactions.event.EventFactionsWarpDelete;
import net.redstoneore.legacyfactions.lang.Lang;
import net.redstoneore.legacyfactions.warp.FactionWarp;

public class CmdFactionsDelwarp extends FCommand {

	// -------------------------------------------------- //
	// INSTANCE
	// -------------------------------------------------- //
	
	private static CmdFactionsDelwarp instance = new CmdFactionsDelwarp();
	public static CmdFactionsDelwarp get() { return instance; }
	
	// -------------------------------------------------- //
	// CONSTRUCT
	// -------------------------------------------------- //

	private CmdFactionsDelwarp() {
		this.aliases.addAll(CommandAliases.cmdAliasesDelwarp);
		
		this.requiredArgs.add("warp name");

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
		String name = this.argAsString(0);
		Optional<FactionWarp> owarp = this.myFaction.warps().get(name);
		
		if (owarp.isPresent()) {
			FactionWarp warp = owarp.get();
			Double cost = Config.warpCost.get("delete");
			
			if (fme.isAdminBypassing()) cost = 0.0;
			
			// call event
			EventFactionsWarpDelete event = new EventFactionsWarpDelete(myFaction, fme, warp, cost);
			event.call();
			if (event.isCancelled()) return;
			
			if (cost > 0 && !fme.isAdminBypassing() && !this.payForCommand(Config.warpCost.get("delete"), Lang.COMMAND_DELFWARP_TODELETE.toString(), Lang.COMMAND_DELFWARP_FORDELETE.toString())) return;
			
			warp.delete();
			fme.sendMessage(Lang.COMMAND_DELFWARP_DELETED, name);
		} else {
			fme.sendMessage(Lang.COMMAND_DELFWARP_INVALID, name);
		}
	}

	@Override
	public String getUsageTranslation() {
		return Lang.COMMAND_DELFWARP_DESCRIPTION.toString();
	}
	
}
