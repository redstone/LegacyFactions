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


public class CmdFactionsMoneyTransferPf extends FCommand {
	
	// -------------------------------------------------- //
	// INSTANCE
	// -------------------------------------------------- //
	
	private static CmdFactionsMoneyTransferPf instance = new CmdFactionsMoneyTransferPf();
	public static CmdFactionsMoneyTransferPf get() { return instance; }
	
	// -------------------------------------------------- //
	// CONSTRUCT
	// -------------------------------------------------- //

	private CmdFactionsMoneyTransferPf() {
		this.aliases.addAll(CommandAliases.cmdAliasesMoneyTransferPf);

		this.requiredArgs.add("amount");
		this.requiredArgs.add("player");
		this.requiredArgs.add("faction");

		this.permission = Permission.MONEY_P2F.getNode();

		this.senderMustBePlayer = false;
		this.senderMustBeMember = false;
		this.senderMustBeModerator = false;
		this.senderMustBeAdmin = false;
	}

	// -------------------------------------------------- //
	// METHODS
	// -------------------------------------------------- //

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

		boolean success = VaultAccount.get(from).transfer(VaultAccount.get(fme), amount, VaultAccount.get(to));

		if (success && Config.logMoneyTransactions) {
			Factions.get().log(ChatColor.stripColor(TextUtil.get().parse(Lang.COMMAND_MONEYTRANSFERPF_TRANSFER.toString(), fme.getName(), VaultEngine.getUtils().moneyString(amount), from.describeTo(null), to.describeTo(null))));
		}
	}

	@Override
	public String getUsageTranslation() {
		return Lang.COMMAND_MONEYTRANSFERPF_DESCRIPTION.toString();
	}
}
