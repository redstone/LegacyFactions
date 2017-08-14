package net.redstoneore.legacyfactions.listeners;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.*;
import org.bukkit.event.player.PlayerInteractAtEntityEvent;

import net.redstoneore.legacyfactions.*;
import net.redstoneore.legacyfactions.entity.Board;
import net.redstoneore.legacyfactions.entity.Conf;
import net.redstoneore.legacyfactions.entity.FPlayer;
import net.redstoneore.legacyfactions.entity.FPlayerColl;
import net.redstoneore.legacyfactions.entity.Faction;
import net.redstoneore.legacyfactions.integration.worldguard.WorldGuardEngine;
import net.redstoneore.legacyfactions.integration.worldguard.WorldGuardIntegration;
import net.redstoneore.legacyfactions.util.TextUtil;

public class FactionsBlockListener implements Listener {
	
	// -------------------------------------------------- //
	// INSTANCE 
	// -------------------------------------------------- //
	
	private static FactionsBlockListener i = new FactionsBlockListener();
	public static FactionsBlockListener get() { return i; }
	private FactionsBlockListener() { }
	
	// -------------------------------------------------- //
	// CAN BUILD DESTROY BLOCK
	// -------------------------------------------------- //
	
	@EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
	public void playerCanBuildDestroyBlock(BlockPlaceEvent event) {
		if (!event.canBuild()) return;
		
		// special case for flint&steel, which should only be prevented by DenyUsage list
		if (event.getBlockPlaced().getType() == Material.FIRE) return;
		
		playerCanBuildDestroyBlock(event.getPlayer(), event.getBlock().getLocation(), LandAction.BUILD, false, event);
	}
		
	@EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
	public void playerCanBuildDestroyBlock(BlockBreakEvent event) {
		playerCanBuildDestroyBlock(event.getPlayer(), event.getBlock().getLocation(), LandAction.DESTROY, false, event);
	}

	@EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
	public void playerCanBuildDestroyBlock(BlockDamageEvent event) {
		if (!event.getInstaBreak()) return;
		
		playerCanBuildDestroyBlock(event.getPlayer(), event.getBlock().getLocation(), LandAction.DESTROY, false, event);
	}
	
	// -------------------------------------------------- //
	// ARMOR STAND PROTECTION
	// -------------------------------------------------- //
	
	@EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
	public void playerInteractEntity(PlayerInteractAtEntityEvent event) {
		EntityType entityRightClicked = event.getRightClicked().getType();
		
		if (entityRightClicked != EntityType.ARMOR_STAND) return;
		
		playerCanBuildDestroyBlock(event.getPlayer(), event.getRightClicked().getLocation(), LandAction.ENTITY, false, event);
	}
	
	// -------------------------------------------------- //
	// PISTON CAN MOVE BLOCK
	// -------------------------------------------------- //

	@EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
	public void canPistonMoveBlock(BlockPistonExtendEvent event) {
		if (Conf.pistonProtectionThroughDenyBuild) return;

		Faction pistonFaction = Board.get().getFactionAt(new FLocation(event.getBlock()));

		// target end-of-the-line empty (air) block which is being pushed into, including if piston itself would extend into air
		@SuppressWarnings("deprecation")
		Block targetBlock = event.getBlock().getRelative(event.getDirection(), event.getLength() + 1);

		// if potentially pushing into air/water/lava in another territory, we need to check it out
		if ((targetBlock.isEmpty() || targetBlock.isLiquid()) && !canPistonMoveBlock(pistonFaction, targetBlock.getLocation())) {
			event.setCancelled(true);
			return;
		}
	}

	
	@EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
	public void canPistonMoveBlock(BlockPistonRetractEvent event) {
		// if not a sticky piston, retraction should be fine
		if (!event.isSticky() || !Conf.pistonProtectionThroughDenyBuild) {
			return;
		}
		
		@SuppressWarnings("deprecation")
		Location targetLoc = event.getRetractLocation();
		Faction otherFaction = Board.get().getFactionAt(new FLocation(targetLoc));

		// Check if the piston is moving in a faction's territory. This disables pistons entirely in faction territory.
		if (otherFaction.isNormal() && Conf.disablePistonsInTerritory) {
			event.setCancelled(true);
			return;
		}

		// if potentially retracted block is just air/water/lava, no worries
		if (targetLoc.getBlock().isEmpty() || targetLoc.getBlock().isLiquid()) {
			return;
		}

		Faction pistonFaction = Board.get().getFactionAt(new FLocation(event.getBlock()));

		if (!canPistonMoveBlock(pistonFaction, targetLoc)) {
			event.setCancelled(true);
			return;
		}
	}
		
	public static boolean canPistonMoveBlock(Faction factionAtPiston, Location target) {
		Faction otherFaction = Board.get().getFactionAt(new FLocation(target));

		if (factionAtPiston == otherFaction) return true;

		if (otherFaction.isWilderness()) {
			if (!Conf.wildernessDenyBuild || Conf.worldsNoWildernessProtection.contains(target.getWorld().getName())) {
				return true;
			}
			return false;
		} else if (otherFaction.isSafeZone()) {
			if (!Conf.safeZoneDenyBuild) {
				return true;
			}
			return false;
		} else if (otherFaction.isWarZone()) {
			if (!Conf.warZoneDenyBuild) {
				return true;
			}
			return false;
		}

		Relation relation = factionAtPiston.getRelationTo(otherFaction);

		if (relation.confDenyBuild(otherFaction.hasPlayersOnline())) return false;

		return true;
	}
	
	// -------------------------------------------------- //
	// FORSTWALK
	// -------------------------------------------------- //
	
	@EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
	public void playerCanFrostWalk(EntityBlockFormEvent event) {
		if (event.getEntity() == null || event.getEntity().getType() != EntityType.PLAYER || event.getBlock() == null) {
			return;
		}

		Player player = (Player) event.getEntity();
		Location location = event.getBlock().getLocation();

		// only notify every 10 seconds
		FPlayer fPlayer = FPlayerColl.get(player);
		boolean justCheck = fPlayer.getLastFrostwalkerMessage() + 10000 > System.currentTimeMillis();
		if (!justCheck) {
			fPlayer.setLastFrostwalkerMessage();
		}

		// Check if they have build permissions here. If not, block this from happening.
		if (!playerCanBuildDestroyBlock(player, location, LandAction.FROST_WALK, justCheck)) {
			event.setCancelled(true);
		}
	}

	// -------------------------------------------------- //
	// METHODS
	// -------------------------------------------------- //
	
	/**
	 * Returns true if a player can destroy a block at the location.
	 * @param player the player to check
	 * @param location the location to check on
	 * @param action the action they are doing 
	 * @param justCheck true if you don't want to send a message
	 * @param event to cancel
	 * @return true if a player can destroy a block at the location.
	 */
	public static boolean playerCanBuildDestroyBlock(Player player, Location location, LandAction action, boolean justCheck, Cancellable event) {
		Boolean result = playerCanBuildDestroyBlock(player, location, action, justCheck);
			
		if (!result) event.setCancelled(true);
		
		return result;
	}
	
	public static boolean playerCanBuildDestroyBlock(Player player, Location location, LandAction action, boolean justCheck) {
		String name = player.getName();
		
		if (Conf.playersWhoBypassAllProtection.contains(name)) return true;
		

		FPlayer me = FPlayerColl.get(player);
		if (me.isAdminBypassing()) return true;
		
		FLocation loc = new FLocation(location);
		Faction otherFaction = Board.get().getFactionAt(loc);

		if (otherFaction.isWilderness()) {
			if (WorldGuardIntegration.get().isEnabled() && Conf.worldGuardBuildPriority && WorldGuardEngine.playerCanBuild(player, location)) {
				return true;
			}

			if (!Conf.wildernessDenyBuild || Conf.worldsNoWildernessProtection.contains(location.getWorld().getName())) {
				return true; // This is not faction territory. Use whatever you like here.
			}

			if (!justCheck) {
				String message = null;
				
				switch (action) {
				case BUILD:
					message = Lang.PLAYER_CANT_WILDERNESS_BUILD.toString();
					break;
				case DESTROY:
					message = Lang.PLAYER_CANT_WILDERNESS_DESTROY.toString();
					break;
				case ENTITY:
					message = Lang.PLAYER_CANT_WILDERNESS_USE.toString();
					break;
				case FROST_WALK:
					message = Lang.PLAYER_CANT_WILDERNESS_FROSTWALK.toString();
					break;
				case PLACE_PAINTING:
					message = Lang.PLAYER_CANT_WILDERNESS_PLACEPAINTING.toString();
					break;
				case REMOVE_PAINTING:
					message = Lang.PLAYER_CANT_WILDERNESS_BREAKPAINTING.toString();
					break;
				default:
					return false;
				}
				
				if (message != null) {
					me.sendMessage(TextUtil.parseColor(message));
				}
			}

			return false;
		} else if (otherFaction.isSafeZone()) {
			if (WorldGuardIntegration.get().isEnabled() && Conf.worldGuardBuildPriority && WorldGuardEngine.playerCanBuild(player, location)) {
				return true;
			}

			if (!Conf.safeZoneDenyBuild || Permission.MANAGE_SAFE_ZONE.has(player)) {
				return true;
			}

			if (!justCheck) {
				String message = null;
				switch (action) {
				case BUILD:
					message = Lang.PLAYER_CANT_SAFEZONE_BUILD.toString();
					break;
				case DESTROY:
					message = Lang.PLAYER_CANT_SAFEZONE_DESTROY.toString();
					break;
				case ENTITY:
					message = Lang.PLAYER_CANT_SAFEZONE_USE.toString();
					break;
				case FROST_WALK:
					message = Lang.PLAYER_CANT_SAFEZONE_FROSTWALK.toString();
					break;
				case PLACE_PAINTING:
					message = Lang.PLAYER_CANT_SAFEZONE_PLACEPAINTING.toString();
					break;
				case REMOVE_PAINTING:
					message = Lang.PLAYER_CANT_SAFEZONE_BREAKPAINTING.toString();
					break;
				default:
					break;
				}
				
				if (message != null) {
					me.sendMessage(TextUtil.parseColor(message));					
				}
			}

			return false;
		} else if (otherFaction.isWarZone()) {
			if (WorldGuardIntegration.get().isEnabled() && Conf.worldGuardBuildPriority && WorldGuardEngine.playerCanBuild(player, location)) {
				return true;
			}

			if (!Conf.warZoneDenyBuild || Permission.MANAGE_WAR_ZONE.has(player)) {
				return true;
			}

			if (!justCheck) {
				String message = null;
				switch (action) {
				case BUILD:
					message = Lang.PLAYER_CANT_WARZONE_BUILD.toString();
					break;
				case DESTROY:
					message = Lang.PLAYER_CANT_WARZONE_DESTROY.toString();
					break;
				case ENTITY:
					message = Lang.PLAYER_CANT_WARZONE_USE.toString();
					break;
				case FROST_WALK:
					message = Lang.PLAYER_CANT_WARZONE_FROSTWALK.toString();
					break;
				case PLACE_PAINTING:
					message = Lang.PLAYER_CANT_WARZONE_PLACEPAINTING.toString();
					break;
				case REMOVE_PAINTING:
					message = Lang.PLAYER_CANT_WARZONE_BREAKPAINTING.toString();
					break;
				default:
					break;
				}
				
				if (message != null) {
					me.sendMessage(TextUtil.parseColor(message));
				}
			}

			return false;
		}
		if (Conf.raidable && otherFaction.getLandRounded() >= otherFaction.getPowerRounded()) {
			return true;
		}

		Faction myFaction = me.getFaction();
		Relation rel = myFaction.getRelationTo(otherFaction);
		boolean online = otherFaction.hasPlayersOnline();
		boolean pain = !justCheck && rel.confPainBuild(online);
		boolean deny = rel.confDenyBuild(online);

		// hurt the player for building/destroying in other territory?
		if (pain) {
			player.damage(Conf.actionDeniedPainAmount);

			if (!deny) {
				String message = null;
				
				switch(action) {
				case BUILD:
					message = Lang.PLAYER_PAINFUL_FACTION_BUILD.toString();
					break;
				case DESTROY:
					message = Lang.PLAYER_PAINFUL_FACTION_DESTROY.toString();
					break;
				case ENTITY:
					message = Lang.PLAYER_PAINFUL_FACTION_USE.toString();
					break;
				case FROST_WALK:
					message = Lang.PLAYER_PAINFUL_FACTION_FROSTWALK.toString();
					break;
				case PLACE_PAINTING:
					message = Lang.PLAYER_PAINFUL_FACTION_PLACEPAINTING.toString();
					break;
				case REMOVE_PAINTING:
					message = Lang.PLAYER_PAINFUL_FACTION_BREAKPAINTING.toString();
					break;
				default:
					break;
				}
				
				if (message != null) {
					me.sendMessage(TextUtil.parseColor(message.replace("<name>", otherFaction.getTag(myFaction))));					
				}
			}
		}

		// cancel building/destroying in other territory?
		if (deny) {
			if (!justCheck) {
				String message = null;
				
				switch (action) {
				case BUILD:
					message = Lang.PLAYER_CANT_FACTION_BUILD.toString();
					break;
				case DESTROY:
					message = Lang.PLAYER_CANT_FACTION_DESTROY.toString();
					break;
				case ENTITY:
					message = Lang.PLAYER_CANT_FACTION_USE.toString();
					break;
				case FROST_WALK:
					message = Lang.PLAYER_CANT_FACTION_FROSTWALK.toString();
					break;	
				case PLACE_PAINTING:
					message = Lang.PLAYER_CANT_FACTION_PLACEPAINTING.toString();
					break;
				case REMOVE_PAINTING:
					message = Lang.PLAYER_CANT_FACTION_BREAKPAINTING.toString();
					break;
				default:
					break;
				}
				
				if (message != null) {
					message = message.replace("<name>", otherFaction.getTag(myFaction));
					me.sendMessage(TextUtil.parseColor(message));					
				}
			}

			return false;
		}

		// Also cancel and/or cause pain if player doesn't have ownership rights for this claim
		if (Conf.ownedAreasEnabled && (Conf.ownedAreaDenyBuild || Conf.ownedAreaPainBuild) && !otherFaction.playerHasOwnershipRights(me, loc)) {
			if (!pain && Conf.ownedAreaPainBuild && !justCheck) {
				player.damage(Conf.actionDeniedPainAmount);

				if (!Conf.ownedAreaDenyBuild) {
					String message = null;
					switch(action) {
					case BUILD:
						message = Lang.PLAYER_PAINFUL_OWNED_BUILD.toString();
						break;
					case DESTROY:
						message = Lang.PLAYER_PAINFUL_OWNED_DESTROY.toString();
						break;
					case ENTITY:
						message = Lang.PLAYER_PAINFUL_OWNED_USE.toString();
						break;
					case FROST_WALK:
						message = Lang.PLAYER_PAINFUL_OWNED_FROSTWALK.toString();
						break;
					case PLACE_PAINTING:
						message = Lang.PLAYER_PAINFUL_OWNED_PLACEPAINTING.toString();
						break;
					case REMOVE_PAINTING:
						message = Lang.PLAYER_PAINFUL_OWNED_BREAKPAINTING.toString();
						break;
					default:
						break;
					}
					
					if (message != null) {
						me.sendMessage(TextUtil.parseColor(message.replace("<who>", otherFaction.getOwnerListString(loc))));
					}
				}
			}
			if (Conf.ownedAreaDenyBuild) {
				if (!justCheck) {
					String message = null;
					switch (action) {
					case BUILD:
						message = Lang.PLAYER_CANT_OWNED_BUILD.toString();
						break;
					case DESTROY:
						message = Lang.PLAYER_CANT_OWNED_DESTROY.toString();
						break;
					case ENTITY:
						message = Lang.PLAYER_CANT_OWNED_USE.toString();
						break;
					case FROST_WALK:
						message = Lang.PLAYER_CANT_OWNED_FROSTWALK.toString();
						break;
					case PLACE_PAINTING:
						message = Lang.PLAYER_CANT_OWNED_PLACEPAINTING.toString();
						break;
					case REMOVE_PAINTING:
						message = Lang.PLAYER_CANT_OWNED_BREAKPAINTING.toString();
						break;
					default:
						break;
					}
					
					if (message != null) {
						me.sendMessage(TextUtil.parseColor(message.replace("<who>", otherFaction.getOwnerListString(loc))));
					}
				}

				return false;
			}
		}

		return true;
	}
	
}
