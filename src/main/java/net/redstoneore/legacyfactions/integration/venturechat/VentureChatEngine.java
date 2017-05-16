package net.redstoneore.legacyfactions.integration.venturechat;

import org.bukkit.event.EventHandler;

import mineverse.Aust1n46.chat.MineverseChat;
import mineverse.Aust1n46.chat.api.MineverseChatAPI;
import mineverse.Aust1n46.chat.api.MineverseChatPlayer;
import net.redstoneore.legacyfactions.ChatMode;
import net.redstoneore.legacyfactions.entity.Conf;
import net.redstoneore.legacyfactions.entity.FPlayer;
import net.redstoneore.legacyfactions.event.EventFactionsChatModeChange;
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
		event.setCancelled(true);
		
		FPlayer fplayer = event.getfPlayer();
		MineverseChatPlayer cplayer = MineverseChatAPI.getMineverseChatPlayer(fplayer.getPlayer());
		
		if (event.getChatMode() == ChatMode.ALLIANCE) {
			fplayer.setChatMode(ChatMode.ALLIANCE);
			cplayer.setCurrentChannel(MineverseChat.ccInfo.getChannelInfo("faction"));
		}
		
		fplayer.setChatMode(ChatMode.PUBLIC);
		cplayer.setCurrentChannel(MineverseChat.ccInfo.getChannelInfo(Conf.chatModePublicChannel));
	}
	
}
