package net.redstoneore.legacyfactions.event;

import net.redstoneore.legacyfactions.entity.FPlayer;
import net.redstoneore.legacyfactions.entity.Faction;

/**
 * Represents an event involving a Faction and a FPlayer.
 */
public abstract class AbstractFactionsPlayerEvent<P> extends AbstractFactionsEvent<P> {

    private final FPlayer fPlayer;

    public AbstractFactionsPlayerEvent(Faction faction, FPlayer fPlayer) {
        super(faction);
        this.fPlayer = fPlayer;
    }

    public FPlayer getfPlayer() {
        return this.fPlayer;
    }
}
