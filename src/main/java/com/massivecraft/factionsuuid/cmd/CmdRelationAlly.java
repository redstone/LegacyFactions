package com.massivecraft.factionsuuid.cmd;

import com.massivecraft.factionsuuid.Relation;

public class CmdRelationAlly extends FRelationCommand {

    public CmdRelationAlly() {
        aliases.add("ally");
        targetRelation = Relation.ALLY;
    }
}
