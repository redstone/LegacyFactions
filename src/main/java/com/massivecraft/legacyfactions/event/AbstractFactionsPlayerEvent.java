package com.massivecraft.legacyfactions.event;

import com.massivecraft.legacyfactions.entity.FPlayer;
import com.massivecraft.legacyfactions.entity.Faction;

/**
 * Represents an event involving a Faction and a FPlayer.
 */
public abstract class AbstractFactionsPlayerEvent extends AbstractFactionsEvent {

    private final FPlayer fPlayer;

    public AbstractFactionsPlayerEvent(Faction faction, FPlayer fPlayer) {
        super(faction);
        this.fPlayer = fPlayer;
    }

    public FPlayer getfPlayer() {
        return this.fPlayer;
    }
}
