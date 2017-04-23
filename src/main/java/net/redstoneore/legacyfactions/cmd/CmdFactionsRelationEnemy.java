package net.redstoneore.legacyfactions.cmd;

import net.redstoneore.legacyfactions.Relation;

public class CmdFactionsRelationEnemy extends FCommandRelation {

    public CmdFactionsRelationEnemy() {
        aliases.add("enemy");
        targetRelation = Relation.ENEMY;
    }
}
