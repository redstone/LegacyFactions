package net.redstoneore.legacyfactions.cmd;

import org.bukkit.ChatColor;

import net.redstoneore.legacyfactions.EconomyParticipator;
import net.redstoneore.legacyfactions.Factions;
import net.redstoneore.legacyfactions.Permission;
import net.redstoneore.legacyfactions.Lang;
import net.redstoneore.legacyfactions.entity.Conf;
import net.redstoneore.legacyfactions.integration.vault.VaultEngine;


public class CmdFactionsMoneyTransferFp extends FCommand {

	// -------------------------------------------------- //
	// CONSTRUCT
	// -------------------------------------------------- //

	public CmdFactionsMoneyTransferFp() {
		this.aliases.addAll(Conf.cmdAliasesMoneyTransferFp);

		this.requiredArgs.add("amount");
		this.requiredArgs.add("faction");
		this.requiredArgs.add("player");

		this.permission = Permission.MONEY_F2P.node;

		this.senderMustBePlayer = false;
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
			Factions.get().log(ChatColor.stripColor(Factions.get().getTextUtil().parse(Lang.COMMAND_MONEYTRANSFERFP_TRANSFER.toString(), fme.getName(), VaultEngine.moneyString(amount), from.describeTo(null), to.describeTo(null))));
		}
	}

	@Override
	public String getUsageTranslation() {
		return Lang.COMMAND_MONEYTRANSFERFP_DESCRIPTION.toString();
	}
}
