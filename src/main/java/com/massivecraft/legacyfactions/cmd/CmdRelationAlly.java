package com.massivecraft.legacyfactions.cmd;

import com.massivecraft.legacyfactions.Relation;

public class CmdRelationAlly extends FCommandRelation {

    public CmdRelationAlly() {
        aliases.add("ally");
        targetRelation = Relation.ALLY;
    }
    
}
