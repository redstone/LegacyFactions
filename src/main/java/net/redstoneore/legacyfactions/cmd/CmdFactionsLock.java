package net.redstoneore.legacyfactions.cmd;

import net.redstoneore.legacyfactions.Factions;
import net.redstoneore.legacyfactions.Permission;
import net.redstoneore.legacyfactions.TL;

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
		msg(Factions.get().isLocked() ? TL.COMMAND_LOCK_LOCKED : TL.COMMAND_LOCK_UNLOCKED);
	}

	@Override
	public TL getUsageTranslation() {
		return TL.COMMAND_LOCK_DESCRIPTION;
	}

}
