package net.redstoneore.legacyfactions.cmd;

import net.redstoneore.legacyfactions.Permission;
import net.redstoneore.legacyfactions.config.CommandAliases;
import net.redstoneore.legacyfactions.config.Config;
import net.redstoneore.legacyfactions.entity.Board;
import net.redstoneore.legacyfactions.lang.Lang;


public class CmdFactionsMap extends FCommand {

	// -------------------------------------------------- //
	// INSTANCE
	// -------------------------------------------------- //
	
	private static CmdFactionsMap instance = new CmdFactionsMap();
	public static CmdFactionsMap get() { return instance; }
	
	// -------------------------------------------------- //
	// CONSTRUCT
	// -------------------------------------------------- //

	private CmdFactionsMap() {
		this.aliases.addAll(CommandAliases.cmdAliasesMap);

		this.optionalArgs.put("on/off", "once");

		this.permission = Permission.MAP.getNode();
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
		if (this.argIsSet(0)) {
			if (this.argAsBool(0, !fme.isMapAutoUpdating())) {
				// Turn on

				// if economy is enabled, they're not on the bypass list, and this command has a cost set, make 'em pay
				if (!payForCommand(Config.econCostMap, "to show the map", "for showing the map")) {
					return;
				}

				this.fme.setMapAutoUpdating(true);
				this.sendMessage(Lang.COMMAND_MAP_UPDATE_ENABLED);

				// And show the map once
				this.showMap();
			} else {
				// Turn off
				this.fme.setMapAutoUpdating(false);
				this.sendMessage(Lang.COMMAND_MAP_UPDATE_DISABLED);
			}
		} else {
			// if economy is enabled, they're not on the bypass list, and this command has a cost set, make 'em pay
			if (!payForCommand(Config.econCostMap, Lang.COMMAND_MAP_TOSHOW, Lang.COMMAND_MAP_FORSHOW)) {
				return;
			}

			this.showMap();
		}
	}

	public void showMap() {
		this.sendMessage(Board.get().getMap(this.myFaction, this.fme.getLastLocation(), this.fme.getLastLocation().getYaw()));
	}

	@Override
	public String getUsageTranslation() {
		return Lang.COMMAND_MAP_DESCRIPTION.toString();
	}

}
