package net.redstoneore.legacyfactions.locality;

import java.util.UUID;

/**
 * A LocalityLazy extends locality, it does not make bukkit references. 
 */
public class LocalityLazy extends Locality {
	
	// -------------------------------------------------- //
	// STATIC FIELDS
	// -------------------------------------------------- //
	
	private static final long serialVersionUID = -1565165766499822994L;

	// -------------------------------------------------- //
	// STATIC METHODS
	// -------------------------------------------------- //
	
	public static LocalityLazy of(UUID world, int chunkX, int chunkZ) {
		return new LocalityLazy(world, chunkX, chunkZ);
	}
	
	// -------------------------------------------------- //
	// CONSTRUCT
	// -------------------------------------------------- //
	
	protected LocalityLazy() { }
	protected LocalityLazy(UUID world, int chunkX, int chunkZ) {
		super(world, chunkX, chunkZ);
	}
	
}
