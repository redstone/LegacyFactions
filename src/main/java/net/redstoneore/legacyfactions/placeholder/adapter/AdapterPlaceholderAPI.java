package net.redstoneore.legacyfactions.placeholder.adapter;

import org.bukkit.entity.Player;

import me.clip.placeholderapi.PlaceholderAPI;
import me.clip.placeholderapi.external.EZPlaceholderHook;
import net.redstoneore.legacyfactions.Factions;
import net.redstoneore.legacyfactions.placeholder.FactionsPlaceholder;
import net.redstoneore.legacyfactions.placeholder.FactionsPlaceholdersAdapter;
import net.redstoneore.legacyfactions.placeholder.FactionsPlaceholderSingleSetup;
import net.redstoneore.legacyfactions.placeholder.FactionsPlaceholders;

public class AdapterPlaceholderAPI extends FactionsPlaceholdersAdapter implements FactionsPlaceholderSingleSetup {
	
	// -------------------------------------------------- //
	// INSTANCE
	// -------------------------------------------------- //
	
	private static AdapterPlaceholderAPI i = new AdapterPlaceholderAPI();
	public static AdapterPlaceholderAPI get() { return i; }
	
	// -------------------------------------------------- //
	// FIELDS
	// -------------------------------------------------- //
	
	private EZPlaceholderHook placeholderHook = new EZPlaceholderHook(Factions.get(), "factions") {
		@Override
		public String onPlaceholderRequest(Player player, String identifier) {
			Factions.get().debug(player.getName() + " requested factions placeholder " + identifier);
			for (FactionsPlaceholder placeholder : FactionsPlaceholders.get().getPlaceholders()) {
				if (!placeholder.placeholder().equalsIgnoreCase(identifier)) continue;
				return placeholder.get(player);
			}
			
			return null;
		}
	};
	
	// -------------------------------------------------- //
	// METHODS
	// -------------------------------------------------- //
	
	@Override
	protected void adapt(FactionsPlaceholder placeholder) {
		// Not used for this placeholder API
	}

	@Override
	public void setup() {
		try {
			Class.forName("me.clip.placeholderapi.expansion.Relational");
			
			if (this.placeholderHook.isHooked()) {
				PlaceholderAPI.unregisterPlaceholderHook(Factions.get());
			}
			
			AdapterPlaceholderAPI28.get().register();
			Factions.get().debug("Hooked with PlaceholderAPI (relational)");
		} catch (Exception e) {
			this.placeholderHook.hook();
			Factions.get().debug("Hooked with PlaceholderAPI (non relational)");
		}
	}

}