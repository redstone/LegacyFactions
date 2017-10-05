package net.redstoneore.legacyfactions.cmd;

import org.bukkit.Bukkit;

import net.redstoneore.legacyfactions.Permission;
import net.redstoneore.legacyfactions.config.CommandAliases;
import net.redstoneore.legacyfactions.config.Config;
import net.redstoneore.legacyfactions.entity.FPlayer;
import net.redstoneore.legacyfactions.entity.FPlayerColl;
import net.redstoneore.legacyfactions.entity.Faction;
import net.redstoneore.legacyfactions.entity.FactionColl;
import net.redstoneore.legacyfactions.event.EventFactionsNameChange;
import net.redstoneore.legacyfactions.lang.Lang;
import net.redstoneore.legacyfactions.scoreboards.FTeamWrapper;
import net.redstoneore.legacyfactions.util.MiscUtil;

import java.util.ArrayList;

public class CmdFactionsTag extends FCommand {

	// -------------------------------------------------- //
	// INSTANCE
	// -------------------------------------------------- //
	
	private static CmdFactionsTag instance = new CmdFactionsTag();
	public static CmdFactionsTag get() { return instance; }
	
	// -------------------------------------------------- //
	// CONSTRUCT
	// -------------------------------------------------- //

	private CmdFactionsTag() {
		this.aliases.addAll(CommandAliases.cmdAliasesTag);

		this.requiredArgs.add("faction tag");
		
		this.permission = Permission.TAG.getNode();
		this.disableOnLock = true;

		this.senderMustBePlayer = true;
		this.senderMustBeMember = false;
		this.senderMustBeModerator = true;
		this.senderMustBeAdmin = false;
	}

	// -------------------------------------------------- //
	// METHODS
	// -------------------------------------------------- //

	@Override
	public void perform() {
		String tag = this.argAsString(0);

		// TODO does not first test cover selfcase?
		if (FactionColl.get().isTagTaken(tag) && !MiscUtil.getComparisonString(tag).equals(myFaction.getComparisonTag())) {
			sendMessage(Lang.COMMAND_TAG_TAKEN);
			return;
		}

		ArrayList<String> errors = MiscUtil.validateTag(tag);
		if (errors.size() > 0) {
			sendMessage(errors);
			return;
		}

		// if economy is enabled, they're not on the bypass list, and this command has a cost set, make sure they can pay
		if (!canAffordCommand(Config.econCostTag, Lang.COMMAND_TAG_TOCHANGE.toString())) {
			return;
		}

		// trigger the faction rename event (cancellable)
		EventFactionsNameChange renameEvent = new EventFactionsNameChange(fme, tag);
		Bukkit.getServer().getPluginManager().callEvent(renameEvent);
		if (renameEvent.isCancelled()) {
			return;
		}

		// then make 'em pay (if applicable)
		if (!payForCommand(Config.econCostTag, Lang.COMMAND_TAG_TOCHANGE, Lang.COMMAND_TAG_FORCHANGE)) {
			return;
		}

		String oldtag = myFaction.getTag();
		myFaction.setTag(tag);

		// Inform
		for (FPlayer fplayer : FPlayerColl.all(true)) {
			if (fplayer.getFactionId().equals(myFaction.getId())) {
				fplayer.sendMessage(Lang.COMMAND_TAG_FACTION, fme.describeTo(myFaction, true), myFaction.getTag(myFaction));
				continue;
			}

			// Broadcast the tag change (if applicable)
			if (Config.broadcastTagChanges) {
				Faction faction = fplayer.getFaction();
				fplayer.sendMessage(Lang.COMMAND_TAG_CHANGED, fme.getColorTo(faction) + oldtag, myFaction.getTag(faction));
			}
		}

		FTeamWrapper.updatePrefixes(myFaction);
	}

	@Override
	public String getUsageTranslation() {
		return Lang.COMMAND_TAG_DESCRIPTION.toString();
	}

}
