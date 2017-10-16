package net.redstoneore.legacyfactions.listeners;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Creature;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.*;
import org.bukkit.util.NumberConversions;

import net.redstoneore.legacyfactions.FLocation;
import net.redstoneore.legacyfactions.Factions;
import net.redstoneore.legacyfactions.Permission;
import net.redstoneore.legacyfactions.Relation;
import net.redstoneore.legacyfactions.Role;
import net.redstoneore.legacyfactions.Volatile;
import net.redstoneore.legacyfactions.config.Config;
import net.redstoneore.legacyfactions.entity.Board;
import net.redstoneore.legacyfactions.entity.FPlayer;
import net.redstoneore.legacyfactions.entity.FPlayerColl;
import net.redstoneore.legacyfactions.entity.Faction;
import net.redstoneore.legacyfactions.entity.FactionColl;
import net.redstoneore.legacyfactions.entity.persist.shared.SharedFPlayer;
import net.redstoneore.legacyfactions.entity.persist.shared.SharedFaction;
import net.redstoneore.legacyfactions.event.EventFactionsChange;
import net.redstoneore.legacyfactions.event.EventFactionsChangedTerritory;
import net.redstoneore.legacyfactions.event.EventFactionsLandChange;
import net.redstoneore.legacyfactions.event.EventFactionsLandChange.LandChangeCause;
import net.redstoneore.legacyfactions.flag.Flags;
import net.redstoneore.legacyfactions.lang.Lang;
import net.redstoneore.legacyfactions.locality.Locality;
import net.redstoneore.legacyfactions.mixin.PlayerMixin;
import net.redstoneore.legacyfactions.scoreboards.FScoreboard;
import net.redstoneore.legacyfactions.scoreboards.FScoreboards;
import net.redstoneore.legacyfactions.scoreboards.FTeamWrapper;
import net.redstoneore.legacyfactions.scoreboards.sidebar.FDefaultSidebar;
import net.redstoneore.legacyfactions.struct.InteractAttemptSpam;
import net.redstoneore.legacyfactions.util.MiscUtil;
import net.redstoneore.legacyfactions.util.StringUtils;
import net.redstoneore.legacyfactions.util.TextUtil;
import net.redstoneore.legacyfactions.util.VisualizeUtil;
import net.redstoneore.legacyfactions.util.cross.CrossMaterial;

import java.util.Map;

public class FactionsPlayerListener implements Listener {
	
	// -------------------------------------------------- //
	// INSTANCE
	// -------------------------------------------------- //
	
	private static FactionsPlayerListener i = new FactionsPlayerListener();
	public static FactionsPlayerListener get() { return i; }
	
	// -------------------------------------------------- //
	// CONSTRUCT
	// -------------------------------------------------- //
	
	private FactionsPlayerListener() {
		// When initialised we want to init all players that are already online.
		Factions.get().getServer().getOnlinePlayers()
			.stream()
			.forEach(this::initPlayer);
	}

	// -------------------------------------------------- //
	// INIT PLAYER
	// -------------------------------------------------- //
	
	@EventHandler(priority = EventPriority.LOWEST)
	public void onPlayerPreLogin(PlayerLoginEvent event) {
		// Do not cancel init events even if factions is disabled in this world. 
		FPlayerColl.get(event.getPlayer());
	}
	
	@EventHandler(priority = EventPriority.NORMAL)
	public void initPlayer(PlayerJoinEvent event) {
		// Do not cancel init events even if factions is disabled in this world.
		this.initPlayer(event.getPlayer());
	}

	public void initPlayer(Player player) {
		// Do not cancel init events even if factions is disabled in this world.
		// Make sure that all online players do have a fplayer.
		final FPlayer me = FPlayerColl.get(player);		
		((SharedFPlayer)me).setName(player.getName());
		
		// Update the lastLoginTime for this fplayer
		me.setLastLoginTime(System.currentTimeMillis());

		// Store player's current FLocation and notify them where they are
		me.setLastLocation(Locality.of(player.getLocation()));

		me.onLogin(); // set kills / deaths

		// Check for Faction announcements. Let's delay this so they actually see it.
		Bukkit.getScheduler().runTaskLater(Factions.get(), () -> {
			if (me.isOnline()) {
				me.getFaction().announcements().sendUnread(me);
			}

		}, 33L);
		
		// Start the scoreboard up
		if (Config.scoreboardDefaultEnabled) {
			FScoreboard scoreboard = FScoreboards.get(me);
			
			scoreboard.setDefaultSidebar(new FDefaultSidebar(), Config.scoreboardDefaultUpdateIntervalSecs);
			scoreboard.setSidebarVisibility(me.showScoreboard());
		}

		Faction myFaction = me.getFaction();
		if (!myFaction.isWilderness()) {
			for (FPlayer other : myFaction.getWhereOnline(true)) {
				if (other != me && other.isMonitoringJoins()) {
					other.sendMessage(Lang.FACTION_LOGIN, me.getName());
				}
			}
		}

		if (me.isSpyingChat() && !player.hasPermission(Permission.CHATSPY.getNode())) {
			me.setSpyingChat(false);
			Factions.get().log("Found %s spying chat without permission on login. Disabled their chat spying.", player.getName());
		}

		if (me.isAdminBypassing() && !player.hasPermission(Permission.BYPASS.getNode())) {
			me.setIsAdminBypassing(false);
			Factions.get().log("Found %s on admin Bypass without permission on login. Disabled it for them.", player.getName());
		}

		// If they have the permission, don't let them autoleave. Bad inverted setter :\
		me.setAutoLeave(!player.hasPermission(Permission.AUTO_LEAVE_BYPASS.getNode()));
	}

	// -------------------------------------------------- //
	// PLAYER LOGOUT
	// -------------------------------------------------- //

	@EventHandler(priority = EventPriority.NORMAL)
	public void playerLogout(PlayerQuitEvent event) {
		FPlayer fplayer = FPlayerColl.get(event.getPlayer());
		fplayer.onLogout();
		FScoreboards.remove(fplayer);
		
		if (Config.teleportToSpawnOnLogoutInRelationEnabled) {
			if (Config.teleportToSpawnOnLogoutInRelationWorlds.contains(fplayer.getLastLocation().getWorld().getName())) {
				Relation relation = fplayer.getLastLocation().getFactionHere().getRelationTo(fplayer);
				if (Config.teleportToSpawnOnLogoutInRelation.contains(relation)) {
					fplayer.teleport(Locality.of(fplayer.getLastLocation().getWorld().getSpawnLocation()));
				}
			}
		}
	}

	// -------------------------------------------------- //
	// PLAYER MOVE
	// -------------------------------------------------- //
	
	
	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void onLocationChange(PlayerTeleportEvent event) {
		Player player = event.getPlayer();
		FPlayer me = FPlayerColl.get(player);
		
		this.onLocationChange(me, event.getFrom(), event.getTo());
	}
	
	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void onLocationChange(PlayerMoveEvent event) {
		Player player = event.getPlayer();
		FPlayer me = FPlayerColl.get(player);

		this.onLocationChange(me, event.getFrom(), event.getTo());
	}
	
	public void onLocationChange(FPlayer me, Location locationFrom, Location locationTo) {
		if (locationFrom.getBlockX() >> 4 == locationTo.getBlockX() >> 4 &&
			locationFrom.getBlockZ() >> 4 == locationTo.getBlockZ() >> 4 &&
			locationFrom.getWorld().getUID() == locationTo.getWorld().getUID()) return;
		
		Locality from = Locality.of(locationFrom);
		Locality to = Locality.of(locationTo);
		
		if (from.equals(to)) return;
		me.setLastLocation(to);
		
		VisualizeUtil.clear(me);
		
		if (me.isWarmingUp()) {
			me.clearWarmup();
			Lang.WARMUPS_CANCELLED.getBuilder()
				.parse()
				.sendTo(me);
		}
		
		// Did we change Faction land?
		Faction factionFrom = Board.get().getFactionAt(from);
		Faction factionTo = Board.get().getFactionAt(to);
		
		// Update to new location
		boolean changedFaction = (factionFrom != factionTo);

		if (me.isMapAutoUpdating()) {
			if (Volatile.get().showTimes().containsKey(me.getId()) && (Volatile.get().showTimes().get(me.getId()) > System.currentTimeMillis())) {
				if (Config.findFactionsExploitLog) {
					Factions.get().warn("%s tried to show a faction map too soon and triggered exploit blocker.", me.getName());
				}
			} else {
				me.sendMessage(Board.get().getMap(me.getFaction(), to, locationFrom.getYaw()));
				Volatile.get().showTimes().put(me.getId(), System.currentTimeMillis() + Config.findFactionsExploitCooldownMils);
			}
		} else {
			Faction myFaction = me.getFaction();
			String ownersTo = StringUtils.join(myFaction.ownership().getOwners(to));

			if (changedFaction) {
				me.sendFactionHereMessage(factionFrom);
				
				EventFactionsChangedTerritory eventChangedTerritory = new EventFactionsChangedTerritory(me, factionFrom, factionTo, me.getLastLocation(), to);
				Bukkit.getServer().getPluginManager().callEvent(eventChangedTerritory);
				
				if (Config.ownedAreasEnabled && Config.ownedMessageOnBorder && myFaction == factionTo && !ownersTo.isEmpty()) {
					me.sendMessage(Lang.GENERIC_OWNERS.format(ownersTo));
				}
			} else if (Config.ownedAreasEnabled && Config.ownedMessageInsideTerritory && myFaction == factionTo && !myFaction.isWilderness()) {
				String ownersFrom = StringUtils.join(myFaction.ownership().getOwners(from));
				if (Config.ownedMessageByChunk || !ownersFrom.equals(ownersTo)) {
					if (!ownersTo.isEmpty()) {
						me.sendMessage(Lang.GENERIC_OWNERS.format(ownersTo));
					} else if (!Lang.GENERIC_PUBLICLAND.toString().isEmpty()) {
						me.sendMessage(Lang.GENERIC_PUBLICLAND.toString());
					}
				}
			}
		}

		if (me.getAutoClaimFor() != null) {
			Map<Locality, Faction> transactions = MiscUtil.newMap(
				to, me.getAutoClaimFor()
			);
			
			EventFactionsLandChange landChangeEvent = new EventFactionsLandChange(me, transactions, LandChangeCause.Claim);
			Bukkit.getServer().getPluginManager().callEvent(landChangeEvent);
			if (landChangeEvent.isCancelled()) return;
			
			landChangeEvent.transactions((locality, faction) -> me.attemptClaim(faction, locality.getLocation(), true, true));

		} else if (me.isAutoSafeClaimEnabled()) {
			if (!Permission.MANAGE_SAFE_ZONE.has(me)) {
				me.setIsAutoSafeClaimEnabled(false);
			} else {
				if (!Board.get().getFactionAt(to).isSafeZone()) {
					Board.get().setFactionAt(FactionColl.get().getSafeZone(), to);
					Lang.PLAYER_SAFEAUTO.getBuilder()
						.parse()
						.sendTo(me);
				}
			}
		} else if (me.isAutoWarClaimEnabled()) {
			if (!Permission.MANAGE_WAR_ZONE.has(me)) {
				me.setIsAutoWarClaimEnabled(false);
			} else {
				if (!Board.get().getFactionAt(to).isWarZone()) {
					Board.get().setFactionAt(FactionColl.get().getWarZone(), to);
					Lang.PLAYER_WARAUTO.getBuilder()
						.parse()
						.sendTo(me);
				}
			}
		}
	}

	// -------------------------------------------------- //
	// INTERACT CHECK
	// -------------------------------------------------- //

	@EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
	public void onPlayerInteract(PlayerInteractEvent event) {
		// only need to check right-clicks and physical as of MC 1.4+; good performance boost
		if (event.getAction() != Action.RIGHT_CLICK_BLOCK && event.getAction() != Action.PHYSICAL) {
			return;
		}

		Block block = event.getClickedBlock();
		Player player = event.getPlayer();
		
		if (block == null || block.getType() == Material.AIR) return;

		if (!canPlayerUseBlock(player, block, false)) {
			event.setCancelled(true);
			if (Config.handleExploitInteractionSpam) {
				String name = player.getName();
				InteractAttemptSpam attempt = Volatile.get().interactSpammers().get(name);
				if (attempt == null) {
					attempt = new InteractAttemptSpam();
					Volatile.get().interactSpammers().put(name, attempt);
				}
				int count = attempt.increment();
				if (count >= 10) {
					FPlayer me = FPlayerColl.get(player);
					me.sendMessage(Lang.PLAYER_OUCH);
					player.damage(NumberConversions.floor((double) count / 10));
				}
			}
			return;
		}
		
		// Check they can use this here
		if (event.getAction() == Action.RIGHT_CLICK_BLOCK && !playerCanUseItemHere(player, block.getLocation(), event.getMaterial(), false)) {
			event.setCancelled(true);
		}
	}
	
	public static boolean playerCanUseItemHere(Player player, Location location, Material material, boolean justCheck) {
		String name = player.getName();
		if (Config.playersWhoBypassAllProtection.contains(name)) {
			return true;
		}

		FPlayer me = FPlayerColl.get(player);
		if (me.isAdminBypassing()) {
			return true;
		}

		Locality locality = Locality.of(location);
		SharedFaction otherFaction = (SharedFaction) Board.get().getFactionAt(Locality.of(location));

		if (Config.raidable && otherFaction.getLandRounded() >= otherFaction.getPowerRounded()) {
			return true;
		}

		if (otherFaction.hasPlayersOnline()) {
			if (!Config.territoryDenyUseageMaterials.contains(CrossMaterial.valueOf(material.name()))) {
				return true; // Item isn't one we're preventing for online factions.
			}
		} else {
			if (!Config.territoryDenyUseageMaterialsWhenOffline.contains(CrossMaterial.valueOf(material.name()))) {
				return true; // Item isn't one we're preventing for offline factions.
			}
		}

		if (otherFaction.isWilderness()) {
			if (!Config.wildernessDenyUseage || Config.worldsNoWildernessProtection.contains(location.getWorld().getName())) {
				return true; // This is not faction territory. Use whatever you like here.
			}

			if (!justCheck) {
				me.sendMessage(Lang.PLAYER_USE_WILDERNESS, TextUtil.getMaterialName(material));
			}

			return false;
		} else if (otherFaction.isSafeZone()) {
			if (!Config.safeZoneDenyUseage || Permission.MANAGE_SAFE_ZONE.has(player)) {
				return true;
			}

			if (!justCheck) {
				me.sendMessage(Lang.PLAYER_USE_SAFEZONE, TextUtil.getMaterialName(material));
			}

			return false;
		} else if (otherFaction.isWarZone()) {
			if (!Config.warZoneDenyUseage || Permission.MANAGE_WAR_ZONE.has(player)) {
				return true;
			}

			if (!justCheck) {
				me.sendMessage(Lang.PLAYER_USE_WARZONE, TextUtil.getMaterialName(material));
			}

			return false;
		}

		SharedFaction myFaction = (SharedFaction) me.getFaction();
		Relation rel = myFaction.getRelationTo(otherFaction);

		// Cancel if we are not in our own territory
		if (rel.confDenyUseage()) {
			if (!justCheck) {
				me.sendMessage(Lang.PLAYER_USE_TERRITORY, TextUtil.getMaterialName(material), otherFaction.getTag(myFaction));
			}

			return false;
		}

		// Also cancel if player doesn't have ownership rights for this claim
		if (Config.ownedAreasEnabled && Config.ownedAreaDenyUseage && otherFaction.ownership().isOwned(locality) && !otherFaction.ownership().isOwner(locality, me)) {
			if (!justCheck) {
				me.sendMessage(Lang.PLAYER_USE_OWNED, TextUtil.getMaterialName(material), StringUtils.join(otherFaction.ownership().getOwners(locality)));
			}

			return false;
		}

		return true;
	}

	public static boolean canPlayerUseBlock(Player player, Block block, boolean justCheck) {
		if (Config.playersWhoBypassAllProtection.contains(player.getName())) {
			return true;
		}

		FPlayer me = FPlayerColl.get(player);
		if (me.isAdminBypassing()) {
			return true;
		}

		Material material = block.getType();
		
		// TODO: move to Locality
		FLocation loc = new FLocation(block);
		SharedFaction otherFaction = (SharedFaction) Board.get().getFactionAt(Locality.of(block));

		// no door/chest/whatever protection in wilderness, war zones, or safe zones
		if (!otherFaction.isNormal()) {
			return true;
		}

		if (Config.raidable && otherFaction.getLandRounded() >= otherFaction.getPowerRounded()) {
			return true;
		}

		// Dupe fix.
		SharedFaction myFaction = (SharedFaction) me.getFaction();
		Relation rel = myFaction.getRelationTo(otherFaction);
		
		if (!rel.isMember() || !otherFaction.getFlag(Flags.EXPLOSIONS) && otherFaction.playerHasOwnershipRights(me, loc) && me.getItemInMainHand() != null) {
			switch (me.getItemInMainHand().getType()) {
				case CHEST:
				case SIGN_POST:
				case TRAPPED_CHEST:
				case SIGN:
				case WOOD_DOOR:
				case IRON_DOOR:
					return false;
				default:
					break;
			}
		}

		// We only care about some material types.
		if (otherFaction.hasPlayersOnline()) {
			if (!Config.territoryProtectedMaterials.contains(CrossMaterial.valueOf(material.name()))) {
				return true;
			}
		} else {
			if (!Config.territoryProtectedMaterialsWhenOffline.contains(CrossMaterial.valueOf(material.name()))) {
				return true;
			}
		}

		// You may use any block unless it is another faction's territory...
		if (rel.isNeutral() || (rel.isEnemy() && Config.territoryEnemyProtectMaterials) || (rel.isAlly() && Config.territoryAllyProtectMaterials) || (rel.isTruce() && Config.territoryTruceProtectMaterials)) {
			if (!justCheck) {
				me.sendMessage(Lang.PLAYER_USE_TERRITORY, (material == Material.SOIL ? "trample " : "use ") + TextUtil.getMaterialName(material), otherFaction.getTag(myFaction));
			}

			return false;
		}

		// Also cancel if player doesn't have ownership rights for this claim
		if (Config.ownedAreasEnabled && Config.ownedAreaProtectMaterials && !otherFaction.playerHasOwnershipRights(me, loc)) {
			if (!justCheck) {
				me.sendMessage(Lang.PLAYER_USE_OWNED, TextUtil.getMaterialName(material), otherFaction.getOwnerListString(loc));
			}

			return false;
		}

		return true;
	}

	@EventHandler(priority = EventPriority.HIGH)
	public void onPlayerRespawn(PlayerRespawnEvent event) {
		FPlayer me = FPlayerColl.get(event.getPlayer());

		me.getPower();  // update power, so they won't have gained any while dead

		Location home = me.getFaction().getHome();
		if (Config.homesEnabled &&
					Config.homesTeleportToOnDeath &&
					home != null &&
					(Config.homesRespawnFromNoPowerLossWorlds || !Config.worldsNoPowerLoss.contains(event.getPlayer().getWorld().getName()))) {
			event.setRespawnLocation(home);
		}
	}

	// For some reason onPlayerInteract() sometimes misses bucket events depending on distance (something like 2-3 blocks away isn't detected),
	// but these separate bucket events below always fire without fail
	@EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
	public void onPlayerBucketEmpty(PlayerBucketEmptyEvent event) {
		Block block = event.getBlockClicked();
		Player player = event.getPlayer();

		if (!playerCanUseItemHere(player, block.getLocation(), event.getBucket(), false)) {
			event.setCancelled(true);
			return;
		}
	}

	@EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
	public void onPlayerBucketFill(PlayerBucketFillEvent event) {
		Block block = event.getBlockClicked();
		Player player = event.getPlayer();

		if (!playerCanUseItemHere(player, block.getLocation(), event.getBucket(), false)) {
			event.setCancelled(true);
			return;
		}
	}


	// -------------------------------------------------- //
	// BAN CHECK
	// -------------------------------------------------- //	
	
	@EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
	public void removeDataWhenBanned(PlayerKickEvent event) {
		FPlayer badGuy = FPlayerColl.get(event.getPlayer());
		if (badGuy == null) return;
		
		if (!Config.removePlayerDataWhenBanned || !event.getPlayer().isBanned()) return;
		
		if (badGuy.getRole() == Role.ADMIN) {
			badGuy.getFaction().promoteNewLeader();
		}

		badGuy.leave(false);
		badGuy.remove();
	}
	
	// -------------------------------------------------- //
	// DAMAGE MODIFIER
	// -------------------------------------------------- //
	
	@EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
	public void damageModifier(EntityDamageByEntityEvent event) {
		if (!(event.getEntity() instanceof Player)) return;
		if (event.getDamage() == 0) return;
		
		// NPC Check
		// Some NPC plugin's will not play nicely with LegacyFactions damage modifier so we will
		// not do anything if an NPC is invovled.
		if (PlayerMixin.isNPC(event.getEntity())) return;
		
		FPlayer fplayer = FPlayerColl.get(event.getEntity());
		if (fplayer == null) return;
		
		FPlayer damager = null;
		
		Relation relationToLocation = fplayer.getRelationTo(fplayer.getLastLocation().getFactionHere());

		if (event.getDamager() instanceof Projectile) {
			Projectile projectile = (Projectile) event.getDamager();
			if (projectile.getShooter() instanceof Player) {
				damager = FPlayerColl.get(projectile.getShooter());				
			} if (!(projectile.getShooter() instanceof Creature)) {
				// Not a player or a creature
				return;
			}
		} else if (event.getDamager() instanceof Player) {
			damager = FPlayerColl.get(event.getDamager());
		}
		
		if (damager == null) {
			// must be a mob attacking 
			if (Config.damageModifierPercentRelationLocationByMob.containsKey(relationToLocation) && Config.damageModifierPercentRelationLocationByMob.get(relationToLocation) != null) {
				double newDamage = event.getDamage() * (Config.damageModifierPercentRelationLocationByMob.get(relationToLocation)/100);
				event.setDamage(newDamage);
			}
		}
		
		if (damager == null) return;
		
		// Damage modifier based on location 
		if (fplayer.getLastLocation().getFactionHere().isWilderness() && Config.damageModifierPercentWilderness != 100) {
			double newDamage = event.getDamage() * (Config.damageModifierPercentWilderness/100);
			event.setDamage(newDamage);
		}
		
		if (fplayer.getLastLocation().getFactionHere().isWarZone() && Config.damageModifierPercentWarzone != 100) {
			double newDamage = event.getDamage() * (Config.damageModifierPercentWarzone/100);
			event.setDamage(newDamage);
		}
		
		if (fplayer.getLastLocation().getFactionHere().isSafeZone() && Config.damageModifierPercentSafezone != 100) {
			double newDamage = event.getDamage() * (Config.damageModifierPercentSafezone/100);
			event.setDamage(newDamage);
		}
		
		// Check for damager modifier by relation to player
		Relation relationToDamager = fplayer.getRelationTo(damager);
		if (Config.damageModifierPercentRelationPlayer.containsKey(relationToDamager) && Config.damageModifierPercentRelationPlayer.get(relationToDamager) != 100) {
			double newDamage = event.getDamage() * (Config.damageModifierPercentRelationPlayer.get(relationToDamager)/100);
			event.setDamage(newDamage);
		}
		
		// Check for damager modifier by relation to location
		if (Config.damageModifierPercentRelationLocationByPlayer.containsKey(relationToLocation) && Config.damageModifierPercentRelationLocationByPlayer.get(relationToLocation) != 100) {
			double newDamage = event.getDamage() * (Config.damageModifierPercentRelationLocationByPlayer.get(relationToLocation)/100);
			event.setDamage(newDamage);
		}
		
	}
	
	// -------------------------------------------------- //
	// SCOREBOARD CLEANUP
	// -------------------------------------------------- //	
	
	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void onFactionLeave(EventFactionsChange event) {
		FTeamWrapper.applyUpdatesLater(event.getFactionNew());
		FTeamWrapper.applyUpdatesLater(event.getFactionOld());
	}
	
	// -------------------------------------------------- //
	// DEPRECATED
	// -------------------------------------------------- //	
	
	/**
	 * Relocated, use {@link FactionsCommandsListener#preventCommand(String, Player)}<br>
	 * TODO: Scheduled for removal 11/2016
	 * @param fullCmd
	 * @param player
	 * @return
	 */
	@Deprecated
	public boolean preventCommand(String fullCmd, Player player) {
		return FactionsCommandsListener.get().preventCommand(fullCmd, player);
	}

	/**
	 * Relocated, use {@link FactionsCommandsListener#preventCommand(String, Player, Boolean)}
	 * TODO: Scheduled for removal 11/2016
	 * @param fullCmd
	 * @param player
	 * @param silent
	 * @return
	 */
	@Deprecated
	public boolean preventCommand(String fullCmd, Player player, Boolean silent) {
		return FactionsCommandsListener.get().preventCommand(fullCmd, player, silent);
	}
	
}
