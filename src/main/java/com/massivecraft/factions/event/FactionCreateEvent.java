package com.massivecraft.factions.event;

import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import com.massivecraft.factions.entity.FPlayer;
import com.massivecraft.factions.entity.FPlayerColl;
import com.massivecraft.factions.entity.Faction;
import com.massivecraft.factions.entity.FactionColl;

/**
 * Event called when a Faction is created.
 */
public class FactionCreateEvent extends Event implements Cancellable {

    private static final HandlerList handlers = new HandlerList();

    private String factionTag;
    private Player sender;
    private boolean cancelled;

    public FactionCreateEvent(Player sender, String tag) {
        this.factionTag = tag;
        this.sender = sender;
        this.cancelled = false;
    }

    public FPlayer getFPlayer() {
        return FPlayerColl.getInstance().getByPlayer(sender);
    }

    public String getFactionTag() {
        return factionTag;
    }
    
    /**
     * Change the faction flag
     * @param tag
     * @return false if tag is taken
     */
    public Boolean setFactionTag(String tag) {
    	Faction check = FactionColl.getInstance().getByTag(tag);
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