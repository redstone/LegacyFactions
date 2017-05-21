package net.redstoneore.legacyfactions.placeholder.adapter;

import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

import com.dthielke.herochat.ChannelChatEvent;

import net.redstoneore.legacyfactions.Factions;
import net.redstoneore.legacyfactions.placeholder.FactionsPlaceholder;
import net.redstoneore.legacyfactions.placeholder.FactionsPlaceholderAdapter;
import net.redstoneore.legacyfactions.placeholder.FactionsPlaceholderSingleSetup;
import net.redstoneore.legacyfactions.placeholder.FactionsPlaceholders;

public class AdapterHeroChat extends FactionsPlaceholderAdapter implements FactionsPlaceholderSingleSetup, Listener {

	// -------------------------------------------------- //
	// INSTANCE
	// -------------------------------------------------- //
	
	private static AdapterHeroChat i = new AdapterHeroChat();
	public static AdapterHeroChat get() { return i; }
	
	// -------------------------------------------------- //
	// METHODS
	// -------------------------------------------------- //
	
	@Override
	public void setup() {
		Bukkit.getServer().getPluginManager().registerEvents(this, Factions.get());
	}

	@Override
	protected void adapt(FactionsPlaceholder placeholder) {
		// Not used for this API
	}
	
	@EventHandler(priority = EventPriority.NORMAL)
	public void onChannelChatEvent(ChannelChatEvent event) {
		String format = event.getFormat();
		
		// Replace {default} here so we can replace faction tags within {default} as well
		format = format.replace("{default}", event.getChannel().getFormatSupplier().getStandardFormat());
		
		for (FactionsPlaceholder placeholder : FactionsPlaceholders.get().getPlaceholders()) {
			format.replaceAll("{factions_" + placeholder.placeholder() + "}", placeholder.get(event.getSender().getPlayer()));
		}
		
		event.setFormat(format);
	}

}
