package net.redstoneore.legacyfactions.cmd;

import net.redstoneore.legacyfactions.Relation;
import net.redstoneore.legacyfactions.entity.CommandAliases;

public class CmdFactionsRelationTruce extends FCommandRelation {

	// -------------------------------------------------- //
	// INSTANCE
	// -------------------------------------------------- //
	
	private static CmdFactionsRelationTruce instance = new CmdFactionsRelationTruce();
	public static CmdFactionsRelationTruce get() { return instance; }
	
	// -------------------------------------------------- //
	// CONSTRUCT
	// -------------------------------------------------- //

	private CmdFactionsRelationTruce() {
		this.aliases.addAll(CommandAliases.cmdAliasesRelationTruce);
		this.targetRelation = Relation.TRUCE;
	}

}
