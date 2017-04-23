package com.massivecraft.legacyfactions.cmd;

import com.massivecraft.legacyfactions.Relation;

public class CmdFactionsRelationAlly extends FCommandRelation {

    public CmdFactionsRelationAlly() {
        aliases.add("ally");
        targetRelation = Relation.ALLY;
    }
    
}
