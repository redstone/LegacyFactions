package net.redstoneore.legacyfactions;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.Vector;
import java.util.concurrent.ConcurrentHashMap;

import net.redstoneore.legacyfactions.cmd.MCommand;
import net.redstoneore.legacyfactions.struct.InteractAttemptSpam;

/**
 * This class contains volatile information that is not kept and is simply stored in memory
 * and never saved for later use.
 */
public class Volatile {

	// -------------------------------------------------- //
	// INSTANCE
	// -------------------------------------------------- //
	
	private static Volatile instance = new Volatile();
	public static Volatile get() { return instance; }
	
	// -------------------------------------------------- //
	// BASE COMMANDS
	// -------------------------------------------------- //
	
	private List<MCommand<?>> baseCommands = new Vector<>();
	public List<MCommand<?>> baseCommands() {
		return this.baseCommands;
	}
	
	// -------------------------------------------------- //
	// STUCK TIMERS
	// -------------------------------------------------- //
	
	private Map<UUID, Long> stuckTimers = new ConcurrentHashMap<>();
	public Map<UUID, Long> stuckTimers() {
		return this.stuckTimers;
	}
	
	// -------------------------------------------------- //
	// STUCK MAP
	// -------------------------------------------------- //
	
	private Map<UUID, Integer> stuckMap = new ConcurrentHashMap<>();
	public Map<UUID, Integer> stuckMap() {
		return this.stuckMap;
	}

	// -------------------------------------------------- //
	// SHOW TIMES
	// -------------------------------------------------- //
	
	// Holds the next time a player can have a map shown.
	private Map<UUID, Long> showTimes = new ConcurrentHashMap<>();
	public Map<UUID, Long> showTimes() {
		return this.showTimes;
	}
	
	// -------------------------------------------------- //
	// INTERACT SPAMMERS
	// -------------------------------------------------- //
	
	private Map<String, InteractAttemptSpam> interactSpammers = new ConcurrentHashMap<>();
	public Map<String, InteractAttemptSpam> interactSpammers() {
		return this.interactSpammers;
	}
	
}
