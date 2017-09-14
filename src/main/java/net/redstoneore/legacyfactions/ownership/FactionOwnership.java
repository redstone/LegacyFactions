package net.redstoneore.legacyfactions.ownership;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import net.redstoneore.legacyfactions.FLocation;
import net.redstoneore.legacyfactions.entity.FPlayer;
import net.redstoneore.legacyfactions.entity.FPlayerColl;
import net.redstoneore.legacyfactions.entity.persist.shared.SharedFaction;
import net.redstoneore.legacyfactions.locality.Locality;

/**
 * Factions can have internal ownership rights. This being, players can be specified as owners of 
 * chunks of land inside a faction. 
 */
public class FactionOwnership {

	// -------------------------------------------------- //
	// CONSTRUCT
	// -------------------------------------------------- //
	
	public FactionOwnership(SharedFaction faction) {
		this.faction = faction;
	}
	
	// -------------------------------------------------- //
	// FIELDS
	// -------------------------------------------------- //
	
	private transient final SharedFaction faction;

	// -------------------------------------------------- //
	// METHODS
	// -------------------------------------------------- //
	
	/**
	 * Clear all ownerships by this faction.
	 */
	public void clearAll() {
		this.faction.clearAllClaimOwnership();
	}
	
	/**
	 * Clear all ownerships for a player in this faction.
	 * @param fplayer Player to remove for.
	 */
	public void clearAll(FPlayer fplayer) {
		this.faction.clearClaimOwnership(fplayer);
	}
	
	/**
	 * Clear ownerships at a location.
	 * @param location The location
	 * @see Locality
	 */
	public void clearAt(Locality location) {
		this.faction.clearClaimOwnership(location);
	}
	
	/**
	 * Confirm is a location has owners set.
	 * @param location The location
	 * @return true if it is owned
	 * @see Locality
	 */
	public boolean isOwned(Locality location) {
		return this.faction.doesLocationHaveOwnersSet(FLocation.valueOf(location.getChunk()));
	}
	
	/**
	 * Get the count of claims with owners.
	 * @return The count of claims with owners.
	 */
	public int count() {
		return this.faction.getCountOfClaimsWithOwners();
	}
	
	/**
	 * Get owners at a location
	 * @param location The location.
	 * @return A list of owners at a location, or an empty list if none.
	 */
	public List<FPlayer> getOwners(Locality location) {
		if (!this.isOwned(location)) {
			return new ArrayList<>();
		}
		
		return this.faction.getOwnerList(FLocation.valueOf(location.getChunk())).stream()
			.map(player -> FPlayerColl.get(UUID.fromString(player)))
			.collect(Collectors.toList());
	}
	
	/**
	 * Is a player an owner at a location.
	 * @param location The location.
	 * @param player The {@link FPlayer} to check.
	 * @return true if the player is an owner
	 * @see Locality
	 */
	public boolean isOwner(Locality location, FPlayer player) {
		if (!this.isOwned(location)) return false;
		
		return (this.faction.isPlayerInOwnerList(player, FLocation.valueOf(location.getChunk())));
	}
	
	/**
	 * Remove a player as an owner at a location.
	 * @param location The location.
	 * @param player The player.
	 */
	public void ownerRemove(Locality location, FPlayer player) {
		this.faction.removePlayerAsOwner(player, FLocation.valueOf(location.getChunk()));
	}
	
	/**
	 * Add a player as an owner at a location.
	 * @param location The location.
	 * @param player The player.
	 */
	public void ownerAdd(Locality location, FPlayer player) {
		this.faction.setPlayerAsOwner(player, FLocation.valueOf(location.getChunk()));
	}

	/**
	 * Get all ownership claims
	 * @return
	 */
	public Map<Locality, Set<String>> getAll() {
		Map<Locality, Set<String>> snapshot = new HashMap<>();
		this.faction.getClaimOwnership().entrySet()
			.forEach(entry -> 
				snapshot.put(Locality.of(entry.getKey().getChunk()), entry.getValue())
			);
		return snapshot;
	}
	
}
