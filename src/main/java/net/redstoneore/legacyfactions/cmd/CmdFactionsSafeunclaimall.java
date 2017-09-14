package net.redstoneore.legacyfactions.cmd;

import net.redstoneore.legacyfactions.Factions;
import net.redstoneore.legacyfactions.Permission;
import net.redstoneore.legacyfactions.config.CommandAliases;
import net.redstoneore.legacyfactions.config.Config;
import net.redstoneore.legacyfactions.Lang;
import net.redstoneore.legacyfactions.entity.Board;
import net.redstoneore.legacyfactions.entity.FactionColl;

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
		Board.get().unclaimAll(FactionColl.get().getSafeZone().getId());
		sendMessage(Lang.COMMAND_SAFEUNCLAIMALL_UNCLAIMED);

		if (Config.logLandUnclaims) {
			Factions.get().log(Lang.COMMAND_SAFEUNCLAIMALL_UNCLAIMEDLOG.format(sender.getName()));
		}
	}

	@Override
	public String getUsageTranslation() {
		return Lang.COMMAND_SAFEUNCLAIMALL_DESCRIPTION.toString();
	}

}
