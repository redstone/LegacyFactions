package net.redstoneore.legacyfactions.event;

import org.bukkit.event.Cancellable;

import net.redstoneore.legacyfactions.entity.FPlayer;
import net.redstoneore.legacyfactions.entity.Faction;
import net.redstoneore.legacyfactions.expansion.chat.ChatMode;

public class EventFactionsChatModeChange extends AbstractFactionsPlayerEvent<EventFactionsChatModeChange> implements Cancellable {

	// -------------------------------------------------- //
	// CONSTRUCT
	// -------------------------------------------------- //
	
	public EventFactionsChatModeChange(Faction faction, FPlayer fPlayer, ChatMode chatMode) {
		super(faction, fPlayer);
		
		this.chatMode = chatMode;
	}

	// -------------------------------------------------- //
	// FIELDS
	// -------------------------------------------------- //
	
	private ChatMode chatMode;
	private boolean silent = false;
	private boolean cancelled = false;
	
	// -------------------------------------------------- //
	// METHODS
	// -------------------------------------------------- //
	
	/**
	 * Get the chat mode the player is switching to
	 * @return ChatCode the player is switching to
	 */
	public ChatMode getChatMode() {
		return this.chatMode;
	}
	
	@Override
	public boolean isCancelled() {
		return this.cancelled;
	}

	@Override
	public void setCancelled(boolean cancel) {
		this.cancelled = cancel;
	}
	
	public boolean isSilent() {
		return this.silent;
	}
	
	public void setSilent(boolean silent) {
		this.silent = silent;
	}
	
	public void setChatMode(ChatMode chatMode) {
		this.chatMode = chatMode;
	}
	
}
