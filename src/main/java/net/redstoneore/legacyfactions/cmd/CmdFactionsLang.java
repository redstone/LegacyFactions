package net.redstoneore.legacyfactions.cmd;

import org.bukkit.ChatColor;

import net.redstoneore.legacyfactions.Permission;
import net.redstoneore.legacyfactions.config.CommandAliases;
import net.redstoneore.legacyfactions.config.meta.Meta;
import net.redstoneore.legacyfactions.lang.Lang;
import net.redstoneore.legacyfactions.lang.Langs;

public class CmdFactionsLang extends FCommand {
	
	// -------------------------------------------------- //
	// INSTANCE
	// -------------------------------------------------- //
	
	private static CmdFactionsLang instance = new CmdFactionsLang();
	public static CmdFactionsLang get() { return instance; }
	
	// -------------------------------------------------- //
	// CONSTRUCT
	// -------------------------------------------------- //

	private CmdFactionsLang() {
		this.aliases.addAll(CommandAliases.cmdAliasesLang);

		this.optionalArgs.put("lang", "lang=list");
		
		this.errorOnToManyArgs = false;
		
		this.permission = Permission.LANG.getNode();
		this.disableOnLock = true;
	}

	// -------------------------------------------------- //
	// METHODS
	// -------------------------------------------------- //
	
	@Override
	public void perform() {
		if (!this.argIsSet(0) || this.argAsString(0) == "list") {
			this.sendMessage("Langs: ");
			Langs.all().forEach(this::sendMessage);
			return;
		}
		
		Langs lang = Langs.valueOf(this.argAsString(0));
		
		if (lang == null) {
			this.sendMessage(ChatColor.RED + "Invalid lang.");
			return;
		}
		
		Meta.get().lang = lang;
		
		// Reload the lang using the new lang
		Lang.reload(lang.getPath());
		
		// Clear help page cache
		CmdFactionsHelp.get().clearHelpPageCache();
		
		this.sendMessage("Lang set to " + lang.name());
	}

	@Override
	public String getUsageTranslation() {
		return "";
	}
}
