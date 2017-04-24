package net.redstoneore.legacyfactions.event;

import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import net.redstoneore.legacyfactions.entity.FPlayer;
import net.redstoneore.legacyfactions.entity.FPlayerColl;
import net.redstoneore.legacyfactions.entity.Faction;
import net.redstoneore.legacyfactions.entity.FactionColl;

/**
 * Event called when a Faction is created.
 */
public class EventFactionsCreate extends Event implements Cancellable {

    private static final HandlerList handlers = new HandlerList();

    private String factionTag;
    private Player sender;
    private boolean cancelled;

    public EventFactionsCreate(Player sender, String tag) {
        this.factionTag = tag;
        this.sender = sender;
        this.cancelled = false;
    }

    public FPlayer getFPlayer() {
        return FPlayerColl.get(this.sender);
    }

    public String getFactionTag() {
        return factionTag;
    }
    
    /**
     * Change the faction tag
     * @param tag
     * @return false if tag is taken
     */
    public Boolean setFactionTag(String tag) {
    	Faction check = FactionColl.get().getByTag(tag);
    	if (check == null) {
    		this.factionTag = tag;
    		return true;
    	}
    	
    	return false;
    }

    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }

    @Override
    public boolean isCancelled() {
        return cancelled;
    }

    @Override
    public void setCancelled(boolean c) {
        this.cancelled = c;
    }
}