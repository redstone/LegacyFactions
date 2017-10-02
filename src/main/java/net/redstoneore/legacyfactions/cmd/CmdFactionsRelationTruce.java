package net.redstoneore.legacyfactions.cmd;

import net.redstoneore.legacyfactions.Relation;
import net.redstoneore.legacyfactions.config.CommandAliases;
import net.redstoneore.legacyfactions.config.Config;

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

	// -------------------------------------------------- //
	// METHODS
	// -------------------------------------------------- //
	
	@Override
	public boolean isAvailable() {
		return Config.enableTruces == true && super.isAvailable();
	}
	
}
