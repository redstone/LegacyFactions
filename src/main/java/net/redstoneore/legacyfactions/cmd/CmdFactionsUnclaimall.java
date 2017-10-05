package net.redstoneore.legacyfactions.cmd;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.Bukkit;

import net.redstoneore.legacyfactions.Factions;
import net.redstoneore.legacyfactions.Permission;
import net.redstoneore.legacyfactions.config.CommandAliases;
import net.redstoneore.legacyfactions.config.Config;
import net.redstoneore.legacyfactions.entity.Board;
import net.redstoneore.legacyfactions.entity.Faction;
import net.redstoneore.legacyfactions.entity.FactionColl;
import net.redstoneore.legacyfactions.event.EventFactionsLandChange;
import net.redstoneore.legacyfactions.event.EventFactionsLandChange.LandChangeCause;
import net.redstoneore.legacyfactions.integration.vault.VaultEngine;
import net.redstoneore.legacyfactions.lang.Lang;
import net.redstoneore.legacyfactions.locality.Locality;

public class CmdFactionsUnclaimall extends FCommand {

	// -------------------------------------------------- //
	// INSTANCE
	// -------------------------------------------------- //
	
	private static CmdFactionsUnclaimall instance = new CmdFactionsUnclaimall();
	public static CmdFactionsUnclaimall get() { return instance; }
	
	// -------------------------------------------------- //
	// CONSTRUCT
	// -------------------------------------------------- //

	private CmdFactionsUnclaimall() {
		this.aliases.addAll(CommandAliases.cmdAliasesUnclaimAll);
		
		this.permission = Permission.UNCLAIM_ALL.getNode();
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
		if (VaultEngine.getUtils().shouldBeUsed()) {
			double refund = VaultEngine.getUtils().calculateTotalLandRefund(this.myFaction.getLandRounded());
			if (Config.bankEnabled && Config.bankFactionPaysLandCosts) {
				if (!VaultEngine.getUtils().modifyMoney(myFaction, refund, Lang.COMMAND_UNCLAIMALL_TOUNCLAIM.toString(), Lang.COMMAND_UNCLAIMALL_FORUNCLAIM.toString())) {
					return;
				}
			} else {
				if (!VaultEngine.getUtils().modifyMoney(fme, refund, Lang.COMMAND_UNCLAIMALL_TOUNCLAIM.toString(), Lang.COMMAND_UNCLAIMALL_FORUNCLAIM.toString())) {
					return;
				}
			}
		}

		Map<Locality, Faction> transactions = new HashMap<Locality, Faction>();
		
		this.myFaction.getClaims().forEach(location -> {
			transactions.put(Locality.of(location.getChunk()), FactionColl.get().getWilderness());
		});
		
		EventFactionsLandChange event = new EventFactionsLandChange(this.fme, transactions, LandChangeCause.Unclaim);
		Bukkit.getServer().getPluginManager().callEvent(event);
		if (event.isCancelled()) return;
		
		event.transactions((location, faction) -> Board.get().removeAt(location));
		
		this.myFaction.sendMessage(Lang.COMMAND_UNCLAIMALL_UNCLAIMED, this.fme.describeTo(this.myFaction, true));
		
		if (Config.logLandUnclaims) {
			Factions.get().log(Lang.COMMAND_UNCLAIMALL_LOG.format(fme.getName(), myFaction.getTag()));
		}
	}

	@Override
	public String getUsageTranslation() {
		return Lang.COMMAND_UNCLAIMALL_DESCRIPTION.toString();
	}

}
