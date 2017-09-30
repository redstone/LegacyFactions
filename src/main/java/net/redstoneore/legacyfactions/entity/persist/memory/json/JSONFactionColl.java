package net.redstoneore.legacyfactions.entity.persist.memory.json;

import net.redstoneore.legacyfactions.Factions;
import net.redstoneore.legacyfactions.entity.Faction;
import net.redstoneore.legacyfactions.entity.FactionColl;
import net.redstoneore.legacyfactions.entity.persist.memory.MemoryFaction;
import net.redstoneore.legacyfactions.entity.persist.memory.MemoryFactionColl;
import net.redstoneore.legacyfactions.entity.persist.shared.SharedFaction;
import net.redstoneore.legacyfactions.locality.Locality;
import net.redstoneore.legacyfactions.util.DiscUtil;
import net.redstoneore.legacyfactions.util.UUIDUtil;

import org.bukkit.Bukkit;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.google.common.base.Function;
import com.google.common.collect.Maps;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.Map.Entry;
import java.util.logging.Level;

public class JSONFactionColl extends MemoryFactionColl {
	
	// -------------------------------------------------- //
	// STATIC 
	// -------------------------------------------------- // 
	
	@JsonIgnore private static transient Path file = FactionsJSON.getDatabasePath().resolve("factions.json");
	public static Path getJsonFile() { return file; }
	
	// -------------------------------------------------- //
	// CONSTRUCTORS
	// -------------------------------------------------- //

	public JSONFactionColl() {
		this.nextId = 1;
	}

	// -------------------------------------------------- //
	// METHODS 
	// -------------------------------------------------- // 
	
	public void forceSave(boolean sync) {
		final Map<String, JSONFaction> entitiesThatShouldBeSaved = new HashMap<String, JSONFaction>();
		for (Faction entity : this.factions.values()) {
			entitiesThatShouldBeSaved.put(entity.getId(), (JSONFaction) entity);
		}

		saveCore(file, entitiesThatShouldBeSaved, sync);
	}
	
	private boolean saveCore(Path target, Map<String, JSONFaction> entities, boolean sync) {
		try {
			return DiscUtil.writeCatch(target, Factions.get().getObjectMapper().writeValueAsString(entities), sync);
		} catch (JsonProcessingException e) {
			e.printStackTrace();
			return false;
		}
	}

	public void load() {
		Map<String, JSONFaction> factions = this.loadCore();
		if (factions == null) {
			return;
		}
		this.factions.putAll(factions);

		super.load();
		Factions.get().log("Loaded " + factions.size() + " Factions");
	}

	private Map<String, JSONFaction> loadCore() {
		if (!Files.exists(file)) {
			return new HashMap<>();			
		}
		
		String content = DiscUtil.readCatch(file);
		if (content == null) return null;
		
		
		if (content == "{}") return new HashMap<>();
		
		Map<String, JSONFaction> data;
		try {
			data = Factions.get().getObjectMapper().readValue(content, new TypeReference<Map<String, JSONFaction>>() {});
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
		
		this.nextId = 1;
		// Do we have any names that need updating in claims or invites?

		int needsUpdate = 0;
		for (Entry<String, JSONFaction> entry : data.entrySet()) {
			String id = entry.getKey();
			SharedFaction f = entry.getValue();
			f.setId(id);
			this.updateNextIdForId(id);
			needsUpdate += whichKeysNeedMigration(f.getInvites()).size();
			for (Set<String> keys : f.getClaimOwnership().values()) {
				needsUpdate += whichKeysNeedMigration(keys).size();
			}
		}

		if (needsUpdate > 0) {
			// We've got some converting to do!
			Bukkit.getLogger().log(Level.INFO, "Factions is now updating factions.json");

			// First we'll make a backup, because god forbid anybody heed a
			// warning
			Path oldFile = Paths.get(getJsonFile().toString(), "factions.json.old");
			try {
				Files.createFile(oldFile);
			} catch (IOException e) {
				e.printStackTrace();
			}
			saveCore(oldFile, (Map<String, JSONFaction>) data, true);
			Bukkit.getLogger().log(Level.INFO, "Backed up your old data at " + oldFile.toAbsolutePath());

			Bukkit.getLogger().log(Level.INFO, "Please wait while Factions converts " + needsUpdate + " old player names to UUID. This may take a while.");

			// Update claim ownership

			for (String string : data.keySet()) {
				Faction f = data.get(string);
				Map<Locality, Set<String>> claims = f.ownership().getAll();
				for (Locality key : claims.keySet()) {
					Set<String> set = claims.get(key);

					Set<String> list = whichKeysNeedMigration(set);

					if (list.size() > 0) {
						UUIDUtil fetcher = new UUIDUtil(new ArrayList<String>(list));
						try {
							Map<String, UUID> response = fetcher.call();
							for (String value : response.keySet()) {
								// Let's replace their old named entry with a
								// UUID key
								String id = response.get(value).toString();
								set.remove(value.toLowerCase()); // Out with the
								// old...
								set.add(id); // And in with the new
							}
						} catch (Exception e) {
							e.printStackTrace();
						}
						claims.put(key, set); // Update
					}
				}
			}

			// Update invites

			for (String string : data.keySet()) {
				Faction f = data.get(string);
				Set<String> invites = f.getInvites();
				Set<String> list = whichKeysNeedMigration(invites);

				if (list.size() > 0) {
					UUIDUtil fetcher = new UUIDUtil(new ArrayList<String>(list));
					try {
						Map<String, UUID> response = fetcher.call();
						for (String value : response.keySet()) {
							// Let's replace their old named entry with a UUID
							// key
							String id = response.get(value).toString();
							invites.remove(value.toLowerCase()); // Out with the
							// old...
							invites.add(id); // And in with the new
						}
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}

			saveCore(oldFile, (Map<String, JSONFaction>) data, true); // Update the flatfile
			Bukkit.getLogger().log(Level.INFO, "Done converting factions.json to UUID.");
		}
		return data;
	}

	private Set<String> whichKeysNeedMigration(Set<String> keys) {
		HashSet<String> list = new HashSet<String>();
		for (String value : keys) {
			if (!value.matches("[0-9a-f]{8}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{12}")) {
				// Not a valid UUID..
				if (value.matches("[a-zA-Z0-9_]{2,16}")) {
					// Valid playername, we'll mark this as one for conversion
					// to UUID
					list.add(value);
				}
			}
		}
		return list;
	}

	@Override
	public Faction generateFactionObject() {
		String id = getNextId();
		Faction faction = new JSONFaction(id);
		updateNextIdForId(id);
		return faction;
	}

	@Override
	public Faction generateFactionObject(String id) {
		Faction faction = new JSONFaction(id);
		return faction;
	}

	@Override
	public void convertFrom(MemoryFactionColl old) {
		this.factions.putAll(Maps.transformValues(old.factions, new Function<Faction, JSONFaction>() {
			@Override
			public JSONFaction apply(Faction arg0) {
				return new JSONFaction((MemoryFaction) arg0);
			}
		}));
		this.nextId = old.nextId;
		forceSave();
		FactionColl.instance = this;
	}
	
	@Override
	public String getPersistType() {
		return FactionsJSON.get().getType().name();
	}
	
}
