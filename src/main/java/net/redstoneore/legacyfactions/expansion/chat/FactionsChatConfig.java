package net.redstoneore.legacyfactions.expansion.chat;

import org.bukkit.ChatColor;

import net.redstoneore.legacyfactions.lang.Lang;

public class FactionsChatConfig {
	
	// -------------------------------------------------- //
	// MISC
	// -------------------------------------------------- // 

	// Enable faction chat
	protected static String _factionChatEnabled = Lang.CONFIG_FACTIONCHAT_ENABLED.name();
	public boolean enabled = true;
	
	// Enable alliance chat
	protected static String _factionChatAlliance = Lang.CONFIG_FACTIONCHAT_ALLIANCECHAT.name();
	public boolean enableAllianceChat = true;
	
	// Enable truce chat
	protected static String _factionChatTruce = Lang.CONFIG_FACTIONCHAT_TRUCECHAT.name();
	public boolean enableTruceChat = true;
	
	// Enable formatting public chat
	protected static String _factionChatPublicFormat = Lang.CONFIG_FACTIONCHAT_PUBLICFORMAT.name();
	public boolean enableFormatPublicChat = false;
		
	protected static String _factionChatFormats = Lang.CONFIG_FACTIONCHAT_FORMATS.name();
	public String chatFormatPublic = "{factions_player_role_prefix}%s:" + ChatColor.WHITE + " %s";
	public String chatFormatFaction = ChatColor.GREEN + "{factions_player_role_prefix}%s:" + ChatColor.WHITE + " %s";
	public String chatFormatAlliance = ChatColor.LIGHT_PURPLE + "{factions_player_role_prefix}%s:" + ChatColor.WHITE + " %s";
	public String chatFormatTruce = ChatColor.DARK_PURPLE + "{factions_player_role_prefix}%s:" + ChatColor.WHITE + " %s";
	public String chatFormatSpy = ChatColor.GRAY + "(spy) {factions_player_role_prefix}%s:" + ChatColor.GRAY + " %s";
	
	// -------------------------------------------------- //
	// CHANNELS
	// -------------------------------------------------- // 
	
	// Use chat channels in external plugins when available 
	protected static String _chatPluginChannelUse = Lang.CONFIG_FACTIONCHAT_CHATPLUGINCHANNEL.name();
	public boolean chatPluginChannelUse = true;
	
	// Default global channel, if empty we try to find it ourselves
	protected static String _chatPluginChannelGlobal = Lang.CONFIG_FACTIONCHAT_CHATPLUGINGLOBALCHANNEL.name();
	public String chatPluginChannelGlobal = "";
	
	// -------------------------------------------------- //
	// CHAT TAG
	// -------------------------------------------------- // 
	
	// Injected Chat Tag will cancel the Chat event and send it to players on its own, but allows for relational colours.
	protected static String _chatTagEnabled = Lang.CONFIG_FACTIONCHAT_CHATTAGENABLED.name();
	public boolean chatTagEnabled = true;
	
	// If you want to use relational placeholders we must cancel and handle the chat ourselves. 
	protected static String _chatTagRelationalOverride = Lang.CONFIG_FACTIONCHAT_CHATTAGRELATIONALOVERRIDE.name();
	public boolean chatTagRelationalOverride = false;
	
	// Chat Tag placeholder.
	protected static String _chatTagPlaceholder = Lang.CONFIG_FACTIONCHAT_CHATTAGPLACEHOLDER.name();
	public String chatTagPlaceholder = "{factions_chat_tag}";
	
	// Chat Tag format for default.
	protected static String _chatTagFormatDefault = Lang.CONFIG_FACTIONCHAT_CHATTAGFORMATDEFAULT.name();
	public String chatTagFormatDefault = "{factions_player_role_prefix}{factions_faction_name}" + ChatColor.WHITE;
	
	// Chat Tag format for factionless.
	protected static String _chatTagFormatFactionless = Lang.CONFIG_FACTIONCHAT_CHATTAGFORMATFACTIONLESS.name();
	public String chatTagFormatFactionless = "";
	
}
