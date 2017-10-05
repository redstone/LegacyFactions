package net.redstoneore.legacyfactions.cmd;

import org.bukkit.command.ConsoleCommandSender;

import com.google.common.base.Joiner;

import net.redstoneore.legacyfactions.config.CommandAliases;
import net.redstoneore.legacyfactions.config.Config;
import net.redstoneore.legacyfactions.config.meta.Meta;
import net.redstoneore.legacyfactions.entity.Board;
import net.redstoneore.legacyfactions.entity.FPlayerColl;
import net.redstoneore.legacyfactions.entity.FactionColl;
import net.redstoneore.legacyfactions.entity.persist.PersistHandler;
import net.redstoneore.legacyfactions.entity.persist.PersistType;
import net.redstoneore.legacyfactions.lang.Lang;

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
		
		this.requiredArgs.add("[" + Joiner.on("|").join(PersistType.values()) + "]");
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
		
		// Perform the save all command and notify the console
		CmdFactionsSaveAll.get().perform(true, this.sender);
		
		PersistType persistType = PersistType.valueOf(this.argAsString(0).toUpperCase());
		if (persistType == Config.backEnd) {
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
		
		if (persistType.requiresDatabaseCredentials()) {
			if (!this.argIsSet(4)) {
				Lang.COMMAND_CONVERT_BACKEND_DBCREDENTIALS.getBuilder()
					.parse()
					.replace("<type>", persistType.toString())
					.sendTo(this.sender);
				
				return;
			}
			
			// Update meta with new credentials
			Meta.get().databaseHost = this.argAsString(1);
			Meta.get().databaseUsername = this.argAsString(2);
			Meta.get().databasePassword = this.argAsString(3);
			Meta.get().databaseName = this.argAsString(4);
		} else {
			// Remove old credentials
			Meta.get().databaseHost = "";
			Meta.get().databaseUsername = "";
			Meta.get().databasePassword = "";
			Meta.get().databaseName = "";
		}
		
		// Mark as non-encrypted
		Meta.get().databaseCredentialsEncrypted = false;
		
		// Set current
		// This will trigger a conversion
		PersistHandler.setCurrent(persistType.getHandler());
		
		// Load everything
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
