package net.redstoneore.legacyfactions.entity.persist;

import net.redstoneore.legacyfactions.entity.Board;
import net.redstoneore.legacyfactions.entity.Conf;
import net.redstoneore.legacyfactions.entity.FPlayerColl;
import net.redstoneore.legacyfactions.entity.FactionColl;

public abstract class PersistHandler {

	// -------------------------------------------------- //
	// STATIC METHODS
	// -------------------------------------------------- //
	
	public static PersistHandler getCurrent() {
		return Conf.backEnd.getHandler();
	}
	
	public static PersistHandler setCurrent(PersistHandler handler) {
		handler.convertfrom(Conf.backEnd.getHandler());
		Conf.backEnd = handler.getType();
		handler.init();
		return Conf.backEnd.getHandler();
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
