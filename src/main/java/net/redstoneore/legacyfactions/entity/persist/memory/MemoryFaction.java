package net.redstoneore.legacyfactions.entity.persist.memory;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.google.common.collect.Maps;

import net.redstoneore.legacyfactions.FLocation;
import net.redstoneore.legacyfactions.Permission;
import net.redstoneore.legacyfactions.Relation;
import net.redstoneore.legacyfactions.Role;
import net.redstoneore.legacyfactions.config.Config;
import net.redstoneore.legacyfactions.entity.Board;
import net.redstoneore.legacyfactions.entity.FPlayer;
import net.redstoneore.legacyfactions.entity.FPlayerColl;
import net.redstoneore.legacyfactions.entity.Faction;
import net.redstoneore.legacyfactions.entity.persist.shared.SharedFaction;
import net.redstoneore.legacyfactions.flag.Flag;
import net.redstoneore.legacyfactions.flag.Flags;
import net.redstoneore.legacyfactions.integration.vault.VaultEngine;
import net.redstoneore.legacyfactions.lang.Lang;
import net.redstoneore.legacyfactions.locality.Locality;
import net.redstoneore.legacyfactions.util.LazyLocation;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * MemoryFaction should be used carefully by developers. You should be able to do what you want
 * with the available methods in Faction. If something is missing, open an issue on GitHub.<br>
 * <br>
 * Do not store references to any fields. Always use the methods available.  
 */
public abstract class MemoryFaction extends SharedFaction {
	
	// -------------------------------------------------- //
	// FIELDS
	// -------------------------------------------------- //
	
	// Misc
	protected String id = null;
	protected String tag;
	protected String description;

	protected long autoKickDays = -1;
	
	// flags
	protected ConcurrentHashMap<String, Boolean> flags = new ConcurrentHashMap<>();
		
	@Deprecated
	protected boolean open;
	@Deprecated
	protected boolean peaceful;
	@Deprecated
	protected boolean peacefulExplosionsEnabled;
	@Deprecated
	protected boolean permanent;
	
	protected Character forcedMapCharacter = null;
	protected ChatColor forcedMapColour = null;
	
	protected Integer permanentPower;
	protected LazyLocation home;
	protected long foundedDate;
	protected double money;
	protected double powerBoost;
	protected Map<String, Relation> relationWish = new HashMap<>();
	protected Map<FLocation, Set<String>> claimOwnership = new ConcurrentHashMap<>();
	
	@JsonIgnore protected transient Set<FPlayer> fplayers = new HashSet<>();
	protected Set<String> invites = new HashSet<>();
	protected Set<String> bannedPlayerIds = new HashSet<>();
	protected ConcurrentHashMap<String, List<String>> announcements = new ConcurrentHashMap<>();
	protected ConcurrentHashMap<String, LazyLocation> warps = new ConcurrentHashMap<>();
	protected ConcurrentHashMap<String, String> warpPasswords = new ConcurrentHashMap<>();
	private long lastDeath;
	
	protected int maxVaults;
	
	private String emblem = null;
		
	// -------------------------------------------------- //
	// METHODS
	// -------------------------------------------------- //
		
	@Override
	public String getId() {
		return this.id;
	}
	
	@Override
	public String getTag() {
		return this.tag;
	}
	
	@Override
	public void setTag(String str) {
		if (Config.factionTagForceUpperCase) {
			str = str.toUpperCase();
		}
		this.tag = str;
	}


	@Override
	public String getDescription() {
		return this.description;
	}

	@Override
	public void setDescription(String value) {
		this.description = value;
	}
	
	@Override
	public Map<Flag, Boolean> getFlags() {
		Map<Flag, Boolean> formattedFlags = new HashMap<>();

		this.flags.forEach((flag, value) ->  {
			if (Flags.get(flag).isPresent()) {
				formattedFlags.put(Flags.get(flag).get(), value);
			}
		});
		
		Flags.getAll().forEach(flag -> {
			Optional<Flag> exists = formattedFlags.keySet().stream().filter(fflag -> fflag.getStoredName().equalsIgnoreCase(flag.getStoredName())).findAny();
			
			if (!exists.isPresent()) {
				// variables -> flag conversion
				
				// TEMPORARY. TODO: Remove old variable conversion crap, convert on load
				if (flag.equals(Flags.PERMANENT)) {
					formattedFlags.put(flag, this.permanent);
					this.setFlag(Flags.PERMANENT, this.permanent);
				} else if (flag.equals(Flags.PEACEFUL)) {
					formattedFlags.put(flag, this.peaceful);
					this.setFlag(Flags.PEACEFUL, this.peaceful);
				} else if (flag.equals(Flags.OPEN)) {
					formattedFlags.put(flag, this.open);
					this.setFlag(Flags.OPEN, this.open);
				} else if (flag.equals(Flags.EXPLOSIONS)) {
					formattedFlags.put(flag, this.peacefulExplosionsEnabled);
					this.setFlag(Flags.EXPLOSIONS, this.peacefulExplosionsEnabled);
				} else {
					formattedFlags.put(flag, flag.getDefaultValue());
				}	
			}
		});
		
		return formattedFlags;
	}
	
	@Override
	public boolean setFlag(Flag flag, Boolean value) {
		this.flags.put(flag.getStoredName(), value);
		
		return true;
	}

	@Override
	public boolean getFlag(Flag flag) {
		// TEMPORARY. TODO: Remove old variable conversion crap, convert on load
		Optional<Entry<Flag, Boolean>> exists = this.getFlags().entrySet().stream().filter(entry -> {
			if (entry.getKey().getStoredName().equalsIgnoreCase(flag.getStoredName())) {
				return true;
			}
			return false;
		}).findFirst();

		if (!exists.isPresent()) {
			return flag.getDefaultValue();
		}
		
		return exists.get().getValue();
	}
	
	@Override
	public Map<String, List<String>> getAnnouncements() {
		return Maps.newHashMap(this.announcements);
	}

	@Override
	public void addAnnouncement(FPlayer fplayer, String msg) {
		List<String> list = announcements.containsKey(fplayer.getId()) ? announcements.get(fplayer.getId()) : new ArrayList<>();
		list.add(msg);
		announcements.put(fplayer.getId(), list);
	}
	
	@Override
	public void removeAnnouncements(FPlayer fplayer) {
		if (announcements.containsKey(fplayer.getId())) {
			announcements.remove(fplayer.getId());
		}
	}

	@Override
	public ConcurrentHashMap<String, LazyLocation> getAllWarps() {
		return this.warps;
	}

	public LazyLocation getWarp(String name) {
		return this.warps.get(name);
	}
	
	public Optional<String> getWarpPassword(String warpName) {
		if (!this.warpPasswords.containsKey(warpName)) return Optional.empty();
		
		return Optional.of(this.warpPasswords.get(warpName).toLowerCase());
	}

	public void setWarp(String name, LazyLocation loc, String password) {
		if (password != null) {
			this.warpPasswords.put(name, password.toLowerCase());
		}
		
		this.warps.put(name, loc);
	}

	public boolean isWarp(String name) {
		return this.warps.containsKey(name);
	}

	public boolean removeWarp(String name) {
		if (this.warps.remove(name) == null) return false;
		
		if (this.warpPasswords.contains(name)) this.warpPasswords.remove(name);
		
		return true;
		
	}

	public void clearWarps() {
		this.warps.clear();
		this.warpPasswords.clear();
	}

	public int getMaxVaults() {
		return this.maxVaults;
	}

	public void setMaxVaults(int value) {
		this.maxVaults = value;
	}

	public Set<String> getInvites() {
		return invites;
	}

	public void setId(String id) {
		this.id = id;
	}

	public void invite(FPlayer fplayer) {
		this.invites.add(fplayer.getId());
	}

	public void uninvite(FPlayer fplayer) {
		this.invites.remove(fplayer.getId());
	}

	public boolean isInvited(FPlayer fplayer) {
		return this.invites.contains(fplayer.getId());
	}

	@Override
	public void ban(FPlayer fplayer) {
		if (fplayer == null) return;
		this.bannedPlayerIds.add(fplayer.getId());
	}
	
	@Override
	public void unban(FPlayer fplayer) {
		if (fplayer == null) return;
		this.bannedPlayerIds.remove(fplayer.getId());
	}
	
	@Override
	public boolean isBanned(FPlayer fplayer) {
		return this.bannedPlayerIds.contains(fplayer.getId());
	}

	

	
	public void setForcedMapCharacter(char character) {
		this.forcedMapCharacter = character;
	}
	
	public Character getForcedMapCharacter() {
		if (this.isWilderness() && this.forcedMapCharacter == null) {
			this.forcedMapCharacter = "-".charAt(0);
		}
		if (this.isSafeZone() && this.forcedMapCharacter == null) {
			this.forcedMapCharacter = "+".charAt(0);
		}
		if (this.isWarZone() && this.forcedMapCharacter == null) {
			this.forcedMapCharacter = "+".charAt(0);
		}
		
		return this.forcedMapCharacter;
	}
	
	
	public void setForcedMapColour(ChatColor colour) {
		this.forcedMapColour = colour;
	}
	
	public ChatColor getForcedMapColour() {
		if (this.isWilderness() && this.forcedMapColour == null) {
			this.forcedMapColour = ChatColor.GRAY;
		}
		if (this.isSafeZone() && this.forcedMapColour == null) {
			this.forcedMapColour = ChatColor.GOLD;
		}
		if (this.isWarZone() && this.forcedMapColour == null) {
			this.forcedMapColour = ChatColor.DARK_RED;
		}
		
		return this.forcedMapColour;
	}
	

	public void setHome(Location home) {
		if (home == null) {
			this.home = null;
		} else {
			this.home = new LazyLocation(home);
		}
	}
	
	public Location getHome(Boolean checkValid) {
		if (checkValid) {
			this.confirmValidHome();			
		}
		return (this.home != null) ? this.home.getLocation() : null;
	}
	
	@Override
	public LazyLocation getLazyHome() {
		return this.home;
	}

	public long getFoundedDate() {
		if (this.foundedDate == 0) {
			setFoundedDate(System.currentTimeMillis());
		}
		return this.foundedDate;
	}

	public void setFoundedDate(long newDate) {
		this.foundedDate = newDate;
	}

	@Override
	public String getEmblem() {
		return this.emblem;
	}
	
	@Override
	public void forceSetEmblem(String emblem) {
		this.emblem = emblem;
	}

	public Integer getPermanentPower() {
		return this.permanentPower;
	}

	public void setPermanentPower(Integer permanentPower) {
		this.permanentPower = permanentPower;
	}

	public double getPowerBoost() {
		return this.powerBoost;
	}

	public void setPowerBoost(double powerBoost) {
		this.powerBoost = powerBoost;
	}



	public void setLastDeath(long time) {
		this.lastDeath = time;
	}

	public long getLastDeath() {
		return this.lastDeath;
	}
	
	// -------------------------------------------- //
	// Construct
	// -------------------------------------------- //
	public MemoryFaction() {
	}

	public MemoryFaction(String id) {
		this.id = id;
		this.tag = "???";
		this.description = Lang.GENERIC_DEFAULTDESCRIPTION.toString();
		this.setLastPlayerLoggedOffTime(0);
		this.peaceful = false;
		this.peacefulExplosionsEnabled = false;
		this.permanent = false;
		this.money = 0.0;
		this.powerBoost = 0.0;
		this.foundedDate = System.currentTimeMillis();
		this.maxVaults = Config.defaultMaxVaults;
		this.autoKickDays = -1;
		this.emblem = "??";
	}

	public MemoryFaction(MemoryFaction old) {
		id = old.id;
		peacefulExplosionsEnabled = old.peacefulExplosionsEnabled;
		permanent = old.permanent;
		tag = old.tag;
		description = old.description;
		open = old.open;
		foundedDate = old.foundedDate;
		peaceful = old.peaceful;
		permanentPower = old.permanentPower;
		home = old.home;
		this.setLastPlayerLoggedOffTime(old.getLastPlayerLoggedOffTime());
		money = old.money;
		powerBoost = old.powerBoost;
		relationWish = old.relationWish;
		claimOwnership = old.claimOwnership;
		fplayers = new HashSet<>();
		invites = old.invites;
		announcements = old.announcements;
		autoKickDays = old.autoKickDays;
		this.emblem = old.emblem;
	}

	// -------------------------------
	// Relation and relation colors
	// -------------------------------

	public Relation getRelationWish(Faction otherFaction) {
		if (this.relationWish.containsKey(otherFaction.getId())) {
			if (this.relationWish.get(otherFaction.getId()) == Relation.TRUCE && !Config.enableTruces) {
				this.relationWish.put(otherFaction.getId(), Relation.NEUTRAL);
			}
			return this.relationWish.get(otherFaction.getId());
		}
		return Relation.fromString(Config.factionDefaultRelation); // Always default to old behavior.
	}

	public void setRelationWish(Faction otherFaction, Relation relation) {
		if (relation == Relation.TRUCE && !Config.enableTruces) {
			relation = Relation.NEUTRAL;
		}
		
		if (this.relationWish.containsKey(otherFaction.getId()) && relation.equals(Relation.NEUTRAL)) {
			this.relationWish.remove(otherFaction.getId());
		} else {
			this.relationWish.put(otherFaction.getId(), relation);
		}
	}
	
	@Override
	public Map<String, Relation> getRelationWishes() {
		return new HashMap<>(this.relationWish);
	}

	// -------------------------------
	// FPlayers
	// -------------------------------

	// maintain the reference list of FPlayers in this faction
	public void memberRefresh() {
		fplayers.clear();
		if (this.isPlayerFreeType()) {
			return;
		}

		for (FPlayer fplayer : FPlayerColl.all()) {
			if (fplayer.getFactionId().equalsIgnoreCase(id)) {
				fplayers.add(fplayer);
			}
		}
	}

	public boolean memberAdd(FPlayer fplayer) {
		return !this.isPlayerFreeType() && fplayers.add(fplayer);

	}

	public boolean memberRemove(FPlayer fplayer) {
		return !this.isPlayerFreeType() && fplayers.remove(fplayer);

	}

	public int memberCount() {
		return fplayers.size();
	}

	public Set<FPlayer> getMembers() {
		// return a shallow copy of the FPlayer list, to prevent tampering and
		// concurrency issues
		return new HashSet<>(this.fplayers);
	}
	
	// ----------------------------------------------//
	// Ownership of specific claims
	// ----------------------------------------------//

	@Override
	public Map<FLocation, Set<String>> getClaimOwnership() {
		return this.claimOwnership;
	}

	public void clearAllClaimOwnership() {
		this.claimOwnership.clear();
	}

	public void clearClaimOwnership(Locality locality) {
		this.claimOwnership.remove(new FLocation(locality.getChunk()));
	}
	
	public void clearClaimOwnership(FLocation loc) {
		this.claimOwnership.remove(loc);
	}

	public void clearClaimOwnership(FPlayer player) {
		if (id == null || id.isEmpty()) {
			return;
		}

		Set<String> ownerData;

		for (Entry<FLocation, Set<String>> entry : claimOwnership.entrySet()) {
			ownerData = entry.getValue();

			if (ownerData == null) {
				continue;
			}

			ownerData.removeIf((String id) -> id.equals(player.getId()));

			if (ownerData.isEmpty()) {
				claimOwnership.remove(entry.getKey());
			}
		}
	}

	public int getCountOfClaimsWithOwners() {
		return claimOwnership.isEmpty() ? 0 : claimOwnership.size();
	}

	public boolean doesLocationHaveOwnersSet(FLocation loc) {
		if (claimOwnership.isEmpty() || !claimOwnership.containsKey(loc)) {
			return false;
		}

		Set<String> ownerData = claimOwnership.get(loc);
		return ownerData != null && !ownerData.isEmpty();
	}

	public boolean isPlayerInOwnerList(FPlayer player, FLocation loc) {
		if (claimOwnership.isEmpty()) {
			return false;
		}
		Set<String> ownerData = claimOwnership.get(loc);
		if (ownerData == null) {
			return false;
		}
		return ownerData.contains(player.getId());
	}

	public void setPlayerAsOwner(FPlayer player, FLocation loc) {
		Set<String> ownerData = claimOwnership.get(loc);
		if (ownerData == null) {
			ownerData = new HashSet<>();
		}
		ownerData.add(player.getId());
		claimOwnership.put(loc, ownerData);
	}

	public void removePlayerAsOwner(FPlayer player, FLocation loc) {
		Set<String> ownerData = claimOwnership.get(loc);
		if (ownerData == null) {
			return;
		}
		ownerData.remove(player.getId());
		claimOwnership.put(loc, ownerData);
	}

	public Set<String> getOwnerList(FLocation loc) {
		return claimOwnership.get(loc);
	}

	public String getOwnerListString(FLocation loc) {
		Set<String> ownerData = claimOwnership.get(loc);
		if (ownerData == null || ownerData.isEmpty()) {
			return "";
		}

		String ownerList = "";

		for (String owner : ownerData) {
			if (!ownerList.isEmpty()) {
				ownerList += ", ";
			}
			OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(UUID.fromString(owner));
			ownerList += offlinePlayer != null ? offlinePlayer.getName() : Lang.GENERIC_NULLPLAYER.toString();
		}
		return ownerList;
	}

	public boolean playerHasOwnershipRights(FPlayer fplayer, FLocation loc) {
		// in own faction, with sufficient role or permission to bypass
		// ownership?
		boolean canBypass = fplayer.getRole().isAtLeast(Config.ownedAreaModeratorsBypass ? Role.MODERATOR : Role.COLEADER) || Permission.OWNERSHIP_BYPASS.has(fplayer.getPlayer());

		if (fplayer.getFaction() == this && canBypass) {
			return true;
		}

		// make sure claimOwnership is initialized
		if (claimOwnership.isEmpty()) return true;
		

		// need to check the ownership list, then
		Set<String> ownerData = claimOwnership.get(loc);

		// if no owner list, owner list is empty, or player is in owner list,
		// they're allowed
		return ownerData == null || ownerData.isEmpty() || ownerData.contains(fplayer.getId());
	}

	@Override
	public void setAutoKick(long days) {
		this.autoKickDays = days;
	}
	
	@Override
	public long getAutoKick() {
		return this.autoKickDays;
	}
	
	// -------------------------------------------------- //
	// Persistance and entity management
	// -------------------------------------------------- //
	
	public void remove() {
		if (VaultEngine.getUtils().shouldBeUsed()) {
			VaultEngine.getUtils().setBalance(this.getAccountId(), 0);
		}

		// Clean the board
		((MemoryBoard) Board.get()).clean(this.id);

		this.fplayers.forEach(fplayer -> fplayer.resetFactionData());
	}


	
	public MemoryFaction asMemoryFaction() {
		return this;
	}
	

}
