package net.redstoneore.legacyfactions.entity.persist.memory;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import net.redstoneore.legacyfactions.Factions;
import net.redstoneore.legacyfactions.entity.FPlayer;
import net.redstoneore.legacyfactions.entity.FPlayerColl;
import net.redstoneore.legacyfactions.entity.FactionColl;

import java.util.*;
import java.util.concurrent.ConcurrentSkipListMap;

public abstract class MemoryFPlayers extends FPlayerColl {
	
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
	public Collection<FPlayer> getOnlinePlayers() {
		Set<FPlayer> entities = new HashSet<>();
		for (Player player : Bukkit.getServer().getOnlinePlayers()) {
			entities.add(this.getByPlayer(player));
		}
		return entities;
	}

	@Override
	public FPlayer getByPlayer(Player player) {
		return this.getById(player.getUniqueId().toString());
	}

	@Override
	public List<FPlayer> getAllFPlayers() {
		return new ArrayList<>(fPlayers.values());
	}
	
	@Override
	public FPlayer getByOfflinePlayer(OfflinePlayer player) {
		return getById(player.getUniqueId().toString());
	}

	@Override
	public FPlayer getById(String id) {
		FPlayer player = fPlayers.get(id);
		if (player == null) {
			player = generateFPlayer(id);
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

	public abstract void convertFrom(MemoryFPlayers old);
	
	
}
