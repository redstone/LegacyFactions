package net.redstoneore.legacyfactions.mixin;

import java.util.stream.Collectors;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.plugin.Plugin;

import net.redstoneore.legacyfactions.Factions;
import net.redstoneore.legacyfactions.integration.Integrations;

public class DebugMixin {

	public static void sendToConsole() {
		ConsoleCommandSender sender = Bukkit.getConsoleSender();
		
		sender.sendMessage(ChatColor.WHITE + "=== START DEBUG ===");
		sender.sendMessage(" ");
		sender.sendMessage("----- Server Information -----");

		// Show server version.
		sender.sendMessage("Server Name: " + Bukkit.getServer().getName());
		sender.sendMessage("Server Version: " + Bukkit.getServer().getVersion());
		
		// Return the name and version.
		sender.sendMessage(Factions.get().getDescription().getName() + " v" + Factions.get().getDescription().getVersion());
		
		// Package name.
		sender.sendMessage("Package: " + DebugMixin.class.getClass().getPackage().toString());
		
		// Java Version.
		sender.sendMessage("Java Version: " + System.getProperty("java.version"));
		
		// Show all plugins with their versions and authors.
		sender.sendMessage("");
		sender.sendMessage("");
		sender.sendMessage("----- Plugins -----");

		for (Plugin plugin : Bukkit.getPluginManager().getPlugins()) {
			String name = plugin.getDescription().getName();
			String version = plugin.getDescription().getVersion();
			String authors = plugin.getDescription().getAuthors().stream().collect(Collectors.joining(", "));
			sender.sendMessage(name + " v" + version + " by " + authors);
		}
		
		// Show all integrations.
		sender.sendMessage(" ");
		sender.sendMessage("----- Integrations -----");
		
		Integrations.getAll().forEach(integration -> {
			sender.sendMessage(integration.getName() + " = " + integration.isEnabled());
		});
		
		sender.sendMessage(" ");
		sender.sendMessage(ChatColor.WHITE + "=== END DEBUG ===");
	}
	
	public static void deprecatedWarning(String what, String use) {
		Factions.get().warn("THIS IS NOT AN ERROR WITH LEGACYFACTIONS!");
		Factions.get().warn("A plugin you are using is calling a depcreated method. Please ask them to change this.");
		Factions.get().warn("Called " + what + ", they should use " + use);
		new Exception("deprecated method call").printStackTrace();
	}
	
}
