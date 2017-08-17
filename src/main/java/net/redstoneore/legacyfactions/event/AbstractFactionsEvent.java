package net.redstoneore.legacyfactions.event;

import org.bukkit.Bukkit;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import net.redstoneore.legacyfactions.entity.Faction;

/**
 * Represents an event involving a Faction.
 */
public abstract class AbstractFactionsEvent<P> extends Event {

    private static final HandlerList handlers = new HandlerList();
    private final Faction faction;
    private boolean called = false;
    
    public AbstractFactionsEvent(Faction faction) {
        this.faction = faction;
    }
    
    @SuppressWarnings("unchecked")
	public P call() {
    	// Don't allow duplicate calls
    	if (this.called == true) return (P) this;
    	this.called = true;
    	
    	// Call the event.
    	Bukkit.getServer().getPluginManager().callEvent(this);
    	
    	 return (P) this;
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
