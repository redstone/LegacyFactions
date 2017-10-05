package net.redstoneore.legacyfactions.expansion.fly;

import net.redstoneore.legacyfactions.lang.Lang;

public class FactionsFlyConfig {

	// -------------------------------------------------- //
	// FIELDS
	// -------------------------------------------------- //
	
	// Is this enabled
	protected static String _factionFlyEnabled = Lang.CONFIG_FACTIONFLY_ENABLED.name();
	public boolean enabled = false;
	
	// Disable Enderpearl when factions fly is enabled 
	protected static String _factionFlyDisableEnderpearlWhileFlying = Lang.CONFIG_FACTIONFLY_ENDERPEARL.name();
	public boolean disableEnderpearlWhileFlying = true;
	
	// Disable Chorus Fruit when factions fly is enabled 
	protected static String _factionFlyDisableChorusFruitWhileFlying = Lang.CONFIG_FACTIONFLY_CHORUSFRUIT.name();
	public boolean disableChorusFruitWhileFlying = true;
	
	// Max flight height
	protected static String _factionFlyMaxY = Lang.CONFIG_FACTIONFLY_MAXY.name();
	public double maxY = -1;
	
	// This will make a player not obtain fall damage when fly is disabled
	protected static String _factionFlyOnDisableNoFallDamage = Lang.CONFIG_FACTIONFLY_NOFALLDAMAGE.name();
	public boolean onDisableNoFallDamage = true;
	
	// This will make a player teleport to floor when disabled
	protected static String _factionFlyOnDisableTeleportToFloor = Lang.CONFIG_FACTIONFLY_FLOORTELEPORT.name();
	public boolean onDisableTeleportToFloor = false;
	
}
