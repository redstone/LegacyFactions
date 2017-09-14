package net.redstoneore.legacyfactions.cmd;

import net.redstoneore.legacyfactions.Relation;
import net.redstoneore.legacyfactions.config.CommandAliases;

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
		this.aliases.addAll(CommandAliases.cmdAliasesRelationEnemy);
		this.targetRelation = Relation.ENEMY;
	}
	
}
