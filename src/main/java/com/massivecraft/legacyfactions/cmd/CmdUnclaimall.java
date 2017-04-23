package com.massivecraft.legacyfactions.cmd;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.Bukkit;

import com.massivecraft.legacyfactions.FLocation;
import com.massivecraft.legacyfactions.Factions;
import com.massivecraft.legacyfactions.Permission;
import com.massivecraft.legacyfactions.TL;
import com.massivecraft.legacyfactions.entity.Conf;
import com.massivecraft.legacyfactions.entity.Faction;
import com.massivecraft.legacyfactions.entity.FactionColl;
import com.massivecraft.legacyfactions.event.EventFactionsLandChange;
import com.massivecraft.legacyfactions.event.EventFactionsLandChange.LandChangeCause;
import com.massivecraft.legacyfactions.integration.vault.VaultEngine;

public class CmdUnclaimall extends FCommand {

    public CmdUnclaimall() {
        this.aliases.add("unclaimall");
        this.aliases.add("declaimall");

        //this.requiredArgs.add("");
        //this.optionalArgs.put("", "");

        this.permission = Permission.UNCLAIM_ALL.node;
        this.disableOnLock = true;

        senderMustBePlayer = true;
        senderMustBeMember = false;
        senderMustBeModerator = true;
        senderMustBeAdmin = false;
    }

    @Override
    public void perform() {
        if (VaultEngine.shouldBeUsed()) {
            double refund = VaultEngine.calculateTotalLandRefund(myFaction.getLandRounded());
            if (Conf.bankEnabled && Conf.bankFactionPaysLandCosts) {
                if (!VaultEngine.modifyMoney(myFaction, refund, TL.COMMAND_UNCLAIMALL_TOUNCLAIM.toString(), TL.COMMAND_UNCLAIMALL_FORUNCLAIM.toString())) {
                    return;
                }
            } else {
                if (!VaultEngine.modifyMoney(fme, refund, TL.COMMAND_UNCLAIMALL_TOUNCLAIM.toString(), TL.COMMAND_UNCLAIMALL_FORUNCLAIM.toString())) {
                    return;
                }
            }
        }

        Map<FLocation, Faction> transactions = new HashMap<FLocation, Faction>();
        
        for (FLocation location : myFaction.getAllClaims()) {
        	transactions.put(location, FactionColl.getInstance().getWilderness());
        }
        
        EventFactionsLandChange event = new EventFactionsLandChange(fme, transactions, LandChangeCause.Unclaim);
        Bukkit.getServer().getPluginManager().callEvent(event);
        if (event.isCancelled()) return;
        
        for (FLocation location : event.getTransactions().keySet()) {
        	myFaction.clearClaimOwnership(location);
        }

        myFaction.msg(TL.COMMAND_UNCLAIMALL_UNCLAIMED, fme.describeTo(myFaction, true));

        if (Conf.logLandUnclaims) {
            Factions.get().log(TL.COMMAND_UNCLAIMALL_LOG.format(fme.getName(), myFaction.getTag()));
        }
    }

    @Override
    public TL getUsageTranslation() {
        return TL.COMMAND_UNCLAIMALL_DESCRIPTION;
    }

}
