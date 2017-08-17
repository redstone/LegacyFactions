package net.redstoneore.legacyfactions.event;

import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;

import net.redstoneore.legacyfactions.entity.FPlayer;
import net.redstoneore.legacyfactions.entity.FPlayerColl;
import net.redstoneore.legacyfactions.entity.FactionColl;

/**
 * Event called when a faction is disbanded.
 */
public class EventFactionsDisband extends AbstractFactionsEvent<EventFactionsDisband> implements Cancellable {

    private final boolean canCancel;
    private final DisbandReason reason;
    private boolean cancelled = false;
    private Player sender;

    public EventFactionsDisband(Player sender, String factionId, boolean canCancel, DisbandReason reason) {
        super(FactionColl.get().getFactionById(factionId));
        this.sender = sender;
        this.canCancel = canCancel;
        this.reason = reason;
    }

    public boolean canCancel() {
        return canCancel;
    }

    public DisbandReason getReason() {
        return reason;
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
        if (canCancel) {
            cancelled = c;
        } else {
            throw new IllegalStateException("This event cannot be cancelled.");
        }
    }

    public enum DisbandReason {
        DISBAND_COMMAND, LEAVE, PLUGIN, INACTIVITY
    }
}
