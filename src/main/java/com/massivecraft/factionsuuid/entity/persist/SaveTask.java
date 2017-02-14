package com.massivecraft.factionsuuid.entity.persist;

import com.massivecraft.factionsuuid.Factions;
import com.massivecraft.factionsuuid.entity.Board;
import com.massivecraft.factionsuuid.entity.FPlayerColl;
import com.massivecraft.factionsuuid.entity.FactionColl;
import com.massivecraft.factionsuuid.zcore.MPlugin;

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
