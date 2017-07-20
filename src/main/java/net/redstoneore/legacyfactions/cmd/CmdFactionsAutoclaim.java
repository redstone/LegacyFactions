package net.redstoneore.legacyfactions.cmd;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.bukkit.Bukkit;

import net.redstoneore.legacyfactions.FLocation;
import net.redstoneore.legacyfactions.Permission;
import net.redstoneore.legacyfactions.Role;
import net.redstoneore.legacyfactions.Lang;
import net.redstoneore.legacyfactions.entity.Conf;
import net.redstoneore.legacyfactions.entity.Faction;
import net.redstoneore.legacyfactions.event.EventFactionsLandChange;
import net.redstoneore.legacyfactions.event.EventFactionsLandChange.LandChangeCause;

public class CmdFactionsAutoclaim extends FCommand {
	
	// -------------------------------------------------- //
	// CONSTRUCT
	// -------------------------------------------------- //
	
	public CmdFactionsAutoclaim() {
		this.aliases.addAll(Conf.cmdAliasesAutoclaim);

		this.optionalArgs.put("faction", "your");

		this.permission = Permission.AUTOCLAIM.getNode();
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
		Faction forFaction = this.argAsFaction(0, myFaction);
		if (forFaction == null || forFaction == fme.getAutoClaimFor()) {
			fme.setAutoClaimFor(null);
			msg(Lang.COMMAND_AUTOCLAIM_DISABLED);
			return;
		}

		if (!fme.canClaimForFaction(forFaction)) {
			if (myFaction == forFaction) {
				msg(Lang.COMMAND_AUTOCLAIM_REQUIREDRANK, Role.MODERATOR.getTranslation());
			} else {
				msg(Lang.COMMAND_AUTOCLAIM_OTHERFACTION, forFaction.describeTo(fme));
			}

			return;
		}

		fme.setAutoClaimFor(forFaction);

		msg(Lang.COMMAND_AUTOCLAIM_ENABLED, forFaction.describeTo(fme));
		
		Map<FLocation, Faction> transactions = new HashMap<>();

		transactions.put(FLocation.valueOf(me.getLocation()), forFaction);
	   
		EventFactionsLandChange event = new EventFactionsLandChange(fme, transactions, LandChangeCause.Claim);
		Bukkit.getServer().getPluginManager().callEvent(event);
		if (event.isCancelled()) return;
		
		for(Entry<FLocation, Faction> claimLocation : event.getTransactions().entrySet()) {
			if ( ! fme.attemptClaim(claimLocation.getValue(), claimLocation.getKey(), true, event)) {
				return;
			}
		}
	}

	@Override
	public String getUsageTranslation() {
		return Lang.COMMAND_AUTOCLAIM_DESCRIPTION.toString();
	}

}