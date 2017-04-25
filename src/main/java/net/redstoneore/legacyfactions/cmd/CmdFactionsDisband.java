package net.redstoneore.legacyfactions.cmd;

import org.bukkit.Bukkit;

import net.redstoneore.legacyfactions.*;
import net.redstoneore.legacyfactions.entity.Conf;
import net.redstoneore.legacyfactions.entity.FPlayer;
import net.redstoneore.legacyfactions.entity.FPlayerColl;
import net.redstoneore.legacyfactions.entity.Faction;
import net.redstoneore.legacyfactions.entity.FactionColl;
import net.redstoneore.legacyfactions.event.EventFactionsChange;
import net.redstoneore.legacyfactions.event.EventFactionsDisband;
import net.redstoneore.legacyfactions.event.EventFactionsChange.ChangeReason;
import net.redstoneore.legacyfactions.integration.vault.VaultEngine;
import net.redstoneore.legacyfactions.scoreboards.FTeamWrapper;

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
            msg(Lang.COMMAND_DISBAND_IMMUTABLE.toString());
            return;
        }
        if (faction.isPermanent()) {
            msg(Lang.COMMAND_DISBAND_MARKEDPERMANENT.toString());
            return;
        }

        EventFactionsDisband disbandEvent = new EventFactionsDisband(me, faction.getId());
        Bukkit.getServer().getPluginManager().callEvent(disbandEvent);
        if (disbandEvent.isCancelled()) {
            return;
        }

        // Send event for each player in the faction
        for (FPlayer fplayer : faction.getFPlayers()) {
            Bukkit.getServer().getPluginManager().callEvent(new EventFactionsChange(fplayer, faction, FactionColl.get().getWilderness(), false, ChangeReason.DISBAND));
        }

        // Inform all players
        for (FPlayer fplayer : FPlayerColl.getAllOnline()) {
            String who = senderIsConsole ? Lang.GENERIC_SERVERADMIN.toString() : fme.describeTo(fplayer);
            if (fplayer.getFaction() == faction) {
                fplayer.msg(Lang.COMMAND_DISBAND_BROADCAST_YOURS, who);
            } else {
                fplayer.msg(Lang.COMMAND_DISBAND_BROADCAST_NOTYOURS, who, faction.getTag(fplayer));
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
                msg(Lang.COMMAND_DISBAND_HOLDINGS, amountString);
                //TODO: Format this correctly and translate
                Factions.get().log(fme.getName() + " has been given bank holdings of " + amountString + " from disbanding " + faction.getTag() + ".");
            }
        }

        FactionColl.get().removeFaction(faction.getId());
        FTeamWrapper.applyUpdates(faction);
    }

    @Override
    public String getUsageTranslation() {
        return Lang.COMMAND_DISBAND_DESCRIPTION.toString();
    }
}
