package com.massivecraft.legacyfactions.cmd;

import com.massivecraft.legacyfactions.Factions;
import com.massivecraft.legacyfactions.Permission;
import com.massivecraft.legacyfactions.TL;

public class CmdLock extends FCommand {

	public CmdLock() {
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
