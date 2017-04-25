package net.redstoneore.legacyfactions.cmd;

import net.redstoneore.legacyfactions.Factions;
import net.redstoneore.legacyfactions.Permission;
import net.redstoneore.legacyfactions.Lang;

public class CmdFactionsLock extends FCommand {

	public CmdFactionsLock() {
		super();
		this.aliases.add("lock");

		this.optionalArgs.put("on/off", "flip");

		this.permission = Permission.LOCK.node;
		this.disableOnLock = false;

		senderMustBePlayer = false;
		senderMustBeMember = false;
		senderMustBeModerator = false;
		senderMustBeAdmin = false;
	}

	@Override
	public void perform() {
		Factions.get().setLocked(this.argAsBool(0, !Factions.get().isLocked()));
		msg(Factions.get().isLocked() ? Lang.COMMAND_LOCK_LOCKED : Lang.COMMAND_LOCK_UNLOCKED);
	}

	@Override
	public String getUsageTranslation() {
		return Lang.COMMAND_LOCK_DESCRIPTION.toString();
	}

}
