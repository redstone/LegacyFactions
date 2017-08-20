package net.redstoneore.legacyfactions.mixin;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

import net.redstoneore.legacyfactions.FLocation;
import net.redstoneore.legacyfactions.Factions;
import net.redstoneore.legacyfactions.LandAction;
import net.redstoneore.legacyfactions.Lang;
import net.redstoneore.legacyfactions.Permission;
import net.redstoneore.legacyfactions.Relation;
import net.redstoneore.legacyfactions.entity.Board;
import net.redstoneore.legacyfactions.entity.Conf;
import net.redstoneore.legacyfactions.entity.FPlayer;
import net.redstoneore.legacyfactions.entity.FPlayerColl;
import net.redstoneore.legacyfactions.entity.Faction;
import net.redstoneore.legacyfactions.integration.worldguard.WorldGuardEngine;
import net.redstoneore.legacyfactions.integration.worldguard.WorldGuardIntegration;

public class PlayerMixin {

	// -------------------------------------------------- //
	// STATIC FIELDS
	// -------------------------------------------------- //
	
	private static boolean supportsOffHand = true;
	
	// -------------------------------------------------- //
	// STATIC LOGIC
	// -------------------------------------------------- //
	
	static {
		try {
			PlayerInventory.class.getMethod("getItemInOffHand");
			supportsOffHand = true;
		} catch (Exception e) {
			supportsOffHand = false;
		}
	}
	
	// -------------------------------------------------- //
	// METHODS
	// -------------------------------------------------- //
	
	/**
	 * Get item in the main hand of a player
	 * @param player
	 * @return
	 */
	@SuppressWarnings("deprecation")
	public static ItemStack getItemInMainHand(Player player) {
		if (supportsOffHand) {
			return player.getInventory().getItemInMainHand();
		} else {
			// use the older method
			return player.getItemInHand();
		}
	}
	
	/**
	 * Get item in the off hand of a player
	 * @param player
	 * @return air if off hand not supported
	 */
	public static ItemStack getItemInOffHand(Player player) {
		if (supportsOffHand) {
			return player.getInventory().getItemInOffHand();
		} else {
			// Some older versions of minecraft don't use off hand, so return nothing (air) instead
			return new ItemStack(Material.AIR);
		}
	}
	
	/**
	 * Check if a player can do an action at a block.
	 * @param player Player to check 
	 * @param block Location to check
	 * @param action Action they are doing
	 * @param justCheck Set to true to not send messages and not give pain on painbuild.
	 * @return true of they can, false if they can't
	 */
	public static boolean canDoAction(Player player, Block block, LandAction action, boolean justCheck) {
		return canDoAction(player, block.getLocation(), action, justCheck);
	}
	
	/**
	 * Check if a player can do an action at a entities location.
	 * @param player Player to check 
	 * @param entity Location to check
	 * @param action Action they are doing
	 * @param justCheck Set to true to not send messages and not give pain on painbuild.
	 * @return true of they can, false if they can't
	 */
	public static boolean canDoAction(Player player, Entity entity, LandAction action, boolean justCheck) {
		return canDoAction(player, entity.getLocation(), action, justCheck);
	}
	
	/**
	 * Check if a player can do an action at a location.
	 * @param player Player to check 
	 * @param location Location to check
	 * @param action Action they are doing
	 * @param justCheck Set to true to not send messages and not give pain on painbuild.
	 * @return true of they can, false if they can't
	 */
	public static boolean canDoAction(Player player, Location location, LandAction action, boolean justCheck) {
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
					me.sendMessage(Factions.get().getTextUtil().parse(message));
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
					me.sendMessage(Factions.get().getTextUtil().parse(message));					
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
					me.sendMessage(Factions.get().getTextUtil().parse(message));
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
					me.sendMessage(Factions.get().getTextUtil().parse(message.replace("<name>", otherFaction.getTag(myFaction))));					
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
					me.sendMessage(Factions.get().getTextUtil().parse(message));					
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
						me.sendMessage(Factions.get().getTextUtil().parse(message.replace("<who>", otherFaction.getOwnerListString(loc))));
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
						me.sendMessage(Factions.get().getTextUtil().parse(message.replace("<who>", otherFaction.getOwnerListString(loc))));
					}
				}

				return false;
			}
		}

		return true;
	}
	
}
