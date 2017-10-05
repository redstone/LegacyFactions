package net.redstoneore.legacyfactions.cmd;

import org.bukkit.World;

import net.redstoneore.legacyfactions.Factions;
import net.redstoneore.legacyfactions.Permission;
import net.redstoneore.legacyfactions.config.CommandAliases;
import net.redstoneore.legacyfactions.config.Config;
import net.redstoneore.legacyfactions.entity.Board;
import net.redstoneore.legacyfactions.entity.FactionColl;
import net.redstoneore.legacyfactions.lang.Lang;

public class CmdFactionsWarunclaimall extends FCommand {

	// -------------------------------------------------- //
	// INSTANCE
	// -------------------------------------------------- //
	
	private static CmdFactionsWarunclaimall instance = new CmdFactionsWarunclaimall();
	public static CmdFactionsWarunclaimall get() { return instance; }
	
	// -------------------------------------------------- //
	// CONSTRUCT
	// -------------------------------------------------- //

	private CmdFactionsWarunclaimall() {
		this.aliases.addAll(CommandAliases.cmdAliasesWarunclaimall);
		
		this.optionalArgs.put("world", "all");
		
		this.permission = Permission.MANAGE_WAR_ZONE.getNode();
		this.disableOnLock = true;

		this.senderMustBePlayer = false;
		this.senderMustBeMember = false;
		this.senderMustBeModerator = false;
		this.senderMustBeColeader = false;
		this.senderMustBeAdmin = false;
	}

	// -------------------------------------------------- //
	// METHODS
	// -------------------------------------------------- //

	@Override
	public void perform() {
		if (this.argIsSet(0)) {
			World world = this.argAsWorld(0);
			if (world == null) return;
			
			Board.get().unclaimAll(FactionColl.get().getWarZone().getId(), world);
			
			Lang.COMMAND_WARUNCLAIMALL_UNCLAIMEDIN.getBuilder()
				.parse()
				.replace("<world>", world.getName())
				.sendTo(this.fme);
			
			if (Config.logLandUnclaims) {
				Factions.get().log(
					Lang.COMMAND_WARUNCLAIMALL_LOGWORLD.getBuilder()
						.parse()
						.replace("<who>", this.sender.getName())
						.replace("<world>", world.getName())
						.toString());
			}

		} else {
			Board.get().unclaimAll(FactionColl.get().getWarZone().getId());
			
			this.sendMessage(Lang.COMMAND_WARUNCLAIMALL_SUCCESS);
			if (Config.logLandUnclaims) {
				Factions.get().log(
					Lang.COMMAND_WARUNCLAIMALL_LOGALL.getBuilder()
						.parse()
						.replace("<who>", this.sender.getName())
						.toString());
			}
		}

	}

	@Override
	public String getUsageTranslation() {
		return Lang.COMMAND_WARUNCLAIMALL_DESCRIPTION.toString();
	}

}
