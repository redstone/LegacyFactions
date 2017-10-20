package net.redstoneore.legacyfactions.placeholder;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import net.redstoneore.legacyfactions.Factions;
import net.redstoneore.legacyfactions.Relation;
import net.redstoneore.legacyfactions.Role;
import net.redstoneore.legacyfactions.config.Config;
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
		this.shouldEnable("MVdWPlaceholderAPI", AdapterMVdWPlaceholderAPI.class);
		this.shouldEnable("PlaceholderAPI", AdapterPlaceholderAPI.class);
		this.shouldEnable("HolographicDisplays", AdapterHolographicDisplays.class);
		this.shouldEnable("ChatEx", AdapterChatEx.class);
		this.shouldEnable("HeroChat", AdapterHeroChat.class);
		this.shouldEnable("Legendchat", AdapterLegendchat.class);
	}
	
	// -------------------------------------------------- //
	// FIELDS
	// -------------------------------------------------- //
	
	private List<FactionsPlaceholdersAdapter> adapters = new ArrayList<>();
	private List<FactionsPlaceholder> placeholders = new ArrayList<>();
	
	// -------------------------------------------------- //
	// METHODS
	// -------------------------------------------------- //
	
	public void shouldEnable(String pluginName, Class<? extends FactionsPlaceholdersAdapter> adapterClass) {
		if (Bukkit.getPluginManager().isPluginEnabled(pluginName)) {
			Factions.get().log("Adapting Placeholders to " + pluginName);
			
			try {
				this.add((FactionsPlaceholdersAdapter) adapterClass.getMethod("get").invoke(this));
			} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException | NoSuchMethodException | SecurityException e) {
				Factions.get().log("Failed to adapt placeholders to " + pluginName);
				e.printStackTrace();
			}
		}
	}
	
	public void add(FactionsPlaceholdersAdapter adapter) {
		this.adapters.add(adapter);
	}
	
	public void init() {
		// FACTION OF PLAYER PLACEHOLDERS
		
		this.placeholders.add(new FactionsPlaceholderFaction("faction_id") {
			@Override
			public String get(Faction faction) {
				return faction.getId();
			}
		});
		
		this.placeholders.add(new FactionsPlaceholderFaction("faction_name") {
			@Override
			public String get(Faction faction) {
				return faction.getTag();
			}
		});
		
		this.placeholders.add(new FactionsPlaceholderFaction("faction_name_blankwild") {
			@Override
			public String get(Faction faction) {
				if (faction.isWilderness()) return "";
				return faction.getTag();
			}
		});
		
		this.placeholders.add(new FactionsPlaceholderFaction("faction_description") {
			@Override
			public String get(Faction faction) {
				return faction.getDescription();
			}
		});
		
		this.placeholders.add(new FactionsPlaceholderFaction("faction_description_blankwild") {
			@Override
			public String get(Faction faction) {
				if (faction.isWilderness()) return "";
				return faction.getDescription();
			}
		});
		
		this.placeholders.add(new FactionsPlaceholderFaction("faction_emblem") {
			@Override
			public String get(Faction faction) {
				return faction.getEmblem();
			}
		});
		
		this.placeholders.add(new FactionsPlaceholderFaction("faction_admin") {
			@Override
			public String get(Faction faction) {
				if (faction == null || faction.getOwner() == null) {
					return "none";
				}
				
				return faction.getOwner().getName();
			}
		});
		
		this.placeholders.add(new FactionsPlaceholderFaction("faction_power") {
			@Override
			public String get(Faction faction) {
				return String.valueOf(faction.getPower());
			}
		});
		
		this.placeholders.add(new FactionsPlaceholderFaction("faction_powermax") {
			@Override
			public String get(Faction faction) {
				return String.valueOf(faction.getPowerMax());
			}
		});
		
		this.placeholders.add(new FactionsPlaceholderFaction("faction_powerboost") {
			@Override
			public String get(Faction faction) {
				return String.valueOf(faction.getPowerBoost());
			}
		});
		
		// This placeholder is deprecated/unlisted
		this.placeholders.add(new FactionsPlaceholderFaction("faction_claims") {
			@Override
			public String get(Faction faction) {
				return String.valueOf(faction.getClaims().size());
			}
		});
		
		this.placeholders.add(new FactionsPlaceholderFaction("faction_founded") {
			@Override
			public String get(Faction faction) {
				return String.valueOf(faction.getFoundedDate());
			}
		});
		
		this.placeholders.add(new FactionsPlaceholderFaction("faction_count_members") {
			@Override
			public String get(Faction faction) {
				return String.valueOf(faction.getMembers().size());
			}
		});
		
		this.placeholders.add(new FactionsPlaceholderFaction("faction_count_members_coleaders") {
			@Override
			public String get(Faction faction) {
				return String.valueOf(faction.getWhereRole(Role.COLEADER).size());
			}
		});
		
		this.placeholders.add(new FactionsPlaceholderFaction("faction_count_members_normal") {
			@Override
			public String get(Faction faction) {
				return String.valueOf(faction.getWhereRole(Role.NORMAL).size());
			}
		});
		
		this.placeholders.add(new FactionsPlaceholderFaction("faction_count_members_moderator") {
			@Override
			public String get(Faction faction) {
				return String.valueOf(faction.getWhereRole(Role.MODERATOR).size());
			}
		});
		
		this.placeholders.add(new FactionsPlaceholderFaction("faction_count_claims") {
			@Override
			public String get(Faction faction) {
				return String.valueOf(faction.getClaims().size());
			}
		});
		
		this.placeholders.add(new FactionsPlaceholderFaction("faction_home_x") {
			@Override
			public String get(Faction faction) {
				return String.valueOf(faction.getLazyHome().getX(2));
			}
		});
		
		this.placeholders.add(new FactionsPlaceholderFaction("faction_home_y") {
			@Override
			public String get(Faction faction) {
				return String.valueOf(faction.getLazyHome().getY(2));
			}
		});
		
		this.placeholders.add(new FactionsPlaceholderFaction("faction_home_z") {
			@Override
			public String get(Faction faction) {
				return String.valueOf(faction.getLazyHome().getZ(2));
			}
		});
		
		// PLAYER SPECIFIC PLACEHOLDERS
		
		this.placeholders.add(new FactionsPlaceholder("player_name") {
			@Override
			public String get(Player player) {
				if (player == null) return null;
				return String.valueOf(FPlayerColl.get(player).getName());
			}
		});
		
		this.placeholders.add(new FactionsPlaceholder("player_role") {
			@Override
			public String get(Player player) {
				if (player == null) return null;
				return String.valueOf(FPlayerColl.get(player).getRole().toNiceName());
			}
		});
		
		this.placeholders.add(new FactionsPlaceholder("player_role_prefix") {
			@Override
			public String get(Player player) {
				if (player == null) return null;
				return String.valueOf(FPlayerColl.get(player).getRole().getPrefix());
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
		
		this.placeholders.add(new FactionsPlaceholder("player_power") {
			@Override
			public String get(Player player) {
				if (player == null) return null;
				return String.valueOf(FPlayerColl.get(player).getPower());
			}
		});
		
		this.placeholders.add(new FactionsPlaceholderRelation("player_chattag") {
			
			@Override
			public String get(Player player) {
				if (player == null) return null;
				return FPlayerColl.get(player).getChatTag();
			}

			@Override
			public String get(Player one, Player two) {
				return FPlayerColl.get(one).getChatTag(FPlayerColl.get(two));
			}
		});
		
		// LOCATION SPECIFIC PLACEHOLDERS
		
		this.placeholders.add(new FactionsPlaceholder("location_relation") {
			@Override
			public String get(Player player) {
				if (player == null) return null;
				FPlayer fplayer = FPlayerColl.get(player);
				Faction faction = Board.get().getFactionAt(fplayer.getLastLocation());
				
				return fplayer.getRelationTo(faction).toNiceName();
			}
		});
		
		this.placeholders.add(new FactionsPlaceholder("location_relation_colour") {
			@Override
			public String get(Player player) {
				if (player == null) return null;
				FPlayer fplayer = FPlayerColl.get(player);
				Faction faction = Board.get().getFactionAt(fplayer.getLastLocation());
				
				return fplayer.getRelationTo(faction).getColor() + "";
			}
		});
		
		this.placeholders.add(new FactionsPlaceholder("location_faction_name") {
			@Override
			public String get(Player player) {
				if (player == null) return null;
				FPlayer fplayer = FPlayerColl.get(player);
				Faction faction = Board.get().getFactionAt(fplayer.getLastLocation());
				
				return faction.getTag();
			}
		});
		
		this.placeholders.add(new FactionsPlaceholder("location_faction_description") {
			@Override
			public String get(Player player) {
				if (player == null) return null;
				FPlayer fplayer = FPlayerColl.get(player);
				Faction faction = Board.get().getFactionAt(fplayer.getLastLocation());
				
				return faction.getDescription();
			}
		});
		
		this.placeholders.add(new FactionsPlaceholder("location_faction_founded") {
			@Override
			public String get(Player player) {
				if (player == null) return null;
				FPlayer fplayer = FPlayerColl.get(player);
				Faction faction = Board.get().getFactionAt(fplayer.getLastLocation());
				
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
				return String.valueOf(Board.get().getAll().size());
			}
		});
		
		// RELATION
		
		this.placeholders.add(new FactionsPlaceholderRelation("relation_colour") {
			
			@Override
			public String get(Player player) {
				return "";
			}

			@Override
			public String get(Player one, Player two) {
				return FPlayerColl.get(one).getRelationTo(FPlayerColl.get(two)).getColor() + "";
			}
		});

		this.placeholders.add(new FactionsPlaceholderRelation("relation_color") {
			
			@Override
			public String get(Player player) {
				return "";
			}

			@Override
			public String get(Player one, Player two) {
				return FPlayerColl.get(one).getRelationTo(FPlayerColl.get(two)).getColor() + "";
			}
		});
		
		this.placeholders.add(new FactionsPlaceholderRelation("relation") {
			
			@Override
			public String get(Player player) {
				return "";
			}

			@Override
			public String get(Player one, Player two) {
				return FPlayerColl.get(one).getRelationTo(FPlayerColl.get(two)).toNiceName();
			}
		});

		this.placeholders.add(new FactionsPlaceholderRelation("is_enemy") {
			
			@Override
			public String get(Player player) {
				return "unknown";
			}

			@Override
			public String get(Player one, Player two) {
				return (FPlayerColl.get(one).getRelationTo(FPlayerColl.get(two)) == Relation.ENEMY) ? "true" : "false";
			}
		});
		
		this.placeholders.add(new FactionsPlaceholderRelation("is_ally") {
			
			@Override
			public String get(Player player) {
				return "unknown";
			}

			@Override
			public String get(Player one, Player two) {
				return (FPlayerColl.get(one).getRelationTo(FPlayerColl.get(two)) == Relation.ALLY) ? "true" : "false";
			}
		});
		
		this.placeholders.add(new FactionsPlaceholderRelation("is_truce") {
			
			@Override
			public String get(Player player) {
				return "unknown";
			}
		
			@Override
			public String get(Player one, Player two) {
				return (FPlayerColl.get(one).getRelationTo(FPlayerColl.get(two)) == Relation.TRUCE && Config.enableTruces) ? "true" : "false";
			}
		});
		
		this.placeholders.add(new FactionsPlaceholderRelation("is_neutral") {
			
			@Override
			public String get(Player player) {
				return "unknown";
			}
		
			@Override
			public String get(Player one, Player two) {
				return (FPlayerColl.get(one).getRelationTo(FPlayerColl.get(two)) == Relation.NEUTRAL) ? "true" : "false";
			}
		});
		
		this.placeholders.add(new FactionsPlaceholderRelation("is_member") {
			
			@Override
			public String get(Player player) {
				return "unknown";
			}
		
			@Override
			public String get(Player one, Player two) {
				return (FPlayerColl.get(one).getRelationTo(FPlayerColl.get(two)) == Relation.MEMBER) ? "true" : "false";
			}
		});
		
	}
	
	public void adaptAll() {
		this.adapters.forEach(adapter -> this.adapt(adapter));
	}
	
	public void adapt(FactionsPlaceholdersAdapter adapter) {
		if (adapter instanceof FactionsPlaceholderSingleSetup) {
			((FactionsPlaceholderSingleSetup) adapter).setup();
			return;
		}
		
		this.placeholders.forEach(placeholder -> placeholder.adapt(adapter));
	}
	
	/**
	 * Returns an unmodifiable list of all placeholders. 
	 * @return List containing placeholders
	 */
	public List<FactionsPlaceholder> getPlaceholders() {
		return Collections.unmodifiableList(this.placeholders);
	}
	
	@SuppressWarnings("unchecked")
	public <T>List<T> getPlaceholders(Class<? extends FactionsPlaceholder> type) {
		if (type == FactionsPlaceholder.class) {
			return (List<T>) Collections.unmodifiableList(this.placeholders);
		}
		
		if (type == FactionsPlaceholderFaction.class) {
			return (List<T>) this.placeholders.stream()
				.filter(placeholder -> placeholder instanceof FactionsPlaceholderFaction)
				.collect(Collectors.toList());
		}
		
		if (type == FactionsPlaceholderRelation.class) {
			return (List<T>) this.placeholders.stream()
				.filter(placeholder -> placeholder instanceof FactionsPlaceholderRelation)
				.collect(Collectors.toList());
		}
		
		
		return null;
	}
	
	/**
	 * Parse a string with FactionsPlaceholders 
	 * @param player Player parsing for (can be null for none).
	 * @param string String to parse.
	 * @return parsed string.
	 */
	public String parse(Player player, String string) {
		for (FactionsPlaceholder placeholder : this.getPlaceholders()) {
			if (string.contains("{factions_" + placeholder.placeholder() + "}")) {
				string = string.replace("{factions_" + placeholder.placeholder() + "}", placeholder.get(player)+"");
			}
		}
		return string;
	}
	
	/**
	 * Parse a string with FactionsPlaceholders using relations if possible
	 * @param player1 Player apart of this relationship.
	 * @param player2 Player apart of this relationship.
	 * @param string String to parse.
	 * @return parsed string.
	 */
	public String parse(Player player1, Player player2, String string) {
		for (FactionsPlaceholder placeholder : this.getPlaceholders()) {
			if (placeholder instanceof FactionsPlaceholderRelation) {
				FactionsPlaceholderRelation placeholderRel = (FactionsPlaceholderRelation) placeholder;
				
				if (string.contains("{rel_factions_" + placeholderRel.placeholder() + "}")) {
					string = string.replace("{rel_factions_" + placeholderRel.placeholder() + "}", placeholderRel.get(player1, player2)+"");
				}
			} 
			
			if (string.contains("{factions_" + placeholder.placeholder() + "}")) {
				string = string.replace("{factions_" + placeholder.placeholder() + "}", placeholder.get(player1)+"");
			}
		}
		return string;
	}
	
	/**
	 * Parse a string with FactionsPlaceholders 
	 * @param fplayer FPlayer parsing for (can be null for none).
	 * @param string String to parse.
	 * @return parsed string.
	 */
	public String parse(FPlayer fplayer, String string) {
		if (fplayer == null) return this.parse((Player)null, string);
		return this.parse(fplayer.getPlayer(), string);
	}

}
