package net.redstoneore.legacyfactions.cmd;

import org.bukkit.command.ConsoleCommandSender;

import net.redstoneore.legacyfactions.TL;
import net.redstoneore.legacyfactions.entity.Conf;
import net.redstoneore.legacyfactions.entity.Conf.Backend;
import net.redstoneore.legacyfactions.entity.persist.json.FactionsJSON;

public class CmdFactionsConvert extends FCommand {

    public CmdFactionsConvert() {
        this.aliases.add("convert");

        this.requiredArgs.add("[MYSQL|JSON]");
    }

    @Override
    public void perform() {
        if (!(this.sender instanceof ConsoleCommandSender)) {
            this.sender.sendMessage(TL.GENERIC_CONSOLEONLY.toString());
        }
        Backend nb = Backend.valueOf(this.argAsString(0).toUpperCase());
        if (nb == Conf.backEnd) {
            this.sender.sendMessage(TL.COMMAND_CONVERT_BACKEND_RUNNING.toString());
            return;
        }
        switch (nb) {
            case JSON:
                FactionsJSON.convertTo();
                break;
            default:
                this.sender.sendMessage(TL.COMMAND_CONVERT_BACKEND_INVALID.toString());
                return;

        }
        Conf.backEnd = nb;
    }

    @Override
    public TL getUsageTranslation() {
        return TL.COMMAND_CONVERT_DESCRIPTION;
    }

}
