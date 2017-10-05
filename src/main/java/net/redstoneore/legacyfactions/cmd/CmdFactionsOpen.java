package net.redstoneore.legacyfactions.cmd;

import net.redstoneore.legacyfactions.Permission;
import net.redstoneore.legacyfactions.config.CommandAliases;
import net.redstoneore.legacyfactions.config.Config;
import net.redstoneore.legacyfactions.entity.FPlayerColl;
import net.redstoneore.legacyfactions.flag.Flags;
import net.redstoneore.legacyfactions.lang.Lang;

public class CmdFactionsOpen extends FCommand {
	
	// -------------------------------------------------- //
	// INSTANCE
	// -------------------------------------------------- //
	
	private static CmdFactionsOpen instance = new CmdFactionsOpen();
	public static CmdFactionsOpen get() { return instance; }
	
	// -------------------------------------------------- //
	// CONSTRUCT
	// -------------------------------------------------- //

	private CmdFactionsOpen() {
		this.aliases.addAll(CommandAliases.cmdAliasesOpen);

		this.optionalArgs.put("yes/no", "flip");

		this.permission = Permission.SET_OPEN.getNode();
		this.disableOnLock = false;

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
		// if economy is enabled, they're not on the bypass list, and this command has a cost set, make 'em pay
		if (!payForCommand(Config.econCostOpen, Lang.COMMAND_OPEN_TOOPEN, Lang.COMMAND_OPEN_FOROPEN)) {
			return;
		}

		this.myFaction.setFlag(Flags.OPEN, this.argAsBool(0, !this.myFaction.getFlag(Flags.OPEN)));

		String open = this.myFaction.getFlag(Flags.OPEN) ? Lang.COMMAND_OPEN_OPEN.toString() : Lang.COMMAND_OPEN_CLOSED.toString();

		// Inform
		FPlayerColl.all(true, fplayer -> {
			if (fplayer.getFactionId().equals(myFaction.getId())) {
				fplayer.sendMessage(Lang.COMMAND_OPEN_CHANGES, fme.getName(), open);
			} else {
				fplayer.sendMessage(Lang.COMMAND_OPEN_CHANGED, myFaction.getTag(fplayer.getFaction()), open);
			}
		});
	}
	
	@Override
	public boolean isAvailable() {
		return Config.enableFlags == false && super.isAvailable();
	}

	@Override
	public String getUsageTranslation() {
		return Lang.COMMAND_OPEN_DESCRIPTION.toString();
	}

}
