package net.redstoneore.legacyfactions.entity.persist.mysql;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.Statistic;
import org.bukkit.entity.Player;

import net.redstoneore.legacyfactions.Role;
import net.redstoneore.legacyfactions.entity.FPlayer;
import net.redstoneore.legacyfactions.entity.FPlayerColl;
import net.redstoneore.legacyfactions.entity.Faction;
import net.redstoneore.legacyfactions.entity.FactionColl;
import net.redstoneore.legacyfactions.entity.persist.mysql.MySQLPrepared.ExecuteType;
import net.redstoneore.legacyfactions.entity.persist.shared.SharedFPlayer;
import net.redstoneore.legacyfactions.expansion.chat.ChatMode;
import net.redstoneore.legacyfactions.util.LocationUtil;

public class MySQLFPlayer extends SharedFPlayer {

	// -------------------------------------------------- //
	// CONSTRUCT
	// -------------------------------------------------- //
	
	protected MySQLFPlayer(FPlayer fplayer) {
		this.id = fplayer.getId();
		this.setFaction(fplayer.getFactionId());
		this.alterPower(fplayer.getPower());
		this.setPowerBoost(fplayer.getPowerBoost());
		this.setLastLoginTime(fplayer.getLastLoginTime());
		this.setMapAutoUpdating(fplayer.isMapAutoUpdating());
		this.setAutoClaimFor(fplayer.getAutoClaimFor());
		this.setIsAutoSafeClaimEnabled(fplayer.isAutoSafeClaimEnabled());
		this.setIsAutoWarClaimEnabled(fplayer.isAutoWarClaimEnabled());
		this.setLoginPVPDisable(fplayer.hasLoginPvpDisabled());
		this.setRole(fplayer.getRole());
		this.setTitle(fplayer.getTitle());
		this.setChatMode(fplayer.getChatMode());
		this.setSpyingChat(fplayer.isSpyingChat());
		this.setLastLocation(fplayer.getLastLocation());
		this.setIsAdminBypassing(fplayer.isAdminBypassing());
		this.setShowScoreboard(fplayer.showScoreboard());
		this.setKills(fplayer.getKills());
		this.setDeaths(fplayer.getDeaths());
		this.territoryTitlesOff(fplayer.territoryTitlesOff());
	}

	protected MySQLFPlayer(String id) {
		this.setId(id);
		
		// Check if this is a new player
		if (this.getName() == null && Bukkit.getOfflinePlayer(UUID.fromString(id)) != null) {
			OfflinePlayer player = Bukkit.getOfflinePlayer(UUID.fromString(id));
			if (player.isOnline()) {
				this.onLogin();
			}
		}
	}
	
	protected MySQLFPlayer(Map<String, String> entry) {
		this.id = entry.get("id");
		this.values = entry;
		this.lastPollMs = System.currentTimeMillis();
	}

	// -------------------------------------------------- //
	// FIELDS
	// -------------------------------------------------- //
	
	public transient String id;
	
	private Map<String, String> values = new HashMap<>();
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
		
		List<Map<String, String>> newValues = null;
		
		
		MySQLPrepared prepared = null;
		try {
			prepared = FactionsMySQL.get().prepare("SELECT * FROM `fplayer` WHERE `id` = ?")
				.set(1, this);
			
			newValues = prepared.execute(ExecuteType.SELECT);
			
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			if (prepared != null) {
				prepared.close();
			}
		}
		
		if (newValues != null && newValues.size() > 0) {
			this.values = newValues.get(0);
		}
	}
	
	@Override
	public String getId() {
		return this.id;
	}

	@Override
	public String getName() {
		this.poll();
		if (this.values.get("name") == null || this.values.get("name") == "") {
			// Check bukkit
			Player potentialPlayer = Bukkit.getPlayer(this.id);
			if (potentialPlayer != null && potentialPlayer.getName() != null) {
				this.setName(potentialPlayer.getName());
			} else {
				return this.getId();				
			}
		}
		
		return this.values.get("name");
	}
	
	@Override
	public void setName(String name) {
		this.values.put("name", name);
		FactionsMySQL.get().prepare("UPDATE `fplayer` SET `name` = ? WHERE `id` = ?")
			.setCatched(1, name)
			.setCatched(2, this.id)
			.execute(ExecuteType.UPDATE);
	}
	
	@Override
	public long getLastLoginTime() {
		this.poll();
		if (this.values.get("lastlogintime") == null || this.values.get("lastlogintime") == "") {
			return 0;
		}
		return Long.valueOf(this.values.get("lastlogintime"));
	}

	@Override
	public void setLastLoginTime(long lastLoginTime) {
		this.values.put("lastlogintime", String.valueOf(lastLoginTime));
		FactionsMySQL.get().prepare("UPDATE `fplayer` SET `lastlogintime` = ? WHERE `id` = ?")
			.setCatched(1, lastLoginTime)
			.setCatched(2, this.id)
			.execute(ExecuteType.UPDATE);
	}

	@Override
	public Faction getFaction() {
		return FactionColl.get().getFactionById(this.getFactionId());
	}
	
	public void setFaction(String factionId) {
		if (factionId == null) {
			factionId = "0";
		}
		
		this.values.put("faction", factionId);
		FactionsMySQL.get().prepare("UPDATE `fplayer` SET `faction` = ? WHERE `id` = ?")
			.setCatched(1, factionId)
			.setCatched(2, this.id)
			.execute(ExecuteType.UPDATE);
	}

	@Override
	public void setFaction(Faction faction) {
		String factionId;
		if (faction == null) {
			factionId = "0";
		} else {
			factionId = faction.getId();
		}
		
		this.setFaction(factionId);
	}

	@Override
	public String getFactionId() {
		this.poll();
		if (this.values.get("faction") == null || this.values.get("faction") == "") {
			this.setFaction("0");
		}
		return this.values.get("faction");
	}

	@Override
	public Role getRole() {
		this.poll();
		if (this.values.get("role") == null || this.values.get("role") == "") {
			this.setRole(Role.NORMAL);
		}
		return Role.valueOf(this.values.get("role"));
	}

	@Override
	public void setRole(Role role) {
		if (role == null) {
			role = Role.NORMAL;
		}
		this.values.put("role", role.name());
		FactionsMySQL.get().prepare("UPDATE `fplayer` SET `role` = ? WHERE `id` = ?")
			.setCatched(1, role.name())
			.setCatched(2, this.id)
			.execute(ExecuteType.UPDATE);
	}

	@Override
	public String getTitle() {
		this.poll();
		if (this.values.get("title") == null) {
			this.setTitle("");
		}
		return this.values.get("title");
	}

	@Override
	public void setTitle(String title) {
		this.values.put("title", title);
		FactionsMySQL.get().prepare("UPDATE `fplayer` SET `title` = ? WHERE `id` = ?")
			.setCatched(1, title)
			.setCatched(2, this.id)
			.execute(ExecuteType.UPDATE);
	}

	@Override
	public boolean willAutoLeave() {
		this.poll();
		if (this.values.get("willautoleave") == null || this.values.get("willautoleave") == "") {
			this.setAutoLeave(false);
		}
		return Boolean.valueOf(this.values.get("willautoleave"));
	}

	@Override
	public void setAutoLeave(boolean autoLeave) {
		this.values.put("willautoleave", String.valueOf(autoLeave));
		FactionsMySQL.get().prepare("UPDATE `fplayer` SET `willautoleave` = ? WHERE `id` = ?")
			.setCatched(1, autoLeave)
			.setCatched(2, this.id)
			.execute(ExecuteType.UPDATE);
	}

	@Override
	public boolean isMonitoringJoins() {
		this.poll();
		if (LocationUtil.isFactionsDisableIn(this)) return false;
		if (this.values.get("monitorjoins") == null || this.values.get("monitorjoins") == "") {
			this.setMonitorJoins(false);
		}
		return Boolean.valueOf(this.values.get("monitorjoins"));
	}

	@Override
	public void setMonitorJoins(boolean monitor) {
		this.values.put("monitorjoins", String.valueOf(monitor));
		FactionsMySQL.get().prepare("UPDATE `fplayer` SET `monitorjoins` = ? WHERE `id` = ?")
			.setCatched(1, monitor)
			.setCatched(2, this.id)
			.execute(ExecuteType.UPDATE);
	}

	@Override
	public boolean isAdminBypassing() {
		this.poll();
		if (LocationUtil.isFactionsDisableIn(this)) return false;
		if (this.values.get("adminbypassing") == null || this.values.get("adminbypassing") == "") {
			this.setIsAdminBypassing(false);
		}
		return Boolean.valueOf(this.values.get("adminbypassing"));
	}

	@Override
	public void setIsAdminBypassing(boolean enabled) {
		this.values.put("adminbypassing", String.valueOf(enabled));
		FactionsMySQL.get().prepare("UPDATE `fplayer` SET `adminbypassing` = ? WHERE `id` = ?")
			.setCatched(1, enabled)
			.setCatched(2, this.id)
			.execute(ExecuteType.UPDATE);

	}

	@Override
	public ChatMode getChatMode() {
		this.poll();
		if (LocationUtil.isFactionsDisableIn(this)) return ChatMode.PUBLIC;
		if (this.values.get("chatmode") == null || this.values.get("chatmode") == "") {
			this.setChatMode(ChatMode.PUBLIC);
		}
		return ChatMode.valueOf(this.values.get("chatmode"));
	}

	@Override
	public void setChatMode(ChatMode chatMode) {
		this.values.put("chatmode", String.valueOf(chatMode));
		FactionsMySQL.get().prepare("UPDATE `fplayer` SET `chatmode` = ? WHERE `id` = ?")
			.setCatched(1, chatMode.name())
			.setCatched(2, this.id)
			.execute(ExecuteType.UPDATE);
	}

	@Override
	public boolean isIgnoreAllianceChat() {
		this.poll();
		if (LocationUtil.isFactionsDisableIn(this)) return true;
		return Boolean.valueOf(this.values.get("ignorealliancechat"));
	}

	@Override
	public void setIgnoreAllianceChat(boolean ignore) {
		this.values.put("ignorealliancechat", String.valueOf(ignore));
		FactionsMySQL.get().prepare("UPDATE `fplayer` SET `ignorealliancechat` = ? WHERE `id` = ?")
			.setCatched(1, ignore)
			.setCatched(2, this.id)
			.execute(ExecuteType.UPDATE);
	}

	@Override
	public boolean isSpyingChat() {
		this.poll();
		if (LocationUtil.isFactionsDisableIn(this)) return false;
		if (this.values.get("spyingchat") == null || this.values.get("spyingchat") == "") {
			this.setSpyingChat(false);
		}
		return Boolean.valueOf(this.values.get("spyingchat"));
	}

	@Override
	public void setSpyingChat(boolean chatSpying) {
		this.values.put("spyingchat", String.valueOf(chatSpying));
		FactionsMySQL.get().prepare("UPDATE `fplayer` SET `spyingchat` = ? WHERE `id` = ?")
			.setCatched(1, chatSpying)
			.setCatched(2, this.id)
			.execute(ExecuteType.UPDATE);
	}

	@Override
	public boolean showScoreboard() {
		this.poll();
		if (LocationUtil.isFactionsDisableIn(this)) return false;
		return Boolean.valueOf(this.values.get("showscoreboard"));
	}

	@Override
	public void setShowScoreboard(boolean show) {
		this.values.put("showscoreboard", String.valueOf(show));
		FactionsMySQL.get().prepare("UPDATE `fplayer` SET `showscoreboard` = ? WHERE `id` = ?")
			.setCatched(1, show)
			.setCatched(2, this.id)
			.execute(ExecuteType.UPDATE);
	}

	@Override
	public int getKills() {
		this.poll();
		if (this.values.get("kills") == null || this.values.get("kills") == "") {
			// Wait for lazy update on this
			this.values.put("kills", "0");
		}
		return this.isOnline() ? this.getPlayer().getStatistic(Statistic.PLAYER_KILLS) : Integer.valueOf(this.values.get("kills"));
	}

	@Override
	public int getDeaths() {
		this.poll();
		if (this.values.get("deaths") == null || this.values.get("deaths") == "") {
			// Wait for lazy update on this
			this.values.put("deaths", "0");
		}
		return this.isOnline() ? this.getPlayer().getStatistic(Statistic.DEATHS) : Integer.valueOf(this.values.get("deaths"));
	}

	@Override
	public void setKills(int amount) {
		this.values.put("kills", String.valueOf(amount));
		FactionsMySQL.get().prepare("UPDATE `fplayer` SET `kills` = ? WHERE `id` = ?")
			.setCatched(1, amount)
			.setCatched(2, this.id)
			.execute(ExecuteType.UPDATE);
	}
	
	@Override
	public void setDeaths(int amount) {
		this.values.put("deaths", String.valueOf(amount));
		FactionsMySQL.get().prepare("UPDATE `fplayer` SET `deaths` = ? WHERE `id` = ?")
			.setCatched(1, amount)
			.setCatched(2, this.id)
			.execute(ExecuteType.UPDATE);
	}
	
	@Override
	public double getPower() {
		this.poll();
		return Double.valueOf(this.values.get("power"));
	}

	@Override
	public void alterPower(double delta) {
		double newPower;
		try {
			newPower = Double.valueOf(this.values.get("power"));
		} catch (Exception e) {
			newPower = 0;
		}
		
		newPower += delta;
		if (newPower > this.getPowerMax()) {
			newPower = this.getPowerMax();
		} else if (newPower < this.getPowerMin()) {
			newPower = this.getPowerMin();
		}
		
		this.values.put("power", String.valueOf(newPower));
		FactionsMySQL.get().prepare("UPDATE `fplayer` SET `power` = ? WHERE `id` = ?")
			.setCatched(1, newPower)
			.setCatched(2, this.id)
			.execute(ExecuteType.UPDATE);
	}

	@Override
	public double getPowerBoost() {
		this.poll();
		try {
			return Double.valueOf(this.values.get("powerboost"));
		} catch (Exception e) {
			return 0d;
		}
	}

	@Override
	public void setPowerBoost(double powerBoost) {
		this.values.put("powerboost", String.valueOf(powerBoost));
		FactionsMySQL.get().prepare("UPDATE `fplayer` SET `powerboost` = ? WHERE `id` = ?")
			.setCatched(1, powerBoost)
			.setCatched(2, this.id)
			.execute(ExecuteType.UPDATE);

	}

	@Override
	public long getLastPowerUpdated() {
		this.poll();
		return Long.valueOf(this.values.get("lastpowerupdate"));
	}

	@Override
	public void setLastPowerUpdated(long time) {
		this.values.put("lastpowerupdate", String.valueOf(time));
		FactionsMySQL.get().prepare("UPDATE `fplayer` SET `lastpowerupdate` = ? WHERE `id` = ?")
			.setCatched(1, time)
			.setCatched(2, this.id)
			.execute(ExecuteType.UPDATE);
	}

	@Override
	public boolean territoryTitlesOff() {
		this.poll();
		if (LocationUtil.isFactionsDisableIn(this)) return true;
		return Boolean.valueOf(this.values.get("territorytitlesoff"));
	}

	@Override
	public void territoryTitlesOff(boolean off) {
		this.values.put("territorytitlesoff", String.valueOf(off));
		FactionsMySQL.get().prepare("UPDATE `fplayer` SET `territorytitlesoff` = ? WHERE `id` = ?")
			.setCatched(1, off)
			.setCatched(2, this.id)
			.execute(ExecuteType.UPDATE);
	}

	@Override
	public void setId(String id) {
		this.id = id;
		// Force a poll if we set the id
		this.poll(true);
	}
	
	@Override
	public void remove() {
		((MySQLFPlayerColl)FPlayerColl.getUnsafeInstance()).remove(this.id);
	}

}
