package net.redstoneore.legacyfactions.util;

import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

import net.redstoneore.legacyfactions.entity.FPlayer;
import net.redstoneore.legacyfactions.mixin.PlayerMixin;
import net.redstoneore.legacyfactions.util.cross.CrossMaterial;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class VisualizeUtil {
	
	// -------------------------------------------------- //
	// STATIC FIELDS
	// -------------------------------------------------- //
	
	protected static Map<String, Set<Location>> playerLocations = new ConcurrentHashMap<>();

	// -------------------------------------------------- //
	// STATIC METHODS
	// -------------------------------------------------- //
	
	public static Set<Location> getPlayerLocations(FPlayer player) {
		return getPlayerLocations(player.getId());
	}
	
	public static Set<Location> getPlayerLocations(Player player) {
		return getPlayerLocations(player.getUniqueId().toString());
	}

	public static Set<Location> getPlayerLocations(String uuid) {
		Set<Location> locations = playerLocations.get(uuid);
		if (locations == null) {
			locations = Collections.newSetFromMap(new ConcurrentHashMap<Location, Boolean>());
			playerLocations.put(uuid, locations);
		}
		return locations;
	}

	public static void addLocation(Player player, Location location, int typeId, byte data) {
		getPlayerLocations(player).add(location);
		PlayerMixin.sendBlockChange(player, location, CrossMaterial.get(typeId).get(), data);
	}
	
	public static void addLocation(Player player, Location location, int typeId) {
		getPlayerLocations(player).add(location);
		PlayerMixin.sendBlockChange(player, location, CrossMaterial.get(typeId).get(), (byte) 0);
	}
	
	public static void addLocations(Player player, Map<Location, Integer> locationMaterialIds) {
		Set<Location> ploc = getPlayerLocations(player);
		
		locationMaterialIds.entrySet().forEach(entry -> {
			ploc.add(entry.getKey());
			
			PlayerMixin.sendBlockChange(player, entry.getKey(), CrossMaterial.get(entry.getValue()).get(), (byte) 0);
		});
	}
	
	public static void addLocations(Player player, Collection<Location> locationsToAdd, int typeId) {
		Set<Location> locations = getPlayerLocations(player);
		locationsToAdd.forEach(location -> {
			locations.add(location);
			
			PlayerMixin.sendBlockChange(player, location, CrossMaterial.get(typeId).get(), (byte) 0);
		});
	}
	
	public static void addBlocks(Player player, Collection<Block> blocks, int typeId) {
		Set<Location> locations = getPlayerLocations(player);
		blocks.forEach(block -> {
			Location location = block.getLocation();
			locations.add(location);
			
			PlayerMixin.sendBlockChange(player, location, CrossMaterial.get(typeId).get(), (byte) 0);

		});
	}
	
	@SuppressWarnings("deprecation")
	public static void clear(Player player) {
		Set<Location> locations = getPlayerLocations(player);
		if (locations == null) return;
		
		locations.forEach(location -> {
			Block block = location.getWorld().getBlockAt(location);
			PlayerMixin.sendBlockChange(player, location, CrossMaterial.get(block.getType().name()).get(), block.getData());
		});
		
		locations.clear();
	}

	@SuppressWarnings("deprecation")
	public static void clear(FPlayer fplayer) {
		Set<Location> locations = getPlayerLocations(fplayer.getId());
		if (locations == null) return;
		
		locations.forEach(location -> {
			Block block = location.getWorld().getBlockAt(location);
			PlayerMixin.sendBlockChange(fplayer.getPlayer(), location, CrossMaterial.get(block.getType().name()).get(), block.getData());
		});
		
		locations.clear();
	}

}
