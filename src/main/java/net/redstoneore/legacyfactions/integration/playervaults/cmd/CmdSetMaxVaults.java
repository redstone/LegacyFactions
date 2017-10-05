package net.redstoneore.legacyfactions.integration.playervaults.cmd;

import org.bukkit.ChatColor;

import net.redstoneore.legacyfactions.Permission;
import net.redstoneore.legacyfactions.cmd.FCommand;
import net.redstoneore.legacyfactions.entity.Faction;
import net.redstoneore.legacyfactions.lang.Lang;

public class CmdSetMaxVaults extends FCommand {

    public CmdSetMaxVaults() {
        this.aliases.add("setmaxvaults");
        this.aliases.add("smv");

        this.requiredArgs.add("faction");
        this.requiredArgs.add("number");

        this.permission = Permission.SETMAXVAULTS.getNode();
        this.disableOnLock = false;

        senderMustBePlayer = false;
        senderMustBeMember = false;
        senderMustBeModerator = false;
        senderMustBeAdmin = false;
    }

    @Override
    public void perform() {
        Faction targetFaction = argAsFaction(0);
        int value = argAsInt(1, -1);
        if(value < 0) {
            sender.sendMessage(ChatColor.RED + "Number must be greater than 0.");
            return;
        }

        if(targetFaction == null) {
            sender.sendMessage(ChatColor.RED + "Couldn't find Faction: " + ChatColor.YELLOW + argAsString(0));
            return;
        }

        targetFaction.setMaxVaults(value);
        sender.sendMessage(Lang.COMMAND_SETMAXVAULTS_SUCCESS.format(targetFaction.getTag(), value));
    }

    @Override
    public String getUsageTranslation() {
        return Lang.COMMAND_SETMAXVAULTS_DESCRIPTION.toString();
    }
}
