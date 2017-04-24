package net.redstoneore.legacyfactions.entity.persist;

import net.redstoneore.legacyfactions.Factions;
import net.redstoneore.legacyfactions.FactionsPluginBase;
import net.redstoneore.legacyfactions.entity.Board;
import net.redstoneore.legacyfactions.entity.FPlayerColl;
import net.redstoneore.legacyfactions.entity.FactionColl;

public class SaveTask implements Runnable {

    private static boolean running = false;

    FactionsPluginBase p;

    public SaveTask(FactionsPluginBase p) {
        this.p = p;
    }

    public void run() {
        if (!p.getAutoSave() || running) {
            return;
        }
        running = true;
        Factions.get().preAutoSave();
        FactionColl.getInstance().forceSave(false);
        FPlayerColl.save(false);
        Board.get().forceSave(false);
        Factions.get().postAutoSave();
        running = false;
    }
}
