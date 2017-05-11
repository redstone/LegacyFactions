package net.redstoneore.legacyfactions.cmd;

import net.redstoneore.legacyfactions.Factions;
import net.redstoneore.legacyfactions.Permission;
import net.redstoneore.legacyfactions.Lang;
import net.redstoneore.legacyfactions.entity.Conf;

public class CmdFactionsReload extends FCommand {

    public CmdFactionsReload() {
        this.aliases.addAll(Conf.cmdAliasesReload);

        this.optionalArgs.put("file", "all");

        this.permission = Permission.RELOAD.node;
        this.disableOnLock = false;

        senderMustBePlayer = false;
        senderMustBeMember = false;
        senderMustBeModerator = false;
        senderMustBeAdmin = false;
    }

    @Override
    public void perform() {
        long timeInitStart = System.currentTimeMillis();
        Conf.load();
        Factions.get().reloadConfig();
        
        Lang.reload();
        
        long timeReload = (System.currentTimeMillis() - timeInitStart);

        msg(Lang.COMMAND_RELOAD_TIME, timeReload);
    }

    @Override
    public String getUsageTranslation() {
        return Lang.COMMAND_RELOAD_DESCRIPTION.toString();
    }
}
