package net.redstoneore.legacyfactions.entity.persist;

import net.redstoneore.legacyfactions.config.Config;
import net.redstoneore.legacyfactions.entity.Board;
import net.redstoneore.legacyfactions.entity.FPlayerColl;
import net.redstoneore.legacyfactions.entity.FactionColl;

public abstract class PersistHandler {

	// -------------------------------------------------- //
	// STATIC METHODS
	// -------------------------------------------------- //
	
	public static PersistHandler getCurrent() {
		return Config.backEnd.getHandler();
	}
	
	public static PersistHandler setCurrent(PersistHandler handler, boolean convert) {
		if (convert) {
			handler.convertfrom(Config.backEnd.getHandler());
		}
		
		Config.backEnd = handler.getType();
		handler.init();
		return Config.backEnd.getHandler();
	}
	
	public static PersistHandler setCurrent(PersistHandler handler) {
		return setCurrent(handler, true);
	}

	// -------------------------------------------------- //
	// METHODS
	// -------------------------------------------------- //
	
	public void init() {
		
	}
	
	// -------------------------------------------------- //
	// ABSTRACT METHODS
	// -------------------------------------------------- //
	
	abstract public void convertfrom(PersistHandler other);
	abstract public PersistType getType();
	
	abstract public Board getBoard();
	abstract public FPlayerColl getFPlayerColl();
	abstract public FactionColl getFactionColl();
	
}
