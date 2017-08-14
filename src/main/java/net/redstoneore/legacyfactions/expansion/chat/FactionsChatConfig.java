package net.redstoneore.legacyfactions.expansion.chat;

import org.bukkit.ChatColor;

public class FactionsChatConfig {
	
	// -------------------------------------------------- //
	// MISC
	// -------------------------------------------------- // 

	// Enable faction chat
	public boolean enabled = true;
	
	// Enable alliance chat
	public boolean enableAllianceChat = true;
	
	// Enable truce chat
	public boolean enableTruceChat = true;
	
	// Enable formatting public chat
	public boolean enableFormatPublicChat = false;
		
	public String chatFormatPublic = "{factions_player_role_prefix}%s:" + ChatColor.WHITE + " %s";
	public String chatFormatFaction = ChatColor.GREEN + "{factions_player_role_prefix}%s:" + ChatColor.WHITE + " %s";
	public String chatFormatAlliance = ChatColor.LIGHT_PURPLE + "{factions_player_role_prefix}%s:" + ChatColor.WHITE + " %s";
	public String chatFormatTruce = ChatColor.DARK_PURPLE + "{factions_player_role_prefix}%s:" + ChatColor.WHITE + " %s";
	public String chatFormatSpy = ChatColor.GRAY + "(spy) {factions_player_role_prefix}%s:" + ChatColor.GRAY + " %s";
	
	// -------------------------------------------------- //
	// CHANNELS
	// -------------------------------------------------- // 
	
	// Use chat channels in external plugins when available 
	public boolean chatPluginChannelUse = true;
	
	// Default global channel, if empty we try to find it ourselves
	public String chatPluginChannelGlobal = "";
	
	// -------------------------------------------------- //
	// CHAT TAG
	// -------------------------------------------------- // 
	
	// Injected Chat Tag will cancel the Chat event and send it to players on its own, but allows for relational colours.
	public boolean chatTagEnabled = true;
	
	// If you want to use relational placeholders we must cancel and handle the chat ourselves. 
	public boolean chatTagRelationalOverride = false;
	
	// Chat Tag placeholder.
	public String chatTagPlaceholder = "{factions_chat_tag}";
	
	// Chat Tag format for default.
	public String chatTagFormatDefault = "{factions_player_role_prefix}{factions_faction_name}" + ChatColor.WHITE;
	
	// Chat Tag format for factionless.
	public String chatTagFormatFactionless = "";
	
}
