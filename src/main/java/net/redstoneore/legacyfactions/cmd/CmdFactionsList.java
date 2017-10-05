package net.redstoneore.legacyfactions.cmd;

import java.util.*;

import net.redstoneore.legacyfactions.Permission;
import net.redstoneore.legacyfactions.config.CommandAliases;
import net.redstoneore.legacyfactions.config.Config;
import net.redstoneore.legacyfactions.entity.Faction;
import net.redstoneore.legacyfactions.entity.FactionColl;
import net.redstoneore.legacyfactions.lang.Lang;
import net.redstoneore.legacyfactions.util.TagUtil;
import net.redstoneore.legacyfactions.util.TextUtil;

public class CmdFactionsList extends FCommand {

	// -------------------------------------------------- //
	// INSTANCE
	// -------------------------------------------------- //
	
	private static CmdFactionsList instance = new CmdFactionsList();
	public static CmdFactionsList get() { return instance; }
	
	// -------------------------------------------------- //
	// CONSTRUCT
	// -------------------------------------------------- //
	
	private CmdFactionsList() {
		this.aliases.addAll(CommandAliases.cmdAliasesList);

		this.optionalArgs.put("page", "1");

		this.permission = Permission.LIST.getNode();
		this.disableOnLock = false;

		this.senderMustBePlayer = false;
		this.senderMustBeMember = false;
		this.senderMustBeModerator = false;
		this.senderMustBeColeader = false;
		this.senderMustBeAdmin = false;
	}

	// -------------------------------------------------- //
	// METHODS
	// -------------------------------------------------- //
	
	@Override
	public void perform() {
		// if economy is enabled, they're not on the bypass list, and this command has a cost set, make 'em pay
		if (!this.payForCommand(Config.econCostList, "to list the factions", "for listing the factions")) {
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
				if (!Config.listExempt.contains(next.getTag())) continue;
				factionIterator.remove();	
			}
		}

		// Sort by total followers first
		Collections.sort(factionList, (Faction faction1, Faction faction2) -> {
			int faction1Size = faction1.getMembers().size();
			int faction2Size = faction2.getMembers().size();
			
			if (faction1Size < faction2Size) return 1;
			if (faction1Size > faction2Size) return -1;
			
			return 0;
		});

		// Then sort by how many members are online now
		Collections.sort(factionList, (Faction faction1, Faction faction2) -> {
			int faction1Size = faction1.getWhereOnline(true).size();
			int faction2Size = faction2.getWhereOnline(true).size();
			
			if (faction1Size < faction2Size) return 1;
			if (faction1Size > faction2Size) return -1;
			
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
		
		String header = Config.listHeader
				.replace("{pagenumber}", String.valueOf(pagenumber))
				.replace("{pagecount}", String.valueOf(pagecount));
		
		this.sendMessage(TextUtil.get().parse(header));

		factionList.subList(start, end).stream()
			.filter(faction -> faction != null && faction.getId() != null)
			.forEach(faction -> {
				if (faction.isWilderness()) {
					this.sendMessage(TextUtil.get().parse(TagUtil.parsePlain(faction, Config.listFactionless)));
					return;
				}
				
				this.sendMessage(TextUtil.get().parse(TagUtil.parsePlain(faction, fme, Config.listEntry)));
		});
	}

	@Override
	public String getUsageTranslation() {
		return Lang.COMMAND_LIST_DESCRIPTION.toString();
	}
}
