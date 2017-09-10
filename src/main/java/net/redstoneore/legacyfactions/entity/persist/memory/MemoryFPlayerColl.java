package net.redstoneore.legacyfactions.entity.persist.memory;

import net.redstoneore.legacyfactions.Factions;
import net.redstoneore.legacyfactions.entity.FPlayer;
import net.redstoneore.legacyfactions.entity.FactionColl;
import net.redstoneore.legacyfactions.entity.persist.shared.SharedFPlayerColl;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentSkipListMap;

public abstract class MemoryFPlayerColl extends SharedFPlayerColl {
	
	// -------------------------------------------------- //
	// FIELDS
	// -------------------------------------------------- //
	
	public Map<String, FPlayer> fPlayers = new ConcurrentSkipListMap<>(String.CASE_INSENSITIVE_ORDER);

	// -------------------------------------------------- //
	// METHODS
	// -------------------------------------------------- //
	
	@Override
	public void clean() {
		this.fPlayers.values().stream()
			.filter(fplayer -> !FactionColl.get().isValidFactionId(fplayer.getFactionId()))
			.forEach(fplayer -> {
				 Factions.get().log("Reset faction data (invalid faction:" + fplayer.getFactionId() + ") for player " + fplayer.getName());
				 fplayer.resetFactionData();
			});
	}

	@Override
	public List<FPlayer> getAllFPlayers() {
		return new ArrayList<>(fPlayers.values());
	}
	

	@Override
	public FPlayer getById(String id) {
		FPlayer player = fPlayers.get(id);
		if (player == null) {
			player = this.generateFPlayer(id);
		}
		return player;
	}

	// -------------------------------------------------- //
	// UTIL METHODS
	// -------------------------------------------------- //
	
	public void remove(String id) {
		this.fPlayers.remove(id);
	}
	
	// -------------------------------------------------- //
	// ABSTRACT METHODS
	// -------------------------------------------------- //
	
	@Override
	public abstract void forceSave();
	
	@Override
	public abstract void loadColl();
	
	public abstract FPlayer generateFPlayer(String id);

	public abstract void convertFrom(MemoryFPlayerColl old);
	
}
