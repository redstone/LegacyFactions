package com.massivecraft.factions.entity.persist.json;

import com.massivecraft.factions.*;
import com.massivecraft.factions.entity.Board;
import com.massivecraft.factions.entity.FPlayer;
import com.massivecraft.factions.entity.FPlayerColl;
import com.massivecraft.factions.entity.Faction;
import com.massivecraft.factions.entity.FactionColl;
import com.massivecraft.factions.entity.persist.memory.MemoryBoard;
import com.massivecraft.factions.entity.persist.memory.MemoryFPlayers;
import com.massivecraft.factions.entity.persist.memory.MemoryFactions;

import org.bukkit.scheduler.BukkitRunnable;

import java.util.logging.Logger;

public class FactionsJSON {

    public static void convertTo() {
        if (!(FactionColl.getInstance() instanceof MemoryFactions)) {
            return;
        }
        if (!(FPlayerColl.getInstance() instanceof MemoryFPlayers)) {
            return;
        }
        if (!(Board.getInstance() instanceof MemoryBoard)) {
            return;
        }
        new BukkitRunnable() {
            @Override
            public void run() {
                Logger logger = Factions.get().getLogger();
                logger.info("Beginning Board conversion to JSON");
                new JSONBoard().convertFrom((MemoryBoard) Board.getInstance());
                logger.info("Board Converted");
                logger.info("Beginning FPlayers conversion to JSON");
                new JSONFPlayers().convertFrom((MemoryFPlayers) FPlayerColl.getInstance());
                logger.info("FPlayers Converted");
                logger.info("Beginning Factions conversion to JSON");
                new JSONFactions().convertFrom((MemoryFactions) FactionColl.getInstance());
                logger.info("Factions Converted");
                logger.info("Refreshing object caches");
                for (FPlayer fPlayer : FPlayerColl.getInstance().getAllFPlayers()) {
                    Faction faction = FactionColl.getInstance().getFactionById(fPlayer.getFactionId());
                    faction.addFPlayer(fPlayer);
                }
                logger.info("Conversion Complete");
            }
        }.runTaskAsynchronously(Factions.get());
    }
}
