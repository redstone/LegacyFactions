package net.redstoneore.legacyfactions.cmd;

import mkremins.fanciful.FancyMessage;
import net.redstoneore.legacyfactions.Permission;
import net.redstoneore.legacyfactions.Role;
import net.redstoneore.legacyfactions.Lang;
import net.redstoneore.legacyfactions.entity.Conf;
import net.redstoneore.legacyfactions.entity.FPlayer;
import net.redstoneore.legacyfactions.entity.Faction;

import org.bukkit.ChatColor;

public class CmdFactionsColeader extends FCommand {

    public CmdFactionsColeader() {
        this.aliases.addAll(Conf.cmdAliasesColeader);

        this.requiredArgs.add("player name");

        this.permission = Permission.COLEADER.node;
        this.disableOnLock = true;

        senderMustBePlayer = false;
        senderMustBeMember = true;
        senderMustBeModerator = false;
        senderMustBeAdmin = true;
    }

    @Override
    public void perform() {
        FPlayer you = this.argAsBestFPlayerMatch(0);
        if (you == null) {
            FancyMessage msg = new FancyMessage(Lang.COMMAND_COLEADER_CANDIDATES.toString()).color(ChatColor.GOLD);
            for (FPlayer player : myFaction.getFPlayersWhereRole(Role.NORMAL)) {
                String s = player.getName();
                msg.then(s + " ").color(ChatColor.WHITE).tooltip(Lang.COMMAND_COLEADER_CLICKTOPROMOTE.toString() + s).command("/" + Conf.baseCommandAliases.get(0) + " coleader " + s);
            }

            sendFancyMessage(msg);
            return;
        }

        boolean permAny = Permission.COLEADER_ANY.has(sender, false);
        Faction targetFaction = you.getFaction();

        if (targetFaction != myFaction && !permAny) {
            msg(Lang.COMMAND_COLEADER_NOTMEMBER, you.describeTo(fme, true));
            return;
        }

        if (fme != null && fme.getRole() != Role.ADMIN && !permAny) {
            msg(Lang.COMMAND_COLEADER_NOTADMIN);
            return;
        }

        if (you == fme && !permAny) {
            msg(Lang.COMMAND_COLEADER_SELF);
            return;
        }

        if (you.getRole() == Role.ADMIN) {
            msg(Lang.COMMAND_COLEADER_TARGETISADMIN);
            return;
        }

        if (you.getRole() == Role.COLEADER) {
            // Revoke
            you.setRole(Role.NORMAL);
            targetFaction.msg(Lang.COMMAND_COLEADER_REVOKED, you.describeTo(targetFaction, true));
            msg(Lang.COMMAND_COLEADER_REVOKES, you.describeTo(fme, true));
        } else {
            // Give
            you.setRole(Role.COLEADER);
            targetFaction.msg(Lang.COMMAND_COLEADER_PROMOTED, you.describeTo(targetFaction, true));
            msg(Lang.COMMAND_COLEADER_PROMOTES, you.describeTo(fme, true));
        }
    }

    @Override
    public String getUsageTranslation() {
        return Lang.COMMAND_COLEADER_DESCRIPTION.toString();
    }

}
