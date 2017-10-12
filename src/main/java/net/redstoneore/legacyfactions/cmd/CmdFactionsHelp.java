package net.redstoneore.legacyfactions.cmd;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;

import mkremins.fanciful.FancyMessage;
import net.redstoneore.legacyfactions.Permission;
import net.redstoneore.legacyfactions.config.CommandAliases;
import net.redstoneore.legacyfactions.config.Config;
import net.redstoneore.legacyfactions.entity.FPlayer;
import net.redstoneore.legacyfactions.integration.vault.VaultEngine;
import net.redstoneore.legacyfactions.lang.Lang;
import net.redstoneore.legacyfactions.util.TextUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class CmdFactionsHelp extends FCommand {

	// -------------------------------------------------- //
	// INSTANCE
	// -------------------------------------------------- //
	
	private static CmdFactionsHelp instance = new CmdFactionsHelp();
	public static CmdFactionsHelp get() { return instance; }
	
	// -------------------------------------------------- //
	// CONSTRUCT
	// -------------------------------------------------- //

	private CmdFactionsHelp() {
		this.aliases.addAll(CommandAliases.cmdAliasesHelp);

		this.optionalArgs.put("page", "1");

		this.permission = Permission.HELP.getNode();
		this.disableOnLock = false;

		this.senderMustBePlayer = false;
		this.senderMustBeMember = false;
		this.senderMustBeModerator = false;
		this.senderMustBeColeader = false;
		this.senderMustBeAdmin = false;
	}

	// -------------------------------------------------- //
	// FIELDS
	// -------------------------------------------------- //
	
	private Map<String, List<List<FancyMessage>>> helpPageCache = new HashMap<>();
	
	// -------------------------------------------------- //
	// METHODS
	// -------------------------------------------------- //

	@Override
	public void perform() {
		// For those that want to create their own help menu they can use this (why)
		if (Config.useCustomHelp) {
			String pageArg = this.argAsString(0, "1");
			List<String> page = Config.helpPages.get(pageArg);
			
			if (page == null || page.isEmpty()) {
				this.sendMessage(Lang.COMMAND_HELP_404.format(pageArg));
				return;
			}
			
			page.forEach(line -> this.sendMessage(TextUtil.get().parse(line)));
				
			return;
		}
		
		// For those that want to use the old help menu the can use this 
		if (Config.useOldHelp) {
			if (helpPages == null) this.updateHelp();
			
			int page = this.argAsInt(0, 1);
			sendMessage(TextUtil.get().titleize(Lang.COMMAND_HELP_PAGES_TITLE.getBuilder().replace("<current>", page).replace("<total>", helpPages.size()).toString()));

			page -= 1;

			if (page < 0 || page >= helpPages.size()) {
				sendMessage(Lang.COMMAND_HELP_404.format(String.valueOf(page)));
				return;
			}
			sendMessage(helpPages.get(page));
			return;
		}	
		
		String id = "console";
		if (!(sender instanceof ConsoleCommandSender)) {
			id = fme.getId();
		}
		
		// Otherwise, those that want an automatically generated menu
		if (!this.helpPageCache.containsKey(id)) {
			int line = 0;
			List<List<FancyMessage>> pages = new ArrayList<>();
			List<FancyMessage> lines = new ArrayList<>();
			
			for (FCommandBase<?> command : CmdFactions.get().getSubcommands()) {
				if (!(sender instanceof ConsoleCommandSender) && command.permission != null && !fme.getPlayer().hasPermission(command.permission)) continue;
				if (!command.isAvailable()) continue;
				
				line++;
				
				String suggest = "/" + CmdFactions.get().aliases.get(0) + " " + command.aliases.get(0);
				
				FancyMessage fm = new FancyMessage();
				fm.text(command.getUseageTemplate(true));
				fm.suggest(suggest);
				fm.tooltip(suggest);
				
				lines.add(fm);
				
				if (line == 10) {
					pages.add(lines);
					lines = new ArrayList<>();
					line = 0;
				}
			}
			
			this.helpPageCache.put(id, pages);
		}
		
		int page = this.argAsInt(0, 1);
		int maxPages = this.helpPageCache.get(id).size();
		
		if (page <= 0 || page > maxPages) {
			sendMessage(Lang.COMMAND_HELP_404.format(String.valueOf(page)));
			return;
		}
		
		String title = TextUtil.get().titleize(Lang.COMMAND_HELP_PAGES_TITLE.getBuilder().replace("<current>", page).replace("<total>", maxPages).toString());
		
		FancyMessage fm = new FancyMessage(Lang.COMMAND_HELP_PAGES_BTN_LEFT.getBuilder().parse().toString() +" ");
		
		if (page == 1) {
			fm.tooltip(Lang.COMMAND_HELP_PAGES_NOPREV.getBuilder().parse().toString());
			fm.color(ChatColor.GRAY);
		} else {
			int prevPage = page-1;
			fm.tooltip(Lang.COMMAND_HELP_PAGES_GOTO.getBuilder().replace("<number>", prevPage).toString());
			fm.style(ChatColor.BOLD);
			fm.command("/" + CmdFactions.get().aliases.get(0) +" " + this.aliases.get(0) + " " + prevPage);
		}
		
		fm.then(title);
		fm.then(" " + Lang.COMMAND_HELP_PAGES_BTN_RIGHT.getBuilder().parse().toString());
		
		if (page == maxPages) {
			fm.tooltip(Lang.COMMAND_HELP_PAGES_NONEXT.getBuilder().parse().toString());
			fm.color(ChatColor.GRAY);
		} else {
			int nextPage = page+1;
			fm.tooltip(Lang.COMMAND_HELP_PAGES_GOTO.getBuilder().replace("<number>", nextPage).toString());
			fm.style(ChatColor.BOLD);
			fm.command("/" + CmdFactions.get().aliases.get(0) +" " + this.aliases.get(0) + " " + nextPage);
		}
		
		fm.send(this.sender);
		
		this.helpPageCache.get(id).get(page-1).forEach(line -> {
			line.send(this.sender);
		});
	}	
	
	public void clearHelpPageCache(CommandSender sender) {
		String id = "console";
		if (!(sender instanceof ConsoleCommandSender)) {
			id = fme.getId();
		}
		
		this.helpPageCache.remove(id);
	}
	
	public void clearHelpPageCache(FPlayer fplayer) {
		this.clearHelpPageCache(fplayer.getPlayer());
	}
	
	public void clearHelpPageCache() {
		this.helpPageCache.clear();
	}
	
	//----------------------------------------------//
	// Old help pages
	//----------------------------------------------//

	public ArrayList<ArrayList<String>> helpPages;

	public void updateHelp() {
		this.helpPages = new ArrayList<>();
		ArrayList<String> pageLines;

		pageLines = new ArrayList<>();
		pageLines.add(CmdFactionsHelp.get().getUseageTemplate(true));
		pageLines.add(CmdFactionsList.get().getUseageTemplate(true));
		pageLines.add(CmdFactionsShow.get().getUseageTemplate(true));
		pageLines.add(CmdFactionsPower.get().getUseageTemplate(true));
		pageLines.add(CmdFactionsJoin.get().getUseageTemplate(true));
		pageLines.add(CmdFactionsLeave.get().getUseageTemplate(true));
		pageLines.add(CmdFactionsHome.get().getUseageTemplate(true));
		pageLines.add(TextUtil.get().parse(Lang.COMMAND_HELP_NEXTCREATE.toString()));
		this.helpPages.add(pageLines);

		pageLines = new ArrayList<>();
		pageLines.add(CmdFactionsCreate.get().getUseageTemplate(true));
		pageLines.add(CmdFactionsDescription.get().getUseageTemplate(true));
		pageLines.add(CmdFactionsTag.get().getUseageTemplate(true));
		pageLines.add(TextUtil.get().parse(Lang.COMMAND_HELP_INVITATIONS.toString()));
		pageLines.add(CmdFactionsOpen.get().getUseageTemplate(true));
		pageLines.add(CmdFactionsInvite.get().getUseageTemplate(true));
		pageLines.add(CmdFactionsDeinvite.get().getUseageTemplate(true));
		pageLines.add(TextUtil.get().parse(Lang.COMMAND_HELP_HOME.toString()));
		pageLines.add(CmdFactionsSethome.get().getUseageTemplate(true));
		this.helpPages.add(pageLines);

		if (VaultEngine.isSetup() && Config.econEnabled && Config.bankEnabled) {
			pageLines = new ArrayList<>();
			pageLines.add("");
			pageLines.add(TextUtil.get().parse(Lang.COMMAND_HELP_BANK_1.toString()));
			pageLines.add(TextUtil.get().parse(Lang.COMMAND_HELP_BANK_2.toString()));
			pageLines.add(TextUtil.get().parse(Lang.COMMAND_HELP_BANK_3.toString()));
			pageLines.add("");
			pageLines.add(CmdFactionsMoney.get().getUseageTemplate(true));
			pageLines.add("");
			pageLines.add("");
			pageLines.add("");
			this.helpPages.add(pageLines);
		}

		pageLines = new ArrayList<>();
		pageLines.add(CmdFactionsClaim.get().getUseageTemplate(true));
		pageLines.add(CmdFactionsAutoclaim.get().getUseageTemplate(true));
		pageLines.add(CmdFactionsUnclaim.get().getUseageTemplate(true));
		pageLines.add(CmdFactionsUnclaimall.get().getUseageTemplate(true));
		pageLines.add(CmdFactionsKick.get().getUseageTemplate(true));
		pageLines.add(CmdFactionsMod.get().getUseageTemplate(true));
		pageLines.add(CmdFactionsColeader.get().getUseageTemplate(true));
		pageLines.add(CmdFactionsAdmin.get().getUseageTemplate(true));
		pageLines.add(CmdFactionsTitle.get().getUseageTemplate(true));
		pageLines.add(CmdFactionsScoreboard.get().getUseageTemplate(true));
		pageLines.add(CmdFactionsSeeChunk.get().getUseageTemplate(true));
		pageLines.add(CmdFactionsStatus.get().getUseageTemplate(true));
		pageLines.add(TextUtil.get().parse(Lang.COMMAND_HELP_PLAYERTITLES.toString()));
		this.helpPages.add(pageLines);

		pageLines = new ArrayList<>();
		pageLines.add(CmdFactionsMap.get().getUseageTemplate(true));
		pageLines.add(CmdFactionsBoom.get().getUseageTemplate(true));
		pageLines.add(CmdFactionsOwner.get().getUseageTemplate(true));
		pageLines.add(CmdFactionsOwnerList.get().getUseageTemplate(true));
		pageLines.add(TextUtil.get().parse(Lang.COMMAND_HELP_OWNERSHIP_1.toString()));
		pageLines.add(TextUtil.get().parse(Lang.COMMAND_HELP_OWNERSHIP_2.toString()));
		pageLines.add(TextUtil.get().parse(Lang.COMMAND_HELP_OWNERSHIP_3.toString()));
		this.helpPages.add(pageLines);

		pageLines = new ArrayList<>();
		pageLines.add(CmdFactionsDisband.get().getUseageTemplate(true));
		pageLines.add("");
		pageLines.add(CmdFactionsRelationAlly.get().getUseageTemplate(true));
		pageLines.add(CmdFactionsRelationNeutral.get().getUseageTemplate(true));
		pageLines.add(CmdFactionsRelationEnemy.get().getUseageTemplate(true));
		pageLines.add(TextUtil.get().parse(Lang.COMMAND_HELP_RELATIONS_1.toString()));
		pageLines.add(TextUtil.get().parse(Lang.COMMAND_HELP_RELATIONS_2.toString()));
		pageLines.add(TextUtil.get().parse(Lang.COMMAND_HELP_RELATIONS_3.toString()));
		pageLines.add(TextUtil.get().parse(Lang.COMMAND_HELP_RELATIONS_4.toString()));
		this.helpPages.add(pageLines);

		pageLines = new ArrayList<>();
		pageLines.add(TextUtil.get().parse(Lang.COMMAND_HELP_RELATIONS_5.toString()));
		pageLines.add(TextUtil.get().parse(Lang.COMMAND_HELP_RELATIONS_6.toString()));
		pageLines.add(TextUtil.get().parse(Lang.COMMAND_HELP_RELATIONS_7.toString()));
		pageLines.add(Lang.COMMAND_HELP_RELATIONS_8.toString());
		pageLines.add(TextUtil.get().parse(Lang.COMMAND_HELP_RELATIONS_9.toString()));
		pageLines.add(TextUtil.get().parse(Lang.COMMAND_HELP_RELATIONS_10.toString()));
		pageLines.add(TextUtil.get().parse(Lang.COMMAND_HELP_RELATIONS_11.toString()));
		pageLines.add(TextUtil.get().parse(Lang.COMMAND_HELP_RELATIONS_12.toString()));
		pageLines.add(TextUtil.get().parse(Lang.COMMAND_HELP_RELATIONS_13.toString()));
		this.helpPages.add(pageLines);

		pageLines = new ArrayList<>();
		pageLines.add(TextUtil.get().parse(Lang.COMMAND_HELP_PERMISSIONS_1.toString()));
		pageLines.add(TextUtil.get().parse(Lang.COMMAND_HELP_PERMISSIONS_2.toString()));
		pageLines.add(TextUtil.get().parse(Lang.COMMAND_HELP_PERMISSIONS_3.toString()));
		pageLines.add(Lang.COMMAND_HELP_PERMISSIONS_4.toString());
		pageLines.add(TextUtil.get().parse(Lang.COMMAND_HELP_PERMISSIONS_5.toString()));
		pageLines.add(TextUtil.get().parse(Lang.COMMAND_HELP_PERMISSIONS_6.toString()));
		pageLines.add(TextUtil.get().parse(Lang.COMMAND_HELP_PERMISSIONS_7.toString()));
		pageLines.add(TextUtil.get().parse(Lang.COMMAND_HELP_PERMISSIONS_8.toString()));
		pageLines.add(TextUtil.get().parse(Lang.COMMAND_HELP_PERMISSIONS_9.toString()));
		this.helpPages.add(pageLines);

		pageLines = new ArrayList<>();
		pageLines.add(Lang.COMMAND_HELP_MOAR_1.toString());
		pageLines.add(CmdFactionsBypass.get().getUseageTemplate(true));
		pageLines.add(TextUtil.get().parse(Lang.COMMAND_HELP_ADMIN_1.toString()));
		pageLines.add(TextUtil.get().parse(Lang.COMMAND_HELP_ADMIN_2.toString()));
		pageLines.add(TextUtil.get().parse(Lang.COMMAND_HELP_ADMIN_3.toString()));
		pageLines.add(CmdFactionsSafeunclaimall.get().getUseageTemplate(true));
		pageLines.add(CmdFactionsWarunclaimall.get().getUseageTemplate(true));
		
		pageLines.add(TextUtil.get().parse("<i>Note: " + CmdFactionsUnclaim.get().getUseageTemplate(false) + TextUtil.get().parse("<i>") + " works on safe/war zones as well."));
		pageLines.add(CmdFactionsPeaceful.get().getUseageTemplate(true));
		this.helpPages.add(pageLines);

		pageLines = new ArrayList<>();
		pageLines.add(TextUtil.get().parse(Lang.COMMAND_HELP_MOAR_2.toString()));
		pageLines.add(CmdFactionsPermanent.get().getUseageTemplate(true));
		pageLines.add(CmdFactionsPermanentPower.get().getUseageTemplate(true));
		pageLines.add(CmdFactionsPowerBoost.get().getUseageTemplate(true));
		pageLines.add(CmdFactionsConfig.get().getUseageTemplate(true));
		this.helpPages.add(pageLines);

		pageLines = new ArrayList<>();
		pageLines.add(TextUtil.get().parse(Lang.COMMAND_HELP_MOAR_3.toString()));
		pageLines.add(CmdFactionsLock.get().getUseageTemplate(true));
		pageLines.add(CmdFactionsReload.get().getUseageTemplate(true));
		pageLines.add(CmdFactionsSaveAll.get().getUseageTemplate(true));
		pageLines.add(CmdFactionsVersion.get().getUseageTemplate(true));
		this.helpPages.add(pageLines);
	}

	@Override
	public String getUsageTranslation() {
		return Lang.COMMAND_HELP_DESCRIPTION.toString();
	}
	
}
