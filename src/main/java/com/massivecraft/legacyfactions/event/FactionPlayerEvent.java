package com.massivecraft.legacyfactions.event;

import com.massivecraft.legacyfactions.entity.FPlayer;
import com.massivecraft.legacyfactions.entity.Faction;

/**
 * Represents an event involving a Faction and a FPlayer.
 */
public class FactionPlayerEvent extends FactionEvent {

    private final FPlayer fPlayer;

    public FactionPlayerEvent(Faction faction, FPlayer fPlayer) {
        super(faction);
        this.fPlayer = fPlayer;
    }

    public FPlayer getfPlayer() {
        return this.fPlayer;
    }
}
