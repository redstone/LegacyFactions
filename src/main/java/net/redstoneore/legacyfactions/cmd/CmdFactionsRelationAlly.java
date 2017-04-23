package net.redstoneore.legacyfactions.cmd;

import net.redstoneore.legacyfactions.Relation;

public class CmdFactionsRelationAlly extends FCommandRelation {

    public CmdFactionsRelationAlly() {
        aliases.add("ally");
        targetRelation = Relation.ALLY;
    }
    
}
