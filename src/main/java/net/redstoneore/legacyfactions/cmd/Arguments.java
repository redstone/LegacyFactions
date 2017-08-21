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
import net.redstoneore.legacyfactions.entity.Faction;
import net.redstoneore.legacyfactions.entity.FactionColl;
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
	
	/**
	 * Get an argument variable
	 * @param type argument type
	 * @param arg argument number to grab
	 * @param callback the resulting callback in async
	 */
	@SuppressWarnings("unchecked")
	public <T>void get(final Class<? extends T> type, final int arg, final Callback<Optional<T>> callback) {
		if (this.args.size() < arg) {
			command.async((done, e) -> 
				callback.then(Optional.empty(), Optional.of(new NullPointerException("Argument does not exist")))
			);
			return;
		}
		
		final String value = this.args.get(arg);
		
		command.async((done, e) -> {
			// String
			if (type.getClass() == String.class.getClass()) {
				callback.then(Optional.of((T) value), Optional.empty());
				return;
			}
			
			// Double
			if (type.getClass() == Double.class.getClass()) {
				callback.then(Optional.of((T)Double.valueOf(value)), Optional.empty());
				return;
			}
			
			// Integer
			if (type.getClass() == Integer.class.getClass()) {
				callback.then(Optional.of((T)Integer.valueOf(value)), Optional.empty());
				return;
			}
			
			// Float
			if (type.getClass() == Float.class.getClass()) {
				callback.then(Optional.of((T)Float.valueOf(value)), Optional.empty());
				return;
			}
			
			// Long
			if (type.getClass() == Long.class.getClass()) {
				callback.then(Optional.of((T)Long.valueOf(value)), Optional.empty());
				return;
			}
			
			// Faction
			if (type.getClass() == Faction.class.getClass()) {
				Faction faction = FactionColl.get(value);
				
				if (faction == null) {
					callback.then(Optional.empty(), Optional.empty());

				}
				return;
			}
			
			// FPlayer
			if (type.getClass() == FPlayer.class.getClass()) {
				
				command.sync((done2, e2) -> {
					// Sync
					final Player player = Bukkit.getPlayer(value);
					
					command.async((done3, e3) -> {
						// Async
						if (player != null) {
							FPlayer fplayer = FPlayerColl.get(player);
							callback.then(Optional.of((T) fplayer), Optional.empty());
							return;
						}
						
						UUID uuid = null;
						try {
							uuid = UUIDUtil.getUUIDOf(value);
						} catch (Exception exception) {
							callback.then(Optional.empty(), Optional.of(exception));
							return;
						}
						
						if (uuid == null) {
							callback.then(Optional.empty(), Optional.empty());
							return;
						}
						
						FPlayer fplayer = FPlayerColl.get(uuid);
						fplayer.asMemoryFPlayer().setName(value);
						
						callback.then(Optional.of((T) fplayer), Optional.empty());
					});
				});	
				
				return;
			}
			
			callback.then(Optional.empty(), Optional.of(new NullPointerException("Argument type not supported.")));			
		});

	}

	
}
