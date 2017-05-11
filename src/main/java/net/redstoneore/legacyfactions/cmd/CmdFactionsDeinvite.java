package net.redstoneore.legacyfactions.cmd;

import mkremins.fanciful.FancyMessage;
import net.redstoneore.legacyfactions.Permission;
import net.redstoneore.legacyfactions.Lang;
import net.redstoneore.legacyfactions.entity.Conf;
import net.redstoneore.legacyfactions.entity.FPlayer;
import net.redstoneore.legacyfactions.entity.FPlayerColl;

import org.bukkit.ChatColor;

public class CmdFactionsDeinvite extends FCommand {

    public CmdFactionsDeinvite() {
        this.aliases.addAll(Conf.cmdAliasesDeinvite);

        this.optionalArgs.put("player name", "name");
        //this.optionalArgs.put("", "");

        this.permission = Permission.DEINVITE.node;
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
            FancyMessage msg = new FancyMessage(Lang.COMMAND_DEINVITE_CANDEINVITE.toString()).color(ChatColor.GOLD);
            for (String id : myFaction.getInvites()) {
                FPlayer fp = FPlayerColl.get(id);
                String name = fp != null ? fp.getName() : id;
                msg.then(name + " ").color(ChatColor.WHITE).tooltip(Lang.COMMAND_DEINVITE_CLICKTODEINVITE.format(name)).command("/" + Conf.baseCommandAliases.get(0) + " deinvite " + name);
            }
            sendFancyMessage(msg);
            return;
        }

        if (you.getFaction() == myFaction) {
            msg(Lang.COMMAND_DEINVITE_ALREADYMEMBER, you.getName(), myFaction.getTag());
            msg(Lang.COMMAND_DEINVITE_MIGHTWANT, CmdFactions.get().cmdKick.getUseageTemplate(false));
            return;
        }

        myFaction.deinvite(you);

        you.msg(Lang.COMMAND_DEINVITE_REVOKED, fme.describeTo(you), myFaction.describeTo(you));

        myFaction.msg(Lang.COMMAND_DEINVITE_REVOKES, fme.describeTo(myFaction), you.describeTo(myFaction));
    }

    @Override
    public String getUsageTranslation() {
        return Lang.COMMAND_DEINVITE_DESCRIPTION.toString();
    }

}
