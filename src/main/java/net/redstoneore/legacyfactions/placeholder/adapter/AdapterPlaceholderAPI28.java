package net.redstoneore.legacyfactions.placeholder.adapter;

import org.bukkit.entity.Player;

import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import me.clip.placeholderapi.expansion.Relational;
import net.redstoneore.legacyfactions.Factions;
import net.redstoneore.legacyfactions.placeholder.FactionsPlaceholder;
import net.redstoneore.legacyfactions.placeholder.FactionsPlaceholderRelation;
import net.redstoneore.legacyfactions.placeholder.FactionsPlaceholders;

public class AdapterPlaceholderAPI28 extends PlaceholderExpansion implements Relational  {
	
	// -------------------------------------------------- //
	// INSTANCE
	// -------------------------------------------------- //
	
	private static AdapterPlaceholderAPI28 i = new AdapterPlaceholderAPI28();
	public static AdapterPlaceholderAPI28 get() { return i; }
	
	// -------------------------------------------------- //
	// METHODS
	// -------------------------------------------------- //
	
	@Override
	public String onPlaceholderRequest(Player player1, Player player2, String identifier) {
		Factions.get().debug(player1.getName() + " requested factions placeholder " + identifier);
		
		for (FactionsPlaceholder placeholder : FactionsPlaceholders.get().getPlaceholders()) {
			if (!placeholder.placeholder().equalsIgnoreCase(identifier) || !(placeholder instanceof FactionsPlaceholderRelation)) continue;
			FactionsPlaceholderRelation relPlaceholder = (FactionsPlaceholderRelation) placeholder;
			return relPlaceholder.get(player1, player2);
		}
		
		return null;
	}
	@Override
	public String getAuthor() {
		return "RedstoneOre";
	}
	
	@Override
	public String getIdentifier() {
		return "factions";
	}
	
	@Override
	public String getPlugin() {
		return Factions.get().getDescription().getName();
	}
	
	@Override
	public String getVersion() {
		return Factions.get().getDescription().getVersion();
	}
	
	@Override
	public String onPlaceholderRequest(Player player, String identifier) {
		Factions.get().debug(player.getName() + " requested factions placeholder " + identifier);
		for (FactionsPlaceholder placeholder : FactionsPlaceholders.get().getPlaceholders()) {
			if (!placeholder.placeholder().equalsIgnoreCase(identifier)) continue;
			return placeholder.get(player);
		}
		
		return null;
	}
	
}
