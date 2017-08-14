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

import net.redstoneore.legacyfactions.*;
import net.redstoneore.legacyfactions.entity.Board;
import net.redstoneore.legacyfactions.entity.Conf;
import net.redstoneore.legacyfactions.entity.FPlayer;
import net.redstoneore.legacyfactions.entity.FPlayerColl;
import net.redstoneore.legacyfactions.entity.Faction;
import net.redstoneore.legacyfactions.event.EventFactionsPowerLoss;
import net.redstoneore.legacyfactions.util.MiscUtil;

import java.util.*;


public class FactionsEntityListener implements Listener {

	// ----------------------------------------
	// INSTANCE
	// ----------------------------------------

	private static FactionsEntityListener i = new FactionsEntityListener();
	public static FactionsEntityListener get() { return i; }
	
    @EventHandler(priority = EventPriority.NORMAL)
    public void onEntityDeath(EntityDeathEvent event) {
        Entity entity = event.getEntity();
        if (!(entity instanceof Player)) {
            return;
        }

        Player player = (Player) entity;
        FPlayer fplayer = FPlayerColl.get(player);
        Faction faction = Board.get().getFactionAt(new FLocation(player.getLocation()));

        EventFactionsPowerLoss powerLossEvent = new EventFactionsPowerLoss(faction, fplayer, Conf.powerPerDeath);
        // Check for no power loss conditions
        if (faction.isWarZone()) {
            // war zones always override worldsNoPowerLoss either way, thus this layout
            if (!Conf.warZonePowerLoss) {
                powerLossEvent.setMessage(Lang.PLAYER_POWER_NOLOSS_WARZONE.toString());
                powerLossEvent.setCancelled(true);
            }
            if (Conf.worldsNoPowerLoss.contains(player.getWorld().getName())) {
                powerLossEvent.setMessage(Lang.PLAYER_POWER_LOSS_WARZONE.toString());
            }
        } else if (faction.isWilderness() && !Conf.wildernessPowerLoss && !Conf.worldsNoWildernessProtection.contains(player.getWorld().getName())) {
            powerLossEvent.setMessage(Lang.PLAYER_POWER_NOLOSS_WILDERNESS.toString());
            powerLossEvent.setCancelled(true);
        } else if (Conf.worldsNoPowerLoss.contains(player.getWorld().getName())) {
            powerLossEvent.setMessage(Lang.PLAYER_POWER_NOLOSS_WORLD.toString());
            powerLossEvent.setCancelled(true);
        } else if (Conf.peacefulMembersDisablePowerLoss && fplayer.hasFaction() && fplayer.getFaction().isPeaceful()) {
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
        } else if (Conf.safeZonePreventAllDamageToPlayers && isPlayerInSafeZone(event.getEntity())) {
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
        if (player == null) {
            return;
        }
        UUID uuid = player.getUniqueId();
        if (Factions.get().getStuckMap().containsKey(uuid)) {
            FPlayerColl.get(player).sendMessage(Lang.COMMAND_STUCK_CANCELLED);
            Factions.get().getStuckMap().remove(uuid);
        }
    }

    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    public void blockExplosion(EntityExplodeEvent event) {
        Location loc = event.getLocation();
        Entity entity = event.getEntity();
        
        Faction faction = Board.get().getFactionAt(FLocation.valueOf(loc));
        
        boolean online = faction.hasPlayersOnline();

        if (faction.noExplosionsInTerritory() || (faction.isPeaceful() && Conf.peacefulTerritoryDisableBoom)) {
        	event.setCancelled(true);
        	return;
        }

        if (entity instanceof Creeper && faction.noCreeperExplosions(loc)) {
        	event.setCancelled(true);
        	return;
        } 
        
        if ((entity instanceof Fireball || entity instanceof WitherSkull || entity instanceof Wither) &&
        	((faction.isWilderness() && Conf.wildernessBlockFireballs && !Conf.worldsNoWildernessProtection.contains(loc.getWorld().getName()))
        	  || (faction.isNormal() && (online ? Conf.territoryBlockFireballs : Conf.territoryBlockFireballsWhenOffline)) 
        	  || (faction.isWarZone() && Conf.warZoneBlockFireballs) 
        	  || faction.isSafeZone())) {
           
        	event.setCancelled(true);
        	return;
        }
        
        if ((entity instanceof TNTPrimed || entity instanceof ExplosiveMinecart) &&
        	((faction.isWilderness() && Conf.wildernessBlockTNT && !Conf.worldsNoWildernessProtection.contains(loc.getWorld().getName()))
        	 || (faction.isNormal() && (online ? Conf.territoryBlockTNT : Conf.territoryBlockTNTWhenOffline)) 
        	 || (faction.isWarZone() && Conf.warZoneBlockTNT)
        	 || (faction.isSafeZone() && Conf.safeZoneBlockTNT))) {
            // TNT which needs prevention
        	event.setCancelled(true);
        	return;
        }
        if ( 
        		(entity instanceof TNTPrimed || entity instanceof ExplosiveMinecart) && Conf.handleExploitTNTWaterlog) {
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
        EntityDamageByEntityEvent sub = new EntityDamageByEntityEvent(event.getCombuster(), event.getEntity(), EntityDamageEvent.DamageCause.FIRE, 0d);
        if (!this.canDamagerHurtDamagee(sub, false)) {
            event.setCancelled(true);
        }
        sub = null;
    }

    private static final Set<PotionEffectType> badPotionEffects = new LinkedHashSet<PotionEffectType>(Arrays.asList(PotionEffectType.BLINDNESS, PotionEffectType.CONFUSION, PotionEffectType.HARM, PotionEffectType.HUNGER, PotionEffectType.POISON, PotionEffectType.SLOW, PotionEffectType.SLOW_DIGGING, PotionEffectType.WEAKNESS, PotionEffectType.WITHER));

    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    public void onPotionSplashEvent(PotionSplashEvent event) {
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
            if (badjuju && fPlayer.getFaction().isPeaceful()) {
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
        if (Board.get().getFactionAt(new FLocation(damagee.getLocation())).isSafeZone()) {
            return true;
        }
        return false;
    }

    public boolean canDamagerHurtDamagee(EntityDamageByEntityEvent sub) {
        return canDamagerHurtDamagee(sub, true);
    }

    public boolean canDamagerHurtDamagee(EntityDamageByEntityEvent sub, boolean notify) {
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
        Faction defLocFaction = Board.get().getFactionAt(new FLocation(defenderLoc));

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

        if (Conf.playersWhoBypassAllProtection.contains(attacker.getName())) {
            return true;
        }

        if (attacker.hasLoginPvpDisabled()) {
            if (notify) {
                attacker.sendMessage(Lang.PLAYER_PVP_LOGIN, Conf.noPVPDamageToOthersForXSecondsAfterLogin);
            }
            return false;
        }

        Faction locFaction = Board.get().getFactionAt(new FLocation(attacker));

        // so we know from above that the defender isn't in a safezone... what about the attacker, sneaky dog that he might be?
        if (locFaction.noPvPInTerritory()) {
            if (notify) {
                attacker.sendMessage(Lang.PLAYER_CANTHURT, (locFaction.isSafeZone() ? Lang.REGION_SAFEZONE.toString() : Lang.REGION_PEACEFUL.toString()));
            }
            return false;
        }

        if (locFaction.isWarZone() && Conf.warZoneFriendlyFire) {
            return true;
        }

        if (Conf.worldsIgnorePvP.contains(defenderLoc.getWorld().getName())) {
            return true;
        }

        Faction defendFaction = defender.getFaction();
        Faction attackFaction = attacker.getFaction();

        if (attackFaction.isWilderness() && Conf.disablePVPForFactionlessPlayers) {
            if (notify) {
                attacker.sendMessage(Lang.PLAYER_PVP_REQUIREFACTION);
            }
            return false;
        } else if (defendFaction.isWilderness()) {
            if (defLocFaction == attackFaction && Conf.enablePVPAgainstFactionlessInAttackersLand) {
                // Allow PVP vs. Factionless in attacker's faction territory
                return true;
            } else if (Conf.disablePVPForFactionlessPlayers) {
                if (notify) {
                    attacker.sendMessage(Lang.PLAYER_PVP_FACTIONLESS);
                }
                return false;
            }
        }

        if (defendFaction.isPeaceful()) {
            if (notify) {
                attacker.sendMessage(Lang.PLAYER_PVP_PEACEFUL);
            }
            return false;
        } else if (attackFaction.isPeaceful()) {
            if (notify) {
                attacker.sendMessage(Lang.PLAYER_PVP_PEACEFUL);
            }
            return false;
        }

        Relation relation = defendFaction.getRelationTo(attackFaction);

        // You can not hurt neutral factions
        if (Conf.disablePVPBetweenNeutralFactions && relation.isNeutral()) {
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
        if (event.getLocation() == null) {
            return;
        }

        if (Conf.safeZoneNerfedCreatureTypes.contains(event.getEntityType()) && Board.get().getFactionAt(new FLocation(event.getLocation())).noMonstersInTerritory()) {
            event.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    public void onEntityTarget(EntityTargetEvent event) {
        // if there is a target
        Entity target = event.getTarget();
        if (target == null) {
            return;
        }

        // We are interested in blocking targeting for certain mobs:
        if (!Conf.safeZoneNerfedCreatureTypes.contains(MiscUtil.creatureTypeFromEntity(event.getEntity()))) {
            return;
        }

        // in case the target is in a safe zone.
        if (Board.get().getFactionAt(new FLocation(target.getLocation())).noMonstersInTerritory()) {
            event.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    public void onPaintingBreak(HangingBreakEvent event) {
        if (event.getCause() == RemoveCause.EXPLOSION) {
            Location loc = event.getEntity().getLocation();
            Faction faction = Board.get().getFactionAt(new FLocation(loc));
            if (faction.noExplosionsInTerritory()) {
                // faction is peaceful and has explosions set to disabled
                event.setCancelled(true);
                return;
            }

            boolean online = faction.hasPlayersOnline();

            if ((faction.isWilderness() && !Conf.worldsNoWildernessProtection.contains(loc.getWorld().getName()) && (Conf.wildernessBlockCreepers || Conf.wildernessBlockFireballs || Conf.wildernessBlockTNT)) ||
                        (faction.isNormal() && (online ? (Conf.territoryBlockCreepers || Conf.territoryBlockFireballs || Conf.territoryBlockTNT) : (Conf.territoryBlockCreepersWhenOffline || Conf.territoryBlockFireballsWhenOffline || Conf.territoryBlockTNTWhenOffline))) ||
                        (faction.isWarZone() && (Conf.warZoneBlockCreepers || Conf.warZoneBlockFireballs || Conf.warZoneBlockTNT)) ||
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

        if (!FactionsBlockListener.playerCanBuildDestroyBlock((Player) breaker, event.getEntity().getLocation(), LandAction.REMOVE_PAINTING, false)) {
            event.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    public void onPaintingPlace(HangingPlaceEvent event) {
        if (!FactionsBlockListener.playerCanBuildDestroyBlock(event.getPlayer(), event.getBlock().getLocation(), LandAction.PLACE_PAINTING, false)) {
            event.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    public void onEntityChangeBlock(EntityChangeBlockEvent event) {
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
            Faction faction = Board.get().getFactionAt(new FLocation(loc));
            // it's a bit crude just using fireball protection, but I'd rather not add in a whole new set of xxxBlockWitherExplosion or whatever
            if ((faction.isWilderness() && Conf.wildernessBlockFireballs && !Conf.worldsNoWildernessProtection.contains(loc.getWorld().getName())) ||
                        (faction.isNormal() && (faction.hasPlayersOnline() ? Conf.territoryBlockFireballs : Conf.territoryBlockFireballsWhenOffline)) ||
                        (faction.isWarZone() && Conf.warZoneBlockFireballs) ||
                        faction.isSafeZone()) {
                event.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onTravel(PlayerPortalEvent event) {
        if (!Conf.portalsLimit) {
            return; // Don't do anything if they don't want us to.
        }

        TravelAgent agent = event.getPortalTravelAgent();

        // If they aren't able to find a portal, it'll try to create one.
        if (event.useTravelAgent() && agent.getCanCreatePortal() && agent.findPortal(event.getTo()) == null) {
            FLocation loc = new FLocation(event.getTo());
            Faction faction = Board.get().getFactionAt(loc);
            if (faction.isWilderness()) {
                return; // We don't care about wilderness.
            } else if (!faction.isNormal() && !event.getPlayer().isOp()) {
                // Don't let non ops make portals in safezone or warzone.
                event.setCancelled(true);
                return;
            }

            FPlayer fp = FPlayerColl.get(event.getPlayer());
            Relation mininumRelation = Relation.fromString(Conf.portalsMinimumRelation);
            if (!fp.getFaction().getRelationTo(faction).isAtLeast(mininumRelation)) {
                event.setCancelled(true);
            }
        }
    }

    private boolean stopEndermanBlockManipulation(Location loc) {
        if (loc == null) {
            return false;
        }
        // quick check to see if all Enderman deny options are enabled; if so, no need to check location
        if (Conf.wildernessDenyEndermanBlocks &&
                    Conf.territoryDenyEndermanBlocks &&
                    Conf.territoryDenyEndermanBlocksWhenOffline &&
                    Conf.safeZoneDenyEndermanBlocks &&
                    Conf.warZoneDenyEndermanBlocks) {
            return true;
        }

        FLocation fLoc = new FLocation(loc);
        Faction claimFaction = Board.get().getFactionAt(fLoc);

        if (claimFaction.isWilderness()) {
            return Conf.wildernessDenyEndermanBlocks;
        } else if (claimFaction.isNormal()) {
            return claimFaction.hasPlayersOnline() ? Conf.territoryDenyEndermanBlocks : Conf.territoryDenyEndermanBlocksWhenOffline;
        } else if (claimFaction.isSafeZone()) {
            return Conf.safeZoneDenyEndermanBlocks;
        } else if (claimFaction.isWarZone()) {
            return Conf.warZoneDenyEndermanBlocks;
        }

        return false;
    }
}
