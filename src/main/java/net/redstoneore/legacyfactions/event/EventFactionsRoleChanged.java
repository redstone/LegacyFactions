package net.redstoneore.legacyfactions.event;

import org.bukkit.event.HandlerList;

import net.redstoneore.legacyfactions.Role;
import net.redstoneore.legacyfactions.entity.FPlayer;
import net.redstoneore.legacyfactions.entity.Faction;

public class EventFactionsRoleChanged extends AbstractFactionsPlayerEvent<EventFactionsRoleChanged> {

	// -------------------------------------------------- //
	// STATIC FIELDS
	// -------------------------------------------------- //

    private static final HandlerList handlers = new HandlerList();

	// -------------------------------------------------- //
	// STATIC METHODS
	// -------------------------------------------------- //
	
	public static EventFactionsRoleChanged create(Faction faction, FPlayer fplayer, Role previousRole, Role newRole) {
		return new EventFactionsRoleChanged(faction, fplayer, previousRole, newRole);
	}

    public static HandlerList getHandlerList() {
        return handlers;
    }
	
	// -------------------------------------------------- //
	// CONSTRUCT
	// -------------------------------------------------- //
	
	public EventFactionsRoleChanged(Faction faction, FPlayer fplayer, Role previousRole, Role newRole) {
		super(faction, fplayer);
		
		this.previousRole = previousRole;
		this.newRole = newRole;
	}
	
	// -------------------------------------------------- //
	// FIELDS
	// -------------------------------------------------- //
	
	private final Role previousRole, newRole;

	// -------------------------------------------------- //
	// METHODS
	// -------------------------------------------------- //
	
	/**
	 * Get the role that the player had before.
	 * @return {@link Role} that the player had before.
	 */
	public Role getPreviousRole() {
		return this.previousRole;
	}
	
	/**
	 * Get the role the player has now.
	 * @return {@link Role} the role the player has now.
	 */
	public Role getNewRole() {
		return this.newRole;
	}
	
    public HandlerList getHandlers() {
        return handlers;
    }
	
}
