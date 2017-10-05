package net.redstoneore.legacyfactions.entity.persist.mysql;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

import org.bukkit.ChatColor;

import net.redstoneore.legacyfactions.Factions;
import net.redstoneore.legacyfactions.config.Config;
import net.redstoneore.legacyfactions.config.meta.Meta;
import net.redstoneore.legacyfactions.entity.Faction;
import net.redstoneore.legacyfactions.entity.persist.mysql.MySQLPrepared.ExecuteType;
import net.redstoneore.legacyfactions.entity.persist.shared.SharedFactionColl;
import net.redstoneore.legacyfactions.entity.persist.shared.SharedFaction;
import net.redstoneore.legacyfactions.flag.Flags;
import net.redstoneore.legacyfactions.lang.Lang;
import net.redstoneore.legacyfactions.util.MiscUtil;

public class MySQLFactionColl extends SharedFactionColl {

	// -------------------------------------------------- //
	// FIELDS
	// -------------------------------------------------- //
	
	/**
	 * This is simply a cache of FPlayer objects, each object does their own lookups
	 */
	private transient Map<String, Faction> factionCache = new ConcurrentHashMap<>();
	
	// -------------------------------------------------- //
	// METHODS
	// -------------------------------------------------- //
	
	@Override
	public Faction generateFactionObject(String id) {
		if (FactionsMySQL.get().prepare("SELECT `id` FROM faction WHERE id = ?")
				.setCatched(1, id)
				.execute(ExecuteType.SELECT)
				.size() == 0) {
				
				if (FactionsMySQL.get().prepare(
						"INSERT INTO `faction` (`id`)" + 
						"VALUES" + 
						"	(?);")
					.setCatched(1, id)
					.execute(ExecuteType.UPDATE) == null) {
						Factions.get().warn("[MySQL] inserting faction row failed");
				}
			}
			
		MySQLFaction faction = new MySQLFaction(id, false);
		faction.setFoundedDate(System.currentTimeMillis());
		faction.setMaxVaults(Config.defaultMaxVaults);
		faction.setDescription(Lang.GENERIC_DEFAULTDESCRIPTION.toString());
		faction.poll(true);
			
		this.factionCache.put(id, faction);
			
		return faction;
	}
	

	public Faction generateFactionObject(Faction other) {
		if (FactionsMySQL.get().prepare("SELECT `id` FROM faction WHERE id = ?")
				.setCatched(1, other.getId())
				.execute(ExecuteType.SELECT)
				.size() == 0) {
				
				if (FactionsMySQL.get().prepare(
						"INSERT INTO `faction` (`id`)" + 
						"VALUES" + 
						"	(?);")
					.setCatched(1, other.getId())
					.execute(ExecuteType.UPDATE) == null) {
						Factions.get().warn("[MySQL] inserting faction row failed");
				}
			}
			
		MySQLFaction faction = new MySQLFaction((SharedFaction) other);
		this.factionCache.put(other.getId(), faction);
		
		return faction;
	}

	@Override
	public Faction getFactionById(String id) {
		if (id == null) return null;
		
		if (this.factionCache.containsKey(id)) {
			return this.factionCache.get(id);
		} 
		
		List<Map<String, String>> result = FactionsMySQL.get().prepare("SELECT `id` FROM faction WHERE id = ?")
			.setCatched(1, id)
			.execute(ExecuteType.SELECT);
			
		if (result == null || result.size() == 0) return null;
		
		this.rawLoad(result.get(0));
		
		return this.factionCache.get(id);
	}

	@Override
	public Faction getByTag(String tag) {
		String comparisongTag = MiscUtil.getComparisonString(tag);
		
		return this.factionCache.values().stream()
			.filter(faction -> faction != null && faction.getComparisonTag() == comparisongTag)
			.findFirst()
				.orElse(null);
	}
	
	@Override
	public boolean isValidFactionId(String id) {
		if (this.factionCache.containsKey(id)) return true;
		
		List<Map<String, String>> result = FactionsMySQL.get().prepare("SELECT `id` FROM faction WHERE id = ?")
			.setCatched(1, id)
			.execute(ExecuteType.SELECT);
			
		if (result == null || result.size() == 0) {
			return false;
		}
		
		return true;
	}

	@Override
	public Faction createFaction() {
		String id = this.getNextId();
		return this.generateFactionObject(id);
	}
	
	public String getNextId() {
		return String.valueOf((
			this.getAllFactions().stream()
				.map(faction -> Long.valueOf(faction.getId()))
				.sorted(Collections.reverseOrder())
				.collect(Collectors.toList())
					.get(0) + 1));
	}
	
	@Override
	public void removeFaction(String id) {
		if (this.factionCache.containsKey(id)) {
			this.factionCache.get(id).remove();
			this.factionCache.remove(id);
		}
	}
	
	@Override
	public ArrayList<Faction> getAllFactions() {
		return new ArrayList<>(this.factionCache.values().stream().collect(Collectors.toList()));
	}

	@Override
	public void forceSave(boolean sync) {
		Meta.get().save();
	}

	@Override
	public void load() {
		List<Map<String,String>> values = FactionsMySQL.get().prepare("SELECT * FROM `faction`")
				.execute(ExecuteType.SELECT);
		
		if (values != null) {
			values.stream()
				.filter(entry -> (entry.get("id") != null))
				.forEach(entry -> this.rawLoad(entry));			
		}
		
		// Make sure the default neutral faction exists
		if (!this.factionCache.containsKey("0")) {
			Faction faction = this.generateFactionObject("0");
			this.factionCache.put("0", faction);
			faction.setTag(Lang.WILDERNESS.toString());
			faction.setDescription(Lang.WILDERNESS_DESCRIPTION.toString());
			faction.setFlag(Flags.PERMANENT, true);
			faction.setForcedMapCharacter("-".charAt(0));
			faction.setForcedMapColour(ChatColor.DARK_GREEN);
		} else {
			Faction faction = this.factionCache.get("0");
			if (!faction.getTag().equalsIgnoreCase(Lang.WILDERNESS.toString())) {
				faction.setTag(Lang.WILDERNESS.toString());
			}
			if (!faction.getDescription().equalsIgnoreCase(Lang.WILDERNESS_DESCRIPTION.toString())) {
				faction.setDescription(Lang.WILDERNESS_DESCRIPTION.toString());
			}
		}

		// Make sure the safe zone faction exists
		if (!this.factionCache.containsKey("-1")) {
			Faction faction = this.generateFactionObject("-1");
			this.factionCache.put("-1", faction);
			faction.setTag(Lang.SAFEZONE.toString());
			faction.setDescription(Lang.SAFEZONE_DESCRIPTION.toString());
			faction.setFlag(Flags.PERMANENT, true);
			faction.setForcedMapCharacter("+".charAt(0));
			faction.setForcedMapColour(ChatColor.GOLD);
		} else {
			Faction faction = this.factionCache.get("-1");
			if (!faction.getTag().equalsIgnoreCase(Lang.SAFEZONE.toString())) {
				faction.setTag(Lang.SAFEZONE.toString());
			}
			if (!faction.getDescription().equalsIgnoreCase(Lang.SAFEZONE_DESCRIPTION.toString())) {
				faction.setDescription(Lang.SAFEZONE_DESCRIPTION.toString());
			}
		}

		// Make sure the war zone faction exists
		if (!this.factionCache.containsKey("-2")) {
			Faction faction = this.generateFactionObject("-2");
			this.factionCache.put("-2", faction);
			faction.setTag(Lang.WARZONE.toString());
			faction.setDescription(Lang.WARZONE_DESCRIPTION.toString());
			faction.setFlag(Flags.PERMANENT, true);
			faction.setForcedMapCharacter("+".charAt(0));
			faction.setForcedMapColour(ChatColor.DARK_RED);
		} else {
			Faction faction = this.factionCache.get("-2");
			if (!faction.getTag().equalsIgnoreCase(Lang.WARZONE.toString())) {
				faction.setTag(Lang.WARZONE.toString());
			}
			if (!faction.getDescription().equalsIgnoreCase(Lang.WARZONE_DESCRIPTION.toString())) {
				faction.setDescription(Lang.WARZONE_DESCRIPTION.toString());
			}
		}
	}
	
	private void rawLoad(Map<String, String> entry) {
		if (this.factionCache.containsKey(entry.get("id"))) return;
		
		MySQLFaction faction = new MySQLFaction(entry);
		this.factionCache.put(entry.get("id"), faction);
	}
	
	@Override
	public String getPersistType() {
		return FactionsMySQL.get().getType().name();
	}

	@Override
	public Faction getWilderness() {
		return this.factionCache.get("0");
	}

	@Override
	public Faction getSafeZone() {
		return this.factionCache.get("-1");
	}

	@Override
	public Faction getWarZone() {
		return this.factionCache.get("-2");
	}


}
