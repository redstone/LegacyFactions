package net.redstoneore.legacyfactions.cmd;

import net.redstoneore.legacyfactions.Permission;
import net.redstoneore.legacyfactions.config.CommandAliases;
import net.redstoneore.legacyfactions.config.Config;
import net.redstoneore.legacyfactions.entity.FPlayerColl;
import net.redstoneore.legacyfactions.lang.Lang;
import net.redstoneore.legacyfactions.util.TextUtil;

public class CmdFactionsDescription extends FCommand {

	// -------------------------------------------------- //
	// INSTANCE
	// -------------------------------------------------- //
	
	private static CmdFactionsDescription instance = new CmdFactionsDescription();
	public static CmdFactionsDescription get() { return instance; }
	
	// -------------------------------------------------- //
	// CONSTRUCT
	// -------------------------------------------------- //

	private CmdFactionsDescription() {
		this.aliases.addAll(CommandAliases.cmdAliasesDescription);

		this.requiredArgs.add("desc");
		this.errorOnToManyArgs = false;

		this.permission = Permission.DESCRIPTION.getNode();
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
		// if economy is enabled, they're not on the bypass list, and this command has a cost set, make 'em pay
		if (!payForCommand(Config.econCostDesc, Lang.COMMAND_DESCRIPTION_TOCHANGE, Lang.COMMAND_DESCRIPTION_FORCHANGE)) {
			return;
		}
		
		String newDescription;

		// Replace all the % because it messes with string formatting and this is easy way around that.
		if (Config.allowColourCodesInFactionDescription) {
			newDescription = (TextUtil.implode(this.args, " ").replaceAll("%", ""));
		} else {
			newDescription = (TextUtil.implode(this.args, " ").replaceAll("%", "").replaceAll("(&([a-f0-9klmnor]))", "& $2"));
		}
		
		// Check the max length
		if (Config.factionDescriptionLengthMax > -1) {
			if (newDescription.length() > Config.factionDescriptionLengthMax) {
				Lang.COMMAND_DESCRIPTION_TOOLONG.getBuilder()
					.parse()
					.replace("<max-length>", Config.factionDescriptionLengthMax)
					.replace("<length>", newDescription.length())
					.sendTo(this.fme);
				
				return;
			}
		}
		
		this.myFaction.setDescription(newDescription);
		
		if (!Config.broadcastDescriptionChanges) {
			this.fme.sendMessage(Lang.COMMAND_DESCRIPTION_CHANGED, this.myFaction.describeTo(this.fme));
			this.fme.sendMessage(this.myFaction.getDescription());
			return;
		}

		// Broadcast the description to everyone
		FPlayerColl.all(true).forEach(fplayer -> {
			fplayer.sendMessage(Lang.COMMAND_DESCRIPTION_CHANGES, this.myFaction.describeTo(fplayer));
			fplayer.sendMessage(myFaction.getDescription());  // players can inject "&" or "`" or "<i>" or whatever in their description; &k is particularly interesting looking
		});
	}

	@Override
	public String getUsageTranslation() {
		return Lang.COMMAND_DESCRIPTION_DESCRIPTION.toString();
	}

}
