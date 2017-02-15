package com.massivecraft.legacyfactions.cmd;

import com.massivecraft.legacyfactions.Relation;

public class CmdRelationNeutral extends FRelationCommand {

    public CmdRelationNeutral() {
        aliases.add("neutral");
        targetRelation = Relation.NEUTRAL;
    }
}
