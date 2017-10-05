package net.redstoneore.legacyfactions.cmd;

import java.util.ArrayList;

import net.redstoneore.legacyfactions.Factions;
import net.redstoneore.legacyfactions.Permission;
import net.redstoneore.legacyfactions.Role;
import net.redstoneore.legacyfactions.config.CommandAliases;
import net.redstoneore.legacyfactions.config.Config;
import net.redstoneore.legacyfactions.entity.FPlayer;
import net.redstoneore.legacyfactions.entity.FPlayerColl;
import net.redstoneore.legacyfactions.entity.Faction;
import net.redstoneore.legacyfactions.entity.FactionColl;
import net.redstoneore.legacyfactions.event.EventFactionsChange;
import net.redstoneore.legacyfactions.event.EventFactionsCreate;
import net.redstoneore.legacyfactions.event.EventFactionsChange.ChangeReason;
import net.redstoneore.legacyfactions.lang.Lang;
import net.redstoneore.legacyfactions.util.MiscUtil;

import org.bukkit.Bukkit;

public class CmdFactionsCreate extends FCommand {

	// -------------------------------------------------- //
	// INSTANCE
	// -------------------------------------------------- //
	
	private static CmdFactionsCreate instance = new CmdFactionsCreate();
	public static CmdFactionsCreate get() { return instance; }
	
	// -------------------------------------------------- //
	// CONSTRUCT
	// -------------------------------------------------- //

	private CmdFactionsCreate() {
		this.aliases.addAll(CommandAliases.cmdAliasesCreate);

		this.requiredArgs.add("faction tag");

		this.permission = Permission.CREATE.getNode();
		this.disableOnLock = true;

		this.senderMustBePlayer = true;
		this.senderMustBeMember = false;
		this.senderMustBeModerator = false;
		this.senderMustBeColeader = false;
		this.senderMustBeAdmin = false;
	}

	// -------------------------------------------------- //
	// METHODS
	// -------------------------------------------------- //

	@Override
	public void perform() {
		String tag = this.argAsString(0);

		if (fme.hasFaction()) {
			sendMessage(Lang.COMMAND_CREATE_MUSTLEAVE);
			return;
		}

		if (FactionColl.get().isTagTaken(tag)) {
			sendMessage(Lang.COMMAND_CREATE_INUSE);
			return;
		}

		ArrayList<String> tagValidationErrors = MiscUtil.validateTag(tag);
		if (tagValidationErrors.size() > 0) {
			sendMessage(tagValidationErrors);
			return;
		}

		// if economy is enabled, they're not on the bypass list, and this command has a cost set, make sure they can pay
		if (!canAffordCommand(Config.econCostCreate, Lang.COMMAND_CREATE_TOCREATE.toString())) {
			return;
		}

		// trigger the faction creation event (cancellable)
		EventFactionsCreate createEvent = new EventFactionsCreate(me, tag);
		Bukkit.getServer().getPluginManager().callEvent(createEvent);
		if (createEvent.isCancelled()) {
			return;
		}
		// update here incase it was changed
		tag = createEvent.getFactionTag();

		// then make 'em pay (if applicable)
		if (!payForCommand(Config.econCostCreate, Lang.COMMAND_CREATE_TOCREATE, Lang.COMMAND_CREATE_FORCREATE)) {
			return;
		}

		Faction faction = FactionColl.get().createFaction();

		if (faction == null) {
			sendMessage(Lang.COMMAND_CREATE_ERROR);
			return;
		}

		// finish setting up the Faction
		faction.setTag(tag);

		// trigger the faction join event for the creator
		EventFactionsChange event = new EventFactionsChange(fme, fme.getFaction(), faction, false, ChangeReason.CREATE);
		
		Bukkit.getServer().getPluginManager().callEvent(event);
		// join event cannot be cancelled or you'll have an empty faction

		// finish setting up the FPlayer
		fme.setRole(Role.ADMIN);
		fme.setFaction(faction);

		for (FPlayer follower : FPlayerColl.all(true)) {
			follower.sendMessage(Lang.COMMAND_CREATE_CREATED, fme.describeTo(follower, true), faction.getTag(follower));
		}

		this.sendMessage(Lang.COMMAND_CREATE_YOUSHOULD, CmdFactionsDescription.get().getUseageTemplate());

		if (Config.logFactionCreate) {
			Factions.get().log(fme.getName() + Lang.COMMAND_CREATE_CREATEDLOG.toString() + tag);
		}
	}

	@Override
	public String getUsageTranslation() {
		return Lang.COMMAND_CREATE_DESCRIPTION.toString();
	}

}
