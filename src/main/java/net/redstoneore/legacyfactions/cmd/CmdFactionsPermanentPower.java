package net.redstoneore.legacyfactions.cmd;

import net.redstoneore.legacyfactions.Permission;
import net.redstoneore.legacyfactions.Lang;
import net.redstoneore.legacyfactions.entity.Conf;
import net.redstoneore.legacyfactions.entity.FPlayer;
import net.redstoneore.legacyfactions.entity.Faction;

public class CmdFactionsPermanentPower extends FCommand {
    public CmdFactionsPermanentPower() {
        this.aliases.addAll(Conf.cmdAliasesPermanentPower);

        this.requiredArgs.add("faction");
        this.optionalArgs.put("power", "reset");

        this.permission = Permission.SET_PERMANENTPOWER.node;
        this.disableOnLock = true;

        senderMustBePlayer = false;
        senderMustBeMember = false;
        senderMustBeModerator = false;
        senderMustBeAdmin = false;
    }

    @Override
    public void perform() {
        Faction targetFaction = this.argAsFaction(0);
        if (targetFaction == null) {
            return;
        }

        Integer targetPower = this.argAsInt(1);

        targetFaction.setPermanentPower(targetPower);

        String change = Lang.COMMAND_PERMANENTPOWER_REVOKE.toString();
        if (targetFaction.hasPermanentPower()) {
            change = Lang.COMMAND_PERMANENTPOWER_GRANT.toString();
        }

        // Inform sender
        msg(Lang.COMMAND_PERMANENTPOWER_SUCCESS, change, targetFaction.describeTo(fme));

        // Inform all other players
        for (FPlayer fplayer : targetFaction.getFPlayersWhereOnline(true)) {
            if (fplayer == fme) {
                continue;
            }
            String blame = (fme == null ? Lang.GENERIC_SERVERADMIN.toString() : fme.describeTo(fplayer, true));
            fplayer.msg(Lang.COMMAND_PERMANENTPOWER_FACTION, blame, change);
        }
    }

    @Override
    public String getUsageTranslation() {
        return Lang.COMMAND_PERMANENTPOWER_DESCRIPTION.toString();
    }
}
