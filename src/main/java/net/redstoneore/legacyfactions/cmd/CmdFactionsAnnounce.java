package net.redstoneore.legacyfactions.cmd;

import org.apache.commons.lang.StringUtils;

import net.redstoneore.legacyfactions.Permission;
import net.redstoneore.legacyfactions.Lang;

public class CmdFactionsAnnounce extends FCommand {
	
	// -------------------------------------------------- //
	// CONSTRUCT
	// -------------------------------------------------- //
	
    public CmdFactionsAnnounce() {
        this.aliases.add("announce");
        this.aliases.add("ann");
        
        this.requiredArgs.add("message");
        this.errorOnToManyArgs = false;
        
        this.permission = Permission.ANNOUNCE.node;
        this.disableOnLock = false;
        
        this.senderMustBePlayer = true;
        this.senderMustBeMember = true;
        this.senderMustBeModerator = true;
    }
    
	// -------------------------------------------------- //
	// METHODS
	// -------------------------------------------------- //

    @Override
    public void perform() {
    	// Compile announcement
        String message = StringUtils.join(args, " ");
        
        String announcement = Lang.COMMAND_ANNOUNCE_TEMPALTE.toString()
        		.replaceAll("<tag>", this.myFaction.getTag())
        		.replaceAll("<player>", this.me.getName())
        		.replaceAll("<message>", message);
        
        // Notify online players
        myFaction.getFPlayersWhereOnline(true).forEach(
        	fplayer -> fplayer.msg(announcement)
        );
        
        // Store announcement for offline players
        myFaction.getFPlayersWhereOnline(false).forEach(
        	fplayer -> this.myFaction.addAnnouncement(fplayer, announcement)
        );
    }

    @Override
    public String getUsageTranslation() {
        return Lang.COMMAND_ANNOUNCE_DESCRIPTION.toString();
    }

}
