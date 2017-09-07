package net.redstoneore.legacyfactions.entity.persist.mysql;

import net.redstoneore.legacyfactions.entity.persist.PersistHandler;
import net.redstoneore.legacyfactions.entity.persist.PersistType;

public class FactionsMySQL extends PersistHandler {

	// -------------------------------------------------- //
	// INSTANCE
	// -------------------------------------------------- //
	
	private static FactionsMySQL instance = new FactionsMySQL();
	public static FactionsMySQL get() { return instance; }
	
	// -------------------------------------------------- //
	// METHODS
	// -------------------------------------------------- //
	
	@Override
	public void convertfrom(PersistHandler other) {
		
	}

	@Override
	public PersistType getType() {
		return PersistType.MYSQL;
	}
	
}
