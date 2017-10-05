package net.redstoneore.legacyfactions.cmd;

import mkremins.fanciful.FancyMessage;

import net.redstoneore.legacyfactions.Permission;
import net.redstoneore.legacyfactions.config.CommandAliases;
import net.redstoneore.legacyfactions.entity.FPlayer;
import net.redstoneore.legacyfactions.entity.FPlayerColl;
import net.redstoneore.legacyfactions.lang.Lang;

import org.bukkit.ChatColor;

public class CmdFactionsDeinvite extends FCommand {

	// -------------------------------------------------- //
	// INSTANCE
	// -------------------------------------------------- //
	
	private static CmdFactionsDeinvite instance = new CmdFactionsDeinvite();
	public static CmdFactionsDeinvite get() { return instance; }
	
	// -------------------------------------------------- //
	// CONSTRUCT
	// -------------------------------------------------- //

	private CmdFactionsDeinvite() {
		this.aliases.addAll(CommandAliases.cmdAliasesDeinvite);

		this.optionalArgs.put("player name", "name");

		this.permission = Permission.DEINVITE.getNode();
		this.disableOnLock = true;

		this.senderMustBePlayer = true;
		this.senderMustBeMember = false;
		this.senderMustBeModerator = true;
		this.senderMustBeColeader = false;
		this.senderMustBeAdmin = false;
	}

	// -------------------------------------------------- //
	// METHODS
	// -------------------------------------------------- //

	@Override
	public void perform() {
		FPlayer you = this.argAsBestFPlayerMatch(0);
		
		if (you == null) {
			FancyMessage message = new FancyMessage(Lang.COMMAND_DEINVITE_CANDEINVITE.toString())
				.color(ChatColor.GOLD);
			
			for (String id : myFaction.getInvites()) {
				FPlayer fp = FPlayerColl.get(id);
				String name = fp != null ? fp.getName() : id;
				
				message.then(name + " ")
					.color(ChatColor.WHITE)
					.tooltip(Lang.COMMAND_DEINVITE_CLICKTODEINVITE.format(name))
					.command("/" + CommandAliases.baseCommandAliases.get(0) + " deinvite " + name);
			}
			sendFancyMessage(message);
			return;
		}

		if (you.getFaction() == myFaction) {
			this.sendMessage(Lang.COMMAND_DEINVITE_ALREADYMEMBER, you.getName(), this.myFaction.getTag());
			this.sendMessage(Lang.COMMAND_DEINVITE_MIGHTWANT, CmdFactionsKick.get().getUseageTemplate(false));
			return;
		}

		myFaction.uninvite(you);

		you.sendMessage(Lang.COMMAND_DEINVITE_REVOKED, fme.describeTo(you), myFaction.describeTo(you));

		myFaction.sendMessage(Lang.COMMAND_DEINVITE_REVOKES, fme.describeTo(myFaction), you.describeTo(myFaction));
	}

	@Override
	public String getUsageTranslation() {
		return Lang.COMMAND_DEINVITE_DESCRIPTION.toString();
	}

}
