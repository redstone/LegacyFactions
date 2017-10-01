package net.redstoneore.legacyfactions.entity.persist.memory.json;

import net.redstoneore.legacyfactions.Factions;
import net.redstoneore.legacyfactions.entity.Board;
import net.redstoneore.legacyfactions.entity.FPlayer;
import net.redstoneore.legacyfactions.entity.FPlayerColl;
import net.redstoneore.legacyfactions.entity.Faction;
import net.redstoneore.legacyfactions.entity.FactionColl;
import net.redstoneore.legacyfactions.entity.persist.PersistHandler;
import net.redstoneore.legacyfactions.entity.persist.PersistType;
import net.redstoneore.legacyfactions.entity.persist.memory.MemoryBoard;
import net.redstoneore.legacyfactions.entity.persist.memory.MemoryFPlayerColl;
import net.redstoneore.legacyfactions.entity.persist.memory.MemoryFactionColl;

import java.nio.file.Path;
import java.nio.file.Paths;

public class FactionsJSON extends PersistHandler {

	// -------------------------------------------------- //
	// INSTANCE
	// -------------------------------------------------- //
	
	private static FactionsJSON instance = new FactionsJSON();
	public static FactionsJSON get() { return instance; }
	
	// -------------------------------------------------- //
	// STATIC METHODS
	// -------------------------------------------------- // 
	
	public static Path getDatabasePath() {
		return Paths.get(Factions.get().getPluginFolder().toString(), "database");
	}
	
	// -------------------------------------------------- //
	// FIELDS
	// -------------------------------------------------- //
	
	private JSONBoard boardInstance = null;
	private JSONFPlayerColl fplayersInstance = null;
	private JSONFactionColl factionsInstance = null;
	
	// -------------------------------------------------- //
	// METHODS
	// -------------------------------------------------- //
	
	@Override
	public void convertfrom(PersistHandler other) {
		if (other.getFactionColl() instanceof MemoryFactionColl) {
			Factions.get().log("Beginning Board conversion to JSON");
			new JSONBoard().convertFrom((MemoryBoard) other.getBoard());
			Factions.get().log("Board Converted");
			
			Factions.get().log("Beginning FPlayerColl conversion to JSON");
			new JSONFPlayerColl().convertFrom((MemoryFPlayerColl) other.getFPlayerColl());
			Factions.get().log("FPlayerColl Converted");
			
			Factions.get().log("Beginning FactionColl conversion to JSON");
			new JSONFactionColl().convertFrom((MemoryFactionColl) other.getFactionColl());
			Factions.get().log("FactionColl Converted");
			
			Factions.get().log("Refreshing object caches");
			for (FPlayer fPlayer : FPlayerColl.all()) {
				Faction faction = FactionColl.get().getFactionById(fPlayer.getFactionId());
				faction.memberAdd(fPlayer);
			}
			Factions.get().log("Conversion Complete");
		} else {
			// TODO
		}
	}

	@Override
	public PersistType getType() {
		return PersistType.JSON;
	}

	@Override
	public Board getBoard() {
		if (this.boardInstance == null) {
			this.boardInstance = new JSONBoard();
		}
		return this.boardInstance;
	}

	@Override
	public FPlayerColl getFPlayerColl() {
		if (this.fplayersInstance == null) {
			this.fplayersInstance = new JSONFPlayerColl();
		}
		return this.fplayersInstance;
	}

	@Override
	public FactionColl getFactionColl() {
		if (this.factionsInstance == null) {
			this.factionsInstance = new JSONFactionColl();
		}
		return this.factionsInstance;
	}
	
}
