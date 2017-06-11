package net.redstoneore.legacyfactions.cmd;

import net.redstoneore.legacyfactions.Permission;
import net.redstoneore.legacyfactions.Lang;
import net.redstoneore.legacyfactions.entity.Conf;
import net.redstoneore.legacyfactions.entity.FPlayer;
import net.redstoneore.legacyfactions.entity.FPlayerColl;
import net.redstoneore.legacyfactions.util.TextUtil;


public class CmdFactionsDescription extends FCommand {

    public CmdFactionsDescription() {
        this.aliases.addAll(Conf.cmdAliasesDescription);

        this.requiredArgs.add("desc");
        this.errorOnToManyArgs = false;

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
        if (!payForCommand(Conf.econCostDesc, Lang.COMMAND_DESCRIPTION_TOCHANGE, Lang.COMMAND_DESCRIPTION_FORCHANGE)) {
            return;
        }

        // Replace all the % because it messes with string formatting and this is easy way around that.
        if(Conf.allowColorCodesInFaction) {
            myFaction.setDescription(TextUtil.implode(args, " ").replaceAll("%", ""));
        }else{
            myFaction.setDescription(TextUtil.implode(args, " ").replaceAll("%", "").replaceAll("(&([a-f0-9klmnor]))", "& $2"));
        }

        if (!Conf.broadcastDescriptionChanges) {
            fme.msg(Lang.COMMAND_DESCRIPTION_CHANGED, myFaction.describeTo(fme));
            fme.sendMessage(myFaction.getDescription());
            return;
        }

        // Broadcast the description to everyone
        for (FPlayer fplayer : FPlayerColl.all(true)) {
            fplayer.msg(Lang.COMMAND_DESCRIPTION_CHANGES, myFaction.describeTo(fplayer));
            fplayer.sendMessage(myFaction.getDescription());  // players can inject "&" or "`" or "<i>" or whatever in their description; &k is particularly interesting looking
        }
    }

    @Override
    public String getUsageTranslation() {
        return Lang.COMMAND_DESCRIPTION_DESCRIPTION.toString();
    }

}
