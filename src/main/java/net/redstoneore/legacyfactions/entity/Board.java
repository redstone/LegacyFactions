package net.redstoneore.legacyfactions.entity;

import java.util.ArrayList;
import java.util.Set;

import org.bukkit.World;

import net.redstoneore.legacyfactions.FLocation;
import net.redstoneore.legacyfactions.locality.Locality;

public abstract class Board {
	
	// -------------------------------------------------- //
	// INSTANCE
	// -------------------------------------------------- // 
	
	private static transient String currentType = null;
	protected static Board instance = get();
	
	public static Board get() {
		if (currentType != Conf.backEnd.name()) {
			instance = Conf.backEnd.getHandler().getBoard();
			currentType = Conf.backEnd.name();
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
	 * Get all claims for a faction by their id
	 * @param factionId The faction id.
	 * @return Set of claims
	 */
	public abstract Set<FLocation> getAllClaims(String factionId);

	/**
	 * Get all claims for a faction
	 * @param faction The faction
	 * @return Set of claims
	 */
	public abstract Set<FLocation> getAllClaims(Faction faction);

	/**
	 * Get all claims.
	 * @return A set of all claims
	 */
	public abstract Set<FLocation> getAllClaims();

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
	
	public abstract String getPersistType();
	
	// -------------------------------------------------- //
	// DEPRECATED
	// -------------------------------------------------- //
	
	/**
	 * Deprecated, use {@link #getIdAt(Locality)}
	 * @param flocation
	 * @return
	 */
	public abstract String getIdAt(FLocation flocation);
	
	/**
	 * Deprecated, use {@link #getFactionAt(Locality)}
	 * @param flocation
	 * @return
	 */
	@Deprecated
	public abstract Faction getFactionAt(FLocation flocation);
	
	/**
	 * Deprecated, use {@link #setFactionAt(Faction, Locality)}
	 * @param id
	 * @param flocation
	 */
	@Deprecated
	public abstract void setIdAt(String id, FLocation flocation);

	/**
	 * Deprecated, use {@link #setFactionAt(Faction, Locality)}
	 * @param faction
	 * @param flocation
	 */
	public abstract void setFactionAt(Faction faction, FLocation flocation);
	
	/**
	 * Deprecated, use {@link #removeAt(Locality)}
	 * @param flocation
	 */
	@Deprecated
	public abstract void removeAt(FLocation flocation);
	
	/**
	 * Deprecated, use {@link #getFactionCoordCountInWorld(Faction, World)}<br>
	 * For removal 10/2017
	 * @param faction
	 * @param worldName
	 * @return
	 */
	@Deprecated
	public abstract int getFactionCoordCountInWorld(Faction faction, String worldName);
	
	/**
	 * Deprecated, use {@link #getMap(Faction, Locality, double)}<br>
	 * For removal 10/2017
	 * @param faction
	 * @param flocation
	 * @param inDegrees
	 * @return
	 */
	@Deprecated
	public abstract ArrayList<String> getMap(Faction faction, FLocation flocation, double inDegrees);

	/**
	 * Deprecated, use {@link #clearOwnershipAt(Locality)}
	 * @param flocation
	 */
	@Deprecated
	public abstract void clearOwnershipAt(FLocation flocation);

	/**
	 * Deprecated, use {@link #isBorderLocation(Locality)}<br>
	 * For removal 10/2017
	 * @param flocation
	 * @return
	 */
	@Deprecated
	public abstract boolean isBorderLocation(FLocation flocation);
	
	/**
	 * Deprecated, use {@link #isConnectedLocation(Locality, Faction)}<br>
	 * For removal 10/2017
	 * @param flocation
	 * @param faction
	 * @return
	 */
	@Deprecated
	public abstract boolean isConnectedLocation(FLocation flocation, Faction faction);
	
	/**
	 * Deprecated, use {@link #hasFactionWithin(Locality, Faction, int)}<br>
	 * For removal 10/2017
	 * @param flocation
	 * @param faction
	 * @param radius
	 * @return
	 */
	@Deprecated
	public abstract boolean hasFactionWithin(FLocation flocation, Faction faction, int radius);
	
}
