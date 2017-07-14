package net.redstoneore.legacyfactions.cmd;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.Bukkit;

import net.redstoneore.legacyfactions.FLocation;
import net.redstoneore.legacyfactions.Factions;
import net.redstoneore.legacyfactions.Permission;
import net.redstoneore.legacyfactions.Lang;
import net.redstoneore.legacyfactions.entity.Conf;
import net.redstoneore.legacyfactions.entity.Faction;
import net.redstoneore.legacyfactions.entity.FactionColl;
import net.redstoneore.legacyfactions.event.EventFactionsLandChange;
import net.redstoneore.legacyfactions.event.EventFactionsLandChange.LandChangeCause;
import net.redstoneore.legacyfactions.integration.vault.VaultEngine;

public class CmdFactionsUnclaimall extends FCommand {

    // -------------------------------------------------- //
    // CONSTRUCT
    // -------------------------------------------------- //

    public CmdFactionsUnclaimall() {
        this.aliases.addAll(Conf.cmdAliasesUnclaimAll);
        
        this.permission = Permission.UNCLAIM_ALL.node;
        this.disableOnLock = true;

		this.senderMustBePlayer = true;
		this.senderMustBeMember = false;
		this.senderMustBeModerator = true;
		this.senderMustBeColeader = false;
		this.senderMustBeAdmin = false;
    }

	// -------------------------------------------------- //
	// METHODS
	// -------------------------------------------------- //

    @Override
    public void perform() {
        if (VaultEngine.shouldBeUsed()) {
            double refund = VaultEngine.calculateTotalLandRefund(myFaction.getLandRounded());
            if (Conf.bankEnabled && Conf.bankFactionPaysLandCosts) {
                if (!VaultEngine.modifyMoney(myFaction, refund, Lang.COMMAND_UNCLAIMALL_TOUNCLAIM.toString(), Lang.COMMAND_UNCLAIMALL_FORUNCLAIM.toString())) {
                    return;
                }
            } else {
                if (!VaultEngine.modifyMoney(fme, refund, Lang.COMMAND_UNCLAIMALL_TOUNCLAIM.toString(), Lang.COMMAND_UNCLAIMALL_FORUNCLAIM.toString())) {
                    return;
                }
            }
        }

        Map<FLocation, Faction> transactions = new HashMap<FLocation, Faction>();
        
        for (FLocation location : myFaction.getAllClaims()) {
        	transactions.put(location, FactionColl.get().getWilderness());
        }
        
        EventFactionsLandChange event = new EventFactionsLandChange(fme, transactions, LandChangeCause.Unclaim);
        Bukkit.getServer().getPluginManager().callEvent(event);
        if (event.isCancelled()) return;
        
        for (FLocation location : event.getTransactions().keySet()) {
        	myFaction.clearClaimOwnership(location);
        }

        myFaction.msg(Lang.COMMAND_UNCLAIMALL_UNCLAIMED, fme.describeTo(myFaction, true));

        if (Conf.logLandUnclaims) {
            Factions.get().log(Lang.COMMAND_UNCLAIMALL_LOG.format(fme.getName(), myFaction.getTag()));
        }
    }

    @Override
    public String getUsageTranslation() {
        return Lang.COMMAND_UNCLAIMALL_DESCRIPTION.toString();
    }

}
