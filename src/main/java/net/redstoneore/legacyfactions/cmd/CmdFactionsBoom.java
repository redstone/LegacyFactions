package net.redstoneore.legacyfactions.cmd;

import net.redstoneore.legacyfactions.Permission;
import net.redstoneore.legacyfactions.config.CommandAliases;
import net.redstoneore.legacyfactions.config.Config;
import net.redstoneore.legacyfactions.flag.Flags;
import net.redstoneore.legacyfactions.lang.Lang;

public class CmdFactionsBoom extends FCommand {

	// -------------------------------------------------- //
	// INSTANCE
	// -------------------------------------------------- //
	
	private static CmdFactionsBoom instance = new CmdFactionsBoom();
	public static CmdFactionsBoom get() { return instance; }
	
	// -------------------------------------------------- //
	// CONSTRUCT
	// -------------------------------------------------- //

	private CmdFactionsBoom() {
		this.aliases.addAll(CommandAliases.cmdAliasesBoom);

		this.optionalArgs.put("on/off", "flip");

		this.permission = Permission.SET_EXPLOSIONS.getNode();
		this.disableOnLock = true;

		this.senderMustBePlayer = true;
		this.senderMustBeMember = false;
		this.senderMustBeModerator = true;
		this.senderMustBeColeader = false;
		this.senderMustBeAdmin = false;
	}

	// -------------------------------------------------- //
	// METHODS
	// -------------------------------------------------- //

	@Override
	public void perform() {
		if (!myFaction.getFlag(Flags.PEACEFUL)) {
			this.fme.sendMessage(Lang.COMMAND_BOOM_PEACEFULONLY);
			return;
		}

		// if economy is enabled, they're not on the bypass list, and this command has a cost set, make 'em pay
		if (!payForCommand(Config.econCostNoBoom, Lang.COMMAND_BOOM_TOTOGGLE, Lang.COMMAND_BOOM_FORTOGGLE)) {
			return;
		}

		this.myFaction.setFlag(Flags.EXPLOSIONS, this.argAsBool(0, !myFaction.getFlag(Flags.EXPLOSIONS)));

		String enabled = myFaction.getFlag(Flags.EXPLOSIONS) ? Lang.GENERIC_DISABLED.toString() : Lang.GENERIC_ENABLED.toString();

		// Inform
		this.myFaction.sendMessage(Lang.COMMAND_BOOM_ENABLED, fme.describeTo(myFaction), enabled);
	}

	@Override
	public boolean isAvailable() {
		return Config.enableFlags == false && super.isAvailable();
	}
	
	@Override
	public String getUsageTranslation() {
		return Lang.COMMAND_BOOM_DESCRIPTION.toString();
	}
	
}
