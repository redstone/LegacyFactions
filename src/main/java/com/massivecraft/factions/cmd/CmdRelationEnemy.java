package com.massivecraft.factions.cmd;

import com.massivecraft.factions.Relation;

public class CmdRelationEnemy extends FRelationCommand {

    public CmdRelationEnemy() {
        aliases.add("enemy");
        targetRelation = Relation.ENEMY;
    }
}
