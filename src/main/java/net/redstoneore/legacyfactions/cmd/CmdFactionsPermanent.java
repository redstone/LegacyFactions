package net.redstoneore.legacyfactions.cmd;

import net.redstoneore.legacyfactions.Factions;
import net.redstoneore.legacyfactions.Permission;
import net.redstoneore.legacyfactions.config.CommandAliases;
import net.redstoneore.legacyfactions.config.Config;
import net.redstoneore.legacyfactions.Lang;
import net.redstoneore.legacyfactions.entity.FPlayer;
import net.redstoneore.legacyfactions.entity.FPlayerColl;
import net.redstoneore.legacyfactions.entity.Faction;
import net.redstoneore.legacyfactions.flag.Flags;

public class CmdFactionsPermanent extends FCommand {

	// -------------------------------------------------- //
	// INSTANCE
	// -------------------------------------------------- //
	
	private static CmdFactionsPermanent instance = new CmdFactionsPermanent();
	public static CmdFactionsPermanent get() { return instance; }
	
	// -------------------------------------------------- //
	// CONSTRUCT
	// -------------------------------------------------- //

	private CmdFactionsPermanent() {
		this.aliases.addAll(CommandAliases.cmdAliasesPermanent);

		this.requiredArgs.add("faction tag");

		this.permission = Permission.SET_PERMANENT.getNode();
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
		if (faction.getFlag(Flags.PERMANENT)) {
			change = Lang.COMMAND_PERMANENT_REVOKE.toString();
			faction.setFlag(Flags.PERMANENT, false);
		} else {
			change = Lang.COMMAND_PERMANENT_GRANT.toString();
			faction.setFlag(Flags.PERMANENT, true);
		}

		Factions.get().log((fme == null ? "A server admin" : fme.getName()) + " " + change + " the faction \"" + faction.getTag() + "\".");

		// Inform all players
		for (FPlayer fplayer : FPlayerColl.all(true)) {
			String blame = (fme == null ? Lang.GENERIC_SERVERADMIN.toString() : fme.describeTo(fplayer, true));
			if (fplayer.getFaction() == faction) {
				fplayer.sendMessage(Lang.COMMAND_PERMANENT_YOURS, blame, change);
			} else {
				fplayer.sendMessage(Lang.COMMAND_PERMANENT_OTHER, blame, change, faction.getTag(fplayer));
			}
		}
	}
	
	@Override
	public boolean isAvailable() {
		return Config.enableFlags == false && super.isAvailable();
	}

	@Override
	public String getUsageTranslation() {
		return Lang.COMMAND_PERMANENT_DESCRIPTION.toString();
	}
}
