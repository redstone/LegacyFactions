package com.massivecraft.factions.zcore.persist.json;

import com.massivecraft.factions.entity.Conf;
import com.massivecraft.factions.entity.FPlayerColl;
import com.massivecraft.factions.zcore.persist.MemoryFPlayer;

public class JSONFPlayer extends MemoryFPlayer {

    public JSONFPlayer(MemoryFPlayer arg0) {
        super(arg0);
    }

    public JSONFPlayer(String id) {
        super(id);
    }

    @Override
    public void remove() {
        ((JSONFPlayers) FPlayerColl.getInstance()).fPlayers.remove(getId());
    }

    public boolean shouldBeSaved() {
        if (!this.hasFaction() && (this.getPowerRounded() == this.getPowerMaxRounded() || this.getPowerRounded() == (int) Math.round(Conf.powerPlayerStarting))) {
            return false;
        }
        return true;
    }
}
