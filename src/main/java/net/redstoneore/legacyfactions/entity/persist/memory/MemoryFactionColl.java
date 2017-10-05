package net.redstoneore.legacyfactions.entity.persist.memory;

import net.redstoneore.legacyfactions.entity.Faction;
import net.redstoneore.legacyfactions.entity.persist.shared.SharedFactionColl;
import net.redstoneore.legacyfactions.lang.Lang;
import net.redstoneore.legacyfactions.util.MiscUtil;

import java.util.ArrayList;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * MemoryFactionColl should be used carefully by developers. You should be able to do what you want
 * with the available methods in FactionColl. If something is missing, open an issue on GitHub.<br>
 * <br>
 * Do not store references to any fields. Always use the methods available.  
 */
public abstract class MemoryFactionColl extends SharedFactionColl {
	
	// -------------------------------------------------- //
	// FIELDS
	// -------------------------------------------------- //
	
	public Map<String, Faction> factions = new ConcurrentHashMap<>();
	public int nextId = 1;

	// -------------------------------------------------- //
	// METHODS
	// -------------------------------------------------- //

	@Override
	public void load() {
		// Make sure the default neutral faction exists
		if (!this.factions.containsKey("0")) {
			Faction faction = this.generateFactionObject("0");
			this.factions.put("0", faction);
			faction.setTag(Lang.WILDERNESS.toString());
			faction.setDescription(Lang.WILDERNESS_DESCRIPTION.toString());
		} else {
			Faction faction = this.factions.get("0");
			if (faction.getTag() == null || !faction.getTag().equalsIgnoreCase(Lang.WILDERNESS.toString())) {
				faction.setTag(Lang.WILDERNESS.toString());
			}
			if (faction.getDescription() == null || !faction.getDescription().equalsIgnoreCase(Lang.WILDERNESS_DESCRIPTION.toString())) {
				faction.setDescription(Lang.WILDERNESS_DESCRIPTION.toString());
			}
		}

		// Make sure the safe zone faction exists
		if (!this.factions.containsKey("-1")) {
			Faction faction = this.generateFactionObject("-1");
			this.factions.put("-1", faction);
			faction.setTag(Lang.SAFEZONE.toString());
			faction.setDescription(Lang.SAFEZONE_DESCRIPTION.toString());
		} else {
			Faction faction = this.factions.get("-1");
			if (faction.getTag() == null || !faction.getTag().equalsIgnoreCase(Lang.SAFEZONE.toString())) {
				faction.setTag(Lang.SAFEZONE.toString());
			}
			if (faction.getDescription() == null || !faction.getDescription().equalsIgnoreCase(Lang.SAFEZONE_DESCRIPTION.toString())) {
				faction.setDescription(Lang.SAFEZONE_DESCRIPTION.toString());
			}
		}

		// Make sure the war zone faction exists
		if (!this.factions.containsKey("-2")) {
			Faction faction = this.generateFactionObject("-2");
			this.factions.put("-2", faction);
			faction.setTag(Lang.WARZONE.toString());
			faction.setDescription(Lang.WARZONE_DESCRIPTION.toString());
		} else {
			Faction faction = this.factions.get("-2");
			if (faction.getTag() == null || !faction.getTag().equalsIgnoreCase(Lang.WARZONE.toString())) {
				faction.setTag(Lang.WARZONE.toString());
			}
			if (faction.getDescription() == null || !faction.getDescription().equalsIgnoreCase(Lang.WARZONE_DESCRIPTION.toString())) {
				faction.setDescription(Lang.WARZONE_DESCRIPTION.toString());
			}
		}
	}

	@Override
	public Faction getFactionById(String id) {
		return this.factions.get(id);
	}

	@Override
	public Faction getByTag(String str) {
		String comparisonString = MiscUtil.getComparisonString(str);
		
		return this.factions.values().stream()
			.filter(faction -> faction.getComparisonTag().equals(comparisonString))
			.findFirst()
			.orElse(null);
	}

	@Override
	public boolean isValidFactionId(String id) {
		return this.factions.containsKey(id);
	}

	@Override
	public Faction createFaction() {
		Faction faction = generateFactionObject();
		this.factions.put(faction.getId(), faction);
		return faction;
	}

	@Override
	public void removeFaction(String id) {
		this.factions.remove(id).remove();
	}

	@Override
	public ArrayList<Faction> getAllFactions() {
		return new ArrayList<>(this.factions.values());
	}

	@Override
	public Faction getWilderness() {
		return this.factions.get("0");
	}

	@Override
	public Faction getSafeZone() {
		return this.factions.get("-1");
	}

	@Override
	public Faction getWarZone() {
		return this.factions.get("-2");
	}

	public abstract Faction generateFactionObject();
	
	public abstract void convertFrom(MemoryFactionColl old);
	
	// -------------------------------------------- //
	// ID MANAGEMENT
	// -------------------------------------------- //

	public String getNextId() {
		while (!this.isIdFree(this.nextId)) {
			this.nextId += 1;
		}
		return Integer.toString(this.nextId);
	}

	public boolean isIdFree(String id) {
		return !this.factions.containsKey(id);
	}

	public boolean isIdFree(int id) {
		return this.isIdFree(Integer.toString(id));
	}

	protected synchronized void updateNextIdForId(int id) {
		if (this.nextId < id) {
			this.nextId = id + 1;
		}
	}

	protected void updateNextIdForId(String id) {
		try {
			int idAsInt = Integer.parseInt(id);
			this.updateNextIdForId(idAsInt);
		} catch (Exception e) {
			
		}
	}
}
