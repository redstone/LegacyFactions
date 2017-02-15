package com.massivecraft.legacyfactions.entity;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.bukkit.OfflinePlayer;
import org.bukkit.World;
import org.bukkit.entity.Player;

import com.massivecraft.legacyfactions.FLocation;
import com.massivecraft.legacyfactions.entity.persist.json.JSONFactions;

public abstract class FactionColl {
    protected static FactionColl instance = getImpl();
    
    private static FactionColl getImpl() {
        switch (Conf.backEnd) {
            case JSON:
                return new JSONFactions();
        }
        return null;
    }
    
    public static FactionColl getInstance() { return instance; }
    
    
    public static Faction get(Object o) {
    	Faction faction = null;
    	
    	// Quick conversions
    	if (o instanceof Player) {
    		o = FPlayerColl.getInstance().getByPlayer((Player) o);
    	}
    	
    	if (o instanceof OfflinePlayer) {
    		o = FPlayerColl.getInstance().getByOfflinePlayer((OfflinePlayer) o);
    	}
    	
    	// String can be either id or tag
    	if (o instanceof String) {
    		// search by id first
    		faction = instance.getFactionById((String) o);
    		
    		if (faction != null) {
    			return faction;
    		}
    		
    		// now try its tag
    		return instance.getByTag((String) o);
    	}
    	
    	if (o instanceof FPlayer) {
    		FPlayer fplayer = (FPlayer) o;
    		return fplayer.getFaction();
    	}
    	
    	return null;
    }
    
    public List<Faction> getAll(World world) {
    	List<Faction> all = new ArrayList<Faction>();
    	
    	for (Faction faction : instance.getAllFactions()) {
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

    @Deprecated
    public abstract Faction getNone();

    public abstract Faction getWilderness();

    public abstract Faction getSafeZone();

    public abstract Faction getWarZone();

    public abstract void forceSave();

    public abstract void forceSave(boolean sync);


    public abstract void load();
}
