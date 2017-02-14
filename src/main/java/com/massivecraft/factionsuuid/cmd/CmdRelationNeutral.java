package com.massivecraft.factionsuuid.cmd;

import com.massivecraft.factionsuuid.Relation;

public class CmdRelationNeutral extends FRelationCommand {

    public CmdRelationNeutral() {
        aliases.add("neutral");
        targetRelation = Relation.NEUTRAL;
    }
}
