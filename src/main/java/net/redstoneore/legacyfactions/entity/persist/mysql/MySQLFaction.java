package net.redstoneore.legacyfactions.entity.persist.mysql;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;

import net.redstoneore.legacyfactions.FLocation;
import net.redstoneore.legacyfactions.Factions;
import net.redstoneore.legacyfactions.Relation;
import net.redstoneore.legacyfactions.adapter.LazyLocationAdapter;
import net.redstoneore.legacyfactions.config.Config;
import net.redstoneore.legacyfactions.entity.Board;
import net.redstoneore.legacyfactions.entity.FPlayer;
import net.redstoneore.legacyfactions.entity.FPlayerColl;
import net.redstoneore.legacyfactions.entity.Faction;
import net.redstoneore.legacyfactions.entity.persist.mysql.MySQLPrepared.ExecuteType;
import net.redstoneore.legacyfactions.entity.persist.shared.SharedFaction;
import net.redstoneore.legacyfactions.flag.Flag;
import net.redstoneore.legacyfactions.flag.Flags;
import net.redstoneore.legacyfactions.integration.vault.VaultEngine;
import net.redstoneore.legacyfactions.locality.Locality;
import net.redstoneore.legacyfactions.util.LazyLocation;
import net.redstoneore.legacyfactions.util.MiscUtil;

public class MySQLFaction extends SharedFaction {
	
	// -------------------------------------------------- //
	// CONSTRUCT
	// -------------------------------------------------- //
	
	/**
	 * Construct a MySQLFaction from an id, and run the set method if needed.
	 * @param id Id of this faction.
	 * @param runSet Set to true to run {@link #setId}.
	 */
	public MySQLFaction(String id, boolean runSet) {
		if (runSet) {
			this.setId(id);			
		} else {
			this.id = id;
			this.values = new ConcurrentHashMap<>();
		}
	}
	
	/**
	 * Construct a MySQLFaction and use the raw data provided. This saves a call to the faction 
	 * table.
	 * @param entry Entry of variables.
	 */
	public MySQLFaction(Map<String, String> entry) {
		this.id = entry.get("id");
		this.values = entry;
		this.lastPollMs = System.currentTimeMillis();
		this.pollOther();
	}
	
	/**
	 * Construct a MySQLFaction from another faction type. 
	 * @param old Old type.
	 */
	public MySQLFaction(SharedFaction old) {
		this.setId(old.getId());
		
		old.getFlags().forEach((flag,value) -> this.setFlag(flag, value));
		this.setTag(old.getTag());
		this.setDescription(old.getDescription());
		this.setFoundedDate(old.getFoundedDate());
		this.setPermanentPower(old.getPermanentPower());
		this.setHome(old.getHome());
		this.setLastPlayerLoggedOffTime(old.getLastPlayerLoggedOffTime());
		this.setPowerBoost(old.getPowerBoost());
		
		this.getRelationWishes().forEach((faction, wish) -> {
			// Insert raw, as the other faction may not exist yet
			if (FactionsMySQL.get().prepare(
					"INSERT INTO `relation_wish` (`faction`, `faction_to`, `relation_wish`)" + 
					"VALUES" + 
					"	(?, ?, ?);")
				.setCatched(1, this)
				.setCatched(2, faction)
				.setCatched(3, wish.name())
			.execute(ExecuteType.UPDATE) == null) {
				Factions.get().warn("[MySQL] insert relation " + wish + " for " + this.id + " failed");
			}
		});
		
		this.getClaimOwnership().forEach((location, playerList) -> 
			playerList.forEach(playerId -> 
				this.setPlayerAsOwner(playerId, location)
			)
		);
		
		old.getInvites().forEach(this::invite);
		
		old.getAnnouncements().forEach((playerId, announcements) -> 
			announcements.forEach(announcement -> this.addAnnouncement(playerId, announcement))
		);
	}
	
	// -------------------------------------------------- //
	// FIELDS
	// -------------------------------------------------- //

	public transient String id;
	
	private Map<String, String> values;
	private List<Map<String, String>> flags = new ArrayList<>();
	private List<Map<String, String>> announcements = new ArrayList<>();
	private Map<String, LazyLocation> warps = new HashMap<>();
	private Map<String, String> warpPasswords = new HashMap<>();
	private List<Map<String, String>> invites = new ArrayList<>();
	private List<Map<String, String>> bans = new ArrayList<>();

	private List<Map<String, String>> relations = new ArrayList<>();

	private List<FPlayer> members = new ArrayList<FPlayer>();
	
	private LazyLocation home = null;
	
	public long lastPollMs;
		
	// -------------------------------------------------- //
	// METHODS
	// -------------------------------------------------- //
	
	/**
	 * Poll the database for updated records if 10 seconds has passed since our last poll.
	 */
	public void poll() {
		this.poll(false);
	}
	
	/**
	 * Poll the database for updated records if 10 seconds has passed since our last poll.
	 * @param force Poll without checking the last poll time
	 */
	public void poll(boolean force) {
		if (!force && this.lastPollMs < this.lastPollMs + TimeUnit.SECONDS.toMillis(10)) {
			return;
		}
		
		this.lastPollMs = System.currentTimeMillis();
		
		// Poll Faction
		Map<String, String> newValues = this.pollSomethingGetFirst("faction", "id");
		if (newValues != null ) {
			this.values = newValues;
		}
		
		this.pollOther();
	}
	
	/**
	 * This will poll everything else.
	 */
	private void pollOther() {
		// Poll Flags
		List<Map<String,String>> newFlags = this.pollSomething("faction_flags", "faction");
		if (newFlags != null) {
			this.flags = newFlags;
		}
		
		// Poll Announcements
		List<Map<String,String>> newAnnouncements = this.pollSomething("faction_announcements", "faction");
		if (newAnnouncements != null) {
			this.announcements = newAnnouncements;
		}
		
		// Poll Warps
		List<Map<String,String>> newWarps = this.pollSomething("faction_warps", "faction");
		if (newWarps != null) {
			final Map<String, LazyLocation> warps = new HashMap<>();
			final Map<String, String> warpPasswords = new HashMap<>();

			newWarps.forEach(warp -> {
				warps.put(warp.get("name"), LazyLocationAdapter.deserialise(warp.get("location")));
				warpPasswords.put(warp.get("name"), warp.get("password"));
			});
			
			this.warps = warps;
			this.warpPasswords = warpPasswords;
		}
		
		// Poll invites
		List<Map<String,String>> newInvites = this.pollSomething("faction_invites", "faction");
		if (newInvites != null) {
			this.invites = newInvites;
		}
		
		// Poll bans
		List<Map<String,String>> newBans = this.pollSomething("faction_bans", "faction");
		if (newBans != null) {
			this.bans = newBans;
		}
		
		// Home
		if (this.values.containsKey("home") && !this.values.get("home").trim().isEmpty() && !this.values.get("home").equalsIgnoreCase("null")) {
			try { 
				this.home = LazyLocationAdapter.deserialise(this.values.get("home"));
			} catch (Throwable e) {
				this.home = null;
			}
		} else {
			this.home = null;
		}
		
		// Members
		this.members = FPlayerColl.all().stream()
			.filter(FPlayer::isOnline)
			.filter(fplayer -> fplayer.getFactionId() == this.getId())
			.collect(Collectors.toList());
		
	}
	
	/**
	 * Utility method, selects all from a table using a variable. 
	 * @param what Table name.
	 * @param using Field to match this.
	 * @return Results
	 */
	private List<Map<String, String>> pollSomething(String what, String using) {
		List<Map<String, String>> newValues = null;
		
		
		MySQLPrepared prepared = null;
		try {
			prepared = FactionsMySQL.get().prepare("SELECT * FROM `" + what + "` WHERE `"+using+"` = ?")
				.set(1, this);
			
			newValues = prepared.execute(ExecuteType.SELECT);
			
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			if (prepared != null) {
				prepared.close();
			}
		}
		
		return newValues;
	}
	
	/**
	 * Utility method, selects all from a table using a variable and fetches the first result.
	 * @param what Table name.
	 * @param using Field to match this.
	 * @return The first result, or null if none.
	 */
	private Map<String, String> pollSomethingGetFirst(String what, String using) {
		List<Map<String, String>> newValues = this.pollSomething(what, using);
		
		if (newValues != null && newValues.size() > 0) {
			return newValues.get(0);
		}
		
		return null;
	}

	@Override
	public String getId() {
		return this.id;
	}

	@Override
	public String getTag() {
		this.poll();
		return this.values.get("tag");
	}
	
	@Override
	public void setTag(String tag) {
		this.values.put("tag", tag);
		FactionsMySQL.get().prepare("UPDATE `faction` SET `tag` = ? WHERE `id` = ?")
			.setCatched(1, tag)
			.setCatched(2, this.id)
			.execute(ExecuteType.UPDATE);
	}

	@Override
	public String getDescription() {
		this.poll();
		return this.values.get("description");
	}

	@Override
	public void setDescription(String value) {
		this.values.put("description", value);
		FactionsMySQL.get().prepare("UPDATE `faction` SET `description` = ? WHERE `id` = ?")
			.setCatched(1, value)
			.setCatched(2, this.id)
			.execute(ExecuteType.UPDATE);
	}
	
	@Override
	public Map<Flag, Boolean> getFlags() {
		this.poll();
		Map<Flag, Boolean> returnedFlags = new HashMap<>();
		this.flags.forEach(entry -> {
			Optional<Flag> oFlag = Flags.get(entry.get("flag"));
			if (oFlag.isPresent()) {
				returnedFlags.put(oFlag.get(), Boolean.valueOf(entry.get("value")));
			}
		});
		
		// Add our default flags
		Flags.getAll().forEach(flag -> {
			Optional<Flag> exists = returnedFlags.keySet().stream().filter(fflag -> fflag.getStoredName().equalsIgnoreCase(flag.getStoredName())).findAny();
			
			if (!exists.isPresent()) {
				returnedFlags.put(flag, flag.getDefaultValue());
			}
		});
		return returnedFlags;
	}

	@Override
	public boolean setFlag(Flag flag, Boolean value) {
		// update in the database
		if (FactionsMySQL.get().prepare("SELECT `flag` FROM `faction_flags` WHERE faction = ? AND flag = ?")
				.setCatched(1, this.id)
				.setCatched(2, flag.getStoredName())
				.execute(ExecuteType.SELECT)
				.size() == 0) {
			
			// Not in database, insert
			if (FactionsMySQL.get().prepare(
					"INSERT INTO `faction_flags` (`faction`, `flag`, `value`)" + 
					"VALUES" + 
					"	(?, ?, ?);")
				.setCatched(1, this.id)
				.setCatched(2, flag.getStoredName())
				.setCatched(3, value)
				.execute(ExecuteType.UPDATE) == null) {
					Factions.get().warn("[MySQL] inserting flag " + flag.getStoredName() + " for " + this.id + " failed");
			}

		} else {
			if (FactionsMySQL.get().prepare(
					"UPDATE `faction_flags` SET `value` = ? WHERE `flag` = ? AND `faction` = ?;")
				.setCatched(1, value)
				.setCatched(2, flag.getStoredName())
				.setCatched(3, this.id)
				.execute(ExecuteType.UPDATE) == null) {
					Factions.get().warn("[MySQL] updating flag " + flag.getStoredName() + " for " + this.id + " failed");
			}
		}
		
		List<Map<String,String>> newFlags = this.pollSomething("faction_flags", "faction");
		if (newFlags != null) {
			this.flags = newFlags;
		}
		
		return true;
	}

	@Override
	public boolean getFlag(Flag flag) {
		this.poll();
		return this.flags.stream()
			.filter(potentialFlag -> potentialFlag.get("flag") == flag.getStoredName())
			.map(foundFlag -> Boolean.valueOf(foundFlag.get("value")))
			.findFirst()
			.orElse(flag.getDefaultValue());
	}

	@Override
	public HashMap<String, List<String>> getAnnouncements() {
		this.poll();
		
		final HashMap<String, List<String>> announcements = new HashMap<>();
		
		this.announcements.stream()
			.forEach(announcement -> {
				if (!announcements.containsKey(announcement.get("announcer"))) {
					announcements.put(announcement.get("announcer"), new ArrayList<>());
				}
				announcements.get(announcement.get("announcer")).add(announcement.get("message"));
			});

		return announcements;
	}

	public void addAnnouncement(String fplayerId, String message) {
		this.announcements.add(MiscUtil.newMap(
				"faction", this.id, 
				"announcer", fplayerId,
				"message", message
			));
			
			if (FactionsMySQL.get().prepare(
					"INSERT INTO `faction_announcements` (`id`, `faction`, `announcer`, `message`)" + 
					"VALUES" + 
					"	(null, ?, ?, ?);")
				.setCatched(1, this)
				.setCatched(2, fplayerId)
				.setCatched(3, message)
			.execute(ExecuteType.UPDATE) == null) {
				Factions.get().warn("[MySQL] inserting announcement row failed");
			}		
	}
	
	@Override
	public void addAnnouncement(FPlayer fplayer, String message) {
		this.addAnnouncement(fplayer.getId(), message);
	}
	
	@Override
	public void removeAnnouncements(FPlayer fplayer) {
		this.announcements = this.announcements.stream()
			.filter(entry -> entry.get("announcer") != fplayer.getId())
			.collect(Collectors.toList());
		
		FactionsMySQL.get().prepare("DELETE FROM `faction_announcements` WHERE `faction` = ? AND `announcer` = ?")
			.setCatched(1, this)
			.setCatched(2, fplayer)
		.execute(ExecuteType.UPDATE);
	}

	@Override
	public int getMaxVaults() {
		this.poll();
		return this.values.get("maxvaults") == null ? Config.defaultMaxVaults : Integer.valueOf(this.values.get("maxvaults"));
	}

	@Override
	public void setMaxVaults(int value) {
		this.values.put("maxvaults", String.valueOf(value));
		
		FactionsMySQL.get().prepare("UPDATE `faction` SET `maxvaults` = ? WHERE `id` = ?")
			.setCatched(1, value)
			.setCatched(2, this.id)
			.execute(ExecuteType.UPDATE);
	}

	@Override
	public Set<String> getInvites() {
		this.poll();
		return this.invites.stream()
			.map(invite -> invite.get("name"))
			.collect(Collectors.toSet());
	}
	
	public void invite(String playerId) {
		this.invites.add(MiscUtil.newMap(
			"faction", this.id,
			"invite", playerId
		));
		
		if (FactionsMySQL.get().prepare(
				"INSERT INTO `faction_invites` (`faction`, `invite`)" + 
				"VALUES" + 
				"	(?, ?);")
			.setCatched(1, this.id)
			.setCatched(2, playerId)
		.execute(ExecuteType.UPDATE) == null) {
			Factions.get().warn("[MySQL] inserting invite " + playerId + " for " + this.id + " failed");
		}
	}
	
	@Override
	public void invite(FPlayer fplayer) {
		this.invite(fplayer.getId());
	}

	@Override
	public void uninvite(FPlayer fplayer) {
		this.invites = this.invites.stream()
			.filter(invite -> invite.get("invite") != fplayer.getId())
			.collect(Collectors.toList());
		
		FactionsMySQL.get().prepare("DELETE FROM `faction_invites` WHERE `faction` = ? AND `invite` = ?")
			.setCatched(1, this)
			.setCatched(2, fplayer)
		.execute(ExecuteType.UPDATE);
	}

	@Override
	public boolean isInvited(FPlayer fplayer) {
		this.poll();
		return this.invites.stream()
			.filter(result -> result.get("invite") == fplayer.getId())
			.map(invite -> true)
			.findFirst()
				.orElse(false);
	}

	@Override
	public void ban(FPlayer fplayer) {
		this.bans.add(MiscUtil.newMap(
			"faction", this.id, 
			"player", fplayer.getId()
		));
		
		if (FactionsMySQL.get().prepare(
				"INSERT INTO `faction_bans` (`faction`, `player`)" + 
				"VALUES" + 
				"	(?, ?);")
			.setCatched(1, this.id)
			.setCatched(2, fplayer)
		.execute(ExecuteType.UPDATE) == null) {
			Factions.get().warn("[MySQL] inserting ban " + fplayer.getId() + " for " + this.id + " failed");
		}

	}

	@Override
	public void unban(FPlayer fplayer) {
		this.bans = this.bans.stream()
			.filter(ban -> ban.get("player") != fplayer.getId())
			.collect(Collectors.toList());
		
		FactionsMySQL.get().prepare("DELETE FROM `faction_bans` WHERE `faction` = ? AND `player` = ?")
			.setCatched(1, this)
			.setCatched(2, fplayer)
		.execute(ExecuteType.UPDATE);
	}

	@Override
	public boolean isBanned(FPlayer fplayer) {
		return this.bans.stream()
			.filter(banned -> banned.get("player") == fplayer.getId())
			.map(ban -> true)
			.findFirst()
				.isPresent();
	}
	
	@Override
	public void setForcedMapCharacter(char character) {
		this.values.put("forcedmapcharacter", String.valueOf(character));
		FactionsMySQL.get().prepare("UPDATE `faction` SET `forcedmapcharacter` = ? WHERE `id` = ?")
			.setCatched(1, String.valueOf(character))
			.setCatched(2, this.id)
		.execute(ExecuteType.UPDATE);
	}
	
	@Override
	public Character getForcedMapCharacter() {
		this.poll();
		
		if (this.values.get("forcedmapcharacter") == null || 
			this.values.get("forcedmapcharacter").toLowerCase() == "null" ||
			this.values.get("forcedmapcharacter") == "" ||
			this.values.get("forcedmapcharacter").length() == 0) {
			return null;
		}
		
		return this.values.get("forcedmapcharacter").charAt(0);
	}

	@Override
	public void setForcedMapColour(ChatColor colour) {
		String colourName;
		if (colour == null) {
			colourName = "";
		} else {
			colourName = colour.name();
		}
		this.values.put("forcedmapcolour", colourName);
		FactionsMySQL.get().prepare("UPDATE `faction` SET `forcedmapcolour` = ? WHERE `id` = ?")
			.setCatched(1, colourName)
			.setCatched(2, this.id)
		.execute(ExecuteType.UPDATE);
	}

	@Override
	public ChatColor getForcedMapColour() {
		this.poll();
		if (!this.values.containsKey("forcedmapcolour") || this.values.get("forcedmapcolour").trim() == "") {
			return null;
		}
		try {
			return ChatColor.valueOf(this.values.get("forcedmapcolour"));
		} catch (IllegalArgumentException e) {
			// Not a valid chat colour, return null.
			return null;
		}
	}

	@Override
	public void setHome(Location home) {
		String jsonHome;
		if (home != null) {
			this.home = LazyLocation.of(home);
			jsonHome = LazyLocationAdapter.serialise(this.home);
		} else {
			jsonHome = "";
		}
		
		FactionsMySQL.get().prepare("UPDATE `faction` SET `home` = ? WHERE `id` = ?")
			.setCatched(1, jsonHome)
			.setCatched(2, this.id)
		.execute(ExecuteType.UPDATE);
	}
	
	@Override
	public Location getHome(Boolean checkValid) {
		return this.getLazyHome().getLocation();
	}

	@Override
	public LazyLocation getLazyHome() {
		this.poll();
		if (this.home == null || this.values.get("home") == "") return null;
		return this.home;
	}
	
	@Override
	public long getFoundedDate() {
		this.poll();
		return Long.valueOf(this.values.get("foundeddate"));
	}

	@Override
	public void setFoundedDate(long newDate) {
		this.values.put("foundeddate", String.valueOf(newDate));
		FactionsMySQL.get().prepare("UPDATE `faction` SET `foundeddate` = ? WHERE `id` = ?")
			.setCatched(1, newDate)
			.setCatched(2, this.id)
		.execute(ExecuteType.UPDATE);
	}
	
	@Override
	public void setAutoKick(long days) {
		this.values.put("autokick", String.valueOf(days));
		FactionsMySQL.get().prepare("UPDATE `faction` SET `autokick` = ? WHERE `id` = ?")
			.setCatched(1, days)
			.setCatched(2, this.id)
		.execute(ExecuteType.UPDATE);

	}
	
	@Override
	public long getAutoKick() {
		this.poll();
		return Long.valueOf(this.values.get("autokick"));
	}
	
	@Override
	public String getEmblem() {
		this.poll();
		return this.values.get("emblem");
	}
	
	@Override
	public void forceSetEmblem(String emblem) {
		this.values.put("emblem", emblem);
		FactionsMySQL.get().prepare("UPDATE `faction` SET `emblem` = ? WHERE `id` = ?")
			.setCatched(1, emblem)
			.setCatched(2, this.id)
		.execute(ExecuteType.UPDATE);

	}
	
	@Override
	public Integer getPermanentPower() {
		this.poll();
		if (this.values.get("permanentpower") == "") {
			return -1;
		}
		
		try {
			return Integer.valueOf(this.values.get("permanentpower"));
		} catch (NumberFormatException e) {
			this.setPermanentPower(0);
			return null;
		}
	}

	@Override
	public void setPermanentPower(Integer permanentPower) {
		if (permanentPower == null) {
			permanentPower = -1;
		}
		
		this.values.put("permanentpower", String.valueOf(permanentPower));
		
		FactionsMySQL.get().prepare("UPDATE `faction` SET `permanentpower` = ? WHERE `id` = ?")
			.setCatched(1, permanentPower)
			.setCatched(2, this.id)
			.execute(ExecuteType.UPDATE);
	}

	@Override
	public double getPowerBoost() {
		this.poll();
		if (this.values.get("powerboost") == "") {
			return 0d;
		}
		
		try {
			return Double.valueOf(this.values.get("powerboost"));
		} catch (NumberFormatException e) {
			this.setPermanentPower(0);
			return 0;
		}
	}

	@Override
	public void setPowerBoost(double powerBoost) {
		this.values.put("powerboost", String.valueOf(powerBoost));
		
		FactionsMySQL.get().prepare("UPDATE `faction` SET `powerboost` = ? WHERE `id` = ?")
			.setCatched(1, powerBoost)
			.setCatched(2, this.id)
		.execute(ExecuteType.UPDATE);
		
	}

	@Override
	public void setLastDeath(long time) {
		this.values.put("lastdeath", String.valueOf(time));
		
		FactionsMySQL.get().prepare("UPDATE `faction` SET `lastdeath` = ? WHERE `id` = ?")
			.setCatched(1, time)
			.setCatched(2, this.id)
		.execute(ExecuteType.UPDATE);
	}
	
	@Override
	public long getLastDeath() {
		this.poll();
		return Long.valueOf(this.values.get("lastdeath"));
	}

	@Override
	public Relation getRelationWish(Faction otherFaction) {
		return this.relations.stream()
			.filter(wish -> wish.get("faction_to") == otherFaction.getId())
			.map(wish -> Relation.valueOf(wish.get("relation_wish")))
			.findFirst()
				.orElse(Relation.NEUTRAL);
	}

	@Override
	public void setRelationWish(Faction otherFaction, Relation relation) {
		this.relations = this.relations.stream()
			.filter(wish -> wish.get("faction_to") != otherFaction.getId())
			.collect(Collectors.toList());
			
		this.relations.add(MiscUtil.newMap(
			"faction_to", otherFaction.getId(),
			"relation_wish", relation.name()
		));
		
		// now update in the database
		if (FactionsMySQL.get().prepare("SELECT `relation_wish` FROM `faction_relations` WHERE faction = ? AND faction_to = ?")
				.setCatched(1, this.id)
				.setCatched(2, otherFaction)
				.execute(ExecuteType.SELECT)
				.size() == 0) {
			
			if (FactionsMySQL.get().prepare(
					"INSERT INTO `relation_wish` (`faction`, `faction_to`, `relation_wish`)" + 
					"VALUES" + 
					"	(?, ?, ?);")
				.setCatched(1, this)
				.setCatched(2, otherFaction)
				.setCatched(3, relation.name())
				.execute(ExecuteType.UPDATE) == null) {
					Factions.get().warn("[MySQL] inserting relation " + relation.name() + " for " + this.id + " failed");
			}

		} else {
			if (FactionsMySQL.get().prepare(
					"UPDATE `relation_wish` SET `relation_wish` = ? WHERE `faction` = ? AND `faction_to` = ?;")
				.setCatched(1, relation.name())
				.setCatched(2, this)
				.setCatched(3, otherFaction)
				.execute(ExecuteType.UPDATE) == null) {
					Factions.get().warn("[MySQL] updating relation " + relation.name() + " for " + this.id + " failed");
			}
		}
	}

	@Override
	public int getRelationCount(Relation relation) {
		this.poll();
		return this.relations.stream()
				.filter(wish -> wish.get("relation_wish") == relation.name())
				.collect(Collectors.toList())
				.size();
	}
	
	@Override
	public Map<String, Relation> getRelationWishes() {
		Map<String, Relation> wishes = new HashMap<>();
		
		// create a snapshot
		this.relations.stream().forEach(
			entry -> wishes.put(entry.get("faction_to"), Relation.valueOf(entry.get("relation_wish")))
		);
		
		return wishes;
	}
	
	@Override
	public void memberRefresh() {
		this.members.clear();
		if (this.isPlayerFreeType()) return;

		FPlayerColl.all().stream()
			.filter(fplayer -> fplayer.getFactionId() == this.getId())
			.forEach(this.members::add);
	}

	@Override
	public boolean memberAdd(FPlayer fplayer) {
		return !this.isPlayerFreeType() && this.members.add(fplayer);
	}

	@Override
	public boolean memberRemove(FPlayer fplayer) {
		return !this.isPlayerFreeType() && this.members.remove(fplayer);
	}

	@Override
	public int memberCount() {
		return this.members.size();
	}

	@Override
	public Set<FPlayer> getMembers() {
		// Return a snapshot of the FPlayer list, to prevent tampering and
		// concurrency issues
		return new HashSet<>(this.members);
	}

	@Override
	public Map<FLocation, Set<String>> getClaimOwnership() {
		List<Map<String, String>> results = FactionsMySQL.get().prepare("SELECT * FROM `faction_ownership` WHERE faction = ?")
			.setCatched(1, this)
			.execute(ExecuteType.SELECT);
		
		Map<FLocation, Set<String>> ownerships = new HashMap<>();
		
		if (results == null || results.isEmpty()) {
			return ownerships;
		}
		
		results.forEach(result -> {
			FLocation flocation = new FLocation(Bukkit.getWorld(UUID.fromString(result.get("world"))).getName(), Integer.valueOf(result.get("x")), Integer.valueOf(result.get("z")));
			AtomicBoolean found = new AtomicBoolean(false);
			
			ownerships.forEach((a,b) -> {
				if (a.equals(flocation)) {
					b.add(result.get("player"));
					found.set(true);
				}
			});
			
			if (!found.get()) {
				ownerships.put(flocation, new HashSet<>());
				ownerships.get(flocation).add(result.get("player"));
			}
		});
		
		return ownerships;
	}

	@Override
	public void clearAllClaimOwnership() {
		FactionsMySQL.get().prepare("DELETE FROM `faction_ownership` WHERE `faction` = ?")
			.setCatched(1, this)
			.execute(ExecuteType.UPDATE);
	}

	@Override
	public void clearClaimOwnership(Locality locality) {
		FactionsMySQL.get().prepare("DELETE FROM `faction_ownership` WHERE `faction` = ? AND `world` = ? AND `x` = ? AND `z` = ?")
			.setCatched(1, this)
			.setCatched(2, locality.getWorld().getUID().toString())
			.setCatched(3, locality.getChunkX())
			.setCatched(4, locality.getChunkZ())
			.execute(ExecuteType.UPDATE);
	}

	@Override
	public void clearClaimOwnership(FLocation loc) {
		FactionsMySQL.get().prepare("DELETE FROM `faction_ownership` WHERE `faction` = ? AND `world` = ? AND `x` = ? AND `z` = ?")
			.setCatched(1, this)
			.setCatched(2, loc.getWorld().getUID().toString())
			.setCatched(3, loc.getX())
			.setCatched(4, loc.getZ())
			.execute(ExecuteType.UPDATE);
	}

	@Override
	public void clearClaimOwnership(FPlayer player) {
		FactionsMySQL.get().prepare("DELETE FROM `faction_ownership` WHERE `faction` = ? AND `player` = ?")
			.setCatched(1, this)
			.setCatched(2, player)
			.execute(ExecuteType.UPDATE);
	}

	@Override
	public int getCountOfClaimsWithOwners() {
		return this.getClaimOwnership().size();
	}

	@Override
	public boolean doesLocationHaveOwnersSet(FLocation loc) {
		List<Map<String, String>> results = FactionsMySQL.get().prepare("SELECT * FROM `faction_ownership` WHERE `faction `= ? AND `world` = ? AND `x` = ? AND `z` = ? ")
			.setCatched(1, this)
			.setCatched(2, loc.getWorld().getUID().toString())
			.setCatched(3, loc.getX())
			.setCatched(4, loc.getZ())
			.execute(ExecuteType.SELECT);

		return results != null && !results.isEmpty();
	}

	@Override
	public boolean isPlayerInOwnerList(FPlayer player, FLocation loc) {
		return this.getOwnerList(loc).contains(player.getId());
	}

	public void setPlayerAsOwner(String player, FLocation loc) {
		String query = "INSERT INTO `faction_ownership` ( `world`, `x`, `z`, `player`, `faction`)\n" + 
				"VALUES\n" + 
				"	(?, ?, ?, ?, ?);\n";
		
		FactionsMySQL.get().prepare(query)
		.setCatched(1, loc.getWorld().getUID().toString())
		.setCatched(2, loc.getX())
		.setCatched(3, loc.getZ())
		.setCatched(4, player)
		.setCatched(5, this)
			.execute(ExecuteType.UPDATE);

	}
	
	@Override
	public void setPlayerAsOwner(FPlayer player, FLocation loc) {
		this.setPlayerAsOwner(player.getName(), loc);
	}

	@Override
	public void removePlayerAsOwner(FPlayer player, FLocation loc) {
		FactionsMySQL.get().prepare("DELETE FROM `faction_ownership` WHERE `faction` = ? AND `world` = ? AND `x` = ? AND `z` = ? AND `player` = ?")
			.setCatched(1, this)
			.setCatched(2, loc.getWorld().getUID().toString())
			.setCatched(3, loc.getX())
			.setCatched(4, loc.getZ())
			.setCatched(5, player)
			.execute(ExecuteType.UPDATE);
		
	}

	@Override
	public Set<String> getOwnerList(FLocation loc) {
		return this.getClaimOwnership().entrySet().stream()
			.filter(entry -> entry.getKey().equals(loc))
			.map(entry -> entry.getValue())
			.findFirst()
				.orElse(new HashSet<>());
	}

	@Override
	public String getOwnerListString(FLocation loc) {
		return this.getClaimOwnership().entrySet().stream()
				.filter(entry -> entry.getKey().equals(loc))
				.map(entry -> entry.getValue())
				.findFirst()
					.orElse(new HashSet<>())
				.toString();
	}

	@Override
	public boolean playerHasOwnershipRights(FPlayer fplayer, FLocation loc) {
		return this.getClaimOwnership().entrySet().stream()
				.filter(entry -> entry.getKey().equals(loc))
				.map(entry -> entry.getValue().contains(fplayer.getId()))
				.findFirst()
					.orElse(false);
	}

	@Override
	public void remove() {
		if (VaultEngine.getUtils().shouldBeUsed()) {
			VaultEngine.getUtils().setBalance(this.getAccountId(), 0);
		}

		// Clean the board
		Board.get().clean(this.id);

		this.getMembers().forEach(fplayer -> fplayer.resetFactionData());
		
		// Remove this from database
		FactionsMySQL.get().prepare("DELETE FROM `faction` WHERE `id` = ?")
			.setCatched(1, this)
			.execute(ExecuteType.UPDATE);
	
		FactionsMySQL.get().prepare("DELETE FROM `faction_announcements` WHERE `faction` = ?")
			.setCatched(1, this)
			.execute(ExecuteType.UPDATE);
		
		FactionsMySQL.get().prepare("DELETE FROM `faction_bans` WHERE `faction` = ?")
			.setCatched(1, this)
			.execute(ExecuteType.UPDATE);
		
		FactionsMySQL.get().prepare("DELETE FROM `faction_flags` WHERE `faction` = ?")
			.setCatched(1, this)
			.execute(ExecuteType.UPDATE);
		
		FactionsMySQL.get().prepare("DELETE FROM `faction_invites` WHERE `faction` = ?")
			.setCatched(1, this)
			.execute(ExecuteType.UPDATE);
		
		FactionsMySQL.get().prepare("DELETE FROM `faction_relations` WHERE `faction` = ? OR `faction_to` = ?")
			.setCatched(1, this)
			.setCatched(2, this)
			.execute(ExecuteType.UPDATE);
		
		FactionsMySQL.get().prepare("DELETE FROM `faction_warps` WHERE `faction` = ?")
			.setCatched(1, this)
			.execute(ExecuteType.UPDATE);
	}

	@Override
	public void setId(String id) {
		this.id = id;
		this.poll(true);
	}
	
	@Override
	public ConcurrentHashMap<String, LazyLocation> getAllWarps() {
		this.poll();
		return new ConcurrentHashMap<>(this.warps);
	}

	@Override
	public boolean removeWarp(String name) {
		this.warps.remove(name);
		
		FactionsMySQL.get().prepare("DELETE FROM `faction_warps` WHERE `faction` = ? AND `name` = ?")
			.setCatched(1, this)
			.setCatched(2, name)
			.execute(ExecuteType.UPDATE);

		return true;
	}

	@Override
	public LazyLocation getWarp(String name) {
		this.poll();
		return this.warps.get(name);
	}

	@Override
	public Optional<String> getWarpPassword(String warpName) {
		this.poll();
		if (this.warpPasswords.containsKey(warpName) && this.warpPasswords.get(warpName) != "") {
			return Optional.of(this.warpPasswords.get(warpName));
		}
		return Optional.empty();
	}

	@Override
	public void setWarp(String name, LazyLocation location, String password) {
		if (password == "") {
			this.warpPasswords.put(name, null);
		} else {
			password = "";
		}
		
		this.warps.put(name, location);
		
		if (FactionsMySQL.get().prepare("SELECT `name` FROM `faction_warps` WHERE faction = ? AND name = ?")
				.setCatched(1, this)
				.setCatched(2, name)
				.execute(ExecuteType.SELECT)
				.size() == 0) {
			
			if (FactionsMySQL.get().prepare(
					"INSERT INTO `faction_warps` (`faction`, `name`, `location`, `password`)" + 
					"VALUES" + 
					"	(?, ?, ?, ?);")
				.setCatched(1, this)
				.setCatched(2, name)
				.setCatched(3, LazyLocationAdapter.serialise(location))
				.setCatched(4, password)
				
				.execute(ExecuteType.UPDATE) == null) {
					Factions.get().warn("[MySQL] inserting warp " + name + " for " + this.id + " failed");
			}

		} else {
			if (FactionsMySQL.get().prepare(
					"UPDATE `faction_warps` SET `location` = ?, `password` = ? WHERE `name` = ? AND `faction` = ?;")
				.setCatched(1, LazyLocationAdapter.serialise(location))
				.setCatched(2, password)
				.setCatched(3, name)
				.setCatched(4, this)
				.execute(ExecuteType.UPDATE) == null) {
					Factions.get().warn("[MySQL] updating warp " + name + " for " + this.id + " failed");
			}
		}
	}

	@Override
	public void clearWarps() {
		this.warps.clear();
		this.warpPasswords.clear();
		
		FactionsMySQL.get().prepare("DELETE FROM `faction_warps` WHERE `faction` = ?")
			.setCatched(1, this)
			.execute(ExecuteType.UPDATE);
	}

}
