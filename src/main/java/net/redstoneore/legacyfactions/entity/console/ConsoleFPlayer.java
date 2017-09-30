package net.redstoneore.legacyfactions.entity.console;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import net.redstoneore.legacyfactions.Role;
import net.redstoneore.legacyfactions.config.Config;
import net.redstoneore.legacyfactions.config.meta.Meta;
import net.redstoneore.legacyfactions.entity.Faction;
import net.redstoneore.legacyfactions.entity.FactionColl;
import net.redstoneore.legacyfactions.entity.persist.shared.SharedFPlayer;
import net.redstoneore.legacyfactions.expansion.chat.ChatMode;

public class ConsoleFPlayer extends SharedFPlayer {

	// -------------------------------------------------- //
	// INSTANCE
	// -------------------------------------------------- //
	
	private transient static ConsoleFPlayer instance = null;
	public static ConsoleFPlayer get() {
		if (instance == null) {
			instance = new ConsoleFPlayer();
		}
		return instance;
	}
	
	// -------------------------------------------------- //
	// FIELDS
	// -------------------------------------------------- //
	
	private transient long loginTime = System.currentTimeMillis();
	
	// -------------------------------------------------- //
	// METHOD
	// -------------------------------------------------- //
	
	@Override
	public String getId() {
		return Meta.get().consoleId;
	}

	@Override
	public String getName() {
		return "@console";
	}

	@Override
	public long getLastLoginTime() {
		return loginTime;
	}

	@Override
	public void setLastLoginTime(long lastLoginTime) {
		
	}

	@Override
	public Faction getFaction() {
		return FactionColl.get().getWilderness();
	}

	@Override
	public void setFaction(Faction faction) {
		
	}

	@Override
	public String getFactionId() {
		return FactionColl.get().getWilderness().getId();
	}

	@Override
	public Role getRole() {
		return Role.NORMAL;
	}

	@Override
	public void setRole(Role role) {
		
	}

	@Override
	public String getTitle() {
		return "";
	}

	@Override
	public void setTitle(String title) {
		
	}

	@Override
	public boolean willAutoLeave() {
		return false;
	}

	@Override
	public void setAutoLeave(boolean autoLeave) {
		
	}

	@Override
	public boolean isMonitoringJoins() {
		return false;
	}

	@Override
	public void setMonitorJoins(boolean monitor) {
		
	}

	@Override
	public boolean isAdminBypassing() {
		return false;
	}

	@Override
	public void setIsAdminBypassing(boolean enabled) {
		
	}

	@Override
	public ChatMode getChatMode() {
		return ChatMode.PUBLIC;
	}

	@Override
	public void setChatMode(ChatMode chatMode) {
		
	}

	@Override
	public boolean isIgnoreAllianceChat() {
		return false;
	}

	@Override
	public void setIgnoreAllianceChat(boolean ignore) {
		
	}

	@Override
	public boolean isSpyingChat() {
		return true;
	}

	@Override
	public void setSpyingChat(boolean chatSpying) {
		
	}

	@Override
	public boolean showScoreboard() {
		return false;
	}

	@Override
	public void setShowScoreboard(boolean show) {
		
	}

	@Override
	public int getKills() {
		return 0;
	}

	@Override
	public int getDeaths() {
		return 0;
	}

	@Override
	public double getPower() {
		return Config.powerPlayerMax;
	}

	@Override
	public void alterPower(double delta) {
		
	}

	@Override
	public double getPowerBoost() {
		return 0;
	}

	@Override
	public void setPowerBoost(double powerBoost) {
		
	}

	@Override
	public long getLastPowerUpdated() {
		return this.loginTime;
	}

	@Override
	public void setLastPowerUpdated(long time) {
		
	}

	@Override
	public boolean territoryTitlesOff() {
		return true;
	}

	@Override
	public void territoryTitlesOff(boolean off) {
		
	}

	@Override
	public void setId(String id) {
		
	}

	@Override
	public void setName(String name) {
		
	}

	@Override
	public void setKills(int amount) {
		
	}

	@Override
	public void setDeaths(int amount) {
		
	}

	@Override
	public void remove() {
		
	}

	@Override
	public boolean isOnlineAndVisibleTo(Player player) {
		return false;
	}

	@Override
	public boolean isOffline() {
		return false;
	}
	
	@Override
	public boolean isOnline() {
		return true;
	}

	// -------------------------------------------------- //
	// MESSAGE SENDING HELPERS
	// -------------------------------------------------- //

	@Override
	public  void sendMessage(String message) {
		if (message.contains("{null}")) return; // user wants this message to not send
		
		if (message.contains("/n/")) {
			for (String line : message.split("/n/")) {
				this.sendMessage(line);
			}
			return;
		}
		
		Bukkit.getConsoleSender().sendMessage(message);
	}
	
}
