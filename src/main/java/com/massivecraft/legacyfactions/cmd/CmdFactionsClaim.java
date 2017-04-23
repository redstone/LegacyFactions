package com.massivecraft.legacyfactions.cmd;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.bukkit.Bukkit;

import com.massivecraft.legacyfactions.FLocation;
import com.massivecraft.legacyfactions.Permission;
import com.massivecraft.legacyfactions.TL;
import com.massivecraft.legacyfactions.entity.Conf;
import com.massivecraft.legacyfactions.entity.Faction;
import com.massivecraft.legacyfactions.event.EventFactionsLandChange;
import com.massivecraft.legacyfactions.event.EventFactionsLandChange.LandChangeCause;
import com.massivecraft.legacyfactions.task.SpiralTask;


public class CmdFactionsClaim extends FCommand {

    public CmdFactionsClaim() {
        super();
        this.aliases.add("claim");

        //this.requiredArgs.add("");
        this.optionalArgs.put("radius", "1");
        this.optionalArgs.put("faction", "your");

        this.permission = Permission.CLAIM.node;
        this.disableOnLock = true;

        senderMustBePlayer = true;
        senderMustBeMember = false;
        senderMustBeModerator = false;
        senderMustBeAdmin = false;
    }

    @Override
    public void perform() {
        // Read and validate input
        int radius = this.argAsInt(0, 1); // Default to 1
        final Faction forFaction = this.argAsFaction(1, myFaction); // Default to own

        if (radius < 1) {
            msg(TL.COMMAND_CLAIM_INVALIDRADIUS);
            return;
        }

        if (radius < 2) {
            // single chunk
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
        } else {
            // radius claim
            if (!Permission.CLAIM_RADIUS.has(sender, false)) {
                msg(TL.COMMAND_CLAIM_DENIED);
                return;
            }

            new SpiralTask(new FLocation(me), radius) {
                private int failCount = 0;
                private final int limit = Conf.radiusClaimFailureLimit - 1;

                @Override
                public boolean work() {
                    Map<FLocation, Faction> transactions = new HashMap<FLocation, Faction>();

                    transactions.put(FLocation.valueOf(this.currentLocation()), forFaction);
                   
                    EventFactionsLandChange event = new EventFactionsLandChange(fme, transactions, LandChangeCause.Claim);
                    Bukkit.getServer().getPluginManager().callEvent(event);
                    if (event.isCancelled()) return false;
                    boolean success = false;
                    for(Entry<FLocation, Faction> claimLocation : event.getTransactions().entrySet()) {
                    	if ( ! fme.attemptClaim(claimLocation.getValue(), claimLocation.getKey(), true, event)) {
                    		success = false;
                    	} else {
                    		success = true;
                    	}
                    }
                    
                    if (success) {
                        failCount = 0;
                    } else if (failCount++ >= limit) {
                        this.stop();
                        return false;
                    }

                    return true;
                }
            };
        }
    }

    @Override
    public TL getUsageTranslation() {
        return TL.COMMAND_CLAIM_DESCRIPTION;
    }

}
