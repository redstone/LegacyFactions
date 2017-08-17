package net.redstoneore.legacyfactions.cmd;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.Player;

import net.redstoneore.legacyfactions.FLocation;
import net.redstoneore.legacyfactions.Permission;
import net.redstoneore.legacyfactions.entity.CommandAliases;
import net.redstoneore.legacyfactions.Lang;
import net.redstoneore.legacyfactions.util.VisualizeUtil;

public class CmdFactionsSeeChunk extends FCommand {

	// -------------------------------------------------- //
	// INSTANCE
	// -------------------------------------------------- //
	
	private static CmdFactionsSeeChunk instance = new CmdFactionsSeeChunk();
	public static CmdFactionsSeeChunk get() { return instance; }
	
	// -------------------------------------------------- //
	// CONSTRUCT
	// -------------------------------------------------- //

	private CmdFactionsSeeChunk() {
		this.aliases.addAll(CommandAliases.cmdAliasesSeeChunk);

		this.permission = Permission.SEECHUNK.getNode();

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
		World world = me.getWorld();
		FLocation flocation = new FLocation(me);
		int chunkX = (int) flocation.getX();
		int chunkZ = (int) flocation.getZ();

		int blockX;
		int blockZ;

		blockX = chunkX * 16;
		blockZ = chunkZ * 16;
		showPillar(me, world, blockX, blockZ);

		blockX = chunkX * 16 + 15;
		blockZ = chunkZ * 16;
		showPillar(me, world, blockX, blockZ);

		blockX = chunkX * 16;
		blockZ = chunkZ * 16 + 15;
		showPillar(me, world, blockX, blockZ);

		blockX = chunkX * 16 + 15;
		blockZ = chunkZ * 16 + 15;
		showPillar(me, world, blockX, blockZ);
	}

	@SuppressWarnings("deprecation")
	public static void showPillar(Player player, World world, int blockX, int blockZ) {
		for (int blockY = 0; blockY < player.getLocation().getBlockY() + 30; blockY++) {
			Location loc = new Location(world, blockX, blockY, blockZ);
			if (loc.getBlock().getType() != Material.AIR) {
				continue;
			}
			int typeId = blockY % 5 == 0 ? Material.REDSTONE_LAMP_ON.getId() : Material.STAINED_GLASS.getId();
			VisualizeUtil.addLocation(player, loc, typeId);
		}
	}

	@Override
	public String getUsageTranslation() {
		return Lang.GENERIC_PLACEHOLDER.toString();
	}

}
