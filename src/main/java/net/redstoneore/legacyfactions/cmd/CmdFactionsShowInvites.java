package net.redstoneore.legacyfactions.cmd;

import mkremins.fanciful.FancyMessage;
import net.redstoneore.legacyfactions.Permission;
import net.redstoneore.legacyfactions.config.CommandAliases;
import net.redstoneore.legacyfactions.entity.FPlayer;
import net.redstoneore.legacyfactions.entity.FPlayerColl;
import net.redstoneore.legacyfactions.lang.Lang;

import org.bukkit.ChatColor;

public class CmdFactionsShowInvites extends FCommand {

	// -------------------------------------------------- //
	// INSTANCE
	// -------------------------------------------------- //
	
	private static CmdFactionsShowInvites instance = new CmdFactionsShowInvites();
	public static CmdFactionsShowInvites get() { return instance; }
	
	// -------------------------------------------------- //
	// CONSTRUCT
	// -------------------------------------------------- //

	private CmdFactionsShowInvites() {
		this.aliases.addAll(CommandAliases.cmdAliasesShowInvites);
		
		this.permission = Permission.SHOW_INVITES.getNode();

		this.senderMustBePlayer = true;
		this.senderMustBeMember = true;
		this.senderMustBeModerator = false;
		this.senderMustBeColeader = false;
		this.senderMustBeAdmin = false;
	}

	// -------------------------------------------------- //
	// METHODS
	// -------------------------------------------------- //

	@Override
	public void perform() {
		FancyMessage msg = new FancyMessage(Lang.COMMAND_SHOWINVITES_PENDING.toString()).color(ChatColor.GOLD);
		for (String id : myFaction.getInvites()) {
			FPlayer fp = FPlayerColl.get(id);
			String name = fp != null ? fp.getName() : id;
			msg.then(name + " ").color(ChatColor.WHITE).tooltip(Lang.COMMAND_SHOWINVITES_CLICKTOREVOKE.format(name)).command("/" + CommandAliases.baseCommandAliases.get(0) + " deinvite " + name);
		}

		sendFancyMessage(msg);
	}

	@Override
	public String getUsageTranslation() {
		return Lang.COMMAND_SHOWINVITES_DESCRIPTION.toString();
	}

}
