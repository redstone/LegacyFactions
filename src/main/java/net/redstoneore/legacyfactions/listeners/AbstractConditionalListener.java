package net.redstoneore.legacyfactions.listeners;

import org.bukkit.event.Listener;

public interface AbstractConditionalListener extends Listener {

	boolean shouldEnable();
	
}
