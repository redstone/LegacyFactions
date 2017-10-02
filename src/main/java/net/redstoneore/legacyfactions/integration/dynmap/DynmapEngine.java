package net.redstoneore.legacyfactions.integration.dynmap;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.scheduler.BukkitRunnable;
import org.dynmap.DynmapAPI;
import org.dynmap.markers.AreaMarker;
import org.dynmap.markers.Marker;
import org.dynmap.markers.MarkerAPI;
import org.dynmap.markers.MarkerSet;
import org.dynmap.markers.PlayerSet;
import org.dynmap.utils.TileFlags;

import net.redstoneore.legacyfactions.Factions;
import net.redstoneore.legacyfactions.config.Config;
import net.redstoneore.legacyfactions.entity.Faction;
import net.redstoneore.legacyfactions.entity.FactionColl;
import net.redstoneore.legacyfactions.integration.dynmap.marker.TempAreaMarker;
import net.redstoneore.legacyfactions.integration.dynmap.marker.TempMarker;
import net.redstoneore.legacyfactions.integration.dynmap.marker.TempMarkerSet;
import net.redstoneore.legacyfactions.integration.dynmap.util.DynmapUtil;
import net.redstoneore.legacyfactions.locality.Locality;
import net.redstoneore.legacyfactions.placeholder.FactionsPlaceholderFaction;
import net.redstoneore.legacyfactions.placeholder.FactionsPlaceholders;
import net.redstoneore.legacyfactions.util.LazyLocation;

public class DynmapEngine extends BukkitRunnable {

	// -------------------------------------------------- //
	// INSTANCE
	// -------------------------------------------------- //
	
	private static DynmapEngine instance = new DynmapEngine();
	public static DynmapEngine get() { return instance; }
	
	// -------------------------------------------------- //
	// FIELDS
	// -------------------------------------------------- //
	
	public DynmapAPI dynmapApi;
	public MarkerAPI markerApi;
	public MarkerSet markerset;
	
	// -------------------------------------------------- //
	// METHODS
	// -------------------------------------------------- //
	
	@Override
	public void run() {
		// Make sure we're not on the main thread
		if (Bukkit.isPrimaryThread()) {
			this.cancel();
			this.runTaskTimerAsynchronously(Factions.get(), 10, 20 * 15);
			return;
		}
		// Async
		
		// If we're not enabled, issue a cleanup
		if (!Config.dynmap.enabled) {
			this.cleanup();
			return;
		}
		
		
		final Map<String, TempAreaMarker> areas = this.createAreas();
		final Map<String, Set<String>> playerSets = createPlayersets();
		final Map<String, TempMarker> homes = this.createHomes();
		
		// Go sync
		Bukkit.getScheduler().scheduleSyncDelayedTask(Factions.get(), () -> {
			
			// Sync
			this.dynmapApi = (DynmapAPI) Bukkit.getPluginManager().getPlugin("dynmap");
			if (this.dynmapApi == null) {
				Factions.get().warn("Could not retrieve the DynmapAPI.");
				return;
			}
			
			this.markerApi = this.dynmapApi.getMarkerAPI();
			if (this.markerApi == null) {
				Factions.get().warn("Could not retrieve the MarkerAPI.");
				return;
			}
			
			if (!this.updateLayer(this.createLayer())) return;
			
			this.updateHomes(homes);
			this.updateAreas(areas);
			this.updatePlayersets(playerSets);
		});

	}
	
	private boolean isVisible(Faction faction) {
		if (Config.dynmap.hiddenFactions.contains(faction.getId())) return false;
		
		if (Config.dynmap.visibleFactions.size() > 0 && !Config.dynmap.visibleFactions.contains(faction.getId())) {
			return false;
		}
		
		return true;
	}
	
	private Map<String, TempMarker> createHomes() {
		Map<String, TempMarker> homes = new HashMap<>();
		
		if (!Config.homesEnabled) return homes;
		
		FactionColl.all(faction -> {
			LazyLocation home = faction.getLazyHome();
			if (home == null) return;
						
			if (Config.dynmap.hiddenFactions.contains(faction.getId())) return;
			
			if (Config.dynmap.visibleFactions.size() > 0 && !Config.dynmap.visibleFactions.contains(faction.getId())) {
				return;
			}
			
			DynmapStyle style = DynmapUtil.getStyle(faction);
			
			String markerId = DynmapIntegration.FACTIONS_HOME + "_" + faction.getId();
			
			TempMarker marker = new TempMarker();
			marker.label = ChatColor.stripColor(faction.getTag());
			marker.world = home.getWorldName();
			marker.x = home.getX();
			marker.y = home.getY();
			marker.z = home.getZ();
			marker.iconName = style.getHomeMarker();
			marker.description = this.getDescription(faction);
			
			homes.put(markerId, marker);
		});
		
		return homes;
	}
	
	private void updateHomes(Map<String, TempMarker> homes) {
		// Put all current faction markers in a map
		Map<String, Marker> markers = new HashMap<>();
		this.markerset.getMarkers().forEach(marker -> markers.put(marker.getMarkerID(), marker));
		
		// Loop homes
		homes.entrySet().forEach(entry -> {
			String markerId = entry.getKey();
			TempMarker temp = entry.getValue();
			
			// Get Creative
			// NOTE: I remove from the map created just in the beginning of this method.
			// NOTE: That way what is left at the end will be outdated markers to remove.
			Marker marker = markers.remove(markerId);
			if (marker == null) {
				Optional<Marker> tempMarker = temp.create(this.markerApi, this.markerset, markerId);
				if (!tempMarker.isPresent()) {
					Factions.get().warn("Could not get/create the home marker " + markerId);
				}
				marker = tempMarker.get();
			} else {
				temp.update(this.markerApi, this.markerset, marker);
			}
		});
		
		markers.values().forEach(Marker::deleteMarker);
	}

	
	private TempMarkerSet createLayer() {
		// Async
		TempMarkerSet ret = new TempMarkerSet();
		ret.label = Config.dynmap.layerName;
		ret.minimumZoom = Config.dynmap.layerMinimumZoom;
		ret.priority = Config.dynmap.layerPriority;
		ret.hideByDefault = !Config.dynmap.layerVisible;
		return ret;
	}
	
	private boolean updateLayer(TempMarkerSet temp) {
		// Sync
		this.markerset = this.markerApi.getMarkerSet(DynmapIntegration.FACTIONS_MARKERSET);
		if (this.markerset == null) {
			Optional<MarkerSet> markerSet = temp.create(this.markerApi, DynmapIntegration.FACTIONS_MARKERSET);
			if (!markerSet.isPresent()) {
				Factions.get().warn("Could not create the Faction Markerset/Layer");
				return false;
			}
			this.markerset = markerSet.get();
		} else {
			temp.update(this.markerApi, this.markerset);
		}
		return true;
	}

	private Map<String, TempAreaMarker> createAreas() {
		Map<String, Map<Faction, Set<Locality>>> worldFactionChunks = createWorldFactionChunks();
		return this.createAreas(worldFactionChunks);
	}
	
	private Map<String, Map<Faction, Set<Locality>>> createWorldFactionChunks() {
		Map<String, Map<Faction, Set<Locality>>> worldFactionChunks = new HashMap<>();
		
		Bukkit.getWorlds().forEach(world -> {
			String worldName = world.getName();
			
			if (!worldFactionChunks.containsKey(worldName)) {
				worldFactionChunks.put(worldName, new ConcurrentHashMap<>());
			}
			
			Map<Faction, Set<Locality>> worldClaims = worldFactionChunks.get(worldName);
			
			FactionColl.get().getAll(world).forEach(faction -> {
				
				if (!worldClaims.containsKey(faction)) {
					worldClaims.put(faction, new HashSet<>());
				}
				
				Set<Locality> factionClaims = worldClaims.get(faction);
				
				faction.getClaims().stream()
					.filter(flocation -> flocation.getWorld().getName() == worldName)
					.forEach(factionClaims::add);
			});
		});
		
		return worldFactionChunks;
	}
	
	private Map<String, TempAreaMarker> createAreas(Map<String, Map<Faction, Set<Locality>>> worldFactionChunks) {
		Map<String, TempAreaMarker> areas = new HashMap<String, TempAreaMarker>();
		
		// For each world
		worldFactionChunks.entrySet().forEach(entry -> {
			String world = entry.getKey();
			Map<Faction, Set<Locality>> factionChunks = entry.getValue();
			factionChunks.entrySet().forEach(chunkSet -> {
				Faction faction = chunkSet.getKey();
				Set<Locality> chunks = chunkSet.getValue();
				Map<String, TempAreaMarker> worldFactionMarkers = this.createAreas(world, faction, chunks);
				areas.putAll(worldFactionMarkers);

			});
		});
		
		return areas;
	}

	private Map<String, TempAreaMarker> createAreas(String world, Faction faction, Set<Locality> chunks) {	
		Map<String, TempAreaMarker> ret = new HashMap<>();
		
		if (!this.isVisible(faction) || chunks.isEmpty()) {
			return ret;
		}
				
		// Index of polygon for given faction
		int markerIndex = 0; 

		// Create the info window
		String description = this.getDescription(faction);
		
		// Fetch Style
		DynmapStyle style = DynmapUtil.getStyle(faction);
		
		// Loop through chunks: set flags on chunk map
		TileFlags allChunkFlags = new TileFlags();
		LinkedList<Locality> allChunks = new LinkedList<>();
		
		for (Locality chunk : chunks) {
			allChunkFlags.setFlag(chunk.getChunkX(), chunk.getChunkZ(), true); // Set flag for chunk
			allChunks.addLast(chunk);
		}
		
		// Loop through until we don't find more areas
		while (allChunks != null) {
			TileFlags ourChunkFlags = null;
			LinkedList<Locality> ourChunks = null;
			LinkedList<Locality> newChunks = null;
			
			int minimumX = Integer.MAX_VALUE;
			int minimumZ = Integer.MAX_VALUE;
			for (Locality chunk : allChunks) {
				int chunkX = chunk.getChunkX();
				int chunkZ = chunk.getChunkZ();
				
				if (ourChunkFlags == null && allChunkFlags.getFlag(chunkX, chunkZ)) {
					ourChunkFlags = new TileFlags(); // Create map for shape
					ourChunks = new LinkedList<>();
					this.floodFillTarget(allChunkFlags, ourChunkFlags, chunkX, chunkZ); // Copy shape
					ourChunks.add(chunk); // Add it to our chunk list
					minimumX = chunkX;
					minimumZ = chunkZ;
				} else if (ourChunkFlags != null && ourChunkFlags.getFlag(chunkX, chunkZ)) {
					ourChunks.add(chunk);
					if (chunkX < minimumX) {
						minimumX = chunkX;
						minimumZ = chunkZ;
					} else if (chunkX == minimumX && chunkZ < minimumZ) {
						minimumZ = chunkZ;
					}
				} else {
					if (newChunks == null) newChunks = new LinkedList<>();
					newChunks.add(chunk);
				}
			}
			
			// Replace list (null if no more to process)
			allChunks = newChunks;
			
			if (ourChunkFlags == null) continue;

			// Trace outline of blocks - start from minx, minz going to x+
			int initialX = minimumX;
			int initialZ = minimumZ;
			int currentX = minimumX;
			int currentZ = minimumZ;
			Direction direction = Direction.XPLUS;
			List<int[]> linelist = new ArrayList<>();
			linelist.add(new int[]{ initialX, initialZ }); // Add start point
			while ((currentX != initialX) || (currentZ != initialZ) || (direction != Direction.ZMINUS)) {
				switch (direction) {
					case XPLUS: // Segment in X+ direction
						if (!ourChunkFlags.getFlag(currentX + 1, currentZ)) { // Right turn?
							linelist.add(new int[]{ currentX + 1, currentZ }); // Finish line
							direction = Direction.ZPLUS; // Change direction
						} else if (!ourChunkFlags.getFlag(currentX + 1, currentZ - 1)) { // Straight?
							currentX++;
						} else { // Left turn
							linelist.add(new int[]{ currentX + 1, currentZ }); // Finish line
							direction = Direction.ZMINUS;
							currentX++;
							currentZ--;
						}
					break;
					case ZPLUS: // Segment in Z+ direction
						if (!ourChunkFlags.getFlag(currentX, currentZ + 1)) { // Right turn?
							linelist.add(new int[]{ currentX + 1, currentZ + 1 }); // Finish line
							direction = Direction.XMINUS; // Change direction
						} else if (!ourChunkFlags.getFlag(currentX + 1, currentZ + 1)) { // Straight?
							currentZ++;
						} else { // Left turn
							linelist.add(new int[]{ currentX + 1, currentZ + 1 }); // Finish line
							direction = Direction.XPLUS;
							currentX++;
							currentZ++;
						}
					break;
					case XMINUS: // Segment in X- direction
						if (!ourChunkFlags.getFlag(currentX - 1, currentZ)) { // Right turn?
							linelist.add(new int[]{ currentX, currentZ + 1 }); // Finish line
							direction = Direction.ZMINUS; // Change direction
						} else if (!ourChunkFlags.getFlag(currentX - 1, currentZ + 1)) { // Straight?
							currentX--;
						} else { // Left turn
							linelist.add(new int[] { currentX, currentZ + 1 }); // Finish line
							direction = Direction.ZPLUS;
							currentX--;
							currentZ++;
						}
					break;
					case ZMINUS: // Segment in Z- direction
						if (!ourChunkFlags.getFlag(currentX, currentZ - 1)) { // Right turn?
							linelist.add(new int[]{ currentX, currentZ }); // Finish line
							direction = Direction.XPLUS; // Change direction
						} else if (!ourChunkFlags.getFlag(currentX - 1, currentZ - 1)) { // Straight?
							currentZ--;
						} else { // Left turn
							linelist.add(new int[] { currentX, currentZ }); // Finish line
							direction = Direction.XMINUS;
							currentX--;
							currentZ--;
						}
					break;
				}
			}
			
			int sz = linelist.size();
			double[] x = new double[sz];
			double[] z = new double[sz];
			for (int i = 0; i < sz; i++) {
				int[] line = linelist.get(i);
				x[i] = (double) line[0] * (double) DynmapIntegration.BLOCKS_PER_CHUNK;
				z[i] = (double) line[1] * (double) DynmapIntegration.BLOCKS_PER_CHUNK;
			}
			
			// Build information for specific area
			String markerId = "faction_" + world + "__" + faction.getId() + "__" + markerIndex;
			
			TempAreaMarker temp = new TempAreaMarker();
			temp.label = faction.getTag();
			temp.world = world;
			temp.x = x;
			temp.z = z;
			temp.description = description;
			
			temp.lineColor = style.getLineColor();
			temp.lineOpacity = style.getLineOpacity();
			temp.lineWeight = style.getLineWeight();
			
			temp.fillColor = style.getFillColor();
			temp.fillOpacity = style.getFillOpacity();
			
			temp.boost = style.getBoost();
			
			ret.put(markerId, temp);
			
			markerIndex++;
		}
		
		return ret;
	}
	
	// Thread Safe: NO
	public void updateAreas(Map<String, TempAreaMarker> areas)
	{
		// Map Current
		Map<String, AreaMarker> markers = new HashMap<>();
		this.markerset.getAreaMarkers().forEach(marker -> markers.put(marker.getMarkerID(), marker));
		
		// Loop New
		areas.entrySet().forEach(entry -> {
			String markerId = entry.getKey();
			TempAreaMarker temp = entry.getValue();
			
			AreaMarker marker = markers.remove(markerId);
			if (marker == null) {
				Optional<AreaMarker> tempMarker = temp.create(this.markerApi, this.markerset, markerId);
				if (!tempMarker.isPresent()) {
					Factions.get().warn("Could not get/create the area marker " + markerId);
				}
				marker = tempMarker.get();
			} else {
				temp.update(this.markerApi, this.markerset, marker);
			}

		});
		
		markers.values().forEach(AreaMarker::deleteMarker);
	}

	
	/**
	 * Async: true
	 */
	public void cleanup() {
		// Always run cleanup async
		if (Bukkit.isPrimaryThread()) {
			Bukkit.getScheduler().runTaskAsynchronously(Factions.get(), () -> { this.cleanup(); });
			return;
		}
		
		if (this.markerset == null) return;
		this.markerset.deleteMarkerSet();
		this.markerset = null;
	}
	
	private String getDescription(Faction faction) {
		String description = "<div class=\"regioninfo regioninfo-legacyfactions\">" + Config.dynmap.description + "</div>";
		
		for (FactionsPlaceholderFaction placeholder : FactionsPlaceholders.get().<FactionsPlaceholderFaction>getPlaceholders(FactionsPlaceholderFaction.class)) {
			description = description.replaceAll("%factions_"+placeholder.placeholder()+"%", placeholder.get(faction));
		}
		
		return description;
	}
	
	enum Direction {
		XPLUS, ZPLUS, XMINUS, ZMINUS
	};
	
	private int floodFillTarget(TileFlags source, TileFlags destination, int x, int y) {
		int cnt = 0;
		ArrayDeque<int[]> stack = new ArrayDeque<>();
		stack.push(new int[] { x, y });
		
		while (stack.isEmpty() == false) {
			int[] nxt = stack.pop();
			x = nxt[0];
			y = nxt[1];
			if (source.getFlag(x, y)) { // Set in src
				source.setFlag(x, y, false); // Clear source
				destination.setFlag(x, y, true); // Set in destination
				cnt++;
				if (source.getFlag(x + 1, y)) stack.push(new int[] { x + 1, y });
				if (source.getFlag(x - 1, y)) stack.push(new int[] { x - 1, y });
				if (source.getFlag(x, y + 1)) stack.push(new int[] { x, y + 1 });
				if (source.getFlag(x, y - 1)) stack.push(new int[] { x, y - 1 });
			}
		}
		return cnt;
	}

	public String createPlayersetId(Faction faction) {
		if (faction == null) return null;
		if (faction.isWilderness()) return null;
		String factionId = faction.getId();
		if (factionId == null) return null;
		return DynmapIntegration.FACTIONS_PLAYERSET + "_" + factionId;
	}
	
	// Thread Safe / Asynchronous: Yes
	public Set<String> createPlayerset(Faction faction) {
		if (faction == null) return null;
		if (faction.isWilderness()) return null;
		
		Set<String> playerSet = new HashSet<>();
		
		faction.getMembers().forEach(member -> {
			playerSet.add(member.getId());
			playerSet.add(member.getName());
		});
		
		return playerSet;
	}
	
	public Map<String, Set<String>> createPlayersets() {
		if (!Config.dynmap.visibilityByFaction) return null;
		
		Map<String, Set<String>> playersets = new HashMap<>();
	
		FactionColl.all(faction -> {
			String playersetId = this.createPlayersetId(faction);
			if (playersetId == null) return;
			Set<String> playerIds = this.createPlayerset(faction);
			if (playerIds == null) return;
			playersets.put(playersetId, playerIds);
		});
		return playersets;
	}
	
	private void updatePlayersets(Map<String, Set<String>> playersets) {
		// Remove
		this.markerApi.getPlayerSets().stream()
			.filter(set -> set.getSetID().startsWith(DynmapIntegration.FACTIONS_PLAYERSET))
			.filter(set -> playersets != null && playersets.containsKey(set.getSetID()))
			.forEach(PlayerSet::deleteSet);
		
		// Add / Update
		playersets.entrySet().stream()
			.forEach(entry -> {
				String setId = entry.getKey();
				Set<String> playerIds = entry.getValue();
				
				// Get Creatively
				PlayerSet set = this.markerApi.getPlayerSet(setId);
				if (set == null) {
					set = this.markerApi.createPlayerSet(
						setId,
						true, 
						playerIds,
						false
					);
				}
				if (set == null) {
					Factions.get().warn("Could not get/create the player set " + setId);
					return;
				}
				
				// Set Content
				set.setPlayers(playerIds);
			});
	}
	
}
