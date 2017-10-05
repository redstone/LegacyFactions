package net.redstoneore.legacyfactions.cmd;

import org.apache.commons.lang.time.DurationFormatUtils;
import org.bukkit.ChatColor;

import net.redstoneore.legacyfactions.Permission;
import net.redstoneore.legacyfactions.config.CommandAliases;
import net.redstoneore.legacyfactions.entity.FPlayer;
import net.redstoneore.legacyfactions.lang.Lang;

import java.util.ArrayList;

public class CmdFactionsStatus extends FCommand {

	// -------------------------------------------------- //
	// INSTANCE
	// -------------------------------------------------- //
	
	private static CmdFactionsStatus instance = new CmdFactionsStatus();
	public static CmdFactionsStatus get() { return instance; }
	
	// -------------------------------------------------- //
	// CONSTRUCT
	// -------------------------------------------------- //

	private CmdFactionsStatus() {
		this.aliases.addAll(CommandAliases.cmdAliasesStatus);

		this.permission = Permission.STATUS.getNode();

		this.senderMustBePlayer = true;
		this.senderMustBeMember = true;
		this.senderMustBeModerator = false;
		this.senderMustBeAdmin = false;
	}

	// -------------------------------------------------- //
	// METHODS
	// -------------------------------------------------- //

	@Override
	public void perform() {
		ArrayList<String> ret = new ArrayList<>();
		for (FPlayer fp : myFaction.getMembers()) {
			String humanized = DurationFormatUtils.formatDurationWords(System.currentTimeMillis() - fp.getLastLoginTime(), true, true) + Lang.COMMAND_STATUS_AGOSUFFIX;
			String last = fp.isOnline() ? ChatColor.GREEN + Lang.COMMAND_STATUS_ONLINE.toString() : (System.currentTimeMillis() - fp.getLastLoginTime() < 432000000 ? ChatColor.YELLOW + humanized : ChatColor.RED + humanized);
			String power = ChatColor.YELLOW + String.valueOf(fp.getPowerRounded()) + " / " + String.valueOf(fp.getPowerMaxRounded()) + ChatColor.RESET;
			ret.add(String.format(Lang.COMMAND_STATUS_FORMAT.toString(), ChatColor.GOLD + fp.getRole().getPrefix() + fp.getName() + ChatColor.RESET, power, last).trim());
		}
		fme.sendMessage(ret);
	}

	@Override
	public String getUsageTranslation() {
		return Lang.COMMAND_STATUS_DESCRIPTION.toString();
	}

}
