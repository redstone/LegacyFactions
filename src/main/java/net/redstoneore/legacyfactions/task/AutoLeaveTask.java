package net.redstoneore.legacyfactions.task;

import net.redstoneore.legacyfactions.Factions;
import net.redstoneore.legacyfactions.entity.Conf;

public class AutoLeaveTask implements Runnable {

    private static AutoLeaveProcessTask task;
    double rate;

    public AutoLeaveTask() {
        this.rate = Conf.autoLeaveRoutineRunsEveryXMinutes;
    }

    public synchronized void run() {
        if (task != null && !task.isFinished()) {
            return;
        }

        task = new AutoLeaveProcessTask();
        task.runTaskTimer(Factions.get(), 1, 1);

        // maybe setting has been changed? if so, restart this task at new rate
        if (this.rate != Conf.autoLeaveRoutineRunsEveryXMinutes) {
            TaskManager.get().startTaskAutoLeave(true);
        }
    }
}
