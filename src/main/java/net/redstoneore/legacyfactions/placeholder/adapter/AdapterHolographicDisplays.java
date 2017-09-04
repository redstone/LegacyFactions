package net.redstoneore.legacyfactions.placeholder.adapter;

import com.gmail.filoghost.holographicdisplays.api.placeholder.PlaceholderReplacer;
import com.gmail.filoghost.holographicdisplays.placeholder.Placeholder;
import com.gmail.filoghost.holographicdisplays.placeholder.PlaceholdersRegister;

import net.redstoneore.legacyfactions.Factions;
import net.redstoneore.legacyfactions.placeholder.FactionsPlaceholder;
import net.redstoneore.legacyfactions.placeholder.FactionsPlaceholdersAdapter;

public class AdapterHolographicDisplays extends FactionsPlaceholdersAdapter {
	
	// -------------------------------------------------- //
	// INSTANCE
	// -------------------------------------------------- //
	
	private static AdapterHolographicDisplays i = new AdapterHolographicDisplays();
	public static AdapterHolographicDisplays get() { return i; }

	// -------------------------------------------------- //
	// METHODS
	// -------------------------------------------------- //
	
	@Override
	protected void adapt(FactionsPlaceholder placeholder) {
		PlaceholdersRegister.register(new Placeholder(Factions.get(), "{factions_" + placeholder.placeholder() + "}", 1.0,
			new PlaceholderReplacer() {
				@Override
				public String update() {
					return placeholder.get(null);
				}
			}
		));
	}

}
