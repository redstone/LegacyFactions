package net.redstoneore.legacyfactions.cmd;

import java.util.*;

import net.redstoneore.legacyfactions.Factions;
import net.redstoneore.legacyfactions.Permission;
import net.redstoneore.legacyfactions.Lang;
import net.redstoneore.legacyfactions.entity.Conf;
import net.redstoneore.legacyfactions.entity.Faction;
import net.redstoneore.legacyfactions.entity.FactionColl;
import net.redstoneore.legacyfactions.util.TagUtil;

public class CmdFactionsList extends FCommand {

	// -------------------------------------------------- //
	// CONSTRUCT
	// -------------------------------------------------- //
	
	public CmdFactionsList() {
		this.aliases.addAll(Conf.cmdAliasesList);
		
		this.optionalArgs.put("page", "1");
		
		this.permission = Permission.LIST.node;
		this.disableOnLock = false;
		
		this.senderMustBePlayer = false;
		this.senderMustBeMember = false;
		this.senderMustBeModerator = false;
		this.senderMustBeAdmin = false;
	}

	// -------------------------------------------------- //
	// METHODS
	// -------------------------------------------------- //
	
	@Override
	public void perform() {
		// if economy is enabled, they're not on the bypass list, and this command has a cost set, make 'em pay
		if (!this.payForCommand(Conf.econCostList, "to list the factions", "for listing the factions")) {
			return;
		}

		List<Faction> factionList = FactionColl.all();
		factionList.remove(FactionColl.get().getWilderness());
		factionList.remove(FactionColl.get().getSafeZone());
		factionList.remove(FactionColl.get().getWarZone());

		// Remove exempt factions
		if (!this.senderIsConsole && !this.fme.getPlayer().hasPermission("factions.show.bypassexempt")) {
			Iterator<Faction> factionIterator = factionList.iterator();
			while (factionIterator.hasNext()) {
				Faction next = factionIterator.next();
				if (!Conf.listExempt.contains(next.getTag())) continue;
				factionIterator.remove();	
			}
		}

		// Sort by total followers first
		Collections.sort(factionList, (Faction f1, Faction f2) -> {
			int f1Size = f1.getFPlayers().size();
			int f2Size = f2.getFPlayers().size();
			
			if (f1Size < f2Size) return 1;
			if (f1Size > f2Size) return -1;
			
			return 0;
		});

		// Then sort by how many members are online now
		Collections.sort(factionList, (Faction f1, Faction f2) -> {
			int f1Size = f1.getFPlayersWhereOnline(true).size();
			int f2Size = f2.getFPlayersWhereOnline(true).size();
			
			if (f1Size < f2Size) return 1;
			if (f1Size > f2Size) return -1;
			
			return 0;
		});
		
		factionList.add(0, FactionColl.get().getWilderness());

		final int pageheight = 9;
		int pagenumber = this.argAsInt(0, 1);
		int pagecount = (factionList.size() / pageheight) + 1;
		if (pagenumber > pagecount) {
			pagenumber = pagecount;
		} else if (pagenumber < 1) {
			pagenumber = 1;
		}
		
		int start = (pagenumber - 1) * pageheight;
		int end = start + pageheight;
		if (end > factionList.size()) {
			end = factionList.size();
		}
		
		String header = Conf.listHeader
				.replace("{pagenumber}", String.valueOf(pagenumber))
				.replace("{pagecount}", String.valueOf(pagecount));
		
		this.msg(Factions.get().getTextUtil().parse(header));

		factionList.subList(start, end).forEach(faction -> {
			if (faction.isWilderness()) {
				this.msg(Factions.get().getTextUtil().parse(TagUtil.parsePlain(faction, Conf.listFactionless)));
				return;
			}
			
			this.msg(Factions.get().getTextUtil().parse(TagUtil.parsePlain(faction, fme, Conf.listEntry)));
		});
	}

	@Override
	public String getUsageTranslation() {
		return Lang.COMMAND_LIST_DESCRIPTION.toString();
	}
}
