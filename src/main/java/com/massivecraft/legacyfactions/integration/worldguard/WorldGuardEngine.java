package com.massivecraft.legacyfactions.integration.worldguard;

import com.massivecraft.legacyfactions.FLocation;
import com.massivecraft.legacyfactions.integration.IntegrationEngine;
import com.sk89q.worldedit.BlockVector;
import com.sk89q.worldedit.Vector;
import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import com.sk89q.worldguard.protection.ApplicableRegionSet;
import com.sk89q.worldguard.protection.flags.DefaultFlag;
import com.sk89q.worldguard.protection.flags.StateFlag.State;
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.regions.ProtectedCuboidRegion;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import static com.sk89q.worldguard.bukkit.BukkitUtil.toVector;

/*
 *  Worldguard Region Checking
 *  Author: Spathizilla
 */

// TODO: use newer faster method, rewrite entire class
public class WorldGuardEngine extends IntegrationEngine {

	private static WorldGuardPlugin wg;
    
	public static void init() {
		wg = WorldGuardPlugin.inst();
	}

    // PVP Flag check
    // Returns:
    //   True: PVP is allowed
    //   False: PVP is disallowed
    public static boolean isPVP(Player player) {
    	// Check for enabled integration
        if ( ! WorldGuardIntegration.get().isEnabled()) {
            return true;
        }

        Location loc = player.getLocation();
        World world = loc.getWorld();
        Vector pt = toVector(loc);

        RegionManager regionManager = wg.getRegionManager(world);
        ApplicableRegionSet set = regionManager.getApplicableRegions(pt);
        
        return set.queryValue(null, DefaultFlag.PVP) == State.ALLOW;
    }

    // Check if player can build at location by worldguards rules.
    // Returns:
    //	True: Player can build in the region.
    //	False: Player can not build in the region.
    public static boolean playerCanBuild(Player player, Location loc) {
        if ( ! WorldGuardIntegration.get().isEnabled())  return false;


        World world = loc.getWorld();
        Vector pt = toVector(loc);

        if (wg.getRegionManager(world).getApplicableRegions(pt).size() > 0) {
            return wg.canBuild(player, loc);
        }
        return false;
    }

    // Check for Regions in chunk the chunk
    // Returns:
    //   True: Regions found within chunk
    //   False: No regions found within chunk
    
    public static boolean checkForRegionsInChunk(Location loc) {
    	return checkForRegionsInChunk(loc.getChunk());	
    }
    
    public static boolean checkForRegionsInChunk(FLocation loc) {
    	return checkForRegionsInChunk(loc.getChunk());	
    }
    
    public static boolean checkForRegionsInChunk(Chunk chunk) {
        if (!WorldGuardIntegration.get().isEnabled()) return false;
        
        World world = chunk.getWorld();
        int minChunkX = chunk.getX() << 4;
        int minChunkZ = chunk.getZ() << 4;
        int maxChunkX = minChunkX + 15;
        int maxChunkZ = minChunkZ + 15;

        int worldHeight = world.getMaxHeight(); // Allow for heights other than default

        BlockVector minChunk = new BlockVector(minChunkX, 0, minChunkZ);
        BlockVector maxChunk = new BlockVector(maxChunkX, worldHeight, maxChunkZ);

        RegionManager regionManager = wg.getRegionManager(world);
        ProtectedCuboidRegion region = new ProtectedCuboidRegion("wgfactionoverlapcheck", minChunk, maxChunk);
        Map<String, ProtectedRegion> allregions = regionManager.getRegions();
        Collection<ProtectedRegion> allregionslist = new ArrayList<ProtectedRegion>(allregions.values());
        
        try {
        	List<ProtectedRegion> overlaps = region.getIntersectingRegions(allregionslist);
            if (overlaps == null || overlaps.isEmpty()) {
               return false;
            } else {
                return true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;
    }

}