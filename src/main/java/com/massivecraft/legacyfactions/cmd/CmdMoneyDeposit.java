package com.massivecraft.legacyfactions.cmd;

import org.bukkit.ChatColor;

import com.massivecraft.legacyfactions.EconomyParticipator;
import com.massivecraft.legacyfactions.Factions;
import com.massivecraft.legacyfactions.Permission;
import com.massivecraft.legacyfactions.TL;
import com.massivecraft.legacyfactions.entity.Conf;
import com.massivecraft.legacyfactions.integration.vault.VaultEngine;


public class CmdMoneyDeposit extends FCommand {

    public CmdMoneyDeposit() {
        super();
        this.aliases.add("d");
        this.aliases.add("deposit");

        this.requiredArgs.add("amount");
        this.optionalArgs.put("faction", "yours");

        this.permission = Permission.MONEY_DEPOSIT.node;

        senderMustBePlayer = true;
        senderMustBeMember = false;
        senderMustBeModerator = false;
        senderMustBeAdmin = false;
    }

    @Override
    public void perform() {
        double amount = this.argAsDouble(0, 0d);
        EconomyParticipator faction = this.argAsFaction(1, myFaction);
        if (faction == null) {
            return;
        }
        boolean success = VaultEngine.transferMoney(fme, fme, faction, amount);

        if (success && Conf.logMoneyTransactions) {
            Factions.get().log(ChatColor.stripColor(Factions.get().txt.parse(TL.COMMAND_MONEYDEPOSIT_DEPOSITED.toString(), fme.getName(), VaultEngine.moneyString(amount), faction.describeTo(null))));
        }
    }

    @Override
    public TL getUsageTranslation() {
        return TL.COMMAND_MONEYDEPOSIT_DESCRIPTION;
    }

}
