package net.redstoneore.legacyfactions.task;

import org.bukkit.Bukkit;

import net.redstoneore.legacyfactions.Factions;
import net.redstoneore.legacyfactions.config.Config;
import net.redstoneore.legacyfactions.entity.persist.SaveTask;

public class TaskManager {

	// -------------------------------------------------- //
	// INSTANCE
	// -------------------------------------------------- //
	
	private static TaskManager instance = null;
	public static TaskManager get() {
		if (instance == null) {
			instance = new TaskManager();
		}
		return instance;
	}
	
	// -------------------------------------------------- //
	// FIELDS
	// -------------------------------------------------- //
	
	private Integer taskAutoLeave = null;
	private Integer saveTask = null;

	// -------------------------------------------------- //
	// METHODS
	// -------------------------------------------------- //
	
	/**
	 * Start tasks required for LegacyFactions
	 */
	public void startTasks() {
		startTaskSave();
		startTaskAutoLeave(false);
	}
	
	/**
	 * Stop tasks required for LegacyFactions
	 */
	public void stopTasks() {
		if (this.taskAutoLeave != null) {
			Factions.get().getServer().getScheduler().cancelTask(this.taskAutoLeave);
			this.taskAutoLeave = null;
		}

		if (this.saveTask != null) {
			Factions.get().getServer().getScheduler().cancelTask(this.saveTask);
			this.saveTask = null;
		}
	}
	
	public void startTaskSave() {
		if (this.saveTask == null && Config.saveToFileEveryXMinutes > 0.0) {
			long saveTicks = (long) (20 * 60 * Config.saveToFileEveryXMinutes);
			this.saveTask = Bukkit.getServer().getScheduler().scheduleSyncRepeatingTask(Factions.get(), new SaveTask(), saveTicks, saveTicks);
		}
	}
	
	public void startTaskAutoLeave(boolean restartIfRunning) {
		// Autoleave tasks
		if (this.taskAutoLeave != null) {
			if (!restartIfRunning) return;
			Factions.get().getServer().getScheduler().cancelTask(this.taskAutoLeave);
		}

		if (Config.autoLeaveRoutineRunsEveryXMinutes > 0.0) {
			long ticks = (long) (20 * 60 * Config.autoLeaveRoutineRunsEveryXMinutes);
			this.taskAutoLeave = Factions.get().getServer().getScheduler().scheduleSyncRepeatingTask(Factions.get(), new AutoLeaveTask(), ticks, ticks);
		}
	}
	
}
