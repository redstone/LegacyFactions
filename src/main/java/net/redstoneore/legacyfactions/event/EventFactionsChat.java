package net.redstoneore.legacyfactions.event;

import java.util.Set;

import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;

import net.redstoneore.legacyfactions.entity.FPlayer;
import net.redstoneore.legacyfactions.entity.Faction;
import net.redstoneore.legacyfactions.expansion.chat.ChatMode;

public class EventFactionsChat extends AbstractFactionsPlayerEvent<EventFactionsChat> implements Cancellable {

	// -------------------------------------------------- //
	// STATIC METHODS
	// -------------------------------------------------- //

	public static EventFactionsChat create(FPlayer fplayer, ChatMode chatMode, String format, String message, Set<Player> recipients, Set<Player> spying) {
		return new EventFactionsChat(fplayer.getFaction(), fplayer, chatMode, format, message, recipients, spying);
	}
	
	// -------------------------------------------------- //
	// CONSTRUCT
	// -------------------------------------------------- //
	
	public EventFactionsChat(Faction faction, FPlayer fplayer, ChatMode chatMode, String format, String message, Set<Player> recipients, Set<Player> spying) {
		super(faction, fplayer);
		
		this.chatMode = chatMode;
		this.format = format;
		this.message = message;
		this.recipients = recipients;
		this.spying = spying;
	}

	// -------------------------------------------------- //
	// FIELDS
	// -------------------------------------------------- //
	
	private ChatMode chatMode;
	private String format;
	private String message;
	private Set<Player> recipients;
	private Set<Player> spying;
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
	
	public String getFormat() {
		return this.format;
	}
	
	public void setFormat(String format) {
		this.format = format;
	}
	
	public String getMessage() {
		return this.message;
	}
	
	public void setMessage(String message) {
		this.message = message;
	}
	
	public Set<Player> getRecipients() {
		return this.recipients;
	}
	
	public Set<Player> getSpying() {
		return this.spying;
	}
	
	@Override
	public boolean isCancelled() {
		return this.cancelled;
	}

	@Override
	public void setCancelled(boolean cancel) {
		this.cancelled = cancel;
	}
	
}
