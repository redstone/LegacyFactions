package com.massivecraft.legacyfactions.event;

import org.bukkit.event.Cancellable;

import com.massivecraft.legacyfactions.entity.FPlayer;
import com.massivecraft.legacyfactions.entity.Faction;

/**
 * Event called when a player loses power.
 */
public class EventFactionsPowerLoss extends AbstractFactionsPlayerEvent implements Cancellable {

    
    public EventFactionsPowerLoss(Faction f, FPlayer p, double powerLoss) {
        super(f, p);
    }
    
    private boolean cancelled = false;
    private String message;
    private double powerLoss;
    
    
    public double getPowerLoss() {
    	return this.powerLoss;
    }
    
    public void setPowerLoss(double powerLoss) {
    	this.powerLoss = powerLoss;
    }
    
    /**
     * Get the power loss message.
     *
     * @return power loss message as String.
     */
    public String getMessage() {
        return message;
    }

    /**
     * Set the power loss message.
     *
     * @param message
     */
    public void setMessage(String message) {
        this.message = message;
    }

    @Override
    public boolean isCancelled() {
        return cancelled;
    }

    @Override
    public void setCancelled(boolean c) {
        this.cancelled = c;
    }

}
