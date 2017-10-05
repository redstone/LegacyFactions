package net.redstoneore.legacyfactions.expansion.chat.cmd;

import net.redstoneore.legacyfactions.Permission;
import net.redstoneore.legacyfactions.cmd.FCommand;
import net.redstoneore.legacyfactions.config.CommandAliases;
import net.redstoneore.legacyfactions.config.Config;

import java.util.Optional;

import net.redstoneore.legacyfactions.event.EventFactionsChatModeChange;
import net.redstoneore.legacyfactions.expansion.chat.ChatMode;
import net.redstoneore.legacyfactions.lang.Lang;

public class CmdFactionsChat extends FCommand {
	
	// -------------------------------------------------- //
	// INSTANCE
	// -------------------------------------------------- //
	
	private static CmdFactionsChat instance = new CmdFactionsChat();
	public static CmdFactionsChat get() { return instance; }
	
	// -------------------------------------------------- //
	// CONSTRUCT
	// -------------------------------------------------- //

	private CmdFactionsChat() {
		this.aliases.addAll(CommandAliases.cmdAliasesChat);

		this.optionalArgs.put("mode", "next");

		this.permission = Permission.CHAT.getNode();
		this.disableOnLock = false;

		this.senderMustBePlayer = true;
		this.senderMustBeMember = true;
		this.senderMustBeModerator = false;
		this.senderMustBeColeader = false;
		this.senderMustBeAdmin = false;
	}

	// -------------------------------------------------- //
	// METHODS
	// -------------------------------------------------- //

	@Override
	public void perform() {
		if (!Config.expansionsFactionsChat.enabled) {
			sendMessage(Lang.COMMAND_CHAT_DISABLED.toString());
			return;
		}
		
		// Set the current chat mode target as the default next
		ChatMode chatModeTarget = this.fme.getChatMode().getNext();
		
		// Check for requested chat mode
		String requestedChatMode = this.argAsString(0);
		if (requestedChatMode != null) {
			String firstCharacter = requestedChatMode.toLowerCase().substring(0, 1);
			
			if (firstCharacter == null) {
				this.sendMessage(Lang.COMMAND_CHAT_INVALIDMODE);
				return;
			}
			
			// Lets attempt to find the chat mode
			Optional<ChatMode> oChatModeTarget = ChatMode.stream().filter(chatMode -> {
				return (chatMode.getNiceName().toLowerCase().startsWith(firstCharacter));
			}).findFirst();
			
			if (!oChatModeTarget.isPresent()) {
				// Okay, try using the normal name - in case they changed the language
				oChatModeTarget = ChatMode.stream().filter(chatMode -> {
					return (chatMode.name().toLowerCase().startsWith(firstCharacter));
				}).findFirst();
				
				if (!oChatModeTarget.isPresent()) {
					// No luck.
					this.sendMessage(Lang.COMMAND_CHAT_INVALIDMODE);
					return;
				}
			}
			
			// Change to this now.
			chatModeTarget = oChatModeTarget.get();
		}
		
		// Call the event.
		EventFactionsChatModeChange chatModeChangeEvent = new EventFactionsChatModeChange(this.myFaction, this.fme, chatModeTarget);
		chatModeChangeEvent.call();
		if (chatModeChangeEvent.isCancelled()) return;
		
		// Update the chat mode from the event
		chatModeTarget = chatModeChangeEvent.getChatMode();
		
		// Set as current
		this.fme.setChatMode(chatModeTarget);
		
		// Ensure we're not being silent
		if (!chatModeChangeEvent.isSilent()) {
			// Sender the appropriate message
			switch (this.fme.getChatMode()) {
			case ALLIANCE:
				this.sendMessage(Lang.COMMAND_CHAT_MODE_ALLIANCE);
				break;
			case PUBLIC:
				this.sendMessage(Lang.COMMAND_CHAT_MODE_PUBLIC);
				break;
			case TRUCE:
				this.sendMessage(Lang.COMMAND_CHAT_MODE_TRUCE);
				break;
			case FACTION:
			default:
				this.sendMessage(Lang.COMMAND_CHAT_MODE_FACTION);
				break;
			}
		}
	}

	@Override
	public String getUsageTranslation() {
		return Lang.COMMAND_CHAT_DESCRIPTION.toString();
	}
}
