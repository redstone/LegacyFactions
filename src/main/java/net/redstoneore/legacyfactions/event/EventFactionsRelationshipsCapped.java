package net.redstoneore.legacyfactions.event;

import org.bukkit.event.Cancellable;

import net.redstoneore.legacyfactions.Relation;
import net.redstoneore.legacyfactions.entity.FPlayer;
import net.redstoneore.legacyfactions.entity.Faction;

/**
 * This event is called when the player is trying to set a relation but has hit the cap. To allow 
 * it anyway, you can cancel this event.
 */
public class EventFactionsRelationshipsCapped extends AbstractFactionsPlayerEvent<EventFactionsRelationshipsCapped> implements Cancellable {

	// -------------------------------------------------- //
	// STATIC METHODS
	// -------------------------------------------------- //
	
	public static EventFactionsRelationshipsCapped create(FPlayer fplayer, Faction faction, Relation relation) {
		return new EventFactionsRelationshipsCapped(fplayer, faction, relation);
	}
	
	// -------------------------------------------------- //
	// CONSTRUCT
	// -------------------------------------------------- //
	
	public EventFactionsRelationshipsCapped(FPlayer fplayer, Faction faction, Relation relation) {
		super(faction, fplayer);
		
		this.relation = relation;
	}
	
	// -------------------------------------------------- //
	// FIELDS
	// -------------------------------------------------- //

	private Relation relation;
	private boolean cancelled = false;
	
	// -------------------------------------------------- //
	// METHODS
	// -------------------------------------------------- //
	
	/**
	 * The relationship type.
	 * @return {@link Relation} type.
	 */
	public Relation getRelation() {
		return this.relation;
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
