package net.redstoneore.legacyfactions.locality;

import java.io.Serializable;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.WorldBorder;
import org.bukkit.block.Block;

import net.redstoneore.legacyfactions.entity.Board;
import net.redstoneore.legacyfactions.entity.FPlayer;
import net.redstoneore.legacyfactions.entity.Faction;
import net.redstoneore.legacyfactions.mixin.LocalityMixin;

/**
 * A powerful location utility to work between {@link Location}, {@link Chunk}, and {@link Block}.
 */
public class Locality implements Serializable {

	// -------------------------------------------------- //
	// STATIC FIELDS
	// -------------------------------------------------- //
	
	private transient static final long serialVersionUID = -1565165766499822994L;

	private static boolean worldBorderAvailable;
	
	static {
		try {
			Class.forName("org.bukkit.WorldBorder");
			worldBorderAvailable = true;
		} catch (ClassNotFoundException e) {
			worldBorderAvailable = false;
		}
	}
	
	// -------------------------------------------------- //
	// STATIC METHODS
	// -------------------------------------------------- //

	public static Locality of(Location location) {
		return new Locality(location);
	}
	
	public static Locality of(Block block) {
		return new Locality(block);
	}
	
	public static Locality of(Chunk chunk) {
		return new Locality(chunk);
	}
		
	public static Locality of(World world, int chunkX, int chunkZ) {
		return of(world.getChunkAt(chunkX, chunkZ));
	}
	
	public static Locality of(String worldName, int chunkX, int chunkZ) {
		return new Locality(worldName, null, chunkX, chunkZ);
	}
	
	public static Locality of(FPlayer fplayer) {
		return new Locality(fplayer.getPlayer().getLocation());
	}
	
	public static Locality of(String string) throws Exception {
		if (string.contains(":")) {
			// Is new format 

			String[] parts = string.split(",");
				
			UUID worldUID = UUID.fromString(parts[0].replace("[", ""));
			String[] chunk = parts[0].split(":");
			String[] coordinates = parts[1].split(":");
			String[] block = parts[2].split(":");
			Type type = Type.valueOf(parts[3].replace("]", ""));
			
			Locality locality = new Locality();
			locality.world = worldUID;
			locality.chunkX = Integer.valueOf(chunk[0]);
			locality.chunkZ = Integer.valueOf(chunk[1]);
			locality.locationX = Double.valueOf(coordinates[0]);
			locality.locationY = Double.valueOf(coordinates[1]);
			locality.locationZ = Double.valueOf(coordinates[2]);
			
			try {
				locality.pitch = Float.valueOf(coordinates[3]);
				locality.yaw = Float.valueOf(coordinates[4]);
			} catch (Exception e) {
				// old versions of this format don't have pitch and yaw
			}
			
			locality.blockX = Integer.valueOf(block[0]);
			locality.blockY = Integer.valueOf(block[1]);
			locality.blockZ = Integer.valueOf(block[2]);
			locality.localityType = type;
			
			return locality;
		}
		
		// Is FLocation format
		int index = string.indexOf(",", 0);
		int start = 1;
		String worldName = string.substring(start, index);
		start = index + 1;
		index = string.indexOf(",", start);
		int chunkX = Integer.valueOf(string.substring(start, index));
		int chunkZ = Integer.valueOf(string.substring(index + 1, string.length() - 1));
				
		World world = Bukkit.getWorld(worldName);
				
		if (world != null) {
			return Locality.of(world, chunkX, chunkZ);
		}
		
		return null;
	}
		
	// -------------------------------------------------- //
	// CONSTRUCT
	// -------------------------------------------------- //
		
	protected Locality(Location location) {
		this.localityType = Type.Location;
		
		this.world = location.getWorld().getUID();
		
		this.locationX = location.getX();
		this.locationY = location.getY();
		this.locationZ = location.getZ();
		
		this.blockX = location.getBlockX();
		this.blockY = location.getBlockY();
		this.blockZ = location.getBlockZ();
		
		this.yaw = location.getYaw();
		this.pitch = location.getPitch();
		
		this.chunkX = location.getChunk().getX();
		this.chunkZ = location.getChunk().getZ();
	}
	
	protected Locality(Block block) {
		this.localityType = Type.Block;
		
		this.world = block.getWorld().getUID();
		
		this.locationX = block.getLocation().getX();
		this.locationY = block.getLocation().getY();
		this.locationZ = block.getLocation().getZ();
		
		this.yaw = block.getLocation().getYaw();
		this.pitch = block.getLocation().getPitch();
		
		this.blockX = block.getX();
		this.blockY = block.getY();
		this.blockZ = block.getZ();
		
		this.chunkX = block.getChunk().getX();
		this.chunkZ = block.getChunk().getZ();
	}
	
	protected Locality(Chunk chunk) {
		this.localityType = Type.Chunk;
		
		this.world = chunk.getWorld().getUID();
		
		this.chunkX = chunk.getX();
		this.chunkZ = chunk.getZ();
	}
	
	// -------------------------------------------------- //
	// LAZY CONSTRUCTORS
	// -------------------------------------------------- //
	
	protected Locality(String worldName, UUID world, int chunkX, int chunkZ) {
		this.localityType = Type.Chunk;
		
		this.worldName = worldName;
		this.world = world;
		
		this.chunkX = chunkX;
		this.chunkZ = chunkZ;
	}
	
	protected Locality(UUID world, int chunkX, int chunkZ) {
		this.localityType = Type.Chunk;
		
		this.world = world;
		
		this.chunkX = chunkX;
		this.chunkZ = chunkZ;
	}
	
	// -------------------------------------------------- //
	// FLOCATION DEPRECATED CONSTRUCTORS
	// -------------------------------------------------- //
	
	protected Locality(String worldName, int chunkX, int chunkZ) {
		this.localityType = Type.Chunk;
		
		this.worldName = worldName;
		
		this.chunkX = chunkX;
		this.chunkZ = chunkZ;
	}
	
	/**
	 * only used when fields are being set manually from static {@link Locality Locality#of} methods
	 */
	protected Locality() { }

	// -------------------------------------------------- //
	// FIELDS
	// -------------------------------------------------- //
	
	private Type localityType;
	
	private transient String worldName = null;
	private UUID world = null;
	
	private double locationX = 0;
	private double locationY = 0;
	private double locationZ = 0;

	private float pitch = 0f;
    private float yaw = 0f;
    
	private int blockX = 0;
	private int blockY = 0;
	private int blockZ = 0;
	
	private int chunkX = 0;
	private int chunkZ = 0;
	
	// -------------------------------------------------- //
	// ENUM
	// -------------------------------------------------- //

	public enum Type {
		Location,
		Block,
		Chunk,
		;
	}
	
	// -------------------------------------------------- //
	// METHODS
	// -------------------------------------------------- //

	public Type getType() {
		return this.localityType;
	}
	
	public double getLocationX() {
		return this.locationX;
	}
	
	public double getLocationY() {
		return this.locationY;
	}
	
	public double getLocationZ() {
		return this.locationZ;
	}
	
	@Deprecated
	public double X() {
		return this.locationX;
	}
	
	@Deprecated
	public double Y() {
		return this.locationY;
	}
	
	@Deprecated
	public double Z() {
		return this.locationZ;
	}
	
	public float getPitch() {
		return this.pitch;
	}
	
	public float getYaw() {
		return this.yaw;
	}
	
	public int getBlockX() {
		return this.blockX;
	}
	
	public int getBlockY() {
		return this.blockY;
	}
	
	public int getBlockZ() {
		return this.blockZ;
	}
	
	public int getChunkX() {
		return this.chunkX;
	}
	
	public int getChunkZ() {
		return this.chunkZ;
	}
	
	public Chunk getChunk() {
		return this.getWorld().getChunkAt(this.getChunkX(), this.getChunkZ());
	}
	
	public UUID getWorldUID() {
		if (this.world == null && this.worldName != null) {
			this.world = Bukkit.getWorld(this.worldName).getUID();
			this.worldName = null;
		}
		return this.world;
	}
	
	public World getWorld() {
		if (this.world == null && this.worldName != null) {
			World world = Bukkit.getWorld(this.worldName);
			this.world = world.getUID();
			return world;
		}
		return Bukkit.getWorld(this.getWorldUID());
	}
	
	public String getWorldName() {
		if (this.worldName == null) {
			this.worldName = this.getWorld().getName();
		}
		return this.worldName;
	}
	
	public String getCoordString() {
		return this.getChunkX() + "," + this.getChunkZ();
	}
	
	public Block getBlock() {
		return this.getWorld().getBlockAt(this.getBlockX(), this.getBlockY(), this.getBlockZ());
	}
	
	public Location getLocation() {
		return new Location(this.getWorld(), this.X(), this.Y(), this.Z(), this.getYaw(), this.getPitch());
	}
	
	public Faction getFactionHere() {
		return Board.get().getFactionAt(this);
	}
	
	// -------------------------------------------------- //
	// LOCATION METHODS
	// -------------------------------------------------- //
	
	/**
	 * Returns a new {@link Locality} with added X
	 * @param x to add
	 * @return  new {@link Locality} with added X
	 */
	public Locality addX(double x) {
		Locality locality = this.copy();
		locality.locationX += x;
		return locality;
	}
	
	/**
	 * Returns a new {@link Locality} with added Y
	 * @param y to add
	 * @return  new {@link Locality} with added Y
	 */
	public Locality addY(double y) {
		Locality locality = this.copy();
		locality.locationY += y;
		return locality;
	}
	
	/**
	 * Returns a new {@link Locality} with added Z
	 * @param z to add
	 * @return  new {@link Locality} with added Z
	 */
	public Locality addZ(double z) {
		Locality locality = this.copy();
		locality.locationZ += z;
		return locality;
	}
	
	/**
	 * Returns a new {@link Locality} with removed X
	 * @param x to remove
	 * @return  new {@link Locality} with removed X
	 */
	public Locality removeX(double x) {
		Locality locality = this.copy();
		locality.locationX -= x;
		return locality;
	}
	
	/**
	 * Returns a new {@link Locality} with removed Y
	 * @param y to remove
	 * @return  new {@link Locality} with removed Y
	 */
	public Locality removeY(double y) {
		Locality locality = this.copy();
		locality.locationY -= y;
		return locality;
	}
	
	/**
	 * Returns a new {@link Locality} with removed Z
	 * @param z to remove
	 * @return  new {@link Locality} with removed Z
	 */
	public Locality removeZ(double z) {
		Locality locality = this.copy();
		locality.locationZ -= z;
		return locality;
	}
	
	public void setChunkX(int x) {
		this.chunkX = x;
	}
	
	public void setChunkZ(int z) {
		this.chunkZ = z;
	}
	
	// -------------------------------------------------- //
	// UTILITY METHODS
	// -------------------------------------------------- //
	
	public boolean isOutsideWorldBorder(int buffer) {
		if (!worldBorderAvailable) return false;

		switch (this.getType()) {
		case Block:
			return false; // TODO: outside world border for block
		case Chunk:
			WorldBorder border = this.getWorld().getWorldBorder();
			Chunk chunk = border.getCenter().getChunk();

			int lim = LocalityMixin.chunkToRegion((int) border.getSize()) - buffer;
			int diffX = Math.abs(chunk.getX() - this.getChunkX());
			int diffZ = Math.abs(chunk.getZ() - this.getChunkZ());
			
			return diffX > lim || diffZ > lim;
		case Location:
			return false; // TODO: outside world border for location
		}
		
		return false;
	}
	
	/**
	 * Get a circle radius of this Locality based chunks
	 * @param radius
	 * @return
	 */
	public Set<Locality> getCircle(double radius) {
		Set<Locality> locations = new LinkedHashSet<>();
		if (radius <= 0) return locations;
		
		double radiusSquared = radius * radius;
		
		int xfrom = (int) Math.floor(this.getChunkX() - radius);
		int xto = (int) Math.ceil(this.getChunkX() + radius);
		int zfrom = (int) Math.floor(this.getChunkZ() - radius);
		int zto = (int) Math.ceil(this.getChunkZ() + radius);
		
		for (int x = xfrom; x <= xto; x++) {
			for (int z = zfrom; z <= zto; z++) {
				Locality potential = Locality.of(this.getWorld(), x, z);
				if (this.getDistanceSquaredTo(potential) > radiusSquared) continue;
				locations.add(potential);
			}
		}
		
		return locations;		
	}
	
	public Locality getRelative(int dx, int dz) {
		return of(this.getWorld().getChunkAt(this.getChunkX() + dx, this.getChunkZ() + dz));
	}
	
	public Locality getRelative(int dx, int dy, int dz) {
		switch (this.getType()) {
		case Block:
			return of(this.getWorld().getBlockAt(this.getBlockX() + dx, this.getBlockX() + dy, this.getBlockX() + dz));
		case Location:
			return of(new Location(this.getWorld(), this.X() + dx, this.Y() + dy, this.Z() + dz));
		default:
			break;
		}
		
		// Return a chunk as our safest bet
		return of(this.getWorld().getChunkAt(this.getChunkX() + dx, this.getChunkZ() + dz));
	}
	
	/**
	 * Get the distance to another locality, based on the type of this.
	 * @param that locality
	 * @return distance
	 */
	public double getDistanceTo(Locality that) {
		if (that.getWorldUID() != this.getWorldUID()) {
			return 0d;
		}
		
		return Math.sqrt(this.getDistanceSquaredTo(that));
	}

	/**
	 * Get the distance squared to another locality, based on the type of this.
	 * @param that locality
	 * @return distance squared
	 */
	public double getDistanceSquaredTo(Locality that) {
		if (that.getWorldUID() != this.getWorldUID()) {
			return 0d;
		}
		
		double dx, dy, dz;
		switch (this.getType()) {
		case Block:
			dx = that.getBlockX() - this.getBlockX();
			dy = that.getBlockY() - this.getBlockY();
			dz = that.getBlockZ() - this.getBlockZ();
			return ((dx * dx) + (dy * dy) + (dz * dz));
		case Chunk:
			dx = that.getChunkX() - this.getChunkX();
			dz = that.getChunkZ() - this.getChunkZ();
			return ((dx * dx) + (dz * dz));
		case Location:
			dx = that.X() - this.X();
			dy = that.Y() - this.Y();
			dz = that.Z() - this.Z();
			return ((dx * dx) + (dy * dy) + (dz * dz));
		default:
			return 0d;
		}
	}

	public boolean isInChunk(Locality chunk) {
		if (chunk == null) return false;
		
		if (this.getWorldUID() != chunk.getWorldUID());
		
		return (this.getChunkX() == chunk.getChunkX() && this.getChunkZ() == chunk.getChunkZ());
	}
	
	public boolean isInBlock(Locality block) {
		if (block == null) return false;
		
		if (this.getWorldUID() != block.getWorldUID());
		
		return (this.getBlockX() == block.getBlockX() && this.getBlockY() == block.getBlockY() && this.getBlockZ() == block.getBlockZ());
	}
	
	/**
	 * Attempts to find the floor upwards, if it fails it reverts to {@link Locality#getFloorDown}, if that fails it returns null
	 * @return {@link Locality} with floor, or null if none
	 */
	public LocalityOwnership getOwnership() {
		return new LocalityOwnership(this);
	}
	
	private Locality getFloorUpLogic(boolean secondWind) {
		Locality at = this.copy();
		double y = at.Y();
		
		while (at.getBlock().getType() == Material.AIR) {
			at = at.addY(1.0);
			
			if (y > at.getWorld().getMaxHeight()) {
				if (secondWind) return null;
				
				return this.getFloorDownLogic(true);
			}
		}
		
		return at;
	}
	
	/**
	 * Attempts to find the floor upwards, if it fails it reverts to {@link Locality#getFloorDown}, if that fails it returns null
	 * @return {@link Locality} with floor, or null if none
	 */
	public Locality getFloorUp() {
		return getFloorUpLogic(false);
	}
	
	private Locality getFloorDownLogic(boolean secondWind) {
		Locality at = this.copy();
		double y = at.Y();
		
		while (at.getBlock().getType() == Material.AIR) {
			at = at.removeY(1.0);
			
			if (y < 0) {
				if (secondWind) return null;
				
				return at.getFloorUpLogic(true);
			}
		}
		
		return at;
	}
	
	/**
	 * Attempts to find the floor downwards, if it fails it reverts to {@link Locality#getFloorUp}, if that fails it returns null
	 * @return {@link Locality} with floor, or null if none
	 */
	public Locality getFloorDown() {
		return this.getFloorDownLogic(false);
	}
	
	@Override
	public String toString() {
		return "[" + this.getWorldUID() + ", " + this.getChunkX() + ":" + this.getChunkZ() + ", " + this.X() + ":" + this.Y() + ":" + this.Z() + ":" + this.getPitch() + ":" + this.getYaw() + ", " + this.getBlockX() + ":" + this.getBlockY() + ":" + this.getBlockZ() + ", " + this.getType().toString() +" ]";
	}

	// -------------------------------------------------- //
	// COMPARISON METHODS
	// -------------------------------------------------- //

	@Override
	public int hashCode() {
		Double result = 25d;
		
		result += 9 * (1 + (this.X()));
		result += 9 * (1 + (this.Y()));
		result += 9 * (1 + (this.Z()));
		
		result += 9 * (1 + (this.getBlockX()));
		result += 9 * (1 + (this.getBlockY()));
		result += 9 * (1 + (this.getBlockZ()));
		
		result += 9 * (1 + (this.getChunkX()));
		result += 9 * (1 + (this.getChunkZ()));

		if (this.world == null && this.worldName != null) {
			this.world = Bukkit.getWorld(worldName).getUID();
		}
		
		result += this.world.hashCode();
		
		return result.intValue();
	}
	
	public Set<LocalityLazy> getRadius(int radius) {
		Set<LocalityLazy> chunks = new HashSet<>();
		
		for (int chunkZ = this.getChunkX() + radius; chunkZ > this.getChunkZ() - radius; chunkZ -= 1) {
			for (int chunkX = this.getChunkX() + radius; chunkX > this.getChunkZ() - radius; chunkX -= 1) {
				chunks.add(LocalityLazy.of(this.getWorldUID(), chunkX, chunkZ));
			}
		}
		
		return chunks;
	}
	
	/**
	 * We are comparing this to otherObject. So we will only compare the type parameters 
	 * of this to otherObject.
	 * 
	 * For example, if this Type is a Chunk. We will chunkX and chunkZ to otherObject. Even if
	 * otherObject is a Block.
	 * 
	 * @param otherObject Other object to compare to.
	 * @return true if they equal
	 */
	@Override
	public boolean equals(Object otherObject) {
		if (otherObject == null) return false;
		if (otherObject == this) return true;
		
		if (!(otherObject instanceof Locality)) return false;
		
		Locality other = (Locality) otherObject;
		
		// If they aren't the same world we can dismiss them now 
		if (other.getWorldUID() != this.getWorldUID()) return false;
		
		// Compare other to this
		switch (this.getType()) {
		case Block:
			return (other.getBlockX() == this.getBlockX() &&
					other.getBlockY() == this.getBlockY() &&
					other.getBlockZ() == this.getBlockZ());
		case Chunk:
			return (other.getChunkX() == this.getChunkX() &&
					other.getChunkZ() == this.getChunkZ());
		case Location:
			return (other.X() == this.X() && 
					other.Y() == this.Y() && 
					other.Z() == this.Z());
		default:
			return false;
		}
	}
	
	/**
	 * Create a clean copy of this {@link Locality}
	 * @return a clean copy of this {@link Locality}
	 */
	public Locality copy() {
		Locality locality = new Locality();
		locality.blockX = this.blockX;
		locality.blockY = this.blockY;
		locality.blockZ = this.blockZ;
		locality.chunkX = this.chunkX;
		locality.chunkZ = this.chunkZ;
		locality.localityType = this.localityType;
		locality.locationX = this.locationX;
		locality.locationY = this.locationY;
		locality.locationZ = this.locationZ;
		locality.pitch = this.pitch;
		locality.yaw = this.yaw;
		return locality;
	}
	
}
