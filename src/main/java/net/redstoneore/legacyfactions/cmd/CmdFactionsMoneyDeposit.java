package net.redstoneore.legacyfactions.cmd;

import org.bukkit.ChatColor;

import net.redstoneore.legacyfactions.EconomyParticipator;
import net.redstoneore.legacyfactions.Factions;
import net.redstoneore.legacyfactions.Permission;
import net.redstoneore.legacyfactions.Lang;
import net.redstoneore.legacyfactions.entity.Conf;
import net.redstoneore.legacyfactions.integration.vault.VaultEngine;


public class CmdFactionsMoneyDeposit extends FCommand {

    // -------------------------------------------------- //
    // CONSTRUCT
    // -------------------------------------------------- //

    public CmdFactionsMoneyDeposit() {
        this.aliases.addAll(Conf.cmdAliasesMoneyDeposit);

        this.requiredArgs.add("amount");
        this.optionalArgs.put("faction", "yours");

        this.permission = Permission.MONEY_DEPOSIT.node;

		this.senderMustBePlayer = true;
		this.senderMustBeMember = false;
		this.senderMustBeModerator = false;
		this.senderMustBeColeader = false;
		this.senderMustBeAdmin = false;
    }

    // -------------------------------------------------- //
	// METHODS
	// -------------------------------------------------- //

    @Override
    public void perform() {
        double amount = this.argAsDouble(0, 0d);
        EconomyParticipator faction = this.argAsFaction(1, myFaction);
        if (faction == null) {
            return;
        }
        boolean success = VaultEngine.transferMoney(fme, fme, faction, amount);

        if (success && Conf.logMoneyTransactions) {
            Factions.get().log(ChatColor.stripColor(Factions.get().getTextUtil().parse(Lang.COMMAND_MONEYDEPOSIT_DEPOSITED.toString(), fme.getName(), VaultEngine.moneyString(amount), faction.describeTo(null))));
        }
    }

    @Override
    public String getUsageTranslation() {
        return Lang.COMMAND_MONEYDEPOSIT_DESCRIPTION.toString();
    }

}
