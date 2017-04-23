package net.redstoneore.legacyfactions.cmd;

import net.redstoneore.legacyfactions.Relation;

public class CmdFactionsRelationNeutral extends FCommandRelation {

    public CmdFactionsRelationNeutral() {
        aliases.add("neutral");
        targetRelation = Relation.NEUTRAL;
    }
}
