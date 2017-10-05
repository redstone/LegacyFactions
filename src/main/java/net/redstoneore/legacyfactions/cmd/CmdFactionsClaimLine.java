package net.redstoneore.legacyfactions.cmd;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.block.BlockFace;

import net.redstoneore.legacyfactions.Permission;
import net.redstoneore.legacyfactions.config.CommandAliases;
import net.redstoneore.legacyfactions.config.Config;
import net.redstoneore.legacyfactions.entity.Faction;
import net.redstoneore.legacyfactions.event.EventFactionsLandChange;
import net.redstoneore.legacyfactions.event.EventFactionsLandChange.LandChangeCause;
import net.redstoneore.legacyfactions.lang.Lang;
import net.redstoneore.legacyfactions.locality.Locality;

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
		this.aliases.addAll(CommandAliases.cmdAliasesClaimLine);

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

		if (amount > Config.lineClaimLimit) {
			fme.sendMessage(Lang.COMMAND_CLAIMLINE_ABOVEMAX, Config.lineClaimLimit);
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
		
		Map<Locality, Faction> transactions = new HashMap<>();
		Location location = me.getLocation();

		for (int i = 0; i < amount; i++) {
			transactions.put(Locality.of(location), forFaction);
			location = location.add(blockFace.getModX() * 16, 0, blockFace.getModZ() * 16);
		}
		
		EventFactionsLandChange event = new EventFactionsLandChange(fme, transactions, LandChangeCause.Claim);
		Bukkit.getServer().getPluginManager().callEvent(event);
		if (event.isCancelled()) return;
		
		CmdFactionsClaim.resume(fme, transactions, forFaction, 1);		
	}

	@Override
	public String getUsageTranslation() {
		return Lang.COMMAND_CLAIMLINE_DESCRIPTION.toString();
	}
	
}
