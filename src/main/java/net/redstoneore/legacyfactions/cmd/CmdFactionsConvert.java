package net.redstoneore.legacyfactions.cmd;

import org.bukkit.command.ConsoleCommandSender;

import net.redstoneore.legacyfactions.Lang;
import net.redstoneore.legacyfactions.entity.CommandAliases;
import net.redstoneore.legacyfactions.entity.Conf;
import net.redstoneore.legacyfactions.entity.Meta;
import net.redstoneore.legacyfactions.entity.persist.PersistHandler;
import net.redstoneore.legacyfactions.entity.persist.PersistType;

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
		this.aliases.addAll(CommandAliases.cmdAliasesConvert);

		this.requiredArgs.add("[JSON|MYSQL]");
		this.optionalArgs.put("dbhost", "none");
		this.optionalArgs.put("dbusername", "none");
		this.optionalArgs.put("dbpassword", "none");
		this.optionalArgs.put("dbname", "none");
	}

	// -------------------------------------------------- //
	// METHODS
	// -------------------------------------------------- //

	@Override
	public void perform() {
		if (!(this.sender instanceof ConsoleCommandSender)) {
			this.sender.sendMessage(Lang.GENERIC_CONSOLEONLY.toString());
		}
		
		PersistType persistType = PersistType.valueOf(this.argAsString(0).toUpperCase());
		if (persistType == Conf.backEnd) {
			Lang.COMMAND_CONVERT_BACKEND_RUNNING.getBuilder()
				.parse()
				.sendTo(this.fme);
			
			return;
		}
		
		if (persistType == null) {
			Lang.COMMAND_CONVERT_BACKEND_INVALID.getBuilder()
				.parse()
				.sendTo(this.fme);
		}
		
		// Update meta with the credentials
		if (this.argIsSet(1)) {
			Meta.get().databaseHost = this.argAsString(1);
		}
		if (this.argIsSet(2)) {
			Meta.get().databaseUsername = this.argAsString(2);
		}
		if (this.argIsSet(3)) {
			Meta.get().databasePassword = this.argAsString(3);
		}
		if (this.argIsSet(4)) {
			Meta.get().databaseName = this.argAsString(4);
		}
		
		// Save meta
		Meta.get().save();
		
		// Set current
		PersistHandler.setCurrent(persistType.getHandler());
	}

	@Override
	public String getUsageTranslation() {
		return Lang.COMMAND_CONVERT_DESCRIPTION.toString();
	}

}
