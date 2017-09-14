package net.redstoneore.legacyfactions.flag;

import java.util.Collections;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import net.redstoneore.legacyfactions.Volatile;
import net.redstoneore.legacyfactions.config.Config;
import net.redstoneore.legacyfactions.util.ConditionalBoolean;

public class Flags {

	// -------------------------------------------------- //
	// STATIC FIELDS
	// -------------------------------------------------- // 
	
	public static final Flag PERMANENT = Flag.of("permanent", false, Volatile.get().provider());
	public static final Flag EXPLOSIONS = Flag.of("explosions", true, Volatile.get().provider());
	public static final Flag PEACEFUL = Flag.of("peaceful", false, Volatile.get().provider());
	public static final Flag OPEN = Flag.of("open", ConditionalBoolean.of(() -> Config.newFactionsDefaultOpen), Volatile.get().provider());
	
	// Registered flags
	private static Set<Flag> flags = Collections.newSetFromMap(new ConcurrentHashMap<Flag, Boolean>());
	
	// -------------------------------------------------- //
	// STATIC METHODS
	// -------------------------------------------------- // 
	
	/**
	 * Returns an unmodifiable set of all flags.
	 * @return Unmodifiable set of all flags.
	 */
	public static Set<Flag> getAll() {
		return Collections.unmodifiableSet(flags);
	}
	
	/**
	 * Attempts to get a flag by name. Returns an {@link Optional} which will contain the flag if
	 * it is found.
	 * @param name Name of the flag to search for.
	 * @return {@link Optional} which will contain the flag if found.
	 */
	public static Optional<Flag> get(String name) {
		return flags.stream().filter(flag -> flag.getName().equalsIgnoreCase(name) || flag.getStoredName().equalsIgnoreCase(name)).findFirst();
	}
	
	/**
	 * Remove a flag from registered flags 
	 * @param flag Flag to remove.
	 * @return true if it was registered and removed.
	 */
	public static boolean remove(Flag flag) {
		return flags.remove(flag);
	}
	
	/**
	 * Adds a flag to registered flags 
	 * @param flag Flag to add.
	 * @return true if it was added
	 */
	public static boolean add(Flag flag) {
		return flags.add(flag);
	}
	
	/**
	 * Called on LegacyFactions startup to add default flags.
	 */
	public static void init() {
		if (flags.size() > 0) return;
		
		flags.add(Flag.of("permanent", false, Volatile.get().provider()));
		flags.add(Flag.of("explosions", true, Volatile.get().provider()));
		flags.add(Flag.of("peaceful", false, Volatile.get().provider()));
		flags.add(Flag.of("open", Config.newFactionsDefaultOpen == true, Volatile.get().provider()));
	}
	
}
