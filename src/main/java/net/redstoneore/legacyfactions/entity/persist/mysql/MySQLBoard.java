package net.redstoneore.legacyfactions.entity.persist.mysql;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import org.bukkit.Bukkit;
import org.bukkit.World;

import net.redstoneore.legacyfactions.Factions;
import net.redstoneore.legacyfactions.entity.Faction;
import net.redstoneore.legacyfactions.entity.FactionColl;
import net.redstoneore.legacyfactions.entity.persist.mysql.MySQLPrepared.ExecuteType;
import net.redstoneore.legacyfactions.entity.persist.shared.SharedBoard;
import net.redstoneore.legacyfactions.locality.Locality;
import net.redstoneore.legacyfactions.locality.LocalityLazy;

public class MySQLBoard extends SharedBoard {

	@Override
	public void setIdAt(String id, Locality locality) {
		if (id == FactionColl.get().getWilderness().getId()) {
			this.removeAt(locality);
		}
		
		UUID world = locality.getWorldUID();
		int chunkX = locality.getChunkX();
		int chunkZ = locality.getChunkZ();
		
		if (FactionsMySQL.get().prepare("SELECT `id` FROM `board` WHERE world = ? AND x = ? AND z = ?")
				.setCatched(1, world.toString())
				.setCatched(2, chunkX)
				.setCatched(3, chunkZ)
				.execute(ExecuteType.SELECT)
				.size() == 0) {
			
			if (FactionsMySQL.get().prepare(
					"INSERT INTO `board` (`world`, `x`, `z`, `faction`)" + 
					"VALUES" + 
					"	(?, ?, ?, ?);")
					.setCatched(1, world.toString())
					.setCatched(2, chunkX)
					.setCatched(3, chunkZ)
					.setCatched(4, id)
				.execute(ExecuteType.UPDATE) == null) {
					Factions.get().warn("[MySQL] inserting claim " + locality.getCoordString() + " for " + id + " failed");
			}

		} else {
			if (FactionsMySQL.get().prepare(
					"UPDATE `board` SET `faction` = ? WHERE `world` = ? AND `x` = ? AND `z` = ?")
				.setCatched(1, id)
				.setCatched(2, world.toString())
				.setCatched(3, chunkX)
				.setCatched(4, chunkZ)
				.execute(ExecuteType.UPDATE) == null) {
					Factions.get().warn("[MySQL] updating claim " +locality.getCoordString() + " for " + id + " failed");
			}
		}
	}

	@Override
	public void removeAt(Locality locality) {
		UUID world = locality.getWorldUID();
		int chunkX = locality.getChunkX();
		int chunkZ = locality.getChunkZ();
		
		FactionsMySQL.get().prepare("DELETE FROM `board` WHERE `world` = ? AND `x` = ? AND `z` = ?")
			.setCatched(1, world.toString())
			.setCatched(2, chunkX)
			.setCatched(3, chunkZ)
			.execute(ExecuteType.UPDATE);
	}

	@Override
	public Set<Locality> getAll(String factionId) {
		Set<Locality> claims = new HashSet<>();
		
		FactionsMySQL.get().prepare("SELECT * FROM `board` WHERE `faction` = ?")
		.setCatched(1, factionId)
		.execute(ExecuteType.SELECT)
		.forEach(row -> {
			claims.add(LocalityLazy.of(Bukkit.getWorld(UUID.fromString(row.get("world"))).getName(), Integer.valueOf(row.get("x")), Integer.valueOf(row.get("z"))));
		});
		return claims;
	}

	@Override
	public Set<Locality> getAll() {
		Set<Locality> claims = new HashSet<>();
		
		FactionsMySQL.get().prepare("SELECT * FROM `board`")
			.execute(ExecuteType.SELECT)
			.forEach(row -> {
				claims.add(LocalityLazy.of(Bukkit.getWorld(UUID.fromString(row.get("world"))).getName(), Integer.valueOf(row.get("x")), Integer.valueOf(row.get("z"))));
			});
			
		return claims;
	}

	@Override
	public int getFactionCoordCount(String factionId) {
		return this.getAll(factionId).size();
	}

	@Override
	public int getFactionCoordCountInWorld(Faction faction, World world) {
		return this.getAll(faction).stream()
			.filter(loc -> loc.getWorld().getUID() == world.getUID())
			.collect(Collectors.counting()).intValue();
	}
	
	@Override
	public void clean() {
		List<Map<String,String>> values = FactionsMySQL.get().prepare("SELECT * FROM `board`")
				.execute(ExecuteType.SELECT);
		
		values.stream()
			.forEach(entry -> {
				if (!FactionColl.get().isValidFactionId(entry.get("faction"))) {
					FactionsMySQL.get().prepare("DELETE FROM `board` WHERE `id` = ?")
						.setCatched(1, Integer.valueOf(entry.get("id")))
						.execute(ExecuteType.UPDATE);
				}
			});
	}
	
	@Override
	public void clean(String factionId) {
		FactionsMySQL.get().prepare("DELETE FROM `board` WHERE `faction` = ?")
			.setCatched(1, factionId)
			.execute(ExecuteType.UPDATE);

	}

	@Override
	public void forceSave() {
		// Nothing to save
	}

	@Override
	public void forceSave(boolean sync) {
		// Nothing to save
	}

	@Override
	public boolean load() {
		return true;
	}
	
	@Override
	public String getIdAt(Locality locality) {
		UUID world = locality.getWorld().getUID();
		int chunkX = locality.getChunkX();
		int chunkZ = locality.getChunkZ();
		
		List<Map<String, String>> found = FactionsMySQL.get().prepare("SELECT `faction` FROM `board` WHERE `world` = ? AND `x` = ? AND `z` = ?")
			.setCatched(1, world.toString())
			.setCatched(2, chunkX)
			.setCatched(3, chunkZ)
				.execute(ExecuteType.SELECT);
		
		if (found == null || found.size() == 0) {
			return FactionColl.get()
					.getWilderness()
						.getId();
		}
			
		return found.get(0).get("faction");
	}
	
	@Override
	public String getPersistType() {
		return FactionsMySQL.get().getType().name();
	}

}
