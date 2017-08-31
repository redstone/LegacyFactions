package net.redstoneore.legacyfactions.integration.novucsftop;

import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import net.redstoneore.legacyfactions.Factions;
import net.redstoneore.legacyfactions.cmd.CmdFactionsTop;
import net.redstoneore.legacyfactions.cmd.FCommand;
import net.redstoneore.legacyfactions.event.EventFactionsCommandExecute;
import net.redstoneore.legacyfactions.integration.Integration;

public class NovucsFactionsTopIntegration extends Integration implements Listener {
	
	// -------------------------------------------------- //
	// INSTANCE
	// -------------------------------------------------- //
	
	private static NovucsFactionsTopIntegration i = new NovucsFactionsTopIntegration();
	public static NovucsFactionsTopIntegration get() { return i; }
	
	// -------------------------------------------------- //
	// METHODS
	// -------------------------------------------------- //
	
	@Override
	public String getName() {
		return "FactionsTop";
	}
	
	@Override
	public boolean isEnabled() {
		if (!Bukkit.getPluginManager().isPluginEnabled(this.getName())) return false;
		
		return Bukkit.getPluginManager().getPlugin(this.getName()).getDescription().getAuthors().contains("novucs");
	}

	@Override
	public void init() {
		Bukkit.getServer().getPluginManager().registerEvents(this, Factions.get());
	}
	
	@EventHandler
	public void onTopCommand(EventFactionsCommandExecute event) {
		if (event.getCommand() != CmdFactionsTop.get()) return;
		event.setCancelled(true);
		
		FCommand command = (FCommand) event.getCommand();
		
		
		if (command.argAsString(0, null) == "gui") {
			event.getfPlayer().getPlayer().performCommand("ftopgui");
			return;
		}
		
		event.getfPlayer().getPlayer().performCommand("ftop " + command.argAsString(0, "1"));
	}

}
