package com.massivecraft.legacyfactions.entity.persist;

import com.massivecraft.legacyfactions.Factions;
import com.massivecraft.legacyfactions.FactionsPluginBase;
import com.massivecraft.legacyfactions.entity.Board;
import com.massivecraft.legacyfactions.entity.FPlayerColl;
import com.massivecraft.legacyfactions.entity.FactionColl;

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
        Board.getInstance().forceSave(false);
        Factions.get().postAutoSave();
        running = false;
    }
}
