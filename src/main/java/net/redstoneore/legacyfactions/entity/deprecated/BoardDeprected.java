package net.redstoneore.legacyfactions.entity.deprecated;

import java.util.ArrayList;
import java.util.Set;

import org.bukkit.Bukkit;
import org.bukkit.World;

import net.redstoneore.legacyfactions.FLocation;
import net.redstoneore.legacyfactions.entity.Faction;
import net.redstoneore.legacyfactions.entity.Board;
import net.redstoneore.legacyfactions.locality.Locality;
import net.redstoneore.legacyfactions.locality.LocalityLazy;
import net.redstoneore.legacyfactions.mixin.DebugMixin;

public interface BoardDeprected {
	// -------------------------------------------------- //
	// DEPRECATED
	// -------------------------------------------------- //
	
	/**
	 * Deprecated, use {@link #getIdAt(Locality)}
	 * @param flocation
	 * @return
	 */
	@Deprecated
	default public String getIdAt(FLocation flocation) {
		DebugMixin.deprecatedWarning("Board#getIdAt(FLocation)", "Board#getIdAt(Locality)");
		return ((Board)this).getIdAt(LocalityLazy.of(flocation.getWorldName(), (int) flocation.getX(), (int) flocation.getZ()));
	}
	
	/**
	 * Deprecated, use {@link #getFactionAt(Locality)}
	 * @param flocation
	 * @return
	 */
	@Deprecated
	default public Faction getFactionAt(FLocation flocation) {
		DebugMixin.deprecatedWarning("Board#getFactionAt(FLocation)", "Board#getFactionAt(Locality)");
		return ((Board)this).getFactionAt(LocalityLazy.of(flocation.getWorldName(), (int) flocation.getX(), (int) flocation.getZ()));
	}
	
	/**
	 * Deprecated, use {@link #setFactionAt(Faction, Locality)}
	 * @param id
	 * @param flocation
	 */
	@Deprecated
	default public void setIdAt(String id, FLocation flocation) {
		DebugMixin.deprecatedWarning("Board#setIdAt(String, FLocation)", "Board#setIdAt(String, Locality)");
		((Board)this).setIdAt(id, LocalityLazy.of(flocation.getWorldName(), (int) flocation.getX(), (int) flocation.getZ()));
	}

	/**
	 * Deprecated, use {@link #setFactionAt(Faction, Locality)}
	 * @param faction
	 * @param flocation
	 */
	default public void setFactionAt(Faction faction, FLocation flocation) {
		DebugMixin.deprecatedWarning("Board#setFactionAt(Faction, FLocation)", "Board#setFactionAt(Faction, Locality)");
		((Board)this).setFactionAt(faction, LocalityLazy.of(flocation.getWorldName(), (int) flocation.getX(), (int) flocation.getZ()));
	}
	
	/**
	 * Deprecated, use {@link #removeAt(Locality)}
	 * @param flocation
	 */
	@Deprecated
	default public void removeAt(FLocation flocation) {
		DebugMixin.deprecatedWarning("Board#removeAt(FLocation)", "Board#removeAt(Locality)");
		((Board)this).removeAt(LocalityLazy.of(flocation.getWorldName(), (int) flocation.getX(), (int) flocation.getZ()));
	}
	
	/**
	 * Deprecated, use {@link #getFactionCoordCountInWorld(Faction, World)}<br>
	 * For removal 10/2017
	 * @param faction
	 * @param worldName
	 * @return
	 */
	@Deprecated
	default public int getFactionCoordCountInWorld(Faction faction, String worldName) {
		DebugMixin.deprecatedWarning("Board#getFactionCoordCountInWorld(Faction, String)", "Board#getFactionCoordCountInWorld(Faction, World)");
		return ((Board)this).getFactionCoordCountInWorld(faction, Bukkit.getWorld(worldName));
	}
	
	/**
	 * Deprecated, use {@link #getMap(Faction, Locality, double)}<br>
	 * For removal 10/2017
	 * @param faction
	 * @param flocation
	 * @param inDegrees
	 * @return
	 */
	@Deprecated
	default public ArrayList<String> getMap(Faction faction, FLocation flocation, double inDegrees) {
		DebugMixin.deprecatedWarning("Board#getMap(Faction, FLocation, double)", "Board#getMap(Faction, Locality, double)");
		return ((Board)this).getMap(faction, LocalityLazy.of(flocation.getWorldName(), (int) flocation.getX(), (int) flocation.getZ()), inDegrees);
	}

	/**
	 * Deprecated, use {@link Faction#ownership}
	 * @param flocation
	 */
	@Deprecated
	default public void clearOwnershipAt(FLocation flocation) {
		DebugMixin.deprecatedWarning("Board#clearOwnershipAt(Faction, FLocation, double)", "Faction#ownership");
		Board.get().getFactionAt(flocation).ownership().clearAt(LocalityLazy.of(flocation.getWorldName(), (int) flocation.getX(), (int) flocation.getZ()));
	}

	/**
	 * Deprecated, use {@link #isBorderLocation(Locality)}<br>
	 * For removal 10/2017
	 * @param flocation
	 * @return
	 */
	@Deprecated
	default public boolean isBorderLocation(FLocation flocation) {
		DebugMixin.deprecatedWarning("Board#isBorderLocation(FLocation)", "Board#isBorderLocation(Locality)");
		return ((Board)this).isBorderLocation(LocalityLazy.of(flocation.getWorldName(), (int) flocation.getX(), (int) flocation.getZ()));
	}
	
	/**
	 * Deprecated, use {@link #isConnectedLocation(Locality, Faction)}<br>
	 * For removal 10/2017
	 * @param flocation
	 * @param faction
	 * @return
	 */
	@Deprecated
	default public boolean isConnectedLocation(FLocation flocation, Faction faction) {
		DebugMixin.deprecatedWarning("Board#isConnectedLocation(FLocation, Faction)", "Board#isBorderLocation(Locality, Faction)");
		return ((Board)this).isConnectedLocation(LocalityLazy.of(flocation.getWorldName(), (int) flocation.getX(), (int) flocation.getZ()), faction);
	}
	
	/**
	 * Deprecated, use {@link #hasFactionWithin(Locality, Faction, int)}<br>
	 * For removal 10/2017
	 * @param flocation
	 * @param faction
	 * @param radius
	 * @return
	 */
	@Deprecated
	default public boolean hasFactionWithin(FLocation flocation, Faction faction, int radius) {
		DebugMixin.deprecatedWarning("Board#hasFactionWithin(FLocation, Faction, int)", "Board#hasFactionWithin(Locality, Faction, int)");
		return ((Board)this).hasFactionWithin(LocalityLazy.of(flocation.getWorldName(), (int) flocation.getX(), (int) flocation.getZ()), faction, radius);
	}
	
	/**
	 * Deprecated, use {@link #getAll(String)}<br>
	 * For removal 11/2017
	 * @return	
	 */
	@Deprecated
	public Set<FLocation> getAllClaims(String factionId);

	/**
	 * Deprecated, use {@link #getAll(Faction)}<br>
	 * For removal 11/2017
	 */
	@Deprecated
	public Set<FLocation> getAllClaims(Faction faction);

	/**
	 * Deprecated, use {@link #getAll()}<br>
	 * For removal 11/2017
	 */
	@Deprecated
	public Set<FLocation> getAllClaims();
	
}
