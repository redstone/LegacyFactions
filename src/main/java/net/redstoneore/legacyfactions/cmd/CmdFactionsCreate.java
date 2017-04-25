package net.redstoneore.legacyfactions.cmd;

import org.bukkit.Bukkit;

import net.redstoneore.legacyfactions.*;
import net.redstoneore.legacyfactions.entity.Conf;
import net.redstoneore.legacyfactions.entity.FPlayer;
import net.redstoneore.legacyfactions.entity.FPlayerColl;
import net.redstoneore.legacyfactions.entity.Faction;
import net.redstoneore.legacyfactions.entity.FactionColl;
import net.redstoneore.legacyfactions.event.EventFactionsChange;
import net.redstoneore.legacyfactions.event.EventFactionsCreate;
import net.redstoneore.legacyfactions.event.EventFactionsChange.ChangeReason;
import net.redstoneore.legacyfactions.util.MiscUtil;

import java.util.ArrayList;


public class CmdFactionsCreate extends FCommand {

    public CmdFactionsCreate() {
        super();
        this.aliases.add("create");

        this.requiredArgs.add("faction tag");
        //this.optionalArgs.put("", "");

        this.permission = Permission.CREATE.node;
        this.disableOnLock = true;

        senderMustBePlayer = true;
        senderMustBeMember = false;
        senderMustBeModerator = false;
        senderMustBeAdmin = false;
    }

    @Override
    public void perform() {
        String tag = this.argAsString(0);

        if (fme.hasFaction()) {
            msg(Lang.COMMAND_CREATE_MUSTLEAVE);
            return;
        }

        if (FactionColl.get().isTagTaken(tag)) {
            msg(Lang.COMMAND_CREATE_INUSE);
            return;
        }

        ArrayList<String> tagValidationErrors = MiscUtil.validateTag(tag);
        if (tagValidationErrors.size() > 0) {
            sendMessage(tagValidationErrors);
            return;
        }

        // if economy is enabled, they're not on the bypass list, and this command has a cost set, make sure they can pay
        if (!canAffordCommand(Conf.econCostCreate, Lang.COMMAND_CREATE_TOCREATE.toString())) {
            return;
        }

        // trigger the faction creation event (cancellable)
        EventFactionsCreate createEvent = new EventFactionsCreate(me, tag);
        Bukkit.getServer().getPluginManager().callEvent(createEvent);
        if (createEvent.isCancelled()) {
            return;
        }
        // update here incase it was changed
        tag = createEvent.getFactionTag();

        // then make 'em pay (if applicable)
        if (!payForCommand(Conf.econCostCreate, Lang.COMMAND_CREATE_TOCREATE, Lang.COMMAND_CREATE_FORCREATE)) {
            return;
        }

        Faction faction = FactionColl.get().createFaction();

        if (faction == null) {
            msg(Lang.COMMAND_CREATE_ERROR);
            return;
        }

        // finish setting up the Faction
        faction.setTag(tag);

        // trigger the faction join event for the creator
        EventFactionsChange event = new EventFactionsChange(fme, fme.getFaction(), faction, false, ChangeReason.CREATE);
        
        Bukkit.getServer().getPluginManager().callEvent(event);
        // join event cannot be cancelled or you'll have an empty faction

        // finish setting up the FPlayer
        fme.setRole(Role.ADMIN);
        fme.setFaction(faction);

        for (FPlayer follower : FPlayerColl.getAllOnline()) {
            follower.msg(Lang.COMMAND_CREATE_CREATED, fme.describeTo(follower, true), faction.getTag(follower));
        }

        msg(Lang.COMMAND_CREATE_YOUSHOULD, Factions.get().cmdBase.cmdDescription.getUseageTemplate());

        if (Conf.logFactionCreate) {
            Factions.get().log(fme.getName() + Lang.COMMAND_CREATE_CREATEDLOG.toString() + tag);
        }
    }

    @Override
    public String getUsageTranslation() {
        return Lang.COMMAND_CREATE_DESCRIPTION.toString();
    }

}
