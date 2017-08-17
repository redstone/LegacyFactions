package net.redstoneore.legacyfactions.event;

import org.bukkit.event.Cancellable;

import net.redstoneore.legacyfactions.entity.FPlayer;
import net.redstoneore.legacyfactions.warp.FactionWarp;

public class EventFactionsWarpUse extends AbstractFactionsPlayerEvent<EventFactionsWarpUse> implements Cancellable {

	public EventFactionsWarpUse(FPlayer fplayer, FactionWarp warp) {
		super(fplayer.getFaction(), fplayer);
		this.warp = warp;
	}
	
	// -------------------------------------------------- //
	// FIELDS
	// -------------------------------------------------- //
	
	private Boolean cancelled = false;
	private final FactionWarp warp;
	
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
	
}
