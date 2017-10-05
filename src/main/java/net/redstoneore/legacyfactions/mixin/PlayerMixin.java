package net.redstoneore.legacyfactions.mixin;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

import net.redstoneore.legacyfactions.FLocation;
import net.redstoneore.legacyfactions.Factions;
import net.redstoneore.legacyfactions.LandAction;
import net.redstoneore.legacyfactions.Permission;
import net.redstoneore.legacyfactions.Relation;
import net.redstoneore.legacyfactions.Role;
import net.redstoneore.legacyfactions.config.Config;
import net.redstoneore.legacyfactions.entity.Board;
import net.redstoneore.legacyfactions.entity.FPlayer;
import net.redstoneore.legacyfactions.entity.FPlayerColl;
import net.redstoneore.legacyfactions.entity.Faction;
import net.redstoneore.legacyfactions.entity.persist.shared.SharedFaction;
import net.redstoneore.legacyfactions.expansion.chat.ChatMode;
import net.redstoneore.legacyfactions.integration.worldguard.WorldGuardEngine;
import net.redstoneore.legacyfactions.integration.worldguard.WorldGuardIntegration;
import net.redstoneore.legacyfactions.lang.Lang;
import net.redstoneore.legacyfactions.locality.Locality;
import net.redstoneore.legacyfactions.util.TextUtil;
import net.redstoneore.legacyfactions.util.VisualizeUtil;
import net.redstoneore.legacyfactions.util.cross.CrossMaterial;

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
		
		if (Config.playersWhoBypassAllProtection.contains(name)) return true;
		FPlayer me = FPlayerColl.get(player);
		if (me.isAdminBypassing()) return true;
		
		FLocation loc = new FLocation(location);
		SharedFaction otherFaction = (SharedFaction) Board.get().getFactionAt(Locality.of(location));

		if (otherFaction.isWilderness()) {
			if (WorldGuardIntegration.get().isEnabled() && Config.worldGuardBuildPriority && WorldGuardEngine.playerCanBuild(player, location)) {
				return true;
			}

			if (!Config.wildernessDenyBuild || Config.worldsNoWildernessProtection.contains(location.getWorld().getName())) {
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
					me.sendMessage(TextUtil.get().parse(message));
				}
			}

			return false;
		} else if (otherFaction.isSafeZone()) {
			if (WorldGuardIntegration.get().isEnabled() && Config.worldGuardBuildPriority && WorldGuardEngine.playerCanBuild(player, location)) {
				return true;
			}

			if (!Config.safeZoneDenyBuild || Permission.MANAGE_SAFE_ZONE.has(player)) {
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
					me.sendMessage(TextUtil.get().parse(message));					
				}
			}

			return false;
		} else if (otherFaction.isWarZone()) {
			if (WorldGuardIntegration.get().isEnabled() && Config.worldGuardBuildPriority && WorldGuardEngine.playerCanBuild(player, location)) {
				return true;
			}

			if (!Config.warZoneDenyBuild || Permission.MANAGE_WAR_ZONE.has(player)) {
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
					me.sendMessage(TextUtil.get().parse(message));
				}
			}

			return false;
		}
		if (Config.raidable && otherFaction.getLandRounded() >= otherFaction.getPowerRounded()) {
			return true;
		}

		Faction myFaction = me.getFaction();
		Relation rel = myFaction.getRelationTo(otherFaction);
		boolean online = otherFaction.hasPlayersOnline();
		boolean pain = !justCheck && rel.confPainBuild(online);
		boolean deny = rel.confDenyBuild(online);

		// hurt the player for building/destroying in other territory?
		if (pain) {
			player.damage(Config.actionDeniedPainAmount);

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
					me.sendMessage(TextUtil.get().parse(message.replace("<name>", otherFaction.getTag(myFaction))));					
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
					me.sendMessage(TextUtil.get().parse(message));					
				}
			}

			return false;
		}

		// Also cancel and/or cause pain if player doesn't have ownership rights for this claim
		if (Config.ownedAreasEnabled && (Config.ownedAreaDenyBuild || Config.ownedAreaPainBuild) && !otherFaction.playerHasOwnershipRights(me, loc)) {
			if (!pain && Config.ownedAreaPainBuild && !justCheck) {
				player.damage(Config.actionDeniedPainAmount);

				if (!Config.ownedAreaDenyBuild) {
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
						me.sendMessage(TextUtil.get().parse(message.replace("<who>", otherFaction.getOwnerListString(loc))));
					}
				}
			}
			if (Config.ownedAreaDenyBuild) {
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
						me.sendMessage(TextUtil.get().parse(message.replace("<who>", otherFaction.getOwnerListString(loc))));
					}
				}

				return false;
			}
		}

		return true;
	}
	
	/**
	 * Is an entity an NPC
	 * @param entity Entity to check
	 * @return true if the entity is an NPC
	 */
	public static boolean isNPC(Entity entity) {
		if (entity.hasMetadata("NPC")) return true;
		
		return false;
	}
	
	/**
	 * Send a block change to a player.
	 * @param player
	 * @param location
	 * @param material
	 * @param data
	 */
	@SuppressWarnings("deprecation")
	public static void sendBlockChange(Player player, Location location, CrossMaterial material, byte data) {
		try {
			player.sendBlockChange(location, material.toBukkitMaterial(), data);
		} catch (Exception e) {
			player.sendBlockChange(location, material.getMaterialId(), data);
		}
	}
	
	/**
	 * Should we let factions handle this chat event. 
	 * @param event Event to validate
	 * @return true if factions should handle this chat.
	 */
	public static boolean shouldLetFactionsHandleThisChat(AsyncPlayerChatEvent event) {
		return event != null && (isPlayerFactionChatting(event.getPlayer()) || isFactionsCommand(event.getPlayer(), event.getMessage()));
	}
	
	/**
	 * Does player have Faction Chat enabled? If so, chat plugins should preferably not do channels,
	 * local chat, or anything else which targets individual recipients, so Faction Chat can be done
	 * @param player
	 * @return
	 */
	public static boolean isPlayerFactionChatting(Player player) {
		FPlayer me = FPlayerColl.get(player);
		return me != null && me.getChatMode() != ChatMode.PUBLIC;
	}
	
	/**
	 * Is this chat message actually a Factions command, and thus should be left alone by other plugins?
	 * @param player
	 * @param check
	 * @return
	 */
	public static boolean isFactionsCommand(Player player, String check) {
		return !(check == null || check.isEmpty()) && Factions.get().handleCommand(player, check, true);
	}

	/**
	 * Get a player's faction tag (faction name), mainly for usage by chat plugins for local/channel chat
	 * @param player
	 * @return
	 */
	public static String getPlayerFactionTag(Player player) {
		return getPlayerFactionTagRelation(player, null);
	}

	/**
	 * Same as {@link #getPlayerFactionTag(Player)}, but with relation (enemy/neutral/ally) colouring potentially added to the tag
	 * @param speaker
	 * @param listener
	 * @return
	 */
	public static String getPlayerFactionTagRelation(Player speaker, Player listener) {
		String tag = "~";
		
		// Invalid speaker, use default tag
		if (speaker == null) return tag;
		
		FPlayer me = FPlayerColl.get(speaker);
		
		// Invalid FPlayer, use default tag
		if (me == null) return tag;
		
		// if listener isn't set, or config option is disabled, give back uncolored tag
		if (listener == null) {
			tag = me.getChatTag().trim();
		} else {
			FPlayer you = FPlayerColl.get(listener);
			if (you == null) {
				tag = me.getChatTag().trim();
			} else {
				tag = me.getChatTag(you).trim();
			}
		}
		
		if (tag.isEmpty()) {
			tag = "~";
		}
		
		return tag;
	}
	
	public static boolean assertHasFaction(FPlayer fplayer) {
		if (!fplayer.hasFaction()) {
			Lang.COMMAND_ERRORS_NOTMEMBER.getBuilder()
				.sendToParsed(fplayer);
			return false;
		}
		return true;
	}

	public static boolean assertMinRole(FPlayer fplayer, Role role, String action) {
		if (fplayer.getRole().isLessThan(role)) {
			Lang.COMMAND_ERRORS_YOUMUSTBE.getBuilder()
				.parse()
				.replace("<thetole>", role.toNiceName())
				.replace("<theaction>", action)
				.sendTo(fplayer);
			return false;
		}
		return true;
	}
	
	@SuppressWarnings("deprecation")
	public static void showPillar(Player player, World world, int blockX, int blockZ) {
		for (int blockY = 0; blockY < player.getLocation().getBlockY() + 30; blockY++) {
			Location loc = new Location(world, blockX, blockY, blockZ);
			if (loc.getBlock().getType() != Material.AIR) {
				continue;
			}
			// TODO: move away from id
			int typeId = blockY % 5 == 0 ? Material.REDSTONE_LAMP_ON.getId() : Material.STAINED_GLASS.getId();
			VisualizeUtil.addLocation(player, loc, typeId);
		}
	}
	
}
