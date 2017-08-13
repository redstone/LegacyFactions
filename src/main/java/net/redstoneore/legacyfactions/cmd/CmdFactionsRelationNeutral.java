package net.redstoneore.legacyfactions.cmd;

import net.redstoneore.legacyfactions.Relation;
import net.redstoneore.legacyfactions.entity.Conf;

public class CmdFactionsRelationNeutral extends FCommandRelation {
	
	// -------------------------------------------------- //
	// INSTANCE
	// -------------------------------------------------- //
	
	private static CmdFactionsRelationNeutral instance = new CmdFactionsRelationNeutral();
	public static CmdFactionsRelationNeutral get() { return instance; }
	
	// -------------------------------------------------- //
	// CONSTRUCT
	// -------------------------------------------------- //

	private CmdFactionsRelationNeutral() {
		this.aliases.addAll(Conf.cmdAliasesRelationNeutral);
		this.targetRelation = Relation.NEUTRAL;
	}

}
