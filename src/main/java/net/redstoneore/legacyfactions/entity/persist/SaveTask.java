package net.redstoneore.legacyfactions.entity.persist;

import net.redstoneore.legacyfactions.Factions;
import net.redstoneore.legacyfactions.entity.Board;
import net.redstoneore.legacyfactions.entity.FPlayerColl;
import net.redstoneore.legacyfactions.entity.FactionColl;

public class SaveTask implements Runnable {

	// -------------------------------------------------- //
	// STATIC FIELDS
	// -------------------------------------------------- //
	
	private static boolean running = false;
	
	// -------------------------------------------------- //
	// METHODS
	// -------------------------------------------------- //
		
	public void run() {
		if (!Factions.get().getAutoSave() || running) {
			return;
		}
		running = true;
		
		Factions.get().preAutoSave();
		FactionColl.get().forceSave(false);
		FPlayerColl.save(false);
		Board.get().forceSave(false);
		Factions.get().postAutoSave();
		
		running = false;
	}
	
}
