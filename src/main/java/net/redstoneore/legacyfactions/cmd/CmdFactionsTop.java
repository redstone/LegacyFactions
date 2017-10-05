package net.redstoneore.legacyfactions.cmd;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import mkremins.fanciful.FancyMessage;
import net.redstoneore.legacyfactions.Permission;
import net.redstoneore.legacyfactions.config.CommandAliases;
import net.redstoneore.legacyfactions.entity.FPlayer;
import net.redstoneore.legacyfactions.entity.Faction;
import net.redstoneore.legacyfactions.entity.FactionColl;
import net.redstoneore.legacyfactions.integration.vault.VaultEngine;
import net.redstoneore.legacyfactions.lang.Lang;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class CmdFactionsTop extends FCommand {
	
	// -------------------------------------------------- //
	// INSTANCE
	// -------------------------------------------------- //
	
	private static CmdFactionsTop i = new CmdFactionsTop();
	public static CmdFactionsTop get() { return i; }
	
	// -------------------------------------------------- //
	// CONSTRUCT
	// -------------------------------------------------- //
	
	private CmdFactionsTop() {
		this.aliases.addAll(CommandAliases.cmdAliasesTop);
		
		this.optionalArgs.put("criteria", "criteria");
		this.optionalArgs.put("page", "1");

		this.permission = Permission.TOP.getNode();
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
		String criteria = argAsString(0, null);
		
		if (criteria == null) {
			String topPrefix = "/" + CmdFactions.get().aliases.get(0) + " " + this.aliases.get(0) + " ";
			
			FancyMessage buttons = new FancyMessage("")
				.then("[money] ")
					.color(ChatColor.AQUA)
					.command(topPrefix + "money")
					.tooltip(Lang.COMMAND_TOP_TOOLTIP_MONEY.toString())
					
				.then("[members] ")
					.color(ChatColor.AQUA)
					.command(topPrefix + "members")
					.tooltip(Lang.COMMAND_TOP_TOOLTIP_MEMBERS.toString())
					
				.then("[online] ")
					.color(ChatColor.AQUA)
					.command(topPrefix + "online")
					.tooltip(Lang.COMMAND_TOP_TOOLTIP_ONLINE.toString())
					
				.then("[allies] ")
					.color(ChatColor.AQUA)
					.command(topPrefix + "allies")
					.tooltip(Lang.COMMAND_TOP_TOOLTIP_ALLIES.toString())

				.then("[enemies] ")
					.color(ChatColor.AQUA)
					.command(topPrefix + "enemies")
					.tooltip(Lang.COMMAND_TOP_TOOLTIP_ENEMIES.toString())

				.then("[power] ")
					.color(ChatColor.AQUA)
					.command(topPrefix + "power")
					.tooltip(Lang.COMMAND_TOP_TOOLTIP_POWER.toString())

				.then("[land] ")
					.color(ChatColor.AQUA)
					.command(topPrefix + "land")
					.tooltip(Lang.COMMAND_TOP_TOOLTIP_LAND.toString());
			
			
			sendMessage(Lang.COMMAND_TOP_INVALID_NONE.toString());
			buttons.send(sender);
			return;
		}
		
		// Can sort by: money, members, online, allies, enemies, power, land.
		// Get all Factions and remove non player ones.
		List<Faction> factionList = FactionColl.get().getAllFactions().stream().filter(faction -> {
			return !faction.isWarZone() && !faction.isSafeZone() && !faction.isWilderness();
		}).collect(Collectors.toList());
		
		factionList.remove(FactionColl.get().getWilderness());
		factionList.remove(FactionColl.get().getSafeZone());
		factionList.remove(FactionColl.get().getWarZone());

		if (criteria.equalsIgnoreCase("members")) {
			Collections.sort(factionList, new Comparator<Faction>() {
				@Override
				public int compare(Faction f1, Faction f2) {
					int f1Size = f1.getMembers().size();
					int f2Size = f2.getMembers().size();
					if (f1Size < f2Size) {
						return 1;
					} else if (f1Size > f2Size) {
						return -1;
					}
					return 0;
				}
			});
		} else if (criteria.equalsIgnoreCase("start")) {
			Collections.sort(factionList, new Comparator<Faction>() {
				@Override
				public int compare(Faction f1, Faction f2) {
					long f1start = f1.getFoundedDate();
					long f2start = f2.getFoundedDate();
					// flip signs because a smaller date is farther in the past
					if (f1start > f2start) {
						return 1;
					} else if (f1start < f2start) {
						return -1;
					}
					return 0;
				}
			});
		} else if (criteria.equalsIgnoreCase("power")) {
			Collections.sort(factionList, new Comparator<Faction>() {
				@Override
				public int compare(Faction f1, Faction f2) {
					int f1Size = f1.getPowerRounded();
					int f2Size = f2.getPowerRounded();
					if (f1Size < f2Size) {
						return 1;
					} else if (f1Size > f2Size) {
						return -1;
					}
					return 0;
				}
			});
		} else if (criteria.equalsIgnoreCase("land")) {
			Collections.sort(factionList, new Comparator<Faction>() {
				@Override
				public int compare(Faction f1, Faction f2) {
					int f1Size = f1.getLandRounded();
					int f2Size = f2.getLandRounded();
					if (f1Size < f2Size) {
						return 1;
					} else if (f1Size > f2Size) {
						return -1;
					}
					return 0;
				}
			});
		} else if (criteria.equalsIgnoreCase("online")) {
			Collections.sort(factionList, new Comparator<Faction>() {
				@Override
				public int compare(Faction f1, Faction f2) {
					int f1Size = f1.getWhereOnline(true).size();
					int f2Size = f2.getWhereOnline(true).size();
					if (f1Size < f2Size) {
						return 1;
					} else if (f1Size > f2Size) {
						return -1;
					}
					return 0;
				}
			});
		} else if (criteria.equalsIgnoreCase("money") || criteria.equalsIgnoreCase("balance") || criteria.equalsIgnoreCase("bal")) {
			Collections.sort(factionList, new Comparator<Faction>() {
				@Override
				public int compare(Faction f1, Faction f2) {
					double f1Size = VaultEngine.getUtils().getBalance(f1.getAccountId());
					// Lets get the balance of /all/ the players in the Faction.
					for (FPlayer fp : f1.getMembers()) {
						f1Size = f1Size + VaultEngine.getUtils().getBalance(fp.getAccountId());
					}
					double f2Size = VaultEngine.getUtils().getBalance(f2.getAccountId());
					for (FPlayer fp : f2.getMembers()) {
						f2Size = f2Size + VaultEngine.getUtils().getBalance(fp.getAccountId());
					}
					if (f1Size < f2Size) {
						return 1;
					} else if (f1Size > f2Size) {
						return -1;
					}
					return 0;
				}
			});
		} else {
			sendMessage(Lang.COMMAND_TOP_INVALID.toString(), criteria);
			return;
		}

		ArrayList<String> lines = new ArrayList<String>();

		final int pageheight = 9;
		int pagenumber = this.argAsInt(1, 1);
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

		lines.add(Lang.COMMAND_TOP_TOP.format(criteria.toUpperCase(), pagenumber, pagecount));

		int rank = 1;
		for (Faction faction : factionList.subList(start, end)) {
			// Get the relation color if player is executing this.
			String fac = sender instanceof Player ? faction.getRelationTo(fme).getColor() + faction.getTag() : faction.getTag();
			lines.add(Lang.COMMAND_TOP_LINE.format(rank, fac, getValue(faction, criteria)));
			rank++;
		}

		sendMessage(lines);
	}

	private String getValue(Faction faction, String criteria) {
		if (criteria.equalsIgnoreCase("online")) {
			return String.valueOf(faction.getWhereOnline(true).size());
		}
		if (criteria.equalsIgnoreCase("start")) {
			return Lang.sdf.format(faction.getFoundedDate());
		}
		if (criteria.equalsIgnoreCase("members")) {
			return String.valueOf(faction.getMembers().size());
		}
		if (criteria.equalsIgnoreCase("land")) {
			return String.valueOf(faction.getLandRounded());
		}
		if (criteria.equalsIgnoreCase("start")) {
			return simpleDateFormat.format(faction.getFoundedDate());
		}
		if (criteria.equalsIgnoreCase("power")) {
			return String.valueOf(faction.getPowerRounded());
		}
		
		// Last one is balance, and it has 3 different things it could be.
		double balance = VaultEngine.getUtils().getBalance(faction.getAccountId());
		for (FPlayer fp : faction.getMembers()) {
			balance = balance + VaultEngine.getUtils().getBalance(fp.getAccountId());
		}
		return String.valueOf(balance);
	}

	@Override
	public String getUsageTranslation() {
		return Lang.COMMAND_TOP_DESCRIPTION.toString();
	}
	
}
