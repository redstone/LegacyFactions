package net.redstoneore.legacyfactions.event;

import org.bukkit.event.Cancellable;

import net.redstoneore.legacyfactions.entity.FPlayer;
import net.redstoneore.legacyfactions.entity.Faction;

public class EventFactionsUnban extends AbstractFactionsPlayerEvent<EventFactionsUnban> implements Cancellable {

	// -------------------------------------------------- //
	// CONSTRUCT
	// -------------------------------------------------- //
	
	public EventFactionsUnban(Faction faction, FPlayer fplayer, FPlayer invoker) {
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
	 * Get the player that invoked this unban.
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
