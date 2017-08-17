package net.redstoneore.legacyfactions.event;

import org.bukkit.Location;
import org.bukkit.event.Cancellable;

import net.redstoneore.legacyfactions.entity.FPlayer;
import net.redstoneore.legacyfactions.entity.Faction;
import net.redstoneore.legacyfactions.util.LazyLocation;

/**
 * This event is called when a player creates a warp.
 */
public class EventFactionsWarpCreate extends AbstractFactionsPlayerEvent<EventFactionsWarpCreate> implements Cancellable {
	
	// -------------------------------------------------- //
	// CONSTRUCT
	// -------------------------------------------------- //
	
	public EventFactionsWarpCreate(Faction faction, FPlayer fplayer, String warpName, String warpPassword, LazyLocation warpLocation, Double warpCost) {
		super(faction, fplayer);
		
		this.warpName = warpName;
		this.warpPassword = warpPassword;
		this.warpLocation = warpLocation;
		this.warpCost = warpCost;
	}
	
	// -------------------------------------------------- //
	// FIELDS
	// -------------------------------------------------- //
	
	private Boolean cancelled = false;
	private String warpName = null;
	private String warpPassword = null;
	private LazyLocation warpLocation = null;
	private Double warpCost;
	
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
	
	public void setName(String name) {
		this.warpName = name;
	}
	
	public String getName() {
		return this.warpName;
	}
	
	public void setPassword(String password) {
		this.warpPassword = password;
	}

	/**
	 * Returns the password of the warp, can be null if none set
	 * @return the password of the warp, can be null if none set
	 */
	public String getPassword() {
		return this.warpPassword;
	}
	
	public void setLocation(Location location) {
		this.warpLocation = new LazyLocation(location);
	}
	
	public void setLocation(LazyLocation location) {
		this.warpLocation = location;
	}
	
	public LazyLocation getLocation() {
		return this.warpLocation;
	}
	
	public void setCost(Double cost) {
		this.warpCost = cost;
	}
	
	public Double getCost() {
		return this.warpCost;
	}
	
}
