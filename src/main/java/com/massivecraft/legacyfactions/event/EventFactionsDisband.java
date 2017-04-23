package com.massivecraft.legacyfactions.event;

import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;

import com.massivecraft.legacyfactions.entity.FPlayer;
import com.massivecraft.legacyfactions.entity.FPlayerColl;
import com.massivecraft.legacyfactions.entity.FactionColl;

/**
 * Event called when a faction is disbanded.
 */
public class EventFactionsDisband extends AbstractFactionsEvent implements Cancellable {

    private boolean cancelled = false;
    private Player sender;

    public EventFactionsDisband(Player sender, String factionId) {
        super(FactionColl.getInstance().getFactionById(factionId));
        this.sender = sender;
    }

    public FPlayer getFPlayer() {
        return FPlayerColl.get(sender);
    }
    
    @Override
    public boolean isCancelled() {
        return cancelled;
    }

    @Override
    public void setCancelled(boolean c) {
        cancelled = c;
    }
}
