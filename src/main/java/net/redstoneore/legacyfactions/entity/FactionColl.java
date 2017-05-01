package net.redstoneore.legacyfactions.entity;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.function.Consumer;

import org.bukkit.OfflinePlayer;
import org.bukkit.World;
import org.bukkit.entity.Player;

import net.redstoneore.legacyfactions.FLocation;
import net.redstoneore.legacyfactions.entity.persist.json.JSONFactions;

public abstract class FactionColl {
	
    protected static FactionColl i = getImpl();
    
    private static FactionColl getImpl() {
        switch (Conf.backEnd) {
            case JSON:
                return new JSONFactions();
        }
        return null;
    }
    
    public static FactionColl get() { return i; }
    
    
    public static Faction get(Object o) {
    	Faction faction = null;
    	
    	// CONVERT
    	if (o instanceof Player) {
    		o = FPlayerColl.instance.getByPlayer((Player) o);
    	} else if (o instanceof OfflinePlayer) {
    		o = FPlayerColl.instance.getByOfflinePlayer((OfflinePlayer) o);
    	}
    	
    	// FIND
    	if (o instanceof String) {
    		// search by id first
    		faction = i.getFactionById((String) o);
    		
    		if (faction != null) {
    			return faction;
    		}
    		
    		// now try its tag
    		return i.getByTag((String) o);
    	} else if (o instanceof FPlayer) {
    		FPlayer fplayer = (FPlayer) o;
    		return fplayer.getFaction();
    	}
    	
    	return null;
    }
    
    public static List<Faction> all() {
    	return get().getAllFactions();
    }
    
    public static void all(Consumer<? super Faction> action) {
    	get().getAllFactions().forEach(action);
    }
    
    public List<Faction> getAll(World world) {
    	List<Faction> all = new ArrayList<Faction>();
    	
    	for (Faction faction : i.getAllFactions()) {
    		for(FLocation loc : faction.getAllClaims()) {
    			if (loc.getWorld() != world) continue;
    			
    			all.add(faction);
    			break;
    		}
    	}
    	
    	return all;
    }
    
    public abstract Faction getFactionById(String id);

    public abstract Faction getByTag(String str);

    public abstract Faction getBestTagMatch(String start);

    public abstract boolean isTagTaken(String str);

    public abstract boolean isValidFactionId(String id);

    public abstract Faction createFaction();

    public abstract void removeFaction(String id);

    public abstract Set<String> getFactionTags();

    public abstract ArrayList<Faction> getAllFactions();

    public abstract Faction getWilderness();

    public abstract Faction getSafeZone();

    public abstract Faction getWarZone();

    public abstract void forceSave();

    public abstract void forceSave(boolean sync);

    public abstract void load();
    
    // -------------------------------------------------- //
    // DEPRECATED    
    // -------------------------------------------------- //
    
    /**
     * deprecated, use getWilderness
     */
    @Deprecated
    public final Faction getNone() {
    	return this.getWilderness();
    }

}
