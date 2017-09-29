package net.redstoneore.legacyfactions.entity.persist.memory.msgpack;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

import com.fasterxml.jackson.core.type.TypeReference;
import com.google.common.base.Function;
import com.google.common.collect.Maps;

import net.redstoneore.legacyfactions.Factions;
import net.redstoneore.legacyfactions.entity.Faction;
import net.redstoneore.legacyfactions.entity.FactionColl;
import net.redstoneore.legacyfactions.entity.persist.memory.MemoryFaction;
import net.redstoneore.legacyfactions.entity.persist.memory.MemoryFactionColl;
import net.redstoneore.legacyfactions.util.DiscUtil;

public class MsgPackFactionColl extends MemoryFactionColl {
	
	// -------------------------------------------------- //
	// STATIC 
	// -------------------------------------------------- // 
	
	private static transient Path file = Paths.get(FactionsMsgPack.getDatabasePath().toString(), "factions.msgpack");
	public static Path getMsgPackFile() { return file; }
	
	// -------------------------------------------------- //
	// METHODS 
	// -------------------------------------------------- // 

	@Override
	public Faction generateFactionObject() {
		String id = this.getNextId();
		Faction faction = new MsgPackFaction(id);
		this.updateNextIdForId(id);
		return faction;
	}

	@Override
	public void convertFrom(MemoryFactionColl old) {
		this.factions.putAll(Maps.transformValues(old.factions, new Function<Faction, MsgPackFaction>() {
			@Override
			public MsgPackFaction apply(Faction faction) {
				return new MsgPackFaction((MemoryFaction) faction);
			}
		}));
		this.nextId = old.nextId;
		forceSave();
		FactionColl.instance = this;
		
	}

	@Override
	public Faction generateFactionObject(String string) {
		String id = this.getNextId();
		Faction faction = new MsgPackFaction(id);
		this.updateNextIdForId(id);
		return faction;
	}

	@Override
	public void forceSave(boolean sync) {
		final Map<String, MsgPackFaction> entitiesThatShouldBeSaved = new HashMap<>();
		for (Faction entity : this.factions.values()) {
			entitiesThatShouldBeSaved.put(entity.getId(), (MsgPackFaction) entity);
		}

		this.saveCore(getMsgPackFile(), entitiesThatShouldBeSaved, sync);
	}
	
	private boolean saveCore(Path target, Map<String, MsgPackFaction> entities, boolean sync) {
		try {
			DiscUtil.writeBytes(target, Factions.get().getMsgPackObjectMapper().writeValueAsBytes(entities));
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	public void load() {
		Map<String, MsgPackFaction> factions = this.loadCore();
		if (factions == null) return;
		this.factions.putAll(factions);

		super.load();
		Factions.get().log("Loaded " + factions.size() + " Factions");
	}

	private Map<String, MsgPackFaction> loadCore() {
		if (!Files.exists(file)) {
			return new HashMap<String, MsgPackFaction>();			
		}
		
		byte[] content = null;
		try {
			content = DiscUtil.readBytes(file);
		} catch (IOException e) {
			e.printStackTrace();
		}
		if (content == null) return null;
		
		
		Map<String, MsgPackFaction> data;
		
		try {
			data = Factions.get().getMsgPackObjectMapper().readValue(content, new TypeReference<Map<String, MsgPackFaction>>() {});
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
		
		this.nextId = 1;
		data.entrySet().forEach(entry -> this.updateNextIdForId(entry.getKey()));
		
		return data;
	}
	@Override
	public String getPersistType() {
		return FactionsMsgPack.get().getType().name();
	}
	
	// -------------------------------------------- //
	// ID MANAGEMENT
	// -------------------------------------------- //

	public String getNextId() {
		while (!isIdFree(this.nextId)) {
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
		} catch (Exception ignored) {
		}
	}

}
