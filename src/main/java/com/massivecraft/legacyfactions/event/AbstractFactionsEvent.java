package com.massivecraft.legacyfactions.event;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import com.massivecraft.legacyfactions.entity.Faction;

/**
 * Represents an event involving a Faction.
 */
public abstract class AbstractFactionsEvent extends Event {

    private static final HandlerList handlers = new HandlerList();
    private final Faction faction;

    public AbstractFactionsEvent(Faction faction) {
        this.faction = faction;
    }

    /**
     * Get the Faction involved in the event.
     *
     * @return faction involved in the event.
     */
    public Faction getFaction() {
        return this.faction;
    }

    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }

}
