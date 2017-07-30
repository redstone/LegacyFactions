package net.redstoneore.legacyfactions.entity;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import net.redstoneore.legacyfactions.Role;
import net.redstoneore.legacyfactions.entity.persist.json.JSONFPlayers;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.function.Consumer;

public abstract class FPlayerColl {
	
	protected static FPlayerColl instance = getImpl();
	
	private static FPlayerColl getImpl() {
		switch (Conf.backEnd) {
			case JSON:
				return new JSONFPlayers();
		}
		return null;
	}
	
	/**
	 * You shouldn't use this, if the implementation changes at
	 * any time this reference will not be safe to use.
	 * @return
	 */
	public static FPlayerColl getUnsafeInstance() {
		return instance;
	}
	
	public static FPlayer get(Object o) {
		if (o instanceof OfflinePlayer) {
			OfflinePlayer player = (OfflinePlayer) o;
			return instance.getByOfflinePlayer(player);
		}
		
		// also catches Entity players 
		if (o instanceof Player) {
			Player player = (Player) o;
			return instance.getByPlayer(player);
		}
		
		if (o instanceof String) {
			String value = (String) o;
			
			// Attempt id first
			FPlayer byId = instance.getById(value);
			if (byId != null) return byId;
			
			// Must be a name 
			FPlayer byName = instance.getByPlayer(Bukkit.getPlayer(value));
			return byName;
		}
		
		return null;
	}

	public static Collection<FPlayer> getAllOnline(Role role) {
		Collection<FPlayer> all = new ArrayList<FPlayer>();
		
		for (FPlayer fplayer : all()) {
			if (fplayer.getRole() == role) {
				all.add(fplayer);
			}
		}
		
		return all;
	}
	
	public static Collection<FPlayer> getAll(Role role) {
		Collection<FPlayer> all = new ArrayList<>();
		
		for (FPlayer fplayer : all()) {
			if (fplayer.getRole() == role) {
				all.add(fplayer);
			}
		}
		
		return all;
	}
	
	public static Collection<FPlayer> all(Boolean mustBeOnline) {
		if (mustBeOnline) return instance.getOnlinePlayers();
		
		return all();
	}
	
	public static void all(Boolean mustBeOnline, Consumer<? super FPlayer> action) {
		if (mustBeOnline) {
			instance.getOnlinePlayers().forEach(action);
			return;
		}
		
		all().forEach(action);
	}
	
	public static Collection<FPlayer> all() {
		return instance.getAllFPlayers();
	}
	
	/**
	 * Rewrap a collection of players into FPlayers
	 * @param players
	 * @return
	 */
	public static Collection<FPlayer> rewrap(Collection<Player> players) {
		List<FPlayer> fplayers = new ArrayList<>();

		players.forEach(player -> {
			fplayers.add(get(player));
		});
		
		return fplayers;
	}
	
	public static void load() {
		instance.loadColl();
	}
	
	public static void save() {
		instance.forceSave();
	}
	
	public static void save(boolean sync) {
		instance.forceSave(sync);
	}

	public static void all(Consumer<? super FPlayer> action) {
		instance.getAllFPlayers().forEach(action);
	}
	
	public abstract void clean();

	public abstract Collection<FPlayer> getOnlinePlayers();

	public abstract FPlayer getByPlayer(Player player);

	public abstract Collection<FPlayer> getAllFPlayers();

	public abstract void forceSave();

	public abstract void forceSave(boolean sync);

	public abstract FPlayer getByOfflinePlayer(OfflinePlayer player);

	public abstract FPlayer getById(String string);

	public abstract void loadColl();
	
}
