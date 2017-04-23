package com.massivecraft.legacyfactions.entity.persist.json;

import com.massivecraft.legacyfactions.entity.Conf;
import com.massivecraft.legacyfactions.entity.FPlayerColl;
import com.massivecraft.legacyfactions.entity.persist.memory.MemoryFPlayer;

public class JSONFPlayer extends MemoryFPlayer {

    public JSONFPlayer(MemoryFPlayer arg0) {
        super(arg0);
    }

    public JSONFPlayer(String id) {
        super(id);
    }

    @Override
    public void remove() {
        ((JSONFPlayers) FPlayerColl.getUnsafeInstance()).fPlayers.remove(getId());
    }

    public boolean shouldBeSaved() {
        if (!this.hasFaction() && (this.getPowerRounded() == this.getPowerMaxRounded() || this.getPowerRounded() == (int) Math.round(Conf.powerPlayerStarting))) {
            return false;
        }
        return true;
    }
}
