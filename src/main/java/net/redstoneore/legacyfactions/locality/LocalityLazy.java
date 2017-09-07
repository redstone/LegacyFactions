package net.redstoneore.legacyfactions.locality;

import java.util.UUID;

/**
 * A LocalityLazy extends locality, it does not make bukkit references. 
 */
public class LocalityLazy extends Locality {
	
	// -------------------------------------------------- //
	// STATIC FIELDS
	// -------------------------------------------------- //
	
	private transient static final long serialVersionUID = -1565165766499822994L;

	// -------------------------------------------------- //
	// STATIC METHODS
	// -------------------------------------------------- //
	
	public static LocalityLazy of(UUID world, int chunkX, int chunkZ) {
		return new LocalityLazy(world, chunkX, chunkZ);
	}
	
	public static Locality of(String world, int chunkX, int chunkZ) {
		return new Locality(world, null, chunkX, chunkZ);
	}
	
	
	// -------------------------------------------------- //
	// CONSTRUCT
	// -------------------------------------------------- //
	
	protected LocalityLazy() { }
	protected LocalityLazy(UUID world, int chunkX, int chunkZ) {
		super(world, chunkX, chunkZ);
	}
	
}
