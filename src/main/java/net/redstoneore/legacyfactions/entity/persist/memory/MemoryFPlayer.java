package net.redstoneore.legacyfactions.entity.persist.memory;

import net.redstoneore.legacyfactions.Factions;
import net.redstoneore.legacyfactions.Role;
import net.redstoneore.legacyfactions.config.Config;
import net.redstoneore.legacyfactions.entity.FPlayer;
import net.redstoneore.legacyfactions.entity.Faction;
import net.redstoneore.legacyfactions.entity.FactionColl;
import net.redstoneore.legacyfactions.entity.persist.shared.SharedFPlayer;
import net.redstoneore.legacyfactions.event.EventFactionsRoleChanged;
import net.redstoneore.legacyfactions.expansion.chat.ChatMode;
import net.redstoneore.legacyfactions.lang.Lang;
import net.redstoneore.legacyfactions.util.LocationUtil;

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
	// CONSTRUCT
	// -------------------------------------------------- //
	
	/**
	 * Construct this if we're going to populate fields ourselves.
	 */
	public MemoryFPlayer() { }
	
	public MemoryFPlayer(String id) {
		this.id = id;
		this.resetFactionData();
		this.power = Config.powerPlayerStarting;
		this.lastPowerUpdateTime = System.currentTimeMillis();
		this.lastLoginTime = System.currentTimeMillis();
		this.powerBoost = 0.0;
		this.showScoreboard = Config.scoreboardDefaultEnabled;
		this.kills = 0;
		this.deaths = 0;

		if (!Config.newPlayerStartingFactionID.equals("0") && FactionColl.get().isValidFactionId(Config.newPlayerStartingFactionID)) {
			this.factionId = Config.newPlayerStartingFactionID;
		}
	}

	public MemoryFPlayer(FPlayer other) {
		this.factionId = other.getFactionId();
		this.id = other.getId();
		this.power = other.getPower();
		this.lastLoginTime = other.getLastLoginTime();
		this.setMapAutoUpdating(other.isMapAutoUpdating());
		this.setAutoClaimFor(other.getAutoClaimFor());
		this.setIsAutoSafeClaimEnabled(other.isAutoSafeClaimEnabled());
		this.setIsAutoWarClaimEnabled(other.isAutoWarClaimEnabled());
		this.setLoginPVPDisable(other.hasLoginPvpDisabled());
		this.powerBoost = other.getPowerBoost();
		this.role = other.getRole();
		this.title = other.getTitle();
		this.chatMode = other.getChatMode();
		this.spyingChat = other.isSpyingChat();
		this.setLastLocation(other.getLastLocation());
		this.isAdminBypassing = other.isAdminBypassing();
		this.showScoreboard = Config.scoreboardDefaultEnabled;
		this.kills = other.getKills();
		this.deaths = other.getDeaths();
		this.territoryTitlesOff = other.territoryTitlesOff();
	}
	
	// -------------------------------------------------- //
	// FIELDS
	// -------------------------------------------------- //
	
	protected String id;
	protected String name;
	protected String factionId = "0";
	protected Role role = Role.NORMAL;
	protected String title = "";
	protected double power;
	protected double powerBoost;
	protected long lastPowerUpdateTime;
	protected long lastLoginTime;
	protected ChatMode chatMode;
	protected boolean ignoreAllianceChat = false;
	protected boolean monitorJoins = false;
	protected boolean spyingChat = false;
	protected boolean showScoreboard = true;
	protected boolean isAdminBypassing = false;
	protected int kills, deaths;
	protected boolean willAutoLeave = true;
	protected boolean territoryTitlesOff = false;

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
	
	@Override
	public void setKills(int amount) {
		this.kills = amount;
	}
	
	@Override
	public void setDeaths(int amount) {
		this.deaths = amount;
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
			oldFaction.memberRemove(this);
		}
		faction.memberAdd(this);
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
		if (this.role == Role.COLEADER && Config.enableColeaders == false) {
			this.role = Role.NORMAL;
		}
		return this.role;
	}
	
	@Override
	public void setRole(Role role) {
		// Coleader check
		if (role == Role.COLEADER && Config.enableColeaders == false) {
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
		if (LocationUtil.isFactionsDisableIn(this)) return ChatMode.PUBLIC;
		// If we're in the wilderness or factions chat is disabled, default to public chat
		if (this.factionId.equals("0") || !Config.expansionsFactionsChat.enabled) {
			this.chatMode = ChatMode.PUBLIC;
		}
		return this.chatMode;
	}
	
	// -------------------------------------------------- //
	// GETTS AND SETTERS
	// -------------------------------------------------- //

	@Override
	public void setIgnoreAllianceChat(boolean ignore) {
		this.ignoreAllianceChat = ignore;
	}

	@Override
	public boolean isIgnoreAllianceChat() {
		if (LocationUtil.isFactionsDisableIn(this)) return true;
		return this.ignoreAllianceChat;
	}
	
	@Override
	public void setSpyingChat(boolean chatSpying) {
		this.spyingChat = chatSpying;
	}

	@Override
	public boolean isSpyingChat() {
		if (LocationUtil.isFactionsDisableIn(this)) return false;
		return this.spyingChat;
	}

	@Override
	public long getLastLoginTime() {
		return this.lastLoginTime;
	}

	@Override
	public void setLastLoginTime(long lastLoginTime) {
		this.losePowerFromBeingOffline();
		this.lastLoginTime = lastLoginTime;
		this.lastPowerUpdateTime = lastLoginTime;
		if (Config.noPVPDamageToOthersForXSecondsAfterLogin > 0) {
			this.setLoginPVPDisable(true);
		}
	}

	@Override
	public boolean territoryTitlesOff() {
		if (LocationUtil.isFactionsDisableIn(this)) return true;
		return this.territoryTitlesOff;
	}
	
	@Override
	public void territoryTitlesOff(boolean off) {
		this.territoryTitlesOff = off;
	}
	
	@Override
	public boolean showScoreboard() {
		if (LocationUtil.isFactionsDisableIn(this)) return false;
		return this.showScoreboard;
	}

	@Override
	public void setShowScoreboard(boolean show) {
		this.showScoreboard = show;
	}
	
}
