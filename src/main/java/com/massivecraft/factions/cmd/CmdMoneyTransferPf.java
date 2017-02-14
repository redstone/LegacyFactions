package com.massivecraft.factions.cmd;

import com.massivecraft.factions.EconomyParticipator;
import com.massivecraft.factions.Factions;
import com.massivecraft.factions.Permission;
import com.massivecraft.factions.TL;
import com.massivecraft.factions.entity.Conf;
import com.massivecraft.factions.integration.vault.VaultEngine;

import org.bukkit.ChatColor;


public class CmdMoneyTransferPf extends FCommand {

    public CmdMoneyTransferPf() {
        this.aliases.add("pf");

        this.requiredArgs.add("amount");
        this.requiredArgs.add("player");
        this.requiredArgs.add("faction");

        //this.optionalArgs.put("", "");

        this.permission = Permission.MONEY_P2F.node;

        senderMustBePlayer = false;
        senderMustBeMember = false;
        senderMustBeModerator = false;
        senderMustBeAdmin = false;
    }

    @Override
    public void perform() {
        double amount = this.argAsDouble(0, 0d);
        EconomyParticipator from = this.argAsBestFPlayerMatch(1);
        if (from == null) {
            return;
        }
        EconomyParticipator to = this.argAsFaction(2);
        if (to == null) {
            return;
        }

        boolean success = VaultEngine.transferMoney(fme, from, to, amount);

        if (success && Conf.logMoneyTransactions) {
            Factions.get().log(ChatColor.stripColor(Factions.get().txt.parse(TL.COMMAND_MONEYTRANSFERPF_TRANSFER.toString(), fme.getName(), VaultEngine.moneyString(amount), from.describeTo(null), to.describeTo(null))));
        }
    }

    @Override
    public TL getUsageTranslation() {
        return TL.COMMAND_MONEYTRANSFERPF_DESCRIPTION;
    }
}
