package com.massivecraft.factions.cmd;

import com.massivecraft.factions.EconomyParticipator;
import com.massivecraft.factions.Factions;
import com.massivecraft.factions.Permission;
import com.massivecraft.factions.TL;
import com.massivecraft.factions.entity.Conf;
import com.massivecraft.factions.integration.vault.VaultEngine;

import org.bukkit.ChatColor;


public class CmdMoneyTransferFp extends FCommand {

    public CmdMoneyTransferFp() {
        this.aliases.add("fp");

        this.requiredArgs.add("amount");
        this.requiredArgs.add("faction");
        this.requiredArgs.add("player");

        //this.optionalArgs.put("", "");

        this.permission = Permission.MONEY_F2P.node;

        senderMustBePlayer = false;
        senderMustBeMember = false;
        senderMustBeModerator = false;
        senderMustBeAdmin = false;
    }

    @Override
    public void perform() {
        double amount = this.argAsDouble(0, 0d);
        EconomyParticipator from = this.argAsFaction(1);
        if (from == null) {
            return;
        }
        EconomyParticipator to = this.argAsBestFPlayerMatch(2);
        if (to == null) {
            return;
        }

        boolean success = VaultEngine.transferMoney(fme, from, to, amount);

        if (success && Conf.logMoneyTransactions) {
            Factions.get().log(ChatColor.stripColor(Factions.get().txt.parse(TL.COMMAND_MONEYTRANSFERFP_TRANSFER.toString(), fme.getName(), VaultEngine.moneyString(amount), from.describeTo(null), to.describeTo(null))));
        }
    }

    @Override
    public TL getUsageTranslation() {
        return TL.COMMAND_MONEYTRANSFERFP_DESCRIPTION;
    }
}
