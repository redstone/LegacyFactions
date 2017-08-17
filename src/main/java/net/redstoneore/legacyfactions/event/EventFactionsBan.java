package net.redstoneore.legacyfactions.event;

import org.bukkit.event.Cancellable;

import net.redstoneore.legacyfactions.entity.FPlayer;
import net.redstoneore.legacyfactions.entity.Faction;

public class EventFactionsBan extends AbstractFactionsPlayerEvent<EventFactionsBan> implements Cancellable {

	// -------------------------------------------------- //
	// CONSTRUCT
	// -------------------------------------------------- //
	
	public EventFactionsBan(Faction faction, FPlayer fplayer, FPlayer invoker) {
		super(faction, fplayer);
		
		this.invoker = null;
	}
		
	// -------------------------------------------------- //
	// FIELDS
	// -------------------------------------------------- //
	
	private FPlayer invoker = null;
	private boolean cancelled = false;
	
	// -------------------------------------------------- //
	// METHODS
	// -------------------------------------------------- //

	/**
	 * Get the player that invoked this ban.
	 * @return
	 */
	public FPlayer getInvoker() {
		return this.invoker;
	}
	
	@Override
	public boolean isCancelled() {
		return this.cancelled;
	}

	@Override
	public void setCancelled(boolean cancel) {
		this.cancelled = cancel;
	}

}
