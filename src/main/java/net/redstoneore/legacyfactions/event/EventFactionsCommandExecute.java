package net.redstoneore.legacyfactions.event;

import org.bukkit.event.Cancellable;

import net.redstoneore.legacyfactions.Factions;
import net.redstoneore.legacyfactions.cmd.FCommandBase;
import net.redstoneore.legacyfactions.entity.FPlayer;

/**
 * EventFactionsCommandExecute is execute when any factions command is executed. It can be cancelled. 
 *
 */
public class EventFactionsCommandExecute extends AbstractFactionsPlayerEvent<EventFactionsCommandExecute> implements Cancellable {

	// -------------------------------------------------- //
	// STATIC METHODS
	// -------------------------------------------------- //
	
	public static EventFactionsCommandExecute create(FPlayer fplayer, FCommandBase<Factions> command) {
		return new EventFactionsCommandExecute(fplayer, command);
	}
	
	// -------------------------------------------------- //
	// CONSTRUCT
	// -------------------------------------------------- //
	
	public EventFactionsCommandExecute(FPlayer fplayer, FCommandBase<Factions> command) {
		super(fplayer.getFaction(), fplayer);
		
		this.command = command;
	}
	
	// -------------------------------------------------- //
	// FIELDS
	// -------------------------------------------------- //
	
	private FCommandBase<Factions> command = null;
	private boolean cancelled = false;
	
	// -------------------------------------------------- //
	// METHODS
	// -------------------------------------------------- //
	
	public FCommandBase<Factions> getCommand() {
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
