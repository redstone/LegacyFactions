package net.redstoneore.legacyfactions.cmd;

import mkremins.fanciful.FancyMessage;
import net.redstoneore.legacyfactions.Permission;
import net.redstoneore.legacyfactions.Lang;
import net.redstoneore.legacyfactions.entity.Conf;
import net.redstoneore.legacyfactions.entity.FPlayer;
import net.redstoneore.legacyfactions.entity.FPlayerColl;

import org.bukkit.ChatColor;

public class CmdFactionsShowInvites extends FCommand {

	// -------------------------------------------------- //
	// CONSTRUCT
	// -------------------------------------------------- //

	public CmdFactionsShowInvites() {
		this.aliases.addAll(Conf.cmdAliasesShowInvites);
		
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
			msg.then(name + " ").color(ChatColor.WHITE).tooltip(Lang.COMMAND_SHOWINVITES_CLICKTOREVOKE.format(name)).command("/" + Conf.baseCommandAliases.get(0) + " deinvite " + name);
		}

		sendFancyMessage(msg);
	}

	@Override
	public String getUsageTranslation() {
		return Lang.COMMAND_SHOWINVITES_DESCRIPTION.toString();
	}

}
