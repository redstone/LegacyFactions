package com.massivecraft.legacyfactions.cmd;

import com.massivecraft.legacyfactions.Factions;
import com.massivecraft.legacyfactions.Permission;
import com.massivecraft.legacyfactions.TL;
import com.massivecraft.legacyfactions.entity.FPlayer;
import com.massivecraft.legacyfactions.entity.FPlayerColl;
import com.massivecraft.legacyfactions.entity.Faction;


public class CmdPermanent extends FCommand {

    public CmdPermanent() {
        super();
        this.aliases.add("permanent");

        this.requiredArgs.add("faction tag");
        //this.optionalArgs.put("", "");

        this.permission = Permission.SET_PERMANENT.node;
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
        if (faction.isPermanent()) {
            change = TL.COMMAND_PERMANENT_REVOKE.toString();
            faction.setPermanent(false);
        } else {
            change = TL.COMMAND_PERMANENT_GRANT.toString();
            faction.setPermanent(true);
        }

        Factions.get().log((fme == null ? "A server admin" : fme.getName()) + " " + change + " the faction \"" + faction.getTag() + "\".");

        // Inform all players
        for (FPlayer fplayer : FPlayerColl.getAllOnline()) {
            String blame = (fme == null ? TL.GENERIC_SERVERADMIN.toString() : fme.describeTo(fplayer, true));
            if (fplayer.getFaction() == faction) {
                fplayer.msg(TL.COMMAND_PERMANENT_YOURS, blame, change);
            } else {
                fplayer.msg(TL.COMMAND_PERMANENT_OTHER, blame, change, faction.getTag(fplayer));
            }
        }
    }

    @Override
    public TL getUsageTranslation() {
        return TL.COMMAND_PERMANENT_DESCRIPTION;
    }
}
