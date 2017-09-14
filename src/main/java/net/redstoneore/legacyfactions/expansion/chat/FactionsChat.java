package net.redstoneore.legacyfactions.expansion.chat;

import java.util.Collection;

import org.bukkit.event.Listener;

import com.google.common.collect.Lists;

import net.redstoneore.legacyfactions.cmd.FCommand;
import net.redstoneore.legacyfactions.config.Config;
import net.redstoneore.legacyfactions.expansion.FactionsExpansion;
import net.redstoneore.legacyfactions.expansion.chat.cmd.CmdFactionsChat;
import net.redstoneore.legacyfactions.expansion.chat.cmd.CmdFactionsChatspy;
import net.redstoneore.legacyfactions.expansion.chat.cmd.CmdFactionsToggleAllianceChat;

public class FactionsChat extends FactionsExpansion {

	// -------------------------------------------------- //
	// INSTANCE
	// -------------------------------------------------- //
	
	private static FactionsChat i = new FactionsChat();
	public static FactionsChat get() { return i; }
	private FactionsChat() { }
	
	// -------------------------------------------------- //
	// FIELDS
	// -------------------------------------------------- // 
	
	private Collection<Listener> listeners = Lists.newArrayList(
		FactionsChatListener.get()
	);
	
	private Collection<FCommand> commands = Lists.newArrayList(
		CmdFactionsChat.get(),
		CmdFactionsChatspy.get(),
		CmdFactionsToggleAllianceChat.get()
	);	
	
	// -------------------------------------------------- //
	// METHODS
	// -------------------------------------------------- // 
	
	@Override
	public String getName() {
		return "FactionsChat";
	}

	@Override
	public Collection<FCommand> getCommands() {
		return this.commands;
	}

	@Override
	public Collection<Listener> getListeners() {
		return this.listeners;
	}
	
	@Override
	public boolean shouldEnable() {
		return Config.expansionsFactionsChat.enabled == true;
	}

}
