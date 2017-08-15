package net.redstoneore.legacyfactions;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import net.redstoneore.legacyfactions.cmd.MCommand;

/**
 * This class contains volatile information that is not stored and is simply stored in memory
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
	
	private List<MCommand<?>> baseCommands = new ArrayList<>();
	public List<MCommand<?>> baseCommands() {
		return this.baseCommands;
	}
	
	// -------------------------------------------------- //
	// STUCK TIMERS
	// -------------------------------------------------- //
	
	private Map<UUID, Long> stuckTimers = new HashMap<>();
	public Map<UUID, Long> stuckTimers() {
		return this.stuckTimers;
	}
	
	// -------------------------------------------------- //
	// STUCK MAP
	// -------------------------------------------------- //
	
	public Map<UUID, Integer> stuckMap = new HashMap<>();
	public Map<UUID, Integer> stuckMap() {
		return this.stuckMap;
	}


	
}
