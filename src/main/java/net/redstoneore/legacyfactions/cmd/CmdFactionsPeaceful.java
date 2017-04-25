package net.redstoneore.legacyfactions.cmd;

import net.redstoneore.legacyfactions.Permission;
import net.redstoneore.legacyfactions.TL;
import net.redstoneore.legacyfactions.entity.FPlayer;
import net.redstoneore.legacyfactions.entity.FPlayerColl;
import net.redstoneore.legacyfactions.entity.Faction;

public class CmdFactionsPeaceful extends FCommand {

    public CmdFactionsPeaceful() {
        super();
        this.aliases.add("peaceful");

        this.requiredArgs.add("faction tag");
        //this.optionalArgs.put("", "");

        this.permission = Permission.SET_PEACEFUL.node;
        this.disableOnLock = true;

        senderMustBePlayer = false;
        senderMustBeMember = false;
        senderMustBeModerator = false;
        senderMustBeAdmin = false;
    }

    @Override
    public void perform() {
        Faction faction = this.argAsFaction(0);
        if (faction == null) {
            return;
        }

        String change;
        if (faction.isPeaceful()) {
            change = TL.COMMAND_PEACEFUL_REVOKE.toString();
            faction.setPeaceful(false);
        } else {
            change = TL.COMMAND_PEACEFUL_GRANT.toString();
            faction.setPeaceful(true);
        }

        // Inform all players
        for (FPlayer fplayer : FPlayerColl.getAllOnline()) {
            String blame = (fme == null ? TL.GENERIC_SERVERADMIN.toString() : fme.describeTo(fplayer, true));
            if (fplayer.getFaction() == faction) {
                fplayer.msg(TL.COMMAND_PEACEFUL_YOURS, blame, change);
            } else {
                fplayer.msg(TL.COMMAND_PEACEFUL_OTHER, blame, change, faction.getTag(fplayer));
            }
        }

    }

    @Override
    public String getUsageTranslation() {
        return TL.COMMAND_PEACEFUL_DESCRIPTION.toString();
    }

}
