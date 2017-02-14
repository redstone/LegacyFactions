package com.massivecraft.factions.event;

import com.massivecraft.factions.entity.FPlayer;
import com.massivecraft.factions.entity.Faction;

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
