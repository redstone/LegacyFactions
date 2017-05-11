package net.redstoneore.legacyfactions.cmd;

import java.util.StringJoiner;

import net.redstoneore.legacyfactions.Permission;
import net.redstoneore.legacyfactions.entity.Conf;
import net.redstoneore.legacyfactions.Lang;

public class CmdFactionsAnnounce extends FCommand {
	
	// -------------------------------------------------- //
	// CONSTRUCT
	// -------------------------------------------------- //
	
	public CmdFactionsAnnounce() {
		this.aliases.addAll(Conf.cmdAliasesAnnounce);
		
		this.requiredArgs.add("message");
		this.errorOnToManyArgs = false;
		
		this.permission = Permission.ANNOUNCE.node;
		this.disableOnLock = false;
		
		this.senderMustBePlayer = true;
		this.senderMustBeMember = true;
		this.senderMustBeModerator = true;
	}
	
	// -------------------------------------------------- //
	// METHODS
	// -------------------------------------------------- //

	@Override
	public void perform() {
		// Compile announcement
		StringJoiner messageJoiner = new StringJoiner(" ", "", "");
		this.args.forEach(messageJoiner::add);
		
		String message = messageJoiner.toString();
		
		String announcement = Lang.COMMAND_ANNOUNCE_TEMPLATE.toString()
				.replaceAll("<tag>", this.myFaction.getTag())
				.replaceAll("<player>", this.me.getName())
				.replaceAll("<message>", message);
		
		// Notify online players
		this.myFaction.getFPlayersWhereOnline(true).forEach(
			fplayer -> fplayer.msg(announcement)
		);
		
		// Store announcement for offline players
		this.myFaction.getFPlayersWhereOnline(false).forEach(
			fplayer -> this.myFaction.addAnnouncement(fplayer, announcement)
		);
	}

	@Override
	public String getUsageTranslation() {
		return Lang.COMMAND_ANNOUNCE_DESCRIPTION.toString();
	}

}
