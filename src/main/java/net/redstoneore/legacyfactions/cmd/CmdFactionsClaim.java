package net.redstoneore.legacyfactions.cmd;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.bukkit.Bukkit;

import net.redstoneore.legacyfactions.Permission;
import net.redstoneore.legacyfactions.config.CommandAliases;
import net.redstoneore.legacyfactions.config.Config;
import net.redstoneore.legacyfactions.entity.FPlayer;
import net.redstoneore.legacyfactions.entity.Faction;
import net.redstoneore.legacyfactions.event.EventFactionsLandChange;
import net.redstoneore.legacyfactions.event.EventFactionsLandChange.LandChangeCause;
import net.redstoneore.legacyfactions.lang.Lang;
import net.redstoneore.legacyfactions.locality.Locality;
import net.redstoneore.legacyfactions.task.NewSpiralTask;


public class CmdFactionsClaim extends FCommand {

	// -------------------------------------------------- //
	// INSTANCE
	// -------------------------------------------------- //
	
	private static CmdFactionsClaim instance = new CmdFactionsClaim();
	public static CmdFactionsClaim get() { return instance; }
	
	// -------------------------------------------------- //
	// CONSTRUCT
	// -------------------------------------------------- //

	private CmdFactionsClaim() {
		this.aliases.addAll(CommandAliases.cmdAliasesClaim);

		this.optionalArgs.put("radius", "1");
		this.optionalArgs.put("faction", "your");

		this.permission = Permission.CLAIM.getNode();
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
		// Read and validate input
		final int radius = this.argAsInt(0, 1); // Default to 1
		final Faction forFaction = this.argAsFaction(1, this.myFaction);

		if (radius < 1) {
			Lang.COMMAND_CLAIM_INVALIDRADIUS.getBuilder()
				.parse()
				.sendTo(this.fme);
			
			return;
		}
		
		if (radius > Config.maxClaimRadius) {
			Lang.COMMAND_CLAIM_RADIUSMAX.getBuilder()
				.parse()
				.replace("<radius>", Config.maxClaimRadius)
				.sendTo(sender);
			return;
		}
		
		Map<Locality, Faction> transactions = new HashMap<Locality, Faction>();
		final FPlayer fplayer = this.fme;
		
		if (radius < 2) {
			// single chunk
			transactions.put(Locality.of(me.getLocation()), forFaction);
		   
			resume(fplayer, transactions, forFaction, radius);
			return;
		} 
				
		// Claiming in a radius, ensure they have permission.
		if (!Permission.CLAIM_RADIUS.has(this.sender, false)) {
			Lang.COMMAND_CLAIM_DENIED.getBuilder()
				.parse()
				.sendTo(fplayer);
			return;
		}
		
		// Okay, use NewSpiralTask to grab the chunks in async. 
		NewSpiralTask.of(this.fme.getLastLocation(), radius).then((localities, e) -> {	
			localities.forEach(locality -> 
				transactions.put(locality, forFaction)
			);
			
			resume(fplayer, transactions, forFaction, radius);
		});
	}

	@Override
	public String getUsageTranslation() {
		return Lang.COMMAND_CLAIM_DESCRIPTION.toString();
	}

	protected static void resume(FPlayer fplayer, Map<Locality, Faction> transactions, Faction forFaction, int radius) {
		EventFactionsLandChange event = new EventFactionsLandChange(fplayer, transactions, LandChangeCause.Claim);
		Bukkit.getServer().getPluginManager().callEvent(event);
		if (event.isCancelled()) return;
		
		int successClaims = 0;
		
		for (Entry<Locality, Faction> claimLocation : event.transactions().entrySet()) {
			if (!fplayer.attemptClaim(claimLocation.getValue(), claimLocation.getKey(), true, false)) {
				break;
			}
			successClaims++;
		}
		
		if (successClaims == 0) return;
		
		if (radius == 1) {
			if (forFaction == fplayer.getFaction()) {
				forFaction.sendMessage(Lang.CLAIM_CLAIMED, forFaction.describeTo(fplayer, true), forFaction.describeTo(fplayer), forFaction.describeTo(fplayer));				
			} else {
				forFaction.sendMessage(Lang.CLAIM_CLAIMED, forFaction.describeTo(fplayer, true), forFaction.describeTo(fplayer), forFaction.describeTo(fplayer));
				fplayer.sendMessage(Lang.CLAIM_CLAIMED, fplayer.describeTo(fplayer, true), fplayer.describeTo(fplayer), fplayer.describeTo(fplayer));
			}
		} else {
			if (forFaction == fplayer.getFaction()) {
				Lang.COMMAND_CLAIM_RADIUSAMOUNT.getBuilder()
					.parse()
					.replace("<amount>", successClaims)
					.replace("<radius>", radius)
					.replace("<player>", fplayer.getName())
					.replace("<chunk>", fplayer.getLastLocation().getCoordString())
					.sendTo(forFaction);	
			} else {
				Lang.COMMAND_CLAIM_RADIUSAMOUNT.getBuilder()
					.parse()
					.replace("<amount>", successClaims)
					.replace("<radius>", radius)
					.replace("<player>", fplayer.getName())
					.replace("<chunk>", fplayer.getLastLocation().getCoordString())
					.sendTo(forFaction)
					.sendTo(fplayer);
			}
		}
	}
	
}
