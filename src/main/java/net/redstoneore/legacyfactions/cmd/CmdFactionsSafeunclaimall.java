package net.redstoneore.legacyfactions.cmd;

import org.bukkit.World;

import net.redstoneore.legacyfactions.Factions;
import net.redstoneore.legacyfactions.Permission;
import net.redstoneore.legacyfactions.config.CommandAliases;
import net.redstoneore.legacyfactions.config.Config;
import net.redstoneore.legacyfactions.entity.Board;
import net.redstoneore.legacyfactions.entity.FactionColl;
import net.redstoneore.legacyfactions.lang.Lang;

public class CmdFactionsSafeunclaimall extends FCommand {

	// -------------------------------------------------- //
	// INSTANCE
	// -------------------------------------------------- //
	
	private static CmdFactionsSafeunclaimall instance = new CmdFactionsSafeunclaimall();
	public static CmdFactionsSafeunclaimall get() { return instance; }
	
	// -------------------------------------------------- //
	// CONSTRUCT
	// -------------------------------------------------- //

	private CmdFactionsSafeunclaimall() {
		this.aliases.addAll(CommandAliases.cmdAliasesSafeunclaimall);
		
		this.optionalArgs.put("world", "all");
		
		this.permission = Permission.MANAGE_SAFE_ZONE.getNode();
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
			
			Board.get().unclaimAll(FactionColl.get().getSafeZone().getId(), world);
			
			Lang.COMMAND_SAFEUNCLAIMALL_UNCLAIMEDIN.getBuilder()
				.parse()
				.replace("<world>", world.getName())
				.sendTo(this.fme);
			
			if (Config.logLandUnclaims) {
				Factions.get().log(
					Lang.COMMAND_SAFEUNCLAIMALL_LOGWORLD.getBuilder()
						.replace("<who>", sender.getName())
						.replace("<world>", world.getName())
						.toString());
			}
		} else {
			Board.get().unclaimAll(FactionColl.get().getSafeZone().getId());
			sendMessage(Lang.COMMAND_SAFEUNCLAIMALL_UNCLAIMED);

			if (Config.logLandUnclaims) {
				Factions.get().log(Lang.COMMAND_SAFEUNCLAIMALL_LOG.getBuilder().replace("<who>", sender.getName()).toString());
			}
		}
	}

	@Override
	public String getUsageTranslation() {
		return Lang.COMMAND_SAFEUNCLAIMALL_DESCRIPTION.toString();
	}

}
