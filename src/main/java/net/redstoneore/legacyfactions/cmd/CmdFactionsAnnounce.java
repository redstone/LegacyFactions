package net.redstoneore.legacyfactions.cmd;

import org.apache.commons.lang.StringUtils;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import net.redstoneore.legacyfactions.Permission;
import net.redstoneore.legacyfactions.TL;
import net.redstoneore.legacyfactions.entity.FPlayer;

public class CmdFactionsAnnounce extends FCommand {

    public CmdFactionsAnnounce() {
        super();
        this.aliases.add("ann");
        this.aliases.add("announce");

        this.requiredArgs.add("message");
        this.errorOnToManyArgs = false;

        this.permission = Permission.ANNOUNCE.node;
        this.disableOnLock = false;

        senderMustBePlayer = true;
        senderMustBeMember = true;
        senderMustBeModerator = true;
    }

    @Override
    public void perform() {
        String prefix = ChatColor.GREEN + myFaction.getTag() + ChatColor.YELLOW + " [" + ChatColor.GRAY + me.getName() + ChatColor.YELLOW + "] " + ChatColor.RESET;
        String message = StringUtils.join(args, " ");

        for (Player player : myFaction.getOnlinePlayers()) {
            player.sendMessage(prefix + message);
        }

        // Add for offline players.
        for (FPlayer fp : myFaction.getFPlayersWhereOnline(false)) {
            myFaction.addAnnouncement(fp, prefix + message);
        }
    }

    @Override
    public String getUsageTranslation() {
        return TL.COMMAND_ANNOUNCE_DESCRIPTION.toString();
    }

}
