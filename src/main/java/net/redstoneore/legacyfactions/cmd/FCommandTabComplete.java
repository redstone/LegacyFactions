package net.redstoneore.legacyfactions.cmd;

import java.util.List;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;

public class FCommandTabComplete implements TabCompleter {

	// -------------------------------------------------- //
	// INSTANCE
	// -------------------------------------------------- //
	
	private static FCommandTabComplete instance = new FCommandTabComplete();
	public static FCommandTabComplete get() { return instance ;}
	
	// -------------------------------------------------- //
	// METHODS
	// -------------------------------------------------- //
	
	@Override
	public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
		// TODO Auto-generated method stub
		return null;
	}

}
