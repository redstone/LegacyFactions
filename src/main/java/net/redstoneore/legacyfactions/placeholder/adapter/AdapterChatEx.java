package net.redstoneore.legacyfactions.placeholder.adapter;

import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import de.thejeterlp.chatex.api.ChatExEvent;
import net.redstoneore.legacyfactions.Factions;
import net.redstoneore.legacyfactions.placeholder.FactionsPlaceholder;
import net.redstoneore.legacyfactions.placeholder.FactionsPlaceholdersAdapter;
import net.redstoneore.legacyfactions.placeholder.FactionsPlaceholderSingleSetup;
import net.redstoneore.legacyfactions.placeholder.FactionsPlaceholders;

public class AdapterChatEx extends FactionsPlaceholdersAdapter implements FactionsPlaceholderSingleSetup, Listener {
	
	// -------------------------------------------------- //
	// INSTANCE
	// -------------------------------------------------- //
	
	private static AdapterChatEx i = new AdapterChatEx();
	public static AdapterChatEx get() { return i; }
	
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
	public void onChatExEvent(ChatExEvent event) {
		String msg = event.getFormat();
		
		for (FactionsPlaceholder placeholder : FactionsPlaceholders.get().getPlaceholders()) {
			msg.replaceAll("%factions_" + placeholder.placeholder(), placeholder.get(event.getPlayer()));
		}
		
		event.setFormat(msg);
	}

}
