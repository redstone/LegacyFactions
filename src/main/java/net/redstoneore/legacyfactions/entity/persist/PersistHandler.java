package net.redstoneore.legacyfactions.entity.persist;

import net.redstoneore.legacyfactions.entity.Conf;

public abstract class PersistHandler {

	// -------------------------------------------------- //
	// STATIC METHODS
	// -------------------------------------------------- //
	
	public static PersistHandler getCurrent() {
		return Conf.backEnd.getHandler();
	}
	
	public static PersistHandler setCurrent(PersistHandler handler) {
		Conf.backEnd = handler.getType();
		return Conf.backEnd.getHandler();
	}

	// -------------------------------------------------- //
	// ABSTRACT METHODS
	// -------------------------------------------------- //
	
	abstract public void convertfrom(PersistHandler other);
	abstract public PersistType getType();
	
}
