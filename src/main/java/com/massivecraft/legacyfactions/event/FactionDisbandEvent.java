package com.massivecraft.legacyfactions.event;

import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;

import com.massivecraft.legacyfactions.entity.FPlayer;
import com.massivecraft.legacyfactions.entity.FPlayerColl;
import com.massivecraft.legacyfactions.entity.FactionColl;

/**
 * Event called when a faction is disbanded.
 */
public class FactionDisbandEvent extends FactionEvent implements Cancellable {

    private boolean cancelled = false;
    private Player sender;

    public FactionDisbandEvent(Player sender, String factionId) {
        super(FactionColl.getInstance().getFactionById(factionId));
        this.sender = sender;
    }

    public FPlayer getFPlayer() {
        return FPlayerColl.getInstance().getByPlayer(sender);
    }

    public Player getPlayer() {
        return sender;
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
