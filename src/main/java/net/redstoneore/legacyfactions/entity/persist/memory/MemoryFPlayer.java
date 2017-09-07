package net.redstoneore.legacyfactions.entity.persist.memory;


import net.redstoneore.legacyfactions.FLocation;
import net.redstoneore.legacyfactions.Factions;
import net.redstoneore.legacyfactions.Lang;
import net.redstoneore.legacyfactions.Role;
import net.redstoneore.legacyfactions.Volatile;
import net.redstoneore.legacyfactions.entity.Conf;
import net.redstoneore.legacyfactions.entity.Faction;
import net.redstoneore.legacyfactions.entity.FactionColl;
import net.redstoneore.legacyfactions.entity.persist.shared.SharedFPlayer;
import net.redstoneore.legacyfactions.event.EventFactionsRoleChanged;
import net.redstoneore.legacyfactions.expansion.chat.ChatMode;
import net.redstoneore.legacyfactions.locality.Locality;
import net.redstoneore.legacyfactions.util.WarmUpUtil;

import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.Statistic;


/**
 * Logged in players always have exactly one FPlayer instance. Logged out players may or may not have an FPlayer
 * instance. They will always have one if they are part of a faction. This is because only players with a faction are
 * saved to disk (in order to not waste disk space).
 * <br><br>
 * The FPlayer is linked to a bukkit player using the player name.
 * <br><br>
 * The same instance is always returned for the same player. This means you can use the == operator. No .equals method
 * necessary.
 * <br><br>
 * MemoryFPlayer should be used carefully by developers. You should be able to do what you want
 * with the available methods in FPlayer. If something is missing, open an issue on GitHub.
 * <br><br>
 * Do not store references to any fields. Always use the methods available.  
 */
public abstract class MemoryFPlayer extends SharedFPlayer {
	
	// -------------------------------------------------- //
	// STATIC FIELDS
	// -------------------------------------------------- //

	private static Locality DEFAULT_LASTSTOODAT = Locality.of(Bukkit.getWorlds().get(0).getSpawnLocation());
	
	// -------------------------------------------------- //
	// CONSTRUCT
	// -------------------------------------------------- //
	
	/**
	 * Construct this if we're going to populate fields ourselves.
	 */
	public MemoryFPlayer() { }
	
	public MemoryFPlayer(String id) {
		this.id = id;
		this.resetFactionData();
		this.power = Conf.powerPlayerStarting;
		this.lastPowerUpdateTime = System.currentTimeMillis();
		this.lastLoginTime = System.currentTimeMillis();
		this.mapAutoUpdating = false;
		this.autoClaimFor = null;
		this.autoSafeZoneEnabled = false;
		this.autoWarZoneEnabled = false;
		this.loginPvpDisabled = Conf.noPVPDamageToOthersForXSecondsAfterLogin > 0;
		this.powerBoost = 0.0;
		this.showScoreboard = Conf.scoreboardDefaultEnabled;
		this.kills = 0;
		this.deaths = 0;

		if (!Conf.newPlayerStartingFactionID.equals("0") && FactionColl.get().isValidFactionId(Conf.newPlayerStartingFactionID)) {
			this.factionId = Conf.newPlayerStartingFactionID;
		}
	}

	public MemoryFPlayer(MemoryFPlayer other) {
		this.factionId = other.factionId;
		this.id = other.id;
		this.power = other.power;
		this.lastLoginTime = other.lastLoginTime;
		this.mapAutoUpdating = other.mapAutoUpdating;
		this.autoClaimFor = other.autoClaimFor;
		this.autoSafeZoneEnabled = other.autoSafeZoneEnabled;
		this.autoWarZoneEnabled = other.autoWarZoneEnabled;
		this.loginPvpDisabled = other.loginPvpDisabled;
		this.powerBoost = other.powerBoost;
		this.role = other.role;
		this.title = other.title;
		this.chatMode = other.chatMode;
		this.spyingChat = other.spyingChat;
		this.lastStoodAt = other.lastStoodAt;
		this.isAdminBypassing = other.isAdminBypassing;
		this.showScoreboard = Conf.scoreboardDefaultEnabled;
		this.kills = other.kills;
		this.deaths = other.deaths;
		this.territoryTitlesOff = other.territoryTitlesOff;
	}
	
	// -------------------------------------------------- //
	// FIELDS
	// -------------------------------------------------- //
	
	protected String factionId = "0";
	protected Role role = Role.NORMAL;
	protected String title = "";
	protected double power;
	protected double powerBoost;
	protected long lastPowerUpdateTime;
	protected long lastLoginTime;
	protected ChatMode chatMode;
	protected boolean ignoreAllianceChat = false;
	protected String id;
	protected String name;
	protected boolean monitorJoins;
	protected boolean spyingChat = false;
	protected boolean showScoreboard = true;
	protected WarmUpUtil.Warmup warmup;
	protected int warmupTask;
	protected boolean isAdminBypassing = false;
	protected int kills, deaths;
	protected boolean willAutoLeave = true;
	protected boolean territoryTitlesOff = false;
	
	protected transient Locality lastStoodAt = DEFAULT_LASTSTOODAT;
	protected transient boolean mapAutoUpdating;
	protected transient Faction autoClaimFor;
	protected transient boolean autoSafeZoneEnabled;
	protected transient boolean autoWarZoneEnabled;
	protected transient boolean loginPvpDisabled;
	protected transient long lastFrostwalkerMessage;


	@Override
	public String getId() {
		return id;
	}
	
	@Override
	public void setId(String id) {
		this.id = id;
	}
	
	// -------------------------------------------------- //
	// Title, Name, Faction Tag and Chat
	// -------------------------------------------------- //
	
	@Override
	public String getTitle() {
		return this.hasFaction() ? this.title : Lang.NOFACTION_PREFIX.toString();
	}

	@Override
	public void setTitle(String title) {
		this.title = title;
	}

	@Override
	public String getName() {
		if (this.name == null) {
			OfflinePlayer offline = Bukkit.getOfflinePlayer(UUID.fromString(this.getId()));
			this.name = offline.getName() != null ? offline.getName() : this.getId();
		}
		return this.name;
	}
	
	@Override
	public void setName(String name) {
		this.name = name;
	}
	
	@Override
	public int getKills() {
		return isOnline() ? getPlayer().getStatistic(Statistic.PLAYER_KILLS) : this.kills;
	}
	
	@Override
	public int getDeaths() {
		return isOnline() ? getPlayer().getStatistic(Statistic.DEATHS) : this.deaths;
	}

	// -------------------------------------------------- //
	// POWER
	// -------------------------------------------------- //
	
	@Override
	public double getPower() {
		this.updatePower();
		return this.power;
	}

	@Override
	public void alterPower(double delta) {
		this.power += delta;
		if (this.power > this.getPowerMax()) {
			this.power = this.getPowerMax();
		} else if (this.power < this.getPowerMin()) {
			this.power = this.getPowerMin();
		}
	}
	
	@Override
	public long getLastPowerUpdated() {
		return this.lastPowerUpdateTime;
	}

	@Override
	public void setLastPowerUpdated(long time) {
		this.lastPowerUpdateTime = time;
	}
	
	// -------------------------------------------------- //
	// EVENTS
	// -------------------------------------------------- //

	@Override
	public void onLogin() {
		this.kills = getPlayer().getStatistic(Statistic.PLAYER_KILLS);
		this.deaths = getPlayer().getStatistic(Statistic.DEATHS);
	}

	@Override
	public void onLogout() {
		// Ensure power is up to date
		this.getPower();
		
		// Update last login time
		this.setLastLoginTime(System.currentTimeMillis());
		
		// Store statistics 
		this.kills = getPlayer().getStatistic(Statistic.PLAYER_KILLS);
		this.deaths = getPlayer().getStatistic(Statistic.DEATHS);
		
		// Remove from stuck map
		if (Volatile.get().stuckMap().containsKey(this.getPlayer().getUniqueId())) {
			Volatile.get().stuckMap().remove(this.getPlayer().getUniqueId());
			Volatile.get().stuckTimers().remove(this.getPlayer().getUniqueId());
		}
		
		if (!this.getFaction().isWilderness()) {
			// Toggle
			this.getFaction().memberLoggedOff();
			
			// Notify members if required
			this.getFaction().getWhereOnline(true)
				.stream()
				.filter(fplayer -> fplayer == this || !fplayer.isMonitoringJoins())
				.forEach(fplayer -> fplayer.sendMessage(Lang.FACTION_LOGOUT, this.getName()));
		}
	}
	
	@Override
	public Faction getFaction() {
		if (this.getFactionId() == null) {
			this.factionId = "0";
		}
		return FactionColl.get().getFactionById(this.getFactionId());
	}

	@Override
	public String getFactionId() {
		return this.factionId;
	}

	@Override
	public void setFaction(Faction faction) {
		Faction oldFaction = this.getFaction();
		if (oldFaction != null) {
			oldFaction.removeFPlayer(this);
		}
		faction.addFPlayer(this);
		this.factionId = faction.getId();
	}

	@Override
	public void setMonitorJoins(boolean monitor) {
		this.monitorJoins = monitor;
	}

	@Override
	public boolean isMonitoringJoins() {
		return this.monitorJoins;
	}
	
	@Override
	public Role getRole() {
		// Coleader check
		if (this.role == Role.COLEADER && Conf.enableColeaders == false) {
			this.role = Role.NORMAL;
		}
		return this.role;
	}
	
	@Override
	public void setRole(Role role) {
		// Coleader check
		if (role == Role.COLEADER && Conf.enableColeaders == false) {
			role = Role.NORMAL;
		}

		Role previousRole = this.role;
		this.role = role;
		EventFactionsRoleChanged.create(this.getFaction(), this, previousRole, role).call();
	}
	
	@Override
	public double getPowerBoost() {
		return this.powerBoost;
	}

	@Override
	public void setPowerBoost(double powerBoost) {
		this.powerBoost = powerBoost;
	}

	@Override
	public boolean willAutoLeave() {
		return this.willAutoLeave;
	}

	@Override
	public void setAutoLeave(boolean willLeave) {
		this.willAutoLeave = willLeave;
		Factions.get().debug(name + " set autoLeave to " + willLeave);
	}

	@Override
	public long getLastFrostwalkerMessage() {
		return this.lastFrostwalkerMessage;
	}

	@Override
	public void setLastFrostwalkerMessage() {
		this.lastFrostwalkerMessage = System.currentTimeMillis();
	}

	@Override
	public Faction getAutoClaimFor() {
		return this.autoClaimFor;
	}

	@Override
	public void setAutoClaimFor(Faction faction) {
		this.autoClaimFor = faction;
		if (this.autoClaimFor != null) {
			if (!this.autoClaimFor.isSafeZone()) {
				this.autoSafeZoneEnabled = false;
			}
			
			if (!this.autoClaimFor.isWarZone()) {
				this.autoWarZoneEnabled = false;
			}
		}
	}

	@Override
	public boolean isAutoSafeClaimEnabled() {
		return this.autoSafeZoneEnabled;
	}

	@Override
	public void setIsAutoSafeClaimEnabled(boolean enabled) {
		this.autoSafeZoneEnabled = enabled;
		if (enabled) {
			this.autoClaimFor = null;
			this.autoWarZoneEnabled = false;
		}
	}

	@Override
	public boolean isAutoWarClaimEnabled() {
		return this.autoWarZoneEnabled;
	}

	@Override
	public void setIsAutoWarClaimEnabled(boolean enabled) {
		this.autoWarZoneEnabled = enabled;
		if (enabled) {
			this.autoClaimFor = null;
			this.autoSafeZoneEnabled = false;
		}
	}

	@Override
	public boolean isAdminBypassing() {
		return this.isAdminBypassing;
	}
	
	@Override
	public void setIsAdminBypassing(boolean val) {
		this.isAdminBypassing = val;
	}

	@Override
	public void setChatMode(ChatMode chatMode) {
		this.chatMode = chatMode;
	}
	
	@Override
	public ChatMode getChatMode() {
		// If we're in the wilderness or factions chat is disabled, default to public chat
		if (this.factionId.equals("0") || !Conf.expansionsFactionsChat.enabled) {
			this.chatMode = ChatMode.PUBLIC;
		}
		return this.chatMode;
	}


	@Override
	public void resetFactionData() {
		// clean up any territory ownership in old faction, if there is one
		if (factionId != null && FactionColl.get().isValidFactionId(this.getFactionId())) {
			Faction currentFaction = this.getFaction();
			currentFaction.removeFPlayer(this);
			if (currentFaction.isNormal()) {
				currentFaction.clearClaimOwnership(this);
			}
		}

		this.factionId = "0"; // The default neutral faction
		this.chatMode = ChatMode.PUBLIC;
		this.role = Role.NORMAL;
		this.title = "";
		this.autoClaimFor = null;	}

	// -------------------------------------------------- //
	// GETTS AND SETTERS
	// -------------------------------------------------- //

	@Override
	public void setIgnoreAllianceChat(boolean ignore) {
		this.ignoreAllianceChat = ignore;
	}

	@Override
	public boolean isIgnoreAllianceChat() {
		return this.ignoreAllianceChat;
	}
	
	@Override
	public void setSpyingChat(boolean chatSpying) {
		this.spyingChat = chatSpying;
	}

	@Override
	public boolean isSpyingChat() {
		return this.spyingChat;
	}

	@Override
	public long getLastLoginTime() {
		return lastLoginTime;
	}

	@Override
	public void setLastLoginTime(long lastLoginTime) {
		this.losePowerFromBeingOffline();
		this.lastLoginTime = lastLoginTime;
		this.lastPowerUpdateTime = lastLoginTime;
		if (Conf.noPVPDamageToOthersForXSecondsAfterLogin > 0) {
			this.loginPvpDisabled = true;
		}
	}

	@Override
	public boolean isMapAutoUpdating() {
		return this.mapAutoUpdating;
	}

	@Override
	public void setMapAutoUpdating(boolean mapAutoUpdating) {
		this.mapAutoUpdating = mapAutoUpdating;
	}

	@Override
	public boolean hasLoginPvpDisabled() {
		if (!this.loginPvpDisabled) {
			return false;
		}
		if (this.lastLoginTime + (Conf.noPVPDamageToOthersForXSecondsAfterLogin * 1000) < System.currentTimeMillis()) {
			this.loginPvpDisabled = false;
			return false;
		}
		return true;
	}

	@Override
	public FLocation getLastStoodAt() {
		return FLocation.valueOf(this.lastStoodAt.getLocation());
	}
	
	@Override
	public Locality getLastLocation() {
		return this.lastStoodAt;
	}

	@Override
	public void setLastStoodAt(FLocation flocation) {
		this.lastStoodAt = Locality.of(flocation.getChunk());
	}

	@Override
	public boolean territoryTitlesOff() {
		return this.territoryTitlesOff;
	}
	
	@Override
	public void territoryTitlesOff(boolean off) {
		this.territoryTitlesOff = off;
	}
	
	@Override
	public boolean showScoreboard() {
		return this.showScoreboard;
	}

	@Override
	public void setShowScoreboard(boolean show) {
		this.showScoreboard = show;
	}
	
	@Override
	public void clearWarmup() {
		if (warmup != null) {
			Bukkit.getScheduler().cancelTask(warmupTask);
			this.stopWarmup();
		}
	}

	@Override
	public void stopWarmup() {
		warmup = null;
	}

	@Override
	public boolean isWarmingUp() {
		return warmup != null;
	}

	@Override
	public WarmUpUtil.Warmup getWarmupType() {
		return warmup;
	}

	@Override
	public void addWarmup(WarmUpUtil.Warmup warmup, int taskId) {
		if (this.warmup != null) {
			this.clearWarmup();
		}
		this.warmup = warmup;
		this.warmupTask = taskId;
	}
	
}
