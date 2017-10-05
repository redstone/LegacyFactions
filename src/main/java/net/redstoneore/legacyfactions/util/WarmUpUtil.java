package net.redstoneore.legacyfactions.util;

import net.redstoneore.legacyfactions.Factions;
import net.redstoneore.legacyfactions.entity.FPlayer;
import net.redstoneore.legacyfactions.lang.Lang;

public class WarmUpUtil {

    /**
     * Process warmup.<br>
     * Note: for translations: %s = action, %d = delay
     * @param player         The player to notify.
     * @param warmup         The warmup
     * @param translationKey The translation key used for notifying.
     * @param action         The action, inserted into the notification message.
     * @param runnable       The task to run after the delay. If the delay is 0, the task is instantly ran.
     * @param delay          The time used, in seconds, for the delay.
     *                    
     */
    public static void process(final FPlayer player, Warmup warmup, Lang translationKey, String action, final Runnable runnable, long delay) {
        if (delay > 0) {
            if (player.isWarmingUp()) {
                player.sendMessage(Lang.WARMUPS_ALREADY);
            } else {
                player.sendMessage(translationKey.format(action, delay));
                int id = Factions.get().getServer().getScheduler().runTaskLater(Factions.get(), new Runnable() {
                    @Override
                    public void run() {
                        player.stopWarmup();
                        runnable.run();
                    }
                }, delay * 20).getTaskId();
                player.addWarmup(warmup, id);
            }
        } else {
            runnable.run();
        }
    }

    public enum Warmup {
        HOME, WARP;
    }

}
