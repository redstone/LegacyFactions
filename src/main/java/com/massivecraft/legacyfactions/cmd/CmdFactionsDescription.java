package com.massivecraft.legacyfactions.cmd;

import com.massivecraft.legacyfactions.Permission;
import com.massivecraft.legacyfactions.TL;
import com.massivecraft.legacyfactions.entity.Conf;
import com.massivecraft.legacyfactions.entity.FPlayer;
import com.massivecraft.legacyfactions.entity.FPlayerColl;
import com.massivecraft.legacyfactions.util.TextUtil;

public class CmdFactionsDescription extends FCommand {

    public CmdFactionsDescription() {
        super();
        this.aliases.add("desc");
        this.aliases.add("description");

        this.requiredArgs.add("desc");
        this.errorOnToManyArgs = false;
        //this.optionalArgs

        this.permission = Permission.DESCRIPTION.node;
        this.disableOnLock = true;

        senderMustBePlayer = true;
        senderMustBeMember = false;
        senderMustBeModerator = true;
        senderMustBeAdmin = false;
    }

    @Override
    public void perform() {
        // if economy is enabled, they're not on the bypass list, and this command has a cost set, make 'em pay
        if (!payForCommand(Conf.econCostDesc, TL.COMMAND_DESCRIPTION_TOCHANGE, TL.COMMAND_DESCRIPTION_FORCHANGE)) {
            return;
        }

        // since "&" color tags seem to work even through plain old FPlayer.sendMessage() for some reason, we need to break those up
        // And replace all the % because it messes with string formatting and this is easy way around that.
        myFaction.setDescription(TextUtil.implode(args, " ").replaceAll("%", "").replaceAll("(&([a-f0-9klmnor]))", "& $2"));

        if (!Conf.broadcastDescriptionChanges) {
            fme.msg(TL.COMMAND_DESCRIPTION_CHANGED, myFaction.describeTo(fme));
            fme.sendMessage(myFaction.getDescription());
            return;
        }

        // Broadcast the description to everyone
        for (FPlayer fplayer : FPlayerColl.getAllOnline()) {
            fplayer.msg(TL.COMMAND_DESCRIPTION_CHANGES, myFaction.describeTo(fplayer));
            fplayer.sendMessage(myFaction.getDescription());  // players can inject "&" or "`" or "<i>" or whatever in their description; &k is particularly interesting looking
        }
    }

    @Override
    public TL getUsageTranslation() {
        return TL.COMMAND_DESCRIPTION_DESCRIPTION;
    }

}
