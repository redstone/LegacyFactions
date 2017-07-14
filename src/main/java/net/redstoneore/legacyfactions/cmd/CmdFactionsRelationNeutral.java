package net.redstoneore.legacyfactions.cmd;

import net.redstoneore.legacyfactions.Relation;
import net.redstoneore.legacyfactions.entity.Conf;

public class CmdFactionsRelationNeutral extends FCommandRelation {

    // -------------------------------------------------- //
    // CONSTRUCT
    // -------------------------------------------------- //

    public CmdFactionsRelationNeutral() {
        this.aliases.addAll(Conf.cmdAliasesRelationNeutral);
        this.targetRelation = Relation.NEUTRAL;
    }

}
