package net.redstoneore.legacyfactions.entity.persist.memory.json;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;

import net.redstoneore.legacyfactions.Factions;
import net.redstoneore.legacyfactions.entity.persist.memory.MemoryBoard;
import net.redstoneore.legacyfactions.util.DiscUtil;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;


public class JSONBoard extends MemoryBoard {
	
	// -------------------------------------------------- //
	// STATIC 
	// -------------------------------------------------- // 
	
	@JsonIgnore private static transient Path file = Paths.get(FactionsJSON.getDatabasePath().toString(), "board.json");
	public static Path getJsonFile() { return file; }
	
	// -------------------------------------------------- //
	// METHODS 
	// -------------------------------------------------- // 
	
	public String toJson() {
		try {
			return Factions.get().getObjectMapper().writeValueAsString(this.dumpAsSaveFormat());
		} catch (JsonProcessingException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	public void fromJson(String json) {		
		if (json == "{}") return;
		
		try {
			Map<String, Map<String, String>> worldCoordIds = Factions.get().getObjectMapper().readValue(json, new TypeReference<Map<String, Map<String, String>>>() {});
			this.loadFromSaveFormat(worldCoordIds);
			Factions.get().log("Loaded " + flocationIds.size() + " board locations");	
		} catch (IOException e) {
			Factions.get().log("Failed to load board locations");	
			e.printStackTrace();
		}
	}

	public void forceSave() {
		this.forceSave(true);
	}

	public void forceSave(boolean sync) {
		DiscUtil.writeCatch(file, this.toJson(), sync);
	}

	public boolean load() {
		Factions.get().log("Loading board from disk");
		
		if (!Files.exists(file)) {
			Factions.get().log("No board to load from disk. Creating new file.");
			forceSave();
			return true;
		}
		
		try {
			this.fromJson(DiscUtil.read(file));
		} catch (IOException e) {
			e.printStackTrace();
			Factions.get().log("Failed to load the board from disk.");
		}

		return true;
	}

	@Override
	public String getPersistType() {
		return FactionsJSON.get().getType().name();
	}
	
}
