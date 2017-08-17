package net.redstoneore.legacyfactions.event;

import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import net.redstoneore.legacyfactions.entity.FPlayer;
import net.redstoneore.legacyfactions.entity.Faction;

public class EventFactionsChange extends Event implements Cancellable {

    private static final HandlerList handlers = new HandlerList();
    
	public EventFactionsChange(FPlayer fplayer, Faction previousFaction, Faction newFaction, boolean canCancel, ChangeReason reason) {
		this.fplayer = fplayer;
		this.previousFaction = previousFaction;
		this.newFaction = newFaction;
		this.canCancel = canCancel;
		this.reason = reason;
	}
	
	private boolean cancelled = false;
	private final FPlayer fplayer;
	private final Faction previousFaction;
	private Faction newFaction;
	private boolean canCancel = true;
	private final ChangeReason reason;
	@Override
	public boolean isCancelled() {
		return this.cancelled;
	}

	@Override
	public void setCancelled(boolean cancel) {
		cancelled = cancel;
	}
	
	public boolean canCancel() {
		return this.canCancel;
	}
	
	public FPlayer getFPlayer() {
		return this.fplayer;
	}
	
	public Faction getFactionOld() {
		return this.previousFaction;
	}
	
	public Faction getFactionNew() {
		return this.newFaction;
	}

	public void setFactionNew(Faction faction) {
		this.newFaction = faction;
	}
	
	public ChangeReason getReason() {
		return this.reason;
	}
	
	@Override
	public HandlerList getHandlers() {
		return handlers;
	}
	
    public static HandlerList getHandlerList() {
        return handlers;
    }
	
	public enum ChangeReason {
		KICKED,
		BANNED,
		DISBAND,
		RESET,
		JOINOTHER,
		LEAVE,
		CREATE,
		LEADER,
		COMMAND,
		
		;
	}
	
}
