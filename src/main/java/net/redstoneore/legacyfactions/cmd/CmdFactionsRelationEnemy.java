package net.redstoneore.legacyfactions.cmd;

import net.redstoneore.legacyfactions.Relation;
import net.redstoneore.legacyfactions.entity.Conf;

public class CmdFactionsRelationEnemy extends FCommandRelation {
	
	// -------------------------------------------------- //
	// INSTANCE
	// -------------------------------------------------- //
	
	private static CmdFactionsRelationEnemy instance = new CmdFactionsRelationEnemy();
	public static CmdFactionsRelationEnemy get() { return instance; }
	
	// -------------------------------------------------- //
	// CONSTRUCT
	// -------------------------------------------------- //

	private CmdFactionsRelationEnemy() {
		this.aliases.addAll(Conf.cmdAliasesRelationEnemy);
		this.targetRelation = Relation.ENEMY;
	}
	
}
