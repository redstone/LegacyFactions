package net.redstoneore.legacyfactions.integration.vault;

import net.milkbowl.vault.economy.Economy;
import net.redstoneore.legacyfactions.integration.IntegrationEngine;

public class VaultEngine extends IntegrationEngine {

	// -------------------------------------------------- //
	// STATIC FIELDS
	// -------------------------------------------------- //

	private static VaultUtils vaultUtils = null;

	// -------------------------------------------------- //
	// STATIC METHODS
	// -------------------------------------------------- //
	
	public static boolean isSetup() {
		return vaultUtils != null;
	}
	
	public static VaultUtils getUtils() {
		if (vaultUtils == null) vaultUtils = new VaultUtils();
		
		return vaultUtils;
	}
	
	public static Economy getEconomy() {
		return VaultEngine.getUtils().getEcon();
	}
	
}
