package net.redstoneore.legacyfactions.placeholder.adapter;

import be.maximvdw.placeholderapi.PlaceholderAPI;
import be.maximvdw.placeholderapi.PlaceholderReplaceEvent;
import be.maximvdw.placeholderapi.PlaceholderReplacer;
import net.redstoneore.legacyfactions.Factions;
import net.redstoneore.legacyfactions.placeholder.FactionsPlaceholder;
import net.redstoneore.legacyfactions.placeholder.FactionsPlaceholdersAdapter;

public class AdapterMVdWPlaceholderAPI extends FactionsPlaceholdersAdapter {

	// -------------------------------------------------- //
	// INSTANCE
	// -------------------------------------------------- //
	
	private static AdapterMVdWPlaceholderAPI i = new AdapterMVdWPlaceholderAPI();
	public static AdapterMVdWPlaceholderAPI get() { return i; }
	
	// -------------------------------------------------- //
	// METHODS
	// -------------------------------------------------- //
	
	@Override
	protected void adapt(final FactionsPlaceholder placeholder) {
		PlaceholderAPI.registerPlaceholder(Factions.get(), "factions_" + placeholder.placeholder(), new PlaceholderReplacer() {
			@Override
			public String onPlaceholderReplace(PlaceholderReplaceEvent event) {
				return placeholder.get(event.getPlayer());
			}
		});
	}

}
