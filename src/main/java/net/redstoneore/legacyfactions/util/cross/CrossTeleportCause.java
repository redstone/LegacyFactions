package net.redstoneore.legacyfactions.util.cross;

import java.util.Optional;

import org.bukkit.event.player.PlayerTeleportEvent.TeleportCause;

public enum CrossTeleportCause implements Cross<CrossTeleportCause> {
	
	// -------------------------------------------------- //
	// ENUM
	// -------------------------------------------------- //
	
	/**
	 * Indicates the teleporation was caused by a player throwing an Ender
	 * Pearl
	 */
	ENDER_PEARL,
	
	/**
	 * Indicates the teleportation was caused by a player executing a
	 * command
	 */
	COMMAND,
	
	/**
	 * Indicates the teleportation was caused by a plugin
	 */
	PLUGIN,
	
	/**
	 * Indicates the teleportation was caused by a player entering a
	 * Nether portal
	 */
	NETHER_PORTAL,
	
	/**
	 * Indicates the teleportation was caused by a player entering an End
	 * portal
	 */
	END_PORTAL,
	
	/**
	 * Indicates the teleportation was caused by a player teleporting to a
	 * Entity/Player via the specatator menu
	 */
	SPECTATE,
	
	/**
	 * Indicates the teleportation was caused by a player entering an End
	 * gateway
	 */
	END_GATEWAY,
	
	/**
	 * Indicates the teleportation was caused by a player consuming chorus
	 * fruit
	 */
	CHORUS_FRUIT,
	
	/**
	 * Indicates the teleportation was caused by an event not covered by
	 * this enum
	 */
	UNKNOWN;
	
	;

	// -------------------------------------------------- //
	// STATIC METHODS
	// -------------------------------------------------- // 
	
	/**
	 * Attempt to get a CrossTeleportCause by their name
	 * @param teleportCauseName Name of cause
	 * @return {@link Optional} of {@link CrossTeleportCause}
	 */
	public static Optional<CrossTeleportCause> get(String teleportCauseName) {
		for (CrossTeleportCause teleportCause : values()) {
			if (teleportCause.name() == teleportCauseName) return Optional.of(teleportCause);
		}
		return Optional.empty();
	}
	
	public static CrossTeleportCause get(TeleportCause cause) {
		return CrossTeleportCause.valueOf(cause.name());
	}
	
	// -------------------------------------------------- //
	// METHODS
	// -------------------------------------------------- //
	
	/**
	 * Converts to a Bukkit {@link TeleportCause}.
	 * @return {@link TeleportCause}, or null
	 */
	public TeleportCause toBukkitTeleportCause() {
		return TeleportCause.valueOf(this.name());
	}
	
	/**
	 * Attempts to cross match to bukkits {@link TeleportCause}
	 * @return true if supported
	 */
	public boolean isSupported() {
		return TeleportCause.valueOf(this.name()) != null;
	}
	
	@Override
	public boolean is(CrossTeleportCause what) {
		return (this == what);
	}
	
}
