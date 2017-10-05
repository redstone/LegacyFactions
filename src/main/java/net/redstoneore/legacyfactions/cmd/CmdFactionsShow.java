package net.redstoneore.legacyfactions.cmd;

import mkremins.fanciful.FancyMessage;
import net.redstoneore.legacyfactions.Permission;
import net.redstoneore.legacyfactions.config.CommandAliases;
import net.redstoneore.legacyfactions.config.Config;
import net.redstoneore.legacyfactions.entity.FPlayer;
import net.redstoneore.legacyfactions.entity.Faction;
import net.redstoneore.legacyfactions.entity.FactionColl;
import net.redstoneore.legacyfactions.lang.Lang;
import net.redstoneore.legacyfactions.util.TagReplacerUtil;
import net.redstoneore.legacyfactions.util.TagUtil;
import net.redstoneore.legacyfactions.util.TextUtil;

import java.util.List;

import org.bukkit.command.CommandSender;

public class CmdFactionsShow extends FCommand {

	// -------------------------------------------------- //
	// INSTANCE
	// -------------------------------------------------- //
	
	private static CmdFactionsShow instance = new CmdFactionsShow();
	public static CmdFactionsShow get() { return instance; }
	
	// -------------------------------------------------- //
	// CONSTRUCT
	// -------------------------------------------------- //

	private CmdFactionsShow() {
		this.aliases.addAll(CommandAliases.cmdAliasesShow);
		
		this.optionalArgs.put("faction tag", "yours");

		this.permission = Permission.SHOW.getNode();
		this.disableOnLock = false;

		this.senderMustBeMember = false;
		this.senderMustBeModerator = false;
		this.senderMustBeAdmin = false;
	}

	// -------------------------------------------------- //
	// METHODS
	// -------------------------------------------------- //

	@Override
	public void perform() {
		// default to our faction
		Faction faction = this.myFaction;
		if (this.senderIsConsole) {
			// if we're the console, we're in the wilderness
			faction = FactionColl.get().getWilderness();
		}
		
		// unless we've specified one otherwise
		if (this.argIsSet(0)) {
			faction = this.argAsFaction(0, null, false);
		}
		
		if (faction == null) { 
			// Okay it's null 
			final CommandSender consoleSender = this.sender;
			final FPlayer fplayer = this.fme;
			final String searching = this.argAsString(0);
			
			this.argAsFactionOrPlayersFaction(0, (foundFaction, exception) -> {
				if (exception.isPresent()) {
					exception.get().printStackTrace();
					return;
				}
				
				if (foundFaction == null) {
					fplayer.sendMessage(Lang.COMMAND_ERRORS_PLAYERORFACTIONNOTFOUND.toString().replaceAll("<name>", searching));
					return;
				}
				
				// Resume
				resume(consoleSender, fplayer, foundFaction);
			});
			
			return;
		}
		
		// Resume
		resume(this.sender, this.fme, faction);
	}
	
	private static void resume(CommandSender sender, FPlayer fme, Faction faction) {
		// Check they have permission to do this
		if (!Permission.SHOW_BYPASSEXEMPT.has(sender) && Config.showExempt.contains(faction.getTag())) {
			sender.sendMessage(Lang.COMMAND_SHOW_EXEMPT.toString());
			return;
		}

		// if economy is enabled, they're not on the bypass list, and this command has a cost set, make 'em pay
		if (fme != null && !fme.payForCommand(Config.econCostShow, Lang.COMMAND_SHOW_TOSHOW.toString(), Lang.COMMAND_SHOW_FORSHOW.toString())) {
			return;
		}
		
		if (!faction.isNormal()) {
			String tag = faction.getTag();
			if (fme != null) {
				tag = faction.getTag(fme);
			}
			// send header and that's all
			String header = Config.showLines.get(0);
			if (TagReplacerUtil.HEADER.contains(header)) {
				sender.sendMessage(TextUtil.get().titleize(tag));
			} else {
				sender.sendMessage(TextUtil.get().parse(TagReplacerUtil.FACTION.replace(header, tag)));
			}
			return; // we only show header for non-normal factions
		}

		for (String line : Config.showLines) {
			String parsed = TagUtil.parsePlain(faction, fme, line); // use relations
			
			if (parsed == null) continue; // Due to minimal f show.
			
			if (TagUtil.hasFancy(parsed)) {
				List<FancyMessage> fancy = null;
				
				fancy = TagUtil.parseFancy(faction, fme, parsed);
				if (fancy == null) continue;
				
				fancy.forEach(message -> {
					message.send(sender);
				});
				continue;
			}
			
			if (!parsed.contains("{notFrozen}") && !parsed.contains("{notPermanent}")) {
				if (parsed.contains("{ig}")) {
					// replaces all variables with no home TL
					parsed = parsed.substring(0, parsed.indexOf("{ig}")) + Lang.COMMAND_SHOW_NOHOME.toString();
				}
				if (parsed.contains("%")) {
					parsed = parsed.replaceAll("%", ""); // Just in case it got in there before we disallowed it.
				}
				sender.sendMessage(TextUtil.get().parse(parsed));
			}
		}
	}

	@Override
	public String getUsageTranslation() {
		return Lang.COMMAND_SHOW_COMMANDDESCRIPTION.toString();
	}

}