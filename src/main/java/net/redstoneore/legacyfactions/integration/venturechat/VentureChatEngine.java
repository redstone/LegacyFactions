package net.redstoneore.legacyfactions.integration.venturechat;

import org.bukkit.event.EventHandler;

import mineverse.Aust1n46.chat.MineverseChat;
import mineverse.Aust1n46.chat.api.MineverseChatAPI;
import mineverse.Aust1n46.chat.api.MineverseChatPlayer;
import net.redstoneore.legacyfactions.config.Config;
import net.redstoneore.legacyfactions.entity.FPlayer;
import net.redstoneore.legacyfactions.event.EventFactionsChatModeChange;
import net.redstoneore.legacyfactions.expansion.chat.ChatMode;
import net.redstoneore.legacyfactions.integration.IntegrationEngine;

public class VentureChatEngine extends IntegrationEngine {

	// -------------------------------------------------- //
	// INSTANCE
	// -------------------------------------------------- //
	
	private static VentureChatEngine i = new VentureChatEngine();
	public static VentureChatEngine get() { return i; }
	
	// -------------------------------------------------- //
	// METHODS
	// -------------------------------------------------- // 
	
	@EventHandler
	public void onChatModeChange(EventFactionsChatModeChange event) {
		if (!Config.expansionsFactionsChat.chatPluginChannelUse) return;
		
		// let's handle this
		event.setCancelled(true);
		
		FPlayer fplayer = event.getfPlayer();
		MineverseChatPlayer cplayer = MineverseChatAPI.getMineverseChatPlayer(fplayer.getPlayer());
		
		if (event.getChatMode() == ChatMode.FACTION) {
			fplayer.setChatMode(ChatMode.FACTION);
			cplayer.setCurrentChannel(MineverseChat.ccInfo.getChannelInfo("faction"));
			return;
		}
		
		if (event.getChatMode() == ChatMode.ALLIANCE) {
			fplayer.setChatMode(ChatMode.ALLIANCE);
			cplayer.setCurrentChannel(MineverseChat.ccInfo.getChannelInfo("alliance"));
			return;
		}
		
		if (event.getChatMode() == ChatMode.TRUCE) {
			fplayer.setChatMode(ChatMode.TRUCE);
			cplayer.setCurrentChannel(MineverseChat.ccInfo.getChannelInfo("truce"));
			return;
		}
		
		fplayer.setChatMode(ChatMode.PUBLIC);
		if (Config.expansionsFactionsChat.chatPluginChannelGlobal == "") {
			cplayer.setCurrentChannel(MineverseChat.ccInfo.getDefaultChannel());
		} else {
			cplayer.setCurrentChannel(MineverseChat.ccInfo.getChannelInfo(Config.expansionsFactionsChat.chatPluginChannelGlobal));
		}
	}
	
}
