package net.redstoneore.legacyfactions.entity.persist.memory;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;

import net.redstoneore.legacyfactions.*;
import net.redstoneore.legacyfactions.entity.Board;
import net.redstoneore.legacyfactions.entity.Conf;
import net.redstoneore.legacyfactions.entity.Faction;
import net.redstoneore.legacyfactions.entity.FactionColl;
import net.redstoneore.legacyfactions.locality.Locality;
import net.redstoneore.legacyfactions.util.AsciiCompass;
import net.redstoneore.legacyfactions.util.TextUtil;
import net.redstoneore.legacyfactions.warp.FactionWarp;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.World;

import java.util.*;
import java.util.Map.Entry;

/**
 * MemoryBoard should be used carefully by developers. You should be able to do what you want
 * with the available methods in Board. If something is missing, open an issue on GitHub.<br>
 * <br>
 * Do not store references to any fields. Always use the methods available.  
 */
public abstract class MemoryBoard extends Board {
    
    public class MemoryBoardMap extends HashMap<FLocation, String> {
        private static final long serialVersionUID = -6689617828610585368L;

        Multimap<String, FLocation> factionToLandMap = HashMultimap.create();

         @Override
         public String put(FLocation floc, String factionId) {
             String previousValue = super.put(floc, factionId);
             if (previousValue != null) {
                 factionToLandMap.remove(previousValue, floc);
             }

             factionToLandMap.put(factionId, floc);
             return previousValue;
         }

         @Override
         public String remove(Object key) {
             String result = super.remove(key);
             if (result != null) {
                 FLocation floc = (FLocation) key;
                 factionToLandMap.remove(result, floc);
             }

             return result;
         }

         @Override
         public void clear() {
             super.clear();
             factionToLandMap.clear();
         }

         public int getOwnedLandCount(String factionId) {
             return factionToLandMap.get(factionId).size();
         }

         public void removeFaction(String factionId) {
             Collection<FLocation> flocations = factionToLandMap.removeAll(factionId);
             for (FLocation floc : flocations) {
                 super.remove(floc);
             }
         }
    }

    public MemoryBoardMap flocationIds = new MemoryBoardMap();
    
    //----------------------------------------------//
    // Get and Set
    //----------------------------------------------//
        
    public String getIdAt(FLocation flocation) {
        if (!flocationIds.containsKey(flocation)) {
            return "0";
        }
        
        return flocationIds.get(flocation);
    }
    
    @Override
    public String getIdAt(Locality locality) {
		FLocation flocation = new FLocation(locality.getWorld().getName(), locality.getChunkX(), locality.getChunkZ());
		return this.getIdAt(flocation);
    }


    public Faction getFactionAt(FLocation flocation) {
        return FactionColl.get().getFactionById(this.getIdAt(flocation));
    }
    
    @Override
    public Faction getFactionAt(Locality locality) {
        return FactionColl.get().getFactionById(this.getIdAt(locality));
    }

    @Override
    public void setIdAt(String id, Locality locality) {
        clearOwnershipAt(locality);

        if (id.equals("0")) {
            this.removeAt(locality);
        }

        this.flocationIds.put(new FLocation(locality.getChunk()), id);

    }
    
    @Override
    public void setIdAt(String id, FLocation flocation) {
        clearOwnershipAt(flocation);

        if (id.equals("0")) {
            removeAt(flocation);
        }

        this.flocationIds.put(flocation, id);
    }

    @Override
    public void setFactionAt(Faction faction, Locality locality) {
        this.setIdAt(faction.getId(), locality);
    }
    
    @Override
    public void setFactionAt(Faction faction, FLocation flocation) {
    		this.setIdAt(faction.getId(), flocation);
    }
    

    public void removeAt(Locality locality) {
        Faction faction = this.getFactionAt(locality);
        Collection<FactionWarp> warps = faction.warps().getAll();
        
        warps.forEach(warp -> {
            if (locality.isInChunk(Locality.of(warp.getLocation()))) {
                warp.delete();
            }
        });
        
        this.clearOwnershipAt(new FLocation(locality.getChunk()));
        flocationIds.remove(new FLocation(locality.getChunk()));
    }
    
    public void removeAt(FLocation flocation) {
        this.removeAt(Locality.of(flocation.getChunk()));
    }

    public Set<FLocation> getAllClaims(String factionId) {
        Set<FLocation> locs = new HashSet<FLocation>();
        Iterator<Entry<FLocation, String>> iter = flocationIds.entrySet().iterator();
        while (iter.hasNext()) {
            Entry<FLocation, String> entry = iter.next();
            if (entry.getValue().equals(factionId)) {
                locs.add(entry.getKey());
            }
        }
        return locs;
    }

    public Set<FLocation> getAllClaims(Faction faction) {
        return getAllClaims(faction.getId());
    }
    
    public Set<FLocation> getAllClaims() {
    	Set<FLocation> claims = new HashSet<>();
    	claims.addAll(flocationIds.keySet());
    	
    	return claims;
    }
    
    // not to be confused with claims, ownership referring to further member-specific ownership of a claim
    @Override
    public void clearOwnershipAt(Locality locality) {
        Faction faction = this.getFactionAt(locality);
        if (faction != null && faction.isNormal()) {
            faction.clearClaimOwnership(locality);
        }

    }
    
    @Override
    public void clearOwnershipAt(FLocation flocation) {
        Faction faction = getFactionAt(flocation);
        if (faction != null && faction.isNormal()) {
            faction.clearClaimOwnership(flocation);
        }
    }

    public void unclaimAll(String factionId) {
        Faction faction = FactionColl.get().getFactionById(factionId);
        if (faction != null && faction.isNormal()) {
            faction.clearAllClaimOwnership();
            faction.warps().deleteAll();
        }
        clean(factionId);
    }

    public void clean(String factionId) {
        flocationIds.removeFaction(factionId);
    }

    // Is this coord NOT completely surrounded by coords claimed by the same faction?
    // Simpler: Is there any nearby coord with a faction other than the faction here?
    
    public boolean isBorderLocation(Locality locality) {
        Faction faction = this.getFactionAt(locality);
        Locality a = locality.getRelative(1, 0);
        Locality b = locality.getRelative(-1, 0);
        Locality c = locality.getRelative(0, 1);
        Locality d = locality.getRelative(0, -1);
        return faction != this.getFactionAt(a) || faction != this.getFactionAt(b) || faction != this.getFactionAt(c) || faction != this.getFactionAt(d);

    }
    
    public boolean isBorderLocation(FLocation flocation) {
        Faction faction = getFactionAt(flocation);
        FLocation a = flocation.getRelative(1, 0);
        FLocation b = flocation.getRelative(-1, 0);
        FLocation c = flocation.getRelative(0, 1);
        FLocation d = flocation.getRelative(0, -1);
        return faction != getFactionAt(a) || faction != getFactionAt(b) || faction != getFactionAt(c) || faction != getFactionAt(d);
    }

    public boolean isConnectedLocation(Locality locality, Faction faction) {
    		Locality a = locality.getRelative(1, 0);
    		Locality b = locality.getRelative(-1, 0);
    		Locality c = locality.getRelative(0, 1);
    		Locality d = locality.getRelative(0, -1);
        return faction == this.getFactionAt(a) || faction == this.getFactionAt(b) || faction == this.getFactionAt(c) || faction == this.getFactionAt(d);

    }
    
    // Is this coord connected to any coord claimed by the specified faction?
    public boolean isConnectedLocation(FLocation flocation, Faction faction) {
        FLocation a = flocation.getRelative(1, 0);
        FLocation b = flocation.getRelative(-1, 0);
        FLocation c = flocation.getRelative(0, 1);
        FLocation d = flocation.getRelative(0, -1);
        return faction == getFactionAt(a) || faction == getFactionAt(b) || faction == getFactionAt(c) || faction == getFactionAt(d);
    }

    /**
     * Checks if there is another faction within a given radius other than Wilderness. Used for HCF feature that
     * requires a 'buffer' between factions.
     *
     * @param locality  center location.
     * @param faction   faction checking for.
     * @param radius    chunk radius to check.
     *
     * @return true if another Faction is within the radius, otherwise false.
     */
    public boolean hasFactionWithin(Locality locality, Faction faction, int radius) {
        for (int x = -radius; x <= radius; x++) {
            for (int z = -radius; z <= radius; z++) {
                if (x == 0 && z == 0) {
                    continue;
                }

                Locality relative = locality.getRelative(x, z);
                Faction other = getFactionAt(relative);

                if (other.isNormal() && other != faction) {
                    return true;
                }
            }
        }
        return false;
    }
    
    @Deprecated
    public boolean hasFactionWithin(FLocation flocation, Faction faction, int radius) {
        return this.hasFactionWithin(Locality.of(flocation.getChunk()), faction, radius);
    }

    //----------------------------------------------//
    // Cleaner. Remove orphaned foreign keys
    //----------------------------------------------//

    public void clean() {
        Iterator<Entry<FLocation, String>> iter = flocationIds.entrySet().iterator();
        while (iter.hasNext()) {
            Entry<FLocation, String> entry = iter.next();
            if (!FactionColl.get().isValidFactionId(entry.getValue())) {
                Factions.get().log("Board cleaner removed " + entry.getValue() + " from " + entry.getKey());
                iter.remove();
            }
        }
    }

    //----------------------------------------------//
    // Coord count
    //----------------------------------------------//

    public int getFactionCoordCount(String factionId) {
        return flocationIds.getOwnedLandCount(factionId);
    }

    public int getFactionCoordCount(Faction faction) {
        return getFactionCoordCount(faction.getId());
    }

    public int getFactionCoordCountInWorld(Faction faction, String worldName) {
    	return getFactionCoordCountInWorld(faction, Bukkit.getWorld(worldName));
    }
    
    public int getFactionCoordCountInWorld(Faction faction, World world) {
        String factionId = faction.getId();
        int ret = 0;
        Iterator<Entry<FLocation, String>> iter = flocationIds.entrySet().iterator();
        while (iter.hasNext()) {
            Entry<FLocation, String> entry = iter.next();
            if (entry.getValue().equals(factionId) && entry.getKey().getWorld().getUID() == world.getUID()) {
                ret += 1;
            }
        }
        return ret;
    }

    //----------------------------------------------//
    // Map generation
    //----------------------------------------------//

    /**
     * The map is relative to a coord and a faction north is in the direction of decreasing x east is in the direction
     * of decreasing z
     */
    @Override
	public ArrayList<String> getMap(Faction faction, Locality locality, double inDegrees) {
        ArrayList<String> ret = new ArrayList<String>();
        Faction factionLoc = getFactionAt(locality);
        ret.add(TextUtil.get().titleize("(" + locality.getCoordString() + ") " + factionLoc.getTag(faction)));

        int halfWidth = Conf.mapWidth / 2;
        int halfHeight = Conf.mapHeight / 2;
        Locality topLeft = locality.getRelative(-halfWidth, -halfHeight);
        int width = halfWidth * 2 + 1;
        int height = halfHeight * 2 + 1;

        if (Conf.showMapFactionKey) {
            height--;
        }

        Map<String, Character> fList = new HashMap<String, Character>();
        int chrIdx = 0;

        // For each row
        for (int dz = 0; dz < height; dz++) {
            // Draw and add that row
            String row = "";
            for (int dx = 0; dx < width; dx++) {
                if (dx == halfWidth && dz == halfHeight) {
                    row += ChatColor.AQUA + "+";
                } else {
                    Locality flocationHere = topLeft.getRelative(dx, dz);
                    Faction factionHere = getFactionAt(flocationHere);
                    Relation relation = faction.getRelationTo(factionHere);
                    
                    // Wilderness, safezone, and warzone all have forced colours and characters
                    if (factionHere.isWilderness() || factionHere.isSafeZone() || factionHere.isWarZone()) {
                        row += factionHere.getForcedMapColour() + "" + factionHere.getForcedMapCharacter();
                    } else if (factionHere == faction ||
                                       factionHere == factionLoc ||
                                       relation.isAtLeast(Relation.ALLY) ||
                                       (Conf.showNeutralFactionsOnMap && relation.equals(Relation.NEUTRAL)) ||
                                       (Conf.showEnemyFactionsOnMap && relation.equals(Relation.ENEMY))) {
                        if (!fList.containsKey(factionHere.getTag())) {
                        	if (factionHere.hasForcedMapCharacter()) {
                                fList.put(factionHere.getTag(), factionHere.getForcedMapCharacter());                        		
                        	} else {
                                fList.put(factionHere.getTag(), Conf.mapKeyChrs[Math.min(chrIdx++, Conf.mapKeyChrs.length - 1)]);                        		
                        	}
                        }
                        char mapCharacter = fList.get(factionHere.getTag());
                        
                        if (factionHere.hasForcedMapColour()){
                            row += factionHere.getForcedMapColour() + "" + mapCharacter;
                        } else {
                            row += factionHere.getColorTo(faction) + "" + mapCharacter;                        	
                        }
                    } else {
                    	// Assume wilderness
                    	row += FactionColl.get().getWilderness().getForcedMapColour() + "" + FactionColl.get().getWilderness().getForcedMapCharacter();
                    }
                }
            }
            ret.add(row);
        }

        // Get the compass
        ArrayList<String> asciiCompass = AsciiCompass.getAsciiCompass(inDegrees, ChatColor.RED, TextUtil.get().parse("<a>"));

        // Add the compass
        ret.set(1, asciiCompass.get(0) + ret.get(1).substring(3 * 3));
        ret.set(2, asciiCompass.get(1) + ret.get(2).substring(3 * 3));
        ret.set(3, asciiCompass.get(2) + ret.get(3).substring(3 * 3));

        // Add the faction key
        if (Conf.showMapFactionKey) {
            String fRow = "";
            for (String key : fList.keySet()) {
                fRow += String.format("%s%s: %s ", ChatColor.GRAY, fList.get(key), key);
            }
            ret.add(fRow);
        }

        return ret;	
    }
    
    @Override
    public ArrayList<String> getMap(Faction faction, FLocation flocation, double inDegrees) {
        return this.getMap(faction, Locality.of(flocation.getChunk()), inDegrees);
    }

    public abstract void convertFrom(MemoryBoard old);
}
