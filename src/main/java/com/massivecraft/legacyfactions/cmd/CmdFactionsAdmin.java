package com.massivecraft.legacyfactions.cmd;

import org.bukkit.Bukkit;

import com.massivecraft.legacyfactions.Permission;
import com.massivecraft.legacyfactions.Role;
import com.massivecraft.legacyfactions.TL;
import com.massivecraft.legacyfactions.entity.FPlayer;
import com.massivecraft.legacyfactions.entity.FPlayerColl;
import com.massivecraft.legacyfactions.entity.Faction;
import com.massivecraft.legacyfactions.event.EventFactionsChange;
import com.massivecraft.legacyfactions.event.EventFactionsChange.ChangeReason;

public class CmdFactionsAdmin extends FCommand {

    public CmdFactionsAdmin() {
        super();
        this.aliases.add("admin");
        this.aliases.add("setadmin");
        this.aliases.add("leader");
        this.aliases.add("setleader");

        this.requiredArgs.add("player name");
        //this.optionalArgs.put("", "");

        this.permission = Permission.ADMIN.node;
        this.disableOnLock = true;

        senderMustBePlayer = false;
        senderMustBeMember = false;
        senderMustBeModerator = false;
        senderMustBeAdmin = false;
    }

    @Override
    public void perform() {
        FPlayer fyou = this.argAsBestFPlayerMatch(0);
        if (fyou == null) {
            return;
        }

        boolean permAny = Permission.ADMIN_ANY.has(sender, false);
        Faction targetFaction = fyou.getFaction();

        if (targetFaction != myFaction && !permAny) {
            msg(TL.COMMAND_ADMIN_NOTMEMBER, fyou.describeTo(fme, true));
            return;
        }

        if (fme != null && fme.getRole() != Role.ADMIN && !permAny) {
            msg(TL.COMMAND_ADMIN_NOTADMIN);
            return;
        }

        if (fyou == fme && !permAny) {
            msg(TL.COMMAND_ADMIN_TARGETSELF);
            return;
        }

        // only perform a FPlayerJoinEvent when newLeader isn't actually in the faction
        if (fyou.getFaction() != targetFaction) {
        	EventFactionsChange event = new EventFactionsChange(fme, fme.getFaction(), targetFaction, true, ChangeReason.LEADER);
            Bukkit.getServer().getPluginManager().callEvent(event);
            if (event.isCancelled()) {
                return;
            }
        }

        FPlayer admin = targetFaction.getFPlayerAdmin();

        // if target player is currently admin, demote and replace him
        if (fyou == admin) {
            targetFaction.promoteNewLeader();
            msg(TL.COMMAND_ADMIN_DEMOTES, fyou.describeTo(fme, true));
            fyou.msg(TL.COMMAND_ADMIN_DEMOTED, senderIsConsole ? TL.GENERIC_SERVERADMIN.toString() : fme.describeTo(fyou, true));
            return;
        }

        // promote target player, and demote existing admin if one exists
        if (admin != null) {
            admin.setRole(Role.MODERATOR);
        }
        fyou.setRole(Role.ADMIN);
        msg(TL.COMMAND_ADMIN_PROMOTES, fyou.describeTo(fme, true));

        // Inform all players
        for (FPlayer fplayer : FPlayerColl.getAllOnline()) {
            fplayer.msg(TL.COMMAND_ADMIN_PROMOTED, senderIsConsole ? TL.GENERIC_SERVERADMIN.toString() : fme.describeTo(fplayer, true), fyou.describeTo(fplayer), targetFaction.describeTo(fplayer));
        }
    }

    public TL getUsageTranslation() {
        return TL.COMMAND_ADMIN_DESCRIPTION;
    }

}
