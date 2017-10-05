package net.redstoneore.legacyfactions.cmd;

import net.redstoneore.legacyfactions.Permission;
import net.redstoneore.legacyfactions.config.CommandAliases;
import net.redstoneore.legacyfactions.entity.Faction;
import net.redstoneore.legacyfactions.integration.vault.VaultEngine;
import net.redstoneore.legacyfactions.lang.Lang;

public class CmdFactionsMoneyBalance extends FCommand {
	
	// -------------------------------------------------- //
	// INSTANCE
	// -------------------------------------------------- //
	
	private static CmdFactionsMoneyBalance instance = new CmdFactionsMoneyBalance();
	public static CmdFactionsMoneyBalance get() { return instance; }
	
	// -------------------------------------------------- //
	// CONSTRUCT
	// -------------------------------------------------- //

	private CmdFactionsMoneyBalance() {
		this.aliases.addAll(CommandAliases.cmdAliasesMoneyBalance);
		
		this.optionalArgs.put("faction", "yours");
		
		this.permission = Permission.MONEY_BALANCE.getNode();
		this.setHelpShort(Lang.COMMAND_MONEYBALANCE_SHORT.toString());

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
		Faction faction = myFaction;
		if (this.argIsSet(0)) {
			faction = this.argAsFaction(0);
		}

		if (faction == null) return;
		
		if (faction != myFaction && !Permission.MONEY_BALANCE_ANY.has(sender, true)) {
			return;
		}

		VaultEngine.getUtils().sendBalanceInfo(fme, faction);
	}

	@Override
	public String getUsageTranslation() {
		return Lang.COMMAND_MONEYBALANCE_DESCRIPTION.toString();
	}

}
