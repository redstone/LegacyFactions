package net.redstoneore.legacyfactions.entity;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import net.redstoneore.legacyfactions.Role;

import java.util.Collection;
import java.util.UUID;
import java.util.function.Consumer;
import java.util.stream.Collectors;

public abstract class FPlayerColl {
	
	// -------------------------------------------------- //
	// STATIC FIELDS
	// -------------------------------------------------- //
	
	private static transient String currentType = null;
	protected static FPlayerColl instance = getUnsafeInstance();

	// -------------------------------------------------- //
	// STATIC METHODS
	// -------------------------------------------------- //

	/**
	 * You shouldn't use this.<br>
	 * If the implementation changes at any time this reference will not be safe to use.<br>
	 * Simply use the {@link FPlayerColl#get(Object)} method.
	 */
	public static FPlayerColl getUnsafeInstance() {
		if (currentType != Conf.backEnd.name()) {
			instance = Conf.backEnd.getHandler().getFPlayerColl();
			currentType = Conf.backEnd.name();
		}
		return instance;
	}
	
	public static FPlayer get(Object o) {
		if (o instanceof OfflinePlayer) {
			OfflinePlayer player = (OfflinePlayer) o;
			return getUnsafeInstance().getByOfflinePlayer(player);
		}
		
		// also catches Entity players 
		if (o instanceof Player) {
			Player player = (Player) o;
			return getUnsafeInstance().getByPlayer(player);
		}
		
		if (o instanceof UUID) {
			return getUnsafeInstance().getById(o.toString());
		}
		
		if (o instanceof String) {
			String value = (String) o;
			
			// Attempt id first
			FPlayer byId = getUnsafeInstance().getById(value);
			if (byId != null) return byId;
			
			// Must be a name 
			FPlayer byName = getUnsafeInstance().getByPlayer(Bukkit.getPlayer(value));
			return byName;
		}
		
		return null;
	}

	public static Collection<FPlayer> getAllOnline(Role role) {
		return all(true).stream()
			.filter(fplayer -> fplayer.getRole() == role)
			.collect(Collectors.toList());
	}
	
	public static Collection<FPlayer> getAll(Role role) {
		return all().stream()
			.filter(fplayer -> fplayer.getRole() == role)
			.collect(Collectors.toList());
	}
	
	public static Collection<FPlayer> all(Boolean mustBeOnline) {
		if (mustBeOnline) return getUnsafeInstance().getOnlinePlayers();
		
		return all();
	}
	
	public static void all(Boolean mustBeOnline, Consumer<? super FPlayer> action) {
		if (mustBeOnline) {
			getUnsafeInstance().getOnlinePlayers().forEach(action);
			return;
		}
		
		all().forEach(action);
	}
	
	public static Collection<FPlayer> all() {
		return getUnsafeInstance().getAllFPlayers();
	}
	
	/**
	 * Rewrap a collection of players into FPlayers
	 * @param players
	 * @return
	 */
	public static Collection<FPlayer> rewrap(Collection<Player> players) {
		return players.stream()
			.map(FPlayerColl::get)
			.collect(Collectors.toList());
	}
	
	public static void load() {
		getUnsafeInstance().loadColl();
	}
	
	public static void save() {
		getUnsafeInstance().forceSave();
	}
	
	public static void save(boolean sync) {
		getUnsafeInstance().forceSave(sync);
	}

	public static void all(Consumer<? super FPlayer> action) {
		getUnsafeInstance().getAllFPlayers().forEach(action);
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
	
	public abstract String getPersistType();
	
}
