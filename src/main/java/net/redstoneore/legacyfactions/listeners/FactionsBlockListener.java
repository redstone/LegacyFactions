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

import net.redstoneore.legacyfactions.LandAction;
import net.redstoneore.legacyfactions.Relation;
import net.redstoneore.legacyfactions.config.Config;
import net.redstoneore.legacyfactions.entity.Board;
import net.redstoneore.legacyfactions.entity.FPlayer;
import net.redstoneore.legacyfactions.entity.FPlayerColl;
import net.redstoneore.legacyfactions.entity.Faction;
import net.redstoneore.legacyfactions.locality.Locality;
import net.redstoneore.legacyfactions.mixin.PlayerMixin;
import net.redstoneore.legacyfactions.util.LocationUtil;

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
		if (LocationUtil.isFactionsDisableIn(event)) return;
		
		if (!event.canBuild()) return;
		
		// special case for flint&steel, which should only be prevented by DenyUsage list
		if (event.getBlockPlaced().getType() == Material.FIRE) return;
		
		if (PlayerMixin.canDoAction(event.getPlayer(), event.getBlock(), LandAction.BUILD, false)) return;
		
		event.setCancelled(true);
	}
		
	@EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
	public void playerCanBuildDestroyBlock(BlockBreakEvent event) {
		if (LocationUtil.isFactionsDisableIn(event)) return;
		
		if (PlayerMixin.canDoAction(event.getPlayer(), event.getBlock(), LandAction.DESTROY, false)) return;
		
		event.setCancelled(true);
	}

	@EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
	public void playerCanBuildDestroyBlock(BlockDamageEvent event) {
		if (LocationUtil.isFactionsDisableIn(event)) return;
		
		if (!event.getInstaBreak()) return;
		if (PlayerMixin.canDoAction(event.getPlayer(), event.getBlock(), LandAction.DESTROY, false)) return;
		
		event.setCancelled(true);;
	}
	
	// -------------------------------------------------- //
	// PISTON CAN MOVE BLOCK
	// -------------------------------------------------- //

	@EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
	public void canPistonMoveBlock(BlockPistonExtendEvent event) {
		if (LocationUtil.isFactionsDisableIn(event)) return;
		
		if (Config.pistonProtectionThroughDenyBuild) return;

		Faction pistonFaction = Board.get().getFactionAt(Locality.of(event.getBlock()));

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
		if (LocationUtil.isFactionsDisableIn(event)) return;
		
		// if not a sticky piston, retraction should be fine
		if (!event.isSticky() || !Config.pistonProtectionThroughDenyBuild) {
			return;
		}
		
		@SuppressWarnings("deprecation")
		Location targetLoc = event.getRetractLocation();
		Faction otherFaction = Board.get().getFactionAt(Locality.of(targetLoc));

		// Check if the piston is moving in a faction's territory. This disables pistons entirely in faction territory.
		if (otherFaction.isNormal() && Config.disablePistonsInTerritory) {
			event.setCancelled(true);
			return;
		}

		// if potentially retracted block is just air/water/lava, no worries
		if (targetLoc.getBlock().isEmpty() || targetLoc.getBlock().isLiquid()) {
			return;
		}

		Faction pistonFaction = Board.get().getFactionAt(Locality.of(event.getBlock()));
		
		if (!canPistonMoveBlock(pistonFaction, targetLoc)) {
			event.setCancelled(true);
			return;
		}
	}
		
	public static boolean canPistonMoveBlock(Faction factionAtPiston, Location target) {
		if (LocationUtil.isFactionsDisableIn(target.getWorld())) return true;
		
		Faction otherFaction = Board.get().getFactionAt(Locality.of(target));

		if (factionAtPiston == otherFaction) return true;

		if (otherFaction.isWilderness()) {
			if (!Config.wildernessDenyBuild || Config.worldsNoWildernessProtection.contains(target.getWorld().getName())) {
				return true;
			}
			return false;
		} else if (otherFaction.isSafeZone()) {
			if (!Config.safeZoneDenyBuild) {
				return true;
			}
			return false;
		} else if (otherFaction.isWarZone()) {
			if (!Config.warZoneDenyBuild) {
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
		
		if (LocationUtil.isFactionsDisableIn(event)) return;
		
		Player player = (Player) event.getEntity();
		Location location = event.getBlock().getLocation();

		// only notify every 10 seconds
		FPlayer fPlayer = FPlayerColl.get(player);
		boolean notify = fPlayer.getLastFrostwalkerMessage() + 10000 > System.currentTimeMillis();
		if (!notify) {
			fPlayer.setLastFrostwalkerMessage();
		}

		// Check if they have build permissions here. 
		if (PlayerMixin.canDoAction(player, location, LandAction.FROST_WALK, notify)) return;
		
		// They can't, block this from happening. 
		event.setCancelled(true);
		
	}

	// -------------------------------------------------- //
	// DEPRECATED METHODS
	// -------------------------------------------------- //
	
	/**
	 * Deprecated. Use {@link PlayerMixin#canDoAction(Player, Location, LandAction, boolean)}<br><br>
	 * Returns true if a player can destroy a block at the location.
	 * @param player the player to check
	 * @param location the location to check on
	 * @param action the action they are doing 
	 * @param justCheck true if you don't want to send a message
	 * @param event to cancel
	 * @return true if a player can destroy a block at the location.
	 */
	@Deprecated
	public static boolean playerCanBuildDestroyBlock(Player player, Location location, LandAction action, boolean justCheck, Cancellable event) {
		Boolean result = PlayerMixin.canDoAction(player, location, action, justCheck);
			
		if (!result) event.setCancelled(true);
		
		return result;
	}
	
	/**
	 * Deprecated. Use {@link PlayerMixin#canDoAction(Player, Location, LandAction, boolean)}<br><br>
	 */
	@Deprecated
	public static boolean playerCanBuildDestroyBlock(Player player, Location location, LandAction action, boolean justCheck) {
		return PlayerMixin.canDoAction(player, location, action, justCheck);
	}
	
	/**
	 * Deprecated. Use {@link PlayerMixin#canDoAction(Player, Location, LandAction, boolean)}<br><br>
	 */
	@Deprecated
	public static boolean playerCanBuildDestroyBlock(Player player, Location location, String action, boolean justCheck, Cancellable event) {
		LandAction landAction = stringToLandAction(action);
		
		return playerCanBuildDestroyBlock(player, location, landAction, justCheck, event);
	}

	/**
	 * Deprecated. Use {@link PlayerMixin#canDoAction(Player, Location, LandAction, boolean)}<br><br>
	 * @param player
	 * @param location
	 * @param action
	 * @param justCheck
	 * @return
	 */
	@Deprecated
	public static boolean playerCanBuildDestroyBlock(Player player, Location location, String action, boolean justCheck) {
		LandAction landAction = stringToLandAction(action);

		return playerCanBuildDestroyBlock(player, location, landAction, justCheck);

	}	
	
	/**
	 * Useful utility to convert the old string names to the new LandAction enum.
	 * @param action Action to convert.
	 * @return {@link LandAction}
	 */
	public static LandAction stringToLandAction(String action) {
		switch (action) {
		case "frost walk":
			return LandAction.FROST_WALK;
		case "remove paintings":
			return LandAction.REMOVE_PAINTING;
		case "place paintings":
			return LandAction.PLACE_PAINTING;
		case "build":
			return LandAction.BUILD;
		case "destory":
			return LandAction.DESTROY;
		}
		return null;
	}

}
