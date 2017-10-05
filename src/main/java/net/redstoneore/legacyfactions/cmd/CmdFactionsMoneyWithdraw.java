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


public class CmdFactionsMoneyWithdraw extends FCommand {
	
	// -------------------------------------------------- //
	// INSTANCE
	// -------------------------------------------------- //
	
	private static CmdFactionsMoneyWithdraw instance = new CmdFactionsMoneyWithdraw();
	public static CmdFactionsMoneyWithdraw get() { return instance; }
	
	// -------------------------------------------------- //
	// CONSTRUCT
	// -------------------------------------------------- //

	private CmdFactionsMoneyWithdraw() {
		this.aliases.addAll(CommandAliases.cmdAliasesMoneyWithdraw);

		this.requiredArgs.add("amount");
		this.optionalArgs.put("faction", "yours");

		this.permission = Permission.MONEY_WITHDRAW.getNode();

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
		EconomyParticipator from = this.argAsFaction(1, this.myFaction);
		if (from == null) return;
		
		boolean success = VaultAccount.get(from).transfer(VaultAccount.get(this.fme), amount, VaultAccount.get(this.fme));

		if (success && Config.logMoneyTransactions) {
			Factions.get().log(ChatColor.stripColor(TextUtil.get().parse(Lang.COMMAND_MONEYWITHDRAW_WITHDRAW.toString(), this.fme.getName(), VaultEngine.getUtils().moneyString(amount), from.describe())));
		}
	}

	@Override
	public String getUsageTranslation() {
		return Lang.COMMAND_MONEYWITHDRAW_DESCRIPTION.toString();
	}
	
}
