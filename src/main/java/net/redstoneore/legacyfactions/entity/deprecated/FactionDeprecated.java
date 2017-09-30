package net.redstoneore.legacyfactions.entity.deprecated;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.bukkit.Bukkit;
import org.bukkit.World;

import net.redstoneore.legacyfactions.FLocation;
import net.redstoneore.legacyfactions.Role;
import net.redstoneore.legacyfactions.entity.FPlayer;
import net.redstoneore.legacyfactions.entity.Faction;
import net.redstoneore.legacyfactions.flag.Flags;
import net.redstoneore.legacyfactions.locality.Locality;
import net.redstoneore.legacyfactions.mixin.DebugMixin;

/**
 * Deprecated methods for the {@link Faction} object.
 */
public interface FactionDeprecated {
	
	/**
	 * Deprecated, use {@link Faction#getOwner()}
	 */
	@Deprecated
	default FPlayer getFPlayerAdmin() {
		DebugMixin.deprecatedWarning("Faction#getFPlayerAdmin", "Faction#getOwner");
		return ((Faction)this).getOwner();
	}

	/**
	 * Deprecated, use {@link Faction#getWhereOnline(boolean)}
	 */
	@Deprecated
	default Set<FPlayer> getFPlayersWhereOnline(boolean online) {
		DebugMixin.deprecatedWarning("Faction#getFPlayersWhereOnline(boolean)", "Faction#getWhereOnline(boolean)");
		return ((Faction)this).getWhereOnline(online);
	}

	/**
	 * Deprecated, use {@link Faction#isWilderness()}
	 */
	@Deprecated
	default boolean isNone() {
		DebugMixin.deprecatedWarning("Faction#isNone", "Faction#isWilderness");
		return ((Faction)this).isWilderness();
	}

	/**
	 * Deprecated, use {@link Faction#getLandRoundedInWorld(World)}
	 */
	@Deprecated
	default int getLandRoundedInWorld(String worldName) {
		DebugMixin.deprecatedWarning("Faction#getLandRoundedInWorld(String)", "Faction#getLandRoundedInWorld(World)");
		return ((Faction)this).getLandRoundedInWorld(Bukkit.getWorld(worldName));
	}
		
	/**
	 * Deprecated, use {@link Faction#getMembers} 
	 */
	default Set<FPlayer> getFPlayers() {
		DebugMixin.deprecatedWarning("Faction#getFPlayers()", "Faction#getMembers()");
		return ((Faction)this).getMembers();
	}
	
	/**
	 * Deprecated, use {@link Faction#getFlag}(Flags.OPEN)
	 */
	@Deprecated
	default boolean getOpen() {
		DebugMixin.deprecatedWarning("Faction#getOpen()", "Faction#getFlag(Flags.OPEN)");
		return ((Faction)this).getFlag(Flags.OPEN);
	}

	/**
	 * Deprecated, use {@link Faction#setFlag}(Flags.OPEN, boolean)
	 */
	@Deprecated
	default void setOpen(boolean isOpen) {
		DebugMixin.deprecatedWarning("Faction#setOpen(boolean)", "Faction#setFlag(Flags.OPEN, boolean)");
		((Faction)this).setFlag(Flags.OPEN, isOpen);
	}
	
	/**
	 * Deprecated, use {@link Faction#announcements()}
	 * @return
	 */
	@Deprecated
	Map<String, List<String>> getAnnouncements();
  
	/**
	 * Deprecated, use {@link Faction#announcements()}
	 */
	@Deprecated
	void addAnnouncement(FPlayer fPlayer, String msg);
	
	/**
	 * Deprecated, use {@link Faction#announcements()}
	 */
	@Deprecated
	void sendUnreadAnnouncements(FPlayer fPlayer);

	/**
	 * Deprecated, use {@link Faction#announcements()}
	 */
	@Deprecated
	void removeAnnouncements(FPlayer fPlayer);

	/**
	 * Deprecated 14th of 09/2017. Use {@link Faction#uninvite(FPlayer)}<br>
	 * Reason: deinvite is not a word<br>
	 * For removal: 14th of 11/2017
	 */
	@Deprecated
	default void deinvite(FPlayer fplayer) {
		DebugMixin.deprecatedWarning("Faction#deinvite(FPlayer)", "Faction#uninvite(FPlayer)");
		((Faction)this).uninvite(fplayer);
	}

	/**
	 * Deprecated 14th of 09/2017. Use {@link Faction#getFlag}(Flags.PEACEFUL)<br>
	 * Reason: data backend change<br>
	 * For removal: 14th of 11/2017
	 */
	@Deprecated
	default boolean isPeaceful() {
		DebugMixin.deprecatedWarning("Faction#isPeaceful()", "Faction#getFlag(Flags.PEACEFUL)");
		return ((Faction)this).getFlag(Flags.PEACEFUL);
	}

	/**
	 * Deprecated 14th of 09/2017. Use {@link Faction#setFlag}(Flags.PEACEFUL, Boolean)<br>
	 * Reason: data backend change<br>
	 * For removal: 14th of 11/2017
	 */
	@Deprecated
	default void setPeaceful(boolean isPeaceful) {
		DebugMixin.deprecatedWarning("Faction#setPeaceful(boolean)", "Faction#setFlag(Flags.PEACEFUL, boolean)");
		((Faction)this).setFlag(Flags.PEACEFUL, isPeaceful);
	}

	/**
	 * Deprecated 14th of 09/2017. Use {@link Faction#setFlag}(Flags.EXPLOSIONS, Boolean)<br>
	 * Reason: data backend change<br>
	 * For removal: 14th of 11/2017
	 */
	@Deprecated
	default void setPeacefulExplosionsEnabled(boolean val) {
		DebugMixin.deprecatedWarning("Faction#setPeacefulExplosionsEnabled(boolean)", "Faction#setFlag(Flags.EXPLOSIONS, boolean)");
		((Faction)this).setFlag(Flags.EXPLOSIONS, val);
	}

	/**
	 * Deprecated 14th of 09/2017. Use {@link Faction#getFlag}(Flags.EXPLOSIONS)<br>
	 * Reason: data backend change<br>
	 * For removal: 14th of 11/2017
	 */
	@Deprecated
	default boolean getPeacefulExplosionsEnabled() {
		DebugMixin.deprecatedWarning("Faction#getPeacefulExplosionsEnabled()", "Faction#getFlag(Flags.EXPLOSIONS)");
		return ((Faction)this).getFlag(Flags.EXPLOSIONS);
	}

	/**
	 * Deprecated 14th of 09/2017. Use {@link Faction#getFlag}(Flags.EXPLOSIONS)<br>
	 * Reason: data backend change<br>
	 * For removal: 14th of 11/2017
	 */
	@Deprecated
	default boolean noExplosionsInTerritory() {
		DebugMixin.deprecatedWarning("Faction#noExplosionsInTerritory()", "Faction#getFlag(Flags.EXPLOSIONS)");
		return ((Faction)this).getFlag(Flags.EXPLOSIONS);
	}
	
	/**
	 * Deprecated 14th of 09/2017. Use {@link Faction#getFlag}(Flags.PERMANENT)<br>
	 * Reason: data backend change<br>
	 * For removal: 14th of 11/2017
	 */
	@Deprecated
	default boolean isPermanent() {
		DebugMixin.deprecatedWarning("Faction#isPermanent()", "Faction#getFlag(Flags.PERMANENT)");
		return ((Faction)this).getFlag(Flags.PERMANENT);
	}

	/**
	 * Deprecated 14th of 09/2017. Use {@link Faction#setFlag}(Flags.PERMANENT, Boolean)<br>
	 * Reason: data backend change<br>
	 * For removal: 14th of 11/2017
	 */
	@Deprecated
	default void setPermanent(boolean isPermanent) {
		DebugMixin.deprecatedWarning("Faction#setPermanent(boolean)", "Faction#setFlag(Flags.PERMANENT, boolean)");
		((Faction)this).setFlag(Flags.PERMANENT, isPermanent);
	}
	
	/**
	 * Deprecated 14th of 09/2017. Use {@link Faction#getWhereRole(Role)}<br>
	 * Reason: simplicity changes<br>
	 * For removal: 14th of 11/2017
	 */
	@Deprecated
	default ArrayList<FPlayer> getFPlayersWhereRole(Role role) {
		DebugMixin.deprecatedWarning("Faction#getFPlayersWhereRole(Role)", "Faction#getWhereRole(Role)");
		return (ArrayList<FPlayer>) ((Faction)this).getWhereRole(role);
	}
	
	/**
	 * Deprecated 14th of 09/2017. Use {@link Faction#memberAdd(FPlayer)}<br>
	 * Reason: simplicity changes<br>
	 * For removal: 14th of 11/2017
	 */
	@Deprecated
	default boolean addFPlayer(FPlayer fplayer) {
		DebugMixin.deprecatedWarning("Faction#addFPlayer(FPlayer)", "Faction#memberAdd(FPlayer)");
		return ((Faction)this).memberAdd(fplayer);
	}
	
	/**
	 * Deprecated 14th of 09/2017. Use {@link Faction#memberRemove(FPlayer)}<br>
	 * Reason: simplicity changes<br>
	 * For removal: 14th of 11/2017
	 */
	@Deprecated
	default boolean removeFPlayer(FPlayer fplayer) {
		DebugMixin.deprecatedWarning("Faction#removeFPlayer(FPlayer)", "Faction#memberRemove(FPlayer)");
		return ((Faction)this).memberRemove(fplayer);
	}
	
	/**
	 * Deprecated 14th of 09/2017. Use {@link Faction#memberCount()}<br>
	 * Reason: simplicity changes<br>
	 * For removal: 14th of 11/2017
	 */
	@Deprecated
	default 	int getSize() {
		DebugMixin.deprecatedWarning("Faction#getSize()", "Faction#memberCount()");
		return ((Faction)this).memberCount();
	}
	
	/**
	 * Deprecated 14th of 09/2017. Use {@link Faction#memberRefresh()}<br>
	 * Reason: simplicity changes<br>
	 * For removal: 14th of 11/2017
	 */
	@Deprecated
	default void refreshFPlayers() {
		DebugMixin.deprecatedWarning("Faction#refreshFPlayers()", "Faction#memberRefresh()");
		((Faction)this).memberRefresh();
	}
	
	/**
	 * Deprecated 14th of 09/2017. Use {@link Faction#ownership()}<br>
	 * Reason: new ownership abstract API<br>
	 * For removal: 14th of 11/2017
	 */
	@Deprecated
	Map<FLocation, Set<String>> getClaimOwnership();

	/**
	 * Deprecated 14th of 09/2017. Use {@link Faction#ownership()}<br>
	 * Reason: new ownership abstract API<br>
	 * For removal: 14th of 11/2017
	 */
	@Deprecated
	void clearAllClaimOwnership();

	/**
	 * Deprecated 14th of 09/2017. Use {@link Faction#ownership()}<br>
	 * Reason: new ownership abstract API<br>
	 * For removal: 14th of 11/2017
	 */
	@Deprecated
	void clearClaimOwnership(Locality locality);
	
	/**
	 * Deprecated 14th of 09/2017. Use {@link Faction#ownership()}<br>
	 * Reason: new ownership abstract API<br>
	 * For removal: 14th of 11/2017
	 */
	@Deprecated
	void clearClaimOwnership(FLocation loc);
	
	/**
	 * Deprecated 14th of 09/2017. Use {@link Faction#ownership()}<br>
	 * Reason: new ownership abstract API<br>
	 * For removal: 14th of 11/2017
	 */
	@Deprecated
	void clearClaimOwnership(FPlayer player);

	/**
	 * Deprecated 14th of 09/2017. Use {@link Faction#ownership()}<br>
	 * Reason: new ownership abstract API<br>
	 * For removal: 14th of 11/2017
	 */
	@Deprecated
	int getCountOfClaimsWithOwners();

	/**
	 * Deprecated 14th of 09/2017. Use {@link Faction#ownership()}<br>
	 * Reason: new ownership abstract API<br>
	 * For removal: 14th of 11/2017
	 */
	@Deprecated
	boolean doesLocationHaveOwnersSet(FLocation loc);

	/**
	 * Deprecated 14th of 09/2017. Use {@link Faction#ownership()}<br>
	 * Reason: new ownership abstract API<br>
	 * For removal: 14th of 11/2017
	 */
	@Deprecated
	boolean isPlayerInOwnerList(FPlayer player, FLocation loc);

	/**
	 * Deprecated 14th of 09/2017. Use {@link Faction#ownership()}<br>
	 * Reason: new ownership abstract API<br>
	 * For removal: 14th of 11/2017
	 */
	@Deprecated
	void setPlayerAsOwner(FPlayer player, FLocation loc);

	/**
	 * Deprecated 14th of 09/2017. Use {@link Faction#ownership()}<br>
	 * Reason: new ownership abstract API<br>
	 * For removal: 14th of 11/2017
	 */
	@Deprecated
	void removePlayerAsOwner(FPlayer player, FLocation loc);

	/**
	 * Deprecated 14th of 09/2017. Use {@link Faction#ownership()}<br>
	 * Reason: new ownership abstract API<br>
	 * For removal: 14th of 11/2017
	 */
	@Deprecated
	Set<String> getOwnerList(FLocation loc);

	/**
	 * Deprecated 14th of 09/2017. Use {@link Faction#ownership()}<br>
	 * Reason: new ownership abstract API<br>
	 * For removal: 14th of 11/2017
	 */
	@Deprecated
	String getOwnerListString(FLocation loc);

	/**
	 * Deprecated 14th of 09/2017. Use {@link Faction#ownership()}<br>
	 * Reason: new ownership abstract API<br>
	 * For removal: 14th of 11/2017
	 */
	@Deprecated
	boolean playerHasOwnershipRights(FPlayer fplayer, FLocation loc);

	/**
	 * Deprecated 14th of 09/2017. Use {@link Faction#ownership()}<br>
	 * Reason: use locality<br>
	 * For removal: 29th of 12/2017
	 */
	@Deprecated
	default Set<FLocation> getAllClaims() {
		return ((Faction)this).getClaims().stream()
				.map(locality -> FLocation.valueOf(locality.getChunk()))
				.collect(Collectors.toSet());
	}

}
