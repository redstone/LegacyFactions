package net.redstoneore.legacyfactions.cmd;

import org.bukkit.ChatColor;

import net.redstoneore.legacyfactions.Permission;
import net.redstoneore.legacyfactions.config.CommandAliases;
import net.redstoneore.legacyfactions.entity.Faction;
import net.redstoneore.legacyfactions.entity.FactionColl;
import net.redstoneore.legacyfactions.lang.Lang;
import net.redstoneore.legacyfactions.util.TextUtil;

public class CmdFactionsFlagList extends FCommand {
	
	// -------------------------------------------------- //
	// INSTANCE
	// -------------------------------------------------- //
	
	private static CmdFactionsFlagList instance = new CmdFactionsFlagList();
	public static CmdFactionsFlagList get() { return instance; }

	// -------------------------------------------------- //
	// CONSTRUCT
	// -------------------------------------------------- //

	private CmdFactionsFlagList() {
		this.aliases.addAll(CommandAliases.cmdAliasesFlagList);
		
		this.optionalArgs.put("faction", "yours");
		
		this.permission = Permission.FLAG_LIST.getNode();
		
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
		Faction forFaction = this.argAsFaction(0, this.myFaction, true);
		if (forFaction == null) {
			if (!this.argIsSet(0) && this.senderIsConsole) {
				// Console defaults to wilderness
				forFaction = FactionColl.get().getWilderness();
			} else {
				return;
			}
		}
		
		// Ensure they have permission
		if (forFaction != this.fme.getFaction() && !Permission.FLAG_LIST_ANY.has(sender, true)) {
			return;
		}
		
		this.sendMessage(TextUtil.get().titleize(forFaction.getTag() + " flags"));
		
		forFaction.getFlags().forEach((flag, value) -> {
			ChatColor colour;
			if (value) {
				colour = ChatColor.DARK_GREEN;
			} else {
				colour = ChatColor.RED;
			}
			
			this.sendMessage(TextUtil.parseColor(ChatColor.GOLD + flag.getName() + ChatColor.WHITE + ChatColor.BOLD + " >> " + colour + value));
		});
	}
	
	@Override
	public String getUsageTranslation() {
		return Lang.COMMAND_FLAGLIST_DESCRIPTION.getBuilder().parse().toString();
	}

}
