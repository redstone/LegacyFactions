package net.redstoneore.legacyfactions.locality;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import net.redstoneore.legacyfactions.FLocation;
import net.redstoneore.legacyfactions.entity.Board;
import net.redstoneore.legacyfactions.entity.FPlayer;
import net.redstoneore.legacyfactions.entity.FPlayerColl;

public class LocalityOwnership {

	// -------------------------------------------------- //
	// CONSTRUCT
	// -------------------------------------------------- //
	
	protected LocalityOwnership(Locality locality) {
		this.locality = locality;
		this.flocation = new FLocation(locality.getChunk());
	}
	
	// -------------------------------------------------- //
	// FIELDS
	// -------------------------------------------------- //
	
	private final transient Locality locality;
	private final transient FLocation flocation;
	
	// -------------------------------------------------- //
	// METHODS
	// -------------------------------------------------- //
	
	/**
	 * Remove all owners at this location.
	 */
	public void removeAll() {
		Board.get().clearOwnershipAt(this.locality);
	}
	
	/**
	 * Get list of owners at this location.
	 * @return
	 */
	public List<FPlayer> getAccess() {
		if (!this.isOwned()) return new ArrayList<>();
		
		List<FPlayer> access = new ArrayList<>();
		
		Board.get().getFactionAt(this.locality).getOwnerList(this.flocation).forEach(playerId -> 
			access.add(FPlayerColl.get(UUID.fromString(playerId)))
		);
		
		return access;
	}
	
	/**
	 * Returns true if this location is owned.
	 * @return true if this location is owned.
	 */
	public boolean isOwned() {
		return Board.get().getFactionAt(this.locality).doesLocationHaveOwnersSet(this.flocation);
	}
	
	/**
	 * Add a player to the owner access list.
	 * @param fplayer the player to add.
	 */
	public void addAccess(FPlayer fplayer) {
		Board.get().getFactionAt(this.locality).setPlayerAsOwner(fplayer, this.flocation);
	}
	
	/**
	 * Check if a player is on the owner list.
	 * @param fplayer the fplayer to check.
	 * @return true if they are on the owner list.
	 */
	public boolean hasAccess(FPlayer fplayer) {
		return Board.get().getFactionAt(this.locality).isPlayerInOwnerList(fplayer, this.flocation);
	}
	
	/**
	 * Remove a player from the owner access list.
	 * @param fplayer the player to remove.
	 */
	public void removeAccess(FPlayer fplayer) {
		Board.get().getFactionAt(this.locality).removePlayerAsOwner(fplayer, this.flocation);
	}
	
}
