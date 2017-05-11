package net.redstoneore.legacyfactions.cmd;

import mkremins.fanciful.FancyMessage;
import net.redstoneore.legacyfactions.Factions;
import net.redstoneore.legacyfactions.Permission;
import net.redstoneore.legacyfactions.Lang;
import net.redstoneore.legacyfactions.entity.Conf;
import net.redstoneore.legacyfactions.entity.Faction;
import net.redstoneore.legacyfactions.util.TagReplacerUtil;
import net.redstoneore.legacyfactions.util.TagUtil;

import java.util.List;

public class CmdFactionsShow extends FCommand {
	
	public CmdFactionsShow() {
		this.aliases.addAll(Conf.cmdAliasesShow);
		
		this.optionalArgs.put("faction tag", "yours");

		this.permission = Permission.SHOW.node;
		this.disableOnLock = false;

		this.senderMustBeMember = false;
		this.senderMustBeModerator = false;
		this.senderMustBeAdmin = false;
	}

	@Override
	public void perform() {
		Faction faction = myFaction;
		if (this.argIsSet(0)) {
			faction = this.argAsFaction(0, null);
		}
		
		if (faction == null) {
			return;
		}

		if (!fme.getPlayer().hasPermission("factions.show.bypassexempt")
				&& Conf.showExempt.contains(faction.getTag())) {
			msg(Lang.COMMAND_SHOW_EXEMPT);
			return;
		}

		// if economy is enabled, they're not on the bypass list, and this command has a cost set, make 'em pay
		if (!payForCommand(Conf.econCostShow, Lang.COMMAND_SHOW_TOSHOW, Lang.COMMAND_SHOW_FORSHOW)) {
			return;
		}
		
		if (!faction.isNormal()) {
			String tag = faction.getTag(fme);
			// send header and that's all
			String header = Conf.showLines.get(0);
			if (TagReplacerUtil.HEADER.contains(header)) {
				msg(Factions.get().getTextUtil().titleize(tag));
			} else {
				msg(Factions.get().getTextUtil().parse(TagReplacerUtil.FACTION.replace(header, tag)));
			}
			return; // we only show header for non-normal factions
		}

		for (String line : Conf.showLines) {
			String parsed = TagUtil.parsePlain(faction, fme, line); // use relations
			
			if (parsed == null) continue; // Due to minimal f show.
			
			if (TagUtil.hasFancy(parsed)) {
				List<FancyMessage> fancy = TagUtil.parseFancy(faction, fme, parsed);
				if (fancy == null) continue;
				sendFancyMessage(fancy);
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
				msg(Factions.get().getTextUtil().parse(parsed));
			}
		}
	}

	@Override
	public String getUsageTranslation() {
		return Lang.COMMAND_SHOW_COMMANDDESCRIPTION.toString();
	}

}