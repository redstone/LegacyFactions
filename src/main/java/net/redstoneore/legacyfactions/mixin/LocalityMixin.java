package net.redstoneore.legacyfactions.mixin;

import java.util.HashSet;
import java.util.Set;

import net.redstoneore.legacyfactions.locality.Locality;
import net.redstoneore.legacyfactions.util.MiscUtil;

/**
 * This mixin provides methods used for Locality, but you are free to use this for your own plugins
 * if needed. 
 */
public class LocalityMixin {

	public static Set<Locality> getArea(Locality from, Locality to) {
		HashSet<Locality> locations = new HashSet<>();
		
		for (Long x : MiscUtil.range(from.getChunkX(), to.getChunkX())) {
			for (Long z : MiscUtil.range(from.getChunkZ(), to.getChunkZ())) {
				locations.add(Locality.of(from.getWorld(), x.intValue(), z.intValue()));
			}
		}

		return locations;
	}
	
	// bit-shifting is used because it's much faster than standard division and multiplication
	public static int blockToChunk(int blockVal) {	// 1 chunk is 16x16 blocks
		return blockVal >> 4;   // ">> 4" == "/ 16"
	}

	public static int blockToRegion(int blockVal) {	// 1 region is 512x512 blocks
		return blockVal >> 9;   // ">> 9" == "/ 512"
	}

	public static int chunkToRegion(int chunkVal) {	// 1 region is 32x32 chunks
		return chunkVal >> 5;   // ">> 5" == "/ 32"
	}

	public static int chunkToBlock(int chunkVal) {
		return chunkVal << 4;   // "<< 4" == "* 16"
	}

	public static int regionToBlock(int regionVal) {
		return regionVal << 9;   // "<< 9" == "* 512"
	}

	public static int regionToChunk(int regionVal) {
		return regionVal << 5;   // "<< 5" == "* 32"
	}
	
	public static Locality getFloor(Locality locality) {
		return null;
	}
	
}
