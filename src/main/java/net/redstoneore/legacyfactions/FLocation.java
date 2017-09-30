package net.redstoneore.legacyfactions;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

import net.redstoneore.legacyfactions.entity.FPlayer;
import net.redstoneore.legacyfactions.locality.Locality;
import net.redstoneore.legacyfactions.mixin.LocalityMixin;
import net.redstoneore.legacyfactions.util.MiscUtil;

/**
 * FLocation is an internal class at the moment used only be ownerships. It is due to be removed
 * in the future so use {@link Locality} instead. 
 */
public class FLocation extends Locality implements Serializable {

	// -------------------------------------------------- //
	// STATIC FIELDS
	// -------------------------------------------------- //
	
	private static final long serialVersionUID = -8292915234027387983L;
	
	// -------------------------------------------------- //
	// STATIC METHODS
	// -------------------------------------------------- //

	public static FLocation valueOf(Location location) {
		return new FLocation(location);
	}
	
	public static FLocation valueOf(Chunk chunk) {
		return new FLocation(chunk);
	}
	
	public static FLocation fromString(String string) {
		int index = string.indexOf(",", 0);
		int start = 1;
		String worldName = string.substring(start, index);
		start = index + 1;
		index = string.indexOf(",", start);
		int x = Integer.valueOf(string.substring(start, index));
		int y = Integer.valueOf(string.substring(index + 1, string.length() - 1));
		return new FLocation(worldName, x, y);
	}

	// -------------------------------------------------- //
	// CONSTRUCT
	// -------------------------------------------------- //
	
	protected FLocation() { }

	public FLocation(String worldName, int x, int z) {
		super(worldName, x, z);
	}

	public FLocation(Location location) {
		this(location.getChunk());
	}

	public FLocation(Player player) {
		this(player.getLocation());
	}

	public FLocation(FPlayer fplayer) {
		this(fplayer.getPlayer());
	}

	public FLocation(Block block) {
		this(block.getLocation());
	}
	
	public FLocation(Chunk chunk) {
		this(chunk.getWorld().getName(), chunk.getX(), chunk.getZ());
	}
	
	// -------------------------------------------------- //
	// FIELDS
	// -------------------------------------------------- //

	private String worldName = "world";
	private Integer x = null;
	private Integer z = null;
	
	// -------------------------------------------------- //
	// GETTERS & SETTERS
	// -------------------------------------------------- //
	
	public String getWorldName() {
		return this.worldName;
	}

	public void setWorldName(String worldName) {
		this.worldName = worldName;
	}

	public long getX() {
		return super.getChunkX();
	}

	public void setX(int x) {
		super.setChunkX(x);
	}

	public long getZ() {
		return super.getChunkZ();
	}

	public void setZ(int z) {
		super.setChunkZ(z);
	}
	
	@Override
	public int getChunkX() {
		if (this.x != null) {
			super.setChunkX(this.x);
			this.x = null;
		}
		return super.getChunkX();
	}
	
	public int getChunkZ() {
		if (this.z != null) {
			super.setChunkX(this.z);
			this.z = null;
		}
		return super.getChunkX();
	}

	// -------------------------------------------------- //
	// Block/Chunk/Region Value Transformation
	// -------------------------------------------------- //

	public static int blockToChunk(int blockVal) {
		return LocalityMixin.blockToChunk(blockVal);
	}

	public static int blockToRegion(int blockVal) {
		return LocalityMixin.blockToRegion(blockVal);
	}

	public static int chunkToRegion(int chunkVal) {
		return LocalityMixin.chunkToRegion(chunkVal);
	}

	public static int chunkToBlock(int chunkVal) {
		return LocalityMixin.chunkToBlock(chunkVal);
	}

	public static int regionToBlock(int regionVal) {
		return LocalityMixin.regionToBlock(regionVal);
	}

	public static int regionToChunk(int regionVal) {
		return LocalityMixin.regionToChunk(regionVal);
	}

	// -------------------------------------------------- //
	// Misc Geometry
	// -------------------------------------------------- //

	public FLocation getRelative(int dx, int dz) {
		return new FLocation(this.worldName, this.x + dx, this.z + dz);
	}

	public double getDistanceTo(FLocation that) {
		double dx = that.x - this.x;
		double dz = that.z - this.z;
		return Math.sqrt(dx * dx + dz * dz);
	}

	public double getDistanceSquaredTo(FLocation that) {
		double dx = that.x - this.x;
		double dz = that.z - this.z;
		return dx * dx + dz * dz;
	}
	
	// -------------------------------------------------- //
	// Some Geometry
	// -------------------------------------------------- //

	public static Set<FLocation> getArea(FLocation from, FLocation to) {
		HashSet<FLocation> result = new HashSet<>();

		for (long x : MiscUtil.range(from.getX(), to.getX())) {
			for (long z : MiscUtil.range(from.getZ(), to.getZ())) {
				result.add(new FLocation(from.getWorldName(), (int) x, (int) z));
			}
		}

		return result;
	}

	// -------------------------------------------------- //
	// Comparison
	// -------------------------------------------------- //

	@Override
	public int hashCode() {
		// should be fast, with good range and few hash collisions: (x * 512) + z + worldName.hashCode
		return (this.x << 9) + this.z + (this.worldName != null ? this.worldName.hashCode() : 0);
	}
	
}
