package net.redstoneore.legacyfactions.entity.persist.memory.json;

import org.bukkit.scheduler.BukkitRunnable;

import net.redstoneore.legacyfactions.*;
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
import java.util.logging.Logger;

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
	
	@Deprecated
    public static void convertTo() {
        new BukkitRunnable() {
            @Override
            public void run() {
                Logger logger = Factions.get().getLogger();
                logger.info("Beginning Board conversion to JSON");
                new JSONBoard().convertFrom((MemoryBoard) Board.get());
                logger.info("Board Converted");
                logger.info("Beginning FPlayers conversion to JSON");
                new JSONFPlayers().convertFrom((MemoryFPlayerColl) FPlayerColl.getUnsafeInstance());
                logger.info("FPlayers Converted");
                logger.info("Beginning Factions conversion to JSON");
                new JSONFactions().convertFrom((MemoryFactionColl) FactionColl.get());
                logger.info("Factions Converted");
                logger.info("Refreshing object caches");
                for (FPlayer fPlayer : FPlayerColl.all()) {
                    Faction faction = FactionColl.get().getFactionById(fPlayer.getFactionId());
                    faction.addFPlayer(fPlayer);
                }
                logger.info("Conversion Complete");
            }
        }.runTaskAsynchronously(Factions.get());
    }

	// -------------------------------------------------- //
	// FIELDS
	// -------------------------------------------------- //
	
	private JSONBoard boardInstance = new JSONBoard();
	private JSONFPlayers fplayersInstance = new JSONFPlayers();
	private JSONFactions factionsInstance = new JSONFactions();
	
	// -------------------------------------------------- //
	// METHODS
	// -------------------------------------------------- //
	
	@Override
	public void convertfrom(PersistHandler other) {
		
	}

	@Override
	public PersistType getType() {
		return PersistType.JSON;
	}

	@Override
	public Board getBoard() {
		return this.boardInstance;
	}

	@Override
	public FPlayerColl getFPlayerColl() {
		return this.fplayersInstance;
	}

	@Override
	public FactionColl getFactionColl() {
		return this.factionsInstance;
	}
    
}
