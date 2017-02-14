package com.massivecraft.factionsuuid.cmd;

import com.massivecraft.factionsuuid.Relation;

public class CmdRelationEnemy extends FRelationCommand {

    public CmdRelationEnemy() {
        aliases.add("enemy");
        targetRelation = Relation.ENEMY;
    }
}
