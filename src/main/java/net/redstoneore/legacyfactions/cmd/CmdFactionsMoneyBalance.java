package net.redstoneore.legacyfactions.cmd;

import net.redstoneore.legacyfactions.Permission;
import net.redstoneore.legacyfactions.Lang;
import net.redstoneore.legacyfactions.entity.Conf;
import net.redstoneore.legacyfactions.entity.Faction;
import net.redstoneore.legacyfactions.integration.vault.VaultEngine;

public class CmdFactionsMoneyBalance extends FCommand {

    public CmdFactionsMoneyBalance() {
        this.aliases.addAll(Conf.cmdAliasesMoneyBalance);

        this.optionalArgs.put("faction", "yours");

        this.permission = Permission.MONEY_BALANCE.node;
        this.setHelpShort(Lang.COMMAND_MONEYBALANCE_SHORT.toString());

        senderMustBePlayer = false;
        senderMustBeMember = false;
        senderMustBeModerator = false;
        senderMustBeAdmin = false;
    }

    @Override
    public void perform() {
        Faction faction = myFaction;
        if (this.argIsSet(0)) {
            faction = this.argAsFaction(0);
        }

        if (faction == null) {
            return;
        }
        if (faction != myFaction && !Permission.MONEY_BALANCE_ANY.has(sender, true)) {
            return;
        }

        VaultEngine.sendBalanceInfo(fme, faction);
    }

    @Override
    public String getUsageTranslation() {
        return Lang.COMMAND_MONEYBALANCE_DESCRIPTION.toString();
    }

}
