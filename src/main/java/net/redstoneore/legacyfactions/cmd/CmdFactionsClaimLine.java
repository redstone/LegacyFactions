package net.redstoneore.legacyfactions.cmd;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.block.BlockFace;

import net.redstoneore.legacyfactions.FLocation;
import net.redstoneore.legacyfactions.Permission;
import net.redstoneore.legacyfactions.Lang;
import net.redstoneore.legacyfactions.entity.Conf;
import net.redstoneore.legacyfactions.entity.Faction;
import net.redstoneore.legacyfactions.event.EventFactionsLandChange;
import net.redstoneore.legacyfactions.event.EventFactionsLandChange.LandChangeCause;

public class CmdFactionsClaimLine extends FCommand {

	// -------------------------------------------------- //
	// INSTANCE
	// -------------------------------------------------- //
	
	private static CmdFactionsClaimLine instance = new CmdFactionsClaimLine();
	public static CmdFactionsClaimLine get() { return instance; }
	
	// -------------------------------------------------- //
	// STATIC FIELDS
	// -------------------------------------------------- //

	private static final BlockFace[] axis = {BlockFace.SOUTH, BlockFace.WEST, BlockFace.NORTH, BlockFace.EAST};

	// -------------------------------------------------- //
	// CONSTRUCT
	// -------------------------------------------------- //

	public CmdFactionsClaimLine() {

		// Aliases
		this.aliases.addAll(Conf.cmdAliasesClaimLine);

		// Args
		this.optionalArgs.put("amount", "1");
		this.optionalArgs.put("direction", "facing");
		this.optionalArgs.put("faction", "you");

		this.permission = Permission.CLAIM_LINE.getNode();
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
		Integer amount = this.argAsInt(0, 1); // Default to 1

		if (amount > Conf.lineClaimLimit) {
			fme.sendMessage(Lang.COMMAND_CLAIMLINE_ABOVEMAX, Conf.lineClaimLimit);
			return;
		}

		String direction = this.argAsString(1);
		BlockFace blockFace = null;

		if (direction == null) {
			blockFace = axis[Math.round(me.getLocation().getYaw() / 90f) & 0x3];
		} else if (direction.equalsIgnoreCase("north")) {
			blockFace = BlockFace.NORTH;
		} else if (direction.equalsIgnoreCase("east")) {
			blockFace = BlockFace.EAST;
		} else if (direction.equalsIgnoreCase("south")) {
			blockFace = BlockFace.SOUTH;
		} else if (direction.equalsIgnoreCase("west")) {
			blockFace = BlockFace.WEST;
		} else {
			fme.sendMessage(Lang.COMMAND_CLAIMLINE_NOTVALID, direction);
			return;
		}

		final Faction forFaction = this.argAsFaction(2, myFaction);
		
		Map<FLocation, Faction> transactions = new HashMap<FLocation, Faction>();
		Location location = me.getLocation();

		for (int i = 0; i < amount; i++) {
			transactions.put(FLocation.valueOf(location), forFaction);
			location = location.add(blockFace.getModX() * 16, 0, blockFace.getModZ() * 16);
		}
		
		EventFactionsLandChange event = new EventFactionsLandChange(fme, transactions, LandChangeCause.Claim);
		Bukkit.getServer().getPluginManager().callEvent(event);
		if (event.isCancelled()) return;
		
		for (Entry<FLocation, Faction> claimLocation : event.getTransactions().entrySet()) {
			if (!fme.attemptClaim(claimLocation.getValue(), claimLocation.getKey(), true, event)) {
				return;
			}
		}
		
	}

	@Override
	public String getUsageTranslation() {
		return Lang.COMMAND_CLAIMLINE_DESCRIPTION.toString();
	}
	
}
