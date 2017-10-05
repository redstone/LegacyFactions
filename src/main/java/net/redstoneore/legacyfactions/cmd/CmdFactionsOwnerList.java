package net.redstoneore.legacyfactions.cmd;

import net.redstoneore.legacyfactions.Permission;
import net.redstoneore.legacyfactions.config.CommandAliases;
import net.redstoneore.legacyfactions.config.Config;
import net.redstoneore.legacyfactions.entity.Board;
import net.redstoneore.legacyfactions.lang.Lang;
import net.redstoneore.legacyfactions.locality.Locality;
import net.redstoneore.legacyfactions.util.StringUtils;


public class CmdFactionsOwnerList extends FCommand {

	// -------------------------------------------------- //
	// INSTANCE
	// -------------------------------------------------- //
	
	private static CmdFactionsOwnerList instance = new CmdFactionsOwnerList();
	public static CmdFactionsOwnerList get() { return instance; }
	
	// -------------------------------------------------- //
	// CONSTRUCT
	// -------------------------------------------------- //

	private CmdFactionsOwnerList() {
		this.aliases.addAll(CommandAliases.cmdAliasesOwnerList);

		//this.requiredArgs.add("");
		//this.optionalArgs.put("", "");

		this.permission = Permission.OWNERLIST.getNode();
		this.disableOnLock = false;

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
		boolean hasBypass = fme.isAdminBypassing();

		if (!hasBypass && !assertHasFaction()) {
			return;
		}

		if (!Config.ownedAreasEnabled) {
			fme.sendMessage(Lang.COMMAND_OWNERLIST_DISABLED);
			return;
		}

		Locality locality = Locality.of(fme);

		if (Board.get().getFactionAt(locality) != myFaction) {
			if (!hasBypass) {
				fme.sendMessage(Lang.COMMAND_OWNERLIST_WRONGFACTION);
				return;
			}
			
			myFaction = Board.get().getFactionAt(locality);
			if (!myFaction.isNormal()) {
				fme.sendMessage(Lang.COMMAND_OWNERLIST_NOTCLAIMED);
				return;
			}
		}

		String owners =  StringUtils.join(this.myFaction.ownership().getOwners(locality));

		if (owners == null || owners.isEmpty()) {
			fme.sendMessage(Lang.COMMAND_OWNERLIST_NONE);
			return;
		}

		fme.sendMessage(Lang.COMMAND_OWNERLIST_OWNERS, owners);
	}

	@Override
	public String getUsageTranslation() {
		return Lang.COMMAND_OWNERLIST_DESCRIPTION.toString();
	}
}
