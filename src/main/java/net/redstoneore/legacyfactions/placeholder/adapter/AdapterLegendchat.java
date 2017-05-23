package net.redstoneore.legacyfactions.placeholder.adapter;

import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import br.com.devpaulo.legendchat.api.events.ChatMessageEvent;
import net.redstoneore.legacyfactions.Factions;
import net.redstoneore.legacyfactions.placeholder.FactionsPlaceholder;
import net.redstoneore.legacyfactions.placeholder.FactionsPlaceholderAdapter;
import net.redstoneore.legacyfactions.placeholder.FactionsPlaceholderSingleSetup;
import net.redstoneore.legacyfactions.placeholder.FactionsPlaceholders;

public class AdapterLegendchat extends FactionsPlaceholderAdapter implements FactionsPlaceholderSingleSetup, Listener {
	
	// -------------------------------------------------- //
	// INSTANCE
	// -------------------------------------------------- //
	
	private static AdapterLegendchat i = new AdapterLegendchat();
	public static AdapterLegendchat get() { return i; }
	
	// -------------------------------------------------- //
	// FIELDS
	// -------------------------------------------------- //
	
	private boolean registered = false;
	
	// -------------------------------------------------- //
	// METHODS
	// -------------------------------------------------- //
	
	@Override
	protected void adapt(FactionsPlaceholder placeholder) {
		// Not used for this placeholder API
	}

	@Override
	public void setup() {
		if (this.registered) return;
		this.registered = true;
		Bukkit.getServer().getPluginManager().registerEvents(this, Factions.get());
	}
	
	@EventHandler
	public void onChatExEvent(ChatMessageEvent event) {
		for (FactionsPlaceholder placeholder : FactionsPlaceholders.get().getPlaceholders()) {
			if (!event.getTags().contains("factions_" + placeholder.placeholder())) continue;
				
			event.setTagValue("factions_" + placeholder.placeholder(), placeholder.get(event.getSender()));
		}
	}

}
