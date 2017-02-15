package com.massivecraft.legacyfactions.event;

import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;

import com.massivecraft.legacyfactions.entity.FPlayer;
import com.massivecraft.legacyfactions.entity.Faction;

/**
 * Event called when a player loses power.
 */
public class PowerLossEvent extends FactionPlayerEvent implements Cancellable {

    
    public PowerLossEvent(Faction f, FPlayer p, double powerLoss) {
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
     * Get the id of the faction.
     *
     * @return id of faction as String
     *
     * @deprecated use getFaction().getId() instead.
     */
    @Deprecated
    public String getFactionId() {
        return getFaction().getId();
    }

    /**
     * Get the tag of the faction.
     *
     * @return tag of faction as String
     *
     * @deprecated use getFaction().getTag() instead.
     */
    @Deprecated
    public String getFactionTag() {
        return getFaction().getTag();
    }

    /**
     * Get the Player involved in the event.
     *
     * @return Player from FPlayer.
     *
     * @deprecated use getfPlayer().getPlayer() instead.
     */
    @Deprecated
    public Player getPlayer() {
        return getfPlayer().getPlayer();
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
