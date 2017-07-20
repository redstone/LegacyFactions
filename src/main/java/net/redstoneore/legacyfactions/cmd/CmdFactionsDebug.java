package net.redstoneore.legacyfactions.cmd;

import java.util.stream.Collectors;

import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;

import net.redstoneore.legacyfactions.Factions;
import net.redstoneore.legacyfactions.Lang;
import net.redstoneore.legacyfactions.Permission;
import net.redstoneore.legacyfactions.entity.Conf;
import net.redstoneore.legacyfactions.integration.Integrations;

public class CmdFactionsDebug extends FCommand {
	
	// -------------------------------------------------- //
	// SINGLETON
	// -------------------------------------------------- //
	
	private static CmdFactionsDebug i = new CmdFactionsDebug();
	public static CmdFactionsDebug get() { return i; }
	
	// -------------------------------------------------- //
	// CONSTRUCT
	// -------------------------------------------------- //

	public CmdFactionsDebug() {
		this.aliases.addAll(Conf.cmdAliasesDebug);

		this.permission = Permission.DEBUG.getNode();
		this.disableOnLock = false;

		this.senderMustBePlayer = false;
		this.senderMustBeMember = false;
		this.senderMustBeModerator = false;
		this.senderMustBeColeader = false;
		this.senderMustBeAdmin = false;
	}

	// -------------------------------------------------- //
	// METHODS
	// -------------------------------------------------- //

	@Override
	public void perform() {
		if (!this.senderIsConsole) {
			this.sendMessage(Lang.GENERIC_CONSOLEONLY.toString());
			return;
		}
		
		this.sendMessage("===== START DEBUG =====");
		this.sendMessage(" ");
		this.sendMessage("----- Server Information -----");

		// Show server version.
		this.sendMessage("Server Version: " + Bukkit.getServer().getVersion());

		// Return the name and version.
		this.sendMessage(Factions.get().getDescription().getName() + " v" + Factions.get().getDescription().getVersion());
		
		// Package name.
		this.sendMessage("Package: " + this.getClass().getPackage().toString());
		
		// Java Version.
		this.sendMessage("Java Version: " + System.getProperty("java.version"));
		
		// Show all plugins with their versions and authors.
		this.sendMessage("");
		this.sendMessage("");
		this.sendMessage("----- Plugins -----");

		for (Plugin plugin : Bukkit.getPluginManager().getPlugins()) {
			String name = plugin.getDescription().getName();
			String version = plugin.getDescription().getVersion();
			String authors = plugin.getDescription().getAuthors().stream().collect(Collectors.joining(", "));
			this.sendMessage(name + " v" + version + " by " + authors);
		}
		
		// Show all integrations.
		this.sendMessage(" ");
		this.sendMessage("----- Integrations -----");
		
		Integrations.getAll().forEach(integration -> {
			this.sendMessage(integration.getName() + " = " + integration.isEnabled());
		});
		
		this.sendMessage(" ");
		this.sendMessage("===== END DEBUG =====");
	}

	@Override
	public String getUsageTranslation() {
		return Lang.COMMAND_SAVEALL_DESCRIPTION.toString();
	}

}
