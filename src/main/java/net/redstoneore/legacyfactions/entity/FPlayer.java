package net.redstoneore.legacyfactions.entity;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import net.redstoneore.legacyfactions.EconomyParticipator;
import net.redstoneore.legacyfactions.FLocation;
import net.redstoneore.legacyfactions.Relation;
import net.redstoneore.legacyfactions.RelationParticipator;
import net.redstoneore.legacyfactions.Role;
import net.redstoneore.legacyfactions.entity.persist.memory.MemoryFPlayer;
import net.redstoneore.legacyfactions.event.EventFactionsLandChange;
import net.redstoneore.legacyfactions.expansion.chat.ChatMode;
import net.redstoneore.legacyfactions.locality.Locality;
import net.redstoneore.legacyfactions.util.WarmUpUtil;

import java.util.List;


/**
 * Logged in players always have exactly one FPlayer instance. Logged out players may or may not have an FPlayer
 * instance. They will always have one if they are part of a faction. This is because only players with a faction are
 * saved to disk (in order to not waste disk space).
 * <p/>
 * The FPlayer is linked to a minecraft player using the player name.
 * <p/>
 * The same instance is always returned for the same player. This means you can use the == operator. No .equals method
 * necessary.
 */
public interface FPlayer extends EconomyParticipator {
	
	// -------------------------------------------------- //
	// PLAYER
	// -------------------------------------------------- //
	
	/**
	 * Returns the player id
	 * @return player id
	 */
	String getId();

	/**
	 * Returns the player name
	 * @return player name
	 */
	String getName();

	/**
	 * Gets the Player object 
	 * @return Player object
	 */
	Player getPlayer();
	
	/**
	 * Get the economy account id
	 */
	String getAccountId();

	/**
	 * Returns the item in the players main hand
	 * @return the item in the players main hand
	 */
	ItemStack getItemInMainHand();
	
	/**
	 * Returns the item in the players off hand
	 * @return the item in the players off hand, or an ItemStack with Material.AIR if the server
	 *         doesn't support it.
	 */
	ItemStack getItemInOffHand();
	
	/**
	 * Teleport a player to a {@link Locality}
	 * @param {@link Locality}
	 */
	void teleport(Locality locality);

	// -------------------------------------------------- //
	// FACTION
	// -------------------------------------------------- //
	
	/**
	 * Get faction this player is in
	 * @return faction they're in, returns wilderness if none 
	 */
	Faction getFaction();

	/**
	 * Set the faction this player is in
	 * @param faction to set
	 */
	void setFaction(Faction faction);

	/**
	 * Get the id of the faction this player is in
	 * @return id of the faction this player is in
	 */
	String getFactionId();
	
	/**
	 * Check if player has a faction
	 * @return true if the player is in a faction
	 */
	boolean hasFaction();
	
	/**
	 * Get player Role
	 * @return Role of player
	 */
	Role getRole();

	/**
	 * Set the Rol of the player
	 * @param new Role
	 */
	void setRole(Role role);
	
	boolean canAdminister(FPlayer who);

	// -------------------------------------------------- //
	// MISC METHODS
	// -------------------------------------------------- //

	boolean hasPermission(String permission);
		
	// auto leave
	boolean willAutoLeave();
	void setAutoLeave(boolean autoLeave);

	// frost walker
	long getLastFrostwalkerMessage();
	void setLastFrostwalkerMessage();

	// auto claim
	Faction getAutoClaimFor();
	void setAutoClaimFor(Faction faction);
	
	// monitoring joines
	boolean isMonitoringJoins();
	void setMonitorJoins(boolean monitor);

	// auto safe claim
	boolean isAutoSafeClaimEnabled();
	void setIsAutoSafeClaimEnabled(boolean enabled);

	// auto war claim
	boolean isAutoWarClaimEnabled();
	void setIsAutoWarClaimEnabled(boolean enabled);

	// admin bypass 
	boolean isAdminBypassing();
	void setIsAdminBypassing(boolean enabled);
	
	/**
	 * Is vanished to a player
	 * @param FPlayer viewing
	 * @return true if appears vanished
	 */
	boolean isVanished(FPlayer viewer);
	
	ChatMode getChatMode();
	void setChatMode(ChatMode chatMode);

	boolean isIgnoreAllianceChat();
	void setIgnoreAllianceChat(boolean ignore);

	boolean isSpyingChat();
	void setSpyingChat(boolean chatSpying);

	boolean showScoreboard();
	void setShowScoreboard(boolean show);

	// FIELD: account
	void resetFactionData();

	long getLastLoginTime();

	void setLastLoginTime(long lastLoginTime);

	boolean isMapAutoUpdating();

	void setMapAutoUpdating(boolean mapAutoUpdating);

	boolean hasLoginPvpDisabled();

	FLocation getLastStoodAt();

	/**
	 * Fetch the last known location of this player.
	 * @return last known location in a {@link Locality} for this player.
	 */
	Locality getLastLocation();
	
	void setLastStoodAt(FLocation flocation);

	String getTitle();

	void setTitle(String title);

	String getTag();

	// Base concatenations:

	String getNameAndSomething(String something);

	String getNameAndTitle();

	String getNameAndTag();

	// Colored concatenations:
	// These are used in information messages

	String getNameAndTitle(Faction faction);

	String getNameAndTitle(FPlayer fplayer);

	// Chat Tag:
	// These are injected into the format of global chat messages.

	String getChatTag();

	// Colored Chat Tag
	String getChatTag(Faction faction);

	String getChatTag(FPlayer fplayer);

	int getKills();

	int getDeaths();

	
	// ----------------------------------------
	// RELATION
	// ----------------------------------------

	@Override
	String describeTo(RelationParticipator that, boolean ucfirst);

	@Override
	String describeTo(RelationParticipator that);

	@Override
	Relation getRelationTo(RelationParticipator rp);

	@Override
	Relation getRelationTo(RelationParticipator rp, boolean ignorePeaceful);

	Relation getRelationToLocation();

	@Override
	ChatColor getColorTo(RelationParticipator rp);

	// ----------------------------------------
	// HEALTH
	// ----------------------------------------
	void heal(int amnt);


	// ----------------------------------------
	// POWER
	// ----------------------------------------
	double getPower();

	void alterPower(double delta);

	double getPowerMax();

	double getPowerMin();

	int getPowerRounded();

	int getPowerMaxRounded();

	int getPowerMinRounded();

	void updatePower();

	void losePowerFromBeingOffline();

	double getPowerBoost();

	void setPowerBoost(double powerBoost);

	void onDeath();
	void onDeath(double powerLoss);

	// ----------------------------------------
	// TERRITORY
	// ----------------------------------------
	
	boolean isInOwnTerritory();

	boolean isInOthersTerritory();

	boolean isInAllyTerritory();

	boolean isInNeutralTerritory();

	boolean isInEnemyTerritory();

	void sendFactionHereMessage(Faction from);

	// ----------------------------------------
	// ACTIONS
	// ----------------------------------------

	void leave(boolean makePay);
	
	void leave(boolean makePay, boolean silent);

	boolean canClaimForFaction(Faction forFaction);

	boolean canClaimForFactionAtLocation(Faction forFaction, Location location, boolean notifyFailure);
	boolean canClaimForFactionAtLocation(Faction forFaction, FLocation location, boolean notifyFailure);
	boolean canClaimForFactionAtLocation(Faction forFaction, Locality location, boolean notifyFailure);
	
	boolean attemptClaim(Faction forFaction, Location location, boolean notifyFailure, boolean notifySuccess);
	boolean attemptClaim(Faction forFaction, Locality location, boolean notifyFailure, boolean notifySuccess);
	
	void sendMessage(String str, Object... args);
	
	void sendMessage(boolean onlyIfTrue, String str, Object... args);

	boolean isOnline();

	void sendMessage(String message);

	void sendMessage(List<String> messages);

	boolean isOnlineAndVisibleTo(Player me);

	void remove();

	boolean isOffline();

	void setId(String id);

	// ----------------------------------------
	// WARMUPS
	// ----------------------------------------

	boolean isWarmingUp();

	WarmUpUtil.Warmup getWarmupType();

	void addWarmup(WarmUpUtil.Warmup warmup, int taskId);

	void stopWarmup();

	void clearWarmup();
	
	// -------------------------------------------------- //
	// EVENTS
	// -------------------------------------------------- //

	void onLogin();

	void onLogout();

	// -------------------------------------------------- //
	// UTIL
	// -------------------------------------------------- //
	
	MemoryFPlayer asMemoryFPlayer();
	
	boolean canAffordCommand(double econCostJoin, String string);
	
	boolean payForCommand(double cost, String toDoThis, String forDoingThis);
	
	// -------------------------------------------------- //
	// DEPRECATED
	// -------------------------------------------------- //
	
	/**
	 * Deprecated, use sendMessage
	 */
	@Deprecated
	void msg(String str, Object... args);
	
	/**
	 * Deprecated, use sendMessage
	 */
	@Deprecated
	void msg(boolean onlyIfTrue, String str, Object... args);

	/**
	 * Deprecated, use onLogin
	 */
	@Deprecated
	default void login() {
		onLogin();
	}
	
	/**
	 * Deprecated, use onLogout
	 */
	@Deprecated
	default void logout() {
		onLogout();
	}
	
	/**
	 * Deprecated! Use {@link #isVanished(FPlayer)}<br>
	 * To be removed after 09/2017
	 * @return
	 */
	@Deprecated
	boolean isVanished();

	/**
	 * Deprecated, spout update no longer included
	 */
	@Deprecated
	void resetFactionData(boolean doSpoutUpdate);
	
	@Deprecated
	boolean attemptClaim(Faction forFaction, Location location, boolean notifyFailure, EventFactionsLandChange eventLandChange);
	
	@Deprecated
	boolean attemptClaim(Faction forFaction, FLocation location, boolean notifyFailure, EventFactionsLandChange eventLandChange);
	
	@Deprecated
	boolean attemptClaim(Faction forFaction, Locality location, boolean notifyFailure, EventFactionsLandChange eventLandChange);

}
