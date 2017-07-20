package net.redstoneore.legacyfactions.cmd;

import net.redstoneore.legacyfactions.Relation;
import net.redstoneore.legacyfactions.entity.Conf;

public class CmdFactionsRelationEnemy extends FCommandRelation {

	// -------------------------------------------------- //
	// CONSTRUCT
	// -------------------------------------------------- //

	public CmdFactionsRelationEnemy() {
		this.aliases.addAll(Conf.cmdAliasesRelationEnemy);
		this.targetRelation = Relation.ENEMY;
	}
	
}
