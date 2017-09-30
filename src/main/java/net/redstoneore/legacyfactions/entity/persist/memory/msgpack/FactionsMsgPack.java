package net.redstoneore.legacyfactions.entity.persist.memory.msgpack;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.bukkit.Bukkit;

import net.redstoneore.legacyfactions.Factions;
import net.redstoneore.legacyfactions.cmd.CmdFactionsSaveAll;
import net.redstoneore.legacyfactions.entity.Board;
import net.redstoneore.legacyfactions.entity.FPlayer;
import net.redstoneore.legacyfactions.entity.FPlayerColl;
import net.redstoneore.legacyfactions.entity.Faction;
import net.redstoneore.legacyfactions.entity.FactionColl;
import net.redstoneore.legacyfactions.entity.persist.PersistHandler;
import net.redstoneore.legacyfactions.entity.persist.PersistType;
import net.redstoneore.legacyfactions.entity.persist.memory.MemoryBoard;
import net.redstoneore.legacyfactions.entity.persist.memory.MemoryFPlayerColl;
import net.redstoneore.legacyfactions.entity.persist.memory.MemoryFactionColl;
import net.redstoneore.legacyfactions.entity.persist.memory.json.FactionsJSON;
import net.redstoneore.legacyfactions.entity.persist.memory.json.JSONBoard;
import net.redstoneore.legacyfactions.entity.persist.memory.json.JSONFPlayerColl;
import net.redstoneore.legacyfactions.entity.persist.memory.json.JSONFactionColl;

public class FactionsMsgPack extends PersistHandler {

	// -------------------------------------------------- //
	// INSTANCE
	// -------------------------------------------------- //
	
	private static FactionsMsgPack instance = new FactionsMsgPack();
	public static FactionsMsgPack get() { return instance; }
	
	// -------------------------------------------------- //
	// STATIC METHODS
	// -------------------------------------------------- // 
	
	public static Path getDatabasePath() {
		return Paths.get(Factions.get().getPluginFolder().toString(), "database");
	}
	
	// -------------------------------------------------- //
	// FIELDS
	// -------------------------------------------------- //
	
	private MsgPackBoard boardInstace = null;
	private MsgPackFactionColl factionCollInstance = null;
	private MsgPackFPlayerColl fplayerCollInstance = null;
	
	// -------------------------------------------------- //
	// METHODS
	// -------------------------------------------------- //
	
	@Override
	public void convertfrom(PersistHandler other) {
		if (other.getBoard() instanceof MemoryBoard) {
			Factions.get().log("Beginning Board conversion to MsgPack");
			((MemoryBoard)this.getBoard()).convertFrom((MemoryBoard) other.getBoard());
			Factions.get().log("Board Converted");
			
			Factions.get().log("Beginning FPlayerColl conversion to MsgPack");
			((MsgPackFPlayerColl)this.getFPlayerColl()).convertFrom((MemoryFPlayerColl) other.getFPlayerColl());
			Factions.get().log("FPlayerColl Converted");
			
			Factions.get().log("Beginning FactionColl conversion to MsgPack");
			((MsgPackFactionColl)this.getFactionColl()).convertFrom((MemoryFactionColl) other.getFactionColl());
			Factions.get().log("FactionColl Converted");
			
			Factions.get().log("Refreshing object caches");
			for (FPlayer fplayer : FPlayerColl.all()) {
				Faction faction = FactionColl.get().getFactionById(fplayer.getFactionId());
				faction.memberAdd(fplayer);
			}
			Factions.get().log("Conversion Complete!");
			
			// Save and notify console
			CmdFactionsSaveAll.get().perform(true, Bukkit.getConsoleSender());
			
			if (other == FactionsJSON.get()) {
				try {
					// lets print out some stats about size difference
					long jsonBoardSize = Files.size(JSONBoard.getJsonFile());
					long jsonFactionsSize = Files.size(JSONFactionColl.getJsonFile());
					long jsonPlayersSize = Files.size(JSONFPlayerColl.getJsonFile());
					long msgpackBoardSize = Files.size(MsgPackBoard.getMsgPackFile());
					long msgpackFactionsSize = Files.size(MsgPackFactionColl.getMsgPackFile());
					long msgpackPlayersSize = Files.size(MsgPackFPlayerColl.getMsgPackFile());
					
					long boardDiff = jsonBoardSize - msgpackBoardSize;
					long factionsDiff = jsonFactionsSize - msgpackFactionsSize;
					long playersDiff = jsonPlayersSize - msgpackPlayersSize;
					
					long totalDiff = boardDiff + factionsDiff + playersDiff;
					
					Factions.get().log("MsgPack format saves a lot of space compared to JSON!");
					Factions.get().log("board.json -> board.msgpack. saved: " + boardDiff + " bytes");
					Factions.get().log("factions.json -> factions.msgpack. saved: " + factionsDiff + " bytes");
					Factions.get().log("players.json -> players.msgpack. saved: " + playersDiff + " bytes");
					Factions.get().log("Total savings: " + totalDiff + " bytes");
				} catch (Exception e) {
					Factions.get().warn("Failed to get file stats :-(");
					e.printStackTrace();
				}
			}
		} else {
			// TODO
		}
	}

	@Override
	public PersistType getType() {
		return PersistType.MSGPACK;
	}

	@Override
	public Board getBoard() {
		if (this.boardInstace == null) {
			this.boardInstace = new MsgPackBoard();
		}
		return this.boardInstace;
	}

	@Override
	public FPlayerColl getFPlayerColl() {
		if (this.fplayerCollInstance == null) {
			this.fplayerCollInstance = new MsgPackFPlayerColl();
		}
		return this.fplayerCollInstance;
	}

	@Override
	public FactionColl getFactionColl() {
		if (this.factionCollInstance == null) {
			this.factionCollInstance = new MsgPackFactionColl();
		}
		return this.factionCollInstance;
	}

}
