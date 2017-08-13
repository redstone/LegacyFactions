package net.redstoneore.legacyfactions.cmd;

import net.redstoneore.legacyfactions.Relation;
import net.redstoneore.legacyfactions.entity.Conf;

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
		this.aliases.addAll(Conf.cmdAliasesRelationAlly);
		this.targetRelation = Relation.ALLY;
	}
	
}
