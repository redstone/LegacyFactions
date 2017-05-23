package net.redstoneore.legacyfactions.placeholder;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import net.redstoneore.legacyfactions.Factions;
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
	
	private static FactionsPlaceholders i = null;
	public static FactionsPlaceholders get() { 
		if (i == null) i = new FactionsPlaceholders();
		return i;
	}
	
	// -------------------------------------------------- //
	// CONSTRUCT
	// -------------------------------------------------- //
	
	public FactionsPlaceholders() {
		if (Bukkit.getPluginManager().isPluginEnabled("MVdWPlaceholderAPI")) {
			Factions.get().log("Adapting Placeholders to MVdWPlaceholderAPI");
			this.add(AdapterMVdWPlaceholderAPI.get());
		}
		
		if (Bukkit.getPluginManager().isPluginEnabled("PlaceholderAPI")) {
			Factions.get().log("Adapting Placeholders to PlaceholderAPI");
			this.add(AdapterPlaceholderAPI.get());
		}
		
		if (Bukkit.getPluginManager().isPluginEnabled("HolographicDisplays")) {
			Factions.get().log("Adapting Placeholders to HolographicDisplays");
			this.add(AdapterHolographicDisplays.get());
		}
		
		if (Bukkit.getPluginManager().isPluginEnabled("ChatEx")) {
			Factions.get().log("Adapting Placeholders to ChatEx");
			this.add(AdapterChatEx.get());
		}
		
		if (Bukkit.getPluginManager().isPluginEnabled("HeroChat")) {
			Factions.get().log("Adapting Placeholders to HeroChat");
			this.add(AdapterHeroChat.get());
		}
		
		if (Bukkit.getPluginManager().isPluginEnabled("Legendchat")) {
			Factions.get().log("Adapting Placeholders to Legendchat");
			this.add(AdapterLegendchat.get());
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
		
		this.placeholders.add(new FactionsPlaceholder("faction_id") {
			@Override
			public String get(Player player) {
				if (player == null) return null;
				return FPlayerColl.get(player).getFactionId();
			}
		});
		
		this.placeholders.add(new FactionsPlaceholder("faction_name") {
			@Override
			public String get(Player player) {
				if (player == null) return null;
				return FPlayerColl.get(player).getFaction().getTag();
			}
		});
		
		this.placeholders.add(new FactionsPlaceholder("faction_description") {
			@Override
			public String get(Player player) {
				if (player == null) return null;
				return FPlayerColl.get(player).getFaction().getDescription();
			}
		});
		
		this.placeholders.add(new FactionsPlaceholder("faction_admin") {
			@Override
			public String get(Player player) {
				if (player == null) return null;
				return FPlayerColl.get(player).getFaction().getOwner().getName();
			}
		});
		
		this.placeholders.add(new FactionsPlaceholder("faction_power") {
			@Override
			public String get(Player player) {
				if (player == null) return null;
				return String.valueOf(FPlayerColl.get(player).getFaction().getPower());
			}
		});
		
		this.placeholders.add(new FactionsPlaceholder("faction_powermax") {
			@Override
			public String get(Player player) {
				if (player == null) return null;
				return String.valueOf(FPlayerColl.get(player).getFaction().getPowerMax());
			}
		});
		
		this.placeholders.add(new FactionsPlaceholder("faction_powerboost") {
			@Override
			public String get(Player player) {
				if (player == null) return null;
				return String.valueOf(FPlayerColl.get(player).getFaction().getPowerBoost());
			}
		});
		
		this.placeholders.add(new FactionsPlaceholder("faction_claims") {
			@Override
			public String get(Player player) {
				if (player == null) return null;
				return String.valueOf(FPlayerColl.get(player).getFaction().getAllClaims().size());
			}
		});
		
		this.placeholders.add(new FactionsPlaceholder("faction_founded") {
			@Override
			public String get(Player player) {
				if (player == null) return null;
				return String.valueOf(FPlayerColl.get(player).getFaction().getFoundedDate());
			}
		});
		
		// PLAYER SPECIFIC PLACEHOLDERS
		
		this.placeholders.add(new FactionsPlaceholder("player_role") {
			@Override
			public String get(Player player) {
				if (player == null) return null;
				return String.valueOf(FPlayerColl.get(player).getRole().toNiceName());
			}
		});
		
		this.placeholders.add(new FactionsPlaceholder("player_kills") {
			@Override
			public String get(Player player) {
				if (player == null) return null;
				return String.valueOf(FPlayerColl.get(player).getKills());
			}
		});
		
		this.placeholders.add(new FactionsPlaceholder("player_deaths") {
			@Override
			public String get(Player player) {
				if (player == null) return null;
				return String.valueOf(FPlayerColl.get(player).getDeaths());
			}
		});
		
		this.placeholders.add(new FactionsPlaceholder("player_title") {
			@Override
			public String get(Player player) {
				if (player == null) return null;
				return FPlayerColl.get(player).getTitle();
			}
		});
		
		// LOCATION SPECIFIC PLACEHOLDERS
		
		this.placeholders.add(new FactionsPlaceholder("location_relation") {
			@Override
			public String get(Player player) {
				if (player == null) return null;
				FPlayer fplayer = FPlayerColl.get(player);
				Faction faction = Board.get().getFactionAt(fplayer.getLastStoodAt());
				
				return fplayer.getRelationTo(faction).toNiceName();
			}
		});
		
		this.placeholders.add(new FactionsPlaceholder("location_faction_name") {
			@Override
			public String get(Player player) {
				if (player == null) return null;
				FPlayer fplayer = FPlayerColl.get(player);
				Faction faction = Board.get().getFactionAt(fplayer.getLastStoodAt());
				
				return faction.getTag();
			}
		});
		
		this.placeholders.add(new FactionsPlaceholder("location_faction_description") {
			@Override
			public String get(Player player) {
				if (player == null) return null;
				FPlayer fplayer = FPlayerColl.get(player);
				Faction faction = Board.get().getFactionAt(fplayer.getLastStoodAt());
				
				return faction.getDescription();
			}
		});
		
		this.placeholders.add(new FactionsPlaceholder("location_faction_founded") {
			@Override
			public String get(Player player) {
				if (player == null) return null;
				FPlayer fplayer = FPlayerColl.get(player);
				Faction faction = Board.get().getFactionAt(fplayer.getLastStoodAt());
				
				return String.valueOf(faction.getFoundedDate());
			}
		});
		
		// GLOBAL COUNTS
		
		this.placeholders.add(new FactionsPlaceholder("count_factions") {
			@Override
			public String get(Player player) {
				return String.valueOf(FactionColl.all().size());
			}
		});
		
		this.placeholders.add(new FactionsPlaceholder("count_fplayers") {
			@Override
			public String get(Player player) {
				return String.valueOf(FPlayerColl.all().size());
			}
		});
		
		this.placeholders.add(new FactionsPlaceholder("count_claims") {
			@Override
			public String get(Player player) {
				return String.valueOf(Board.get().getAllClaims().size());
			}
		});
	}
	
	public void adaptAll() {
		this.adapters.forEach(adapter -> this.adapt(adapter));
	}
	
	public void adapt(FactionsPlaceholderAdapter adapter) {
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
