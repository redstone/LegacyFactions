package com.massivecraft.legacyfactions.cmd;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.bukkit.Bukkit;

import com.massivecraft.legacyfactions.FLocation;
import com.massivecraft.legacyfactions.Permission;
import com.massivecraft.legacyfactions.Role;
import com.massivecraft.legacyfactions.TL;
import com.massivecraft.legacyfactions.entity.Faction;
import com.massivecraft.legacyfactions.event.EventFactionsLandChange;
import com.massivecraft.legacyfactions.event.EventFactionsLandChange.LandChangeCause;

public class CmdAutoClaim extends FCommand {

    public CmdAutoClaim() {
        super();
        this.aliases.add("autoclaim");

        //this.requiredArgs.add("");
        this.optionalArgs.put("faction", "your");

        this.permission = Permission.AUTOCLAIM.node;
        this.disableOnLock = true;

        senderMustBePlayer = true;
        senderMustBeMember = false;
        senderMustBeModerator = false;
        senderMustBeAdmin = false;
    }

    @Override
    public void perform() {
        Faction forFaction = this.argAsFaction(0, myFaction);
        if (forFaction == null || forFaction == fme.getAutoClaimFor()) {
            fme.setAutoClaimFor(null);
            msg(TL.COMMAND_AUTOCLAIM_DISABLED);
            return;
        }

        if (!fme.canClaimForFaction(forFaction)) {
            if (myFaction == forFaction) {
                msg(TL.COMMAND_AUTOCLAIM_REQUIREDRANK, Role.MODERATOR.getTranslation());
            } else {
                msg(TL.COMMAND_AUTOCLAIM_OTHERFACTION, forFaction.describeTo(fme));
            }

            return;
        }

        fme.setAutoClaimFor(forFaction);

        msg(TL.COMMAND_AUTOCLAIM_ENABLED, forFaction.describeTo(fme));
        
        Map<FLocation, Faction> transactions = new HashMap<FLocation, Faction>();

        transactions.put(FLocation.valueOf(me.getLocation()), forFaction);
       
        EventFactionsLandChange event = new EventFactionsLandChange(fme, transactions, LandChangeCause.Claim);
        Bukkit.getServer().getPluginManager().callEvent(event);
        if (event.isCancelled()) return;
        
        for(Entry<FLocation, Faction> claimLocation : event.getTransactions().entrySet()) {
        	if ( ! fme.attemptClaim(claimLocation.getValue(), claimLocation.getKey(), true, event)) {
        		return;
        	}
        }
    }

    @Override
    public TL getUsageTranslation() {
        return TL.COMMAND_AUTOCLAIM_DESCRIPTION;
    }

}