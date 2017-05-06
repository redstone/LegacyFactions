package net.redstoneore.legacyfactions.placeholder;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import net.redstoneore.legacyfactions.entity.Board;
import net.redstoneore.legacyfactions.entity.FPlayer;
import net.redstoneore.legacyfactions.entity.FPlayerColl;
import net.redstoneore.legacyfactions.entity.Faction;
import net.redstoneore.legacyfactions.entity.FactionColl;
import net.redstoneore.legacyfactions.placeholder.adapter.*;

public class FactionsPlaceholders {
	
	// -------------------------------------------------- //
	// INSTANCE
	// -------------------------------------------------- //
	
	private static FactionsPlaceholders i = new FactionsPlaceholders();
	public static FactionsPlaceholders get() { return i; }
	
	// -------------------------------------------------- //
	// CONSTRUCT
	// -------------------------------------------------- //
	
	public FactionsPlaceholders() {
		if (Bukkit.getPluginManager().isPluginEnabled("MVdWPlaceholderAPI")) {
			this.add(AdapterMVdWPlaceholderAPI.get());
		}
		
		if (Bukkit.getPluginManager().isPluginEnabled("PlaceholderAPI")) {
			this.add(AdapterPlaceholderAPI.get());
		}
	}
	
	// -------------------------------------------------- //
	// FIELDS
	// -------------------------------------------------- //
	
	private List<FactionsPlaceholderAdapter> adapters = new ArrayList<>();
	private List<FactionsPlaceholder> placeholders = new ArrayList<>();
	
	// -------------------------------------------------- //
	// METHODS
	// -------------------------------------------------- //
	
	public void add(FactionsPlaceholderAdapter adapter) {
		this.adapters.add(adapter);
	}
	
	public void init() {
		// FACTION OF PLAYER PLACEHOLDERS
		
		this.placeholders.add(new FactionsPlaceholder("factions_faction_id") {
			@Override
			public String get(Player player) {
				if (player == null) return null;
				return FPlayerColl.get(player).getFactionId();
			}
		});
		
		this.placeholders.add(new FactionsPlaceholder("factions_faction_name") {
			@Override
			public String get(Player player) {
				if (player == null) return null;
				return FPlayerColl.get(player).getFaction().getTag();
			}
		});
		
		this.placeholders.add(new FactionsPlaceholder("factions_faction_description") {
			@Override
			public String get(Player player) {
				if (player == null) return null;
				return FPlayerColl.get(player).getFaction().getDescription();
			}
		});
		
		this.placeholders.add(new FactionsPlaceholder("factions_faction_admin") {
			@Override
			public String get(Player player) {
				if (player == null) return null;
				return FPlayerColl.get(player).getFaction().getFPlayerAdmin().getName();
			}
		});
		
		this.placeholders.add(new FactionsPlaceholder("factions_faction_power") {
			@Override
			public String get(Player player) {
				if (player == null) return null;
				return String.valueOf(FPlayerColl.get(player).getFaction().getPower());
			}
		});
		
		this.placeholders.add(new FactionsPlaceholder("factions_faction_powermax") {
			@Override
			public String get(Player player) {
				if (player == null) return null;
				return String.valueOf(FPlayerColl.get(player).getFaction().getPowerMax());
			}
		});
		
		this.placeholders.add(new FactionsPlaceholder("factions_faction_powerboost") {
			@Override
			public String get(Player player) {
				if (player == null) return null;
				return String.valueOf(FPlayerColl.get(player).getFaction().getPowerBoost());
			}
		});
		
		this.placeholders.add(new FactionsPlaceholder("factions_faction_claims") {
			@Override
			public String get(Player player) {
				if (player == null) return null;
				return String.valueOf(FPlayerColl.get(player).getFaction().getAllClaims().size());
			}
		});
		
		this.placeholders.add(new FactionsPlaceholder("factions_faction_founded") {
			@Override
			public String get(Player player) {
				if (player == null) return null;
				return String.valueOf(FPlayerColl.get(player).getFaction().getFoundedDate());
			}
		});
		
		// PLAYER SPECIFIC PLACEHOLDERS
		
		this.placeholders.add(new FactionsPlaceholder("factions_player_role") {
			@Override
			public String get(Player player) {
				if (player == null) return null;
				return String.valueOf(FPlayerColl.get(player).getRole().toNiceName());
			}
		});
		
		this.placeholders.add(new FactionsPlaceholder("factions_player_kills") {
			@Override
			public String get(Player player) {
				if (player == null) return null;
				return String.valueOf(FPlayerColl.get(player).getKills());
			}
		});
		
		this.placeholders.add(new FactionsPlaceholder("factions_player_deaths") {
			@Override
			public String get(Player player) {
				if (player == null) return null;
				return String.valueOf(FPlayerColl.get(player).getDeaths());
			}
		});
		
		this.placeholders.add(new FactionsPlaceholder("factions_player_title") {
			@Override
			public String get(Player player) {
				if (player == null) return null;
				return FPlayerColl.get(player).getTitle();
			}
		});
		
		// LOCATION SPECIFIC PLACEHOLDERS
		
		this.placeholders.add(new FactionsPlaceholder("factions_location_relation") {
			@Override
			public String get(Player player) {
				FPlayer fplayer = FPlayerColl.get(player);
				Faction faction = Board.get().getFactionAt(fplayer.getLastStoodAt());
				
				return fplayer.getRelationTo(faction).toNiceName();
			}
		});
		
		this.placeholders.add(new FactionsPlaceholder("factions_location_faction_name") {
			@Override
			public String get(Player player) {
				FPlayer fplayer = FPlayerColl.get(player);
				Faction faction = Board.get().getFactionAt(fplayer.getLastStoodAt());
				
				return faction.getTag();
			}
		});
		
		this.placeholders.add(new FactionsPlaceholder("factions_location_faction_description") {
			@Override
			public String get(Player player) {
				FPlayer fplayer = FPlayerColl.get(player);
				Faction faction = Board.get().getFactionAt(fplayer.getLastStoodAt());
				
				return faction.getDescription();
			}
		});
		
		this.placeholders.add(new FactionsPlaceholder("factions_location_faction_founded") {
			@Override
			public String get(Player player) {
				FPlayer fplayer = FPlayerColl.get(player);
				Faction faction = Board.get().getFactionAt(fplayer.getLastStoodAt());
				
				return String.valueOf(faction.getFoundedDate());
			}
		});
		
		// GLOBAL COUNTS
		
		this.placeholders.add(new FactionsPlaceholder("factions_count_factions") {
			@Override
			public String get(Player player) {
				return String.valueOf(FactionColl.all().size());
			}
		});
		
		this.placeholders.add(new FactionsPlaceholder("factions_count_fplayers") {
			@Override
			public String get(Player player) {
				return String.valueOf(FPlayerColl.all().size());
			}
		});
		
		this.placeholders.add(new FactionsPlaceholder("factions_count_claims") {
			@Override
			public String get(Player player) {
				return String.valueOf(Board.get().getAllClaims().size());
			}
		});
	}
	
	public void adaptAll() {
		this.adapters.forEach(adapter -> this.adaptAllTo(adapter));
	}
	
	public void adaptAllTo(FactionsPlaceholderAdapter adapter) {
		if (adapter instanceof FactionsPlaceholderSingleSetup) {
			((FactionsPlaceholderSingleSetup) adapter).setup();
			return;
		}
		
		this.placeholders.forEach(placeholder -> placeholder.adapt(adapter));
	}
	
	public List<FactionsPlaceholder> getPlaceholders() {
		return this.placeholders;
	}
	
}
