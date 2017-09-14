package net.redstoneore.legacyfactions.locality;

import java.util.ArrayList;
import java.util.List;

import net.redstoneore.legacyfactions.entity.Board;
import net.redstoneore.legacyfactions.entity.FPlayer;
import net.redstoneore.legacyfactions.ownership.FactionOwnership;

public class LocalityOwnership {

	// -------------------------------------------------- //
	// CONSTRUCT
	// -------------------------------------------------- //
	
	protected LocalityOwnership(Locality locality) {
		this.locality = locality;
	}
	
	// -------------------------------------------------- //
	// FIELDS
	// -------------------------------------------------- //
	
	private final transient Locality locality;
	
	// -------------------------------------------------- //
	// METHODS
	// -------------------------------------------------- //
	
	/**
	 * Remove all owners at this location.
	 * 
	 * @see FactionOwnership
	 */
	public void removeAll() {
		Board.get().clearOwnershipAt(this.locality);
	}
	
	/**
	 * Get list of owners at this location.
	 * 
	 * @return List of owners at this location.
	 * 
	 * @see FactionOwnership
	 */
	public List<FPlayer> getAccess() {
		if (!this.isOwned()) return new ArrayList<>();
				
		return Board.get().getFactionAt(this.locality).ownership().getOwners(this.locality);
	}
	
	/**
	 * Returns true if this location is owned.
	 * 
	 * @return true if this location is owned.
	 * 
	 * @see FactionOwnership
	 */
	public boolean isOwned() {
		return Board.get().getFactionAt(this.locality).ownership().isOwned(this.locality);
	}
	
	/**
	 * Add a player to the owner access list.
	 * 
	 * @param fplayer the player to add.
	 * 
	 * @see FactionOwnership
	 */
	public void addAccess(FPlayer fplayer) {
		Board.get().getFactionAt(this.locality).ownership().ownerAdd(this.locality, fplayer);
	}
	
	/**
	 * Check if a player is on the owner list.
	 * 
	 * @param fplayer the fplayer to check.
	 * 
	 * @return true if they are on the owner list.
	 * 
	 * @see FactionOwnership
	 */
	public boolean hasAccess(FPlayer fplayer) {
		return Board.get().getFactionAt(this.locality).ownership().isOwner(this.locality, fplayer);
	}
	
	/**
	 * Remove a player from the owner access list.
	 * 
	 * @param fplayer the player to remove.
	 * 
	 * @see FactionOwnership
	 */
	public void removeAccess(FPlayer fplayer) {
		Board.get().getFactionAt(this.locality).ownership().ownerRemove(this.locality, fplayer);
	}
	
}
