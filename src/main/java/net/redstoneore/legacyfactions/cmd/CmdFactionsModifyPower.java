package net.redstoneore.legacyfactions.cmd;

import net.redstoneore.legacyfactions.Permission;
import net.redstoneore.legacyfactions.Lang;
import net.redstoneore.legacyfactions.entity.Conf;
import net.redstoneore.legacyfactions.entity.FPlayer;

public class CmdFactionsModifyPower extends FCommand {

    public CmdFactionsModifyPower() {
        this.aliases.addAll(Conf.cmdAliasesModifyPower);

        this.requiredArgs.add("name");
        this.requiredArgs.add("power");

        this.permission = Permission.MODIFY_POWER.node; // admin only perm.

        // Let's not require anything and let console modify this as well.
        this.senderMustBeAdmin = false;
        this.senderMustBePlayer = false;
        this.senderMustBeMember = false;
        this.senderMustBeModerator = false;
    }

    @Override
    public void perform() {
        // /f modify <name> #
        FPlayer player = argAsBestFPlayerMatch(0);
        Double number = argAsDouble(1); // returns null if not a Double.

        if (player == null || number == null) {
            sender.sendMessage(getHelpShort());
            return;
        }

        player.alterPower(number);
        int newPower = player.getPowerRounded(); // int so we don't have super long doubles.
        msg(Lang.COMMAND_MODIFYPOWER_ADDED, number, player.getName(), newPower);
    }

    @Override
    public String getUsageTranslation() {
        return Lang.COMMAND_MODIFYPOWER_DESCRIPTION.toString();
    }
}
