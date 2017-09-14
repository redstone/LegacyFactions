package net.redstoneore.legacyfactions.expansion.chat;

import java.util.HashSet;
import java.util.Set;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

import net.redstoneore.legacyfactions.Relation;
import net.redstoneore.legacyfactions.config.Config;
import net.redstoneore.legacyfactions.entity.FPlayer;
import net.redstoneore.legacyfactions.entity.FPlayerColl;
import net.redstoneore.legacyfactions.event.EventFactionsChat;
import net.redstoneore.legacyfactions.placeholder.FactionsPlaceholders;

public class FactionsChatListener implements Listener {

	// -------------------------------------------------- //
	// INSTANCE
	// -------------------------------------------------- //
	
	private static FactionsChatListener instance = new FactionsChatListener();
	public static FactionsChatListener get() { return instance; }
	private FactionsChatListener() { } 
	
	// -------------------------------------------------- //
	// METHODS
	// -------------------------------------------------- //
	
	/**
	 * This method will handle the formatting for the faction chat, and remove anyone who is not
	 * supposed to receive it.
	 * @param event {@link AsyncPlayerChatEvent}
	 */
	@EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
	public void onFactionChat(AsyncPlayerChatEvent event) {
		FPlayer fplayer = FPlayerColl.get(event.getPlayer());
		
		Set<Player> recipients = new HashSet<>(event.getRecipients());
		Set<Player> spying = new HashSet<>();
		
		String format = null;
		
		switch (fplayer.getChatMode()) {
		case ALLIANCE:
			recipients.forEach(recipient -> {
				FPlayer frecipient = FPlayerColl.get(recipient);
				Relation relation = frecipient.getRelationTo(fplayer);
				
				// If they are not a member or an ally, or they are an ally thats ignoring alliance chat, remove them!
				if ((relation != Relation.MEMBER && relation != Relation.ALLY) || (relation == Relation.ALLY && frecipient.isIgnoreAllianceChat())) {
					event.getRecipients().remove(recipient);
					
					// But if they're spying, we'll let them know soon.
					if (frecipient.isSpyingChat()) {
						spying.add(recipient);
					}
				}
				
			});
			
			format = Config.expansionsFactionsChat.chatFormatAlliance.toString();
			break;
			
		case FACTION:
			recipients.forEach(recipient -> {
				FPlayer frecipient = FPlayerColl.get(recipient);
				Relation relation = frecipient.getRelationTo(fplayer);
				
				// If they aren't a member, remove them.
				if (relation != Relation.MEMBER) {
					event.getRecipients().remove(recipient);
					
					// But if they're spying, we'll let them know soon.
					if (frecipient.isSpyingChat()) {
						spying.add(recipient);
					}
				}
			});
			format = Config.expansionsFactionsChat.chatFormatFaction.toString();
			break;
			
		case PUBLIC:
			if (!Config.expansionsFactionsChat.enableFormatPublicChat) return;
			
			// Simply format.
			format = Config.expansionsFactionsChat.chatFormatPublic.toString();
			break;
			
		case TRUCE:
			recipients.forEach(recipient -> {
				FPlayer frecipient = FPlayerColl.get(recipient);
				Relation relation = frecipient.getRelationTo(fplayer);
				
				if (relation != Relation.TRUCE && relation != Relation.MEMBER) {
					event.getRecipients().remove(recipient);
					
					if (frecipient.isSpyingChat()) {
						spying.add(recipient);
					}
				}
			});
			format = Config.expansionsFactionsChat.chatFormatTruce.toString();
			break;		
		}
		
		EventFactionsChat chatEvent = EventFactionsChat.create(fplayer, fplayer.getChatMode(), format, event.getMessage(), recipients, spying).call();
		if (chatEvent.isCancelled()) return;
		
		// if there is no format then something went wrong, don't continue
		format = chatEvent.getFormat();
		if (format == null) return;
		
		// Set the new format, parsing our placeholders
		format = FactionsPlaceholders.get().parse(fplayer, format);
		
		format = format.replace("{fc_message}", chatEvent.getMessage());
		
		event.setFormat(format);
		
		// Notify anyone who is spying
		if (!chatEvent.getSpying().isEmpty()) {
			String message = String.format(Config.expansionsFactionsChat.chatFormatSpy.toString(), event.getPlayer().getDisplayName(), chatEvent.getMessage());
			
			chatEvent.getSpying().forEach(spy -> spy.sendMessage(message));
		}
		
	}
	
	/**
	 * Replaces the chat tag if enabled, run on HIGH to give other plugins a chance to format
	 * @param event {@link AsyncPlayerChatEvent}
	 */
	@EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
	public void chatTagReplacer(AsyncPlayerChatEvent event) {
		if (!Config.expansionsFactionsChat.chatTagEnabled) return;
		
		FPlayer fplayer = FPlayerColl.get(event.getPlayer());
		
		String chatTag = "";
		
		if (fplayer.hasFaction()) {
			chatTag = Config.expansionsFactionsChat.chatTagFormatDefault.toString();
		} else {
			chatTag = Config.expansionsFactionsChat.chatTagFormatFactionless.toString();
		}
		
		chatTag = FactionsPlaceholders.get().parse(fplayer, chatTag);
		
		event.setFormat(event.getFormat().replace(Config.expansionsFactionsChat.chatTagPlaceholder.toString(), chatTag));
	}
	
	/**
	 * Using {@link FactionsChatConfig#chatTagRelationalOverride} is discouraged but sometimes required. It will
	 * cancel the event so we can use relational placeholders by sending it ourselves. <br><br>
	 * This can break compatibility with plugins that listen on MONITOR to send to external chat
	 * services like Discord, Slack, or IRC.
	 * @param event {@link AsyncPlayerChatEvent}
	 */
	@EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
	public void chatOverride(AsyncPlayerChatEvent event) {
		if (!Config.expansionsFactionsChat.chatTagRelationalOverride) return;
		
		// Cancel this event. We want to be bad and handle it ourselves. 
		event.setCancelled(true);

		FPlayer fsender = FPlayerColl.get(event.getPlayer());
		
		// Go over each recipient and send a formatted message
		FPlayerColl.rewrap(event.getRecipients()).forEach(frecipient -> {
			String messageFormat = FactionsPlaceholders.get().parse(fsender.getPlayer(), frecipient.getPlayer(), event.getFormat());
			
			String formattedMessage = String.format(messageFormat, event.getPlayer().getDisplayName(), event.getMessage());
			
			frecipient.sendMessage(formattedMessage);
		});
	}	
	
}
