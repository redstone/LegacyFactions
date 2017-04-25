package net.redstoneore.legacyfactions.scoreboards;

import java.util.List;

import net.redstoneore.legacyfactions.Factions;
import net.redstoneore.legacyfactions.Lang;
import net.redstoneore.legacyfactions.entity.FPlayer;
import net.redstoneore.legacyfactions.entity.Faction;
import net.redstoneore.legacyfactions.util.TagUtil;

public abstract class FSidebarProvider {
	
	// -------------------------------------------------- //
	// METHODS
	// -------------------------------------------------- //
	
    public String replaceTags(FPlayer fPlayer, String s) {
        return this.qualityAssure(TagUtil.parsePlain(fPlayer, s));
    }

    public String replaceTags(Faction faction, FPlayer fPlayer, String s) {
        return this.qualityAssure(TagUtil.parsePlain(faction, fPlayer, s));
    }

    private String qualityAssure(String line) {
        if (line.contains("{notFrozen}") || line.contains("{notPermanent}")) {
            return "n/a"; // we dont support support these error variables in scoreboards
        }
        if (line.contains("{ig}")) {
            // since you can't really fit a whole "Faction Home: world, x, y, z" on one line
            // we assume it's broken up into two lines, so returning our tl will suffice.
            return Lang.COMMAND_SHOW_NOHOME.toString();
        }
        return Factions.get().getTextUtil().parse(line); // finally add color :)
    }
    
	// -------------------------------------------------- //
	// ABSTRACT METHODS
	// -------------------------------------------------- //
    
    public abstract String getTitle(FPlayer fplayer);

    public abstract List<String> getLines(FPlayer fplayer);
    
}
