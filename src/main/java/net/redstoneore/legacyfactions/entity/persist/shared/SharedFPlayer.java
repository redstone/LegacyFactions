package net.redstoneore.legacyfactions.entity.persist.shared;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Statistic;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import net.redstoneore.legacyfactions.EconomyParticipator;
import net.redstoneore.legacyfactions.Factions;
import net.redstoneore.legacyfactions.Permission;
import net.redstoneore.legacyfactions.Relation;
import net.redstoneore.legacyfactions.RelationParticipator;
import net.redstoneore.legacyfactions.Role;
import net.redstoneore.legacyfactions.Volatile;
import net.redstoneore.legacyfactions.config.Config;
import net.redstoneore.legacyfactions.entity.Board;
import net.redstoneore.legacyfactions.entity.FPlayer;
import net.redstoneore.legacyfactions.entity.FPlayerColl;
import net.redstoneore.legacyfactions.entity.Faction;
import net.redstoneore.legacyfactions.entity.FactionColl;
import net.redstoneore.legacyfactions.entity.VaultAccount;
import net.redstoneore.legacyfactions.event.EventFactionsChange;
import net.redstoneore.legacyfactions.event.EventFactionsDisband;
import net.redstoneore.legacyfactions.event.EventFactionsLandChange;
import net.redstoneore.legacyfactions.event.EventFactionsChange.ChangeReason;
import net.redstoneore.legacyfactions.expansion.chat.ChatMode;
import net.redstoneore.legacyfactions.flag.Flags;
import net.redstoneore.legacyfactions.integration.essentials.EssentialsEngine;
import net.redstoneore.legacyfactions.integration.vault.VaultEngine;
import net.redstoneore.legacyfactions.integration.worldguard.WorldGuardEngine;
import net.redstoneore.legacyfactions.integration.worldguard.WorldGuardIntegration;
import net.redstoneore.legacyfactions.lang.Lang;
import net.redstoneore.legacyfactions.locality.Locality;
import net.redstoneore.legacyfactions.locality.LocalityLazy;
import net.redstoneore.legacyfactions.mixin.PlayerMixin;
import net.redstoneore.legacyfactions.placeholder.FactionsPlaceholders;
import net.redstoneore.legacyfactions.scoreboards.FScoreboards;
import net.redstoneore.legacyfactions.scoreboards.sidebar.FInfoSidebar;
import net.redstoneore.legacyfactions.util.RelationUtil;
import net.redstoneore.legacyfactions.util.TextUtil;
import net.redstoneore.legacyfactions.util.TitleUtil;
import net.redstoneore.legacyfactions.util.WarmUpUtil;

public abstract class SharedFPlayer implements FPlayer {
	
	// -------------------------------------------------- //
	// STATIC FIELDS
	// -------------------------------------------------- //

	private static transient Locality DEFAULT_LASTSTOODAT = Locality.of(Bukkit.getWorlds().get(0).getSpawnLocation());

	// -------------------------------------------------- //
	// FIELDS
	// -------------------------------------------------- //

	private transient Locality lastStoodAtLocation = DEFAULT_LASTSTOODAT;
	private transient boolean mapAutoUpdating = false;
	private transient Faction autoClaimFor = null;
	private transient boolean autoSafeZoneEnabled = false;
	private transient boolean autoWarZoneEnabled = false;
	private transient boolean loginPvpDisabled = Config.noPVPDamageToOthersForXSecondsAfterLogin > 0;
	private transient long lastFrostwalkerMessage = 0;
	
	private transient WarmUpUtil.Warmup warmup;
	private transient int warmupTask;
	
	// -------------------------------------------------- //
	// METHODS
	// -------------------------------------------------- //
	
	@Override
	public String getAccountId() {
		return this.getId();
	}
	
	@Override
	public ItemStack getItemInMainHand() {
		return PlayerMixin.getItemInMainHand(this.getPlayer());
	}
	
	@Override
	public ItemStack getItemInOffHand() {
		return PlayerMixin.getItemInOffHand(this.getPlayer());
	}
	
	@Override
	public void teleport(Locality locality) {
		this.getPlayer().teleport(locality.getLocation());
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
		if (this.getLastLoginTime() + (Config.noPVPDamageToOthersForXSecondsAfterLogin * 1000) < System.currentTimeMillis()) {
			this.loginPvpDisabled = false;
			return false;
		}
		return true;
	}
	
	public void setLoginPVPDisable(boolean disabled) {
		this.loginPvpDisabled = disabled;
	}
	
	@Override
	public long getLastFrostwalkerMessage() {
		return this.lastFrostwalkerMessage;
	}

	@Override
	public void setLastFrostwalkerMessage() {
		this.lastFrostwalkerMessage = System.currentTimeMillis();
	}
	
	@Deprecated
	@Override
	public net.redstoneore.legacyfactions.FLocation getLastStoodAt() {
		return net.redstoneore.legacyfactions.FLocation.valueOf(this.lastStoodAtLocation.getLocation());
	}
	
	@Override
	public void setLastLocation(Locality locality) {
		this.lastStoodAtLocation = locality;
	}
	
	@Override
	public Locality getLastLocation() {
		return this.lastStoodAtLocation;
	}

	@Deprecated
	@Override
	public void setLastStoodAt(net.redstoneore.legacyfactions.FLocation flocation) {
		this.lastStoodAtLocation = Locality.of(flocation.getChunk());
	}
	
	@Override
	public boolean hasFaction() {
		return !this.getFaction().getId().equals("0");
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
	public boolean canAdminister(FPlayer who) {
		if (!who.getFaction().equals(this.getFaction())) {
			who.sendMessage(TextUtil.get().parse(Lang.COMMAND_ERRORS_NOTSAME.toString().replaceAll("<name>", this.describeTo(who, true))));
			return false;
		}

		if (who.getRole().isMoreThan(this.getRole()) || who.getRole().equals(Role.ADMIN)) {
			return true;
		}

		if (this.getRole().equals(Role.ADMIN)) {
			who.sendMessage(TextUtil.get().parse(Lang.COMMAND_ERRORS_ONLYFACTIONADMIN.toString()));
		} else if (who.getRole().equals(Role.MODERATOR)) {
			if (who == this) return true;
			
			who.sendMessage(TextUtil.get().parse(Lang.COMMAND_ERRORS_MODERATORSCANT.toString()));
		} else if (who.getRole().equals(Role.COLEADER)) {
			if (who == this) return true;

			who.sendMessage(TextUtil.get().parse(Lang.COMMAND_ERRORS_COLEADERSCANT.toString()));
		} else {
			who.sendMessage(TextUtil.get().parse(Lang.COMMAND_ERRORS_NOTMODERATOR.toString()));
		}
		
		return false;
	}

	@Override
	public boolean hasPermission(String permission) {
		return this.getPlayer().hasPermission(permission);
	}
	
	@Override
	public boolean isVanished(FPlayer viewer) {
		return EssentialsEngine.isVanished(this.getPlayer()) || viewer.getPlayer().canSee(this.getPlayer());
	}
	

	@Override
	public void resetFactionData() {
		// clean up any territory ownership in old faction, if there is one
		if (this.getFactionId() != null && FactionColl.get().isValidFactionId(this.getFactionId())) {
			Faction currentFaction = this.getFaction();
			currentFaction.memberRemove(this);
			if (currentFaction.isNormal()) {
				currentFaction.ownership().clearAll(this);
			}
		}

		this.setFaction(FactionColl.get().getWilderness());
		this.setChatMode(ChatMode.PUBLIC);
		this.setRole(Role.NORMAL);
		this.setTitle("");
		this.setAutoClaimFor(null);
	}
	
	@Override
	public String getTag() {
		return this.hasFaction() ? this.getFaction().getTag() : "";
	}

	// Base concatenations:

	@Override
	public String getNameAndSomething(String something) {
		String ret = this.getRole().getPrefix();
		if (something.length() > 0) {
			ret += something + " ";
		}
		ret += this.getName();
		return ret;
	}
	
	@Override
	public String getNameAndTitle() {
		return this.getNameAndSomething(this.getTitle());
	}

	@Override
	public String getNameAndTag() {
		return this.getNameAndSomething(this.getTag());
	}

	// Colored concatenations:
	// These are used in information messages

	@Override
	public String getNameAndTitle(Faction faction) {
		return this.getColorTo(faction) + this.getNameAndTitle();
	}

	// Chat Tag:
	// These are injected into the format of global chat messages.

	@Override
	public String getChatTag() {
		String format = null;
		
		if (this.hasFaction()) {
			// Clone from the configuration 
			format = Config.expansionsFactionsChat.chatTagFormatDefault.toString();
		} else {
			// Clone from the configuration
			format = Config.expansionsFactionsChat.chatTagFormatFactionless.toString();
		}
		
		// Format with Placeholders
		format = FactionsPlaceholders.get().parse(this, format);
		
		return format;
	}

	// Colored Chat Tag
	@Override
	public String getChatTag(Faction faction) {
		return this.hasFaction() ? this.getRelationTo(faction).getColor() + getChatTag() : "";
	}
	
	// -------------------------------
	// Relation and relation colors
	// -------------------------------
	
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

	public Relation getRelationToLocation() {
		return Board.get().getFactionAt(this.getLastLocation()).getRelationTo(this);
	}

	@Override
	public ChatColor getColorTo(RelationParticipator rp) {
		return RelationUtil.getColorOfThatToMe(this, rp);
	}

	// -------------------------------------------------- //
	// HEALTH
	// -------------------------------------------------- //
	
	@Override
	public void heal(int amnt) {
		Player player = this.getPlayer();
		if (player == null) {
			return;
		}
		player.setHealth(player.getHealth() + amnt);
	}
	
	// -------------------------------------------------- //
	// POWER
	// -------------------------------------------------- //
	
	@Override
	public int getPowerRounded() {
		return (int) Math.round(this.getPower());
	}

	@Override
	public int getPowerMaxRounded() {
		return (int) Math.round(this.getPowerMax());
	}

	@Override
	public int getPowerMinRounded() {
		return (int) Math.round(this.getPowerMin());
	}
	
	@Override
	public double getPowerMax() {
		return Config.powerPlayerMax + this.getPowerBoost();
	}

	@Override
	public double getPowerMin() {
		return Config.powerPlayerMin + this.getPowerBoost();
	}
	

	@Override
	public void updatePower() {
		if (this.isOffline()) {
			losePowerFromBeingOffline();
			if (!Config.powerRegenOffline) {
				return;
			}
		} else if (hasFaction() && getFaction().isPowerFrozen()) {
			return; // Don't let power regen if faction power is frozen.
		}
		long now = System.currentTimeMillis();
		long millisPassed = now - this.getLastPowerUpdated();
		this.setLastPowerUpdated(now);
		
		Player thisPlayer = this.getPlayer();
		if (thisPlayer != null && thisPlayer.isDead()) {
			return;  // don't let dead players regain power until they respawn
		}

		int millisPerMinute = 60 * 1000;
		this.alterPower(millisPassed * Config.powerPerMinute / millisPerMinute);
	}

	@Override
	public void losePowerFromBeingOffline() {
		if (Config.powerOfflineLossPerDay > 0.0 && this.getPower() > Config.powerOfflineLossLimit) {
			long now = System.currentTimeMillis();
			long millisPassed = now - this.getLastPowerUpdated();
			
			this.setLastPowerUpdated(now);
			
			double loss = millisPassed * Config.powerOfflineLossPerDay / (24 * 60 * 60 * 1000);
			if (this.getPower() - loss < Config.powerOfflineLossLimit) {
				loss = this.getPower();
			}
			this.alterPower(-loss);
		}
	}
	
	// -------------------------------------------------- //
	// EVENTS
	// -------------------------------------------------- //
	
	@Override
	public void onDeath() {
		this.onDeath(Config.powerPerDeath);
	}
	
	@Override
	public void onDeath(double powerLoss) {
		this.updatePower();
		this.alterPower(-powerLoss);
		if (hasFaction()) {
			getFaction().setLastDeath(System.currentTimeMillis());
		}
	}
	
	// -------------------------------------------------- //
	// TERRITORY
	// -------------------------------------------------- //
	
	@Override
	public boolean isInOwnTerritory() {
		return Board.get().getFactionAt(this.getLastLocation()) == this.getFaction();
	}

	@Override
	public boolean isInOthersTerritory() {
		Faction factionHere = Board.get().getFactionAt(this.getLastLocation());
		return factionHere != null && factionHere.isNormal() && factionHere != this.getFaction();
	}

	@Override
	public boolean isInAllyTerritory() {
		return Board.get().getFactionAt(this.getLastLocation()).getRelationTo(this).isAlly();
	}

	@Override
	public boolean isInNeutralTerritory() {
		return Board.get().getFactionAt(this.getLastLocation()).getRelationTo(this).isNeutral();
	}

	@Override
	public boolean isInEnemyTerritory() {
		return Board.get().getFactionAt(this.getLastLocation()).getRelationTo(this).isEnemy();
	}

	@Override
	public void sendFactionHereMessage(Faction factionFrom) {
		Faction factionHere = Board.get().getFactionAt(this.getLastLocation());
		boolean showInChat = true;
		
		// Territory change scoreboard message
		if (this.showInfoBoard(factionHere)) {
			FScoreboards.get(this).setTemporarySidebar(new FInfoSidebar(factionHere));
			showInChat = Config.scoreboardInChat;
		}
		
		// Territory change chat message
		if (showInChat && Config.territoryChangeText) {
			this.sendMessage(TextUtil.get().parse(Lang.FACTION_LEAVE.format(factionFrom.getTag(this), factionHere.getTag(this))));
		}
		
		// Territory change title message 
		if (!this.territoryTitlesOff() && Config.territoryTitlesShow) {
			String titleHeader = FactionsPlaceholders.get().parse(this.getPlayer(), Config.territoryTitlesHeader.trim());
			String titleFooter = FactionsPlaceholders.get().parse(this.getPlayer(), Config.territoryTitlesFooter.trim());
			
			// Hide footer if needed
			if (this.getLastLocation().getFactionHere().isWarZone() && Config.hideFooterForWarzone) {
				titleFooter = "";
			}
			
			if (this.getLastLocation().getFactionHere().isSafeZone() && Config.hideFooterForSafezone) {
				titleFooter = "";
			}
			
			if (this.getLastLocation().getFactionHere().isWilderness() && Config.hideFooterForWilderness) {
				titleFooter = "";
			}
			
			TitleUtil.sendTitle(this.getPlayer(), Config.territoryTitlesTimeFadeInTicks, Config.territoryTitlesTimeStayTicks, Config.territoryTitlesTimeFadeOutTicks, titleHeader, titleFooter);
		}
	}

	/**
	 * Check if the scoreboard should be shown. 
	 * @param toShow Faction to be shown.
	 * @return true if should show, otherwise false.
	 */
	public boolean showInfoBoard(Faction toShow) {
		return this.showScoreboard() && !toShow.isWarZone() && !toShow.isWilderness() && !toShow.isSafeZone() && !Config.scoreboardInfo.isEmpty() && Config.scoreboardInfoEnabled && FScoreboards.get(this) != null;
	}

	// -------------------------------------------------- //
	// ACTIONS
	// -------------------------------------------------- //

	@Override
	public void leave(boolean makePay) {
		this.leave(makePay, false);
	}
	
	@Override
	public void leave(boolean makePay, boolean silent) {
		Faction myFaction = this.getFaction();
		makePay = makePay && VaultEngine.getUtils().shouldBeUsed() && !this.isAdminBypassing();

		if (myFaction == null) {
			resetFactionData();
			return;
		}

		boolean perm = myFaction.getFlag(Flags.PERMANENT);
		
		if (!perm && this.getRole() == Role.ADMIN && myFaction.getMembers().size() > 1) {
			this.sendMessage(Lang.LEAVE_PASSADMIN);
			return;
		}

		if (!Config.canLeaveWithNegativePower && this.getPower() < 0) {
			this.sendMessage(Lang.LEAVE_NEGATIVEPOWER);
			return;
		}

		// if economy is enabled and they're not on the bypass list, make sure they can pay
		if (makePay && !VaultEngine.getUtils().hasAtLeast(this, Config.econCostLeave, Lang.LEAVE_TOLEAVE.toString())) {
			return;
		}
		
		EventFactionsChange event = new EventFactionsChange(this, myFaction, FactionColl.get().getWilderness(), true, ChangeReason.LEAVE);
		Bukkit.getServer().getPluginManager().callEvent(event);
		if (event.isCancelled()) {
			return;
		}

		// then make 'em pay (if applicable)
		if (makePay && !VaultEngine.getUtils().modifyMoney(this, -Config.econCostLeave, Lang.LEAVE_TOLEAVE.toString(), Lang.LEAVE_FORLEAVE.toString())) {
			return;
		}

		// Am I the last one in the faction?
		if (myFaction.getMembers().size() == 1) {
			// Transfer all money
			if (VaultEngine.getUtils().shouldBeUsed()) {
				VaultAccount.get(myFaction).transfer(VaultAccount.get(this), VaultAccount.get(myFaction).getBalance(), VaultAccount.get(this));
			}
		}

		if (myFaction.isNormal()) {
			for (FPlayer fplayer : myFaction.getWhereOnline(true)) {
				fplayer.sendMessage(Lang.LEAVE_LEFT, this.describeTo(fplayer, true), myFaction.describeTo(fplayer));
			}

			if (Config.logFactionLeave) {
				Factions.get().log(Lang.LEAVE_LEFT.format(this.getName(), myFaction.getTag()));
			}
		}

		myFaction.announcements().remove(this);
		this.resetFactionData();

		if (myFaction.isNormal() && !perm && myFaction.getMembers().isEmpty()) {
			EventFactionsDisband disbandEvent = new EventFactionsDisband(getPlayer(), myFaction.getId(), false,
					EventFactionsDisband.DisbandReason.LEAVE);
			Bukkit.getPluginManager().callEvent(disbandEvent);

			// Remove this faction
			for (FPlayer fplayer : FPlayerColl.all()) {
				fplayer.sendMessage(Lang.LEAVE_DISBANDED, myFaction.describeTo(fplayer, true));
			}

			FactionColl.get().removeFaction(myFaction.getId());
			if (Config.logFactionDisband) {
				Factions.get().log(Lang.LEAVE_DISBANDEDLOG.format(myFaction.getTag(), myFaction.getId(), this.getName()));
			}
		}
	}

	@Override
	public boolean canClaimForFaction(Faction forFaction) {
		return !forFaction.isWilderness() && (this.isAdminBypassing() || (forFaction == this.getFaction() && this.getRole().isAtLeast(Role.MODERATOR)) || (forFaction.isSafeZone() && Permission.MANAGE_SAFE_ZONE.has(getPlayer())) || (forFaction.isWarZone() && Permission.MANAGE_WAR_ZONE.has(getPlayer())));
	}
	
	@Override
	public boolean canClaimForFactionAtLocation(Faction forFaction, Location location, boolean notifyFailure) {
		return this.canClaimForFactionAtLocation(forFaction, LocalityLazy.of(location.getWorld().getName(), location.getChunk().getX(), location.getChunk().getZ()), notifyFailure);
	}
	
	@Override
	public boolean canClaimForFactionAtLocation(Faction forFaction, Locality locality, boolean notifyFailure) {
		Faction myFaction = this.getFaction();
		Faction currentFaction = Board.get().getFactionAt(locality);
		int ownedLand = forFaction.getLandRounded();
		
		// Admin Bypass needs no further checks
		if (this.isAdminBypassing()) return true;
		
		// Can claim in safe zone?
		if (forFaction.isSafeZone() && Permission.MANAGE_SAFE_ZONE.has(getPlayer())) return true;
		
		// Claim for war zone
		if (forFaction.isWarZone() && Permission.MANAGE_WAR_ZONE.has(getPlayer())) return true;

		// Checks for WorldGuard regions in the chunk attempting to be claimed
		if (WorldGuardIntegration.get().isEnabled() && Config.worldGuardChecking && WorldGuardEngine.checkForRegionsInChunk(locality.getChunk())) {
			this.sendMessage(notifyFailure, Lang.CLAIM_PROTECTED);
			return false;
		}
		
		// Check if this is a no-claim world
		if (Config.worldsNoClaiming.contains(locality.getWorld().getName())) {
			this.sendMessage(notifyFailure, Lang.CLAIM_DISABLED);
			return false;
		} 
		
		// Can only claim for own faction
		if (myFaction != forFaction) {
			this.sendMessage(notifyFailure, Lang.CLAIM_CANTCLAIM, forFaction.describeTo(this));
			return false;
		}
		
		// Do we already own this?
		if (forFaction == currentFaction) {
			this.sendMessage(notifyFailure, Lang.CLAIM_ALREADYOWN, forFaction.describeTo(this, true));
			return false;
		}
		
		// Are they at lease a moderator?
		if (!this.getRole().isAtLeast(Role.MODERATOR)) {
			this.sendMessage(notifyFailure, Lang.CLAIM_MUSTBE, Role.MODERATOR.getTranslation());
			return false;
		}
		
		// Check for minimum members
		if (forFaction.getMembers().size() < Config.claimsRequireMinFactionMembers) {
			this.sendMessage(notifyFailure, Lang.CLAIM_MEMBERS, Config.claimsRequireMinFactionMembers);
			return false;
		}
		
		// Check for safezone
		if (currentFaction.isSafeZone()) {
			this.sendMessage(notifyFailure, Lang.CLAIM_SAFEZONE);
			return false;
		}
		
		// Check for warzone
		if (currentFaction.isWarZone()) {
			this.sendMessage(notifyFailure, Lang.CLAIM_WARZONE);
			return false;
		} 
		
		// Check raidable can overclaim
		if (Config.raidableAllowOverclaim && ownedLand >= forFaction.getPowerRounded()) {
			this.sendMessage(notifyFailure, Lang.CLAIM_POWER);
			return false;
		}
		
		// Check for claimedLandsMax
		if (Config.claimedLandsMax > 0 && ownedLand >= Config.claimedLandsMax && forFaction.isNormal()) {
			this.sendMessage(notifyFailure, Lang.CLAIM_LIMIT);
			return false;
		}
		
		// Check for ally claim 
		if (currentFaction.getRelationTo(forFaction) == Relation.ALLY) {
			this.sendMessage(notifyFailure, Lang.CLAIM_ALLY.toString());
			return false;
		} 

		// Check if must be connected
		if (Config.claimsMustBeConnected && !this.isAdminBypassing() && myFaction.getLandRoundedInWorld(locality.getWorld()) > 0 && !Board.get().isConnectedLocation(locality, myFaction) && (!Config.claimsCanBeUnconnectedIfOwnedByOtherFaction || !currentFaction.isNormal())) {
			if (Config.claimsCanBeUnconnectedIfOwnedByOtherFaction) {
				this.sendMessage(notifyFailure, Lang.CLAIM_CONTIGIOUS);
			} else {
				this.sendMessage(notifyFailure, Lang.CLAIM_FACTIONCONTIGUOUS);
			}
			return false;
		}
		
		// Check for buffer
		if (Config.bufferFactions > 0 && Board.get().hasFactionWithin(locality, myFaction, Config.bufferFactions)) {
			this.sendMessage(notifyFailure, Lang.CLAIM_TOOCLOSETOOTHERFACTION.format(Config.bufferFactions));
			return false;
		}
		
		// Border check
		if (Config.claimsCanBeOutsideBorder == false && locality.isOutsideWorldBorder(Config.bufferWorldBorder)) {
			if (Config.bufferWorldBorder > 0) {
				this.sendMessage(notifyFailure, Lang.CLAIM_OUTSIDEBORDERBUFFER.format(Config.bufferWorldBorder));
			} else {
				this.sendMessage(notifyFailure, Lang.CLAIM_OUTSIDEWORLDBORDER);
			}
			return false;
		}
		
		// If the faction we're trying to claim is a normal faction ...
		if (currentFaction.isNormal()) {
			
			// .. and i'm peaceful ...
			if (myFaction.getFlag(Flags.PEACEFUL)) {
				// .. don't allow - i'm peaceful.
				this.sendMessage(notifyFailure, Lang.CLAIM_PEACEFUL, currentFaction.getTag(this));
				return false;
			}
			
			// .. and they're peaceful ...
			if (currentFaction.getFlag(Flags.PEACEFUL)) {
				// .. don't allow - they're peaceful.
				this.sendMessage(notifyFailure, Lang.CLAIM_PEACEFULTARGET, currentFaction.getTag(this));
				return false;
			}
			
			// .. and they're strong enough to hold it
			if (!currentFaction.hasLandInflation()) {
				// ... don't allow - they're too strong
				this.sendMessage(notifyFailure, Lang.CLAIM_THISISSPARTA, currentFaction.getTag(this));
				return false;
			}
			
			// .. raidableAllowOverclaim is false, and the current faction is not strong enough 
			if (!Config.raidableAllowOverclaim && currentFaction.hasLandInflation()) {
				// .. don't allow it, overclaim is disabled
				this.sendMessage(notifyFailure, Lang.CLAIM_OVERCLAIM_DISABLED);
				return false;
			}
			
			if (!Board.get().isBorderLocation(locality)) {
				this.sendMessage(notifyFailure, Lang.CLAIM_BORDER);
				return false;
			}
		}
		
		// can claim!
		return true;
	}
	
	@Override
	public boolean attemptClaim(Faction forFaction, Locality locality, boolean notifyFailure, EventFactionsLandChange eventLandChange) {
		return this.attemptClaim(forFaction, locality, notifyFailure, notifyFailure);
	}
	
	@Override
	public boolean attemptClaim(Faction forFaction, Location location, boolean notifyFailure, EventFactionsLandChange eventLandChange) {
		return this.attemptClaim(forFaction, location, notifyFailure, notifyFailure);
	}
		
	@Override
	public boolean attemptClaim(Faction forFaction, Location location, boolean notifyFailure, boolean notifySuccess) {		
		return this.attemptClaim(forFaction, Locality.of(location), notifyFailure, notifySuccess);
	}
	
	@Override
	public boolean attemptClaim(Faction forFaction, Locality locality, boolean notifyFailure, boolean notifySuccess) {
		Faction currentFaction = Board.get().getFactionAt(locality);
		
		int ownedLand = forFaction.getLandRounded();

		if (!this.canClaimForFactionAtLocation(forFaction, locality, notifyFailure)) {
			return false;
		}

		// if economy is enabled and they're not on the bypass list, make sure they can pay
		boolean mustPay = VaultEngine.getUtils().shouldBeUsed() && !this.isAdminBypassing() && !forFaction.isSafeZone() && !forFaction.isWarZone();
		double cost = 0.0;
		EconomyParticipator payee = null;
		if (mustPay) {
			cost = VaultEngine.getUtils().calculateClaimCost(ownedLand, currentFaction.isNormal(), locality);

			if (Config.econClaimUnconnectedFee != 0.0 && forFaction.getLandRoundedInWorld(locality.getWorld()) > 0 && !Board.get().isConnectedLocation(locality, forFaction)) {
				cost += Config.econClaimUnconnectedFee;
			}

			if (Config.bankEnabled && Config.bankFactionPaysLandCosts && this.hasFaction()) {
				payee = this.getFaction();
			} else {
				payee = this;
			}
			
			if (!payee.getVaultAccount().has(cost)) {
				payee.sendMessage(Lang.CLAIM_TOCLAIM.getBuilder().parse().toString());
				return false;
			}
			
			// then make 'em pay (if applicable)
			if (!VaultEngine.getUtils().modifyMoney(payee, -cost, Lang.CLAIM_TOCLAIM.toString(), Lang.CLAIM_FORCLAIM.toString())) {
				return false;
			}
		}

		// Was an over claim
		if (currentFaction.isNormal() && currentFaction.hasLandInflation()) {
			// Give them money for over claiming.
			VaultEngine.getUtils().modifyMoney(payee, Config.econOverclaimRewardMultiplier, Lang.CLAIM_TOOVERCLAIM.toString(), Lang.CLAIM_FOROVERCLAIM.toString());
		}

		// announce success
		if (notifySuccess) {
			Set<FPlayer> informTheseFPlayers = new HashSet<FPlayer>();
			informTheseFPlayers.add(this);
			informTheseFPlayers.addAll(forFaction.getWhereOnline(true));
			
			informTheseFPlayers.forEach(fplayer -> fplayer.sendMessage(Lang.CLAIM_CLAIMED, this.describeTo(fplayer, true), forFaction.describeTo(fplayer), currentFaction.describeTo(fplayer)));
		}
		
		Board.get().setFactionAt(forFaction, locality);

		if (Config.logLandClaims) {
			Factions.get().log(Lang.CLAIM_CLAIMEDLOG.toString(), this.getName(), locality.getCoordString(), forFaction.getTag());
		}

		return true;
	}

	public boolean shouldBeSaved() {
		if (!this.hasFaction() && (this.getPowerRounded() == this.getPowerMaxRounded() || this.getPowerRounded() == (int) Math.round(Config.powerPlayerStarting))) {
			return false;
		}
		return true;
	}
	
	@Override
	public void sendMessage(boolean onlyIfTrue, String str, Object... args) {
		if (onlyIfTrue) this.sendMessage(str, args);
	}

	@Override
	public void sendMessage(String str, Object... args) {
		this.sendMessage(TextUtil.get().parse(str, args));
	}

	public void sendMessage(boolean onlyIfTrue, Lang translation, Object... args) {
		if(onlyIfTrue) this.sendMessage(translation, args);
	}
	
	@Override
	public void sendMessage(Lang translation, Object... args) {
		this.sendMessage(translation.toString(), args);
	}
	
	@Override
	public Player getPlayer() {
		return Bukkit.getPlayer(UUID.fromString(this.getId()));
	}

	@Override
	public boolean isOnline() {
		return this.getPlayer() != null;
	}

	// make sure target player should be able to detect that this player is online
	@Override
	public boolean isOnlineAndVisibleTo(Player player) {
		Player target = this.getPlayer();
		return target != null && player.canSee(target);
	}

	@Override
	public boolean isOffline() {
		return !isOnline();
	}

	// -------------------------------------------------- //
	// MESSAGE SENDING HELPERS
	// -------------------------------------------------- //

	@Override
	public void sendMessage(String message) {
		if (message.contains("{null}")) return; // user wants this message to not send
		
		if (message.contains("/n/")) {
			for (String line : message.split("/n/")) {
				this.sendMessage(line);
			}
			return;
		}
		
		Player player = this.getPlayer();
		if (player == null) return; 
		
		player.sendMessage(message);
	}

	@Override
	public void sendMessage(List<String> messages) {
		messages.forEach(message -> this.sendMessage(message));
	}

	@Override
	public String getNameAndTitle(FPlayer fplayer) {
		return this.getColorTo(fplayer) + this.getNameAndTitle();
	}
	
	@Override
	public String getChatTag(FPlayer fplayer) {
		return this.hasFaction() ? this.getRelationTo(fplayer).getColor() + getChatTag() : "";
	}

	// -------------------------------------------------- //
	// UTILS
	// -------------------------------------------------- //
	
	@Override
	public boolean payForCommand(double cost, String toDoThis, String forDoingThis) {
		if (!VaultEngine.getUtils().shouldBeUsed() || cost == 0.0 || this.isAdminBypassing()) {
			return true;
		}

		if (Config.bankEnabled && Config.bankFactionPaysCosts && this.hasFaction()) {
			return VaultEngine.getUtils().modifyMoney(this.getFaction(), -cost, toDoThis, forDoingThis);
		} else {
			return VaultEngine.getUtils().modifyMoney(this, -cost, toDoThis, forDoingThis);
		}
	}
	
	// like above, but just make sure they can pay; returns true unless person can't afford the cost
	@Override
	public boolean canAffordCommand(double cost, String toDoThis) {
		if (!VaultEngine.getUtils().shouldBeUsed() || cost == 0.0 || this.isAdminBypassing()) {
			return true;
		}

		if (Config.bankEnabled && Config.bankFactionPaysCosts && this.hasFaction()) {
			return VaultEngine.getUtils().hasAtLeast(this.getFaction(), cost, toDoThis);
		} else {
			return VaultEngine.getUtils().hasAtLeast(this, cost, toDoThis);
		}
	}
	
	// -------------------------------------------------- //
	// WARMUPS
	// -------------------------------------------------- //
	
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
	
	// -------------------------------------------------- //
	// EVENTS
	// -------------------------------------------------- //

	@Override
	public void onLogin() {
		this.setKills(this.getPlayer().getStatistic(Statistic.PLAYER_KILLS));
		this.setDeaths(this.getPlayer().getStatistic(Statistic.DEATHS));
	}

	@Override
	public void onLogout() {
		// Ensure power is up to date
		this.getPower();
		
		// Update last login time
		this.setLastLoginTime(System.currentTimeMillis());
		
		// Store statistics 
		this.setKills(this.getPlayer().getStatistic(Statistic.PLAYER_KILLS));
		this.setDeaths(this.getPlayer().getStatistic(Statistic.DEATHS));
		
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
	
	// -------------------------------------------------- //
	// ABSTRACT METHODS
	// -------------------------------------------------- //

	public abstract void setName(String name);
	public abstract void setKills(int amount);
	public abstract void setDeaths(int amount);
	
	public abstract void remove();
	
}
