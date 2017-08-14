package net.redstoneore.legacyfactions.landmanager;

import org.bukkit.Chunk;
import org.bukkit.World;

import com.sk89q.worldedit.BlockVector;
import com.sk89q.worldedit.EditSession;
import com.sk89q.worldedit.LocalWorld;
import com.sk89q.worldedit.bukkit.BukkitUtil;
import com.sk89q.worldedit.regions.CuboidRegion;

public class LandManagerWorldEdit implements LandManager {
	
	@Override
	public void rebuild(Chunk chunk) {
        World world = chunk.getWorld();
        
        int minChunkX = chunk.getX() << 4;
        int minChunkZ = chunk.getZ() << 4;
        int maxChunkX = minChunkX + 15;
        int maxChunkZ = minChunkZ + 15;

        int worldHeight = world.getMaxHeight();

        BlockVector minChunk = new BlockVector(minChunkX, 0, minChunkZ);
        BlockVector maxChunk = new BlockVector(maxChunkX, worldHeight, maxChunkZ);
        
        LocalWorld localWorld = BukkitUtil.getLocalWorld(chunk.getWorld());
        CuboidRegion region = new CuboidRegion(localWorld, minChunk, maxChunk);
        
        EditSession editSession = new EditSession(localWorld, -1);
        
        localWorld.regenerate(region, editSession);
	}
	
}
