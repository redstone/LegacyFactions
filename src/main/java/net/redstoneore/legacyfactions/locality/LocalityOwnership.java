package net.redstoneore.legacyfactions.locality;

import java.util.ArrayList;
import java.util.List;

import net.redstoneore.legacyfactions.entity.FPlayer;

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
	
	public void removeAll() {
		// TODO
	}
	
	public List<FPlayer> getAccess() {
		// TODO
		return new ArrayList<>();
	}
	
	public boolean isOwned() {
		// TODO
		return false;
	}
	
	public void addAccess(FPlayer fplayer) {
		// TODO;
	}
	
	public void hasAccess(FPlayer fplayer) {
		
	}
	
	public void removeAccess(FPlayer fplayer) {
		
	}
	
}
