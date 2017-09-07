package net.redstoneore.legacyfactions.entity.persist.shared;

import net.redstoneore.legacyfactions.entity.Faction;
import net.redstoneore.legacyfactions.entity.FactionColl;

public abstract class SharedFactionColl extends FactionColl {

	@Override
    public boolean isTagTaken(String str) {
        return this.getByTag(str) != null;
    }

	// -------------------------------------------------- //
	// ABSTRACT METHODS
	// -------------------------------------------------- //
	
    public abstract Faction generateFactionObject(String string);

}
