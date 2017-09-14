package net.redstoneore.legacyfactions.cmd;

import net.redstoneore.legacyfactions.Relation;
import net.redstoneore.legacyfactions.config.CommandAliases;

public class CmdFactionsRelationAlly extends FCommandRelation {
	
	// -------------------------------------------------- //
	// INSTANCE
	// -------------------------------------------------- //
	
	private static CmdFactionsRelationAlly instance = new CmdFactionsRelationAlly();
	public static CmdFactionsRelationAlly get() { return instance; }
	
	// -------------------------------------------------- //
	// CONSTRUCT
	// -------------------------------------------------- //

	private CmdFactionsRelationAlly() {
		this.aliases.addAll(CommandAliases.cmdAliasesRelationAlly);
		this.targetRelation = Relation.ALLY;
	}
	
}
