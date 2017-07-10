package net.redstoneore.legacyfactions.cmd;

import net.redstoneore.legacyfactions.Relation;
import net.redstoneore.legacyfactions.entity.Conf;

public class CmdFactionsRelationTruce extends FCommandRelation {

    public CmdFactionsRelationTruce() {
        this.aliases.addAll(Conf.cmdAliasesRelationTruce);
        this.targetRelation = Relation.TRUCE;
    }

}
