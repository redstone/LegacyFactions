package net.redstoneore.legacyfactions.cmd;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.bukkit.Bukkit;

import net.redstoneore.legacyfactions.Factions;
import net.redstoneore.legacyfactions.Permission;
import net.redstoneore.legacyfactions.Role;
import net.redstoneore.legacyfactions.config.CommandAliases;
import net.redstoneore.legacyfactions.config.Config;
import net.redstoneore.legacyfactions.entity.Board;
import net.redstoneore.legacyfactions.entity.FPlayer;
import net.redstoneore.legacyfactions.entity.Faction;
import net.redstoneore.legacyfactions.entity.FactionColl;
import net.redstoneore.legacyfactions.event.EventFactionsLandChange;
import net.redstoneore.legacyfactions.event.EventFactionsLandChange.LandChangeCause;
import net.redstoneore.legacyfactions.integration.vault.VaultEngine;
import net.redstoneore.legacyfactions.lang.Lang;
import net.redstoneore.legacyfactions.locality.Locality;
import net.redstoneore.legacyfactions.task.NewSpiralTask;

public class CmdFactionsUnclaim extends FCommand {

	// -------------------------------------------------- //
	// INSTANCE
	// -------------------------------------------------- //
	
	private static CmdFactionsUnclaim instance = new CmdFactionsUnclaim();
	public static CmdFactionsUnclaim get() { return instance; }
	
	// -------------------------------------------------- //
	// CONSTRUCT
	// -------------------------------------------------- //

	private CmdFactionsUnclaim() {
		this.aliases.addAll(CommandAliases.cmdAliasesUnclaim);

		this.optionalArgs.put("radius", "1");
		this.optionalArgs.put("faction", "your");

		this.permission = Permission.UNCLAIM.getNode();
		this.disableOnLock = true;

		this.senderMustBePlayer = true;
		this.senderMustBeMember = false;
		this.senderMustBeModerator = false;
		this.senderMustBeAdmin = false;
	}

	// -------------------------------------------------- //
	// METHODS
	// -------------------------------------------------- //

	@Override
	public void perform() {
		// Read and validate input
		int radius = this.argAsInt(0, 1); // Default to 1
		
		if (radius < 1) {
			sendMessage(Lang.COMMAND_CLAIM_INVALIDRADIUS);
			return;
		}

		
		Map<Locality, Faction> transactions = new HashMap<>();
		final FPlayer fplayer = this.fme;

		if (radius < 2) {
			transactions.put(fplayer.getLastLocation(), FactionColl.get().getWilderness());
		} else {
			// radius claim
			if (!Permission.CLAIM_RADIUS.has(sender, false)) {
				sendMessage(Lang.COMMAND_CLAIM_DENIED);
				return;
			}
			
			// Okay, use NewSpiralTask to grab the chunks in async. 
			NewSpiralTask.of(this.fme.getLastLocation(), radius).then((localities, e) -> {	
				localities.forEach(locality -> 
					transactions.put(locality, FactionColl.get().getWilderness())
				);
				
				resume(fplayer, transactions, radius);
			});
			return;
		}
		
		resume(fplayer, transactions, 1);

	}
	
	protected static void resume(FPlayer fplayer, Map<Locality, Faction> transactions, int radius) {
		Locality target = fplayer.getLastLocation();
		Faction targetFaction = Board.get().getFactionAt(target);
		
		if (targetFaction.isSafeZone()) {
			if (Permission.MANAGE_SAFE_ZONE.has(fplayer.getPlayer())) {
				Board.get().removeAt(target);
				
				Lang.COMMAND_UNCLAIM_SAFEZONE_SUCCESS.getBuilder().parse().sendTo(fplayer);
				
				if (Config.logLandUnclaims) {
					Factions.get().log(Lang.COMMAND_UNCLAIM_LOG.format(fplayer.getName(), target.getCoordString(), targetFaction.getTag()));
				}
				return;
			} else {
				fplayer.sendMessage(Lang.COMMAND_UNCLAIM_SAFEZONE_NOPERM);
				return;
			}
		} else if (targetFaction.isWarZone()) {
			if (Permission.MANAGE_WAR_ZONE.has(fplayer.getPlayer())) {
				Board.get().removeAt(target);
				Lang.COMMAND_UNCLAIM_WARZONE_SUCCESS.getBuilder().parse().sendTo(fplayer);
				if (Config.logLandUnclaims) {
					Factions.get().log(Lang.COMMAND_UNCLAIM_LOG.format(fplayer.getName(), target.getCoordString(), targetFaction.getTag()));
				}
				return;
			} else {
				Lang.COMMAND_UNCLAIM_WARZONE_NOPERM.getBuilder().parse().sendTo(fplayer);
				return;
			}
		}
		
		if (fplayer.isAdminBypassing()) {
			EventFactionsLandChange event = new EventFactionsLandChange(fplayer, transactions, LandChangeCause.Unclaim);
			Bukkit.getServer().getPluginManager().callEvent(event);
			if (event.isCancelled()) return;
			
			event.transactions((locality, faction) -> Board.get().removeAt(locality));
			
			targetFaction.sendMessage(Lang.COMMAND_UNCLAIM_UNCLAIMED, fplayer.describeTo(targetFaction, true));
			fplayer.sendMessage(Lang.COMMAND_UNCLAIM_UNCLAIMS);

			if (Config.logLandUnclaims) {
				Factions.get().log(Lang.COMMAND_UNCLAIM_LOG.format(fplayer.getName(), target.getCoordString(), targetFaction.getTag()));
			}

			return;
		}

		if (!fplayer.hasFaction()) {
			Lang.COMMAND_ERRORS_NOTMEMBER.getBuilder().parse().sendTo(fplayer);
			return;
		}

		if (fplayer.getRole().isLessThan(Role.MODERATOR)) {
			Lang.COMMAND_ERRORS_YOUMUSTBE.getBuilder()
					.parse()
					.replace("<therole>", Role.MODERATOR.toNiceName())
					.replace("<theaction>", "unclaim").sendTo(fplayer);
			return;
		}
		
		if (fplayer.getFaction() != targetFaction) {
			Lang.COMMAND_UNCLAIM_WRONGFACTION.getBuilder().parse().sendTo(fplayer);
			return;
		}
		
		EventFactionsLandChange event = new EventFactionsLandChange(fplayer, transactions, LandChangeCause.Unclaim);
		Bukkit.getServer().getPluginManager().callEvent(event);
		if (event.isCancelled()) return;

		if (VaultEngine.getUtils().shouldBeUsed()) {
			double refund = VaultEngine.getUtils().calculateClaimRefund(fplayer.getFaction().getLandRounded());

			if (Config.bankEnabled && Config.bankFactionPaysLandCosts) {
				if (!VaultEngine.getUtils().modifyMoney(fplayer.getFaction(), refund, Lang.COMMAND_UNCLAIM_TOUNCLAIM.toString(), Lang.COMMAND_UNCLAIM_FORUNCLAIM.toString())) {
					return;
				}
			} else {
				if (!VaultEngine.getUtils().modifyMoney(fplayer, refund, Lang.COMMAND_UNCLAIM_TOUNCLAIM.toString(), Lang.COMMAND_UNCLAIM_FORUNCLAIM.toString())) {
					return;
				}
			}
		}
				
		if (event.transactions().size() < 2) {
			if (event.transactions().isEmpty()) {
				// Other plugin modified this and removed all transactions
				return;
			}
			
			Entry<Locality, Faction> entry = event.transactions().entrySet().stream().findFirst().get();
			
			Board.get().removeAt(entry.getKey());
			
			entry.getValue().sendMessage(Lang.COMMAND_UNCLAIM_FACTIONUNCLAIMED, fplayer.describeTo(entry.getValue(), true));
			
			if (Config.logLandUnclaims) {
				Factions.get().log(Lang.COMMAND_UNCLAIM_LOG.format(fplayer.getName(), entry.getKey().getCoordString(), entry.getValue().getTag()));
			}
			
			return;
		}
		// TODO: add option to click on the 
		List<String> locations = new ArrayList<>();
		
		event.transactions((locality, faction) -> {
			locations.add(locality.getChunkX() + "," + locality.getChunkZ());
			
			Board.get().removeAt(locality);
			
			if (Config.logLandUnclaims) {
				Factions.get().log(Lang.COMMAND_UNCLAIM_LOG.format(fplayer.getName(), locality.getCoordString(), faction.getTag()));
			}
		});
				
		if (fplayer.getFaction() != targetFaction) {
			Lang.COMMAND_UNCLAIM_FACTIONUNCLAIMEDAMOUNT.getBuilder()
				.parse()
				.replace("<amount>", locations.size())
				.replace("<radius>", radius)
				.replace("<chunk>", fplayer.getLastLocation().getCoordString())
				.sendTo(targetFaction)
				.sendTo(fplayer);
		} else {
			Lang.COMMAND_UNCLAIM_FACTIONUNCLAIMEDAMOUNT.getBuilder()
			.parse()
			.replace("<amount>", locations.size())
			.replace("<radius>", radius)
			.replace("<chunk>", fplayer.getLastLocation().getCoordString())
			.sendTo(targetFaction);
		}		
	}
	
	@Override
	public String getUsageTranslation() {
		return Lang.COMMAND_UNCLAIM_DESCRIPTION.toString();
	}

}
