package net.redstoneore.legacyfactions.entity.persist.mysql;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import net.redstoneore.legacyfactions.Factions;
import net.redstoneore.legacyfactions.config.meta.Meta;
import net.redstoneore.legacyfactions.entity.FPlayer;
import net.redstoneore.legacyfactions.entity.persist.mysql.MySQLPrepared.ExecuteType;
import net.redstoneore.legacyfactions.entity.persist.shared.SharedFPlayerColl;

public class MySQLFPlayerColl extends SharedFPlayerColl {

	// -------------------------------------------------- //
	// FIELDS
	// -------------------------------------------------- //
	
	/**
	 * This is simply a cache of FPlayer objects, each object does their own lookups
	 */
	private transient Map<String, FPlayer> fplayerCache = new ConcurrentHashMap<>();
	
	// -------------------------------------------------- //
	// METHODS
	// -------------------------------------------------- //
	
	@Override
	public void clean() {
		
	}

	@Override
	public Collection<FPlayer> getAllFPlayers() {
		return new HashSet<>(this.fplayerCache.values());
	}

	@Override
	public void forceSave() {
		this.forceSave(true);
	}

	@Override
	public void forceSave(boolean sync) {
		// We don't need to save, everything is stored in MySQL
		// Save the meta?
		Meta.get().save();
	}

	@Override
	public FPlayer getById(String string) {
		if (!this.fplayerCache.containsKey(string)) {
			this.fplayerCache.put(string, this.createFPlayer(string));
		}
		return this.fplayerCache.get(string);
	}

	@Override
	public void loadColl() {
		this.fplayerCache.clear();
		
		List<Map<String,String>> values = FactionsMySQL.get().prepare("SELECT * FROM `fplayer`")
			.execute(ExecuteType.SELECT);
		
		if (values == null) return;
		
		values.stream()
			.filter(entry -> (entry.get("id") != null))
			.forEach(entry -> this.rawLoad(entry));
		
	}

	// -------------------------------------------------- //
	// UTIL METHODS
	// -------------------------------------------------- //
	
	/**
	 * Load a raw values into the cache, helps prevent a call to {@link #createFPlayer(String)}
	 * @param entry
	 */
	private void rawLoad(Map<String, String> entry) {
		FPlayer fplayer = new MySQLFPlayer(entry);
		this.fplayerCache.put(entry.get("id"), fplayer);
	}

	public void remove(String id) {
		this.fplayerCache.remove(id);
		
		FactionsMySQL.get().prepare("DELETE FROM `fplayer` WHERE `id` = ?")
			.setCatched(1, id)
			.execute(ExecuteType.UPDATE);
	}
	
	public FPlayer createFPlayer(String id) {
		if (FactionsMySQL.get().prepare("SELECT `id` FROM fplayer WHERE id = ?")
			.setCatched(1, id)
			.execute(ExecuteType.SELECT)
			.size() == 0) {
			
			if (FactionsMySQL.get().prepare(
					"INSERT INTO `fplayer` (`id`)" + 
					"VALUES" + 
					"	(?);")
				.setCatched(1, id)
				.execute(ExecuteType.UPDATE) == null) {
					Factions.get().warn("[MySQL] inserting fplayer row failed");
			}
		}
		
		FPlayer fplayer = new MySQLFPlayer(id);
		
		this.fplayerCache.put(fplayer.getId(), fplayer);
		return fplayer;
	}
	
	public FPlayer createFPlayer(FPlayer fplayer) {
		String id = fplayer.getId();
		
		if (FactionsMySQL.get().prepare("SELECT `id` FROM fplayer WHERE id = ?")
			.setCatched(1, id)
		.execute(ExecuteType.SELECT).size() == 0) {
			if (FactionsMySQL.get().prepare(
					"INSERT INTO `fplayer` (`id`)" + 
					"VALUES" + 
					"	(?);")
				.setCatched(1, id)
				.execute(ExecuteType.UPDATE) == null) {
					Factions.get().warn("[MySQL] inserting fplayer row from existing fplayer failed");
			}
		}
		
		FPlayer newfplayer = new MySQLFPlayer(fplayer);
		this.fplayerCache.put(fplayer.getId(), newfplayer);
		
		return newfplayer;
	}
	
	@Override
	public String getPersistType() {
		return FactionsMySQL.get().getType().name();
	}

}
