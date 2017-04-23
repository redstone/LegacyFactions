package net.redstoneore.legacyfactions.event;

import java.util.Map;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import net.redstoneore.legacyfactions.FLocation;
import net.redstoneore.legacyfactions.entity.FPlayer;
import net.redstoneore.legacyfactions.entity.Faction;

public class EventFactionsLandChange extends Event {

    private static final HandlerList handlers = new HandlerList();

	public EventFactionsLandChange(FPlayer fplayer, Map<FLocation, Faction> transactions, LandChangeCause cause) {
		this.fplayer = fplayer;
		this.transactions = transactions;
		this.cause = cause;
	}
	
	private final FPlayer fplayer;
	private Map<FLocation, Faction> transactions;
	private final LandChangeCause cause;
	private boolean cancelled = false;
	
    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }
    
    public void setCancelled(boolean cancel) {
    	this.cancelled = cancel;
    }
    
    public boolean isCancelled() {
    	return this.cancelled;
    }
    
    public FPlayer getFPlayer() {
    	return this.fplayer;
    }
    
    public Map<FLocation, Faction> getTransactions() {
    	return this.transactions;
    }
    
    public void setTransactions(Map<FLocation, Faction> newTransactions) {
    	this.transactions = newTransactions;
    }
    
    public LandChangeCause getCause() {
    	return this.cause;
    }
    
    public enum LandChangeCause {
    	Claim,
    	Unclaim,
    	;
    }
    
}
