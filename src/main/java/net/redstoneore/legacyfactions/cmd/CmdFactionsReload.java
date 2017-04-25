package net.redstoneore.legacyfactions.cmd;

import net.redstoneore.legacyfactions.Factions;
import net.redstoneore.legacyfactions.Permission;
import net.redstoneore.legacyfactions.TL;
import net.redstoneore.legacyfactions.entity.Conf;

public class CmdFactionsReload extends FCommand {

    public CmdFactionsReload() {
        super();
        this.aliases.add("reload");

        //this.requiredArgs.add("");
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
        Factions.get().loadLang();
        long timeReload = (System.currentTimeMillis() - timeInitStart);

        msg(TL.COMMAND_RELOAD_TIME, timeReload);
    }

    @Override
    public String getUsageTranslation() {
        return TL.COMMAND_RELOAD_DESCRIPTION.toString();
    }
}
