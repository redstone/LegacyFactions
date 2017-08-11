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
	
	public void removeAll() {
		Board.get().clearOwnershipAt(new FLocation(locality.getChunk()));
	}
	
	public List<FPlayer> getAccess() {
		if (!Board.get().getFactionAt(this.flocation).doesLocationHaveOwnersSet(this.flocation)) return new ArrayList<>();
		
		List<FPlayer> access = new ArrayList<>();
		
		Board.get().getFactionAt(this.flocation).getOwnerList(this.flocation).forEach(playerId -> {
			access.add(FPlayerColl.get(UUID.fromString(playerId)));
		});
		
		return access;
	}
	
	public boolean isOwned() {
		// TODO
		return false;
	}
	
	public void addAccess(FPlayer fplayer) {
		// TODO;
	}
	
	public boolean hasAccess(FPlayer fplayer) {
		return Board.get().getFactionAt(this.flocation).isPlayerInOwnerList(fplayer, this.flocation);
	}
	
	public void removeAccess(FPlayer fplayer) {
		Board.get().getFactionAt(this.flocation).removePlayerAsOwner(fplayer, this.flocation);
	}
	
}
