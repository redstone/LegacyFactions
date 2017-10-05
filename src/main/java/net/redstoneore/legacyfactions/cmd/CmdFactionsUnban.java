package net.redstoneore.legacyfactions.cmd;

import net.redstoneore.legacyfactions.Factions;
import net.redstoneore.legacyfactions.Permission;
import net.redstoneore.legacyfactions.config.CommandAliases;
import net.redstoneore.legacyfactions.config.Config;
import net.redstoneore.legacyfactions.entity.FPlayer;
import net.redstoneore.legacyfactions.event.EventFactionsUnban;
import net.redstoneore.legacyfactions.lang.Lang;
import net.redstoneore.legacyfactions.util.TextUtil;

public class CmdFactionsUnban extends FCommand {
	
	// -------------------------------------------------- //
	// INSTANCE
	// -------------------------------------------------- //
	
	private static CmdFactionsUnban instance = new CmdFactionsUnban();
	public static CmdFactionsUnban get() { return instance; }
	
	// -------------------------------------------------- //
	// CONSTRUCT
	// -------------------------------------------------- //

	private CmdFactionsUnban() {
		this.aliases.addAll(CommandAliases.cmdAliasesUnban);

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
				if (exception.isPresent()) {
					fplayer.sendMessage(TextUtil.parseColor(Lang.COMMAND_UNBAN_NOTFOUND.toString()).replace("<player>", fplayerName));
					return;
				}
				
				if (found == null) {
					fplayer.sendMessage(TextUtil.parseColor(Lang.COMMAND_UNBAN_NOTFOUND.toString()).replace("<player>", fplayerName));
					return;
				}
				
				resume(who, fplayer);
			});
			return;
		}
		
		resume(who, this.fme);
	}

	private static final void resume(final FPlayer who, final FPlayer fme) {
		// See if they are banned.
		if (!fme.getFaction().isBanned(who)) {
			fme.sendMessage(Lang.COMMAND_UNBAN_NOTBANNED.toString().replace("<player>", who.getName()));
			return;
		}
		
		// Can they afford this command?
		if (!fme.canAffordCommand(Config.econCostUnban, Lang.COMMAND_UNBAN_TOUNBAN.toString())) {
			return;
		}
		
		// Make them pay for the unban.
		if (!fme.payForCommand(Config.econCostUnban, Lang.COMMAND_UNBAN_TOUNBAN.toString(), Lang.COMMAND_UNBAN_FORUNBAN.toString())) {
			return;
		}
		
		// Handle unban event 
		EventFactionsUnban unbanEvent = new EventFactionsUnban(who.getFaction(), who, fme).call();
		if (unbanEvent.isCancelled()) return;
		
		// Ban them
		fme.getFaction().unban(who);
		
		String messageMe = Lang.COMMAND_UNBAN_YOUUNBAN.toString();
		messageMe = messageMe.replace("<name>", who.getName());
		messageMe = TextUtil.parseColor(messageMe);
			
		fme.sendMessage(messageMe);
			
		final String messageAll = TextUtil.parseColor(Lang.COMMAND_UNBAN_SOMEONEUNBAN.toString()
				.replace("<someone>", fme.getName())
				.replace("<name>", who.getName()));
			
		fme.getFaction().getMembers().forEach(member -> {
			if (member.getId() == fme.getId()) return;
			if (member.isOffline()) return;
			member.sendMessage(messageAll);
		});
		
		if (Config.logFactionBan) {
			Factions.get().log(fme.getName() + " unbanned " + who.getName() + " from the faction: " + who.getTag());
		}
	}
	
	@Override
	public String getUsageTranslation() {
		return Lang.COMMAND_BAN_DESCRIPTION.toString();
	}
}
