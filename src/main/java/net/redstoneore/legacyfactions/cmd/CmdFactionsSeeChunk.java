package net.redstoneore.legacyfactions.cmd;

import org.bukkit.World;
import org.bukkit.entity.Player;

import net.redstoneore.legacyfactions.Permission;
import net.redstoneore.legacyfactions.config.CommandAliases;
import net.redstoneore.legacyfactions.lang.Lang;
import net.redstoneore.legacyfactions.mixin.PlayerMixin;

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
		World world = this.fme.getLastLocation().getWorld();
		
		int chunkX = this.fme.getLastLocation().getChunkX();
		int chunkZ = this.fme.getLastLocation().getChunkZ();

		int blockX;
		int blockZ;

		blockX = chunkX * 16;
		blockZ = chunkZ * 16;
		PlayerMixin.showPillar(this.me, world, blockX, blockZ);

		blockX = chunkX * 16 + 15;
		blockZ = chunkZ * 16;
		PlayerMixin.showPillar(this.me, world, blockX, blockZ);

		blockX = chunkX * 16;
		blockZ = chunkZ * 16 + 15;
		PlayerMixin.showPillar(this.me, world, blockX, blockZ);

		blockX = chunkX * 16 + 15;
		blockZ = chunkZ * 16 + 15;
		PlayerMixin.showPillar(this.me, world, blockX, blockZ);
	}

	/**
	 * Deprecated, use {@link PlayerMixin#showPillar(Player, World, int, int)}
	 * @param player
	 * @param world
	 * @param blockX
	 * @param blockZ
	 */
	@Deprecated
	public static void showPillar(Player player, World world, int blockX, int blockZ) {
		PlayerMixin.showPillar(player, world, blockX, blockZ);
	}

	@Override
	public String getUsageTranslation() {
		return Lang.GENERIC_PLACEHOLDER.toString();
	}

}
