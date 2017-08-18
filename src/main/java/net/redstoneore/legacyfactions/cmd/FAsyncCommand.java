package net.redstoneore.legacyfactions.cmd;

import java.util.Optional;

import org.bukkit.Bukkit;

import net.redstoneore.legacyfactions.Factions;
import net.redstoneore.legacyfactions.callback.Callback;
import net.redstoneore.legacyfactions.entity.FPlayer;
import net.redstoneore.legacyfactions.entity.FPlayerColl;

/**
 * This command is run asynchronous to improve performance. Because of this, access to the bukkit
 * API requires using synchronised tasks with callbacks. 
 */
public abstract class FAsyncCommand extends MCommand<Factions> {

	// -------------------------------------------------- //
	// METHODS
	// -------------------------------------------------- //
	
	@Override
	public final void perform() {
		final FPlayer fplayer = FPlayerColl.get(this.sender);
		final Arguments arguments = new Arguments(this, this.args);
		
		Bukkit.getScheduler().runTaskAsynchronously(Factions.get(), () -> execute(arguments, fplayer));
	}
	
	public final void sync(Callback<Callback<Boolean>> sync) {
		this.sync(sync, null);
	}
	
	public final void sync(Callback<Callback<Boolean>> sync, final Callback<Boolean> completed) {
		if (sync == null) {
			Bukkit.getScheduler().runTaskAsynchronously(Factions.get(), () -> completed.then(true, Optional.empty()));
			return;
		}
		
		Bukkit.getScheduler().runTask(Factions.get(), () -> 
			sync.then((result, exception) -> {
				if (completed != null) {
					Bukkit.getScheduler().runTaskAsynchronously(Factions.get(), () -> completed.then(true, Optional.empty()));					
				}
			}, Optional.empty())
		);
	}
	
	public abstract void execute(final Arguments arguments, final FPlayer fplayer);
	
}
