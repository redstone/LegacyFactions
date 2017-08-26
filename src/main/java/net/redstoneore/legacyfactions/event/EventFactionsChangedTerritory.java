package net.redstoneore.legacyfactions.event;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import net.redstoneore.legacyfactions.Relation;
import net.redstoneore.legacyfactions.entity.FPlayer;
import net.redstoneore.legacyfactions.entity.Faction;
import net.redstoneore.legacyfactions.locality.Locality;

public class EventFactionsChangedTerritory extends Event {

	// -------------------------------------------------- //
	// STATIC FIELDS
	// -------------------------------------------------- //
	
	private static final HandlerList handlers = new HandlerList();
    
	// -------------------------------------------------- //
	// CONSTRUCT
	// -------------------------------------------------- //
	
	public EventFactionsChangedTerritory(FPlayer fplayer, Faction factionFrom, Faction factionTo, Locality previousLocation, Locality newLocation) {
		this.fplayer = fplayer;
		this.factionFrom = factionFrom;
		this.factionTo = factionTo;
		this.previousLocation = previousLocation;
		this.newLocation = newLocation;
	}
	
	// -------------------------------------------------- //
	// FIELDS
	// -------------------------------------------------- //
	
	private FPlayer fplayer;
	private Faction factionFrom;
	private Faction factionTo;
	private Locality previousLocation;
	private Locality newLocation;
	
	// -------------------------------------------------- //
	// METHODS
	// -------------------------------------------------- //
	
	public FPlayer getFPlayer() {
		return this.fplayer;
	}
	
	public Faction getFactionFrom() {
		return this.factionFrom;
	}
	
	public Faction getFactionTo() {
		return this.factionTo;
	}
	
	public Locality getPreviousLocation() {
		return this.previousLocation;
	}
	
	public Locality getNewLocation() {
		return this.newLocation;
	}
	
	public Relation getRelationBetweenLocations() {
		return this.getFactionFrom().getRelationTo(this.getFactionTo());
	}
	
	@Override
	public HandlerList getHandlers() {
		return handlers;
	}
	
    public static HandlerList getHandlerList() {
        return handlers;
    }
    
}
