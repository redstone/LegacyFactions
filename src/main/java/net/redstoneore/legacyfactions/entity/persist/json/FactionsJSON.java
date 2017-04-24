package net.redstoneore.legacyfactions.entity.persist.json;

import org.bukkit.scheduler.BukkitRunnable;

import net.redstoneore.legacyfactions.*;
import net.redstoneore.legacyfactions.entity.Board;
import net.redstoneore.legacyfactions.entity.FPlayer;
import net.redstoneore.legacyfactions.entity.FPlayerColl;
import net.redstoneore.legacyfactions.entity.Faction;
import net.redstoneore.legacyfactions.entity.FactionColl;
import net.redstoneore.legacyfactions.entity.persist.memory.MemoryBoard;
import net.redstoneore.legacyfactions.entity.persist.memory.MemoryFPlayers;
import net.redstoneore.legacyfactions.entity.persist.memory.MemoryFactions;

import java.util.logging.Logger;

public class FactionsJSON {

    public static void convertTo() {
        new BukkitRunnable() {
            @Override
            public void run() {
                Logger logger = Factions.get().getLogger();
                logger.info("Beginning Board conversion to JSON");
                new JSONBoard().convertFrom((MemoryBoard) Board.get());
                logger.info("Board Converted");
                logger.info("Beginning FPlayers conversion to JSON");
                new JSONFPlayers().convertFrom((MemoryFPlayers) FPlayerColl.getUnsafeInstance());
                logger.info("FPlayers Converted");
                logger.info("Beginning Factions conversion to JSON");
                new JSONFactions().convertFrom((MemoryFactions) FactionColl.get());
                logger.info("Factions Converted");
                logger.info("Refreshing object caches");
                for (FPlayer fPlayer : FPlayerColl.getAll()) {
                    Faction faction = FactionColl.get().getFactionById(fPlayer.getFactionId());
                    faction.addFPlayer(fPlayer);
                }
                logger.info("Conversion Complete");
            }
        }.runTaskAsynchronously(Factions.get());
    }
}
