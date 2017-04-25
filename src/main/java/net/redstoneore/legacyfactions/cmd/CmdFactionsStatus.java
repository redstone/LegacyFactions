package net.redstoneore.legacyfactions.cmd;

import org.apache.commons.lang.time.DurationFormatUtils;
import org.bukkit.ChatColor;

import net.redstoneore.legacyfactions.Permission;
import net.redstoneore.legacyfactions.TL;
import net.redstoneore.legacyfactions.entity.FPlayer;

import java.util.ArrayList;

public class CmdFactionsStatus extends FCommand {

    public CmdFactionsStatus() {
        super();
        this.aliases.add("status");
        this.aliases.add("s");

        this.permission = Permission.STATUS.node;

        senderMustBePlayer = true;
        senderMustBeMember = true;
        senderMustBeModerator = false;
        senderMustBeAdmin = false;
    }

    @Override
    public void perform() {
        ArrayList<String> ret = new ArrayList<String>();
        for (FPlayer fp : myFaction.getFPlayers()) {
            String humanized = DurationFormatUtils.formatDurationWords(System.currentTimeMillis() - fp.getLastLoginTime(), true, true) + TL.COMMAND_STATUS_AGOSUFFIX;
            String last = fp.isOnline() ? ChatColor.GREEN + TL.COMMAND_STATUS_ONLINE.toString() : (System.currentTimeMillis() - fp.getLastLoginTime() < 432000000 ? ChatColor.YELLOW + humanized : ChatColor.RED + humanized);
            String power = ChatColor.YELLOW + String.valueOf(fp.getPowerRounded()) + " / " + String.valueOf(fp.getPowerMaxRounded()) + ChatColor.RESET;
            ret.add(String.format(TL.COMMAND_STATUS_FORMAT.toString(), ChatColor.GOLD + fp.getRole().getPrefix() + fp.getName() + ChatColor.RESET, power, last).trim());
        }
        fme.sendMessage(ret);
    }

    @Override
    public String getUsageTranslation() {
        return TL.COMMAND_STATUS_DESCRIPTION.toString();
    }

}
