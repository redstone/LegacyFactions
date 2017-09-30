package net.redstoneore.legacyfactions.entity;

import java.util.ArrayList;
import java.util.Set;

import org.bukkit.World;

import net.redstoneore.legacyfactions.config.Config;
import net.redstoneore.legacyfactions.entity.deprecated.BoardDeprected;
import net.redstoneore.legacyfactions.locality.Locality;

public abstract class Board implements BoardDeprected {
	
	// -------------------------------------------------- //
	// INSTANCE
	// -------------------------------------------------- // 
	
	private static transient String currentType = null;
	protected static Board instance = get();
	
	public static Board get() {
		if (currentType != Config.backEnd.name()) {
			instance = Config.backEnd.getHandler().getBoard();
			currentType = Config.backEnd.name();
		}
		return instance;
	}
	
	// -------------------------------------------------- //
	// METHODS
	// -------------------------------------------------- // 
	
	/**
	 * Get the faction id at a location
	 * @param locality Location.
	 * @return the faction id.
	 */
	public abstract String getIdAt(Locality locality);
	
	/**
	 * Get the faction at a location, returns wilderness if none.
	 * @param locality Location.
	 * @return the faction at the location.
	 */
	
	public abstract Faction getFactionAt(Locality locality);
	
	/**
	 * Get all claims for a faction by their id
	 * @param factionId The faction id.
	 * @return Set of claims
	 */
	public abstract Set<Locality> getAll(String factionId);

	/**
	 * Get all claims for a faction
	 * @param faction The faction
	 * @return Set of claims
	 */
	public abstract Set<Locality> getAll(Faction faction);

	/**
	 * Get all claims.
	 * @return A set of all claims
	 */
	public abstract Set<Locality> getAll();
	
	/**
	 * Set the faction id at a location.
	 * @param id Id to set.
	 * @param locality Location.
	 */
	public abstract void setIdAt(String id, Locality locality);

	/**
	 * Set the faction at a location.
	 * @param faction Faction to set.
	 * @param locality Location.
	 */
	public abstract void setFactionAt(Faction faction, Locality locality);

	/**
	 * Remove a faction at a location.
	 * @param locality Location.
	 */
	public abstract void removeAt(Locality locality);

	/**
	 * Not to be confused with claims, ownership referring to further member-specific ownership of a claim
	 * @param locality Location
	 */
	public abstract void clearOwnershipAt(Locality locality);

	/**
	 * Unclaim all for a faction id
	 * @param factionId
	 */
	public abstract void unclaimAll(String factionId);
	
	/**
	 * Unclaim all for a faction id in a world
	 * @param factionId
	 * @param world
	 */
	public abstract void unclaimAll(String factionId, World world);

	/**
	 * Is there any nearby chunk with a faction other than the faction at this location.
	 * @param locality Location.
	 * @return true if it borders a chunk with a different faction.
	 */
	public abstract boolean isBorderLocation(Locality locality);

	/**
	 * Is this location connected to any location claimed by the specified faction?
	 * @param locality Location to check
	 * @param faction Faction to check against
	 * @return true if there is a chunk claimed by faction
	 */
	public abstract boolean isConnectedLocation(Locality locality, Faction faction);

	/**
	 * Is this faction with a radius of the locality.
	 * @param locality Locality.
	 * @param faction Faction.
	 * @param radius Radius.
	 * @return true if the faction is within a radius of the locality.
	 */
	public abstract boolean hasFactionWithin(Locality locality, Faction faction, int radius);
	
	// -------------------------------------------------- //
	// COUNT
	// -------------------------------------------------- //

	/**
	 * Get count of claims for a faction id
	 * @param factionId Faction id
	 * @return the count
	 */
	public abstract int getFactionCoordCount(String factionId);

	/**
	 * Get count of claims for a faction
	 * @param faction Faction
	 * @return the count
	 */
	public abstract int getFactionCoordCount(Faction faction);
	
	/**
	 * Get count of claims for a faction in a world
	 * @param faction Faction
	 * @param world The world
	 * @return the count
	 */
	public abstract int getFactionCoordCountInWorld(Faction faction, World world);

	// -------------------------------------------------- //
	// MAP
	// -------------------------------------------------- //

	/**
	 * The map is relative to a coord and a faction north is in the direction of decreasing x east is in the direction
	 * of decreasing z
	 */
	public abstract ArrayList<String> getMap(Faction faction, Locality locality, double inDegrees);

	// -------------------------------------------------- //
	// PERSIST UTIL
	// -------------------------------------------------- //
	
	/**
	 * Cleaner. Removes orphaned foreign keys
	 */
	public abstract void clean();
	
	/**
	 * Cleaner. Removes orphaned foreign keys
	 * @param factionId
	 */
	public abstract void clean(String factionId);
	
	/**
	 * Force a synchronised save
	 */
	public abstract void forceSave();

	/**
	 * Force a save
	 * @param sync Is this save sync? Pass false for async.
	 */
	public abstract void forceSave(boolean sync);

	/**
	 * Load the board.
	 * @return true if it was a success.
	 */
	public abstract boolean load();
	
	/**
	 * Get the persist type of Board
	 * @return The persist type.
	 */
	public abstract String getPersistType();
	
	
}
