package net.redstoneore.legacyfactions.cmd;

import org.bukkit.ChatColor;

import net.redstoneore.legacyfactions.EconomyParticipator;
import net.redstoneore.legacyfactions.Factions;
import net.redstoneore.legacyfactions.Permission;
import net.redstoneore.legacyfactions.Lang;
import net.redstoneore.legacyfactions.entity.Conf;
import net.redstoneore.legacyfactions.integration.vault.VaultEngine;


public class CmdFactionsMoneyWithdraw extends FCommand {

    public CmdFactionsMoneyWithdraw() {
        this.aliases.addAll(Conf.cmdAliasesMoneyWithdraw);

        this.requiredArgs.add("amount");
        this.optionalArgs.put("faction", "yours");

        this.permission = Permission.MONEY_WITHDRAW.node;

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
        boolean success = VaultEngine.transferMoney(fme, faction, fme, amount);

        if (success && Conf.logMoneyTransactions) {
            Factions.get().log(ChatColor.stripColor(Factions.get().getTextUtil().parse(Lang.COMMAND_MONEYWITHDRAW_WITHDRAW.toString(), fme.getName(), VaultEngine.moneyString(amount), faction.describeTo(null))));
        }
    }

    @Override
    public String getUsageTranslation() {
        return Lang.COMMAND_MONEYWITHDRAW_DESCRIPTION.toString();
    }
}
