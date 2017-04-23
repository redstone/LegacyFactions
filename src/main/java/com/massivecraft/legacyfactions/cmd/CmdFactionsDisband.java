package com.massivecraft.legacyfactions.cmd;

import org.bukkit.Bukkit;

import com.massivecraft.legacyfactions.*;
import com.massivecraft.legacyfactions.entity.Conf;
import com.massivecraft.legacyfactions.entity.FPlayer;
import com.massivecraft.legacyfactions.entity.FPlayerColl;
import com.massivecraft.legacyfactions.entity.Faction;
import com.massivecraft.legacyfactions.entity.FactionColl;
import com.massivecraft.legacyfactions.event.EventFactionsChange.ChangeReason;
import com.massivecraft.legacyfactions.event.EventFactionsChange;
import com.massivecraft.legacyfactions.event.EventFactionsDisband;
import com.massivecraft.legacyfactions.integration.vault.VaultEngine;
import com.massivecraft.legacyfactions.scoreboards.FTeamWrapper;

public class CmdFactionsDisband extends FCommand {

    public CmdFactionsDisband() {
        super();
        this.aliases.add("disband");

        //this.requiredArgs.add("");
        this.optionalArgs.put("faction tag", "yours");

        this.permission = Permission.DISBAND.node;
        this.disableOnLock = true;

        senderMustBePlayer = false;
        senderMustBeMember = false;
        senderMustBeModerator = false;
        senderMustBeAdmin = false;
    }

    @Override
    public void perform() {
        // The faction, default to your own.. but null if console sender.
        Faction faction = this.argAsFaction(0, fme == null ? null : myFaction);
        if (faction == null) {
            return;
        }

        boolean isMyFaction = fme == null ? false : faction == myFaction;

        if (isMyFaction) {
            if (!assertMinRole(Role.ADMIN)) {
                return;
            }
        } else {
            if (!Permission.DISBAND_ANY.has(sender, true)) {
                return;
            }
        }

        if (!faction.isNormal()) {
            msg(TL.COMMAND_DISBAND_IMMUTABLE.toString());
            return;
        }
        if (faction.isPermanent()) {
            msg(TL.COMMAND_DISBAND_MARKEDPERMANENT.toString());
            return;
        }

        EventFactionsDisband disbandEvent = new EventFactionsDisband(me, faction.getId());
        Bukkit.getServer().getPluginManager().callEvent(disbandEvent);
        if (disbandEvent.isCancelled()) {
            return;
        }

        // Send event for each player in the faction
        for (FPlayer fplayer : faction.getFPlayers()) {
            Bukkit.getServer().getPluginManager().callEvent(new EventFactionsChange(fplayer, faction, FactionColl.getInstance().getWilderness(), false, ChangeReason.DISBAND));
        }

        // Inform all players
        for (FPlayer fplayer : FPlayerColl.getAllOnline()) {
            String who = senderIsConsole ? TL.GENERIC_SERVERADMIN.toString() : fme.describeTo(fplayer);
            if (fplayer.getFaction() == faction) {
                fplayer.msg(TL.COMMAND_DISBAND_BROADCAST_YOURS, who);
            } else {
                fplayer.msg(TL.COMMAND_DISBAND_BROADCAST_NOTYOURS, who, faction.getTag(fplayer));
            }
        }
        if (Conf.logFactionDisband) {
            //TODO: Format this correctly and translate.
            Factions.get().log("The faction " + faction.getTag() + " (" + faction.getId() + ") was disbanded by " + (senderIsConsole ? "console command" : fme.getName()) + ".");
        }

        if (VaultEngine.shouldBeUsed() && !senderIsConsole) {
            //Give all the faction's money to the disbander
            double amount = VaultEngine.getBalance(faction.getAccountId());
            VaultEngine.transferMoney(fme, faction, fme, amount, false);

            if (amount > 0.0) {
                String amountString = VaultEngine.moneyString(amount);
                msg(TL.COMMAND_DISBAND_HOLDINGS, amountString);
                //TODO: Format this correctly and translate
                Factions.get().log(fme.getName() + " has been given bank holdings of " + amountString + " from disbanding " + faction.getTag() + ".");
            }
        }

        FactionColl.getInstance().removeFaction(faction.getId());
        FTeamWrapper.applyUpdates(faction);
    }

    @Override
    public TL getUsageTranslation() {
        return TL.COMMAND_DISBAND_DESCRIPTION;
    }
}
