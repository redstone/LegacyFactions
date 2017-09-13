package net.redstoneore.legacyfactions.entity.persist.shared;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;

import net.redstoneore.legacyfactions.EconomyParticipator;
import net.redstoneore.legacyfactions.FLocation;
import net.redstoneore.legacyfactions.Factions;
import net.redstoneore.legacyfactions.Lang;
import net.redstoneore.legacyfactions.Relation;
import net.redstoneore.legacyfactions.RelationParticipator;
import net.redstoneore.legacyfactions.Role;
import net.redstoneore.legacyfactions.announcement.Announcements;
import net.redstoneore.legacyfactions.entity.Board;
import net.redstoneore.legacyfactions.entity.Conf;
import net.redstoneore.legacyfactions.entity.FPlayer;
import net.redstoneore.legacyfactions.entity.FPlayerColl;
import net.redstoneore.legacyfactions.entity.Faction;
import net.redstoneore.legacyfactions.entity.FactionColl;
import net.redstoneore.legacyfactions.flag.Flags;
import net.redstoneore.legacyfactions.integration.vault.VaultEngine;
import net.redstoneore.legacyfactions.lang.LangBuilder;
import net.redstoneore.legacyfactions.locality.Locality;
import net.redstoneore.legacyfactions.util.LazyLocation;
import net.redstoneore.legacyfactions.util.MiscUtil;
import net.redstoneore.legacyfactions.util.RelationUtil;
import net.redstoneore.legacyfactions.util.TextUtil;
import net.redstoneore.legacyfactions.warp.FactionWarps;

public abstract class SharedFaction implements Faction, EconomyParticipator {
	
	// -------------------------------------------------- //
	// FIELDS
	// -------------------------------------------------- //

	private transient long lastPlayerLoggedOffTime;
	private transient FactionWarps factionWarps = new FactionWarps(this);
	private transient Announcements announcements = new Announcements(this);
	
	// -------------------------------------------------- //
	// FACTION INFORMATION
	// -------------------------------------------------- //
	
	@Override
	public String getAccountId() {
		String accountId = "faction-" + this.getId();
		
		// We need to override the default money given to players.
		if (!VaultEngine.getUtils().hasAccount(accountId)) {
			VaultEngine.getUtils().setBalance(accountId, 0);
		}

		return accountId;
	}
	
	@Override
	public FactionWarps warps() {
		return this.factionWarps;
	}
	
	
	@Override
	public String getTag(String prefix) {
		return prefix + this.getTag();
	}

	@Override
	public String getTag(Faction otherFaction) {
		if (otherFaction == null) {
			return this.getTag();
		}
		return this.getTag(this.getColorTo(otherFaction).toString());
	}
	
	@Override
	public String getTag(FPlayer otherFplayer) {
		if (otherFplayer == null) {
			return this.getTag();
		}
		return this.getTag(this.getColorTo(otherFplayer).toString());
	}
	
	@Override
	public Announcements announcements() {
		return this.announcements;
	}
	
	public void sendUnreadAnnouncements(FPlayer fplayer) {
		if (!this.getAnnouncements().containsKey(fplayer.getId())) {
			return;
		}
		fplayer.sendMessage(ChatColor.LIGHT_PURPLE + "--Unread Faction Announcements--");
		
		this.getAnnouncements().get(fplayer.getId()).stream()
			.forEach(message -> fplayer.sendMessage(message));
		
		fplayer.sendMessage(ChatColor.LIGHT_PURPLE + "--Unread Faction Announcements--");
		this.getAnnouncements().remove(fplayer.getId());
	}

	// STATS
	
	public int getKills() {
		return this.getMembers().stream()
			.map(fplayer -> fplayer.getKills())
			.collect(Collectors.summingInt(Integer::intValue));
	}

	public int getDeaths() {
		return this.getMembers().stream()
			.map(fplayer -> fplayer.getDeaths())
			.collect(Collectors.summingInt(Integer::intValue));
	}
	
	// -------------------------------------------------- //
	// TYPE
	// -------------------------------------------------- //

	public boolean isNormal() {
		return !(this.isWilderness() || this.isSafeZone() || this.isWarZone());
	}

	public boolean isNone() {
		return this.getId().equals("0");
	}

	public boolean isWilderness() {
		return this.getId().equals("0");
	}

	public boolean isSafeZone() {
		return this.getId().equals("-1");
	}

	public boolean isWarZone() {
		return this.getId().equals("-2");
	}

	public boolean isPlayerFreeType() {
		return this.isSafeZone() || this.isWarZone();
	}
	
	// -------------------------------------------------- //
	// COMPARISON
	// -------------------------------------------------- //
	
	@Override
	public String getComparisonTag() {
		return MiscUtil.getComparisonString(this.getTag());
	}
	
	// -------------------------------------------------- //
	// HOMES
	// -------------------------------------------------- //

	@Override
	public Location getHome() {
		return this.getHome(true);
	}
	
	@Override
	public boolean hasHome() {
		return this.getHome(false) != null;
	}
	
	@Override
	public void confirmValidHome() {
		if (!Conf.homesMustBeInClaimedTerritory || this.getHome(false) == null || (this.getHome(false) != null && Board.get().getFactionAt(Locality.of(this.getHome(false))) == this)) {
			return;
		}
		
		this.sendMessage(TextUtil.parseColor(Lang.GENERIC_HOMEREMOVED.toString()));
		this.setHome(null); 
	}

	// -------------------------------------------------- //
	// POWER
	// -------------------------------------------------- //
	
	@Override
	public double getPower() {
		if (this.hasPermanentPower()) {
			return this.getPermanentPower();
		}

		double ret = 0;
		for (FPlayer fplayer : this.getMembers()) {
			ret += fplayer.getPower();
		}
		if (Conf.powerFactionMax > 0 && ret > Conf.powerFactionMax) {
			ret = Conf.powerFactionMax;
		}
		return ret + this.getPowerBoost();
	}


	@Override
	public boolean hasPermanentPower() {
		return this.getPermanentPower() != null;
	}

	@Override
	public double getPowerMax() {
		if (this.hasPermanentPower()) {
			return this.getPermanentPower();
		}

		double ret = 0;
		for (FPlayer fplayer : this.getMembers()) {
			ret += fplayer.getPowerMax();
		}
		if (Conf.powerFactionMax > 0 && ret > Conf.powerFactionMax) {
			ret = Conf.powerFactionMax;
		}
		return ret + this.getPowerBoost();
	}

	@Override
	public int getPowerRounded() {
		return (int) Math.round(this.getPower());
	}

	@Override
	public int getPowerMaxRounded() {
		return (int) Math.round(this.getPowerMax());
	}
	
	// -------------------------------------------------- //
	// LAND
	// -------------------------------------------------- //
	
	@Override
	public int getLandRounded() {
		return Board.get().getFactionCoordCount(this);
	}

	@Override
	public int getLandRoundedInWorld(String worldName) {
		return this.getLandRoundedInWorld(Bukkit.getWorld(worldName));
	}

	@Override
	public int getLandRoundedInWorld(World world) {
		return Board.get().getFactionCoordCountInWorld(this, world);
	}

	@Override
	public boolean hasLandInflation() {
		return this.getLandRounded() > this.getPowerRounded();
	}
	
	@Override
	public Set<FLocation> getAllClaims() {
		return Board.get().getAllClaims(this);
	}
	
	// -------------------------------------------------- //
	// STYLE
	// -------------------------------------------------- //
	
	@Override
	public boolean hasForcedMapCharacter() {
		return this.getForcedMapCharacter() != null;
	}

	@Override
	public boolean hasForcedMapColour() {
		return this.getForcedMapColour() != null;
	}
	
	// -------------------------------------------------- //
	// MEMBERS
	// -------------------------------------------------- //

	@Override
	public Set<FPlayer> getFPlayersWhereOnline(boolean online) {
		return this.getWhereOnline(online);
	}
	
	@Override
	public Set<FPlayer> getWhereOnline(boolean online) {
		if (!this.isNormal() || this.getMembers().size() == 0) return new HashSet<>();
		
		return this.getMembers().stream()
				.filter(fplayer -> fplayer.isOnline() == online)
				.collect(Collectors.toSet());
	}

	@Override
	public FPlayer getFPlayerAdmin() {
		return this.getOwner();
	}

	@Override
	public FPlayer getOwner() {
		if (!this.isNormal()) return null;
		
		return this.getMembers().stream()
				.filter(fplayer -> fplayer.getRole() == Role.ADMIN)
				.findFirst()
				.orElse(null);
	}

	@Override
	public ArrayList<FPlayer> getFPlayersWhereRole(Role role) {
		return this.getWhereRole(role);
	}
	
	@Override
	public ArrayList<FPlayer> getWhereRole(Role role) {
		if (!this.isNormal()) return new ArrayList<>();

		return this.getMembers().stream()
				.filter(fplayer -> fplayer.getRole() == role)
				.collect(Collectors.toCollection(ArrayList::new));

	}

	@Override
	public ArrayList<Player> getOnlinePlayers() {
		if (this.isPlayerFreeType()) return new ArrayList<>();

		return Factions.get().getServer().getOnlinePlayers().stream()
				.filter(player -> FPlayerColl.get(player).getFaction() == this)
				.collect(Collectors.toCollection(ArrayList::new));
	}
	
	// slightly faster check than getOnlinePlayers() if you just want to see if
	// there are any players online
	@Override
	public boolean hasPlayersOnline() {
		// only real factions can have players online, not safe zone / war zone
		if (this.isPlayerFreeType()) return false;
		
		Optional<? extends Player> found = Factions.get().getServer().getOnlinePlayers().stream()
				.filter(player -> FPlayerColl.get(player) != null && FPlayerColl.get(player) == this)
				.findFirst();
		
		if (found.isPresent()) return true;
		
		// even if all players are technically logged off, maybe someone was on
		// recently enough to not consider them officially offline yet
		return Conf.considerFactionsReallyOfflineAfterXMinutes > 0 && System.currentTimeMillis() <  lastPlayerLoggedOffTime + (Conf.considerFactionsReallyOfflineAfterXMinutes * 60000);
	}

	@Override
	public void memberLoggedOff() {
		if (!this.isNormal()) return;
		lastPlayerLoggedOffTime = System.currentTimeMillis();
	}
	
	/**
	 * Updates the lastPlayerLoggedOffTime field
	 * @param value
	 */
	public void setLastPlayerLoggedOffTime(long value) {
		this.lastPlayerLoggedOffTime = value;
	}
	
	/**
	 * Retrives the lastPlayerLoggedOffTime field
	 * @return
	 */
	public long getLastPlayerLoggedOffTime() {
		return this.lastPlayerLoggedOffTime;
	}
	

	

	// used when current leader is about to be removed from the faction;
	// promotes new leader, or disbands faction if no other members left
	public void promoteNewLeader() {
		if (!this.isNormal()) {
			return;
		}
		if (this.isPermanent() && Conf.permanentFactionsDisableLeaderPromotion) {
			return;
		}

		FPlayer oldLeader = this.getFPlayerAdmin();

		// get list of coleaders, mods and then normal members to promote from.
		ArrayList<FPlayer> replacements = this.getFPlayersWhereRole(Role.COLEADER);
		if (replacements == null || replacements.isEmpty()) {
			replacements = this.getFPlayersWhereRole(Role.MODERATOR);
			if (replacements == null || replacements.isEmpty()) {
				replacements = this.getFPlayersWhereRole(Role.NORMAL);
			}
		}

		if (replacements == null || replacements.isEmpty()) { // faction admin  is the only  member; one-man  faction
			if (this.isPermanent()) {
				if (oldLeader != null) {
					oldLeader.setRole(Role.NORMAL);
				}
				return;
			}

			// no members left and faction isn't permanent, so disband it
			if (Conf.logFactionDisband) {
				Factions.get().log("The faction " + this.getTag() + " (" + this.getId() + ") has been disbanded since it has no members left.");
			}

			FPlayerColl.all(fplayer -> fplayer.sendMessage(Lang.LEAVE_DISBANDED, this.getTag(fplayer)));

			FactionColl.get().removeFaction(getId());
		} else { // promote new faction admin
			if (oldLeader != null) {
				oldLeader.setRole(Role.NORMAL);
			}
			replacements.get(0).setRole(Role.ADMIN);
			
			LangBuilder message;
			if (oldLeader != null) { 
				message = Lang.LEAVE_NEWADMINPROMOTED_PLAYER.getBuilder().parse().replace("<player>", oldLeader.getName());
			} else {
				message = Lang.LEAVE_NEWADMINPROMOTED_UNKNOWN.getBuilder().parse();
			}
			message.replace("<new-admin>", replacements.get(0).getName())
				.sendTo(this);
			
			Factions.get().log("Faction " + this.getTag() + " (" + this.getId() + ") admin was removed. Replacement admin: " + replacements.get(0).getName());
		}
	}
	
	// -------------------------------------------------- //
	// FLAGS & STATES
	// -------------------------------------------------- //

	@Override
	public boolean isPowerFrozen() {
		if (Conf.raidablePowerFreeze == 0) return false;

		return System.currentTimeMillis() - this.getLastDeath() < Conf.raidablePowerFreeze * 1000;
	}
	
	@Override
	public boolean isPermanent() {
		return this.getFlag(Flags.PERMANENT) || !this.isNormal();
	}

	@Override
	public void setPermanent(boolean isPermanent) {
		this.setFlag(Flags.PERMANENT, isPermanent);
	}
	
	@Override
	public boolean getOpen() {
		return this.getFlag(Flags.OPEN);
	}

	@Override
	public void setOpen(boolean isOpen) {
		this.setFlag(Flags.OPEN, isOpen);
	}

	@Override
	public boolean isPeaceful() {
		return this.getFlag(Flags.PEACEFUL);
	}

	@Override
	public void setPeaceful(boolean isPeaceful) {
		this.setFlag(Flags.PEACEFUL, isPeaceful);
	}

	@Override
	public void setPeacefulExplosionsEnabled(boolean val) {
		this.setFlag(Flags.EXPLOSIONS, val);
	}

	@Override
	public boolean getPeacefulExplosionsEnabled() {
		return this.getFlag(Flags.EXPLOSIONS);
	}

	@Override
	public boolean noExplosionsInTerritory() {
		return (this.isPeaceful() && !this.getPeacefulExplosionsEnabled()) || this.isSafeZone();
	}

	@Override
	public boolean noCreeperExplosions(Location location) {
		return (
			this.isWilderness() && Conf.wildernessBlockCreepers && !Conf.worldsNoWildernessProtection.contains(location.getWorld().getName()) ||
			this.isNormal() && (this.hasPlayersOnline() ? Conf.territoryBlockCreepers : Conf.territoryBlockCreepersWhenOffline) ||
			this.isWarZone() && Conf.warZoneBlockCreepers ||
			this.isSafeZone()
		);
	}

	@Override
	public boolean noPvPInTerritory() {
		return this.isSafeZone() || (this.getFlag(Flags.PEACEFUL) && Conf.peacefulTerritoryDisablePVP);
	}
	
	@Override
	public boolean noMonstersInTerritory() {
		return this.isSafeZone() || (this.getFlag(Flags.PEACEFUL) && Conf.peacefulTerritoryDisableMonsters);
	}
	
	// -------------------------------------------------- //
	// RELATION AND RELATION COLOURS
	// -------------------------------------------------- //

	@Override
	public String describe() {
		return this.describeTo(null);
	}
	
	@Override
	public String describeTo(RelationParticipator that, boolean ucfirst) {
		return RelationUtil.describeThatToMe(this, that, ucfirst);
	}

	@Override
	public String describeTo(RelationParticipator that) {
		return RelationUtil.describeThatToMe(this, that);
	}

	@Override
	public Relation getRelationTo(RelationParticipator rp) {
		return RelationUtil.getRelationTo(this, rp);
	}

	@Override
	public Relation getRelationTo(RelationParticipator rp, boolean ignorePeaceful) {
		return RelationUtil.getRelationTo(this, rp, ignorePeaceful);
	}

	@Override
	public ChatColor getColorTo(RelationParticipator rp) {
		return RelationUtil.getColorOfThatToMe(this, rp);
	}
	
	@Override
	public boolean hasMaxRelations(Faction them, Relation rel, Boolean silent) {
		if (!Conf.maxRelations.containsKey(rel)) return false;
		if (Conf.maxRelations.get(rel) < 0) return false;
		
		int maxRelations = Conf.maxRelations.get(rel);
		
		if (this.getRelationCount(rel) >= maxRelations) {
		 	if (!silent) this.sendMessage(Lang.COMMAND_RELATIONS_EXCEEDS_ME, maxRelations, rel.getPluralTranslation());
			return true;
		}
			
		if (them.getRelationCount(rel) > maxRelations) {
			if (!silent) this.sendMessage(Lang.COMMAND_RELATIONS_EXCEEDS_THEY, maxRelations, rel.getPluralTranslation());
			return true;
		}
		
		return false;
	}
	
	@Override
	public int getRelationCount(Relation relation) {
		int count = 0;
		for (Faction faction : FactionColl.get().getAllFactions()) {
			if (faction.getRelationTo(this) == relation) {
				count++;
			}
		}
		return count;
	}
	
	// -------------------------------------------------- //
	// MESSAGES
	// -------------------------------------------------- //
	
	@Override
	public void sendMessage(String message, Object... args) {
		message = TextUtil.get().parse(message, args);

		for (FPlayer fplayer : this.getFPlayersWhereOnline(true)) {
			fplayer.sendMessage(message);
		}
	}

	@Override
	public void sendMessage(Lang translation, Object... args) {
		this.sendMessage(translation.toString(), args);
	}

	@Override
	public void sendMessage(String message) {
		for (FPlayer fplayer : this.getFPlayersWhereOnline(true)) {
			fplayer.sendMessage(message);
		}
	}

	@Override
	public void sendMessage(List<String> messages) {
		for (FPlayer fplayer : this.getFPlayersWhereOnline(true)) {
			fplayer.sendMessage(messages);
		}
	}
	
	@Override
	public void sendPlainMessage(String message) {
		this.getFPlayersWhereOnline(true).forEach(fplayer -> fplayer.sendMessage(message));
	}

	@Override
	public void sendPlainMessage(List<String> messages) {
		this.getFPlayersWhereOnline(true).forEach(fplayer -> fplayer.sendMessage(messages));
	}

	// -------------------------------------------------- //
	// ABSTRACT METHODS
	// -------------------------------------------------- //
	// These abstract methods are used by our API utilities. This allows us to create beautiful
	// abstractions! 
	
	public abstract ConcurrentHashMap<String, LazyLocation> getAllWarps();
	public abstract boolean removeWarp(String name);
	public abstract LazyLocation getWarp(String name);
	public abstract Optional<String> getWarpPassword(String warpName);
	public abstract void setWarp(String name, LazyLocation loc, String password);
	public abstract void clearWarps();
	
	public abstract Map<String, List<String>> getAnnouncements();
	public abstract void addAnnouncement(FPlayer fPlayer, String msg);
	public abstract void removeAnnouncements(FPlayer fPlayer);

	// -------------------------------------------------- //
	// DEPRECATED
	// -------------------------------------------------- //
	
	@Deprecated
	public void msg(String message, Object... args) {
		this.sendMessage(message, args);
	}

	@Deprecated
	public void msg(Lang translation, Object... args) {
		this.sendMessage(translation, args);
	}
	
}
