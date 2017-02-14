package com.massivecraft.factions.entity.persist;

import com.massivecraft.factions.Factions;
import com.massivecraft.factions.entity.Board;
import com.massivecraft.factions.entity.FPlayerColl;
import com.massivecraft.factions.entity.FactionColl;
import com.massivecraft.factions.zcore.MPlugin;

public class SaveTask implements Runnable {

    private static boolean running = false;

    MPlugin p;

    public SaveTask(MPlugin p) {
        this.p = p;
    }

    public void run() {
        if (!p.getAutoSave() || running) {
            return;
        }
        running = true;
        Factions.get().preAutoSave();
        FactionColl.getInstance().forceSave(false);
        FPlayerColl.getInstance().forceSave(false);
        Board.getInstance().forceSave(false);
        Factions.get().postAutoSave();
        running = false;
    }
}
