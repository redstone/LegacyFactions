package net.redstoneore.legacyfactions.entity;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import net.redstoneore.legacyfactions.ChatMode;
import net.redstoneore.legacyfactions.EconomyParticipator;
import net.redstoneore.legacyfactions.FLocation;
import net.redstoneore.legacyfactions.Relation;
import net.redstoneore.legacyfactions.RelationParticipator;
import net.redstoneore.legacyfactions.Role;
import net.redstoneore.legacyfactions.entity.persist.memory.MemoryFPlayer;
import net.redstoneore.legacyfactions.event.EventFactionsLandChange;
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
	
	void login();

	void logout();

	Faction getFaction();

	String getFactionId();

	boolean hasFaction();

	void setFaction(Faction faction);

	boolean willAutoLeave();

	void setAutoLeave(boolean autoLeave);

	long getLastFrostwalkerMessage();

	void setLastFrostwalkerMessage();

	void setMonitorJoins(boolean monitor);

	boolean isMonitoringJoins();

	Role getRole();

	void setRole(Role role);

	double getPowerBoost();

	void setPowerBoost(double powerBoost);

	Faction getAutoClaimFor();

	void setAutoClaimFor(Faction faction);

	boolean isAutoSafeClaimEnabled();

	void setIsAutoSafeClaimEnabled(boolean enabled);

	boolean isAutoWarClaimEnabled();

	void setIsAutoWarClaimEnabled(boolean enabled);

	boolean isAdminBypassing();

	/**
	 * Deprecated! Use {@link #isVanished(FPlayer)}<br>
	 * To be removed after 09/2017
	 * @return
	 */
	@Deprecated
	boolean isVanished();
	
	/**
	 * Is vanished to a player
	 * @param FPlayer viewing
	 * @return true if appears vanished
	 */
	boolean isVanished(FPlayer viewer);

	void setIsAdminBypassing(boolean val);

	void setChatMode(ChatMode chatMode);

	ChatMode getChatMode();

	void setIgnoreAllianceChat(boolean ignore);

	boolean isIgnoreAllianceChat();

	void setSpyingChat(boolean chatSpying);

	boolean isSpyingChat();

	boolean showScoreboard();

	void setShowScoreboard(boolean show);

	// FIELD: account
	String getAccountId();

	void resetFactionData(boolean doSpoutUpdate);

	void resetFactionData();

	long getLastLoginTime();

	void setLastLoginTime(long lastLoginTime);

	boolean isMapAutoUpdating();

	void setMapAutoUpdating(boolean mapAutoUpdating);

	boolean hasLoginPvpDisabled();

	FLocation getLastStoodAt();

	void setLastStoodAt(FLocation flocation);

	String getTitle();

	void setTitle(String title);

	String getName();

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

	boolean canClaimForFaction(Faction forFaction);

	boolean canClaimForFactionAtLocation(Faction forFaction, Location location, boolean notifyFailure);

	boolean canClaimForFactionAtLocation(Faction forFaction, FLocation location, boolean notifyFailure);

	boolean attemptClaim(Faction forFaction, Location location, boolean notifyFailure, EventFactionsLandChange eventLandChange);
	boolean attemptClaim(Faction forFaction, FLocation location, boolean notifyFailure, EventFactionsLandChange eventLandChange);

	void sendMessage(String str, Object... args);
	
	void sendMessage(boolean onlyIfTrue, String str, Object... args);

	String getId();

	Player getPlayer();

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
	
	// ----------------------------------------
	// UTIL
	// ----------------------------------------
	
	MemoryFPlayer asMemoryFPlayer();
	
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


}
