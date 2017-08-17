package net.redstoneore.legacyfactions.event;

import org.bukkit.event.Cancellable;

import net.redstoneore.legacyfactions.entity.FPlayer;
import net.redstoneore.legacyfactions.entity.Faction;
import net.redstoneore.legacyfactions.warp.FactionWarp;

public class EventFactionsWarpDelete extends AbstractFactionsPlayerEvent<EventFactionsWarpDelete> implements Cancellable {
	
	// -------------------------------------------------- //
	// CONSTRUCT
	// -------------------------------------------------- //
	
	public EventFactionsWarpDelete(Faction faction, FPlayer fplayer, FactionWarp warp, Double cost) {
		super(faction, fplayer);
		
		this.warp = warp;
		this.cost = cost;
	}
	
	// -------------------------------------------------- //
	// FIELDS
	// -------------------------------------------------- //
	
	private Boolean cancelled = false;
	private FactionWarp warp = null;
	private Double cost = 0.0;
	
	// -------------------------------------------------- //
	// METHODS
	// -------------------------------------------------- //
	
	@Override
	public boolean isCancelled() {
		return this.cancelled;
	}

	@Override
	public void setCancelled(boolean cancel) {
		this.cancelled = cancel;
	}
	
	public FactionWarp getWarp() {
		return this.warp;
	}
	
	public void setCost(Double cost) {
		this.cost = cost;
	}
	
	public Double getCost() {
		return this.cost;
	}
	
}
