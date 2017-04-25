package net.redstoneore.legacyfactions.cmd;

import net.redstoneore.legacyfactions.Permission;
import net.redstoneore.legacyfactions.Lang;
import net.redstoneore.legacyfactions.entity.Conf;
import net.redstoneore.legacyfactions.entity.FPlayer;

public class CmdFactionsPower extends FCommand {

    public CmdFactionsPower() {
        super();
        this.aliases.add("power");
        this.aliases.add("pow");

        //this.requiredArgs.add("faction tag");
        this.optionalArgs.put("player name", "you");

        this.permission = Permission.POWER.node;
        this.disableOnLock = false;

        senderMustBePlayer = false;
        senderMustBeMember = false;
        senderMustBeModerator = false;
        senderMustBeAdmin = false;
    }

    @Override
    public void perform() {
        FPlayer target = this.argAsBestFPlayerMatch(0, fme);
        if (target == null) {
            return;
        }

        if (target != fme && !Permission.POWER_ANY.has(sender, true)) {
            return;
        }

        // if economy is enabled, they're not on the bypass list, and this command has a cost set, make 'em pay
        if (!payForCommand(Conf.econCostPower, Lang.COMMAND_POWER_TOSHOW, Lang.COMMAND_POWER_FORSHOW)) {
            return;
        }

        double powerBoost = target.getPowerBoost();
        String boost = (powerBoost == 0.0) ? "" : (powerBoost > 0.0 ? Lang.COMMAND_POWER_BONUS.toString() : Lang.COMMAND_POWER_PENALTY.toString()) + powerBoost + ")";
        msg(Lang.COMMAND_POWER_POWER, target.describeTo(fme, true), target.getPowerRounded(), target.getPowerMaxRounded(), boost);
    }

    @Override
    public String getUsageTranslation() {
        return Lang.COMMAND_POWER_DESCRIPTION.toString();
    }

}
