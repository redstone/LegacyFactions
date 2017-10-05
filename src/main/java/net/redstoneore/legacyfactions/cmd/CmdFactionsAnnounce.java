package net.redstoneore.legacyfactions.cmd;

import java.util.StringJoiner;

import net.redstoneore.legacyfactions.Permission;
import net.redstoneore.legacyfactions.config.CommandAliases;
import net.redstoneore.legacyfactions.lang.Lang;

public class CmdFactionsAnnounce extends FCommand {
	
	// -------------------------------------------------- //
	// INSTANCE
	// -------------------------------------------------- //
	
	private static CmdFactionsAnnounce instance = new CmdFactionsAnnounce();
	public static CmdFactionsAnnounce get() { return instance; }
	
	// -------------------------------------------------- //
	// CONSTRUCT
	// -------------------------------------------------- //
	
	private CmdFactionsAnnounce() {
		this.aliases.addAll(CommandAliases.cmdAliasesAnnounce);
		
		this.requiredArgs.add("message");
		this.errorOnToManyArgs = false;
		
		this.permission = Permission.ANNOUNCE.getNode();
		this.disableOnLock = false;
		
		this.senderMustBePlayer = true;
		this.senderMustBeMember = true;
		this.senderMustBeModerator = true;
		this.senderMustBeColeader = false;
		this.senderMustBeAdmin = false;
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
		this.myFaction.getWhereOnline(true).forEach(
			fplayer -> fplayer.sendMessage(announcement)
		);
		
		// Store announcement for offline players
		this.myFaction.getWhereOnline(false).forEach(
			fplayer -> this.myFaction.announcements().add(fplayer, announcement)
		);
	}

	@Override
	public String getUsageTranslation() {
		return Lang.COMMAND_ANNOUNCE_DESCRIPTION.toString();
	}

}
