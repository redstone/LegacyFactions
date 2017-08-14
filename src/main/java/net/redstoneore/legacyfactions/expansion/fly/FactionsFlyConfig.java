package net.redstoneore.legacyfactions.expansion.fly;

public class FactionsFlyConfig {

	// -------------------------------------------------- //
	// FIELDS
	// -------------------------------------------------- //
	
	// Is this enabled
	public boolean enabled = false;
	
	// Disable Enderpearl when factions fly is enabled 
	public boolean disableEnderpearlWhileFlying = true;
	
	// Disable Chorus Fruit when factions fly is enabled 
	public boolean disableChorusFruitWhileFlying = true;
	
	// Max flight height
	public double maxY = -1;
	
	// This will make a player not obtain fall damage when fly is disabled
	public boolean onDisableNoFallDamage = true;
	
	// This will make a player teleport to floor when disable
	public boolean onDisableTeleportToFloor = false;
	
}
