package net.redstoneore.legacyfactions.event;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.function.BiConsumer;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import net.redstoneore.legacyfactions.FLocation;
import net.redstoneore.legacyfactions.entity.FPlayer;
import net.redstoneore.legacyfactions.entity.Faction;
import net.redstoneore.legacyfactions.locality.Locality;

public class EventFactionsLandChange extends Event {

	// -------------------------------------------------- //
	// STATIC FIELDS
	// -------------------------------------------------- // 
	
	private static final HandlerList handlers = new HandlerList();
	
	// -------------------------------------------------- //
	// STATIC METHODS
	// -------------------------------------------------- // 
	
	public static HandlerList getHandlerList() {
		return handlers;
	}
	
	// -------------------------------------------------- //
	// CONSTRUCT
	// -------------------------------------------------- // 
	
	public EventFactionsLandChange(FPlayer fplayer, Map<Locality, Faction> transactions, LandChangeCause cause) {
		this.fplayer = fplayer;
		this.transactions = transactions;
		this.cause = cause;
	}
	
	// -------------------------------------------------- //
	// FIELDS
	// -------------------------------------------------- // 
	
	private final FPlayer fplayer;
	private Map<Locality, Faction> transactions;
	private final LandChangeCause cause;
	private boolean cancelled = false;
	
	// -------------------------------------------------- //
	// METHODS
	// -------------------------------------------------- // 
		
	public void setCancelled(boolean cancel) {
		this.cancelled = cancel;
	}
	
	public boolean isCancelled() {
		return this.cancelled;
	}
	
	public FPlayer getFPlayer() {
		return this.fplayer;
	}
	
	public LandChangeCause getCause() {
		return this.cause;
	}
	
	public HandlerList getHandlers() {
		return handlers;
	}

	public Map<Locality, Faction> transactions() {
		return this.transactions;
	}
	
	public void transactions(BiConsumer<? super Locality, ? super Faction> action) {
		this.transactions.forEach(action);
	}
	
	public void transactions(Map<Locality, Faction> newTransactions) {
		this.transactions = newTransactions;
	}
	
	public Iterator<Entry<Locality, Faction>> transactionsIterator() {
		return this.transactions.entrySet().iterator();
	}
	
	// -------------------------------------------------- //
	// DEPRECATED
	// -------------------------------------------------- // 
	
	@Deprecated
	public Map<FLocation, Faction> getTransactions() {
		Map<FLocation, Faction> convertedTransactions = new HashMap<>();
		
		this.transactions.forEach((locality, faction) -> {
			convertedTransactions.put(FLocation.valueOf(locality.getChunk()), faction);
		});
		
		return convertedTransactions;
	}
	
	@Deprecated
	public void setTransactions(Map<FLocation, Faction> newTransactions) {
		Map<Locality, Faction> convertedTransactions = new HashMap<>();
		
		newTransactions.forEach((flocation, faction) -> {
			convertedTransactions.put(Locality.of(flocation.getWorld(), (int)flocation.getX(), (int)flocation.getZ()), faction);
		});
		
		this.transactions = convertedTransactions;
	}
	
	// -------------------------------------------------- //
	// ENUM
	// -------------------------------------------------- // 
	
	public enum LandChangeCause {
		Claim,
		Unclaim,
		
		;
	}
	
}
