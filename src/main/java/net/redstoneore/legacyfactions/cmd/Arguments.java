package net.redstoneore.legacyfactions.cmd;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import com.google.common.collect.Lists;

import net.redstoneore.legacyfactions.callback.Callback;
import net.redstoneore.legacyfactions.entity.FPlayer;
import net.redstoneore.legacyfactions.entity.FPlayerColl;
import net.redstoneore.legacyfactions.util.UUIDUtil;

public class Arguments {
	
	// -------------------------------------------------- //
	// CONSTRUCT
	// -------------------------------------------------- //
	
	public Arguments(final FAsyncCommand command, final List<String> args) {
		this.args = Lists.newArrayList(args);
		this.command = command;
	}
	
	// -------------------------------------------------- //
	// FIELDS
	// -------------------------------------------------- //
	
	private final List<String> args;
	private final FAsyncCommand command;
	
	// -------------------------------------------------- //
	// METHODS
	// -------------------------------------------------- //
	
	public Optional<String> get(Class<? extends String> type, int arg) {
		if (!this.args.contains(arg)) {
			return Optional.empty();
		}
		
		return Optional.of(this.args.get(arg));
	}
	
	public void get(Class<? extends FPlayer> type, final int arg, FPlayer defaultValue, final Callback<FPlayer> callback) {
		if (!this.args.contains(arg)) {
			callback.then(null,  Optional.empty());
			return;
		}
		
		final String value = this.args.get(arg);
		
		command.sync((done, e) -> {
			// Sync
			final Player player = Bukkit.getPlayer(value);
			
			command.sync((done2, e2) -> {
				// Sync
				done2.then(true, Optional.empty());
			}, (done3, e3) -> {
				// Async
				if (player != null) {
					FPlayer fplayer = FPlayerColl.get(player);
					callback.then(fplayer, Optional.empty());
					return;
				}
				
				UUID uuid = null;
				try {
					uuid = UUIDUtil.getUUIDOf(value);
				} catch (Exception exception) {
					callback.then(null, Optional.of(exception));
					return;
				}
				
				if (uuid == null) {
					callback.then(null, Optional.empty());
					return;
				}
				
				FPlayer fplayer = FPlayerColl.get(uuid);
				fplayer.asMemoryFPlayer().setName(value);
				
				callback.then(fplayer, Optional.empty());
			});
		});		
	}
	
}
