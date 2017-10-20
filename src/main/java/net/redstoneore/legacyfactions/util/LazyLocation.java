package net.redstoneore.legacyfactions.util;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;

import java.io.Serializable;

/**
 * This class provides a lazy-load Location, so that World doesn't need to be initialised
 * yet when an object of this class is created, only when the Location is first accessed. *
 */
public class LazyLocation implements Serializable {
	
	// -------------------------------------------------- //
	// STATIC FIELDS
	// -------------------------------------------------- //
	
	private static final long serialVersionUID = -6049901271320963314L;
	
	public static LazyLocation of(Location location) {
		return new LazyLocation(location);
	}
	
	// -------------------------------------------------- //
	// CONSTRUCT
	// -------------------------------------------------- //

	/**
	 * Construct a LazyLocation using a location
	 * @param location The location.
	 */
	public LazyLocation(Location location) {
		this.setLocation(location);
	}

	/**
	 * Construct a LazyLocation from fields
	 * @param worldName Location World
	 * @param x Location X
	 * @param y Location Y
	 * @param z Location Z
	 */
	public LazyLocation(final String worldName, final double x, final double y, final double z) {
		this(worldName, x, y, z, 0, 0);
	}
	
	/**
	 * Construct a LazyLocation from fields
	 * @param worldName Location World
	 * @param x Location X
	 * @param y Location Z
	 * @param z Location Z
	 * @param yaw Location Yaw
	 * @param pitch Location Pitch
	 */
	public LazyLocation(final String worldName, final double x, final double y, final double z, final float yaw, final float pitch) {
		this.worldName = worldName;
		this.x = x;
		this.y = y;
		this.z = z;
		this.yaw = yaw;
		this.pitch = pitch;
	}
	
	// -------------------------------------------------- //
	// FIELDS
	// -------------------------------------------------- //
	
	private transient Location location = null;
	private String worldName;
	private double x;
	private double y;
	private double z;
	private float pitch;
	private float yaw;
	
	// -------------------------------------------------- //
	// METHODS
	// -------------------------------------------------- //
	
	/**
	 * This returns the actual location
	 * @return {@link Location}
	 */
	public final Location getLocation() {
		// if location is already initialized, simply return
		if (location != null) {
			return this.location;
		}

		// get World; hopefully it's initialized at this point
		World world = Bukkit.getWorld(worldName);
		if (world == null) {
			return null;
		}

		// store the Location for future calls, and pass it on
		location = new Location(world, x, y, z, yaw, pitch);
		if (location == null) {
			return null;
		}
		
		return this.location;
	}

	/**
	 * Change the location
	 * @param location to set to
	 */
	public final void setLocation(Location location) {
		this.location = location;
		this.worldName = location.getWorld().getName();
		this.x = location.getX();
		this.y = location.getY();
		this.z = location.getZ();
		this.yaw = location.getYaw();
		this.pitch = location.getPitch();
	}


	/**
	 * Get the world name
	 * @return world name
	 */
	public final String getWorldName() {
		return worldName;
	}

	/**
	 * Get the location X
	 * @return location X
	 */
	public final double getX() {
		return x;
	}
	
	/**
	 * Get the location X, rounded.
	 * @param decimalPlaces
	 * @return location X, rounded.
	 */
	public final double getX(int decimalPlaces) {
		return MathUtil.roundUp(this.x, decimalPlaces);
	}

	/**
	 * Get the location Y
	 * @return location Y
	 */
	public final double getY() {
		return y;
	}
	
	/**
	 * Get the location Y, rounded
	 * @param decimalPlaces
	 * @return location Y, rounded.
	 */
	public final double getY(int decimalPlaces) {
		return MathUtil.roundUp(this.y, decimalPlaces);
	}

	/**
	 * Get the location Z
	 * @return location Z
	 */
	public final double getZ() {
		return z;
	}
	
	/**
	 * Get the location Z, rounded.
	 * @param decimalPlaces
	 * @return location Z, rounded.
	 */
	public final double getZ(int decimalPlaces) {
		return MathUtil.roundUp(this.z, decimalPlaces);
	}

	/**
	 * Get the location pitch
	 * @return location pitch
	 */
	public final double getPitch() {
		return pitch;
	}
	
	/**
	 * Get the location yaw
	 * @return location yaw
	 */
	public final double getYaw() {
		return yaw;
	}
	
}
