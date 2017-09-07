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

import net.redstoneore.legacyfactions.*;
import net.redstoneore.legacyfactions.entity.Board;
import net.redstoneore.legacyfactions.entity.Conf;
import net.redstoneore.legacyfactions.entity.FPlayer;
import net.redstoneore.legacyfactions.entity.FPlayerColl;
import net.redstoneore.legacyfactions.entity.Faction;
import net.redstoneore.legacyfactions.entity.FactionColl;
import net.redstoneore.legacyfactions.entity.persist.shared.SharedFPlayer;
import net.redstoneore.legacyfactions.event.EventFactionsChange;
import net.redstoneore.legacyfactions.event.EventFactionsChangedTerritory;
import net.redstoneore.legacyfactions.event.EventFactionsLandChange;
import net.redstoneore.legacyfactions.event.EventFactionsLandChange.LandChangeCause;
import net.redstoneore.legacyfactions.locality.Locality;
import net.redstoneore.legacyfactions.mixin.PlayerMixin;
import net.redstoneore.legacyfactions.scoreboards.FScoreboard;
import net.redstoneore.legacyfactions.scoreboards.FScoreboards;
import net.redstoneore.legacyfactions.scoreboards.FTeamWrapper;
import net.redstoneore.legacyfactions.scoreboards.sidebar.FDefaultSidebar;
import net.redstoneore.legacyfactions.struct.InteractAttemptSpam;
import net.redstoneore.legacyfactions.util.TextUtil;
import net.redstoneore.legacyfactions.util.VisualizeUtil;
import net.redstoneore.legacyfactions.util.cross.CrossMaterial;

import java.util.HashMap;
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
		FPlayerColl.get(event.getPlayer());
	}
	
	@EventHandler(priority = EventPriority.NORMAL)
	public void initPlayer(PlayerJoinEvent event) {
		this.initPlayer(event.getPlayer());
	}

	public void initPlayer(Player player) {
		// Make sure that all online players do have a fplayer.
		final FPlayer me = FPlayerColl.get(player);		
		((SharedFPlayer)me).setName(player.getName());
		
		// Update the lastLoginTime for this fplayer
		me.setLastLoginTime(System.currentTimeMillis());

		// Store player's current FLocation and notify them where they are
		me.setLastStoodAt(new FLocation(player.getLocation()));

		me.onLogin(); // set kills / deaths

		// Check for Faction announcements. Let's delay this so they actually see it.
		Bukkit.getScheduler().runTaskLater(Factions.get(), () -> {
			if (me.isOnline()) {
				me.getFaction().sendUnreadAnnouncements(me);
			}

		}, 33L);
		
		// Start the scoreboard up
		if (Conf.scoreboardDefaultEnabled) {
			FScoreboard scoreboard = FScoreboards.get(me);
			
			scoreboard.setDefaultSidebar(new FDefaultSidebar(), Conf.scoreboardDefaultUpdateIntervalSecs);
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
		FPlayerColl.get(event.getPlayer()).onLogout();
		FScoreboards.remove(FPlayerColl.get(event.getPlayer()));
	}

	// -------------------------------------------------- //
	// PLAYER MOVE
	// -------------------------------------------------- //

	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void onPlayerMove(PlayerMoveEvent event) {
		Player player = event.getPlayer();
		FPlayer me = FPlayerColl.get(player);

		// clear visualization
		if (event.getFrom().getBlockX() != event.getTo().getBlockX() || event.getFrom().getBlockY() != event.getTo().getBlockY() || event.getFrom().getBlockZ() != event.getTo().getBlockZ()) {
			VisualizeUtil.clear(event.getPlayer());
			if (me.isWarmingUp()) {
				me.clearWarmup();
				me.sendMessage(Lang.WARMUPS_CANCELLED);
			}
		}

		// quick check to make sure player is moving between chunks; good performance boost
		if (event.getFrom().getBlockX() >> 4 == event.getTo().getBlockX() >> 4 && event.getFrom().getBlockZ() >> 4 == event.getTo().getBlockZ() >> 4 && event.getFrom().getWorld() == event.getTo().getWorld()) {
			return;
		}

		// Did we change coord?
		FLocation from = me.getLastStoodAt();
		FLocation to = new FLocation(event.getTo());

		if (from.equals(to)) return;

		// Did we change "host"(faction)?
		Faction factionFrom = Board.get().getFactionAt(me.getLastLocation());
		Faction factionTo = Board.get().getFactionAt(Locality.of(event.getTo()));
		
		// Update to new location
		me.setLastStoodAt(to);

		boolean changedFaction = (factionFrom != factionTo);

		if (me.isMapAutoUpdating()) {
			if (Volatile.get().showTimes().containsKey(player.getUniqueId()) && (Volatile.get().showTimes().get(player.getUniqueId()) > System.currentTimeMillis())) {
				if (Conf.findFactionsExploitLog) {
					Factions.get().warn("%s tried to show a faction map too soon and triggered exploit blocker.", player.getName());
				}
			} else {
				me.sendMessage(Board.get().getMap(me.getFaction(), Locality.of(event.getTo()), player.getLocation().getYaw()));
				Volatile.get().showTimes().put(player.getUniqueId(), System.currentTimeMillis() + Conf.findFactionsExploitCooldownMils);
			}
		} else {
			Faction myFaction = me.getFaction();
			String ownersTo = myFaction.getOwnerListString(to);

			if (changedFaction) {
				me.sendFactionHereMessage(factionFrom);
				
				EventFactionsChangedTerritory eventChangedTerritory = new EventFactionsChangedTerritory(me, factionFrom, factionTo, me.getLastLocation(), Locality.of(event.getTo()));
				Bukkit.getServer().getPluginManager().callEvent(eventChangedTerritory);
				
				if (Conf.ownedAreasEnabled && Conf.ownedMessageOnBorder && myFaction == factionTo && !ownersTo.isEmpty()) {
					me.sendMessage(Lang.GENERIC_OWNERS.format(ownersTo));
				}
			} else if (Conf.ownedAreasEnabled && Conf.ownedMessageInsideTerritory && myFaction == factionTo && !myFaction.isWilderness()) {
				String ownersFrom = myFaction.getOwnerListString(from);
				if (Conf.ownedMessageByChunk || !ownersFrom.equals(ownersTo)) {
					if (!ownersTo.isEmpty()) {
						me.sendMessage(Lang.GENERIC_OWNERS.format(ownersTo));
					} else if (!Lang.GENERIC_PUBLICLAND.toString().isEmpty()) {
						me.sendMessage(Lang.GENERIC_PUBLICLAND.toString());
					}
				}
			}
		}

		if (me.getAutoClaimFor() != null) {
			Map<Locality, Faction> transactions = new HashMap<>();

			transactions.put(Locality.of(event.getTo()), me.getAutoClaimFor());
		   
			EventFactionsLandChange landChangeEvent = new EventFactionsLandChange(me, transactions, LandChangeCause.Claim);
			Bukkit.getServer().getPluginManager().callEvent(landChangeEvent);
			if (landChangeEvent.isCancelled()) return;
			
			landChangeEvent.transactions((locality, faction) -> {
				if ( ! me.attemptClaim(faction, locality.getLocation(), true, true)) {
					return;
				}
			});

			
		} else if (me.isAutoSafeClaimEnabled()) {
			if (!Permission.MANAGE_SAFE_ZONE.has(player)) {
				me.setIsAutoSafeClaimEnabled(false);
			} else {
				if (!Board.get().getFactionAt(Locality.of(event.getTo())).isSafeZone()) {
					Board.get().setFactionAt(FactionColl.get().getSafeZone(), to);
					me.sendMessage(Lang.PLAYER_SAFEAUTO);
				}
			}
		} else if (me.isAutoWarClaimEnabled()) {
			if (!Permission.MANAGE_WAR_ZONE.has(player)) {
				me.setIsAutoWarClaimEnabled(false);
			} else {
				if (!Board.get().getFactionAt(Locality.of(event.getTo())).isWarZone()) {
					Board.get().setFactionAt(FactionColl.get().getWarZone(), to);
					me.sendMessage(Lang.PLAYER_WARAUTO);
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
			if (Conf.handleExploitInteractionSpam) {
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
		if (Conf.playersWhoBypassAllProtection.contains(name)) {
			return true;
		}

		FPlayer me = FPlayerColl.get(player);
		if (me.isAdminBypassing()) {
			return true;
		}

		FLocation loc = new FLocation(location);
		Faction otherFaction = Board.get().getFactionAt(Locality.of(location));

		if (Conf.raidable && otherFaction.getLandRounded() >= otherFaction.getPowerRounded()) {
			return true;
		}

		if (otherFaction.hasPlayersOnline()) {
			if (!Conf.territoryDenyUseageMaterials.contains(CrossMaterial.valueOf(material.name()))) {
				return true; // Item isn't one we're preventing for online factions.
			}
		} else {
			if (!Conf.territoryDenyUseageMaterialsWhenOffline.contains(CrossMaterial.valueOf(material.name()))) {
				return true; // Item isn't one we're preventing for offline factions.
			}
		}

		if (otherFaction.isWilderness()) {
			if (!Conf.wildernessDenyUseage || Conf.worldsNoWildernessProtection.contains(location.getWorld().getName())) {
				return true; // This is not faction territory. Use whatever you like here.
			}

			if (!justCheck) {
				me.sendMessage(Lang.PLAYER_USE_WILDERNESS, TextUtil.getMaterialName(material));
			}

			return false;
		} else if (otherFaction.isSafeZone()) {
			if (!Conf.safeZoneDenyUseage || Permission.MANAGE_SAFE_ZONE.has(player)) {
				return true;
			}

			if (!justCheck) {
				me.sendMessage(Lang.PLAYER_USE_SAFEZONE, TextUtil.getMaterialName(material));
			}

			return false;
		} else if (otherFaction.isWarZone()) {
			if (!Conf.warZoneDenyUseage || Permission.MANAGE_WAR_ZONE.has(player)) {
				return true;
			}

			if (!justCheck) {
				me.sendMessage(Lang.PLAYER_USE_WARZONE, TextUtil.getMaterialName(material));
			}

			return false;
		}

		Faction myFaction = me.getFaction();
		Relation rel = myFaction.getRelationTo(otherFaction);

		// Cancel if we are not in our own territory
		if (rel.confDenyUseage()) {
			if (!justCheck) {
				me.sendMessage(Lang.PLAYER_USE_TERRITORY, TextUtil.getMaterialName(material), otherFaction.getTag(myFaction));
			}

			return false;
		}

		// Also cancel if player doesn't have ownership rights for this claim
		if (Conf.ownedAreasEnabled && Conf.ownedAreaDenyUseage && !otherFaction.playerHasOwnershipRights(me, loc)) {
			if (!justCheck) {
				me.sendMessage(Lang.PLAYER_USE_OWNED, TextUtil.getMaterialName(material), otherFaction.getOwnerListString(loc));
			}

			return false;
		}

		return true;
	}

	public static boolean canPlayerUseBlock(Player player, Block block, boolean justCheck) {
		if (Conf.playersWhoBypassAllProtection.contains(player.getName())) {
			return true;
		}

		FPlayer me = FPlayerColl.get(player);
		if (me.isAdminBypassing()) {
			return true;
		}

		Material material = block.getType();
		FLocation loc = new FLocation(block);
		Faction otherFaction = Board.get().getFactionAt(Locality.of(block));

		// no door/chest/whatever protection in wilderness, war zones, or safe zones
		if (!otherFaction.isNormal()) {
			return true;
		}

		if (Conf.raidable && otherFaction.getLandRounded() >= otherFaction.getPowerRounded()) {
			return true;
		}

		// Dupe fix.
		Faction myFaction = me.getFaction();
		Relation rel = myFaction.getRelationTo(otherFaction);
		
		if (!rel.isMember() || !otherFaction.playerHasOwnershipRights(me, loc) && me.getItemInMainHand() != null) {
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
			if (!Conf.territoryProtectedMaterials.contains(CrossMaterial.valueOf(material.name()))) {
				return true;
			}
		} else {
			if (!Conf.territoryProtectedMaterialsWhenOffline.contains(CrossMaterial.valueOf(material.name()))) {
				return true;
			}
		}

		// You may use any block unless it is another faction's territory...
		if (rel.isNeutral() || (rel.isEnemy() && Conf.territoryEnemyProtectMaterials) || (rel.isAlly() && Conf.territoryAllyProtectMaterials) || (rel.isTruce() && Conf.territoryTruceProtectMaterials)) {
			if (!justCheck) {
				me.sendMessage(Lang.PLAYER_USE_TERRITORY, (material == Material.SOIL ? "trample " : "use ") + TextUtil.getMaterialName(material), otherFaction.getTag(myFaction));
			}

			return false;
		}

		// Also cancel if player doesn't have ownership rights for this claim
		if (Conf.ownedAreasEnabled && Conf.ownedAreaProtectMaterials && !otherFaction.playerHasOwnershipRights(me, loc)) {
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
		if (Conf.homesEnabled &&
					Conf.homesTeleportToOnDeath &&
					home != null &&
					(Conf.homesRespawnFromNoPowerLossWorlds || !Conf.worldsNoPowerLoss.contains(event.getPlayer().getWorld().getName()))) {
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
		
		if (!Conf.removePlayerDataWhenBanned || !event.getPlayer().isBanned()) return;
		
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
			if (Conf.damageModifierPercentRelationLocationByMob.containsKey(relationToLocation) && Conf.damageModifierPercentRelationLocationByMob.get(relationToLocation) != null) {
				double newDamage = event.getDamage() * (Conf.damageModifierPercentRelationLocationByMob.get(relationToLocation)/100);
				event.setDamage(newDamage);
			}
		}
		
		if (damager == null) return;
		
		// Damage modifier based on location 
		if (fplayer.getLastLocation().getFactionHere().isWilderness() && Conf.damageModifierPercentWilderness != 100) {
			double newDamage = event.getDamage() * (Conf.damageModifierPercentWilderness/100);
			event.setDamage(newDamage);
		}
		
		if (fplayer.getLastLocation().getFactionHere().isWarZone() && Conf.damageModifierPercentWarzone != 100) {
			double newDamage = event.getDamage() * (Conf.damageModifierPercentWarzone/100);
			event.setDamage(newDamage);
		}
		
		if (fplayer.getLastLocation().getFactionHere().isSafeZone() && Conf.damageModifierPercentSafezone != 100) {
			double newDamage = event.getDamage() * (Conf.damageModifierPercentSafezone/100);
			event.setDamage(newDamage);
		}
		
		// Check for damager modifier by relation to player
		Relation relationToDamager = fplayer.getRelationTo(damager);
		if (Conf.damageModifierPercentRelationPlayer.containsKey(relationToDamager) && Conf.damageModifierPercentRelationPlayer.get(relationToDamager) != 100) {
			double newDamage = event.getDamage() * (Conf.damageModifierPercentRelationPlayer.get(relationToDamager)/100);
			event.setDamage(newDamage);
		}
		
		// Check for damager modifier by relation to location
		if (Conf.damageModifierPercentRelationLocationByPlayer.containsKey(relationToLocation) && Conf.damageModifierPercentRelationLocationByPlayer.get(relationToLocation) != 100) {
			double newDamage = event.getDamage() * (Conf.damageModifierPercentRelationLocationByPlayer.get(relationToLocation)/100);
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
