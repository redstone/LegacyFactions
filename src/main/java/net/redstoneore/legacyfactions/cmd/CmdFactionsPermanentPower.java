package net.redstoneore.legacyfactions.cmd;

import net.redstoneore.legacyfactions.Permission;
import net.redstoneore.legacyfactions.TL;
import net.redstoneore.legacyfactions.entity.FPlayer;
import net.redstoneore.legacyfactions.entity.Faction;

public class CmdFactionsPermanentPower extends FCommand {
    public CmdFactionsPermanentPower() {
        super();
        this.aliases.add("permanentpower");

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

        String change = TL.COMMAND_PERMANENTPOWER_REVOKE.toString();
        if (targetFaction.hasPermanentPower()) {
            change = TL.COMMAND_PERMANENTPOWER_GRANT.toString();
        }

        // Inform sender
        msg(TL.COMMAND_PERMANENTPOWER_SUCCESS, change, targetFaction.describeTo(fme));

        // Inform all other players
        for (FPlayer fplayer : targetFaction.getFPlayersWhereOnline(true)) {
            if (fplayer == fme) {
                continue;
            }
            String blame = (fme == null ? TL.GENERIC_SERVERADMIN.toString() : fme.describeTo(fplayer, true));
            fplayer.msg(TL.COMMAND_PERMANENTPOWER_FACTION, blame, change);
        }
    }

    @Override
    public TL getUsageTranslation() {
        return TL.COMMAND_PERMANENTPOWER_DESCRIPTION;
    }
}
