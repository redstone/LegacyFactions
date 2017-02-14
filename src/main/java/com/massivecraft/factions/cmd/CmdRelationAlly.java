package com.massivecraft.factions.cmd;

import com.massivecraft.factions.Relation;

public class CmdRelationAlly extends FRelationCommand {

    public CmdRelationAlly() {
        aliases.add("ally");
        targetRelation = Relation.ALLY;
    }
}
