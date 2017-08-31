package net.redstoneore.legacyfactions.cmd;

import net.redstoneore.legacyfactions.Lang;
import net.redstoneore.legacyfactions.Permission;
import net.redstoneore.legacyfactions.entity.CommandAliases;
import net.redstoneore.legacyfactions.entity.Conf;

public class CmdFactionsFlag extends FCommand {

	// -------------------------------------------------- //
	// INSTANCE
	// -------------------------------------------------- //
	
	private static CmdFactionsFlag instance = new CmdFactionsFlag();
	public static CmdFactionsFlag get() { return instance; }
	
	// -------------------------------------------------- //
	// CONSTRUCT
	// -------------------------------------------------- //
	
	private CmdFactionsFlag() {
		this.aliases.addAll(CommandAliases.cmdAliasesFlag);
		
		this.requiredArgs.add(CommandAliases.cmdAliasesFlagList.get(0) + "|" + CommandAliases.cmdAliasesFlagSet.get(0));
		
		this.permission = Permission.FLAG.getNode();
		this.disableOnLock = true;
		
		this.senderMustBePlayer = false;
		this.senderMustBeMember = false;
		this.senderMustBeModerator = false;
		this.senderMustBeColeader = false;
		this.senderMustBeAdmin = false;
		
		this.addSubCommand(CmdFactionsFlagList.get());
		this.addSubCommand(CmdFactionsFlagSet.get());
	}

	// -------------------------------------------------- //
	// METHODS
	// -------------------------------------------------- //

	@Override
	public void perform() {
		this.commandChain.add(this);
		CmdFactionsAutohelp.get().execute(this.sender, this.args, this.commandChain);
	}

	@Override
	public boolean isAvailable() {
		return Conf.enableFlags == true;
	}
	
	@Override
	public String getUsageTranslation() {
		return Lang.COMMAND_FLAG_DESCRIPTION.getBuilder().parse().toString();
	}
	
}