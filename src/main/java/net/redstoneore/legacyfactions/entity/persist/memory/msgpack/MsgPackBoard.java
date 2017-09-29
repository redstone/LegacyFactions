package net.redstoneore.legacyfactions.entity.persist.memory.msgpack;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;

import com.fasterxml.jackson.core.type.TypeReference;

import net.redstoneore.legacyfactions.Factions;
import net.redstoneore.legacyfactions.entity.persist.memory.MemoryBoard;
import net.redstoneore.legacyfactions.util.DiscUtil;

public class MsgPackBoard extends MemoryBoard {

	// -------------------------------------------------- //
	// STATIC 
	// -------------------------------------------------- // 
	
	private static transient Path file = Paths.get(FactionsMsgPack.getDatabasePath().toString(), "board.msgpack");
	public static Path getMsgPackFile() { return file; }
	
	// -------------------------------------------------- //
	// METHODS 
	// -------------------------------------------------- // 
	
	public byte[] toMsgPack() {
		try {
			return Factions.get().getMsgPackObjectMapper().writeValueAsBytes(this.dumpAsSaveFormat());
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
	
	public void fromMsgPack(byte[] msgpack) {		
		try {
			Map<String, Map<String, String>> worldCoordIds = Factions.get().getMsgPackObjectMapper().readValue(msgpack, new TypeReference<Map<String, Map<String, String>>>() {});
			this.loadFromSaveFormat(worldCoordIds);
			Factions.get().log("Loaded " + this.flocationIds.size() + " board locations");	
		} catch (IOException e) {
			Factions.get().log("Failed to load board locations");	
			e.printStackTrace();
		}
	}

	public void forceSave() {
		this.forceSave(true);
	}

	public void forceSave(boolean sync) {
		try {
			DiscUtil.writeBytes(file, this.toMsgPack());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public boolean load() {
		Factions.get().log("Loading board from disk");
		
		if (!Files.exists(file)) {
			Factions.get().log("No board to load from disk. Creating new file.");
			forceSave();
			return true;
		}
		
		try {
			this.fromMsgPack(DiscUtil.readBytes(file));
		} catch (IOException e) {
			e.printStackTrace();
			Factions.get().log("Failed to load the board from disk.");
		}

		return true;
	}

	@Override
	public String getPersistType() {
		return FactionsMsgPack.get().getType().name();
	}
	

}
