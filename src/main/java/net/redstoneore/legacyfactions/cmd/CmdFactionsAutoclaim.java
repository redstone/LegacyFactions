package net.redstoneore.legacyfactions.cmd;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.Bukkit;

import net.redstoneore.legacyfactions.Permission;
import net.redstoneore.legacyfactions.Role;
import net.redstoneore.legacyfactions.config.CommandAliases;
import net.redstoneore.legacyfactions.entity.Faction;
import net.redstoneore.legacyfactions.event.EventFactionsLandChange;
import net.redstoneore.legacyfactions.event.EventFactionsLandChange.LandChangeCause;
import net.redstoneore.legacyfactions.lang.Lang;
import net.redstoneore.legacyfactions.locality.Locality;

public class CmdFactionsAutoclaim extends FCommand {
	
	// -------------------------------------------------- //
	// INSTANCE
	// -------------------------------------------------- //
	
	private static CmdFactionsAutoclaim instance = new CmdFactionsAutoclaim();
	public static CmdFactionsAutoclaim get() { return instance; }
	
	// -------------------------------------------------- //
	// CONSTRUCT
	// -------------------------------------------------- //
	
	private CmdFactionsAutoclaim() {
		this.aliases.addAll(CommandAliases.cmdAliasesAutoclaim);

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
			sendMessage(Lang.COMMAND_AUTOCLAIM_DISABLED);
			return;
		}

		if (!fme.canClaimForFaction(forFaction)) {
			if (myFaction == forFaction) {
				sendMessage(Lang.COMMAND_AUTOCLAIM_REQUIREDRANK, Role.MODERATOR.getTranslation());
			} else {
				sendMessage(Lang.COMMAND_AUTOCLAIM_OTHERFACTION, forFaction.describeTo(fme));
			}

			return;
		}

		fme.setAutoClaimFor(forFaction);

		this.sendMessage(Lang.COMMAND_AUTOCLAIM_ENABLED, forFaction.describeTo(fme));
		
		Map<Locality, Faction> transactions = new HashMap<>();

		transactions.put(fme.getLastLocation(), forFaction);
	   
		EventFactionsLandChange event = new EventFactionsLandChange(fme, transactions, LandChangeCause.Claim);
		Bukkit.getServer().getPluginManager().callEvent(event);
		if (event.isCancelled()) return;
		
		CmdFactionsClaim.resume(fme, transactions, forFaction, 1);
	}

	@Override
	public String getUsageTranslation() {
		return Lang.COMMAND_AUTOCLAIM_DESCRIPTION.toString();
	}

}