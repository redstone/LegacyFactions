package net.redstoneore.legacyfactions.scoreboards.sidebar;

import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;

import net.redstoneore.legacyfactions.entity.Conf;
import net.redstoneore.legacyfactions.entity.FPlayer;
import net.redstoneore.legacyfactions.scoreboards.FSidebarProvider;

public class FDefaultSidebar extends FSidebarProvider {

    @Override
    public String getTitle(FPlayer fplayer) {
        return replaceTags(fplayer, Conf.scoreboardDefaultTitle);
    }

    @Override
    public List<String> getLines(FPlayer fplayer) {
        if (fplayer.hasFaction()) {
            return this.getOutput(fplayer, Conf.scoreboardDefault);
        } else {
            if (Conf.scoreboardFactionlessEnabled) {
                return this.getOutput(fplayer, Conf.scoreboardFactionless);
            }
        }
        return this.getOutput(fplayer, Conf.scoreboardDefault); // no faction, factionless-board disabled
    }

    public List<String> getOutput(FPlayer fplayer, List<String> lines) {
    	
        if (lines == null || lines.isEmpty()) {
            return new ArrayList<>();
        }

        ListIterator<String> it = lines.listIterator();
        while (it.hasNext()) {
            it.set(replaceTags(fplayer, it.next()));
        }
        return lines;
    }
}
