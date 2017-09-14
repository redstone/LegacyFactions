package net.redstoneore.legacyfactions.cmd;

import net.redstoneore.legacyfactions.Factions;
import net.redstoneore.legacyfactions.Permission;
import net.redstoneore.legacyfactions.config.CommandAliases;
import net.redstoneore.legacyfactions.Lang;

public class CmdFactionsLock extends FCommand {

	// -------------------------------------------------- //
	// INSTANCE
	// -------------------------------------------------- //
	
	private static CmdFactionsLock instance = new CmdFactionsLock();
	public static CmdFactionsLock get() { return instance; }
	
	// -------------------------------------------------- //
	// CONSTRUCT
	// -------------------------------------------------- //

	private CmdFactionsLock() {
		this.aliases.addAll(CommandAliases.cmdAliasesLock);

		this.optionalArgs.put("on/off", "flip");

		this.permission = Permission.LOCK.getNode();
		this.disableOnLock = false;

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
		Factions.get().setLocked(this.argAsBool(0, !Factions.get().isLocked()));
		sendMessage(Factions.get().isLocked() ? Lang.COMMAND_LOCK_LOCKED : Lang.COMMAND_LOCK_UNLOCKED);
	}

	@Override
	public String getUsageTranslation() {
		return Lang.COMMAND_LOCK_DESCRIPTION.toString();
	}

}
