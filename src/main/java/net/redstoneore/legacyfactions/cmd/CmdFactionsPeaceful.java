package net.redstoneore.legacyfactions.cmd;

import net.redstoneore.legacyfactions.Permission;
import net.redstoneore.legacyfactions.config.CommandAliases;
import net.redstoneore.legacyfactions.config.Config;
import net.redstoneore.legacyfactions.entity.FPlayerColl;
import net.redstoneore.legacyfactions.entity.Faction;
import net.redstoneore.legacyfactions.flag.Flags;
import net.redstoneore.legacyfactions.lang.Lang;

public class CmdFactionsPeaceful extends FCommand {

	// -------------------------------------------------- //
	// INSTANCE
	// -------------------------------------------------- //
	
	private static CmdFactionsPeaceful instance = new CmdFactionsPeaceful();
	public static CmdFactionsPeaceful get() { return instance; }
	
	// -------------------------------------------------- //
	// CONSTRUCT
	// -------------------------------------------------- //

	private CmdFactionsPeaceful() {
		this.aliases.addAll(CommandAliases.cmdAliasesPeaceful);

		this.requiredArgs.add("faction tag");
			
		this.permission = Permission.SET_PEACEFUL.getNode();
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
		Faction faction = this.argAsFaction(0);
		if (faction == null) {
			return;
		}

		String change;
		if (faction.getFlag(Flags.PEACEFUL)) {
			change = Lang.COMMAND_PEACEFUL_REVOKE.toString();
			faction.setFlag(Flags.PEACEFUL, false);
		} else {
			change = Lang.COMMAND_PEACEFUL_GRANT.toString();
			faction.setFlag(Flags.PEACEFUL, true);
		}

		// Inform all players
		FPlayerColl.all(true).forEach(fplayer -> {
			String blame = (this.fme == null ? Lang.GENERIC_SERVERADMIN.toString() : this.fme.describeTo(fplayer, true));
			if (fplayer.getFaction() == faction) {
				fplayer.sendMessage(Lang.COMMAND_PEACEFUL_YOURS, blame, change);
			} else {
				fplayer.sendMessage(Lang.COMMAND_PEACEFUL_OTHER, blame, change, faction.getTag(fplayer));
			}
		});
	}
	
	@Override
	public boolean isAvailable() {
		return Config.enableFlags == false && super.isAvailable();
	}

	@Override
	public String getUsageTranslation() {
		return Lang.COMMAND_PEACEFUL_DESCRIPTION.toString();
	}

}
