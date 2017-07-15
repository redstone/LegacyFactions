package net.redstoneore.legacyfactions.cmd;

import net.redstoneore.legacyfactions.Factions;
import net.redstoneore.legacyfactions.Lang;
import net.redstoneore.legacyfactions.entity.Conf;

public class CmdFactionsMoney extends FCommand {

	// -------------------------------------------------- //
	// FIELDS
	// -------------------------------------------------- //

	public CmdFactionsMoneyBalance cmdMoneyBalance = new CmdFactionsMoneyBalance();
	public CmdFactionsMoneyDeposit cmdMoneyDeposit = new CmdFactionsMoneyDeposit();
	public CmdFactionsMoneyWithdraw cmdMoneyWithdraw = new CmdFactionsMoneyWithdraw();
	public CmdFactionsMoneyTransferFf cmdMoneyTransferFf = new CmdFactionsMoneyTransferFf();
	public CmdFactionsMoneyTransferFp cmdMoneyTransferFp = new CmdFactionsMoneyTransferFp();
	public CmdFactionsMoneyTransferPf cmdMoneyTransferPf = new CmdFactionsMoneyTransferPf();

	// -------------------------------------------------- //
	// CONSTRUCT
	// -------------------------------------------------- //

	public CmdFactionsMoney() {
		this.aliases.addAll(Conf.cmdAliasesMoney);

		this.isMoneyCommand = true;

		this.senderMustBePlayer = false;
		this.senderMustBeMember = false;
		this.senderMustBeModerator = false;
		this.senderMustBeColeader = false;
		this.senderMustBeAdmin = false;

		this.helpLong.add(Factions.get().getTextUtil().parseTags(Lang.COMMAND_MONEY_LONG.toString()));

		this.addSubCommand(this.cmdMoneyBalance);
		this.addSubCommand(this.cmdMoneyDeposit);
		this.addSubCommand(this.cmdMoneyWithdraw);
		this.addSubCommand(this.cmdMoneyTransferFf);
		this.addSubCommand(this.cmdMoneyTransferFp);
		this.addSubCommand(this.cmdMoneyTransferPf);
	}

	// -------------------------------------------------- //
	// METHODS
	// -------------------------------------------------- //

	@Override
	public void perform() {
		this.commandChain.add(this);
		CmdFactionsAutohelp.get().execute(this.sender, this.args, this.commandChain);
	}

	@Override
	public String getUsageTranslation() {
		return Lang.COMMAND_MONEY_DESCRIPTION.toString();
	}

}
