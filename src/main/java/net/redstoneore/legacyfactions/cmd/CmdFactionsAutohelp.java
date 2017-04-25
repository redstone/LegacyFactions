package net.redstoneore.legacyfactions.cmd;

import java.util.ArrayList;

import net.redstoneore.legacyfactions.Factions;
import net.redstoneore.legacyfactions.Lang;

public class CmdFactionsAutohelp extends MCommand<Factions> {

    public CmdFactionsAutohelp() {
        this.aliases.add("?");
        this.aliases.add("h");
        this.aliases.add("help");

        this.setHelpShort("");

        this.optionalArgs.put("page", "1");
    }

    @Override
    public void perform() {
        if (this.commandChain.size() == 0) {
            return;
        }
        MCommand<?> pcmd = this.commandChain.get(this.commandChain.size() - 1);

        ArrayList<String> lines = new ArrayList<String>();

        lines.addAll(pcmd.helpLong);

        for (MCommand<?> scmd : pcmd.subCommands) {
            if (scmd.visibility == CommandVisibility.VISIBLE || (scmd.visibility == CommandVisibility.SECRET && scmd.validSenderPermissions(sender, false))) {
                lines.add(scmd.getUseageTemplate(this.commandChain, true));
            }
        }

        sendMessage(Factions.get().getTextUtil().getPage(lines, this.argAsInt(0, 1), Lang.COMMAND_AUTOHELP_HELPFOR.toString() + pcmd.aliases.get(0) + "\""));
    }

    @Override
    public String getUsageTranslation() {
        return Lang.COMMAND_HELP_DESCRIPTION.toString();
    }
}
