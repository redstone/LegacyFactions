package net.redstoneore.legacyfactions.event;

import org.bukkit.event.Cancellable;

import net.redstoneore.legacyfactions.entity.FPlayer;
import net.redstoneore.legacyfactions.entity.Faction;

/**
 * Event called when a player loses power.
 */
public class EventFactionsPowerLoss extends AbstractFactionsPlayerEvent<EventFactionsPowerLoss> implements Cancellable {
	
	// -------------------------------------------------- //
	// CONSTRUCT
	// -------------------------------------------------- //
	
	public EventFactionsPowerLoss(Faction faction, FPlayer fplayer, double powerLoss) {
		super(faction, fplayer);
		this.powerLoss = powerLoss;
	}
	
	// -------------------------------------------------- //
	// FIELDS
	// -------------------------------------------------- //
	
	private boolean cancelled = false;
	private String message;
	private double powerLoss;
	
	// -------------------------------------------------- //
	// METHODS
	// -------------------------------------------------- //
	
	/**
	 * Get the power being lost from this event
	 * @return the power being lost
	 */
	public double getPowerLoss() {
		return this.powerLoss;
	}
	
	/**
	 * Set the power being lost from this event
	 * @param powerLoss The new power being lost.
	 */
	public void setPowerLoss(double powerLoss) {
		this.powerLoss = powerLoss;
	}
	
	/**
	 * Get the power loss message.
	 * @return power loss message as String.
	 */
	public String getMessage() {
		return this.message;
	}
	
	/**
	 * Set the power loss message.
	 * @param message Message as a string
	 */
	public void setMessage(String message) {
		this.message = message;
	}
	
	@Override
	public boolean isCancelled() {
		return this.cancelled;
	}

	@Override
	public void setCancelled(boolean cancelled) {
		this.cancelled = cancelled;
	}

}
