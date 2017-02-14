package com.massivecraft.factions.entity;

import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import com.massivecraft.factions.entity.persist.json.JSONFPlayers;

import java.util.Collection;

public abstract class FPlayerColl {
    protected static FPlayerColl instance = getFPlayersImpl();

    public abstract void clean();

    public static FPlayerColl getInstance() {
        return instance;
    }

    private static FPlayerColl getFPlayersImpl() {
        switch (Conf.backEnd) {
            case JSON:
                return new JSONFPlayers();
        }
        return null;
    }

    public abstract Collection<FPlayer> getOnlinePlayers();

    public abstract FPlayer getByPlayer(Player player);

    public abstract Collection<FPlayer> getAllFPlayers();

    public abstract void forceSave();

    public abstract void forceSave(boolean sync);

    public abstract FPlayer getByOfflinePlayer(OfflinePlayer player);

    public abstract FPlayer getById(String string);

    public abstract void load();
}
