package net.redstoneore.legacyfactions.task;

import org.bukkit.scheduler.BukkitRunnable;

import net.redstoneore.legacyfactions.*;
import net.redstoneore.legacyfactions.entity.Conf;
import net.redstoneore.legacyfactions.entity.FPlayer;
import net.redstoneore.legacyfactions.entity.FPlayerColl;
import net.redstoneore.legacyfactions.entity.Faction;

import java.util.ArrayList;
import java.util.ListIterator;
import java.util.logging.Level;

public class AutoLeaveProcessTask extends BukkitRunnable {

    private Boolean ready = false;
    private Boolean finished = false;
    private ListIterator<FPlayer> iterator;
    private Double toleranceMillis;

    public AutoLeaveProcessTask() {
        ArrayList<FPlayer> fplayers = (ArrayList<FPlayer>) FPlayerColl.all();
        this.iterator = fplayers.listIterator();
        this.toleranceMillis = Conf.autoLeaveAfterDaysOfInactivity * 24 * 60 * 60 * 1000;
        this.ready = true;
        this.finished = false;
    }
    
    @Override
    public void run() {
        if (Conf.autoLeaveAfterDaysOfInactivity <= 0.0 || Conf.autoLeaveRoutineMaxMillisecondsPerTick <= 0.0) {
            this.cancel();
            return;
        }
        
        // If we're not ready, stop here.
        if (!this.ready) return;
        
        // Only allow one iteration at a time, no matter how frequently the timer fires.
        this.ready = false;
        
        Long loopStartTime = System.currentTimeMillis();

        while (iterator.hasNext()) {
            Long now = System.currentTimeMillis();

            // if this iteration has been running for maximum time, stop to take a breather until next tick
            if (now > loopStartTime + Conf.autoLeaveRoutineMaxMillisecondsPerTick) {
            	this. ready = true;
                return;
            }

            FPlayer fplayer = iterator.next();

            // Check if they should be exempt from this.
            if (!fplayer.willAutoLeave()) {
                Factions.get().debug(Level.INFO, fplayer.getName() + " was going to be auto-removed but was set not to.");
                continue;
            }

            if (fplayer.isOffline() && now - fplayer.getLastLoginTime() > toleranceMillis) {
                if (Conf.logFactionLeave || Conf.logFactionKick) {
                    Factions.get().log("Player " + fplayer.getName() + " was auto-removed due to inactivity.");
                }

                // if player is faction admin, sort out the faction since he's going away
                if (fplayer.getRole() == Role.ADMIN) {
                    Faction faction = fplayer.getFaction();
                    if (faction != null) {
                        fplayer.getFaction().promoteNewLeader();
                    }
                }

                fplayer.leave(false);
                iterator.remove();  // go ahead and remove this list's link to the FPlayer object
                
                if (Conf.autoLeaveDeleteFPlayerData) {
                    fplayer.remove();
                }
            }
        }

        // Finish up.
        this.cancel();
    }
    
    @Override
    public void cancel() {
    	this.ready = false;
    	this.finished = true;
        super.cancel();
    }

    public Boolean isFinished() {
        return this.finished;
    }
}
