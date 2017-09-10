package net.redstoneore.legacyfactions.cmd;

import org.bukkit.command.ConsoleCommandSender;

import net.redstoneore.legacyfactions.Lang;
import net.redstoneore.legacyfactions.entity.Board;
import net.redstoneore.legacyfactions.entity.CommandAliases;
import net.redstoneore.legacyfactions.entity.Conf;
import net.redstoneore.legacyfactions.entity.FPlayerColl;
import net.redstoneore.legacyfactions.entity.FactionColl;
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
			Lang.GENERIC_CONSOLEONLY.getBuilder()
				.parse()
				.sendTo(this.sender);
			
			return;
		}
		
		PersistType persistType = PersistType.valueOf(this.argAsString(0).toUpperCase());
		if (persistType == Conf.backEnd) {
			Lang.COMMAND_CONVERT_BACKEND_RUNNING.getBuilder()
				.parse()
				.sendTo(this.sender);
			
			return;
		}
		
		if (persistType == null) {
			Lang.COMMAND_CONVERT_BACKEND_INVALID.getBuilder()
				.parse()
				.sendTo(this.sender);
		}
		
		// Update meta with the credentials
		if (this.argIsSet(1)) {
			Meta.get().databaseHost = this.argAsString(1);
		}
		
		if (this.argIsSet(2)) {
			Meta.get().databaseUsername = this.argAsString(2);
		}
		
		if (this.argIsSet(3)) {
			if (!this.argAsString(3).equalsIgnoreCase("null")) {
				Meta.get().databasePassword = this.argAsString(3);
			} else {
				Meta.get().databasePassword = "";
			}
		}
		
		if (this.argIsSet(4)) {
			Meta.get().databaseName = this.argAsString(4);
		}
		
		Meta.get().databaseCredentialsEncrypted = false;
		
		// Set current
		PersistHandler.setCurrent(persistType.getHandler());
		
		FPlayerColl.getUnsafeInstance().loadColl();
		FactionColl.get().load();
		Board.get().load();
		
		// Save meta
		Meta.get().save();
	}

	@Override
	public String getUsageTranslation() {
		return Lang.COMMAND_CONVERT_DESCRIPTION.toString();
	}

}
