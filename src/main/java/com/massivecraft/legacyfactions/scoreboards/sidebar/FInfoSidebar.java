package com.massivecraft.legacyfactions.scoreboards.sidebar;

import java.util.List;
import java.util.ListIterator;

import com.massivecraft.legacyfactions.Factions;
import com.massivecraft.legacyfactions.entity.FPlayer;
import com.massivecraft.legacyfactions.entity.Faction;
import com.massivecraft.legacyfactions.scoreboards.FSidebarProvider;

public class FInfoSidebar extends FSidebarProvider {
    private final Faction faction;

    public FInfoSidebar(Faction faction) {
        this.faction = faction;
    }

    @Override
    public String getTitle(FPlayer fplayer) {
        return faction.getRelationTo(fplayer).getColor() + faction.getTag();
    }

    @Override
    public List<String> getLines(FPlayer fplayer) {
        List<String> lines = Factions.get().getConfig().getStringList("scoreboard.finfo");

        ListIterator<String> it = lines.listIterator();
        while (it.hasNext()) {
            it.set(replaceTags(faction, fplayer, it.next()));
        }
        return lines;
    }
}