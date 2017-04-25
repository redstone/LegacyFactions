package net.redstoneore.legacyfactions.cmd;

import net.redstoneore.legacyfactions.Factions;
import net.redstoneore.legacyfactions.TL;

public class CmdFactionsMoney extends FCommand {

    public CmdFactionsMoneyBalance cmdMoneyBalance = new CmdFactionsMoneyBalance();
    public CmdFactionsMoneyDeposit cmdMoneyDeposit = new CmdFactionsMoneyDeposit();
    public CmdFactionsMoneyWithdraw cmdMoneyWithdraw = new CmdFactionsMoneyWithdraw();
    public CmdFactionsMoneyTransferFf cmdMoneyTransferFf = new CmdFactionsMoneyTransferFf();
    public CmdFactionsMoneyTransferFp cmdMoneyTransferFp = new CmdFactionsMoneyTransferFp();
    public CmdFactionsMoneyTransferPf cmdMoneyTransferPf = new CmdFactionsMoneyTransferPf();

    public CmdFactionsMoney() {
        super();
        this.aliases.add("money");

        //this.requiredArgs.add("");
        //this.optionalArgs.put("","")

        this.isMoneyCommand = true;

        senderMustBePlayer = false;
        senderMustBeMember = false;
        senderMustBeModerator = false;
        senderMustBeAdmin = false;

        this.helpLong.add(Factions.get().getTextUtil().parseTags(TL.COMMAND_MONEY_LONG.toString()));

        this.addSubCommand(this.cmdMoneyBalance);
        this.addSubCommand(this.cmdMoneyDeposit);
        this.addSubCommand(this.cmdMoneyWithdraw);
        this.addSubCommand(this.cmdMoneyTransferFf);
        this.addSubCommand(this.cmdMoneyTransferFp);
        this.addSubCommand(this.cmdMoneyTransferPf);
    }

    @Override
    public void perform() {
        this.commandChain.add(this);
        Factions.get().cmdAutoHelp.execute(this.sender, this.args, this.commandChain);
    }

    @Override
    public String getUsageTranslation() {
        return TL.COMMAND_MONEY_DESCRIPTION.toString();
    }

}
