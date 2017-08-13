package net.redstoneore.legacyfactions.cmd;

import org.bukkit.command.ConsoleCommandSender;

import net.redstoneore.legacyfactions.Lang;
import net.redstoneore.legacyfactions.entity.Conf;
import net.redstoneore.legacyfactions.entity.Conf.Backend;
import net.redstoneore.legacyfactions.entity.persist.json.FactionsJSON;

public class CmdFactionsConvert extends FCommand {

	// -------------------------------------------------- //
	// INSTANCE
	// -------------------------------------------------- //
	
	private static CmdFactionsConvert instance = new CmdFactionsConvert();
	public static CmdFactionsConvert get() { return instance; }
	
	// -------------------------------------------------- //
	// CONSTRUCT
	// -------------------------------------------------- //

	private CmdFactionsConvert() {
		this.aliases.addAll(Conf.cmdAliasesConvert);

		this.requiredArgs.add("[MYSQL|JSON]");
	}

	// -------------------------------------------------- //
	// METHODS
	// -------------------------------------------------- //

	@Override
	public void perform() {
		if (!(this.sender instanceof ConsoleCommandSender)) {
			this.sender.sendMessage(Lang.GENERIC_CONSOLEONLY.toString());
		}
		
		Backend newBackend = Backend.valueOf(this.argAsString(0).toUpperCase());
		if (newBackend == Conf.backEnd) {
			this.sender.sendMessage(Lang.COMMAND_CONVERT_BACKEND_RUNNING.toString());
			return;
		}
		
		switch (newBackend) {
		case JSON:
			FactionsJSON.convertTo();
			break;
		default:
			this.sender.sendMessage(Lang.COMMAND_CONVERT_BACKEND_INVALID.toString());
			return;
		}
		
		Conf.backEnd = newBackend;
	}

	@Override
	public String getUsageTranslation() {
		return Lang.COMMAND_CONVERT_DESCRIPTION.toString();
	}

}
