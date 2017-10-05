package net.redstoneore.legacyfactions.cmd;

import net.redstoneore.legacyfactions.config.CommandAliases;
import net.redstoneore.legacyfactions.lang.Lang;
import net.redstoneore.legacyfactions.util.TextUtil;

public class CmdFactionsMoney extends FCommand {

	// -------------------------------------------------- //
	// INSTANCE
	// -------------------------------------------------- //
	
	private static CmdFactionsMoney instance = new CmdFactionsMoney();
	public static CmdFactionsMoney get() { return instance; }
	
	// -------------------------------------------------- //
	// CONSTRUCT
	// -------------------------------------------------- //

	private CmdFactionsMoney() {
		this.aliases.addAll(CommandAliases.cmdAliasesMoney);

		this.isMoneyCommand = true;

		this.senderMustBePlayer = false;
		this.senderMustBeMember = false;
		this.senderMustBeModerator = false;
		this.senderMustBeColeader = false;
		this.senderMustBeAdmin = false;

		this.helpLong.add(TextUtil.get().parseTags(Lang.COMMAND_MONEY_LONG.toString()));

		this.addSubCommand(CmdFactionsMoneyBalance.get());
		this.addSubCommand(CmdFactionsMoneyDeposit.get());
		this.addSubCommand(CmdFactionsMoneyWithdraw.get());
		this.addSubCommand(CmdFactionsMoneyTransferFf.get());
		this.addSubCommand(CmdFactionsMoneyTransferFp.get());
		this.addSubCommand(CmdFactionsMoneyTransferPf.get());
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
	
	// -------------------------------------------------- //
	// DEPRECATED FIELDS
	// -------------------------------------------------- //

	@Deprecated
	public CmdFactionsMoneyBalance cmdMoneyBalance = CmdFactionsMoneyBalance.get();
	
	@Deprecated
	public CmdFactionsMoneyDeposit cmdMoneyDeposit = CmdFactionsMoneyDeposit.get();
	
	@Deprecated
	public CmdFactionsMoneyWithdraw cmdMoneyWithdraw = CmdFactionsMoneyWithdraw.get();
	
	@Deprecated
	public CmdFactionsMoneyTransferFf cmdMoneyTransferFf = CmdFactionsMoneyTransferFf.get();
	
	@Deprecated
	public CmdFactionsMoneyTransferFp cmdMoneyTransferFp = CmdFactionsMoneyTransferFp.get();
	
	@Deprecated
	public CmdFactionsMoneyTransferPf cmdMoneyTransferPf = CmdFactionsMoneyTransferPf.get();
	
}
