package net.redstoneore.legacyfactions.cmd;

import mkremins.fanciful.FancyMessage;
import net.redstoneore.legacyfactions.Permission;
import net.redstoneore.legacyfactions.Lang;
import net.redstoneore.legacyfactions.entity.Conf;
import net.redstoneore.legacyfactions.entity.FPlayer;

import org.bukkit.ChatColor;

public class CmdFactionsInvite extends FCommand {

    public CmdFactionsInvite() {
        super();
        this.aliases.add("invite");
        this.aliases.add("inv");

        this.requiredArgs.add("player name");
        //this.optionalArgs.put("", "");

        this.permission = Permission.INVITE.node;
        this.disableOnLock = true;

        senderMustBePlayer = true;
        senderMustBeMember = false;
        senderMustBeModerator = true;
        senderMustBeAdmin = false;
    }

    @Override
    public void perform() {
        FPlayer you = this.argAsBestFPlayerMatch(0);
        if (you == null) {
            return;
        }

        if (you.getFaction() == myFaction) {
            msg(Lang.COMMAND_INVITE_ALREADYMEMBER, you.getName(), myFaction.getTag());
            msg(Lang.GENERIC_YOUMAYWANT.toString() + CmdFactions.get().cmdKick.getUseageTemplate(false));
            return;
        }

        // if economy is enabled, they're not on the bypass list, and this command has a cost set, make 'em pay
        if (!payForCommand(Conf.econCostInvite, Lang.COMMAND_INVITE_TOINVITE.toString(), Lang.COMMAND_INVITE_FORINVITE.toString())) {
            return;
        }

        myFaction.invite(you);
        if (!you.isOnline()) {
            return;
        }

        // Tooltips, colors, and commands only apply to the string immediately before it.
        FancyMessage message = new FancyMessage(fme.describeTo(you, true)).tooltip(Lang.COMMAND_INVITE_CLICKTOJOIN.toString()).command("/" + Conf.baseCommandAliases.get(0) + " join " + myFaction.getTag()).then(Lang.COMMAND_INVITE_INVITEDYOU.toString()).color(ChatColor.YELLOW).tooltip(Lang.COMMAND_INVITE_CLICKTOJOIN.toString()).command("/" + Conf.baseCommandAliases.get(0) + " join " + myFaction.getTag()).then(myFaction.describeTo(you)).tooltip(Lang.COMMAND_INVITE_CLICKTOJOIN.toString()).command("/" + Conf.baseCommandAliases.get(0) + " join " + myFaction.getTag());

        message.send(you.getPlayer());

        //you.msg("%s<i> invited you to %s", fme.describeTo(you, true), myFaction.describeTo(you));
        myFaction.msg(Lang.COMMAND_INVITE_INVITED, fme.describeTo(myFaction, true), you.describeTo(myFaction));
    }

    @Override
    public String getUsageTranslation() {
        return Lang.COMMAND_INVITE_DESCRIPTION.toString();
    }

}
