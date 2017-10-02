package net.redstoneore.legacyfactions.entity;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;

import net.redstoneore.legacyfactions.EconomyParticipator;
import net.redstoneore.legacyfactions.Relation;
import net.redstoneore.legacyfactions.RelationParticipator;
import net.redstoneore.legacyfactions.Role;
import net.redstoneore.legacyfactions.announcement.Announcement;
import net.redstoneore.legacyfactions.announcement.Announcements;
import net.redstoneore.legacyfactions.flag.Flag;
import net.redstoneore.legacyfactions.flag.Flags;
import net.redstoneore.legacyfactions.ownership.FactionOwnership;
import net.redstoneore.legacyfactions.util.LazyLocation;
import net.redstoneore.legacyfactions.warp.FactionWarp;
import net.redstoneore.legacyfactions.warp.FactionWarps;
import net.redstoneore.legacyfactions.integration.playervaults.PlayerVaultsIntegration;
import net.redstoneore.legacyfactions.locality.Locality;
import net.redstoneore.legacyfactions.config.Config;
import net.redstoneore.legacyfactions.entity.deprecated.FactionDeprecated;
import net.redstoneore.legacyfactions.event.EventFactionsRelationshipsCapped;

/**
 * Each faction has exactly one Faction object. However, these objects change if the backend is
 * switched. To ensure they match either use the {@link #equals(Object)} method or compare using
 * the faction id from {@link #getId()}.<br>
 * <br>
 * All maps returned are always a snapshot so do not depend on them.
 */
public interface Faction extends EconomyParticipator, FactionDeprecated {
	
	// -------------------------------------------------- //
	// FACTION
	// -------------------------------------------------- //

	/**
	 * Each faction is given a unique id. This can not be changed.
	 * 
	 * @return This faction id.
	 */
	String getId();

	/**
	 * Each faction can have a changeable display named called a tag.
	 * 
	 * @return The tag.
	 */
	String getTag();

	/**
	 * Returns the result of {@link #getTag()} but with a prefix.
	 * 
	 * @param prefix The prefix to add.
	 * 
	 * @return The tag, with the prefix specified.
	 */
	String getTag(String prefix);
	
	/**
	 * Returns the result of {@link #getTag()} but with the relation colour to the other 
	 * faction specified.
	 * 
	 * @param otherFaction Other faction get relation colour of.
	 * 
	 * @return The tag, with the relation colour.
	 */
	String getTag(Faction otherFaction);

	/**
	 * Returns the result of {@link #getTag()} but with the relation colour to a player specified. 
	 * 
	 * @param player Player to get relation colour of.
	 * 
	 * @return The tag, with the relation colour.
	 */
	String getTag(FPlayer player);

	/**
	 * Set the tag of this faction
	 * 
	 * @param tag Tag to set to.
	 */
	void setTag(String tag);

	/**
	 * To ease with matching you can get a comparison tag which will return a comparable version of
	 * this factions tag.
	 * 
	 * @return The comparison tag.
	 */
	String getComparisonTag();

	/**
	 * Each faction can set a description to describe their faction.
	 * 
	 * @return The faction description.
	 */
	String getDescription();

	/**
	 * Set the faction description.
	 * 
	 * @param description The description to set.
	 */
	void setDescription(String description);
	
	/**
	 * Check if there should be no creeper explosions at a location. This checks against peaceful
	 * and flags.
	 * 
	 * @param location The {@link Location} to check.
	 * 
	 * @return true if there are no creeper explosions here.
	 */
	boolean noCreeperExplosions(Location location);
	
	
	void setHome(Location home);

	boolean hasHome();

	Location getHome(Boolean checkValid);
	
	Location getHome();
	
	LazyLocation getLazyHome();
	
	void confirmValidHome();

	String getAccountId();
	
	Set<Locality> getClaims();
	
	// -------------------------------------------------- //
	// FLAGS
	// -------------------------------------------------- //
	
	/**
	 * Each faction can have options enabled and disabled. These are called flags. 
	 * 
	 * @return A snapshot map of the flags and their values. 
	 * 
	 * @see Flag
	 * @see Flags
	 */
	Map<Flag, Boolean> getFlags();
	
	/**
	 * Set a flag value.
	 * @param flag Flag to set.
	 * @param value The value to set.
	 * 
	 * @return true if it was a success.
	 * 
	 * @see Flag
	 * @see Flags
	 */
	boolean setFlag(Flag flag, Boolean value);
	
	/**
	 * Get the value of a flag.
	 * 
	 * @param flag Flag to get value of.
	 * 
	 * @return the value of this flag
	 */
	boolean getFlag(Flag flag);
	
	// -------------------------------------------------- //
	// WARPS
	// -------------------------------------------------- //

	/**
	 * Returns the {@link FactionWarps} API for this faction.
	 * 
	 * @return {@link FactionWarps} API
	 * 
	 * @see FactionWarp
	 * @see FactionWarps
	 */
	FactionWarps warps();
	
	// -------------------------------------------------- //
	// VAULTS
	// -------------------------------------------------- //
	
	/**
	 * For our integration with vault style plugins players are usually limited. If this returns
	 * a value less than 0 then there is no limit. 0 indicates none. 
	 * 
	 * @return The maximum vaults.
	 */
	int getMaxVaults();

	/**
	 * Set the maximum amount of vaults for this faction. 0 indicates none. -1 indicates no limit.
	 * 
	 * @param maxVaults The maximum amount of vaults.
	 * 
	 * @see PlayerVaultsIntegration
	 */
	void setMaxVaults(int maxVaults);
	
	// -------------------------------------------------- //
	// INVITES
	// -------------------------------------------------- //

	/**
	 * Returns a set of player ids that are invited to this faction.
	 * 
	 * @return a snapshot set of the invites.
	 */
	Set<String> getInvites();
	
	/**
	 * Invite a player to the faction.
	 * 
	 * @param fplayer The {@link FPlayer} to invite.
	 */
	void invite(FPlayer fplayer);

	/**
	 * Uninvite a player from this faction.
	 * 
	 * @param fplayer The {@link FPlayer} to uninvite.
	 */
	void uninvite(FPlayer fplayer);

	/**
	 * Confirm if a player is invited to this faction.
	 * 
	 * @param fplayer The {@link FPlayer} to check.
	 * 
	 * @return true if the player is invited.
	 */
	boolean isInvited(FPlayer fplayer);
	
	// -------------------------------------------------- //
	// BANS
	// -------------------------------------------------- //

	/**
	 * Ban a player from joining this faction. 
	 * 
	 * @param fplayer The {@link FPlayer} to ban.
	 */
	void ban(FPlayer fplayer);
	
	/**
	 * Unban a player from joining this faction.
	 * 
	 * @param fplayer The {@link FPlayer} to unban.
	 */
	void unban(FPlayer fplayer);
	
	/**
	 * Confirm if a player is banned from this faction.
	 * 
	 * @param fplayer The {@link FPlayer} to check.
	 * 
	 * @return true if the player is banned.
	 */
	boolean isBanned(FPlayer fplayer);
	
	// -------------------------------------------------- //
	// STYLES
	// -------------------------------------------------- //
	
	/**
	 * Map characters from '/f map' can be changed per-faction. This will validate that this
	 * faction has a forced map character. By default, they don't.
	 * 
	 * @return true if there is a forced map character.
	 */
	boolean hasForcedMapCharacter();
	
	/**
	 * Map characters from '/f map' can be changed per-faction. This will set a map character, or
	 * pass null to removed it.
	 * 
	 * @param character The character to set it to.
	 */
	void setForcedMapCharacter(char character);
	
	/**
	 * Map characters from '/f map' can be changed per-faction. This will return the map character,
	 * or null if none exists
	 * 
	 * @return The character.
	 */
	Character getForcedMapCharacter();
	
	boolean hasForcedMapColour();
	
	void setForcedMapColour(ChatColor colour);
	
	ChatColor getForcedMapColour();

	// -------------------------------------------------- //
	// POWER
	// -------------------------------------------------- //

	/**
	 * Factions can have enforced permanent power. If this is set, the power of this faction will
	 * be locked at this value. Returns null if none is set.
	 * 
	 * @return The permanent power.
	 */
	Integer getPermanentPower();

	/**
	 * Factions can have enforced permanent power. This method allows setting that. If you want to
	 * turn off permanent power, pass null.
	 * 
	 * @param permanentPower The permanent power to set.
	 */
	void setPermanentPower(Integer permanentPower);

	/**
	 * Factions can have enforced permanent power. This method will confirm if this faction has it
	 * enabled.
	 * 
	 * @return true, if permanent power is set.
	 */
	boolean hasPermanentPower();

	/**
	 * Factions can be given a power boost. This method will return the power boost, or 0 if there
	 * is no power boost.
	 * 
	 * @return The power boost.
	 */
	double getPowerBoost();

	/**
	 * Factions can be given a power boost. This method will set the power boost. Set to 0 to 
	 * remove the power boost.
	 * 
	 * @param powerBoost The power boost.
	 */
	void setPowerBoost(double powerBoost);

	// -------------------------------------------------- //
	// COMPARATOR METHODS
	// -------------------------------------------------- //

	/**
	 * Comparator: Is PVP disable in this territory?
	 * 
	 * @return true if there is no PVP in this territory.
	 */
	boolean noPvPInTerritory();

	/**
	 * Comparator: Are monsters allowed in this territory?
	 * 
	 * @return true if monsters are not allowed in this territory.
	 */
	boolean noMonstersInTerritory();

	/**
	 * Comparator: Is this faction normal?
	 * 
	 * @return true if this is a normal faction.
	 */
	boolean isNormal();

	/**
	 * Comparator: Is this faction the wilderness?
	 * 
	 * @return true if this faction is the wilderness.
	 */
	boolean isWilderness();

	/**
	 * Comparator: Is this faction the safezone?
	 * 
	 * @return true if this faction is the safezone.
	 */
	boolean isSafeZone();

	/**
	 * Comparator: Is this faction the warzone?
	 * 
	 * @return true if this faction is the warzone.
	 */
	boolean isWarZone();

	/**
	 * Comparator: is this a player-free faction? (e.g. safezone, warzone)
	 * 
	 * @return true if this is a player-free faction.
	 */
	boolean isPlayerFreeType();

	/**
	 * Comparator: is this faction power frozen. This is a raid value.
	 * 
	 * @return true, if the faction power is frozen.
	 * 
	 * @see Config#raidablePowerFreeze
	 */
	boolean isPowerFrozen();
	
	// -------------------------------------------------- //
	// STATS
	// -------------------------------------------------- //
	
	/**
	 * Get the last death of a player in this faction.
	 * 
	 * @return the timestamp of the last death in this faction
	 */
	long getLastDeath();

	/**
	 * Set the last death of a player in this faction.
	 * 
	 * @param time The timestamp for the last death.
	 */
	void setLastDeath(long time);
	
	/**
	 * Get the total kills by this faction.
	 * 
	 * @return The total kills.
	 */
	int getKills();

	/**
	 * Get the total deaths by this faction.
	 * 
	 * @return The total deaths.
	 */
	int getDeaths();
	
	/**
	 * Get the founded date of this faction.
	 * 
	 * @return The founded date.
	 */
	long getFoundedDate();

	/**
	 * Set the founded date of this faction.
	 * 
	 * @param foundedDate The founded date.
	 */
	void setFoundedDate(long foundedDate);
	
	// -------------------------------------------------- //
	// AUTOKICK
	// -------------------------------------------------- //
	
	/**
	 * Set auto kick for this faction.
	 * @param days Days to set to.
	 */
	void setAutoKick(long days);
	
	/**
	 * Get auto kick for this faction.
	 * @return Days, or -1 to use server default.
	 */
	long getAutoKick();
	
	// -------------------------------------------------- //
	// EMBLEM
	// -------------------------------------------------- //
	
	/**
	 * Attempt to set the emblem
	 * @param emblem The emblem to set.
	 * @return true if it was set, false if it is already taken.
	 */
	boolean setEmblem(String emblem);
	
	/**
	 * Get the emblem.
	 * @return The emblem.
	 */
	String getEmblem();
	
	// -------------------------------------------------- //
	// ANNOUNCEMENTS
	// -------------------------------------------------- //
	
	/**
	 * Get the {@link Announcements} API for this faction.
	 * 
	 * @return The {@link Announcements} API
	 * 
	 * @see Announcement
	 * @see Announcements
	 */
	Announcements announcements();

	// -------------------------------------------------- //
	// OWNERSHIP
	// -------------------------------------------------- //

	/**
	 * Get the {@link FactionOwnership} API for this faction.
	 * 
	 * @return The {@link FactionOwnership} API
	 * 
	 * @see FactionOwnership
	 */
	FactionOwnership ownership();

	// -------------------------------------------------- //
	// RELATION AND RELATION COLOURS
	// -------------------------------------------------- //
	
	@Override
	String describeTo(RelationParticipator that, boolean ucfirst);

	@Override
	String describeTo(RelationParticipator that);

	@Override
	Relation getRelationTo(RelationParticipator rp);

	@Override
	Relation getRelationTo(RelationParticipator rp, boolean ignorePeaceful);

	@Override
	ChatColor getColorTo(RelationParticipator rp);
	
	/**
	 * Factions can have relationship wishes to other factions. This will get the relationship
	 * wish to another faction.
	 * 
	 * @param otherFaction The other faction.
	 * 
	 * @return The relation to that faction.
	 * 
	 * @see Relation
	 */
	Relation getRelationWish(Faction otherFaction);

	/**
	 * Factions can have relationship wishes to other factions. This will set the relationship wish
	 * to another faction.
	 * 
	 * @param otherFaction The other faction.
	 * @param relation The relationship wish.
	 * 
	 * @see Relation
	 */
	void setRelationWish(Faction otherFaction, Relation relation);

	/**
	 * Factions can have relationships to other factions. This method will return the total amount
	 * of relations of this type.
	 * 
	 * @param relation The relation type.
	 * 
	 * @return The amount of relations this faction has of the specified relation.
	 * 
	 * @see Relation
	 */
	int getRelationCount(Relation relation);

	/**
	 * Factions can have relationships to other factions. It can be configured that there is a 
	 * maximum amount of relation types per faction. This will compare the maximum with both
	 * this faction and the other faction we are setting it to.
	 * 
	 * @param them The faction we want to set a relationship with
	 * @param relation The relation type.
	 * @param silent Should we notify the faction if we are at the maximum
	 * 
	 * @return true if we have the maximum amount of relations. However, you should also call
	 *         the event {@link EventFactionsRelationshipsCapped} to see if another plugin lets
	 *         you bypass it.
	 *         
	 * @see Relation
	 * @see EventFactionsRelationshipsCapped
	 */
	boolean hasMaxRelations(Faction them, Relation relation, Boolean silent);
	
	/**
	 * Returns a snapshot of current relationship wishes. This will return faction ids.
	 * 
	 * @return A snapshot of all relationship wishes. 
	 * 
	 * @see Relation
	 */
	Map<String, Relation> getRelationWishes();

	// ----------------------------------------------//
	// Power
	// ----------------------------------------------//
	
	/**
	 * Get the current total power of this faction.
	 * 
	 * @return The current power.
	 */
	double getPower();

	/**
	 * Get the total power max of this faction.
	 * 
	 * @return Total power.
	 */
	double getPowerMax();
	
	/**
	 * Get the power value, but round it so it is easier to read.
	 * 
	 * @return The current power, rounded.
	 */
	int getPowerRounded();

	/**
	 * Get the maximum power value, but round it so it is easier to read.
	 * 
	 * @return The maximum power, rounded.
	 */
	int getPowerMaxRounded();
	
	/**
	 * Get the land count.
	 * 
	 * @return Land count.
	 */
	int getLandRounded();

	/**
	 * Get the land count in a world.
	 * 
	 * @param world The world to tally from.
	 * 
	 * @return Land count in world.
	 */
	int getLandRoundedInWorld(World world);

	/**
	 * Confirm if this faction has land inflation.
	 * 
	 * @return true if this faction has land inflation.
	 */
	boolean hasLandInflation();

	// -------------------------------------------------- //
	// MEMBERS
	// -------------------------------------------------- //

	/**
	 * Refresh the maintained list of members in this faction.
	 */
	void memberRefresh();

	/**
	 * Add a member to this faction.
	 * 
	 * @param fplayer The {@link FPlayer} to add
	 * 
	 * @return true if they were added.
	 */
	boolean memberAdd(FPlayer fplayer);

	/**
	 * Remove a member from this faction.
	 * 
	 * @param fplayer The {@link FPlayer} to remove
	 * 
	 * @return true if they were removed.
	 */
	boolean memberRemove(FPlayer fplayer);

	/**
	 * Get the count of all members in the faction.
	 * 
	 * @return The total count of members in this faction.
	 */
	int memberCount();

	/**
	 * Get all members in this faction.
	 * 
	 * @return A snapshot {@link Set} of all members in this faction.
	 */
	Set<FPlayer> getMembers();

	/**
	 * Get all online members in this faction.
	 * @param online Online only players?
	 * @return A snapshot {@link Set} of all online members in this faction.
	 */
	Set<FPlayer> getWhereOnline(boolean online);
	
	/**
	 * Get the owner of this faction. Also referred to as "leader", or "admin".
	 * 
	 * @return The owner of this faction.
	 */
	FPlayer getOwner();
	
	/**
	 * Get a list of players with the specified role.
	 * @param role The {@link Role}
	 * @return A snapshot list of all players with this role.
	 * @see Role
	 */
	List<FPlayer> getWhereRole(Role role);
	
	/**
	 * Get all online members as players.
	 * 
	 * @return A snapshot list of all online players.
	 */
	ArrayList<Player> getOnlinePlayers();

	// 
	// there are any players online
	/**
	 * Slightly faster check than {@link #getOnlinePlayers()} if you just want to see if there
	 * are any players online.
	 * 
	 * @return true if there are players online
	 */
	boolean hasPlayersOnline();
	
	/**
	 * Called when a member logs off.
	 */
	void memberLoggedOff();
	
	/**
	 * used when current leader is about to be removed from the faction; promotes a new leader, or
	 * disbands the faction if no other members left.
	 */
	void promoteNewLeader();

	// -------------------------------------------------- //
	// MESSAGES
	// -------------------------------------------------- //
	
	/**
	 * Send a message to the faction. It will be formatted.
	 * 
	 * @param message Message to send.
	 */
	void sendMessage(String message);

	/**
	 * Sends a list of messages to the faction. They are formatted.
	 * 
	 * @param messages List of messages to send.
	 */
	void sendMessage(List<String> messages);
	
	/**
	 * Sends a plain message to the faction. It is not formatted.
	 * 
	 * @param message Message to send.
	 */
	void sendPlainMessage(String message);
	
	/**
	 * Sends a list of plain messages to the faction. They are not formatted.
	 * 
	 * @param messages List of messages to send.
	 */
	void sendPlainMessage(List<String> messages);	
	
	// -------------------------------------------------- //
	// PERSISTANCE AND ENTITY MANAGEMENT
	// -------------------------------------------------- //
	
	/**
	 * Remove this faction.
	 */
	void remove();
	
	boolean equals(Object object);
	
}
