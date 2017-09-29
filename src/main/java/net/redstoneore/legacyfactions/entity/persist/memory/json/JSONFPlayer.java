package net.redstoneore.legacyfactions.entity.persist.memory.json;

import net.redstoneore.legacyfactions.config.Config;
import net.redstoneore.legacyfactions.entity.FPlayerColl;
import net.redstoneore.legacyfactions.entity.persist.memory.MemoryFPlayer;

public class JSONFPlayer extends MemoryFPlayer {

	
    public JSONFPlayer(MemoryFPlayer arg0) {
        super(arg0);
    }

    public JSONFPlayer(String id) {
        super(id);
    }
    
	public JSONFPlayer() {
		
	}

    @Override
    public void remove() {
        ((JSONFPlayerColl) FPlayerColl.getUnsafeInstance()).remove(getId());
    }

    public boolean shouldBeSaved() {
        if (!this.hasFaction() && (this.getPowerRounded() == this.getPowerMaxRounded() || this.getPowerRounded() == (int) Math.round(Config.powerPlayerStarting))) {
            return false;
        }
        return true;
    }
}
