package net.redstoneore.legacyfactions;

import java.io.Serializable;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Set;

import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

import net.redstoneore.legacyfactions.entity.FPlayer;
import net.redstoneore.legacyfactions.util.MiscUtil;


public class FLocation implements Serializable {

	// -------------------------------------------------- //
	// STATIC FIELDS
	// -------------------------------------------------- //
	
	private static final long serialVersionUID = -8292915234027387983L;
	private static boolean worldBorderSupport;
	
	static {
		worldBorderSupport = false;
		
		try {
			Class.forName("org.bukkit.WorldBorder");
			worldBorderSupport = true;
		} catch (ClassNotFoundException e) { }
	}
	
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
		this.worldName = worldName;
		this.x = x;
		this.z = z;
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
	private int x = 0;
	private int z = 0;
	
	// -------------------------------------------------- //
	// GETTERS & SETTERS
	// -------------------------------------------------- //

	public Chunk getChunk() {
		return this.getWorld().getChunkAt(this.x, this.z);
	}
	
	public String getWorldName() {
		return this.worldName;
	}

	public World getWorld() {
		return Bukkit.getWorld(this.worldName);
	}

	public void setWorldName(String worldName) {
		this.worldName = worldName;
	}

	public long getX() {
		return this.x;
	}

	public void setX(int x) {
		this.x = x;
	}

	public long getZ() {
		return this.z;
	}

	public void setZ(int z) {
		this.z = z;
	}

	public String getCoordString() {
		return this.x + "," + this.z;
	}

	@Override
	public String toString() {
		return "[" + this.getWorldName() + "," + this.getCoordString() + "]";
	}

	// -------------------------------------------------- //
	// Block/Chunk/Region Value Transformation
	// -------------------------------------------------- //

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

	public boolean isInChunk(Location loc) {
		if (loc == null) return false;
		
		Chunk chunk = loc.getChunk();
		return loc.getWorld().getName().equalsIgnoreCase(getWorldName()) && chunk.getX() == x && chunk.getZ() == z;
	}

	/**
	 * Checks if the chunk represented by this FLocation is outside the world border
	 *
	 * @param buffer the number of chunks from the border that will be treated as "outside"
	 * @return whether this location is outside of the border
	 */
	public boolean isOutsideWorldBorder(int buffer) {
		if (!worldBorderSupport) return false;

		WorldBorder border = getWorld().getWorldBorder();
		Chunk chunk = border.getCenter().getChunk();

		int lim = FLocation.chunkToRegion((int) border.getSize()) - buffer;
		int diffX = Math.abs(chunk.getX() - x);
		int diffZ = Math.abs(chunk.getZ() - z);
		return diffX > lim || diffZ > lim;
	}

	// -------------------------------------------------- //
	// Some Geometry
	// -------------------------------------------------- //
	
	public Set<FLocation> getCircle(double radius) {
		double radiusSquared = radius * radius;

		Set<FLocation> ret = new LinkedHashSet<>();
		if (radius <= 0) return ret;

		int xfrom = (int) Math.floor(this.x - radius);
		int xto = (int) Math.ceil(this.x + radius);
		int zfrom = (int) Math.floor(this.z - radius);
		int zto = (int) Math.ceil(this.z + radius);

		for (int x = xfrom; x <= xto; x++) {
			for (int z = zfrom; z <= zto; z++) {
				FLocation potential = new FLocation(this.worldName, x, z);
				if (this.getDistanceSquaredTo(potential) <= radiusSquared) {
					ret.add(potential);
				}
			}
		}

		return ret;
	}

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

	@Override
	public boolean equals(Object obj) {
		if (obj == null) return false;
		if (obj == this) return true;
		
		if (!(obj instanceof FLocation)) return false;
		
		FLocation that = (FLocation) obj;
		return this.x == that.x && this.z == that.z && (this.worldName == null ? that.worldName == null : this.worldName.equals(that.worldName));
	}
	
}
