package net.redstoneore.legacyfactions.listeners;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.TravelAgent;
import org.bukkit.block.Block;
import org.bukkit.entity.*;
import org.bukkit.entity.minecart.ExplosiveMinecart;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.*;
import org.bukkit.event.hanging.HangingBreakByEntityEvent;
import org.bukkit.event.hanging.HangingBreakEvent;
import org.bukkit.event.hanging.HangingBreakEvent.RemoveCause;
import org.bukkit.event.hanging.HangingPlaceEvent;
import org.bukkit.event.player.PlayerPortalEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.projectiles.ProjectileSource;

import net.redstoneore.legacyfactions.LandAction;
import net.redstoneore.legacyfactions.Relation;
import net.redstoneore.legacyfactions.Volatile;
import net.redstoneore.legacyfactions.config.Config;
import net.redstoneore.legacyfactions.entity.Board;
import net.redstoneore.legacyfactions.entity.FPlayer;
import net.redstoneore.legacyfactions.entity.FPlayerColl;
import net.redstoneore.legacyfactions.entity.Faction;
import net.redstoneore.legacyfactions.event.EventFactionsPowerLoss;
import net.redstoneore.legacyfactions.flag.Flags;
import net.redstoneore.legacyfactions.lang.Lang;
import net.redstoneore.legacyfactions.locality.Locality;
import net.redstoneore.legacyfactions.mixin.PlayerMixin;
import net.redstoneore.legacyfactions.util.LocationUtil;
import net.redstoneore.legacyfactions.util.MiscUtil;
import net.redstoneore.legacyfactions.util.cross.CrossEntityType;

import java.util.*;


public class FactionsEntityListener implements Listener {
	
	// -------------------------------------------------- //
	// INSTANCE
	// -------------------------------------------------- //

	private static FactionsEntityListener i = new FactionsEntityListener();
	public static FactionsEntityListener get() { return i; }
	
	// -------------------------------------------------- //
	// METHODS
	// -------------------------------------------------- //
	
	@EventHandler(priority = EventPriority.NORMAL)
	public void onEntityDeath(EntityDeathEvent event) {
		Entity entity = event.getEntity();
		if (!(entity instanceof Player)) return;
		
		Player player = (Player) entity;
		
		if (LocationUtil.isFactionsDisableIn(player)) return;
		
		FPlayer fplayer = FPlayerColl.get(player);
		Faction faction = Board.get().getFactionAt(Locality.of(player.getLocation()));

		EventFactionsPowerLoss powerLossEvent = new EventFactionsPowerLoss(faction, fplayer, Config.powerPerDeath);
		// Check for no power loss conditions
		if (faction.isWarZone()) {
			// war zones always override worldsNoPowerLoss either way, thus this layout
			if (!Config.warZonePowerLoss) {
				powerLossEvent.setMessage(Lang.PLAYER_POWER_NOLOSS_WARZONE.toString());
				powerLossEvent.setCancelled(true);
			}
			if (Config.worldsNoPowerLoss.contains(player.getWorld().getName())) {
				powerLossEvent.setMessage(Lang.PLAYER_POWER_LOSS_WARZONE.toString());
			}
		} else if (faction.isWilderness() && !Config.wildernessPowerLoss && !Config.worldsNoWildernessProtection.contains(player.getWorld().getName())) {
			powerLossEvent.setMessage(Lang.PLAYER_POWER_NOLOSS_WILDERNESS.toString());
			powerLossEvent.setCancelled(true);
		} else if (Config.worldsNoPowerLoss.contains(player.getWorld().getName())) {
			powerLossEvent.setMessage(Lang.PLAYER_POWER_NOLOSS_WORLD.toString());
			powerLossEvent.setCancelled(true);
		} else if (Config.peacefulMembersDisablePowerLoss && fplayer.hasFaction() && fplayer.getFaction().getFlag(Flags.PEACEFUL)) {
			powerLossEvent.setMessage(Lang.PLAYER_POWER_NOLOSS_PEACEFUL.toString());
			powerLossEvent.setCancelled(true);
		} else {
			powerLossEvent.setMessage(Lang.PLAYER_POWER_NOW.toString());
		}

		// call Event
		Bukkit.getPluginManager().callEvent(powerLossEvent);

		// Call player onDeath if the event is not cancelled
		if (!powerLossEvent.isCancelled()) {
			fplayer.onDeath(powerLossEvent.getPowerLoss());
		}
		// Send the message from the powerLossEvent
		final String msg = powerLossEvent.getMessage();
		if (msg != null && !msg.isEmpty()) {
			fplayer.sendMessage(msg, fplayer.getPowerRounded(), fplayer.getPowerMaxRounded());
		}
	}

	/**
	 * Who can I hurt? I can never hurt members or allies. I can always hurt enemies. I can hurt neutrals as long as
	 * they are outside their own territory.
	 */
	@EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
	public void onEntityDamage(EntityDamageEvent event) {
		if (LocationUtil.isFactionsDisableIn(event)) return;
		
		if (event instanceof EntityDamageByEntityEvent) {
			EntityDamageByEntityEvent sub = (EntityDamageByEntityEvent) event;
			if (!this.canDamagerHurtDamagee(sub, true)) {
				event.setCancelled(true);
			}
			// event is not cancelled by factions

			Entity damagee = sub.getEntity();
			Entity damager = sub.getDamager();

			if (damagee != null && damagee instanceof Player) {
				cancelFStuckTeleport((Player) damagee);
			}
			if (damager instanceof Player) {
				cancelFStuckTeleport((Player) damager);
			}
		} else if (Config.safeZonePreventAllDamageToPlayers && isPlayerInSafeZone(event.getEntity())) {
			// Players can not take any damage in a Safe Zone
			event.setCancelled(true);
		}

		// entity took generic damage?
		Entity entity = event.getEntity();
		if (entity instanceof Player) {
			Player player = (Player) entity;
			FPlayer me = FPlayerColl.get(player);
			cancelFStuckTeleport(player);
			if (me.isWarmingUp()) {
				me.clearWarmup();
				me.sendMessage(Lang.WARMUPS_CANCELLED);
			}
		}
	}

	public void cancelFStuckTeleport(Player player) {
		if (player == null) return;
		
		UUID uuid = player.getUniqueId();
		if (Volatile.get().stuckMap().containsKey(uuid)) {
			FPlayerColl.get(player).sendMessage(Lang.COMMAND_STUCK_CANCELLED);
			Volatile.get().stuckMap().remove(uuid);
		}
	}

	@EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
	public void blockExplosion(EntityExplodeEvent event) {
		if (LocationUtil.isFactionsDisableIn(event)) return;
		
		Location loc = event.getLocation();
		Entity entity = event.getEntity();
		
		Faction faction = Board.get().getFactionAt(Locality.of(loc));
		
		boolean online = faction.hasPlayersOnline();

		if (faction.getFlag(Flags.EXPLOSIONS) || (faction.getFlag(Flags.PEACEFUL) && Config.peacefulTerritoryDisableBoom)) {
			event.setCancelled(true);
			return;
		}

		if (entity instanceof Creeper && faction.noCreeperExplosions(loc)) {
			event.setCancelled(true);
			return;
		} 
		
		if ((entity instanceof Fireball || entity instanceof WitherSkull || entity instanceof Wither) &&
			((faction.isWilderness() && Config.wildernessBlockFireballs && !Config.worldsNoWildernessProtection.contains(loc.getWorld().getName()))
			  || (faction.isNormal() && (online ? Config.territoryBlockFireballs : Config.territoryBlockFireballsWhenOffline)) 
			  || (faction.isWarZone() && Config.warZoneBlockFireballs) 
			  || faction.isSafeZone())) {
		   
			event.setCancelled(true);
			return;
		}
		
		if ((entity instanceof TNTPrimed || entity instanceof ExplosiveMinecart) &&
			((faction.isWilderness() && Config.wildernessBlockTNT && !Config.worldsNoWildernessProtection.contains(loc.getWorld().getName()))
			 || (faction.isNormal() && (online ? Config.territoryBlockTNT : Config.territoryBlockTNTWhenOffline)) 
			 || (faction.isWarZone() && Config.warZoneBlockTNT)
			 || (faction.isSafeZone() && Config.safeZoneBlockTNT))) {
			// TNT which needs prevention
			event.setCancelled(true);
			return;
		}
		if ( 
				(entity instanceof TNTPrimed || entity instanceof ExplosiveMinecart) && Config.handleExploitTNTWaterlog) {
			// TNT in water/lava doesn't normally destroy any surrounding blocks, which is usually desired behavior, but...
			// this change below provides workaround for waterwalling providing perfect protection,
			// and makes cheap (non-obsidian) TNT cannons require minor maintenance between shots
			Block center = loc.getBlock();
			if (center.isLiquid()) {
				// a single surrounding block in all 6 directions is broken if the material is weak enough
				List<Block> targets = new ArrayList<Block>();
				targets.add(center.getRelative(0, 0, 1));
				targets.add(center.getRelative(0, 0, -1));
				targets.add(center.getRelative(0, 1, 0));
				targets.add(center.getRelative(0, -1, 0));
				targets.add(center.getRelative(1, 0, 0));
				targets.add(center.getRelative(-1, 0, 0));
				for (Block target : targets) {
					// Switches are a lot faster than if statements
					switch (target.getType()) {
					case AIR:
					case WATER:
					case STATIONARY_WATER:
					case LAVA:
					case STATIONARY_LAVA:
					case OBSIDIAN:
					case PORTAL:
					case ENDER_PORTAL:
					case ENDER_PORTAL_FRAME:
					case ENCHANTMENT_TABLE:
					case ENDER_CHEST:
						break;
					default:
						target.breakNaturally();
					}
				}
			}
		}
	}

	// mainly for flaming arrows; don't want allies or people in safe zones to be ignited even after damage event is cancelled
	@SuppressWarnings("deprecation")
	@EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
	public void onEntityCombustByEntity(EntityCombustByEntityEvent event) {
		if (LocationUtil.isFactionsDisableIn(event)) return;
		
		EntityDamageByEntityEvent sub = new EntityDamageByEntityEvent(event.getCombuster(), event.getEntity(), EntityDamageEvent.DamageCause.FIRE, 0d);
		if (!this.canDamagerHurtDamagee(sub, false)) {
			event.setCancelled(true);
		}
		sub = null;
	}

	private static final Set<PotionEffectType> badPotionEffects = new LinkedHashSet<PotionEffectType>(Arrays.asList(PotionEffectType.BLINDNESS, PotionEffectType.CONFUSION, PotionEffectType.HARM, PotionEffectType.HUNGER, PotionEffectType.POISON, PotionEffectType.SLOW, PotionEffectType.SLOW_DIGGING, PotionEffectType.WEAKNESS, PotionEffectType.WITHER));

	@EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
	public void onPotionSplashEvent(PotionSplashEvent event) {
		if (LocationUtil.isFactionsDisableIn(event)) return;
		
		// see if the potion has a harmful effect
		boolean badjuju = false;
		for (PotionEffect effect : event.getPotion().getEffects()) {
			if (badPotionEffects.contains(effect.getType())) {
				badjuju = true;
				break;
			}
		}
		if (!badjuju) {
			return;
		}

		ProjectileSource thrower = event.getPotion().getShooter();
		if (!(thrower instanceof Entity)) {
			return;
		}

		if (thrower instanceof Player) {
			Player player = (Player) thrower;
			FPlayer fPlayer = FPlayerColl.get(player);
			if (badjuju && fPlayer.getFaction().getFlag(Flags.PEACEFUL)) {
				event.setCancelled(true);
				return;
			}
		}

		// scan through affected entities to make sure they're all valid targets
		Iterator<LivingEntity> iter = event.getAffectedEntities().iterator();
		while (iter.hasNext()) {
			LivingEntity target = iter.next();
			@SuppressWarnings("deprecation")
			EntityDamageByEntityEvent sub = new EntityDamageByEntityEvent((Entity) thrower, target, EntityDamageEvent.DamageCause.CUSTOM, 0);
			if (!this.canDamagerHurtDamagee(sub, true)) {
				event.setIntensity(target, 0.0);  // affected entity list doesn't accept modification (so no iter.remove()), but this works
			}
			sub = null;
		}
	}

	public boolean isPlayerInSafeZone(Entity damagee) {
		if (!(damagee instanceof Player)) {
			return false;
		}
		if (Board.get().getFactionAt(Locality.of(damagee.getLocation())).isSafeZone()) {
			return true;
		}
		return false;
	}

	public boolean canDamagerHurtDamagee(EntityDamageByEntityEvent sub) {
		if (LocationUtil.isFactionsDisableIn(sub)) return true;
		return canDamagerHurtDamagee(sub, true);
	}

	public boolean canDamagerHurtDamagee(EntityDamageByEntityEvent sub, boolean notify) {
		if (LocationUtil.isFactionsDisableIn(sub)) return true;
		
		Entity damager = sub.getDamager();
		Entity damagee = sub.getEntity();

		if (!(damagee instanceof Player)) {
			return true;
		}

		FPlayer defender = FPlayerColl.get(damagee);

		if (defender == null || defender.getPlayer() == null) {
			return true;
		}

		Location defenderLoc = defender.getPlayer().getLocation();
		Faction defLocFaction = Board.get().getFactionAt(Locality.of(defenderLoc));

		// for damage caused by projectiles, getDamager() returns the projectile... what we need to know is the source
		if (damager instanceof Projectile) {
			Projectile projectile = (Projectile) damager;

			if (!(projectile.getShooter() instanceof Entity)) {
				return true;
			}

			damager = (Entity) projectile.getShooter();
		}

		if (damager == damagee)  // ender pearl usage and other self-inflicted damage
		{
			return true;
		}

		// Players can not take attack damage in a SafeZone, or possibly peaceful territory
		if (defLocFaction.noPvPInTerritory()) {
			if (damager instanceof Player) {
				if (notify) {
					FPlayer attacker = FPlayerColl.get((Player) damager);
					attacker.sendMessage(Lang.PLAYER_CANTHURT, (defLocFaction.isSafeZone() ? Lang.REGION_SAFEZONE.toString() : Lang.REGION_PEACEFUL.toString()));
				}
				return false;
			}
			return !defLocFaction.noMonstersInTerritory();
		}

		if (!(damager instanceof Player)) {
			return true;
		}

		FPlayer attacker = FPlayerColl.get(damager);

		if (attacker == null || attacker.getPlayer() == null) {
			return true;
		}

		if (Config.playersWhoBypassAllProtection.contains(attacker.getName())) {
			return true;
		}

		if (attacker.hasLoginPvpDisabled()) {
			if (notify) {
				attacker.sendMessage(Lang.PLAYER_PVP_LOGIN, Config.noPVPDamageToOthersForXSecondsAfterLogin);
			}
			return false;
		}

		Faction locFaction = Board.get().getFactionAt(Locality.of(attacker));

		// so we know from above that the defender isn't in a safezone... what about the attacker, sneaky dog that he might be?
		if (locFaction.noPvPInTerritory()) {
			if (notify) {
				attacker.sendMessage(Lang.PLAYER_CANTHURT, (locFaction.isSafeZone() ? Lang.REGION_SAFEZONE.toString() : Lang.REGION_PEACEFUL.toString()));
			}
			return false;
		}

		if (locFaction.isWarZone() && Config.warZoneFriendlyFire) {
			return true;
		}

		if (Config.worldsIgnorePvP.contains(defenderLoc.getWorld().getName())) {
			return true;
		}

		Faction defendFaction = defender.getFaction();
		Faction attackFaction = attacker.getFaction();

		if (attackFaction.isWilderness() && Config.disablePVPForFactionlessPlayers) {
			if (notify) {
				attacker.sendMessage(Lang.PLAYER_PVP_REQUIREFACTION);
			}
			return false;
		} else if (defendFaction.isWilderness()) {
			if (defLocFaction == attackFaction && Config.enablePVPAgainstFactionlessInAttackersLand) {
				// Allow PVP vs. Factionless in attacker's faction territory
				return true;
			} else if (Config.disablePVPForFactionlessPlayers) {
				if (notify) {
					attacker.sendMessage(Lang.PLAYER_PVP_FACTIONLESS);
				}
				return false;
			}
		}

		if (defendFaction.getFlag(Flags.PEACEFUL)) {
			if (notify) {
				attacker.sendMessage(Lang.PLAYER_PVP_PEACEFUL);
			}
			return false;
		} else if (attackFaction.getFlag(Flags.PEACEFUL)) {
			if (notify) {
				attacker.sendMessage(Lang.PLAYER_PVP_PEACEFUL);
			}
			return false;
		}

		Relation relation = defendFaction.getRelationTo(attackFaction);

		// You can not hurt neutral factions
		if (Config.disablePVPBetweenNeutralFactions && relation.isNeutral()) {
			if (notify) {
				attacker.sendMessage(Lang.PLAYER_PVP_NEUTRAL);
			}
			return false;
		}

		// Players without faction may be hurt anywhere
		if (!defender.hasFaction()) {
			return true;
		}

		// You can never hurt faction members or allies
		if (relation.isMember() || relation.isAlly()) {
			if (notify) {
				attacker.sendMessage(Lang.PLAYER_PVP_CANTHURT, defender.describeTo(attacker));
			}
			return false;
		}

		boolean ownTerritory = defender.isInOwnTerritory();

		// You can not hurt neutrals in their own territory.
		if (ownTerritory && relation.isNeutral()) {
			if (notify) {
				attacker.sendMessage(Lang.PLAYER_PVP_NEUTRALFAIL, defender.describeTo(attacker));
				defender.sendMessage(Lang.PLAYER_PVP_TRIED, attacker.describeTo(defender, true));
			}
			return false;
		}

		// Damage will be dealt. However check if the damage should be reduced.
		/*
		if (damage > 0.0 && ownTerritory && Conf.territoryShieldFactor > 0) {
			double newDamage = Math.ceil(damage * (1D - Conf.territoryShieldFactor));
			sub.setDamage(newDamage);

			// Send message
			if (notify) {
				String perc = MessageFormat.format("{0,number,#%}", (Conf.territoryShieldFactor)); // TODO does this display correctly??
				defender.sendMessage("<i>Enemy damage reduced by <rose>%s<i>.", perc);
			}
		} */

		return true;
	}

	@EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
	public void onCreatureSpawn(CreatureSpawnEvent event) {
		if (event.getLocation() == null) return;
		if (LocationUtil.isFactionsDisableIn(event)) return;
		
		if (Config.safeZoneNerfedCreatureTypes.contains(CrossEntityType.of(event.getEntityType().name())) && Board.get().getFactionAt(Locality.of(event.getLocation())).noMonstersInTerritory()) {
			event.setCancelled(true);
		}
	}

	@EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
	public void onEntityTarget(EntityTargetEvent event) {
		if (LocationUtil.isFactionsDisableIn(event)) return;
		
		// if there is a target
		Entity target = event.getTarget();
		if (target == null) return;
		
		EntityType creatureType = MiscUtil.creatureTypeFromEntity(event.getEntity());
		if (creatureType != null) {
			// We are interested in blocking targeting for certain mobs:
			if (!Config.safeZoneNerfedCreatureTypes.contains(CrossEntityType.of(creatureType.name()))) {
				return;
			}
		}
		
		// in case the target is in a safe zone.
		if (Board.get().getFactionAt(Locality.of(target.getLocation())).noMonstersInTerritory()) {
			event.setCancelled(true);
		}
	}

	@EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
	public void onPaintingBreak(HangingBreakEvent event) {
		if (LocationUtil.isFactionsDisableIn(event.getEntity().getLocation())) return;
		
		if (event.getCause() == RemoveCause.EXPLOSION) {
			Location loc = event.getEntity().getLocation();
			Faction faction = Board.get().getFactionAt(Locality.of(loc));
			if (faction.getFlag(Flags.EXPLOSIONS)) {
				// faction is peaceful and has explosions set to disabled
				event.setCancelled(true);
				return;
			}

			boolean online = faction.hasPlayersOnline();

			if ((faction.isWilderness() && !Config.worldsNoWildernessProtection.contains(loc.getWorld().getName()) && (Config.wildernessBlockCreepers || Config.wildernessBlockFireballs || Config.wildernessBlockTNT)) ||
						(faction.isNormal() && (online ? (Config.territoryBlockCreepers || Config.territoryBlockFireballs || Config.territoryBlockTNT) : (Config.territoryBlockCreepersWhenOffline || Config.territoryBlockFireballsWhenOffline || Config.territoryBlockTNTWhenOffline))) ||
						(faction.isWarZone() && (Config.warZoneBlockCreepers || Config.warZoneBlockFireballs || Config.warZoneBlockTNT)) ||
						faction.isSafeZone()) {
				// explosion which needs prevention
				event.setCancelled(true);
			}
		}

		if (!(event instanceof HangingBreakByEntityEvent)) {
			return;
		}

		Entity breaker = ((HangingBreakByEntityEvent) event).getRemover();
		if (!(breaker instanceof Player)) {
			return;
		}

		if (PlayerMixin.canDoAction((Player) breaker, event.getEntity(), LandAction.REMOVE_PAINTING, false)) return;
		
		event.setCancelled(true);
		
	}

	@EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
	public void onPaintingPlace(HangingPlaceEvent event) {
		if (LocationUtil.isFactionsDisableIn(event.getEntity().getLocation())) return;
		
		if (PlayerMixin.canDoAction((Player) event.getPlayer(), event.getEntity(), LandAction.PLACE_PAINTING, false)) return;

		if (PlayerMixin.canDoAction(event.getPlayer(), event.getBlock(), LandAction.PLACE_PAINTING, false)) return;
		
		event.setCancelled(true);
	}

	@EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
	public void onEntityChangeBlock(EntityChangeBlockEvent event) {
		if (LocationUtil.isFactionsDisableIn(event)) return;
		
		Entity entity = event.getEntity();

		// for now, only interested in Enderman and Wither boss tomfoolery
		if (!(entity instanceof Enderman) && !(entity instanceof Wither)) {
			return;
		}

		Location loc = event.getBlock().getLocation();

		if (entity instanceof Enderman) {
			if (stopEndermanBlockManipulation(loc)) {
				event.setCancelled(true);
			}
		} else if (entity instanceof Wither) {
			Faction faction = Board.get().getFactionAt(Locality.of(loc));
			// it's a bit crude just using fireball protection, but I'd rather not add in a whole new set of xxxBlockWitherExplosion or whatever
			if ((faction.isWilderness() && Config.wildernessBlockFireballs && !Config.worldsNoWildernessProtection.contains(loc.getWorld().getName())) ||
						(faction.isNormal() && (faction.hasPlayersOnline() ? Config.territoryBlockFireballs : Config.territoryBlockFireballsWhenOffline)) ||
						(faction.isWarZone() && Config.warZoneBlockFireballs) ||
						faction.isSafeZone()) {
				event.setCancelled(true);
			}
		}
	}

	@EventHandler
	public void onTravel(PlayerPortalEvent event) {
		if (!Config.portalsLimit) {
			return; // Don't do anything if they don't want us to.
		}
		if (LocationUtil.isFactionsDisableIn(event)) return;

		TravelAgent agent = event.getPortalTravelAgent();

		// If they aren't able to find a portal, it'll try to create one.
		if (event.useTravelAgent() && agent.getCanCreatePortal() && agent.findPortal(event.getTo()) == null) {
			Faction faction = Board.get().getFactionAt(Locality.of(event.getTo()));
			if (faction.isWilderness()) {
				return; // We don't care about wilderness.
			} else if (!faction.isNormal() && !event.getPlayer().isOp()) {
				// Don't let non ops make portals in safezone or warzone.
				event.setCancelled(true);
				return;
			}

			FPlayer fp = FPlayerColl.get(event.getPlayer());
			Relation mininumRelation = Relation.fromString(Config.portalsMinimumRelation);
			if (!fp.getFaction().getRelationTo(faction).isAtLeast(mininumRelation)) {
				event.setCancelled(true);
			}
		}
	}

	private boolean stopEndermanBlockManipulation(Location loc) {
		if (loc == null) return false;
		if (LocationUtil.isFactionsDisableIn(loc)) return false;
		
		// quick check to see if all Enderman deny options are enabled; if so, no need to check location
		if (Config.wildernessDenyEndermanBlocks &&
					Config.territoryDenyEndermanBlocks &&
					Config.territoryDenyEndermanBlocksWhenOffline &&
					Config.safeZoneDenyEndermanBlocks &&
					Config.warZoneDenyEndermanBlocks) {
			return true;
		}

		Faction claimFaction = Board.get().getFactionAt(Locality.of(loc));

		if (claimFaction.isWilderness()) {
			return Config.wildernessDenyEndermanBlocks;
		} else if (claimFaction.isNormal()) {
			return claimFaction.hasPlayersOnline() ? Config.territoryDenyEndermanBlocks : Config.territoryDenyEndermanBlocksWhenOffline;
		} else if (claimFaction.isSafeZone()) {
			return Config.safeZoneDenyEndermanBlocks;
		} else if (claimFaction.isWarZone()) {
			return Config.warZoneDenyEndermanBlocks;
		}

		return false;
	}
}
