package net.redstoneore.legacyfactions.entity;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.function.Consumer;

import org.bukkit.OfflinePlayer;
import org.bukkit.World;
import org.bukkit.entity.Player;

import net.redstoneore.legacyfactions.config.Config;
import net.redstoneore.legacyfactions.locality.Locality;

public abstract class FactionColl {
	
	// -------------------------------------------------- //
	// STATIC FIELDS
	// -------------------------------------------------- //
	
	private static transient String currentType = null;
	protected static FactionColl instance = get();
	
	// -------------------------------------------------- //
	// STATIC METHODS
	// -------------------------------------------------- //
	
	public static FactionColl get() {
		if (currentType != Config.backEnd.name()) {
			instance = Config.backEnd.getHandler().getFactionColl();
			currentType = Config.backEnd.name();
		}
		return instance;
	}
	
	public static Faction get(Object o) {
		Faction faction = null;
		
		// CONVERT
		if (o instanceof Player) {
			o = FPlayerColl.getUnsafeInstance().getByPlayer((Player) o);
		} else if (o instanceof OfflinePlayer) {
			o = FPlayerColl.getUnsafeInstance().getByOfflinePlayer((OfflinePlayer) o);
		}
		
		// FIND
		if (o instanceof String) {
			// search by id first
			faction = get().getFactionById((String) o);
			
			if (faction != null) {
				return faction;
			}
			
			// now try its tag
			return get().getByTag((String) o);
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
		List<Faction> all = new ArrayList<>();
		
		for (Faction faction : get().getAllFactions()) {
			for (Locality location : faction.getClaims()) {
				if (location.getWorld() != world) continue;
				
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

	/**
	 * Returns a set with a snapshot of all faction tags.
	 * @return A {@link Set} with a snapshot of all faction tags.
	 */
	public abstract Set<String> getFactionTags();

	/**
	 * Returns a list with a snapshot of all factions.
	 * @return A {@link List} with a snapshot of all factions.
	 */
	public abstract List<Faction> getAllFactions();

	public abstract Faction getWilderness();

	public abstract Faction getSafeZone();

	public abstract Faction getWarZone();

	public abstract void validate();
	
	public abstract void forceSave();

	public abstract void forceSave(boolean sync);

	public abstract void load();
	
	public abstract String getPersistType();
	
	// -------------------------------------------------- //
	// DEPRECATED	
	// -------------------------------------------------- //
	
	/**
	 * Deprecated, use {@link #getWilderness()}
	 */
	@Deprecated
	public final Faction getNone() {
		return this.getWilderness();
	}

}
