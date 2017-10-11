package net.redstoneore.legacyfactions.cmd;

import org.bukkit.Bukkit;

import net.redstoneore.legacyfactions.Factions;
import net.redstoneore.legacyfactions.Permission;
import net.redstoneore.legacyfactions.config.CommandAliases;
import net.redstoneore.legacyfactions.config.Config;
import net.redstoneore.legacyfactions.entity.FPlayer;
import net.redstoneore.legacyfactions.entity.FactionColl;
import net.redstoneore.legacyfactions.event.EventFactionsBan;
import net.redstoneore.legacyfactions.event.EventFactionsChange;
import net.redstoneore.legacyfactions.event.EventFactionsChange.ChangeReason;
import net.redstoneore.legacyfactions.lang.Lang;
import net.redstoneore.legacyfactions.util.TextUtil;

public class CmdFactionsBan extends FCommand {
	
	// -------------------------------------------------- //
	// INSTANCE
	// -------------------------------------------------- //
	
	private static CmdFactionsBan instance = new CmdFactionsBan();
	public static CmdFactionsBan get() { return instance; }
	
	// -------------------------------------------------- //
	// CONSTRUCT
	// -------------------------------------------------- //

	private CmdFactionsBan() {
		this.aliases.addAll(CommandAliases.cmdAliasesBan);

		this.requiredArgs.add("player name");

		this.permission = Permission.BAN.getNode();
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
		FPlayer who = this.argAsBestFPlayerMatch(0, null, true);
		
		if (who == null) {
			final FPlayer fplayer = this.fme;
			final String fplayerName = this.argAsString(0);
			
			this.argAsPlayerToMojangFPlayer(0, null, (found, exception) -> {
				if (exception.isPresent() || found == null) {
					Lang.COMMAND_BAN_NOTFOUND.getBuilder()
						.parse()
						.replace("<player>", fplayerName)
						.sendTo(fplayer);
					
					return;
				}
				
				resume(who, fplayer);
			});
			return;
		}
		
		resume(who, this.fme);
	}
	
	private static final void resume(final FPlayer who, final FPlayer fme) {
		// Can't ban someone of a higher rank.
		if (who.getRole().isMoreThan(fme.getRole()) && !fme.isAdminBypassing()) {
			String message = Lang.COMMAND_BAN_CANT.toString();
			message = message.replace("<your-rank>", fme.getRole().toNiceName());
			message = message.replace("<their-rank>", who.getRole().toNiceName());
			message = message.replace("<name>", who.getName());
			
			fme.sendMessage(TextUtil.parseColor(message));
			return;
		}
		
		// We can't kick ourselves.
		if (who == fme) {
			fme.sendMessage(TextUtil.parseColor(Lang.COMMAND_BAN_CANTYOURSELF.toString()));
			return;
		}
		
		// Can they afford this command?
		if (!fme.canAffordCommand(Config.econCostBan, Lang.COMMAND_BAN_TOBAN.toString())) {
			return;
		}
		
		// Make them pay for the ban.
		if (!fme.payForCommand(Config.econCostBan, Lang.COMMAND_BAN_TOBAN.toString(), Lang.COMMAND_BAN_FORBAN.toString())) {
			return;
		}
			
		// Handle ban event 
		EventFactionsBan banEvent = new EventFactionsBan(who.getFaction(), who, fme).call();
		if (banEvent.isCancelled()) return;
		
		// Now leave 
		if (who.getFaction() == fme.getFaction()) {
			// Handle the change faction event
			EventFactionsChange changeEvent = new EventFactionsChange(who, who.getFaction(), FactionColl.get().getWilderness(), true, ChangeReason.BANNED);
			Bukkit.getServer().getPluginManager().callEvent(changeEvent);
			if (changeEvent.isCancelled()) return;
			
			// Leave as they are in the same faction
			who.leave(false);
		}
		
		// Ban them
		fme.getFaction().ban(who);
		
		String messageMe = Lang.COMMAND_BAN_YOUBANKICKED.toString();
		messageMe = messageMe.replace("<name>", who.getName());
		messageMe = TextUtil.parseColor(messageMe);
			
		fme.sendMessage(messageMe);
			
		final String messageAll = TextUtil.parseColor(Lang.COMMAND_BAN_SOMEONEBANKICKED.toString()
				.replace("<someone>", fme.getName())
				.replace("<name>", who.getName()));
			
		fme.getFaction().getMembers().forEach(member -> {
			if (member.getId() == fme.getId()) return;
			if (member.isOffline()) return;
			member.sendMessage(messageAll);
		});
		
		if (Config.logFactionBan) {
			Factions.get().log(fme.getName() + " banned " + who.getName() + " from the faction: " + who.getTag());
		}
	}
	
	@Override
	public String getUsageTranslation() {
		return Lang.COMMAND_BAN_DESCRIPTION.toString();
	}
}
