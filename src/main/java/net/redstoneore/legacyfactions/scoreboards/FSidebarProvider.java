package net.redstoneore.legacyfactions.scoreboards;

import java.util.List;

import net.redstoneore.legacyfactions.entity.FPlayer;
import net.redstoneore.legacyfactions.entity.Faction;
import net.redstoneore.legacyfactions.lang.Lang;
import net.redstoneore.legacyfactions.util.TagUtil;
import net.redstoneore.legacyfactions.util.TextUtil;

public abstract class FSidebarProvider {
	
	// -------------------------------------------------- //
	// METHODS
	// -------------------------------------------------- //
	
    public String replaceTags(FPlayer fplayer, String string) {
        return this.qualityAssure(TagUtil.parsePlain(fplayer, string));
    }

    public String replaceTags(Faction faction, FPlayer fplayer, String string) {
        return this.qualityAssure(TagUtil.parsePlain(faction, fplayer, string));
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
        return TextUtil.get().parse(line); // finally add color :)
    }
    
    
	// -------------------------------------------------- //
	// ABSTRACT METHODS
	// -------------------------------------------------- //
    
    public abstract String getTitle(FPlayer fplayer);

    public abstract List<String> getLines(FPlayer fplayer);
    
}
