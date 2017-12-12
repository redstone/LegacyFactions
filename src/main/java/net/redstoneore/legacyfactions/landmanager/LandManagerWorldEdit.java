package net.redstoneore.legacyfactions.landmanager;

import org.bukkit.Chunk;

import com.sk89q.worldedit.BlockVector;
import com.sk89q.worldedit.EditSession;
import com.sk89q.worldedit.WorldEdit;
import com.sk89q.worldedit.bukkit.BukkitWorld;
import com.sk89q.worldedit.regions.CuboidRegion;
import com.sk89q.worldedit.world.World;

public class LandManagerWorldEdit implements LandManager {
	
	@Override
	public void rebuild(Chunk chunk) {	
		// Work out the chunk min and max positions.
		int minChunkX = chunk.getX() << 4;
		int minChunkZ = chunk.getZ() << 4;
		int maxChunkX = minChunkX + 15;
		int maxChunkZ = minChunkZ + 15;

		// Fetch the world max height, as certain worlds can have different max heights set.
		int worldMaxHeight = chunk.getWorld().getMaxHeight();

		// Get the block vectors.
		BlockVector minChunk = new BlockVector(minChunkX, 0, minChunkZ);
		BlockVector maxChunk = new BlockVector(maxChunkX, worldMaxHeight, maxChunkZ);
		
		// Get the WorldEdit world and region.
		World world = new BukkitWorld(chunk.getWorld());
		CuboidRegion region = new CuboidRegion(world, minChunk, maxChunk);
		
		// Open an edit session.
		EditSession editSession = WorldEdit.getInstance().getEditSessionFactory().getEditSession(world, -1);
		
		// Regenerate the region.
		world.regenerate(region, editSession);
	}
	
}
