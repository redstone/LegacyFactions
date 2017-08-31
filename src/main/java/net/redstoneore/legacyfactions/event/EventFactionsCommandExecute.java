package net.redstoneore.legacyfactions.event;

import org.bukkit.event.Cancellable;

import net.redstoneore.legacyfactions.Factions;
import net.redstoneore.legacyfactions.cmd.MCommand;
import net.redstoneore.legacyfactions.entity.FPlayer;

/**
 * EventFactionsCommandExecute is execute when any factions command is executed. It can be cancelled. 
 *
 */
public class EventFactionsCommandExecute extends AbstractFactionsPlayerEvent<EventFactionsCommandExecute> implements Cancellable {

	// -------------------------------------------------- //
	// STATIC METHODS
	// -------------------------------------------------- //
	
	public static EventFactionsCommandExecute create(FPlayer fplayer, MCommand<Factions> command) {
		return new EventFactionsCommandExecute(fplayer, command);
	}
	
	// -------------------------------------------------- //
	// CONSTRUCT
	// -------------------------------------------------- //
	
	public EventFactionsCommandExecute(FPlayer fplayer, MCommand<Factions> command) {
		super(fplayer.getFaction(), fplayer);
		
		this.command = command;
	}
	
	// -------------------------------------------------- //
	// FIELDS
	// -------------------------------------------------- //
	
	private MCommand<Factions> command = null;
	private boolean cancelled = false;
	
	// -------------------------------------------------- //
	// METHODS
	// -------------------------------------------------- //
	
	public MCommand<Factions> getCommand() {
		return this.command;
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
