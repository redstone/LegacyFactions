package com.massivecraft.legacyfactions.entity.persist.json;

import org.bukkit.scheduler.BukkitRunnable;

import com.massivecraft.legacyfactions.*;
import com.massivecraft.legacyfactions.entity.Board;
import com.massivecraft.legacyfactions.entity.FPlayer;
import com.massivecraft.legacyfactions.entity.FPlayerColl;
import com.massivecraft.legacyfactions.entity.Faction;
import com.massivecraft.legacyfactions.entity.FactionColl;
import com.massivecraft.legacyfactions.entity.persist.memory.MemoryBoard;
import com.massivecraft.legacyfactions.entity.persist.memory.MemoryFPlayers;
import com.massivecraft.legacyfactions.entity.persist.memory.MemoryFactions;

import java.util.logging.Logger;

public class FactionsJSON {

    public static void convertTo() {
        new BukkitRunnable() {
            @Override
            public void run() {
                Logger logger = Factions.get().getLogger();
                logger.info("Beginning Board conversion to JSON");
                new JSONBoard().convertFrom((MemoryBoard) Board.getInstance());
                logger.info("Board Converted");
                logger.info("Beginning FPlayers conversion to JSON");
                new JSONFPlayers().convertFrom((MemoryFPlayers) FPlayerColl.getUnsafeInstance());
                logger.info("FPlayers Converted");
                logger.info("Beginning Factions conversion to JSON");
                new JSONFactions().convertFrom((MemoryFactions) FactionColl.getInstance());
                logger.info("Factions Converted");
                logger.info("Refreshing object caches");
                for (FPlayer fPlayer : FPlayerColl.getAll()) {
                    Faction faction = FactionColl.getInstance().getFactionById(fPlayer.getFactionId());
                    faction.addFPlayer(fPlayer);
                }
                logger.info("Conversion Complete");
            }
        }.runTaskAsynchronously(Factions.get());
    }
}
