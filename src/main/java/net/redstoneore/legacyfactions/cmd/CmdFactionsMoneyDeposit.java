package net.redstoneore.legacyfactions.cmd;

import org.bukkit.ChatColor;

import net.redstoneore.legacyfactions.EconomyParticipator;
import net.redstoneore.legacyfactions.Factions;
import net.redstoneore.legacyfactions.Permission;
import net.redstoneore.legacyfactions.config.CommandAliases;
import net.redstoneore.legacyfactions.config.Config;
import net.redstoneore.legacyfactions.entity.VaultAccount;
import net.redstoneore.legacyfactions.integration.vault.VaultEngine;
import net.redstoneore.legacyfactions.lang.Lang;
import net.redstoneore.legacyfactions.util.TextUtil;


public class CmdFactionsMoneyDeposit extends FCommand {

	// -------------------------------------------------- //
	// INSTANCE
	// -------------------------------------------------- //
	
	private static CmdFactionsMoneyDeposit instance = new CmdFactionsMoneyDeposit();
	public static CmdFactionsMoneyDeposit get() { return instance; }
	
    // -------------------------------------------------- //
    // CONSTRUCT
    // -------------------------------------------------- //

    private CmdFactionsMoneyDeposit() {
        this.aliases.addAll(CommandAliases.cmdAliasesMoneyDeposit);

        this.requiredArgs.add("amount");
        this.optionalArgs.put("faction", "yours");

        this.permission = Permission.MONEY_DEPOSIT.getNode();

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
        
        EconomyParticipator to = this.argAsFaction(1, myFaction);
        if (to == null) return;
        
        boolean success = VaultAccount.get(this.fme).transfer(VaultAccount.get(fme), amount, VaultAccount.get(to));
        

        if (success && Config.logMoneyTransactions) {
            Factions.get().log(ChatColor.stripColor(TextUtil.get().parse(Lang.COMMAND_MONEYDEPOSIT_DEPOSITED.toString(), fme.getName(), VaultEngine.getUtils().moneyString(amount), to.describeTo(null))));
        }
    }

    @Override
    public String getUsageTranslation() {
        return Lang.COMMAND_MONEYDEPOSIT_DESCRIPTION.toString();
    }

}
