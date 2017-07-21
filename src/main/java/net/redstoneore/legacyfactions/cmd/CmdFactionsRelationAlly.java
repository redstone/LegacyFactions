package net.redstoneore.legacyfactions.cmd;

import net.redstoneore.legacyfactions.Relation;
import net.redstoneore.legacyfactions.entity.Conf;

public class CmdFactionsRelationAlly extends FCommandRelation {

	// -------------------------------------------------- //
	// CONSTRUCT
	// -------------------------------------------------- //

	public CmdFactionsRelationAlly() {
		this.aliases.addAll(Conf.cmdAliasesRelationAlly);
		this.targetRelation = Relation.ALLY;
	}
	
}
