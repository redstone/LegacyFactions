package net.redstoneore.legacyfactions.event;

import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;

import net.redstoneore.legacyfactions.entity.FPlayer;
import net.redstoneore.legacyfactions.entity.FPlayerColl;
import net.redstoneore.legacyfactions.entity.FactionColl;

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
